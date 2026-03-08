package net.hulan.ivr.block;

import mtr.block.BlockPSDAPGDoorBase;
import mtr.mappings.BlockEntityMapper;
import net.hulan.ivr.IVRBlockEntityTypes;
import net.hulan.ivr.IVRItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockKCRPSDDoor extends BlockPSDAPGDoorBase {

    private final int style;

    public BlockKCRPSDDoor(int style) {
        this.style = style;
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityKCRPSDDoor(style, pos, state);
    }

    @Override
    public @NotNull Item asItem() {
        return style == 0 ? IVRItems.KCR_PSD_DOOR_1.get() : IVRItems.KCR_PSD_DOOR_2.get();
    }

    public static class TileEntityKCRPSDDoor extends TileEntityPSDAPGDoorBase {
        public TileEntityKCRPSDDoor(int style, BlockPos pos, BlockState state) {
            super(style == 0 ? IVRBlockEntityTypes.KCR_PSD_DOOR_1_TILE_ENTITY.get() : IVRBlockEntityTypes.KCR_PSD_DOOR_2_TILE_ENTITY.get(), pos, state);
        }
    }
}
