package net.hulan.ivr.packet;

import net.minecraft.resources.ResourceLocation;

public interface IVRPacket {

    ResourceLocation PACKET_OPEN_CLASSICAL_SIGN_SCREEN = new ResourceLocation("ivr", "packet_open_classical_sign_screen");
    ResourceLocation PACKET_OPEN_CLASSICAL_1ODD_SIGN_SCREEN = new ResourceLocation("ivr", "packet_open_classical_1odd_sign_screen");
    ResourceLocation PACKET_OPEN_MODERN_SIGN_SCREEN = new ResourceLocation("ivr", "packet_open_modern_sign_screen");
    ResourceLocation PACKET_CLASSICAL_SIGN_TYPES = new ResourceLocation("ivr", "packet_classical_sign_types");
    ResourceLocation PACKET_CLASSICAL_1ODD_SIGN_TYPES = new ResourceLocation("ivr", "packet_classical_1odd_sign_types");
    ResourceLocation PACKET_MODERN_SIGN_TYPES = new ResourceLocation("ivr", "packet_modern_sign_types");
    ResourceLocation PACKET_IVR_CHUNK_S2C = new ResourceLocation("ivr", "packet_chunk_s2c");
    ResourceLocation PACKET_IVR_UPDATE_STATION = new ResourceLocation("ivr", "packet_update_station");
    ResourceLocation PACKET_IVR_UPDATE_PLATFORM = new ResourceLocation("ivr", "packet_update_platform");
    ResourceLocation PACKET_IVR_UPDATE_SIDING = new ResourceLocation("ivr", "packet_update_siding");
    ResourceLocation PACKET_IVR_UPDATE_ROUTE = new ResourceLocation("ivr", "packet_update_route");
    ResourceLocation PACKET_IVR_UPDATE_DEPOT = new ResourceLocation("ivr", "packet_update_depot");
    ResourceLocation PACKET_IVR_UPDATE_LIFT = new ResourceLocation("ivr", "packet_update_lift");
    ResourceLocation PACKET_IVR_DELETE_STATION = new ResourceLocation("ivr", "packet_delete_station");
    ResourceLocation PACKET_IVR_DELETE_PLATFORM = new ResourceLocation("ivr", "packet_delete_platform");
    ResourceLocation PACKET_IVR_DELETE_SIDING = new ResourceLocation("ivr", "packet_delete_siding");
    ResourceLocation PACKET_IVR_DELETE_ROUTE = new ResourceLocation("ivr", "packet_delete_route");
    ResourceLocation PACKET_IVR_DELETE_DEPOT = new ResourceLocation("ivr", "packet_delete_depot");
}
