package net.hulan.ivr.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mtr.block.IBlock;
import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.data.Platform;
import mtr.data.RailwayData;
import mtr.data.Station;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.UtilitiesClient;
import mtr.render.MoreRenderLayers;
import mtr.render.RenderTrains;
import net.hulan.ivr.block.BlockKCRRouteSignBase;
import net.hulan.ivr.block.BlockKCRStationNameBase;
import net.hulan.ivr.client.IVRClientData;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Map;

public class RenderModernRouteSign<T extends BlockKCRRouteSignBase.TileEntityKCRRouteSignBase> extends BlockEntityRendererMapper<T> implements IBlock, IGui {

    public RenderModernRouteSign(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        BlockGetter world = entity.getLevel();
        if (world != null) {
            BlockPos pos = entity.getBlockPos();
            BlockState state = world.getBlockState(pos);
            Direction facing = IBlock.getStatePropertySafe(state, BlockKCRStationNameBase.FACING);
            if (!RenderTrains.shouldNotRender(pos, RenderTrains.maxTrainRenderDistance, facing)) {
                boolean isTop = IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.UPPER;
                int arrowDirection = IBlock.getStatePropertySafe(state, BlockKCRRouteSignBase.ARROW_DIRECTION);
                Station station = RailwayData.getStation(ClientData.STATIONS, ClientData.DATA_CACHE, pos);
                if (station != null) {
                    Map<Long, Platform> platformPositions = ClientData.DATA_CACHE.requestStationIdToPlatforms(station.id);
                    if (platformPositions != null && !platformPositions.isEmpty()) {
                        Platform platform = platformPositions.get(entity.getPlatformId());
                        if (platform != null) {
                            matrices.pushPose();
                            matrices.translate(0.5D, 0.0D, 0.5D);
                            UtilitiesClient.rotateYDegrees(matrices, -facing.toYRot());
                            matrices.translate(-0.5D, 0.0D, 0.43124999990686774D);
                            long platformId = platform.id;
                            VertexConsumer vertexConsumer1 = vertexConsumers.getBuffer(MoreRenderLayers.getExterior(IVRClientData.DATA_CACHE.getDirectionArrowForRS(
                                    platformId,
                                    (arrowDirection & 1) > 0,
                                    (arrowDirection & 2) > 0,
                                    HorizontalAlignment.CENTER,
                                    0.2F,
                                    4.4F,
                                    ARGB_BLACK,
                                    -1,
                                    0).resourceLocation));
                            IDrawing.drawTexture(matrices, vertexConsumer1, 0.84375F, 0.96875F + (float)(isTop ? 0 : 1), 0.0F, 0.15625F, 0.8125F + (float)(isTop ? 0 : 1), 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, facing.getOpposite(), -1, light);
                            VertexConsumer vertexConsumer2 = vertexConsumers.getBuffer(MoreRenderLayers.getExterior(IVRClientData.DATA_CACHE.getRouteMapForRS(
                                    platformId,
                                    true,
                                    false,
                                    1.6818181F,
                                    false).resourceLocation));
                            IDrawing.drawTexture(matrices, vertexConsumer2, 0.84375F, 0.8125F + (float)(isTop ? 0 : 1), 0.0F, 0.84375F, isTop ? 0.0F : 0.65625F, 0.0F, 0.15625F, isTop ? 0.0F : 0.65625F, 0.0F, 0.15625F, 0.8125F + (float)(isTop ? 0 : 1), 0.0F, 0.0F, 0.0F, isTop ? 0.7027027F : 1.0F, 1.0F, facing.getOpposite(), -1, light);
                            matrices.popPose();
                        }
                    }
                }
            }
        }
    }

    public boolean shouldRenderOffScreen(T blockEntity) {
        return true;
    }
}
