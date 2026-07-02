package net.hulan.ivr.block;

import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.Text;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockModernRouteSign extends BlockKCRRouteSignBase {

    public BlockModernRouteSign() {
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        boolean isBottom = IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.LOWER;
        return IBlock.getVoxelShapeByDirection(2.0D, isBottom ? 10.0D : 0.0D, 0.0D, 14.0D, 16.0D, 1.0D, IBlock.getStatePropertySafe(state, FACING));
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityModernRouteSign(pos, state);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.add(Text.literal("ModernRouteSign is developing,do not use it!!").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
        tooltip.add(Text.literal("九广铁路线路图（九广西铁+九广马鞍山铁路专用）处于开发状态，请不要使用它！！").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
    }

    public static class TileEntityModernRouteSign extends TileEntityKCRRouteSignBase {

        public TileEntityModernRouteSign(BlockPos pos, BlockState state) {
            super(MODERN_ROUTE_SIGN_TILE_ENTITY.get(), pos, state);
        }
    }
}
