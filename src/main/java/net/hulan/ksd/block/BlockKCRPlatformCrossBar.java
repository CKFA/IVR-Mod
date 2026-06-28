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

public class BlockKCRPlatformCrossBar extends BlockDirectionalMapper {

    private final Style type;

    public BlockKCRPlatformCrossBar(Style type) {
        super(Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(1));
        this.type = type;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        Direction direction = IBlock.getStatePropertySafe(state, FACING);
        VoxelShape hb = IBlock.getVoxelShapeByDirection(0, 8, 4, 16, 16, 12, direction);
        VoxelShape hb_l = IBlock.getVoxelShapeByDirection(0, 0, 4, 16, 8, 12, direction.getClockWise());
        VoxelShape hb_ending = IBlock.getVoxelShapeByDirection(-14, 8, 4, 16, 16, 12, direction);
        VoxelShape lb = IBlock.getVoxelShapeByDirection(5.9, -0.5, 0, 10.1, 8, 16, direction);
        switch (type) {
            case HORIZONTAL -> {
                return hb;
            }
            case HORIZONTAL_LOWER -> {
                return hb_l;
            }
            case HORIZONTAL_ENDING -> {
                return hb_ending;
            }
            case LONGITUDINAL -> {
                return lb;
            }
            case HB_WITH_LB -> {
                return Shapes.or(hb, lb);
            }
            case HB_WITH_HB -> {
                return Shapes.or(hb, hb_l);
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

    public enum Style {

        HORIZONTAL,
        HORIZONTAL_LOWER,
        HORIZONTAL_ENDING,
        LONGITUDINAL,
        HB_WITH_LB,
        HB_WITH_HB
    }
}
