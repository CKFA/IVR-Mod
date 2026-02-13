package net.hulan.ivr.render;

import mtr.block.IBlock;
import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.data.RailwayData;
import mtr.data.Station;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.Text;
import mtr.mappings.UtilitiesClient;
import mtr.render.RenderRouteBase;
import mtr.render.StoredMatrixTransformations;
import net.hulan.ivr.block.BlockKCRStationNameBase;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public abstract class RenderKCRStationNameBase<T extends BlockKCRStationNameBase.TileEntityKCRStationNameBase> extends BlockEntityRendererMapper<T> implements IGui, IDrawing {

    public RenderKCRStationNameBase(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockView world = entity.getWorld();
        if (world != null) {
            BlockPos pos = entity.getPos();
            BlockState state = world.getBlockState(pos);
            Direction facing = IBlock.getStatePropertySafe(state, BlockKCRStationNameBase.FACING);
            int color = RenderRouteBase.getShadingColor(facing, entity.getColor(state));
            StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations();
            storedMatrixTransformations.add((matricesNew) -> {
                matricesNew.translate(0.5D + (double)entity.getPos().getX(), 0.5D + (double)entity.yOffset + (double)entity.getPos().getY(), 0.5D + (double)entity.getPos().getZ());
                UtilitiesClient.rotateYDegrees(matricesNew, -facing.asRotation());
                UtilitiesClient.rotateZDegrees(matricesNew, 180.0F);
            });
            Station station = RailwayData.getStation(ClientData.STATIONS, ClientData.DATA_CACHE, pos);

            for(int i = 0; i < (entity.isDoubleSided ? 2 : 1); ++i) {
                StoredMatrixTransformations storedMatrixTransformations2 = storedMatrixTransformations.copy();
                boolean shouldFlip = i == 1;
                storedMatrixTransformations2.add((matricesNew) -> {
                    if (shouldFlip) {
                        UtilitiesClient.rotateYDegrees(matricesNew, 180.0F);
                    }

                    matricesNew.translate(0.0D, 0.0D, 0.5D - (double)entity.zOffset - 0.0031250000465661287D);
                });
                this.drawStationName(world, pos, state, facing, storedMatrixTransformations2, vertexConsumers, station == null ? Text.translatable("gui.mtr.untitled", new Object[0]).getString() : station.name, station == null ? 0 : station.color, color, light);
            }

        }
    }

    protected abstract void drawStationName(BlockView var1, BlockPos var2, BlockState var3, Direction var4, StoredMatrixTransformations var5, VertexConsumerProvider var6, String var7, int var8, int var9, int var10);
}
