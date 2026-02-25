package net.hulan.ivr.block;

import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BlockModernRouteSign extends BlockKCRRouteSignBase {

    public BlockModernRouteSign() {
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
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
