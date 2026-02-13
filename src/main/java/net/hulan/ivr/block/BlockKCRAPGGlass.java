package net.hulan.ivr.block;

import mtr.block.BlockPSDAPGGlassBase;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.EntityBlockMapper;
import net.hulan.ivr.IVRBlockEntityTypes;
import net.hulan.ivr.IVRItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockKCRAPGGlass extends BlockPSDAPGGlassBase implements EntityBlockMapper {
    public static final IntProperty ARROW_DIRECTION = IntProperty.of("propagate_property", 0, 3);

    public BlockKCRAPGGlass() {
    }

    @Override
    public Item asItem() {
        return IVRItems.KCR_APG_GLASS.get();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand interactionHand, BlockHitResult blockHitResult) {
        double y = blockHitResult.getPos().y;
        return IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.UPPER && y - Math.floor(y) > 0.21875D ? IBlock.checkHoldingBrush(world, player, () -> {
            world.setBlockState(pos, state.cycle(ARROW_DIRECTION));
            this.propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).rotateYClockwise(), ARROW_DIRECTION, 3);
            this.propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).rotateYCounterclockwise(), ARROW_DIRECTION, 3);
        }) : super.onUse(state, world, pos, player, interactionHand, blockHitResult);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new BlockKCRAPGGlass.TileEntityKCRAPGGlass(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, SIDE_EXTENDED, ARROW_DIRECTION);
    }

    public static class TileEntityKCRAPGGlass extends BlockKCRPSDTop.TileEntityKCRRouteBase {
        public TileEntityKCRAPGGlass(BlockPos pos, BlockState state) {
            super(IVRBlockEntityTypes.KCR_APG_GLASS_TILE_ENTITY.get(), pos, state);
        }
    }
}
