package net.hulan.ivr.block;

import mtr.block.IBlock;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class BlockKCRStationNameWallBase extends BlockKCRStationNameBase implements IBlock {

    public BlockKCRStationNameWallBase() {
        super();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return world.getBlockState(pos.offset(facing)).isSideSolidFullSquare(world, pos.offset(facing), facing.getOpposite());
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction side = ctx.getSide();
        return side != Direction.UP && side != Direction.DOWN ? this.getDefaultState().with(FACING, side.getOpposite()) : null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        return direction.getOpposite() == IBlock.getStatePropertySafe(state, FACING).getOpposite() && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : state;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView blockGetter, BlockPos pos, ShapeContext collisionContext) {
        return IBlock.getVoxelShapeByDirection(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D, IBlock.getStatePropertySafe(state, FACING));
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockView blockGetter, BlockPos blockPos, ShapeContext collisionContext) {
        return VoxelShapes.empty();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public abstract static class TileEntityKCRStationNameWallBase extends BlockKCRStationNameBase.TileEntityKCRStationNameBase {

        public TileEntityKCRStationNameWallBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state, 0.0F, 0.0F, false);
        }
    }
}
