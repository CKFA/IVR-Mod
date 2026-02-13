package net.hulan.ivr.block;

import mtr.block.IBlock;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class BlockKCRStationNameTallBase extends BlockKCRStationNameBase implements IBlock {

    public static final BooleanProperty METAL = BooleanProperty.of("metal");

    public BlockKCRStationNameTallBase() {
        super();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand interactionHand, BlockHitResult blockHitResult) {
        return IBlock.checkHoldingBrush(world, player, () -> {
            boolean isWhite = IBlock.getStatePropertySafe(state, COLOR) == 0;
            int newColorProperty = isWhite ? 2 : 0;
            boolean newMetalProperty = isWhite == IBlock.getStatePropertySafe(state, METAL);
            updateProperties(world, pos, newMetalProperty, newColorProperty);
            switch (IBlock.getStatePropertySafe(state, THIRD)) {
                case MIDDLE -> {
                    updateProperties(world, pos.down(), newMetalProperty, newColorProperty);
                    updateProperties(world, pos.up(), newMetalProperty, newColorProperty);
                }
                case UPPER -> {
                    updateProperties(world, pos.down(), newMetalProperty, newColorProperty);
                    updateProperties(world, pos.down(2), newMetalProperty, newColorProperty);
                }
                case LOWER -> {
                    updateProperties(world, pos.up(), newMetalProperty, newColorProperty);
                    updateProperties(world, pos.up(2), newMetalProperty, newColorProperty);
                }
            }

        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        return (direction == Direction.UP && IBlock.getStatePropertySafe(state, THIRD) != EnumThird.UPPER || direction == Direction.DOWN && IBlock.getStatePropertySafe(state, THIRD) != EnumThird.LOWER) && !newState.isOf(this) ? Blocks.AIR.getDefaultState() : state;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        switch (IBlock.getStatePropertySafe(state, THIRD)) {
            case MIDDLE -> IBlock.onBreakCreative(world, player, pos.down());
            case UPPER -> IBlock.onBreakCreative(world, player, pos.down(2));
        }

        super.onBreak(world, pos, state, player);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            Direction facing = IBlock.getStatePropertySafe(state, FACING);
            world.setBlockState(pos.up(), this.getDefaultState().with(FACING, facing).with(METAL, true).with(THIRD, EnumThird.MIDDLE), 3);
            world.setBlockState(pos.up(2), this.getDefaultState().with(FACING, facing).with(METAL, true).with(THIRD, EnumThird.UPPER), 3);
            world.updateNeighborsAlways(pos, Blocks.AIR);
            state.updateNeighbors(world, pos, 3);
        }

    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(COLOR, FACING, METAL, THIRD);
    }

    protected static Pair<Integer, Integer> getBounds(BlockState state) {
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
        return new Pair<>((int) start, (int) end);
    }

    private static void updateProperties(World world, BlockPos pos, boolean metalProperty, int colorProperty) {
        world.setBlockState(pos, world.getBlockState(pos).with(COLOR, colorProperty).with(METAL, metalProperty));
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
