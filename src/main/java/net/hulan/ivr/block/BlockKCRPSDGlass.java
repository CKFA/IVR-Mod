package net.hulan.ivr.block;

import mtr.block.BlockPSDAPGGlassBase;
import net.hulan.ivr.IVRItems;
import net.minecraft.item.Item;

public class BlockKCRPSDGlass extends BlockPSDAPGGlassBase {

    private final int style;

    public BlockKCRPSDGlass(int style) {
        this.style = style;
    }

    @Override
    public Item asItem() {
        return this.style == 0 ? IVRItems.KCR_PSD_GLASS_1.get() : IVRItems.KCR_PSD_GLASS_2.get();
    }

}
