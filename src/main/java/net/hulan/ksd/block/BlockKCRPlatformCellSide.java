package net.hulan.ksd.block;

import mtr.block.IBlock;
import mtr.mappings.BlockDirectionalMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BlockKCRPlatformCellSide extends BlockDirectionalMapper implements IBlock {

    public static final EnumProperty<Style> STYLE = EnumProperty.create("style", Style.class);

    public BlockKCRPlatformCellSide() {
        super(Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(1));
    }

    public BlockKCRPlatformCellSide(Properties properties) {
        super(properties);
    }


    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        Direction direction = IBlock.getStatePropertySafe(state, FACING);
        Style style = IBlock.getStatePropertySafe(state, STYLE);
        VoxelShape is_1_2 = IBlock.getVoxelShapeByDirection(14, 0, 0, 16, 16, 16, direction);
        VoxelShape is_3 = IBlock.getVoxelShapeByDirection(14, 0, 0, 16, 8, 16, direction);
        VoxelShape os_side_1_2 = IBlock.getVoxelShapeByDirection(0, 0, 0, 2, 16, 16, direction);
        VoxelShape os_side_3 = IBlock.getVoxelShapeByDirection(0, 0, 0, 2, 8, 16, direction);
        VoxelShape os_top = IBlock.getVoxelShapeByDirection(0, 0, 0, 16, 4, 16, direction);
        VoxelShape os_light = IBlock.getVoxelShapeByDirection(3, 5, 0,7, 8, 16, direction);
        return switch (style) {
            case SIDE_1_IS, SIDE_2_IS -> is_1_2;
            case SIDE_3_IS -> is_3;
            case SIDE_1_OS, SIDE_2_OS -> os_side_1_2;
            case SIDE_1_OS_WITH_LIGHT, SIDE_2_OS_WITH_LIGHT -> Shapes.or(os_side_1_2, os_light);
            case SIDE_3_OS -> Shapes.or(os_side_3, os_top);
        };
    }

    @Override
    public @NotNull Item asItem() {
        return super.asItem();
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getLightBlock(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return 5;
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        BlockPos top = pos, side = pos, st = pos;
        Direction direction = IBlock.getStatePropertySafe(state, FACING);
        Style style = IBlock.getStatePropertySafe(state, STYLE);
        switch (style) {
            case SIDE_1_IS, SIDE_1_OS, SIDE_1_OS_WITH_LIGHT -> {
                top = pos.above();
                side = pos.relative(direction);
                st = side.above();
            }
            case SIDE_2_IS, SIDE_2_OS, SIDE_2_OS_WITH_LIGHT -> {
                top = pos.above();
                side = pos.relative(direction.getOpposite());
                st = side.above();
            }
            case SIDE_3_IS, SIDE_3_OS -> top = pos.below();
        }
        IBlock.onBreakCreative(world, player, top);
        IBlock.onBreakCreative(world, player, side);
        IBlock.onBreakCreative(world, player, st);
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, STYLE);
    }

    public enum Style implements StringRepresentable {

        SIDE_1_IS,
        SIDE_1_OS,
        SIDE_1_OS_WITH_LIGHT,
        SIDE_2_IS,
        SIDE_2_OS,
        SIDE_2_OS_WITH_LIGHT,
        SIDE_3_IS,
        SIDE_3_OS,
        ;

        @Override
        public @NotNull String getSerializedName() {
            return toString().toLowerCase();
        }
    }
}
