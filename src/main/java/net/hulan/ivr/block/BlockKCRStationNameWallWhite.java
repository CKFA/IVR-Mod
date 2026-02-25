package net.hulan.ivr.block;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockKCRStationNameWallWhite extends BlockKCRStationNameWallBase {

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new BlockKCRStationNameWallWhite.TileEntityKCRStationNameWallWhite(pos, state);
    }

    public static class TileEntityKCRStationNameWallWhite extends TileEntityKCRStationNameWallBase {
        
        public TileEntityKCRStationNameWallWhite(BlockPos pos, BlockState state) {
            super(KCR_STATION_NAME_WALL_WHITE_TILE_ENTITY.get(), pos, state);
        }

        @Override
        public int getColor(BlockState state) {
            return -1;
        }
    }
}
