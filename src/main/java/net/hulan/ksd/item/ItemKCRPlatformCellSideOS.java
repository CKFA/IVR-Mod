package net.hulan.ksd.item;

import mtr.block.IBlock;
import mtr.item.ItemWithCreativeTabBase;
import net.hulan.ksd.KSDBlocks;
import net.hulan.ksd.KSDCreativeModTabs;
import net.hulan.ksd.block.BlockKCRPlatformCellSide;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemKCRPlatformCellSideOS extends ItemWithCreativeTabBase implements IBlock, KSDBlocks {

    public ItemKCRPlatformCellSideOS() {
        super(KSDCreativeModTabs.KCR_PLATFORM_BLOCKS);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        Direction facing = context.getHorizontalDirection();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        BlockPlaceContext _this = new BlockPlaceContext(context);
        BlockPlaceContext _next = BlockPlaceContext.at(_this, pos.relative(facing), context.getClickedFace());
        if (IBlock.isReplaceable(_this, Direction.UP, 2) && IBlock.isReplaceable(_this, facing, 2) && IBlock.isReplaceable(_next, Direction.UP, 2)) {
            BlockPos newPos = pos.relative(facing);
            world.setBlock(pos, KP_CELL_SIDE.get().defaultBlockState().setValue(BlockKCRPlatformCellSide.FACING, facing).setValue(BlockKCRPlatformCellSide.STYLE, BlockKCRPlatformCellSide.Style.SIDE_1_OS), 3);
            world.setBlock(pos.above(), KP_CELL_SIDE.get().defaultBlockState().setValue(BlockKCRPlatformCellSide.FACING, facing).setValue(BlockKCRPlatformCellSide.STYLE, BlockKCRPlatformCellSide.Style.SIDE_3_OS), 3);
            world.setBlock(newPos, KP_CELL_SIDE.get().defaultBlockState().setValue(BlockKCRPlatformCellSide.FACING, facing).setValue(BlockKCRPlatformCellSide.STYLE, BlockKCRPlatformCellSide.Style.SIDE_2_OS), 3);
            world.setBlock(newPos.above(), KP_CELL_SIDE.get().defaultBlockState().setValue(BlockKCRPlatformCellSide.FACING, facing).setValue(BlockKCRPlatformCellSide.STYLE, BlockKCRPlatformCellSide.Style.SIDE_3_OS), 3);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
        }
    }
}
