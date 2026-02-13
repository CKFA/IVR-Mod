package net.hulan.ivr;

import mtr.RegistryClient;
import mtr.client.ClientData;
import mtr.data.Depot;
import mtr.data.Route;
import mtr.data.Station;
import net.fabricmc.api.ClientModInitializer;
import net.hulan.ivr.packet.IVRPacket;
import net.hulan.ivr.packet.IVRPacketTrainDataGuiClient;
import net.hulan.ivr.render.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class IVRClient implements ClientModInitializer, IVRPacket, IVRBlockEntityTypes, IVRBlocks {

    @Override
    public void onInitializeClient() {
        RegistryClient.registerBlockRenderType(RenderLayer.getCutout(), KCR_CLOCK.get());
        RegistryClient.registerBlockRenderType(RenderLayer.getCutout(), IVR_LOGO.get());
        RegistryClient.registerBlockRenderType(RenderLayer.getCutout(), KCR_PLATFORM_INDENTED.get());
        RegistryClient.registerBlockRenderType(RenderLayer.getCutout(), KCR_APG_DOOR.get());
        RegistryClient.registerBlockRenderType(RenderLayer.getCutout(), KCR_APG_GLASS.get());
        RegistryClient.registerBlockRenderType(RenderLayer.getCutout(), KCR_APG_GLASS_END.get());
        RegistryClient.registerBlockRenderType(RenderLayer.getCutout(), KCR_PSD_DOOR_1.get());
        RegistryClient.registerBlockRenderType(RenderLayer.getCutout(), KCR_PSD_GLASS_1.get());
        RegistryClient.registerBlockRenderType(RenderLayer.getCutout(), KCR_PSD_GLASS_END_1.get());
        RegistryClient.registerBlockRenderType(RenderLayer.getCutout(), KCR_PSD_DOOR_2.get());
        RegistryClient.registerBlockRenderType(RenderLayer.getCutout(), KCR_PSD_GLASS_2.get());
        RegistryClient.registerBlockRenderType(RenderLayer.getCutout(), KCR_PSD_GLASS_END_2.get());
        RegistryClient.registerBlockRenderType(RenderLayer.getCutout(), KCR_STATION_NAME_TALL_BLOCK.get());
        RegistryClient.registerBlockRenderType(RenderLayer.getCutout(), KCR_STATION_NAME_TALL_BLOCK_DOUBLE_SIDED.get());
        RegistryClient.registerBlockRenderType(RenderLayer.getCutout(), KCR_STATION_NAME_TALL_WALL.get());
        RegistryClient.registerBlockColors(KCR_STATION_COLOR_STATION_WALL.get());
        RegistryClient.registerBlockColors(KCR_STATION_COLOR_STATION_WALL_SLAB.get());
        RegistryClient.registerTileEntityRenderer(CLASSICAL_SIGN_1_EVEN_TILE_ENTITY.get(), RenderClassicalSign::new);
        RegistryClient.registerTileEntityRenderer(CLASSICAL_SIGN_1_ODD_TILE_ENTITY.get(), RenderClassicalSign::new);
        RegistryClient.registerTileEntityRenderer(CLASSICAL_SIGN_2_EVEN_TILE_ENTITY.get(), RenderClassicalSign::new);
        RegistryClient.registerTileEntityRenderer(CLASSICAL_SIGN_2_ODD_TILE_ENTITY.get(), RenderClassicalSign::new);
        RegistryClient.registerTileEntityRenderer(CLASSICAL_SIGN_3_EVEN_TILE_ENTITY.get(), RenderClassicalSign::new);
        RegistryClient.registerTileEntityRenderer(CLASSICAL_SIGN_3_ODD_TILE_ENTITY.get(), RenderClassicalSign::new);
        RegistryClient.registerTileEntityRenderer(CLASSICAL_SIGN_4_EVEN_TILE_ENTITY.get(), RenderClassicalSign::new);
        RegistryClient.registerTileEntityRenderer(CLASSICAL_SIGN_4_ODD_TILE_ENTITY.get(), RenderClassicalSign::new);
        RegistryClient.registerTileEntityRenderer(CLASSICAL_SIGN_5_EVEN_TILE_ENTITY.get(), RenderClassicalSign::new);
        RegistryClient.registerTileEntityRenderer(CLASSICAL_SIGN_5_ODD_TILE_ENTITY.get(), RenderClassicalSign::new);
        RegistryClient.registerTileEntityRenderer(CLASSICAL_SIGN_6_EVEN_TILE_ENTITY.get(), RenderClassicalSign::new);
        RegistryClient.registerTileEntityRenderer(CLASSICAL_SIGN_6_ODD_TILE_ENTITY.get(), RenderClassicalSign::new);
        RegistryClient.registerTileEntityRenderer(CLASSICAL_SIGN_7_EVEN_TILE_ENTITY.get(), RenderClassicalSign::new);
        RegistryClient.registerTileEntityRenderer(CLASSICAL_SIGN_7_ODD_TILE_ENTITY.get(), RenderClassicalSign::new);
        RegistryClient.registerTileEntityRenderer(MODERN_SIGN_1_EVEN_TILE_ENTITY.get(), RenderModernSign::new);
        RegistryClient.registerTileEntityRenderer(MODERN_SIGN_1_ODD_TILE_ENTITY.get(), RenderModernSign::new);
        RegistryClient.registerTileEntityRenderer(MODERN_SIGN_2_EVEN_TILE_ENTITY.get(), RenderModernSign::new);
        RegistryClient.registerTileEntityRenderer(MODERN_SIGN_2_ODD_TILE_ENTITY.get(), RenderModernSign::new);
        RegistryClient.registerTileEntityRenderer(MODERN_SIGN_3_EVEN_TILE_ENTITY.get(), RenderModernSign::new);
        RegistryClient.registerTileEntityRenderer(MODERN_SIGN_3_ODD_TILE_ENTITY.get(), RenderModernSign::new);
        RegistryClient.registerTileEntityRenderer(MODERN_SIGN_4_EVEN_TILE_ENTITY.get(), RenderModernSign::new);
        RegistryClient.registerTileEntityRenderer(MODERN_SIGN_4_ODD_TILE_ENTITY.get(), RenderModernSign::new);
        RegistryClient.registerTileEntityRenderer(MODERN_SIGN_5_EVEN_TILE_ENTITY.get(), RenderModernSign::new);
        RegistryClient.registerTileEntityRenderer(MODERN_SIGN_5_ODD_TILE_ENTITY.get(), RenderModernSign::new);
        RegistryClient.registerTileEntityRenderer(MODERN_SIGN_6_EVEN_TILE_ENTITY.get(), RenderModernSign::new);
        RegistryClient.registerTileEntityRenderer(MODERN_SIGN_6_ODD_TILE_ENTITY.get(), RenderModernSign::new);
        RegistryClient.registerTileEntityRenderer(MODERN_SIGN_7_EVEN_TILE_ENTITY.get(), RenderModernSign::new);
        RegistryClient.registerTileEntityRenderer(MODERN_SIGN_7_ODD_TILE_ENTITY.get(), RenderModernSign::new);
        RegistryClient.registerTileEntityRenderer(KCR_CLOCK_TILE_ENTITY.get(), RenderKCRClock::new);
        RegistryClient.registerTileEntityRenderer(KCR_PSD_DOOR_1_TILE_ENTITY.get(), dispatcher -> new RenderKCRPSDAPGDoor<>(dispatcher, 0));
        RegistryClient.registerTileEntityRenderer(KCR_PSD_DOOR_2_TILE_ENTITY.get(), dispatcher -> new RenderKCRPSDAPGDoor<>(dispatcher, 1));
        RegistryClient.registerTileEntityRenderer(KCR_PSD_TOP_TILE_ENTITY.get(), RenderKCRPSDTop::new);
        RegistryClient.registerTileEntityRenderer(KCR_APG_GLASS_TILE_ENTITY.get(), RenderKCRAPGGlass::new);
        RegistryClient.registerTileEntityRenderer(KCR_APG_DOOR_TILE_ENTITY.get(), dispatcher -> new RenderKCRPSDAPGDoor<>(dispatcher, 2));
        RegistryClient.registerTileEntityRenderer(KCR_STATION_NAME_ENTRANCE_TILE_ENTITY.get(), (dispatcher) -> new RenderKCRStationNameTiled<>(dispatcher, true));
        RegistryClient.registerTileEntityRenderer(KCR_STATION_NAME_TALL_BLOCK_TILE_ENTITY.get(), RenderKCRStationNameTall::new);
        RegistryClient.registerTileEntityRenderer(KCR_STATION_NAME_TALL_BLOCK_DOUBLE_SIDED_TILE_ENTITY.get(), RenderKCRStationNameTall::new);
        RegistryClient.registerTileEntityRenderer(KCR_STATION_NAME_TALL_WALL_TILE_ENTITY.get(), RenderKCRStationNameTall::new);
        RegistryClient.registerTileEntityRenderer(KCR_STATION_NAME_WALL_WHITE_TILE_ENTITY.get(), (dispatcher) -> new RenderKCRStationNameTiled<>(dispatcher, false));
        RegistryClient.registerTileEntityRenderer(KCR_STATION_NAME_WALL_GRAY_TILE_ENTITY.get(), (dispatcher) -> new RenderKCRStationNameTiled<>(dispatcher, false));
        RegistryClient.registerTileEntityRenderer(KCR_STATION_NAME_WALL_BLACK_TILE_ENTITY.get(), (dispatcher) -> new RenderKCRStationNameTiled<>(dispatcher, false));
        RegistryClient.registerTileEntityRenderer(MODERN_ROUTE_SIGN_TILE_ENTITY.get(), RenderModernRouteSign::new);
        RegistryClient.registerNetworkReceiver(PACKET_OPEN_MODERN_SIGN_SCREEN, packet -> IVRPacketTrainDataGuiClient.openModernSignScreenS2C(MinecraftClient.getInstance(), packet));
        RegistryClient.registerNetworkReceiver(PACKET_OPEN_CLASSICAL_SIGN_SCREEN, packet -> IVRPacketTrainDataGuiClient.openClassicalSignScreenS2C(MinecraftClient.getInstance(), packet));
        RegistryClient.registerNetworkReceiver(PACKET_IVR_CHUNK_S2C, (packet) -> IVRPacketTrainDataGuiClient.receiveChunk(MinecraftClient.getInstance(), packet));
        RegistryClient.registerNetworkReceiver(PACKET_IVR_UPDATE_STATION, (packet) -> IVRPacketTrainDataGuiClient.receiveUpdateOrDeleteS2C(MinecraftClient.getInstance(), packet, ClientData.STATIONS, ClientData.DATA_CACHE.stationIdMap, (id, transportMode) -> new Station(id), false));
        RegistryClient.registerNetworkReceiver(PACKET_IVR_UPDATE_PLATFORM, (packet) -> IVRPacketTrainDataGuiClient.receiveUpdateOrDeleteS2C(MinecraftClient.getInstance(), packet, ClientData.PLATFORMS, ClientData.DATA_CACHE.platformIdMap, null, false));
        RegistryClient.registerNetworkReceiver(PACKET_IVR_UPDATE_SIDING, (packet) -> IVRPacketTrainDataGuiClient.receiveUpdateOrDeleteS2C(MinecraftClient.getInstance(), packet, ClientData.SIDINGS, ClientData.DATA_CACHE.sidingIdMap, null, false));
        RegistryClient.registerNetworkReceiver(PACKET_IVR_UPDATE_ROUTE, (packet) -> IVRPacketTrainDataGuiClient.receiveUpdateOrDeleteS2C(MinecraftClient.getInstance(), packet, ClientData.ROUTES, ClientData.DATA_CACHE.routeIdMap, Route::new, false));
        RegistryClient.registerNetworkReceiver(PACKET_IVR_UPDATE_DEPOT, (packet) -> IVRPacketTrainDataGuiClient.receiveUpdateOrDeleteS2C(MinecraftClient.getInstance(), packet, ClientData.DEPOTS, ClientData.DATA_CACHE.depotIdMap, Depot::new, false));
        RegistryClient.registerNetworkReceiver(PACKET_IVR_DELETE_STATION, (packet) -> IVRPacketTrainDataGuiClient.receiveUpdateOrDeleteS2C(MinecraftClient.getInstance(), packet, ClientData.STATIONS, ClientData.DATA_CACHE.stationIdMap, (id, transportMode) -> new Station(id), true));
        RegistryClient.registerNetworkReceiver(PACKET_IVR_DELETE_PLATFORM, (packet) -> IVRPacketTrainDataGuiClient.receiveUpdateOrDeleteS2C(MinecraftClient.getInstance(), packet, ClientData.PLATFORMS, ClientData.DATA_CACHE.platformIdMap, null, true));
        RegistryClient.registerNetworkReceiver(PACKET_IVR_DELETE_SIDING, (packet) -> IVRPacketTrainDataGuiClient.receiveUpdateOrDeleteS2C(MinecraftClient.getInstance(), packet, ClientData.SIDINGS, ClientData.DATA_CACHE.sidingIdMap, null, true));
        RegistryClient.registerNetworkReceiver(PACKET_IVR_DELETE_ROUTE, (packet) -> IVRPacketTrainDataGuiClient.receiveUpdateOrDeleteS2C(MinecraftClient.getInstance(), packet, ClientData.ROUTES, ClientData.DATA_CACHE.routeIdMap, Route::new, true));
        RegistryClient.registerNetworkReceiver(PACKET_IVR_DELETE_DEPOT, (packet) -> IVRPacketTrainDataGuiClient.receiveUpdateOrDeleteS2C(MinecraftClient.getInstance(), packet, ClientData.DEPOTS, ClientData.DATA_CACHE.depotIdMap, Depot::new, true));
        RegistryClient.registerNetworkReceiver(PACKET_IVR_UPDATE_LIFT, (packet) -> IVRPacketTrainDataGuiClient.receiveUpdateOrDeleteS2C(MinecraftClient.getInstance(), packet, ClientData.LIFTS, ClientData.DATA_CACHE.liftsClientIdMap, null, false));
    }
}
