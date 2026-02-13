package net.hulan.ivr.block;

import mtr.block.BlockPSDAPGGlassEndBase;
import net.hulan.ivr.IVRItems;
import net.minecraft.item.Item;

public class BlockKCRAPGGlassEnd extends BlockPSDAPGGlassEndBase {

    public BlockKCRAPGGlassEnd() {
    }

    @Override
    public Item asItem() {
        return IVRItems.KCR_APG_GLASS_END.get();
    }

}
