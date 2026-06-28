package net.hulan.ksd.block;

import mtr.block.IBlock;
import mtr.mappings.BlockDirectionalMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockKCRPlatformPole extends BlockDirectionalMapper {

    private final Style style;

    public enum Style {
        NORMAL,
        SINGLE_SIDE,
        DOUBLE_SIDE,
    }

    public BlockKCRPlatformPole(Style style) {
        super(Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(1));
        this.style = style;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        Direction direction = IBlock.getStatePropertySafe(state, FACING);
        VoxelShape main = IBlock.getVoxelShapeByDirection(5, 0, 4.25,11, 16, 11.75, direction);
        VoxelShape side = IBlock.getVoxelShapeByDirection(5, 11, 4.5,11, 24, 11.5, direction);
        VoxelShape side_mirror = IBlock.getVoxelShapeByDirection(5, 11, 4.5,11, 24, 11.5, direction.getOpposite());
        switch (style) {
            case NORMAL -> {
                return main;
            }
            case SINGLE_SIDE -> {
                return Shapes.or(main, side);
            }
            case DOUBLE_SIDE -> {
                return Shapes.or(main, side, side_mirror);
            }
            default -> {
                return super.getShape(state, blockGetter, pos, collisionContext);
            }
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
