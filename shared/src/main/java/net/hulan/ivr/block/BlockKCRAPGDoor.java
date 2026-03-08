package net.hulan.ivr.block;

import mtr.block.BlockPSDAPGDoorBase;
import mtr.mappings.BlockEntityMapper;
import net.hulan.ivr.IVRBlockEntityTypes;
import net.hulan.ivr.IVRItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockKCRAPGDoor extends BlockPSDAPGDoorBase {

    public BlockKCRAPGDoor() {
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityKCRAPGDoor(pos, state);
    }

    @Override
    public @NotNull Item asItem() {
        return IVRItems.KCR_APG_DOOR.get();
    }

    public static class TileEntityKCRAPGDoor extends TileEntityPSDAPGDoorBase {
        public TileEntityKCRAPGDoor(BlockPos pos, BlockState state) {
            super(IVRBlockEntityTypes.KCR_APG_DOOR_TILE_ENTITY.get(), pos, state);
        }
    }
}
