package net.hulan.ivr.render;

import mtr.block.BlockPSDAPGDoorBase;
import mtr.block.BlockPSDAPGGlassEndBase;
import mtr.block.IBlock;
import mtr.client.IDrawing;
import mtr.render.RenderTrains;
import mtr.render.StoredMatrixTransformations;
import net.hulan.ivr.block.BlockKCRPSDTop;
import net.hulan.ivr.client.IVRClientData;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RenderKCRPSDTop extends RenderKCRRouteBase<BlockKCRPSDTop.TileEntityKCRPSDTop> {

    private static final float END_FRONT_OFFSET;
    private static final float BOTTOM_DIAGONAL_OFFSET;
    private static final float ROOT_TWO_SCALED;
    private static final float BOTTOM_END_DIAGONAL_OFFSET;
    private static final float COLOR_STRIP_START = 0.90625F;
    private static final float COLOR_STRIP_END = 0.9375F;

    public RenderKCRPSDTop(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher, 1.95F, 7.5F, 1.5F, 0.125F, true, BlockKCRPSDTop.ARROW_DIRECTION);
    }

    @Override
    protected RenderType getRenderType(BlockGetter world, BlockPos pos, BlockState state) {
        BlockKCRPSDTop.EnumPersistent persistent = IBlock.getStatePropertySafe(state, BlockKCRPSDTop.PERSISTENT);
        if (persistent == BlockKCRPSDTop.EnumPersistent.NONE) {
            Block blockBelow = world.getBlockState(pos.below()).getBlock();
            if (blockBelow instanceof BlockPSDAPGDoorBase) {
                return RenderType.ARROW;
            } else {
                return !(blockBelow instanceof BlockPSDAPGGlassEndBase) ? RenderType.ROUTE : RenderType.NONE;
            }
        } else {
            return persistent == BlockKCRPSDTop.EnumPersistent.ARROW ? RenderType.ARROW : (persistent == BlockKCRPSDTop.EnumPersistent.ROUTE ? RenderType.ROUTE : RenderType.NONE);
        }
    }

    @Override
    protected void renderAdditionalUnmodified(StoredMatrixTransformations storedMatrixTransformations, BlockState state, Direction facing, int light) {
        boolean airLeft = IBlock.getStatePropertySafe(state, BlockKCRPSDTop.AIR_LEFT);
        boolean airRight = IBlock.getStatePropertySafe(state, BlockKCRPSDTop.AIR_RIGHT);
        boolean persistent = IBlock.getStatePropertySafe(state, BlockKCRPSDTop.PERSISTENT) != BlockKCRPSDTop.EnumPersistent.NONE;
        if ((airLeft || airRight) && !persistent) {
            RenderTrains.scheduleRender(new ResourceLocation("ivr:textures/block/kcr_psd_top.png"), false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
                storedMatrixTransformations.transform(matrices);
                if (airLeft) {
                    IDrawing.drawTexture(matrices, vertexConsumer, -0.125F, 0.0F, 0.5F, 0.5F, 0.0F, -0.125F, 0.5F, 1.0F, -0.125F, -0.125F, 1.0F, 0.5F, 0.0F, 0.0F, 1.0F, 1.0F, facing, -1, light);
                    IDrawing.drawTexture(matrices, vertexConsumer, 0.5F - END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, -0.25F - END_FRONT_OFFSET, 0.0625F, 0.25F - END_FRONT_OFFSET, -0.25F - END_FRONT_OFFSET, 1.0F, 0.25F - END_FRONT_OFFSET, 0.5F - END_FRONT_OFFSET, 1.0F, -0.5F - END_FRONT_OFFSET, 0.0F, 0.0F, 1.0F, 0.9375F, facing.getOpposite(), -1, light);
                    IDrawing.drawTexture(matrices, vertexConsumer, 0.5F - BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5F - BOTTOM_END_DIAGONAL_OFFSET, -0.25F - BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, 0.25F - BOTTOM_END_DIAGONAL_OFFSET, -0.25F - END_FRONT_OFFSET, 0.0625F, 0.25F - END_FRONT_OFFSET, 0.5F - END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, 0.0F, 0.9375F, 1.0F, 0.96875F, facing.getOpposite(), -1, light);
                    IDrawing.drawTexture(matrices, vertexConsumer, 0.5F, 0.0F, -0.5F, -0.25F, 0.0F, 0.25F, -0.25F - BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, 0.25F - BOTTOM_END_DIAGONAL_OFFSET, 0.5F - BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5F - BOTTOM_END_DIAGONAL_OFFSET, 0.0F, 0.96875F, 1.0F, 1.0F, facing.getOpposite(), -1, light);
                    IDrawing.drawTexture(matrices, vertexConsumer, 0.5F, 0.003125F, -0.125F, -0.125F, 0.003125F, 0.5F, -0.125F, 0.003125F, 0.125F, 0.5F, 0.003125F, -0.5F, 0.125F, 0.125F, 0.1875F, 0.1875F, facing, -1, light);
                    IDrawing.drawTexture(matrices, vertexConsumer, 0.5F, 0.996875F, -0.5F, -0.125F, 0.996875F, 0.125F, -0.125F, 0.996875F, 0.5F, 0.5F, 0.996875F, -0.125F, 0.125F, 0.125F, 0.1875F, 0.1875F, Direction.UP, -1, light);
                    IDrawing.drawTexture(matrices, vertexConsumer, 0.5F - END_FRONT_OFFSET, 0.996875F, -0.5F - END_FRONT_OFFSET, -0.125F - ROOT_TWO_SCALED, 0.996875F, 0.125F, -0.125F, 0.996875F, 0.125F, 0.5F, 0.996875F, -0.5F, 0.125F, 0.125F, 0.1875F, 0.1875F, Direction.UP, -1, light);
                    IDrawing.drawTexture(matrices, vertexConsumer, 0.5F, 0.0625F, -0.5F, 0.5F - END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, 0.5F - END_FRONT_OFFSET, 1.0F, -0.5F - END_FRONT_OFFSET, 0.5F, 1.0F, -0.5F, 0.9375F, 0.0F, 1.0F, 0.9375F, facing, -1, light);
                    IDrawing.drawTexture(matrices, vertexConsumer, 0.5F, 0.0F, -0.5F, 0.5F - BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5F - BOTTOM_END_DIAGONAL_OFFSET, 0.5F - END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, 0.5F, 0.0625F, -0.5F, 0.9375F, 0.9375F, 1.0F, 1.0F, facing, -1, light);
                }
                if (airRight) {
                    IDrawing.drawTexture(matrices, vertexConsumer, -0.5F, 0.0F, -0.125F, 0.125F, 0.0F, 0.5F, 0.125F, 1.0F, 0.5F, -0.5F, 1.0F, -0.125F, 0.0F, 0.0F, 1.0F, 1.0F, facing, -1, light);
                    IDrawing.drawTexture(matrices, vertexConsumer, 0.25F + END_FRONT_OFFSET, 0.0625F, 0.25F - END_FRONT_OFFSET, -0.5F + END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, -0.5F + END_FRONT_OFFSET, 1.0F, -0.5F - END_FRONT_OFFSET, 0.25F + END_FRONT_OFFSET, 1.0F, 0.25F - END_FRONT_OFFSET, 0.0F, 0.0F, 1.0F, 0.9375F, facing.getOpposite(), -1, light);
                    IDrawing.drawTexture(matrices, vertexConsumer, 0.25F + BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, 0.25F - BOTTOM_END_DIAGONAL_OFFSET, -0.5F + BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5F - BOTTOM_END_DIAGONAL_OFFSET, -0.5F + END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, 0.25F + END_FRONT_OFFSET, 0.0625F, 0.25F - END_FRONT_OFFSET, 0.0F, 0.9375F, 1.0F, 0.96875F, facing.getOpposite(), -1, light);
                    IDrawing.drawTexture(matrices, vertexConsumer, 0.25F, 0.0F, 0.25F, -0.5F, 0.0F, -0.5F, -0.5F + BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5F - BOTTOM_END_DIAGONAL_OFFSET, 0.25F + BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, 0.25F - BOTTOM_END_DIAGONAL_OFFSET, 0.0F, 0.96875F, 1.0F, 1.0F, facing.getOpposite(), -1, light);
                    IDrawing.drawTexture(matrices, vertexConsumer, 0.125F, 0.003125F, 0.5F, -0.5F, 0.003125F, -0.125F, -0.5F, 0.003125F, -0.5F, 0.125F, 0.003125F, 0.125F, 0.125F, 0.125F, 0.1875F, 0.1875F, facing, -1, light);
                    IDrawing.drawTexture(matrices, vertexConsumer, 0.125F, 0.996875F, 0.125F, -0.5F, 0.996875F, -0.5F, -0.5F, 0.996875F, -0.125F, 0.125F, 0.996875F, 0.5F, 0.125F, 0.125F, 0.1875F, 0.1875F, Direction.UP, -1, light);
                    IDrawing.drawTexture(matrices, vertexConsumer, 0.125F + ROOT_TWO_SCALED, 0.996875F, 0.125F, -0.5F + END_FRONT_OFFSET, 0.996875F, -0.5F - END_FRONT_OFFSET, -0.5F, 0.996875F, -0.5F, 0.125F, 0.996875F, 0.125F, 0.125F, 0.125F, 0.1875F, 0.1875F, Direction.UP, -1, light);
                    IDrawing.drawTexture(matrices, vertexConsumer, -0.5F + END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, -0.5F, 0.0625F, -0.5F, -0.5F, 1.0F, -0.5F, -0.5F + END_FRONT_OFFSET, 1.0F, -0.5F - END_FRONT_OFFSET, 0.0F, 0.0F, 0.0625F, 0.9375F, facing, -1, light);
                    IDrawing.drawTexture(matrices, vertexConsumer, -0.5F + BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5F - BOTTOM_END_DIAGONAL_OFFSET, -0.5F, 0.0F, -0.5F, -0.5F, 0.0625F, -0.5F, -0.5F + END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, 0.0F, 0.9375F, 0.0625F, 1.0F, facing, -1, light);
                }
                matrices.popPose();
            });
        }
    }

    @Override
    protected void renderAdditional(StoredMatrixTransformations storedMatrixTransformations, long platformId, BlockState state, int leftBlocks, int rightBlocks, Direction facing, int color, int light) {
        boolean isNotPersistent = IBlock.getStatePropertySafe(state, BlockKCRPSDTop.PERSISTENT) == BlockKCRPSDTop.EnumPersistent.NONE;
        boolean airLeft = isNotPersistent && IBlock.getStatePropertySafe(state, BlockKCRPSDTop.AIR_LEFT);
        boolean airRight = isNotPersistent && IBlock.getStatePropertySafe(state, BlockKCRPSDTop.AIR_RIGHT);
        RenderTrains.scheduleRender(IVRClientData.DATA_CACHE.getColorStrip(platformId).resourceLocation, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
            storedMatrixTransformations.transform(matrices);
            IDrawing.drawTexture(matrices, vertexConsumer, airLeft ? 0.625F : 0.0F, 0.90625F, 0.0F, airRight ? 0.375F : 1.0F, 0.9375F, 0.0F, facing, color, light);
            if (airLeft) {
                IDrawing.drawTexture(matrices, vertexConsumer, END_FRONT_OFFSET, 0.90625F, -0.625F - END_FRONT_OFFSET, 0.75F + END_FRONT_OFFSET, 0.9375F, 0.125F - END_FRONT_OFFSET, facing, -1, light);
            }
            if (airRight) {
                IDrawing.drawTexture(matrices, vertexConsumer, 0.25F - END_FRONT_OFFSET, 0.90625F, 0.125F - END_FRONT_OFFSET, 1.0F - END_FRONT_OFFSET, 0.9375F, -0.625F - END_FRONT_OFFSET, facing, -1, light);
            }
            matrices.popPose();
        });
    }

    @Override
    protected float getAdditionalOffset(BlockState state) {
        return IBlock.getStatePropertySafe(state, BlockKCRPSDTop.PERSISTENT) == BlockKCRPSDTop.EnumPersistent.NONE ? 0.0F : 0.46875F;
    }

    static {
        END_FRONT_OFFSET = 1.0F / (Mth.SQRT_OF_TWO * 16.0F);
        BOTTOM_DIAGONAL_OFFSET = ((float)Math.sqrt(3.0D) - 1.0F) / 32.0F;
        ROOT_TWO_SCALED = Mth.SQRT_OF_TWO / 16.0F;
        BOTTOM_END_DIAGONAL_OFFSET = END_FRONT_OFFSET - BOTTOM_DIAGONAL_OFFSET / Mth.SQRT_OF_TWO;
    }
}
