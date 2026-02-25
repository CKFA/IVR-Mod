package net.hulan.ivr.block;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockKCRStationNameWallGray extends BlockKCRStationNameWallBase {

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new BlockKCRStationNameWallGray.TileEntityKCRStationNameWallGray(pos, state);
    }

    public static class TileEntityKCRStationNameWallGray extends TileEntityKCRStationNameWallBase {
        
        public TileEntityKCRStationNameWallGray(BlockPos pos, BlockState state) {
            super(KCR_STATION_NAME_WALL_GRAY_TILE_ENTITY.get(), pos, state);
        }

        @Override
        public int getColor(BlockState state) {
            return -5592406;
        }
    }
}
