package net.hulan.ivr.block;

import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.BlockMapper;
import mtr.mappings.EntityBlockMapper;
import net.hulan.ivr.IVRBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BlockKCRClock extends BlockMapper implements EntityBlockMapper {

    public static final BooleanProperty FACING = BooleanProperty.create("facing");

    public BlockKCRClock() {
        super(Properties.of(Material.METAL, MaterialColor.QUARTZ).requiresCorrectToolForDrops().strength(2.0F).lightLevel((state) -> 5));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        boolean facing = ctx.getHorizontalDirection().getAxis() == Direction.Axis.X;
        return defaultBlockState().setValue(FACING, facing);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        Direction facing = IBlock.getStatePropertySafe(state, FACING) ? Direction.EAST : Direction.NORTH;
        return Shapes.or(IBlock.getVoxelShapeByDirection(3.0D, 0.0D, 6.0D, 13.0D, 12.0D, 10.0D, facing), Block.box(7.5D, 12.0D, 7.5D, 8.5D, 16.0D, 8.5D));
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityKCRClock(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public static class TileEntityKCRClock extends BlockEntityMapper {

        public TileEntityKCRClock(BlockPos pos, BlockState state) {
            super(IVRBlockEntityTypes.KCR_CLOCK_TILE_ENTITY.get(), pos, state);
        }
    }
}
