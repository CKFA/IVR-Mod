package net.hulan.ksd;

import mtr.CreativeModeTabs;
import mtr.MTR;
import mtr.RegistryObject;
import mtr.data.RailwayData;
import mtr.item.ItemWithCreativeTabBase;
import mtr.mappings.FabricRegistryUtilities;
import mtr.mappings.RegistryUtilities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.hulan.ksd.data.FirstClassValidationSystem;
import net.hulan.ksd.data.KSDRailwayData;
import net.hulan.ksd.data.KSDRoute;
import net.hulan.ksd.data.KSDStation;
import net.hulan.ksd.packet.KSDPacket;
import net.hulan.ksd.packet.KSDPacketServer;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BiConsumer;

public class KSDMain implements ModInitializer, KSDBlocks, KSDItems, KSDCreativeModTabs, KSDPacket {

    public static final String MOD_ID = "ksd";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        init(KSDMain::registerItem, KSDMain::registerBlock, KSDMain::registerBlock);
    }

    private void init(BiConsumer<String, RegistryObject<Item>> registerItem,
                      BiConsumer<String, RegistryObject<Block>> registerBlock,
                      MTR.RegisterBlockItem registerBlockItem) {
        registerBlock.accept("kp_cell_side", KP_CELL_SIDE);
        registerBlock.accept("kp_cell_side_with_light", KP_CELL_SIDE_WITH_LIGHT);
        registerItem.accept("kp_cell_side_is", KP_CELL_SIDE_IS);
        registerItem.accept("kp_cell_side_os", KP_CELL_SIDE_OS);
        registerItem.accept("kp_cell_side_os_with_light", KP_CELL_SIDE_OS_WITH_LIGHT);
        registerItem.accept("ksd_dashboard", KSD_DASHBOARD);
        //registerItem.accept("octopus", OCTOPUS);
        registerBlockItem.accept("kp_pole_normal", KP_POLE_NORMAL, KCR_PLATFORM_BLOCKS);
        registerBlockItem.accept("kp_pole_single_side", KP_POLE_1_SIDE, KCR_PLATFORM_BLOCKS);
        registerBlockItem.accept("kp_pole_double_side", KP_POLE_2_SIDE, KCR_PLATFORM_BLOCKS);
        registerBlockItem.accept("kp_horizontal_crossbar", KP_HORIZONTAL_CROSSBAR, KCR_PLATFORM_BLOCKS);
        registerBlockItem.accept("kp_horizontal_lower_crossbar", KP_HORIZONTAL_LOWER_CROSSBAR, KCR_PLATFORM_BLOCKS);
        registerBlockItem.accept("kp_horizontal_ending_crossbar", KP_HORIZONTAL_ENDING_CROSSBAR, KCR_PLATFORM_BLOCKS);
        registerBlockItem.accept("kp_longitudinal_crossbar", KP_LONGITUDINAL_CROSSBAR, KCR_PLATFORM_BLOCKS);
        registerBlockItem.accept("kp_hb_with_lb", KP_HB_WITH_LB, KCR_PLATFORM_BLOCKS);
        registerBlockItem.accept("kp_hb_with_hb", KP_HB_WITH_HB, KCR_PLATFORM_BLOCKS);
        registerBlockItem.accept("kp_cell_top", KP_CELL_TOP, KCR_PLATFORM_BLOCKS);
        registerBlockItem.accept("kp_cell_light", KP_LIGHT, KCR_PLATFORM_BLOCKS);
        registerBlockItem.accept("first_class_processor", FIRST_CLASS_PROCESSOR, KCR_PLATFORM_BLOCKS);
        mtr.Registry.registerNetworkReceiver(KSD_PACKET_UPDATE_STATION,
                (minecraftServer, player, packet) -> KSDPacketServer.receiveUpdateOrDeleteC2S(minecraftServer,
                        player,
                        packet,
                        KSD_PACKET_UPDATE_STATION,
                        (railwayData) -> railwayData.stations,
                        (railwayData) -> railwayData.dataCache.stationIdMap,
                        KSDStation::new,
                        false));
        mtr.Registry.registerNetworkReceiver(KSD_PACKET_UPDATE_PLATFORM,
                (minecraftServer, player, packet) -> KSDPacketServer.receiveUpdateOrDeleteC2S(minecraftServer,
                        player,
                        packet,
                        KSD_PACKET_UPDATE_PLATFORM,
                        railwayData -> railwayData.platforms,
                        railwayData -> railwayData.dataCache.platformIdMap,
                        null,
                        false));
        mtr.Registry.registerNetworkReceiver(KSD_PACKET_UPDATE_ROUTE,
                (minecraftServer, player, packet) -> KSDPacketServer.receiveUpdateOrDeleteC2S(minecraftServer,
                        player,
                        packet,
                        KSD_PACKET_UPDATE_ROUTE,
                        railwayData -> railwayData.routes,
                        railwayData -> railwayData.dataCache.routeIdMap,
                        KSDRoute::new,
                        false));
        mtr.Registry.registerNetworkReceiver(KSD_PACKET_DELETE_STATION,
                (minecraftServer, player, packet) -> KSDPacketServer.receiveUpdateOrDeleteC2S(minecraftServer,
                        player,
                        packet,
                        KSD_PACKET_DELETE_STATION,
                        (railwayData) -> railwayData.stations,
                        (railwayData) -> railwayData.dataCache.stationIdMap,
                        null,
                        true));
        mtr.Registry.registerNetworkReceiver(KSD_PACKET_DELETE_PLATFORM,
                (minecraftServer, player, packet) -> KSDPacketServer.receiveUpdateOrDeleteC2S(minecraftServer,
                        player,
                        packet,
                        KSD_PACKET_DELETE_PLATFORM,
                        railwayData -> railwayData.platforms,
                        railwayData -> railwayData.dataCache.platformIdMap,
                        null,
                        true));
        mtr.Registry.registerNetworkReceiver(KSD_PACKET_DELETE_ROUTE,
                (minecraftServer, player, packet) -> KSDPacketServer.receiveUpdateOrDeleteC2S(minecraftServer,
                        player,
                        packet,
                        KSD_PACKET_DELETE_ROUTE,
                        railwayData -> railwayData.routes,
                        railwayData -> railwayData.dataCache.routeIdMap,
                        null,
                        true));
        mtr.Registry.registerPlayerJoinEvent((player) -> {
            KSDRailwayData ksdRailwayData = KSDRailwayData.getInstance(player.getLevel());
            if (ksdRailwayData != null) {
                ksdRailwayData.onPlayerJoin(player);
            }
        });
        mtr.Registry.registerTickEvent(playerTick -> playerTick.getAllLevels().forEach(level -> {
            KSDRailwayData ksd = KSDRailwayData.getInstance(level);
            RailwayData mtr = RailwayData.getInstance(level);
            if (ksd != null && mtr != null) {
                FirstClassValidationSystem.tick(ksd, mtr, level, level.players());
            }
        }));
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            overworld = server.getLevel(Level.OVERWORLD);
            the_nether = server.getLevel(Level.NETHER);
            the_end = server.getLevel(Level.END);
        });
    }

    private static void registerItem(String path, RegistryObject<Item> item) {
        Item itemObject = item.get();
        Registry.register(RegistryUtilities.registryGetItem(), new ResourceLocation(MOD_ID, path), itemObject);
        if (itemObject instanceof ItemWithCreativeTabBase) {
            FabricRegistryUtilities.registerCreativeModeTab(((ItemWithCreativeTabBase)itemObject).creativeModeTab.get(), itemObject);
        } else if (itemObject instanceof ItemWithCreativeTabBase.ItemPlaceOnWater) {
            FabricRegistryUtilities.registerCreativeModeTab(((ItemWithCreativeTabBase.ItemPlaceOnWater)itemObject).creativeModeTab.get(), itemObject);
        }
    }

    private static void registerBlock(String path, RegistryObject<Block> block) {
        Registry.register(RegistryUtilities.registryGetBlock(), new ResourceLocation(MOD_ID, path), block.get());
    }

    private static void registerBlock(String path, RegistryObject<Block> block, CreativeModeTabs.Wrapper creativeModeTab) {
        registerBlock(path, block);
        final BlockItem blockItem = new BlockItem(block.get(), RegistryUtilities.createItemProperties(creativeModeTab::get));
        Registry.register(RegistryUtilities.registryGetItem(), new ResourceLocation(MOD_ID, path), blockItem);
        FabricRegistryUtilities.registerCreativeModeTab(creativeModeTab.get(), blockItem);
    }

    public static ServerLevel overworld;
    public static ServerLevel the_nether;
    public static ServerLevel the_end;
}
