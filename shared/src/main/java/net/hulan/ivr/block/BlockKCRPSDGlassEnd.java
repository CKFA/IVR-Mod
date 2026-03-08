package net.hulan.ivr.block;

import mtr.block.BlockPSDAPGGlassEndBase;
import net.hulan.ivr.IVRItems;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockKCRPSDGlassEnd extends BlockPSDAPGGlassEndBase {

    private final int style;

    public BlockKCRPSDGlassEnd(int style) {
        this.style = style;
    }

    @Override
    public @NotNull Item asItem() {
        return style == 0 ? IVRItems.KCR_PSD_GLASS_END_1.get() : IVRItems.KCR_PSD_GLASS_END_2.get();
    }
}
