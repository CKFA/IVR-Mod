package net.hulan.ivr.render;

import mtr.block.IBlock;
import mtr.client.IDrawing;
import mtr.render.RenderTrains;
import mtr.render.StoredMatrixTransformations;
import net.hulan.ivr.block.BlockKCRAPGGlass;
import net.hulan.ivr.client.IVRClientData;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class RenderKCRAPGGlass extends RenderKCRRouteBase<BlockKCRAPGGlass.TileEntityKCRAPGGlass> {

    private static final float COLOR_STRIP_START = 0.75F;
    private static final float COLOR_STRIP_END = 0.78125F;

    public RenderKCRAPGGlass(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher, 4.0F, 8.0F, 4.0F, 8.0F, false, BlockKCRAPGGlass.ARROW_DIRECTION);
    }

    @Override
    protected RenderType getRenderType(BlockGetter world, BlockPos pos, BlockState state) {
        if (IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.LOWER) {
            return RenderType.NONE;
        } else {
            return Math.floorMod(pos.getX(), 8) < 4 == Math.floorMod(pos.getZ(), 8) < 4 ? RenderType.ARROW : RenderType.ROUTE;
        }
    }

    @Override
    protected void renderAdditional(StoredMatrixTransformations storedMatrixTransformations, long platformId, BlockState state, int leftBlocks, int rightBlocks, Direction facing, int color, int light) {
        if (IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.UPPER && IBlock.getStatePropertySafe(state, SIDE_EXTENDED) != EnumSide.SINGLE) {
            boolean isLeft = this.isLeft(state);
            boolean isRight = this.isRight(state);
            RenderTrains.scheduleRender(IVRClientData.DATA_CACHE.getColorStrip(platformId).resourceLocation, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
                storedMatrixTransformations.transform(matrices);
                IDrawing.drawTexture(matrices, vertexConsumer, isLeft ? this.sidePadding : 0.0F, 0.75F, 0.0F, isRight ? 1.0F - this.sidePadding : 1.0F, 0.78125F, 0.0F, facing, color, light);
                IDrawing.drawTexture(matrices, vertexConsumer, isRight ? 1.0F - this.sidePadding : 1.0F, 0.75F, 0.125F, isLeft ? this.sidePadding : 0.0F, 0.78125F, 0.125F, facing, color, light);
                matrices.popPose();
            });
            float width = (float)(leftBlocks + rightBlocks + 1) - this.sidePadding * 2.0F;
            float height = 1.0F - this.topPadding - this.bottomPadding;
            RenderTrains.scheduleRender(IVRClientData.DATA_CACHE.getSingleRowStationName(platformId, width / height).resourceLocation, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
                storedMatrixTransformations.transform(matrices);
                IDrawing.drawTexture(matrices, vertexConsumer, 1.0F - (rightBlocks == 0 ? this.sidePadding : 0.0F), this.topPadding, 0.125F, leftBlocks == 0 ? this.sidePadding : 0.0F, 1.0F - this.bottomPadding, 0.125F, ((float)rightBlocks - (rightBlocks == 0 ? 0.0F : this.sidePadding)) / width, 0.0F, (width - (float)leftBlocks + (leftBlocks == 0 ? 0.0F : this.sidePadding)) / width, 1.0F, facing, color, light);
                matrices.popPose();
            });
        }
    }
}
