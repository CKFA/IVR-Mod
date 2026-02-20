package net.hulan.ivr.packet;

import net.minecraft.util.Identifier;

public interface IVRPacket {

    Identifier PACKET_OPEN_CLASSICAL_SIGN_SCREEN = new Identifier("ivr", "packet_open_classical_sign_screen");
    Identifier PACKET_OPEN_CLASSICAL_1ODD_SIGN_SCREEN = new Identifier("ivr", "packet_open_classical_1odd_sign_screen");
    Identifier PACKET_OPEN_MODERN_SIGN_SCREEN = new Identifier("ivr", "packet_open_modern_sign_screen");
    Identifier PACKET_CLASSICAL_SIGN_TYPES = new Identifier("ivr", "packet_classical_sign_types");
    Identifier PACKET_CLASSICAL_1ODD_SIGN_TYPES = new Identifier("ivr", "packet_classical_1odd_sign_types");
    Identifier PACKET_MODERN_SIGN_TYPES = new Identifier("ivr", "packet_modern_sign_types");
    Identifier PACKET_IVR_CHUNK_S2C = new Identifier("ivr", "packet_chunk_s2c");
    Identifier PACKET_IVR_UPDATE_STATION = new Identifier("ivr", "packet_update_station");
    Identifier PACKET_IVR_UPDATE_PLATFORM = new Identifier("ivr", "packet_update_platform");
    Identifier PACKET_IVR_UPDATE_SIDING = new Identifier("ivr", "packet_update_siding");
    Identifier PACKET_IVR_UPDATE_ROUTE = new Identifier("ivr", "packet_update_route");
    Identifier PACKET_IVR_UPDATE_DEPOT = new Identifier("ivr", "packet_update_depot");
    Identifier PACKET_IVR_UPDATE_LIFT = new Identifier("ivr", "packet_update_lift");
    Identifier PACKET_IVR_DELETE_STATION = new Identifier("ivr", "packet_delete_station");
    Identifier PACKET_IVR_DELETE_PLATFORM = new Identifier("ivr", "packet_delete_platform");
    Identifier PACKET_IVR_DELETE_SIDING = new Identifier("ivr", "packet_delete_siding");
    Identifier PACKET_IVR_DELETE_ROUTE = new Identifier("ivr", "packet_delete_route");
    Identifier PACKET_IVR_DELETE_DEPOT = new Identifier("ivr", "packet_delete_depot");
}
