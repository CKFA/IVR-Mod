package net.hulan.ivr;

import com.mojang.logging.LogUtils;
import mtr.CreativeModeTabs;
import mtr.MTR;
import mtr.RegistryObject;
import mtr.block.BlockTicketBarrier;
import mtr.data.Depot;
import mtr.data.Route;
import mtr.data.Station;
import mtr.data.TicketSystem;
import mtr.item.ItemBlockEnchanted;
import mtr.item.ItemWithCreativeTabBase;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.FabricRegistryUtilities;
import mtr.mappings.RegistryUtilities;
import net.fabricmc.api.ModInitializer;
import net.hulan.ivr.client.IVRClientData;
import net.hulan.ivr.packet.IVRPacket;
import net.hulan.ivr.packet.IVRPacketTrainDataGuiServer;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.function.BiConsumer;

public class IVR implements ModInitializer, IVRPacket, IVRBlocks, IVRBlockEntityTypes, IVRCreativeModTabs {

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "ivr";

    @Override
    public void onInitialize() {
        init(IVR::registerItem, IVR::registerBlock, IVR::registerBlockItem, IVR::registerBlockEntityType, IVR::registerEnchantedBlockItem);
    }

    private void init(BiConsumer<String, RegistryObject<Item>> registerItem,
                      BiConsumer<String, RegistryObject<Block>> registerBlock,
                      MTR.RegisterBlockItem registerBlockItem,
                      BiConsumer<String, RegistryObject<? extends BlockEntityType<? extends BlockEntityMapper>>> registerBlockEntityType,
                      MTR.RegisterBlockItem registerEnchantedBlockItem) {
        registerItem.accept("kcr_apg_door", IVRItems.KCR_APG_DOOR);
        registerItem.accept("kcr_apg_glass", IVRItems.KCR_APG_GLASS);
        registerItem.accept("kcr_apg_glass_end", IVRItems.KCR_APG_GLASS_END);
        registerItem.accept("kcr_psd_door", IVRItems.KCR_PSD_DOOR_1);
        registerItem.accept("kcr_psd_glass", IVRItems.KCR_PSD_GLASS_1);
        registerItem.accept("kcr_psd_glass_end", IVRItems.KCR_PSD_GLASS_END_1);
        registerItem.accept("kcr_psd_door_2", IVRItems.KCR_PSD_DOOR_2);
        registerItem.accept("kcr_psd_glass_2", IVRItems.KCR_PSD_GLASS_2);
        registerItem.accept("kcr_psd_glass_end_2", IVRItems.KCR_PSD_GLASS_END_2);
        registerBlockItem.accept("classical_sign_1_even", CLASSICAL_SIGN_1_EVEN, IVR_);
        registerBlockItem.accept("classical_sign_1_odd", CLASSICAL_SIGN_1_ODD, IVR_);
        registerBlockItem.accept("classical_sign_2_even", CLASSICAL_SIGN_2_EVEN, IVR_);
        registerBlockItem.accept("classical_sign_2_odd", CLASSICAL_SIGN_2_ODD, IVR_);
        registerBlockItem.accept("classical_sign_3_even", CLASSICAL_SIGN_3_EVEN, IVR_);
        registerBlockItem.accept("classical_sign_3_odd", CLASSICAL_SIGN_3_ODD, IVR_);
        registerBlockItem.accept("classical_sign_4_even", CLASSICAL_SIGN_4_EVEN, IVR_);
        registerBlockItem.accept("classical_sign_4_odd", CLASSICAL_SIGN_4_ODD, IVR_);
        registerBlockItem.accept("classical_sign_5_even", CLASSICAL_SIGN_5_EVEN, IVR_);
        registerBlockItem.accept("classical_sign_5_odd", CLASSICAL_SIGN_5_ODD, IVR_);
        registerBlockItem.accept("classical_sign_6_even", CLASSICAL_SIGN_6_EVEN, IVR_);
        registerBlockItem.accept("classical_sign_6_odd", CLASSICAL_SIGN_6_ODD, IVR_);
        registerBlockItem.accept("classical_sign_7_even", CLASSICAL_SIGN_7_EVEN, IVR_);
        registerBlockItem.accept("classical_sign_7_odd", CLASSICAL_SIGN_7_ODD, IVR_);
        registerBlock.accept("classical_sign_middle", CLASSICAL_SIGN_MIDDLE);
        registerBlockItem.accept("classical_sign_pole", CLASSICAL_SIGN_POLE, IVR_);
        registerBlockItem.accept("ivr_logo", IVR_LOGO, IVR_);
        registerBlock.accept("kcr_apg_door", KCR_APG_DOOR);
        registerBlock.accept("kcr_apg_glass", KCR_APG_GLASS);
        registerBlock.accept("kcr_apg_glass_end", KCR_APG_GLASS_END);
        registerBlockItem.accept("kcr_ceiling_auto", KCR_CEILING_AUTO, IVR_);
        registerBlockItem.accept("kcr_ceiling_light", KCR_CEILING_LIGHT, IVR_);
        registerBlockItem.accept("kcr_ceiling_no_light", KCR_CEILING_NO_LIGHT, IVR_);
        registerBlockItem.accept("kcr_clock", KCR_CLOCK, IVR_);
        registerBlockItem.accept("kcr_clock_pole", KCR_CLOCK_POLE, IVR_);
        registerBlockItem.accept("kcr_platform_normal", KCR_PLATFORM_NORMAL, IVR_);
        registerBlockItem.accept("kcr_platform_yellow_line", KCR_PLATFORM_YELLOW_LINE, IVR_);
        registerBlockItem.accept("kcr_platform_indented", KCR_PLATFORM_INDENTED, IVR_);
        registerBlock.accept("kcr_psd_door", KCR_PSD_DOOR_1);
        registerBlock.accept("kcr_psd_glass", KCR_PSD_GLASS_1);
        registerBlock.accept("kcr_psd_glass_end", KCR_PSD_GLASS_END_1);
        registerBlock.accept("kcr_psd_door_2", KCR_PSD_DOOR_2);
        registerBlock.accept("kcr_psd_glass_2", KCR_PSD_GLASS_2);
        registerBlock.accept("kcr_psd_glass_end_2", KCR_PSD_GLASS_END_2);
        registerBlock.accept("kcr_psd_top", KCR_PSD_TOP);
        registerBlockItem.accept("kcr_station_name_entrance", KCR_STATION_NAME_ENTRANCE, IVR_);
        registerEnchantedBlockItem.accept("kcr_station_name_tall_block", KCR_STATION_NAME_TALL_BLOCK, IVR_);
        registerEnchantedBlockItem.accept("kcr_station_name_tall_block_double_sided", KCR_STATION_NAME_TALL_BLOCK_DOUBLE_SIDED, IVR_);
        registerEnchantedBlockItem.accept("kcr_station_name_tall_wall", KCR_STATION_NAME_TALL_WALL, IVR_);
        registerEnchantedBlockItem.accept("kcr_station_name_wall_white", KCR_STATION_NAME_WALL_WHITE, IVR_);
        registerEnchantedBlockItem.accept("kcr_station_name_wall_gray", KCR_STATION_NAME_WALL_GRAY, IVR_);
        registerEnchantedBlockItem.accept("kcr_station_name_wall_black", KCR_STATION_NAME_WALL_BLACK, IVR_);
        registerBlockItem.accept("modern_sign_1_even", MODERN_SIGN_1_EVEN, IVR_);
        registerBlockItem.accept("modern_sign_1_odd", MODERN_SIGN_1_ODD, IVR_);
        registerBlockItem.accept("modern_sign_2_even", MODERN_SIGN_2_EVEN, IVR_);
        registerBlockItem.accept("modern_sign_2_odd", MODERN_SIGN_2_ODD, IVR_);
        registerBlockItem.accept("modern_sign_3_even", MODERN_SIGN_3_EVEN, IVR_);
        registerBlockItem.accept("modern_sign_3_odd", MODERN_SIGN_3_ODD, IVR_);
        registerBlockItem.accept("modern_sign_4_even", MODERN_SIGN_4_EVEN, IVR_);
        registerBlockItem.accept("modern_sign_4_odd", MODERN_SIGN_4_ODD, IVR_);
        registerBlockItem.accept("modern_sign_5_even", MODERN_SIGN_5_EVEN, IVR_);
        registerBlockItem.accept("modern_sign_5_odd", MODERN_SIGN_5_ODD, IVR_);
        registerBlockItem.accept("modern_sign_6_even", MODERN_SIGN_6_EVEN, IVR_);
        registerBlockItem.accept("modern_sign_6_odd", MODERN_SIGN_6_ODD, IVR_);
        registerBlockItem.accept("modern_sign_7_even", MODERN_SIGN_7_EVEN, IVR_);
        registerBlockItem.accept("modern_sign_7_odd", MODERN_SIGN_7_ODD, IVR_);
        registerBlock.accept("modern_sign_middle", MODERN_SIGN_MIDDLE);
        registerBlockItem.accept("modern_sign_pole", MODERN_SIGN_POLE, IVR_);
        registerBlockItem.accept("kcr_station_wall", KCR_STATION_WALL, IVR_);
        registerBlockItem.accept("kcr_station_wall_yellow", KCR_STATION_WALL_YELLOW, IVR_);
        registerBlockItem.accept("kcr_station_wall_slab", KCR_STATION_WALL_SLAB, IVR_);
        registerBlockItem.accept("kcr_station_wall_yellow_slab", KCR_STATION_WALL_YELLOW_SLAB, IVR_);
        registerEnchantedBlockItem.accept("kcr_station_color_station_wall", KCR_STATION_COLOR_STATION_WALL, IVR_);
        registerEnchantedBlockItem.accept("kcr_station_color_station_wall_slab", KCR_STATION_COLOR_STATION_WALL_SLAB, IVR_);
        registerBlockItem.accept("modern_route_sign", MODERN_ROUTE, IVR_);
        registerBlockEntityType.accept("classical_sign_1_even", CLASSICAL_SIGN_1_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("classical_sign_1_odd", CLASSICAL_SIGN_1_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("classical_sign_2_even", CLASSICAL_SIGN_2_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("classical_sign_2_odd", CLASSICAL_SIGN_2_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("classical_sign_3_even", CLASSICAL_SIGN_3_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("classical_sign_3_odd", CLASSICAL_SIGN_3_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("classical_sign_4_even", CLASSICAL_SIGN_4_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("classical_sign_4_odd", CLASSICAL_SIGN_4_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("classical_sign_5_even", CLASSICAL_SIGN_5_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("classical_sign_5_odd", CLASSICAL_SIGN_5_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("classical_sign_6_even", CLASSICAL_SIGN_6_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("classical_sign_6_odd", CLASSICAL_SIGN_6_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("classical_sign_7_even", CLASSICAL_SIGN_7_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("classical_sign_7_odd", CLASSICAL_SIGN_7_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("modern_sign_1_even", MODERN_SIGN_1_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("modern_sign_1_odd", MODERN_SIGN_1_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("modern_sign_2_even", MODERN_SIGN_2_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("modern_sign_2_odd", MODERN_SIGN_2_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("modern_sign_3_even", MODERN_SIGN_3_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("modern_sign_3_odd", MODERN_SIGN_3_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("modern_sign_4_even", MODERN_SIGN_4_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("modern_sign_4_odd", MODERN_SIGN_4_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("modern_sign_5_even", MODERN_SIGN_5_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("modern_sign_5_odd", MODERN_SIGN_5_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("modern_sign_6_even", MODERN_SIGN_6_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("modern_sign_6_odd", MODERN_SIGN_6_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("modern_sign_7_even", MODERN_SIGN_7_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("modern_sign_7_odd", MODERN_SIGN_7_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("kcr_clock", KCR_CLOCK_TILE_ENTITY);
        registerBlockEntityType.accept("kcr_apg_glass", KCR_APG_GLASS_TILE_ENTITY);
        registerBlockEntityType.accept("kcr_apg_door", KCR_APG_DOOR_TILE_ENTITY);
        registerBlockEntityType.accept("kcr_psd_door_1", KCR_PSD_DOOR_1_TILE_ENTITY);
        registerBlockEntityType.accept("kcr_psd_door_2", KCR_PSD_DOOR_2_TILE_ENTITY);
        registerBlockEntityType.accept("kcr_psd_top", KCR_PSD_TOP_TILE_ENTITY);
        registerBlockEntityType.accept("kcr_station_name_entrance", KCR_STATION_NAME_ENTRANCE_TILE_ENTITY);
        registerBlockEntityType.accept("kcr_station_name_wall_white", KCR_STATION_NAME_WALL_WHITE_TILE_ENTITY);
        registerBlockEntityType.accept("kcr_station_name_wall_gray", KCR_STATION_NAME_WALL_GRAY_TILE_ENTITY);
        registerBlockEntityType.accept("kcr_station_name_wall_black", KCR_STATION_NAME_WALL_BLACK_TILE_ENTITY);
        registerBlockEntityType.accept("kcr_station_name_tall_block", KCR_STATION_NAME_TALL_BLOCK_TILE_ENTITY);
        registerBlockEntityType.accept("kcr_station_name_tall_block_double_sided", KCR_STATION_NAME_TALL_BLOCK_DOUBLE_SIDED_TILE_ENTITY);
        registerBlockEntityType.accept("kcr_station_name_tall_wall", KCR_STATION_NAME_TALL_WALL_TILE_ENTITY);
        registerBlockEntityType.accept("modern_route_sign", MODERN_ROUTE_SIGN_TILE_ENTITY);
        mtr.Registry.registerNetworkReceiver(PACKET_CLASSICAL_SIGN_TYPES, IVRPacketTrainDataGuiServer::receiveClassicalSignIdsC2S);
        mtr.Registry.registerNetworkReceiver(PACKET_CLASSICAL_1ODD_SIGN_TYPES, IVRPacketTrainDataGuiServer::receiveClassicalSign1OddIdsC2S);
        mtr.Registry.registerNetworkReceiver(PACKET_MODERN_SIGN_TYPES, IVRPacketTrainDataGuiServer::receiveModernSignIdsC2S);
        mtr.Registry.registerNetworkReceiver(PACKET_IVR_UPDATE_STATION, (minecraftServer, player, packet) -> IVRPacketTrainDataGuiServer.receiveUpdateOrDeleteC2S(minecraftServer, player, packet, PACKET_IVR_UPDATE_STATION, (railwayData) -> railwayData.stations, (railwayData) -> railwayData.dataCache.stationIdMap, (id, transportMode) -> new Station(id), false));
        mtr.Registry.registerNetworkReceiver(PACKET_IVR_UPDATE_PLATFORM, (minecraftServer, player, packet) -> IVRPacketTrainDataGuiServer.receiveUpdateOrDeleteC2S(minecraftServer, player, packet, PACKET_IVR_UPDATE_PLATFORM, (railwayData) -> railwayData.platforms, (railwayData) -> railwayData.dataCache.platformIdMap, null, false));
        mtr.Registry.registerNetworkReceiver(PACKET_IVR_UPDATE_SIDING, (minecraftServer, player, packet) -> IVRPacketTrainDataGuiServer.receiveUpdateOrDeleteC2S(minecraftServer, player, packet, PACKET_IVR_UPDATE_SIDING, (railwayData) -> railwayData.sidings, (railwayData) -> railwayData.dataCache.sidingIdMap, null, false));
        mtr.Registry.registerNetworkReceiver(PACKET_IVR_UPDATE_ROUTE, (minecraftServer, player, packet) -> IVRPacketTrainDataGuiServer.receiveUpdateOrDeleteC2S(minecraftServer, player, packet, PACKET_IVR_UPDATE_ROUTE, (railwayData) -> railwayData.routes, (railwayData) -> railwayData.dataCache.routeIdMap, Route::new, false));
        mtr.Registry.registerNetworkReceiver(PACKET_IVR_UPDATE_DEPOT, (minecraftServer, player, packet) -> IVRPacketTrainDataGuiServer.receiveUpdateOrDeleteC2S(minecraftServer, player, packet, PACKET_IVR_UPDATE_DEPOT, (railwayData) -> railwayData.depots, (railwayData) -> railwayData.dataCache.depotIdMap, Depot::new, false));
        mtr.Registry.registerNetworkReceiver(PACKET_IVR_UPDATE_LIFT, (minecraftServer, player, packet) -> IVRPacketTrainDataGuiServer.receiveUpdateOrDeleteC2S(minecraftServer, player, packet, PACKET_IVR_UPDATE_LIFT, (railwayData) -> railwayData.lifts, (railwayData) -> railwayData.dataCache.liftsServerIdMap, null, false));
        mtr.Registry.registerNetworkReceiver(PACKET_IVR_DELETE_STATION, (minecraftServer, player, packet) -> IVRPacketTrainDataGuiServer.receiveUpdateOrDeleteC2S(minecraftServer, player, packet, PACKET_IVR_DELETE_STATION, (railwayData) -> railwayData.stations, (railwayData) -> railwayData.dataCache.stationIdMap, null, true));
        mtr.Registry.registerNetworkReceiver(PACKET_IVR_DELETE_PLATFORM, (minecraftServer, player, packet) -> IVRPacketTrainDataGuiServer.receiveUpdateOrDeleteC2S(minecraftServer, player, packet, PACKET_IVR_DELETE_PLATFORM, (railwayData) -> railwayData.platforms, (railwayData) -> railwayData.dataCache.platformIdMap, null, true));
        mtr.Registry.registerNetworkReceiver(PACKET_IVR_DELETE_SIDING, (minecraftServer, player, packet) -> IVRPacketTrainDataGuiServer.receiveUpdateOrDeleteC2S(minecraftServer, player, packet, PACKET_IVR_DELETE_SIDING, (railwayData) -> railwayData.sidings, (railwayData) -> railwayData.dataCache.sidingIdMap, null, true));
        mtr.Registry.registerNetworkReceiver(PACKET_IVR_DELETE_ROUTE, (minecraftServer, player, packet) -> IVRPacketTrainDataGuiServer.receiveUpdateOrDeleteC2S(minecraftServer, player, packet, PACKET_IVR_DELETE_ROUTE, (railwayData) -> railwayData.routes, (railwayData) -> railwayData.dataCache.routeIdMap, null, true));
        mtr.Registry.registerNetworkReceiver(PACKET_IVR_DELETE_DEPOT, (minecraftServer, player, packet) -> IVRPacketTrainDataGuiServer.receiveUpdateOrDeleteC2S(minecraftServer, player, packet, PACKET_IVR_DELETE_DEPOT, (railwayData) -> railwayData.depots, (railwayData) -> railwayData.dataCache.depotIdMap, null, true));
        mtr.Registry.registerPlayerJoinEvent(IVRClientData::onPlayerJoin);
    }

    private static void registerItem(String path, RegistryObject<Item> item) {
        Item itemObject = item.get();
        Registry.register(RegistryUtilities.registryGetItem(), new Identifier(MOD_ID, path), itemObject);
        if (itemObject instanceof ItemWithCreativeTabBase) {
            FabricRegistryUtilities.registerCreativeModeTab(((ItemWithCreativeTabBase)itemObject).creativeModeTab.get(), itemObject);
        } else if (itemObject instanceof ItemWithCreativeTabBase.ItemPlaceOnWater) {
            FabricRegistryUtilities.registerCreativeModeTab(((ItemWithCreativeTabBase.ItemPlaceOnWater)itemObject).creativeModeTab.get(), itemObject);
        }
    }

    private static void registerBlock(String path, RegistryObject<Block> block) {
        Registry.register(RegistryUtilities.registryGetBlock(), new Identifier(MOD_ID, path), block.get());
    }

    private static void registerBlockItem(String path, RegistryObject<Block> block, CreativeModeTabs.Wrapper creativeModeTab) {
        registerBlock(path, block);
        final BlockItem blockItem = new BlockItem(block.get(), RegistryUtilities.createItemProperties(creativeModeTab::get));
        Registry.register(RegistryUtilities.registryGetItem(), new Identifier(MOD_ID, path), blockItem);
        FabricRegistryUtilities.registerCreativeModeTab(creativeModeTab.get(), blockItem);
    }

    private static void registerEnchantedBlockItem(String path, RegistryObject<Block> block, CreativeModeTabs.Wrapper creativeModeTab) {
        registerBlock(path, block);
        Objects.requireNonNull(creativeModeTab);
        ItemBlockEnchanted itemBlockEnchanted = new ItemBlockEnchanted(block.get(), RegistryUtilities.createItemProperties(creativeModeTab::get));
        Registry.register(RegistryUtilities.registryGetItem(), new Identifier(MOD_ID, path), itemBlockEnchanted);
        FabricRegistryUtilities.registerCreativeModeTab(creativeModeTab.get(), itemBlockEnchanted);
    }

    private static void registerBlockEntityType(String path, RegistryObject<? extends BlockEntityType<? extends BlockEntityMapper>> blockEntityType) {
        Registry.register(RegistryUtilities.registryGetBlockEntityType(), new Identifier(MOD_ID, path), (BlockEntityType<? extends BlockEntityMapper>)blockEntityType.get());
    }
}
