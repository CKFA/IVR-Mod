package net.hulan.ivr.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mtr.RegistryClient;
import mtr.data.NameColorDataBase;
import mtr.data.TransportMode;
import mtr.mappings.UtilitiesClient;
import mtr.packet.PacketTrainDataBase;
import net.hulan.ivr.client.IVRClientData;
import net.hulan.ivr.screen.ClassicalSign1OddScreen;
import net.hulan.ivr.screen.ClassicalSignScreen;
import net.hulan.ivr.screen.ModernSignScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public class IVRPacketTrainDataGuiClient extends PacketTrainDataBase implements IVRPacket {

    private static final Map<Integer, ByteBuf> TEMP_PACKETS_RECEIVER = new HashMap<>();
    private static long tempPacketId = 0L;
    private static int expectedSize = 0;

    public static void openClassicalSignScreenS2C(Minecraft minecraftClient, FriendlyByteBuf packet) {
        final BlockPos pos = packet.readBlockPos();
        minecraftClient.execute(() -> {
            if (!(minecraftClient.screen instanceof ClassicalSignScreen)) {
                UtilitiesClient.setScreen(minecraftClient, new ClassicalSignScreen(pos));
            }
        });
    }

    public static void openClassicalSign1OddScreenS2C(Minecraft minecraftClient, FriendlyByteBuf packet) {
        final BlockPos pos = packet.readBlockPos();
        minecraftClient.execute(() -> {
            if (!(minecraftClient.screen instanceof ClassicalSign1OddScreen)) {
                UtilitiesClient.setScreen(minecraftClient, new ClassicalSign1OddScreen(pos));
            }
        });
    }

    public static void openModernSignScreenS2C(Minecraft minecraftClient, FriendlyByteBuf packet) {
        final BlockPos pos = packet.readBlockPos();
        minecraftClient.execute(() -> {
            if (!(minecraftClient.screen instanceof ModernSignScreen)) {
                UtilitiesClient.setScreen(minecraftClient, new ModernSignScreen(pos));
            }
        });
    }

    public static void sendClassicalSignIdsC2S(BlockPos signPos, Set<Long> selectedIds, String[] signIds, boolean luminance) {
        final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(signPos);
        packet.writeInt(selectedIds.size());
        selectedIds.forEach(packet::writeLong);
        packet.writeInt(signIds.length);
        for (final String signType : signIds) {
            packet.writeUtf(signType == null ? "" : signType);
        }
        packet.writeBoolean(luminance);
        RegistryClient.sendToServer(PACKET_CLASSICAL_SIGN_TYPES, packet);
    }

    public static void sendClassicalSign1OddIdsC2S(BlockPos signPos, Set<Long> selectedIds1, String[] signId1, Set<Long> selectedIds2, String[] signId2, boolean luminance) {
        final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(signPos);
        packet.writeInt(selectedIds1.size());
        selectedIds1.forEach(packet::writeLong);
        packet.writeInt(1);
        packet.writeUtf(signId1[0] == null ? "" : signId1[0]);
        packet.writeInt(selectedIds2.size());
        selectedIds2.forEach(packet::writeLong);
        packet.writeInt(1);
        packet.writeUtf(signId2[0] == null ? "" : signId2[0]);
        packet.writeBoolean(luminance);
        RegistryClient.sendToServer(PACKET_CLASSICAL_1ODD_SIGN_TYPES, packet);
    }

    public static void sendModernSignIdsC2S(BlockPos signPos, Set<Long> selectedIds, String[] signIds, boolean luminance) {
        final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(signPos);
        packet.writeInt(selectedIds.size());
        selectedIds.forEach(packet::writeLong);
        packet.writeInt(signIds.length);
        for (final String signType : signIds) {
            packet.writeUtf(signType == null ? "" : signType);
        }
        packet.writeBoolean(luminance);
        RegistryClient.sendToServer(PACKET_MODERN_SIGN_TYPES, packet);
    }

    public static void receiveChunk(Minecraft minecraftClient, FriendlyByteBuf packet) {
        long id = packet.readLong();
        int chunk = packet.readInt();
        boolean complete = packet.readBoolean();
        if (tempPacketId != id) {
            TEMP_PACKETS_RECEIVER.clear();
            tempPacketId = id;
            expectedSize = 2147483647;
        }
        if (complete) {
            expectedSize = chunk + 1;
        }
        TEMP_PACKETS_RECEIVER.put(chunk, packet.readBytes(packet.readableBytes()));
        if (TEMP_PACKETS_RECEIVER.size() == expectedSize) {
            FriendlyByteBuf newPacket = new FriendlyByteBuf(Unpooled.buffer());
            for(int i = 0; i < expectedSize; ++i) {
                newPacket.writeBytes(TEMP_PACKETS_RECEIVER.get(i));
            }
            TEMP_PACKETS_RECEIVER.clear();
            try {
                minecraftClient.execute(IVRClientData::receivePacket);
            } catch (Exception var8) {
                var8.printStackTrace();
            }
        }
    }

    public static <T extends NameColorDataBase> void receiveUpdateOrDeleteS2C(Minecraft minecraftClient, FriendlyByteBuf packet, Set<T> dataSet, Map<Long, T> cacheMap, BiFunction<Long, TransportMode, T> createDataWithId, boolean isDelete) {
        PacketCallback packetCallback = (updatePacket, fullPacket) -> {
            IVRClientData.DATA_CACHE.sync();
            IVRClientData.DATA_CACHE.refreshDynamicResources();
        };
        if (isDelete) {
            deleteData(dataSet, cacheMap, minecraftClient, packet, packetCallback, null);
        } else {
            updateData(dataSet, cacheMap, minecraftClient, packet, packetCallback, createDataWithId, null);
        }
    }
}
