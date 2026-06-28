package net.hulan.ksd.mixin;

import mtr.data.SavedRailBase;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(SavedRailBase.class)
public interface SavedRailBaseAccessor {

    @Accessor(remap = false)
    Set<BlockPos> getPositions();
}
