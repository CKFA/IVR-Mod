package net.hulan.ivr.packet;

import io.netty.buffer.Unpooled;
import mtr.RegistryClient;
import mtr.client.ClientData;
import mtr.mappings.UtilitiesClient;
import mtr.packet.PacketTrainDataBase;
import net.hulan.ivr.IVR;
import net.hulan.ivr.client.IVRClientData;
import net.hulan.ivr.screen.ClassicalSign1OddScreen;
import net.hulan.ivr.screen.ClassicalSignScreen;
import net.hulan.ivr.screen.ModernSignScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Set;

public class IVRPacketTrainDataGuiClient extends PacketTrainDataBase implements IVRPacket {

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
}
