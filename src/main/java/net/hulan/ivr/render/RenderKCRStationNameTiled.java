package net.hulan.ivr.render;

import mtr.block.IBlock;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.render.RenderTrains;
import mtr.render.StoredMatrixTransformations;
import net.hulan.ivr.block.BlockKCRStationNameBase;
import net.hulan.ivr.block.BlockKCRStationNameEntrance;
import net.hulan.ivr.client.IVRClientData;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RenderKCRStationNameTiled<T extends BlockKCRStationNameBase.TileEntityKCRStationNameBase> extends RenderKCRStationNameBase<T> {

    private final boolean showLogo;

    public RenderKCRStationNameTiled(BlockEntityRenderDispatcher dispatcher, boolean showLogo) {
        super(dispatcher);
        this.showLogo = showLogo;
    }

    @Override
    protected void drawStationName(BlockGetter world, BlockPos pos, BlockState state, Direction facing, StoredMatrixTransformations storedMatrixTransformations, MultiBufferSource vertexConsumers, String stationName, int stationColor, int color, int light) {
        int lengthLeft = this.getLength(world, pos, false);
        int lengthRight = this.getLength(world, pos, true);
        int totalLength = lengthLeft + lengthRight - 1;
        if (this.showLogo) {
            int propagateProperty = IBlock.getStatePropertySafe(world, pos, BlockKCRStationNameEntrance.STYLE);
            float logoSize = propagateProperty % 2 == 0 ? 0.5F : 1.0F;
            RenderTrains.scheduleRender(IVRClientData.DATA_CACHE.getStationNameEntrance(propagateProperty >= 2 && propagateProperty < 4 ? -16777216 : -1, IGui.insertTranslation("gui.mtr.station_cjk", "gui.mtr.station", 1, stationName), (float)totalLength / logoSize).resourceLocation, false, RenderTrains.QueuedRenderLayer.INTERIOR, (matrices, vertexConsumer) -> {
                storedMatrixTransformations.transform(matrices);
                IDrawing.drawTexture(matrices, vertexConsumer, -0.5F, -logoSize / 2.0F, 1.0F, logoSize, (float)(lengthLeft - 1) / (float)totalLength, 0.0F, (float)lengthLeft / (float)totalLength, 1.0F, facing, color, light);
                matrices.popPose();
            });
        } else {
            RenderTrains.scheduleRender(IVRClientData.DATA_CACHE.getStationName(stationName, (float)totalLength).resourceLocation, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
                storedMatrixTransformations.transform(matrices);
                IDrawing.drawTexture(matrices, vertexConsumer, -0.5F, -0.5F, 1.0F, 1.0F, (float)(lengthLeft - 1) / (float)totalLength, 0.0F, (float)lengthLeft / (float)totalLength, 1.0F, facing, color, light);
                matrices.popPose();
            });
        }
    }

    private int getLength(BlockGetter world, BlockPos pos, boolean lookRight) {
        if (world == null) {
            return 1;
        } else {
            Direction facing = IBlock.getStatePropertySafe(world, pos, BlockKCRStationNameBase.FACING);
            Block thisBlock = world.getBlockState(pos).getBlock();
            int length = 1;
            while(true) {
                Block checkBlock = world.getBlockState(pos.relative(lookRight ? facing.getClockWise() : facing.getCounterClockWise(), length)).getBlock();
                if (!(checkBlock instanceof BlockKCRStationNameBase) || checkBlock != thisBlock) {
                    return length;
                }
                ++length;
            }
        }
    }
}
