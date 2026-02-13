package net.hulan.ivr.block;

import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class BlockKCRStationNameEntrance extends BlockKCRStationNameBase {

    public static final IntProperty STYLE = IntProperty.of("propagate_property", 0, 5);

    public BlockKCRStationNameEntrance() {
        super();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand interactionHand, BlockHitResult blockHitResult) {
        return IBlock.checkHoldingBrush(world, player, () -> {
            world.setBlockState(pos, state.cycle(STYLE));
            this.propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).rotateYClockwise(), STYLE, 1);
            this.propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).rotateYCounterclockwise(), STYLE, 1);
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return world.getBlockState(pos.offset(facing)).getMaterial().isSolid();
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
        boolean tall = IBlock.getStatePropertySafe(state, STYLE) % 2 == 1;
        return IBlock.getVoxelShapeByDirection(0.0D, tall ? 0.0D : 4.0D, 0.0D, 16.0D, tall ? 16.0D : 12.0D, 1.0D, IBlock.getStatePropertySafe(state, FACING));
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockView blockGetter, BlockPos blockPos, ShapeContext collisionContext) {
        return VoxelShapes.empty();
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new BlockKCRStationNameEntrance.TileEntityKCRStationNameEntrance(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, STYLE);
    }

    public static class TileEntityKCRStationNameEntrance extends BlockKCRStationNameBase.TileEntityKCRStationNameBase {
        public TileEntityKCRStationNameEntrance(BlockPos pos, BlockState state) {
            super(KCR_STATION_NAME_ENTRANCE_TILE_ENTITY.get(), pos, state, 0.0F, 0.00625F, false);
        }

        @Override
        public int getColor(BlockState state) {
            return switch (IBlock.getStatePropertySafe(state, BlockKCRStationNameBase.COLOR)) {
                case 1 -> -5592406;
                case 2 -> -16777216;
                default -> -1;
            };
        }
    }
}
