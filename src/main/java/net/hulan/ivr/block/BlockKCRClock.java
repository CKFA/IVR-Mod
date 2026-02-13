package net.hulan.ivr.block;

import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.BlockMapper;
import mtr.mappings.EntityBlockMapper;
import net.hulan.ivr.IVRBlockEntityTypes;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class BlockKCRClock extends BlockMapper implements EntityBlockMapper {

    public static final BooleanProperty FACING = BooleanProperty.of("facing");

    public BlockKCRClock() {
        super(Settings.of(Material.METAL, MapColor.OFF_WHITE).requiresTool().strength(2.0F).luminance((state) -> 5));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        boolean facing = ctx.getPlayerFacing().getAxis() == Direction.Axis.X;
        return this.getDefaultState().with(FACING, facing);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView blockGetter, BlockPos pos, ShapeContext collisionContext) {
        Direction facing = IBlock.getStatePropertySafe(state, FACING) ? Direction.EAST : Direction.NORTH;
        return VoxelShapes.union(IBlock.getVoxelShapeByDirection(3.0D, 0.0D, 6.0D, 13.0D, 12.0D, 10.0D, facing), Block.createCuboidShape(7.5D, 12.0D, 7.5D, 8.5D, 16.0D, 8.5D));
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityKCRClock(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public static class TileEntityKCRClock extends BlockEntityMapper {

        public TileEntityKCRClock(BlockPos pos, BlockState state) {
            super(IVRBlockEntityTypes.KCR_CLOCK_TILE_ENTITY.get(), pos, state);
        }
    }
}
