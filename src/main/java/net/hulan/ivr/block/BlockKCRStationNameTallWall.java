package net.hulan.ivr.block;

import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BlockKCRStationNameTallWall extends BlockKCRStationNameTallBase {

    public BlockKCRStationNameTallWall() {
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        Tuple<Integer, Integer> bounds = getBounds(state);
        return IBlock.getVoxelShapeByDirection(2.0F, (double) bounds.getA(), 0.0F, 14.0F, (double) bounds.getB(), 0.5F, IBlock.getStatePropertySafe(state, FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction blockSide = ctx.getClickedFace();
        Direction facing = blockSide != Direction.UP && blockSide != Direction.DOWN ? blockSide.getOpposite() : ctx.getHorizontalDirection();
        return IBlock.isReplaceable(ctx, Direction.UP, 3) ? defaultBlockState().setValue(FACING, facing).setValue(METAL, true).setValue(THIRD, EnumThird.LOWER) : null;
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new BlockKCRStationNameTallWall.TileEntityKCRStationNameTallWall(pos, state);
    }

    public static class TileEntityKCRStationNameTallWall extends BlockKCRStationNameTallBase.TileEntityKCRStationNameTallBase {

        public TileEntityKCRStationNameTallWall(BlockPos pos, BlockState state) {
            super(KCR_STATION_NAME_TALL_WALL_TILE_ENTITY.get(), pos, state, 0.03125F, false);
        }
    }
}
