package net.hulan.ivr.block;

import mtr.block.BlockStationColorPole;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class BlockKCRStationNameTallBlockDoubleSided extends BlockKCRStationNameTallBase {

    public BlockKCRStationNameTallBlockDoubleSided() {
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView blockGetter, BlockPos pos, ShapeContext collisionContext) {
        Pair<Integer, Integer> bounds = getBounds(state);
        return VoxelShapes.union(IBlock.getVoxelShapeByDirection(2.0D, (double) bounds.getLeft(), 5.0D, 14.0D, (double) bounds.getRight(), 11.0D, IBlock.getStatePropertySafe(state, FACING)), BlockStationColorPole.getStationPoleShape());
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return IBlock.isReplaceable(ctx, Direction.UP, 3) ? this.getDefaultState().with(FACING, ctx.getPlayerFacing()).with(METAL, true).with(THIRD, EnumThird.LOWER) : null;
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new BlockKCRStationNameTallBlockDoubleSided.TileEntityKCRStationNameTallBlockDoubleSided(pos, state);
    }

    public static class TileEntityKCRStationNameTallBlockDoubleSided extends BlockKCRStationNameTallBase.TileEntityKCRStationNameTallBase {

        public TileEntityKCRStationNameTallBlockDoubleSided(BlockPos pos, BlockState state) {
            super(KCR_STATION_NAME_TALL_BLOCK_DOUBLE_SIDED_TILE_ENTITY.get(), pos, state, 0.6875F, true);
        }
    }
}
