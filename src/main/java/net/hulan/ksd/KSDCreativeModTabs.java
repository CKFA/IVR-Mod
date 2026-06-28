package net.hulan.ksd;

import mtr.CreativeModeTabs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static net.hulan.ksd.KSDMain.MOD_ID;

public interface KSDCreativeModTabs {

    CreativeModeTabs.Wrapper KCR_PLATFORM_BLOCKS = new CreativeModeTabs.Wrapper(new ResourceLocation(MOD_ID, "kcr_platform_blocks"), () -> new ItemStack(KSDBlocks.KP_POLE_2_SIDE.get()));
    CreativeModeTabs.Wrapper KCR_STATION_PIDS = new CreativeModeTabs.Wrapper(new ResourceLocation(MOD_ID, "kcr_station_pids"), () -> new ItemStack(KSDBlocks.KP_POLE_2_SIDE.get()));
}
