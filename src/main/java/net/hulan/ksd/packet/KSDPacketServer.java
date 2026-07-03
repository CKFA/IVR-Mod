package net.hulan.ksd.packet;

import io.netty.buffer.Unpooled;
import mtr.Registry;
import mtr.data.NameColorDataBase;
import mtr.data.SerializedDataBase;
import mtr.data.TicketSystem;
import mtr.data.TransportMode;
import mtr.packet.PacketTrainDataBase;
import net.hulan.ksd.data.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class KSDPacketServer extends PacketTrainDataBase implements KSDPacket {
    
    private static final int PACKET_CHUNK_SIZE = (int) Math.pow(2.0F, 14.0F);

    public static void openKSDDashboardScreenS2C(ServerPlayer player, TransportMode transportMode, boolean useTimeAndWindSync) {
        FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeUtf(transportMode.toString());
        packet.writeBoolean(useTimeAndWindSync);
        Registry.sendToPlayer(player, KSD_PACKET_OPEN_KSD_DASHBOARD_SCREEN, packet);
    }

    public static void openKCRTicketMachineScreenS2C(ServerPlayer player, BlockPos machinePos) {
        FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(machinePos);
        Registry.sendToPlayer(player, KSD_PACKET_OPEN_KCR_TICKET_MACHINE_SCREEN, packet);
    }

    public static void sendAllInChunks(ServerPlayer player, Set<KSDStation> stations, Set<KSDPlatform> platforms, Set<KSDRoute> routes) {
        final long tempPacketId = new Random().nextLong();
        final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        serializeData(packet, stations);
        serializeData(packet, platforms);
        serializeData(packet, routes);
        int i = 0;
        while (!sendChunk(player, packet, tempPacketId, i)) {
            i++;
        }
    }

    private static boolean sendChunk(ServerPlayer player, FriendlyByteBuf packet, long tempPacketId, int chunk) {
        FriendlyByteBuf packetChunk = new FriendlyByteBuf(Unpooled.buffer());
        packetChunk.writeLong(tempPacketId);
        packetChunk.writeInt(chunk);
        boolean success = chunk * PACKET_CHUNK_SIZE > packet.readableBytes();
        packetChunk.writeBoolean(success);
        if (!success) {
            packetChunk.writeBytes(packet.copy(chunk * PACKET_CHUNK_SIZE, Math.min(PACKET_CHUNK_SIZE, packet.readableBytes() - chunk * PACKET_CHUNK_SIZE)));
        }
        try {
            Registry.sendToPlayer(player, KSD_PACKET_CHUNK_S2C, packetChunk);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public static <T extends NameColorDataBase> void receiveUpdateOrDeleteC2S(MinecraftServer minecraftServer,
                                                                              ServerPlayer player,
                                                                              FriendlyByteBuf packet,
                                                                              ResourceLocation packetId,
                                                                              Function<KSDRailwayData, Set<T>> dataSet,
                                                                              Function<KSDRailwayData, Map<Long, T>> cacheMap,
                                                                              BiFunction<Long, TransportMode, T> createDataWithId,
                                                                              boolean isDelete) {
        if (!KSDRailwayData.hasNoPermission(player)) {
            Level world = player.level;
            KSDRailwayData railwayData = KSDRailwayData.getInstance(world);
            if (railwayData != null) {
                PacketCallback packetCallback = (updatePacket, fullPacket) -> world.players().forEach((worldPlayer) -> {
                    if (!worldPlayer.getUUID().equals(player.getUUID())) {
                        Registry.sendToPlayer((ServerPlayer)worldPlayer, packetId, fullPacket);
                    }
                    railwayData.dataCache.sync();
                });
                if (isDelete) {
                    deleteData(
                            dataSet.apply(railwayData),
                            cacheMap.apply(railwayData),
                            minecraftServer,
                            packet,
                            packetCallback,
                            (data) -> railwayData.railwayDataLoggingModule.addEvent(player, data.getClass(), data.id, data.name, KSDRailwayDataLoggingModule.getData(data), new ArrayList<>()));
                } else {
                    updateData(
                            dataSet.apply(railwayData),
                            cacheMap.apply(railwayData),
                            minecraftServer,
                            packet,
                            packetCallback,
                            createDataWithId,
                            (data, oldData) -> railwayData.railwayDataLoggingModule.addEvent(player, data.getClass(), data.id, data.name, oldData, KSDRailwayDataLoggingModule.getData(data)));
                }
            }
        }
    }

    private static <T extends SerializedDataBase> void serializeData(FriendlyByteBuf packet, Collection<T> objects) {
        packet.writeInt(objects.size());
        objects.forEach((object) -> object.writePacket(packet));
    }
}
