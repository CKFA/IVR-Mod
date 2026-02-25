package net.hulan.ivr.client;

import mtr.data.RailwayData;
import net.hulan.ivr.packet.IVRPacketTrainDataGuiServer;
import net.minecraft.server.level.ServerPlayer;

public class IVRClientData {

    public static final IVRClientCache DATA_CACHE = new IVRClientCache();

    public static void onPlayerJoin(ServerPlayer player) {
        RailwayData railwayData = RailwayData.getInstance(player.getLevel());
        if (railwayData != null) {
            IVRPacketTrainDataGuiServer.sendAllInChunks(player, railwayData.stations, railwayData.platforms, railwayData.sidings, railwayData.routes, railwayData.depots, railwayData.lifts);
            railwayData.railwayDataCoolDownModule.onPlayerJoin(player);
        }
    }

    public static void receivePacket() {
        DATA_CACHE.sync();
        DATA_CACHE.refreshDynamicResources();
    }
}
