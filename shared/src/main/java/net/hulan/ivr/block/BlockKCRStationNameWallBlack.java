package net.hulan.ivr.block;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockKCRStationNameWallBlack extends BlockKCRStationNameWallBase {

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityKCRStationNameWallBlack(pos, state);
    }

    public static class TileEntityKCRStationNameWallBlack extends TileEntityKCRStationNameWallBase {

        public TileEntityKCRStationNameWallBlack(BlockPos pos, BlockState state) {
            super(KCR_STATION_NAME_WALL_BLACK_TILE_ENTITY.get(), pos, state);
        }

        @Override
        public int getColor(BlockState state) {
            return -16777216;
        }
    }
}
