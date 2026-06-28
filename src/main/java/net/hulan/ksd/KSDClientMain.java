package net.hulan.ksd;

import mtr.RegistryClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hulan.ksd.client.KSDClientData;
import net.hulan.ksd.data.KSDRoute;
import net.hulan.ksd.data.KSDStation;
import net.hulan.ksd.packet.KSDPacket;
import net.hulan.ksd.packet.KSDPacketClient;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class KSDClientMain implements ClientModInitializer, KSDPacket {

    @Override
    public void onInitializeClient() {
        RegistryClient.registerNetworkReceiver(KSD_PACKET_OPEN_KSD_DASHBOARD_SCREEN, (packet) -> KSDPacketClient.openKSDDashboardScreenS2C(Minecraft.getInstance(), packet));
        RegistryClient.registerNetworkReceiver(KSD_PACKET_CHUNK_S2C, (packet) -> KSDPacketClient.receiveChunk(Minecraft.getInstance(), packet));
        RegistryClient.registerNetworkReceiver(KSD_PACKET_UPDATE_STATION, (packet) -> KSDPacketClient.receiveUpdateOrDeleteS2C(Minecraft.getInstance(),
                packet,
                KSDClientData.STATIONS,
                KSDClientData.DATA_CACHE.stationIdMap,
                KSDStation::new,
                false));
        RegistryClient.registerNetworkReceiver(KSD_PACKET_UPDATE_PLATFORM, packet -> KSDPacketClient.receiveUpdateOrDeleteS2C(Minecraft.getInstance(),
                packet,
                KSDClientData.PLATFORMS,
                KSDClientData.DATA_CACHE.platformIdMap,
                null,
                false));
        RegistryClient.registerNetworkReceiver(KSD_PACKET_UPDATE_ROUTE, packet -> KSDPacketClient.receiveUpdateOrDeleteS2C(Minecraft.getInstance(),
                packet,
                KSDClientData.ROUTES,
                KSDClientData.DATA_CACHE.routeIdMap,
                KSDRoute::new,
                false));
        RegistryClient.registerNetworkReceiver(KSD_PACKET_DELETE_STATION, (packet) -> KSDPacketClient.receiveUpdateOrDeleteS2C(Minecraft.getInstance(),
                packet,
                KSDClientData.STATIONS,
                KSDClientData.DATA_CACHE.stationIdMap,
                KSDStation::new,
                true));
        RegistryClient.registerNetworkReceiver(KSD_PACKET_DELETE_PLATFORM, packet -> KSDPacketClient.receiveUpdateOrDeleteS2C(Minecraft.getInstance(),
                packet,
                KSDClientData.PLATFORMS,
                KSDClientData.DATA_CACHE.platformIdMap,
                null,
                true));
        RegistryClient.registerNetworkReceiver(KSD_PACKET_DELETE_ROUTE, packet -> KSDPacketClient.receiveUpdateOrDeleteS2C(Minecraft.getInstance(),
                packet,
                KSDClientData.ROUTES,
                KSDClientData.DATA_CACHE.routeIdMap,
                KSDRoute::new,
                true));
    }
}
