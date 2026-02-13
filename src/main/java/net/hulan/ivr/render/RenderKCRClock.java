package net.hulan.ivr.render;

import mtr.block.IBlock;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.UtilitiesClient;
import mtr.render.MoreRenderLayers;
import net.hulan.ivr.block.BlockKCRClock;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RenderKCRClock extends BlockEntityRendererMapper<BlockKCRClock.TileEntityKCRClock> implements IGui, IBlock {
    
    public RenderKCRClock(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(BlockKCRClock.TileEntityKCRClock entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        if (world != null) {
            BlockPos pos = entity.getPos();
            BlockState state = world.getBlockState(pos);
            boolean rotated = IBlock.getStatePropertySafe(state, BlockKCRClock.FACING);
            matrices.push();
            matrices.translate(0.5D, 0.3125D, 0.5D);
            if (rotated) {
                UtilitiesClient.rotateYDegrees(matrices, 90.0F);
            }
            long time = world.getTimeOfDay() + 6000L;
            drawHand(matrices, vertexConsumers, (float)time * 360.0F / 12000.0F, true);
            drawHand(matrices, vertexConsumers, (float)time * 360.0F / 1000.0F, false);
            UtilitiesClient.rotateYDegrees(matrices, 180.0F);
            drawHand(matrices, vertexConsumers, (float)time * 360.0F / 12000.0F, true);
            drawHand(matrices, vertexConsumers, (float)time * 360.0F / 1000.0F, false);
            matrices.pop();
        }
    }

    private static void drawHand(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float rotation, boolean isHourHand) {
        matrices.push();
        UtilitiesClient.rotateZDegrees(matrices, -rotation);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(MoreRenderLayers.getLight(new Identifier("mtr:textures/block/white.png"), false));
        IDrawing.drawTexture(matrices, vertexConsumer, -0.01F, isHourHand ? 0.15F : 0.24F, isHourHand ? 0.1F : 0.105F, 0.01F, -0.03F, isHourHand ? 0.1F : 0.105F, Direction.UP, -5592406, 15728816);
        matrices.pop();
    }
}
