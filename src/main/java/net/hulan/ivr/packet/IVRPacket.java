package net.hulan.ivr.packet;

import net.minecraft.resources.ResourceLocation;

public interface IVRPacket {

    ResourceLocation PACKET_OPEN_CLASSICAL_SIGN_SCREEN = new ResourceLocation("ivr", "packet_open_classical_sign_screen");
    ResourceLocation PACKET_OPEN_CLASSICAL_1ODD_SIGN_SCREEN = new ResourceLocation("ivr", "packet_open_classical_1odd_sign_screen");
    ResourceLocation PACKET_OPEN_MODERN_SIGN_SCREEN = new ResourceLocation("ivr", "packet_open_modern_sign_screen");
    ResourceLocation PACKET_CLASSICAL_SIGN_TYPES = new ResourceLocation("ivr", "packet_classical_sign_types");
    ResourceLocation PACKET_CLASSICAL_1ODD_SIGN_TYPES = new ResourceLocation("ivr", "packet_classical_1odd_sign_types");
    ResourceLocation PACKET_MODERN_SIGN_TYPES = new ResourceLocation("ivr", "packet_modern_sign_types");
}
