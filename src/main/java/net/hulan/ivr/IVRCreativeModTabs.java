package net.hulan.ivr;

import mtr.CreativeModeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import static net.hulan.ivr.IVR.MOD_ID;

public interface IVRCreativeModTabs {

    CreativeModeTabs.Wrapper IVR_ = new CreativeModeTabs.Wrapper(new Identifier(MOD_ID, "ivr"), () -> new ItemStack(IVRBlocks.IVR_LOGO.get()));
}
