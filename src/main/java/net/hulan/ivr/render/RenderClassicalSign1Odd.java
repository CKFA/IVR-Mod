package net.hulan.ivr.render;

import com.mojang.blaze3d.vertex.PoseStack;
import mtr.block.BlockRailwaySign;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class RenderClassicalSign1Odd<T extends BlockClassicalSign.TileEntityClassicalSign1Odd> extends BlockEntityRendererMapper<T> implements IBlock, IGui, IDrawing {

    public RenderClassicalSign1Odd(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        renderSide(entity, matrices, vertexConsumers);
        renderOppositeSide(entity, matrices, vertexConsumers);
    }

    private void renderSide(T entity, PoseStack matrices, MultiBufferSource vertexConsumers) {
        final BlockGetter world = entity.getLevel();
        if (world == null) {
            return;
        }
        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof final BlockClassicalSign block)) {
            return;
        }
        if (entity.getSignId1().length != block.length || entity.getSignId2().length != block.length) {
            return;
        }
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStationNameBase.FACING);
        final String[] signId1 = entity.getSignId1();
        boolean renderBackground = false;
        int backgroundColor = 0;
        for (final String signId : signId1) {
            if (signId != null) {
                final CustomResources.CustomSign sign = getSign(signId);
                if (sign != null) {
                    renderBackground = true;
                    if (sign.backgroundColor != 0) {
                        backgroundColor = sign.backgroundColor;
                        break;
                    }
                }
            }
        }
        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations();
        storedMatrixTransformations.add(matricesNew -> {
            matricesNew.translate(0.5 + entity.getBlockPos().getX(), 0.53125 + entity.getBlockPos().getY(), 0.5 + entity.getBlockPos().getZ());
            UtilitiesClient.rotateYDegrees(matricesNew, -facing.toYRot());
            UtilitiesClient.rotateZDegrees(matricesNew, 180);
            matricesNew.translate(block.getXStart() / 16F - 0.5, 0, -0.0625 - SMALL_OFFSET * 2 - 0.125D);
        });
        matrices.pushPose();
        matrices.translate(0.5, 0.53125, 0.5);
        UtilitiesClient.rotateYDegrees(matrices, -facing.toYRot());
        UtilitiesClient.rotateZDegrees(matrices, 180);
        matrices.translate(block.getXStart() / 16F - 0.5, 0, -0.0625 - SMALL_OFFSET * 2);
        if (renderBackground) {
            final int newBackgroundColor = backgroundColor | ARGB_BLACK;
            RenderTrains.scheduleRender(new ResourceLocation("mtr:textures/block/white.png"), false, RenderTrains.QueuedRenderLayer.INTERIOR, (matricesNew, vertexConsumer) -> {
                storedMatrixTransformations.transform(matricesNew);
                IDrawing.drawTexture(matricesNew, vertexConsumer, 0, 0, SMALL_OFFSET, 0.5F * (signId1.length), 0.5F, SMALL_OFFSET, facing, newBackgroundColor, MAX_LIGHT_GLOWING);
                matricesNew.popPose();
            });
        }
        for (int i = 0; i < signId1.length; i++) {
            if (signId1[i] != null) {
                RenderClassicalSign.drawSign(matrices,
                        vertexConsumers,
                        storedMatrixTransformations,
                        Minecraft.getInstance().font,
                        pos,
                        signId1[i],
                        0.5F * i,
                        0,
                        0.5F,
                        getMaxWidth(signId1, i, false),
                        getMaxWidth(signId1, i, true),
                        entity.getSelectedIds1(),
                        facing,
                        backgroundColor | ARGB_BLACK,
                        (textureId, x, y, size, flipTexture) -> RenderTrains.scheduleRender(new ResourceLocation(textureId.toString()), true, RenderTrains.QueuedRenderLayer.LIGHT_TRANSLUCENT, (matricesNew, vertexConsumer) -> {
                            storedMatrixTransformations.transform(matricesNew);
                            IDrawing.drawTexture(matricesNew, vertexConsumer, x, y, size, size, flipTexture ? 1 : 0, 0, flipTexture ? 0 : 1, 1, facing, -1, MAX_LIGHT_GLOWING);
                            matricesNew.popPose();
                        }));
            }
        }
        matrices.popPose();
    }

    private void renderOppositeSide(T entity, PoseStack matrices, MultiBufferSource vertexConsumers) {
        final BlockGetter world = entity.getLevel();
        if (world == null) {
            return;
        }
        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof final BlockClassicalSign block)) {
            return;
        }
        if (entity.getSignId2().length != block.length || entity.getSignId2().length != block.length) {
            return;
        }
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStationNameBase.FACING).getOpposite();
        final String[] SignId2 = entity.getSignId2();
        boolean renderBackground = false;
        int backgroundColor = 0;
        for (final String signId : SignId2) {
            if (signId != null) {
                final CustomResources.CustomSign sign = getSign(signId);
                if (sign != null) {
                    renderBackground = true;
                    if (sign.backgroundColor != 0) {
                        backgroundColor = sign.backgroundColor;
                        break;
                    }
                }
            }
        }
        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations();
        storedMatrixTransformations.add(matricesNew -> {
            matricesNew.translate(0.5 + entity.getBlockPos().getX(), 0.53125 + entity.getBlockPos().getY(), 0.5 + entity.getBlockPos().getZ());
            UtilitiesClient.rotateYDegrees(matricesNew, -facing.toYRot());
            UtilitiesClient.rotateZDegrees(matricesNew, 180);
            matricesNew.translate(block.getXStart() / 16F - 0.5, 0, -0.0625 - SMALL_OFFSET * 2 - 0.125D);
        });
        matrices.pushPose();
        matrices.translate(0.5, 0.53125, 0.5);
        UtilitiesClient.rotateYDegrees(matrices, -facing.toYRot());
        UtilitiesClient.rotateZDegrees(matrices, 180);
        matrices.translate(block.getXStart() / 16F - 0.5, 0, -0.0625 - SMALL_OFFSET * 2);
        if (renderBackground) {
            final int newBackgroundColor = backgroundColor | ARGB_BLACK;
            RenderTrains.scheduleRender(new ResourceLocation("mtr:textures/block/white.png"), false, RenderTrains.QueuedRenderLayer.INTERIOR, (matricesNew, vertexConsumer) -> {
                storedMatrixTransformations.transform(matricesNew);
                IDrawing.drawTexture(matricesNew, vertexConsumer, 0, 0, SMALL_OFFSET, 0.5F * (SignId2.length), 0.5F, SMALL_OFFSET, facing, newBackgroundColor, MAX_LIGHT_GLOWING);
                matricesNew.popPose();
            });
        }
        for (int i = 0; i < SignId2.length; i++) {
            if (SignId2[i] != null) {
                RenderClassicalSign.drawSign(matrices,
                        vertexConsumers,
                        storedMatrixTransformations,
                        Minecraft.getInstance().font,
                        pos,
                        SignId2[i],
                        0.5F * i,
                        0,
                        0.5F,
                        getMaxWidth(SignId2, i, false),
                        getMaxWidth(SignId2, i, true),
                        entity.getSelectedIds2(),
                        facing,
                        backgroundColor | ARGB_BLACK,
                        (textureId, x, y, size, flipTexture) -> RenderTrains.scheduleRender(new ResourceLocation(textureId.toString()), true, RenderTrains.QueuedRenderLayer.LIGHT_TRANSLUCENT, (matricesNew, vertexConsumer) -> {
                            storedMatrixTransformations.transform(matricesNew);
                            IDrawing.drawTexture(matricesNew, vertexConsumer, x, y, size, size, flipTexture ? 1 : 0, 0, flipTexture ? 0 : 1, 1, facing, -1, MAX_LIGHT_GLOWING);
                            matricesNew.popPose();
                        }));
            }
        }
        matrices.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(T blockEntity) {
        return true;
    }

    public static CustomResources.CustomSign getSign(String signId) {
        try {
            final BlockRailwaySign.SignType sign = BlockRailwaySign.SignType.valueOf(signId);
            return new CustomResources.CustomSign(sign.textureId, sign.flipTexture, sign.customText, sign.flipCustomText, sign.small, sign.backgroundColor);
        } catch (Exception ignored) {
            return signId == null ? null : CustomResources.CUSTOM_SIGNS.get(signId);
        }
    }

    public static float getMaxWidth(String[] signIds, int index, boolean right) {
        float maxWidthLeft = 0;
        for (int i = index + (right ? 1 : -1); right ? i < signIds.length : i >= 0; i += (right ? 1 : -1)) {
            if (signIds[i] != null) {
                final CustomResources.CustomSign sign = RenderClassicalSign.getSign(signIds[i]);
                if (sign != null && sign.hasCustomText() && right == sign.flipCustomText) {
                    maxWidthLeft /= 2;
                }
                return maxWidthLeft;
            }
            maxWidthLeft++;
        }
        return maxWidthLeft;
    }
}
