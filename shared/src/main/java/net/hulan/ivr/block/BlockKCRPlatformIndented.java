package net.hulan.ivr.block;

import mtr.block.BlockPlatform;
import mtr.block.IBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockKCRPlatformIndented extends BlockPlatform {

    public BlockKCRPlatformIndented() {
        super(Properties.of(Material.METAL, MaterialColor.COLOR_YELLOW).requiresCorrectToolForDrops().strength(2.0F).noOcclusion(), true);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        return defaultBlockState().setValue(FACING, IBlock.getStatePropertySafe(state, FACING));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return Shapes.or(IBlock.getVoxelShapeByDirection(0.0D, 0.0D, 6.0D, 16.0D, 13.0D, 16.0D, facing), Block.box(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D));
    }
}
