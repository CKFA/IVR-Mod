package net.hulan.ivr.render;

import mtr.block.IBlock;
import mtr.client.IDrawing;
import mtr.render.RenderTrains;
import mtr.render.StoredMatrixTransformations;
import net.hulan.ivr.block.BlockKCRStationNameTallBase;
import net.hulan.ivr.client.IVRClientData;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class RenderKCRStationNameTall<T extends BlockKCRStationNameTallBase.TileEntityKCRStationNameTallBase> extends RenderKCRStationNameBase<T> {

    private static final float WIDTH = 0.6875F;
    private static final float HEIGHT = 1.5F;

    public RenderKCRStationNameTall(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    protected void drawStationName(BlockView world, BlockPos pos, BlockState state, Direction facing, StoredMatrixTransformations storedMatrixTransformations, VertexConsumerProvider vertexConsumers, String stationName, int stationColor, int color, int light) {
        if (IBlock.getStatePropertySafe(state, BlockKCRStationNameTallBase.THIRD) == IBlock.EnumThird.MIDDLE) {
            RenderTrains.scheduleRender(IVRClientData.DATA_CACHE.getStationName(stationName, 1).resourceLocation, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
                storedMatrixTransformations.transform(matrices);
                IDrawing.drawTexture(matrices, vertexConsumer, -0.5F, -0.5F, 1.0F, 1.0F, 0, 0.0F, 1, 1.0F, facing, color, light);
                matrices.pop();
            });
        }

    }
}
