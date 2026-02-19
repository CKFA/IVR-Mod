package net.hulan.ivr.render;

import mtr.block.BlockStationNameBase;
import mtr.block.IBlock;
import mtr.client.CustomResources;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.UtilitiesClient;
import mtr.render.RenderTrains;
import mtr.render.StoredMatrixTransformations;
import net.hulan.ivr.block.BlockClassicalSign;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import static net.hulan.ivr.render.RenderClassicalSign.getSign;
import static net.hulan.ivr.render.RenderClassicalSign.drawSign;

public class RenderClassicalSign1Odd<T extends BlockClassicalSign.TileEntityClassicalSign1Odd> extends BlockEntityRendererMapper<T> implements IBlock, IGui, IDrawing {

    public RenderClassicalSign1Odd(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        final BlockView world = entity.getWorld();
        if (world == null) {
            return;
        }

        final BlockPos pos = entity.getPos();
        final BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof final BlockClassicalSign block)) {
            return;
        }
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStationNameBase.FACING);
        final String signId1 = entity.getSignId1();

        boolean renderBackground1 = false;
        int backgroundColor1 = 0;
        if (signId1 != null) {
            final CustomResources.CustomSign sign = getSign(signId1);
            if (sign != null) {
                renderBackground1 = true;
                if (sign.backgroundColor != 0) {
                    backgroundColor1 = sign.backgroundColor;
                }
            }
        }

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations();
        storedMatrixTransformations.add(matricesNew -> {
            matricesNew.translate(0.5 + entity.getPos().getX(), 0.53125 + entity.getPos().getY(), 0.5 + entity.getPos().getZ());
            UtilitiesClient.rotateYDegrees(matricesNew, -facing.asRotation());
            UtilitiesClient.rotateZDegrees(matricesNew, 180);
            matricesNew.translate(block.getXStart() / 16F - 0.5, 0, -0.0625 - SMALL_OFFSET * 2 - 0.1875D);
        });

        matrices.push();
        matrices.translate(0.5, 0.53125, 0.5);
        UtilitiesClient.rotateYDegrees(matrices, -facing.asRotation());
        UtilitiesClient.rotateZDegrees(matrices, 180);
        matrices.translate(block.getXStart() / 16F - 0.5, 0, -0.0625 - SMALL_OFFSET * 2);

        if (renderBackground1) {
            final int newBackgroundColor = backgroundColor1 | ARGB_BLACK;
            RenderTrains.scheduleRender(new Identifier("mtr:textures/block/white.png"), false, RenderTrains.QueuedRenderLayer.INTERIOR, (matricesNew, vertexConsumer) -> {
                storedMatrixTransformations.transform(matricesNew);
                IDrawing.drawTexture(matricesNew, vertexConsumer, 0, 0, SMALL_OFFSET, 0.5F, 0.5F, SMALL_OFFSET, facing, newBackgroundColor, MAX_LIGHT_GLOWING);
                matricesNew.pop();
            });
        }
        drawSign(matrices,
                vertexConsumers,
                storedMatrixTransformations,
                MinecraftClient.getInstance().textRenderer,
                pos,
                signId1,
                0,
                0,
                0.5F,
                0,
                0,
                entity.getSelectedIds1(),
                facing,
                backgroundColor1 | ARGB_BLACK,
                (textureId, x, y, size, flipTexture) -> RenderTrains.scheduleRender(new Identifier(textureId.toString()), true, RenderTrains.QueuedRenderLayer.LIGHT_TRANSLUCENT, (matricesNew, vertexConsumer) -> {
                    storedMatrixTransformations.transform(matricesNew);
                    IDrawing.drawTexture(matricesNew, vertexConsumer, x, y, size, size, flipTexture ? 1 : 0, 0, flipTexture ? 0 : 1, 1, facing, -1, MAX_LIGHT_GLOWING);
                    matricesNew.pop();
                }));
        matrices.pop();
    }

    @Override
    public boolean rendersOutsideBoundingBox(T blockEntity) {
        return true;
    }
}
