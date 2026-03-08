package net.hulan.ivr.block;

import mtr.block.BlockPSDAPGGlassEndBase;
import net.hulan.ivr.IVRItems;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockKCRAPGGlassEnd extends BlockPSDAPGGlassEndBase {

    public BlockKCRAPGGlassEnd() {
    }

    @Override
    public @NotNull Item asItem() {
        return IVRItems.KCR_APG_GLASS_END.get();
    }
}
