package net.hulan.ksd.packet;

import net.minecraft.resources.ResourceLocation;

public interface KSDPacket {

    ResourceLocation KSD_PACKET_OPEN_KSD_DASHBOARD_SCREEN = new ResourceLocation("ksd", "ksd_packet_open_ksd_dashboard_screen");
    ResourceLocation KSD_PACKET_OPEN_TICKET_MACHINE_SCREEN = new ResourceLocation("ksd", "ksd_packet_open_ticket_machine_screen");
    ResourceLocation KSD_PACKET_CHUNK_S2C = new ResourceLocation("ksd", "ksd_packet_chunk_s2c");
    ResourceLocation KSD_PACKET_UPDATE_STATION = new ResourceLocation("ksd", "ksd_packet_update_station");
    ResourceLocation KSD_PACKET_DELETE_STATION = new ResourceLocation("ksd", "ksd_packet_delete_station");
    ResourceLocation KSD_PACKET_UPDATE_PLATFORM = new ResourceLocation("ksd", "ksd_packet_update_platform");
    ResourceLocation KSD_PACKET_DELETE_PLATFORM = new ResourceLocation("ksd", "ksd_packet_delete_platform");
    ResourceLocation KSD_PACKET_UPDATE_ROUTE = new ResourceLocation("ksd", "ksd_packet_update_route");
    ResourceLocation KSD_PACKET_DELETE_ROUTE = new ResourceLocation("ksd", "ksd_packet_delete_route");
    //IPacket
}
