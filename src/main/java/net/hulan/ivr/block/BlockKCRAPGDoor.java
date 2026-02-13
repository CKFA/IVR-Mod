package net.hulan.ivr.block;

import mtr.block.BlockPSDAPGDoorBase;
import mtr.mappings.BlockEntityMapper;
import net.hulan.ivr.IVRBlockEntityTypes;
import net.hulan.ivr.IVRItems;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

public class BlockKCRAPGDoor extends BlockPSDAPGDoorBase {

    public BlockKCRAPGDoor() {
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new BlockKCRAPGDoor.TileEntityKCRAPGDoor(pos, state);
    }

    @Override
    public Item asItem() {
        return IVRItems.KCR_APG_DOOR.get();
    }

    public static class TileEntityKCRAPGDoor extends BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase {
        public TileEntityKCRAPGDoor(BlockPos pos, BlockState state) {
            super(IVRBlockEntityTypes.KCR_APG_DOOR_TILE_ENTITY.get(), pos, state);
        }
    }
}
