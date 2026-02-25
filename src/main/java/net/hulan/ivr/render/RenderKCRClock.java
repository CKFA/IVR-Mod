package net.hulan.ivr.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mtr.block.IBlock;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.UtilitiesClient;
import mtr.render.MoreRenderLayers;
import net.hulan.ivr.block.BlockKCRClock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RenderKCRClock extends BlockEntityRendererMapper<BlockKCRClock.TileEntityKCRClock> implements IGui, IBlock {
    
    public RenderKCRClock(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(BlockKCRClock.TileEntityKCRClock entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        Level world = entity.getLevel();
        if (world != null) {
            BlockPos pos = entity.getBlockPos();
            BlockState state = world.getBlockState(pos);
            boolean rotated = IBlock.getStatePropertySafe(state, BlockKCRClock.FACING);
            matrices.pushPose();
            matrices.translate(0.5D, 0.3125D, 0.5D);
            if (rotated) {
                UtilitiesClient.rotateYDegrees(matrices, 90.0F);
            }
            long time = world.getDayTime() + 6000L;
            drawHand(matrices, vertexConsumers, (float)time * 360.0F / 12000.0F, true);
            drawHand(matrices, vertexConsumers, (float)time * 360.0F / 1000.0F, false);
            UtilitiesClient.rotateYDegrees(matrices, 180.0F);
            drawHand(matrices, vertexConsumers, (float)time * 360.0F / 12000.0F, true);
            drawHand(matrices, vertexConsumers, (float)time * 360.0F / 1000.0F, false);
            matrices.popPose();
        }
    }

    private static void drawHand(PoseStack matrices, MultiBufferSource vertexConsumers, float rotation, boolean isHourHand) {
        matrices.pushPose();
        UtilitiesClient.rotateZDegrees(matrices, -rotation);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(MoreRenderLayers.getLight(new ResourceLocation("mtr:textures/block/white.png"), false));
        IDrawing.drawTexture(matrices, vertexConsumer, -0.01F, isHourHand ? 0.15F : 0.24F, isHourHand ? 0.1F : 0.105F, 0.01F, -0.03F, isHourHand ? 0.1F : 0.105F, Direction.UP, -5592406, 15728816);
        matrices.popPose();
    }
}
