package net.hulan.ivr.block;

import mtr.block.BlockPlatform;
import mtr.block.IBlock;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class BlockKCRPlatformIndented extends BlockPlatform {

    public BlockKCRPlatformIndented() {
        super(Settings.of(Material.METAL, MapColor.YELLOW).requiresTool().strength(2.0F).nonOpaque(), true);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        return this.getDefaultState().with(FACING, IBlock.getStatePropertySafe(state, FACING));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return VoxelShapes.union(IBlock.getVoxelShapeByDirection(0.0D, 0.0D, 6.0D, 16.0D, 13.0D, 16.0D, facing), Block.createCuboidShape(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D));
    }
}
