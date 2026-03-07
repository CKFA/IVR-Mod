package net.hulan.ivr.block;

import mtr.block.BlockStationColorPole;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BlockKCRStationNameTallBlock extends BlockKCRStationNameTallBase {

    public BlockKCRStationNameTallBlock() {
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        Tuple<Integer, Integer> bounds = getBounds(state);
        return Shapes.or(IBlock.getVoxelShapeByDirection(2.0F, (double) bounds.getA(), 5.0F, 14.0F, (double) bounds.getB(), 11.0F, IBlock.getStatePropertySafe(state, FACING)), BlockStationColorPole.getStationPoleShape());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return IBlock.isReplaceable(ctx, Direction.UP, 3) ? defaultBlockState().setValue(FACING, ctx.getHorizontalDirection()).setValue(METAL, true).setValue(THIRD, EnumThird.LOWER) : null;
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityKCRStationNameTallBlock(pos, state);
    }

    public static class TileEntityKCRStationNameTallBlock extends TileEntityKCRStationNameTallBase {

        public TileEntityKCRStationNameTallBlock(BlockPos pos, BlockState state) {
            super(KCR_STATION_NAME_TALL_BLOCK_TILE_ENTITY.get(), pos, state, 0.6875F, false);
        }
    }
}
