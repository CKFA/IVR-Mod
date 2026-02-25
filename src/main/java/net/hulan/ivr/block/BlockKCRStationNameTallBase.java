package net.hulan.ivr.block;

import mtr.block.IBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public abstract class BlockKCRStationNameTallBase extends BlockKCRStationNameBase implements IBlock {

    public static final BooleanProperty METAL = BooleanProperty.create("metal");

    public BlockKCRStationNameTallBase() {
        super();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return IBlock.checkHoldingBrush(world, player, () -> {
            boolean isWhite = IBlock.getStatePropertySafe(state, COLOR) == 0;
            int newColorProperty = isWhite ? 2 : 0;
            boolean newMetalProperty = isWhite == IBlock.getStatePropertySafe(state, METAL);
            updateProperties(world, pos, newMetalProperty, newColorProperty);
            switch (IBlock.getStatePropertySafe(state, THIRD)) {
                case MIDDLE -> {
                    updateProperties(world, pos.below(), newMetalProperty, newColorProperty);
                    updateProperties(world, pos.above(), newMetalProperty, newColorProperty);
                }
                case UPPER -> {
                    updateProperties(world, pos.below(), newMetalProperty, newColorProperty);
                    updateProperties(world, pos.below(2), newMetalProperty, newColorProperty);
                }
                case LOWER -> {
                    updateProperties(world, pos.above(), newMetalProperty, newColorProperty);
                    updateProperties(world, pos.above(2), newMetalProperty, newColorProperty);
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        return (direction == Direction.UP && IBlock.getStatePropertySafe(state, THIRD) != EnumThird.UPPER || direction == Direction.DOWN && IBlock.getStatePropertySafe(state, THIRD) != EnumThird.LOWER) && !newState.is(this) ? Blocks.AIR.defaultBlockState() : state;
    }

    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        switch (IBlock.getStatePropertySafe(state, THIRD)) {
            case MIDDLE -> IBlock.onBreakCreative(world, player, pos.below());
            case UPPER -> IBlock.onBreakCreative(world, player, pos.below(2));
        }
        super.playerWillDestroy(world, pos, state, player);
    }

    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClientSide) {
            Direction facing = IBlock.getStatePropertySafe(state, FACING);
            world.setBlock(pos.above(), defaultBlockState().setValue(FACING, facing).setValue(METAL, true).setValue(THIRD, EnumThird.MIDDLE), 3);
            world.setBlock(pos.above(2), defaultBlockState().setValue(FACING, facing).setValue(METAL, true).setValue(THIRD, EnumThird.UPPER), 3);
            world.updateNeighborsAt(pos, Blocks.AIR);
            state.updateNeighbourShapes(world, pos, 3);
        }

    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COLOR, FACING, METAL, THIRD);
    }

    protected static Tuple<Integer, Integer> getBounds(BlockState state) {
        EnumThird third = IBlock.getStatePropertySafe(state, THIRD);
        byte start;
        byte end;
        switch (third) {
            case UPPER -> {
                start = 0;
                end = 8;
            }
            case LOWER -> {
                start = 10;
                end = 16;
            }
            default -> {
                start = 0;
                end = 16;
            }
        }
        return new Tuple<>((int) start, (int) end);
    }

    private static void updateProperties(Level world, BlockPos pos, boolean metalProperty, int colorProperty) {
        world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(COLOR, colorProperty).setValue(METAL, metalProperty));
    }

    public static class TileEntityKCRStationNameTallBase extends TileEntityKCRStationNameBase {

        public TileEntityKCRStationNameTallBase(BlockEntityType<?> type, BlockPos pos, BlockState state, float zOffset, boolean isDoubleSided) {
            super(type, pos, state, 0.21875F, zOffset, isDoubleSided);
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
