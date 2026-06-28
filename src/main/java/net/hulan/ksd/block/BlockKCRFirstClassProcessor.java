package net.hulan.ksd.block;

import mtr.block.IBlock;
import mtr.mappings.BlockDirectionalMapper;
import mtr.mappings.Utilities;
import net.hulan.ksd.data.FirstClassValidationSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockKCRFirstClassProcessor extends BlockDirectionalMapper {

    public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 3);

    public BlockKCRFirstClassProcessor() {
        super(Properties.of(Material.METAL, MaterialColor.GOLD).requiresCorrectToolForDrops().strength(1).lightLevel((state) -> 6));
        registerDefaultState(defaultBlockState().setValue(TYPE, 0));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(BlockState blockState, Level world, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!world.isClientSide) {
            FirstClassValidationSystem.FirstClassState firstClassState = FirstClassValidationSystem.validateOnMachine(blockPos, world, player);
            switch (firstClassState) {
                case ENABLED_ACCESS, ENABLED_ACCESS_CONCESSIONARY -> world.setBlockAndUpdate(blockPos, blockState.setValue(TYPE, 1));
                case NEGATIVE_AFTER_EXIT, NEGATIVE_AFTER_EXIT_CONCESSIONARY -> world.setBlockAndUpdate(blockPos, blockState.setValue(TYPE, 2));
                case DENIED -> world.setBlockAndUpdate(blockPos, blockState.setValue(TYPE, 3));
            }
            Utilities.scheduleBlockTick(world, blockPos, this, 20);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos) {
        world.setBlockAndUpdate(pos, state.setValue(TYPE, 0));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        Direction direction = IBlock.getStatePropertySafe(state, FACING);
        return IBlock.getVoxelShapeByDirection(5, 4, 0,11, 14, 1, direction);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
    }
}
