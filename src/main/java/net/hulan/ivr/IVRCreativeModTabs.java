package net.hulan.ivr;

import mtr.CreativeModeTabs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static net.hulan.ivr.IVR.MOD_ID;

public interface IVRCreativeModTabs {

    CreativeModeTabs.Wrapper IVR_ = new CreativeModeTabs.Wrapper(new ResourceLocation(MOD_ID, "ivr"), () -> new ItemStack(IVRBlocks.IVR_LOGO.get()));
}
