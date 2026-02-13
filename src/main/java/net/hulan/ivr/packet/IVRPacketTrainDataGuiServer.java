package net.hulan.ivr.packet;

import io.netty.buffer.Unpooled;
import mtr.Registry;
import mtr.block.BlockRouteSignBase;
import mtr.data.*;
import mtr.mappings.BlockEntityMapper;
import mtr.packet.PacketTrainDataBase;
import mtr.packet.UpdateBlueMap;
import mtr.packet.UpdateDynmap;
import mtr.packet.UpdateSquaremap;
import net.hulan.ivr.block.BlockClassicalSign;
import net.hulan.ivr.block.BlockKCRRouteSignBase;
import net.hulan.ivr.block.BlockModernSign;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class IVRPacketTrainDataGuiServer extends PacketTrainDataBase implements IVRPacket {

    private static final int PACKET_CHUNK_SIZE = (int)Math.pow(2.0D, 14.0D);

    public static void openClassicalSignScreenS2C(ServerPlayerEntity player, BlockPos signPos) {
        final PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeBlockPos(signPos);
        Registry.sendToPlayer(player, PACKET_OPEN_CLASSICAL_SIGN_SCREEN, packet);
    }

    public static void openModernSignScreenS2C(ServerPlayerEntity player, BlockPos signPos) {
        final PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeBlockPos(signPos);
        Registry.sendToPlayer(player, IVRPacket.PACKET_OPEN_MODERN_SIGN_SCREEN, packet);
    }

    public static void receiveClassicalSignIdsC2S(MinecraftServer minecraftServer, ServerPlayerEntity player, PacketByteBuf packet) {
        final BlockPos signPos = packet.readBlockPos();
        final int selectedIdsLength = packet.readInt();
        final Set<Long> selectedIds = new HashSet<>();
        for (int i = 0; i < selectedIdsLength; i++) {
            selectedIds.add(packet.readLong());
        }
        final int signLength = packet.readInt();
        final String[] signIds = new String[signLength];
        for (int i = 0; i < signLength; i++) {
            final String signId = packet.readString(SerializedDataBase.PACKET_STRING_READ_LENGTH);
            signIds[i] = signId.isEmpty() ? null : signId;
        }
        final boolean luminance = packet.readBoolean();
        minecraftServer.execute(() -> {
            final BlockEntity entity = player.world.getBlockEntity(signPos);
            if (entity instanceof BlockClassicalSign.TileEntityClassicalSign) {
                setTileEntityDataAndWriteUpdate(player, entity2 -> entity2.setData(selectedIds, signIds, luminance), (BlockClassicalSign.TileEntityClassicalSign) entity);
            } else if (entity instanceof BlockRouteSignBase.TileEntityRouteSignBase) {
                final long platformId = selectedIds.isEmpty() ? 0 : (long) selectedIds.toArray()[0];
                final BlockEntity entityAbove = player.world.getBlockEntity(signPos.up());
                if (entityAbove instanceof BlockRouteSignBase.TileEntityRouteSignBase) {
                    setTileEntityDataAndWriteUpdate(player, entity2 -> entity2.setPlatformId(platformId), ((BlockRouteSignBase.TileEntityRouteSignBase) entityAbove), (BlockRouteSignBase.TileEntityRouteSignBase) entity);
                } else {
                    setTileEntityDataAndWriteUpdate(player, entity2 -> entity2.setPlatformId(platformId), (BlockRouteSignBase.TileEntityRouteSignBase) entity);
                }
            }
        });
    }

    public static void receiveModernSignIdsC2S(MinecraftServer minecraftServer, ServerPlayerEntity player, PacketByteBuf packet) {
        BlockPos signPos = packet.readBlockPos();
        int selectedIdsLength = packet.readInt();
        Set<Long> selectedIds = new HashSet<>();
        int signLength;
        for(signLength = 0; signLength < selectedIdsLength; ++signLength) {
            selectedIds.add(packet.readLong());
        }
        signLength = packet.readInt();
        String[] signIds = new String[signLength];
        for(int i = 0; i < signLength; ++i) {
            String signId = packet.readString(32767);
            signIds[i] = signId.isEmpty() ? null : signId;
        }
        final boolean luminance = packet.readBoolean();
        minecraftServer.execute(() -> {
            BlockEntity entity = player.world.getBlockEntity(signPos);
            if (entity instanceof BlockModernSign.TileEntityModernSign) {
                setTileEntityDataAndWriteUpdate(player, (entity2) -> entity2.setData(selectedIds, signIds, luminance), (BlockModernSign.TileEntityModernSign)entity);
            } else if (entity instanceof BlockKCRRouteSignBase.TileEntityKCRRouteSignBase) {
                long platformId = selectedIds.isEmpty() ? 0L : (Long)selectedIds.toArray()[0];
                BlockEntity entityAbove = player.world.getBlockEntity(signPos.up());
                if (entityAbove instanceof BlockKCRRouteSignBase.TileEntityKCRRouteSignBase) {
                    setTileEntityDataAndWriteUpdate(player, (entity2) -> entity2.setPlatformId(platformId), (BlockKCRRouteSignBase.TileEntityKCRRouteSignBase)entityAbove, (BlockKCRRouteSignBase.TileEntityKCRRouteSignBase)entity);
                } else {
                    setTileEntityDataAndWriteUpdate(player, (entity2) -> entity2.setPlatformId(platformId), (BlockKCRRouteSignBase.TileEntityKCRRouteSignBase)entity);
                }
            }
        });
    }

    @SafeVarargs
    private static <T extends BlockEntityMapper> void setTileEntityDataAndWriteUpdate(ServerPlayerEntity player, Consumer<T> setData, T... entities) {
        final RailwayData railwayData = RailwayData.getInstance(player.world);
        if (railwayData != null && entities.length > 0) {
            final NbtCompound compoundTagOld = new NbtCompound();
            entities[0].writeCompoundTag(compoundTagOld);
            BlockPos blockPos = null;
            long posLong = 0;
            for (final T entity : entities) {
                setData.accept(entity);
                final BlockPos entityPos = entity.getPos();
                if (blockPos == null || entityPos.asLong() > posLong) {
                    blockPos = entityPos;
                    posLong = entityPos.asLong();
                }
            }
            final NbtCompound compoundTagNew = new NbtCompound();
            entities[0].writeCompoundTag(compoundTagNew);
            railwayData.railwayDataLoggingModule.addEvent(player, entities[0].getClass(), RailwayDataLoggingModule.getData(compoundTagOld), RailwayDataLoggingModule.getData(compoundTagNew), blockPos);
        }
    }

    public static void sendAllInChunks(ServerPlayerEntity player, Set<Station> stations, Set<Platform> platforms, Set<Siding> sidings, Set<Route> routes, Set<Depot> depots, Set<LiftServer> lifts) {
        long tempPacketId = new Random().nextLong();
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        serializeData(packet, stations);
        serializeData(packet, platforms);
        serializeData(packet, sidings);
        serializeData(packet, routes);
        serializeData(packet, depots);
        serializeData(packet, lifts);
        int i = 0;
        while (!sendChunk(player, packet, tempPacketId, i)) {
            ++i;
        }
    }

    private static <T extends SerializedDataBase> void serializeData(PacketByteBuf packet, Collection<T> objects) {
        packet.writeInt(objects.size());
        objects.forEach((object) -> object.writePacket(packet));
    }

    private static boolean sendChunk(ServerPlayerEntity player, PacketByteBuf packet, long tempPacketId, int chunk) {
        PacketByteBuf packetChunk = new PacketByteBuf(Unpooled.buffer());
        packetChunk.writeLong(tempPacketId);
        packetChunk.writeInt(chunk);
        boolean success = chunk * PACKET_CHUNK_SIZE > packet.readableBytes();
        packetChunk.writeBoolean(success);
        if (!success) {
            packetChunk.writeBytes(packet.copy(chunk * PACKET_CHUNK_SIZE, Math.min(PACKET_CHUNK_SIZE, packet.readableBytes() - chunk * PACKET_CHUNK_SIZE)));
        }
        try {
            Registry.sendToPlayer(player, PACKET_IVR_CHUNK_S2C, packetChunk);
        } catch (Exception var8) {
            var8.printStackTrace();
        }
        return success;
    }

    public static <T extends NameColorDataBase> void receiveUpdateOrDeleteC2S(MinecraftServer minecraftServer, ServerPlayerEntity player, PacketByteBuf packet, Identifier packetId, Function<RailwayData, Set<T>> dataSet, Function<RailwayData, Map<Long, T>> cacheMap, BiFunction<Long, TransportMode, T> createDataWithId, boolean isDelete) {
        if (!RailwayData.hasNoPermission(player)) {
            World world = player.world;
            RailwayData railwayData = RailwayData.getInstance(world);
            if (railwayData != null) {
                PacketCallback packetCallback = (updatePacket, fullPacket) -> {
                    world.getPlayers().forEach((worldPlayer) -> {
                        if (!worldPlayer.getUuid().equals(player.getUuid())) {
                            Registry.sendToPlayer((ServerPlayerEntity)worldPlayer, packetId, fullPacket);
                        }
                    });
                    if (packetId.equals(PACKET_IVR_UPDATE_STATION) || packetId.equals(PACKET_IVR_DELETE_STATION) || packetId.equals(PACKET_IVR_UPDATE_DEPOT) || packetId.equals(PACKET_IVR_DELETE_DEPOT)) {
                        try {
                            UpdateDynmap.updateDynmap(world, railwayData);
                        } catch (Exception | NoClassDefFoundError ignored) {
                        }

                        try {
                            UpdateBlueMap.updateBlueMap(world, railwayData);
                        } catch (Exception | NoClassDefFoundError ignored) {
                        }

                        try {
                            UpdateSquaremap.updateSquaremap(world, railwayData);
                        } catch (Exception | NoClassDefFoundError ignored) {
                        }
                    }
                };
                if (isDelete) {
                    deleteData(dataSet.apply(railwayData),
                            cacheMap.apply(railwayData),
                            minecraftServer,
                            packet,
                            packetCallback,
                            (data) -> railwayData.railwayDataLoggingModule.addEvent(player,
                                    data.getClass(),
                                    data.id,
                                    data.name,
                                    RailwayDataLoggingModule.getData(data),
                                    new ArrayList<>()));
                } else {
                    updateData(dataSet.apply(railwayData),
                            cacheMap.apply(railwayData),
                            minecraftServer,
                            packet,
                            packetCallback,
                            createDataWithId,
                            (data, oldData) -> railwayData.railwayDataLoggingModule.addEvent(player,
                                    data.getClass(),
                                    data.id,
                                    data.name,
                                    oldData,
                                    RailwayDataLoggingModule.getData(data)));
                }

            }
        }
    }
}
