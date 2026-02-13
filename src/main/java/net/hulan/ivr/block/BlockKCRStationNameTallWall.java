package net.hulan.ivr.block;

import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BlockKCRStationNameTallWall extends BlockKCRStationNameTallBase {

    public BlockKCRStationNameTallWall() {
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView blockGetter, BlockPos pos, ShapeContext collisionContext) {
        Pair<Integer, Integer> bounds = getBounds(state);
        return IBlock.getVoxelShapeByDirection(2.0D, (double) bounds.getLeft(), 0.0D, 14.0D, (double) bounds.getRight(), 0.5D, IBlock.getStatePropertySafe(state, FACING));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction blockSide = ctx.getSide();
        Direction facing = blockSide != Direction.UP && blockSide != Direction.DOWN ? blockSide.getOpposite() : ctx.getPlayerFacing();
        return IBlock.isReplaceable(ctx, Direction.UP, 3) ? this.getDefaultState().with(FACING, facing).with(METAL, true).with(THIRD, EnumThird.LOWER) : null;
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new BlockKCRStationNameTallWall.TileEntityKCRStationNameTallWall(pos, state);
    }

    public static class TileEntityKCRStationNameTallWall extends BlockKCRStationNameTallBase.TileEntityKCRStationNameTallBase {

        public TileEntityKCRStationNameTallWall(BlockPos pos, BlockState state) {
            super(KCR_STATION_NAME_TALL_WALL_TILE_ENTITY.get(), pos, state, 0.03125F, false);
        }
    }
}
