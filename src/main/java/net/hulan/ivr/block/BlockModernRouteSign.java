package net.hulan.ivr.block;

import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BlockModernRouteSign extends BlockKCRRouteSignBase {

    public BlockModernRouteSign() {
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView blockGetter, BlockPos pos, ShapeContext collisionContext) {
        boolean isBottom = IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.LOWER;
        return IBlock.getVoxelShapeByDirection(2.0D, isBottom ? 10.0D : 0.0D, 0.0D, 14.0D, 16.0D, 1.0D, IBlock.getStatePropertySafe(state, FACING));
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new BlockModernRouteSign.TileEntityModernRouteSign(pos, state);
    }

    public static class TileEntityModernRouteSign extends TileEntityKCRRouteSignBase {
        public TileEntityModernRouteSign(BlockPos pos, BlockState state) {
            super(MODERN_ROUTE_SIGN_TILE_ENTITY.get(), pos, state);
        }
    }
}
