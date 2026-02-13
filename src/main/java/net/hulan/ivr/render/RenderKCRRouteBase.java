package net.hulan.ivr.render;

import mtr.block.IBlock;
import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.UtilitiesClient;
import mtr.render.RenderTrains;
import mtr.render.StoredMatrixTransformations;
import net.hulan.ivr.block.BlockKCRPSDTop;
import net.hulan.ivr.client.IVRClientData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class RenderKCRRouteBase <T extends BlockKCRPSDTop.TileEntityKCRRouteBase> extends BlockEntityRendererMapper<T> implements IGui, IBlock {

    protected final float topPadding;
    protected final float bottomPadding;
    protected final float sidePadding;
    private final float z;
    private final boolean transparentWhite;
    private final Property<Integer> arrowDirectionProperty;

    public RenderKCRRouteBase(BlockEntityRenderDispatcher dispatcher, float z, float topPadding, float bottomPadding, float sidePadding, boolean transparentWhite, Property<Integer> arrowDirectionProperty) {
        super(dispatcher);
        this.z = z / 16.0F;
        this.topPadding = topPadding / 16.0F;
        this.bottomPadding = bottomPadding / 16.0F;
        this.sidePadding = sidePadding / 16.0F;
        this.transparentWhite = transparentWhite;
        this.arrowDirectionProperty = arrowDirectionProperty;
    }

    @Override
    public final void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        if (world != null) {
            BlockPos pos = entity.getPos();
            BlockState state = world.getBlockState(pos);
            Direction facing = IBlock.getStatePropertySafe(state, HorizontalFacingBlock.FACING);
            StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations();
            storedMatrixTransformations.add((matricesNew) -> {
                matricesNew.translate(0.5D + (double)entity.getPos().getX(), entity.getPos().getY(), 0.5D + (double)entity.getPos().getZ());
                UtilitiesClient.rotateYDegrees(matricesNew, -facing.asRotation());
            });
            this.renderAdditionalUnmodified(storedMatrixTransformations.copy(), state, facing, light);
            if (!RenderTrains.shouldNotRender(pos, RenderTrains.maxTrainRenderDistance, null)) {
                long platformId = entity.getPlatformId(ClientData.PLATFORMS, ClientData.DATA_CACHE);
                if (platformId != 0L) {
                    storedMatrixTransformations.add((matricesNew) -> {
                        matricesNew.translate(0.0D, 1.0D, 0.0D);
                        UtilitiesClient.rotateZDegrees(matricesNew, 180.0F);
                        matricesNew.translate(-0.5D, -this.getAdditionalOffset(state), this.z);
                    });
                    int leftBlocks = this.getTextureNumber(world, pos, facing, true);
                    int rightBlocks = this.getTextureNumber(world, pos, facing, false);
                    int color = getShadingColor(facing, -1);
                    RenderKCRRouteBase.RenderType renderType = this.getRenderType(world, pos.offset(facing.rotateYCounterclockwise(), leftBlocks), state);
                    if ((renderType == RenderKCRRouteBase.RenderType.ARROW || renderType == RenderKCRRouteBase.RenderType.ROUTE) && IBlock.getStatePropertySafe(state, SIDE_EXTENDED) != EnumSide.SINGLE) {
                        float width = (float)(leftBlocks + rightBlocks + 1) - this.sidePadding * 2.0F;
                        float height = 1.0F - this.topPadding - this.bottomPadding;
                        int arrowDirection = IBlock.getStatePropertySafe(state, this.arrowDirectionProperty);
                        Identifier resourceLocation;
                        if (renderType == RenderKCRRouteBase.RenderType.ARROW) {
                            resourceLocation = IVRClientData.DATA_CACHE.getDirectionArrow(
                                    platformId,
                                    (arrowDirection & 1) > 0,
                                    (arrowDirection & 2) > 0,
                                    HorizontalAlignment.CENTER,
                                    true,
                                    0.25F,
                                    width / height,
                                    -1,
                                    -16777216,
                                    this.transparentWhite ? -1 : 0).resourceLocation;
                        } else {
                            resourceLocation = IVRClientData.DATA_CACHE.getRouteMap(
                                    platformId,
                                    false,
                                    arrowDirection == 2,
                                    width / height,
                                    this.transparentWhite).resourceLocation;
                        }
                        RenderTrains.scheduleRender(resourceLocation, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
                            storedMatrixTransformations.transform(matricesNew);
                            IDrawing.drawTexture(matricesNew, vertexConsumer, leftBlocks == 0 ? this.sidePadding : 0.0F, this.topPadding, 0.0F, 1.0F - (rightBlocks == 0 ? this.sidePadding : 0.0F), 1.0F - this.bottomPadding, 0.0F, ((float)leftBlocks - (leftBlocks == 0 ? 0.0F : this.sidePadding)) / width, 0.0F, (width - (float)rightBlocks + (rightBlocks == 0 ? 0.0F : this.sidePadding)) / width, 1.0F, facing.getOpposite(), color, light);
                            matricesNew.pop();
                        });
                    }

                    this.renderAdditional(storedMatrixTransformations, platformId, state, leftBlocks, rightBlocks, facing.getOpposite(), color, light);
                }
            }

        }
    }

    public boolean shouldRenderOffScreen(T blockEntity) {
        return true;
    }

    protected void renderAdditionalUnmodified(StoredMatrixTransformations storedMatrixTransformations, BlockState state, Direction facing, int light) {
    }

    protected float getAdditionalOffset(BlockState state) {
        return 0.0F;
    }

    protected boolean isLeft(BlockState state) {
        return IBlock.getStatePropertySafe(state, SIDE_EXTENDED) == EnumSide.LEFT;
    }

    protected boolean isRight(BlockState state) {
        return IBlock.getStatePropertySafe(state, SIDE_EXTENDED) == EnumSide.RIGHT;
    }

    protected abstract RenderKCRRouteBase.RenderType getRenderType(BlockView var1, BlockPos var2, BlockState var3);

    protected abstract void renderAdditional(StoredMatrixTransformations var1, long var2, BlockState var4, int var5, int var6, Direction var7, int var8, int var9);

    private int getTextureNumber(BlockView world, BlockPos pos, Direction facing, boolean searchLeft) {
        int number = 0;
        Block thisBlock = world.getBlockState(pos).getBlock();

        while(true) {
            BlockState state = world.getBlockState(pos.offset(searchLeft ? facing.rotateYCounterclockwise() : facing.rotateYClockwise(), number));
            if (state.getBlock() != thisBlock) {
                break;
            }

            boolean isLeft = this.isLeft(state);
            boolean isRight = this.isRight(state);
            if (number != 0) {
                if (searchLeft) {
                    if (isRight) {
                        break;
                    }
                } else if (isLeft) {
                    break;
                }
            }

            ++number;
            if (searchLeft) {
                if (isLeft) {
                    break;
                }
            } else if (isRight) {
                break;
            }
        }

        return number - 1;
    }

    public static int getShadingColor(Direction facing, int grayscaleColorByte) {
        int colorByte = Math.round((float)(grayscaleColorByte & 255) * (facing.getAxis() == Direction.Axis.X ? 0.75F : 1.0F));
        return -16777216 | (colorByte << 16) + (colorByte << 8) + colorByte;
    }

    protected enum RenderType {
        ARROW,
        ROUTE,
        NONE;

        RenderType() {
        }
    }
}
