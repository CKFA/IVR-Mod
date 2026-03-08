package net.hulan.ivr.block;

import mtr.Items;
import mtr.block.IBlock;
import mtr.block.BlockPSDAPGGlassEndBase;
import mtr.block.BlockPSDAPGBase;
import mtr.data.DataCache;
import mtr.data.Platform;
import mtr.data.RailwayData;
import mtr.mappings.BlockDirectionalMapper;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.EntityBlockMapper;
import net.hulan.ivr.IVRBlockEntityTypes;
import net.hulan.ivr.IVRBlocks;
import net.hulan.ivr.IVRItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BlockKCRPSDTop extends BlockDirectionalMapper implements EntityBlockMapper, IBlock {

    public static final BooleanProperty AIR_LEFT = BooleanProperty.create("air_left");
    public static final BooleanProperty AIR_RIGHT = BooleanProperty.create("air_right");
    public static final IntegerProperty ARROW_DIRECTION = IntegerProperty.create("propagate_property", 0, 3);
    public static final EnumProperty<EnumPersistent> PERSISTENT = EnumProperty.create("persistent", EnumPersistent.class);

    public BlockKCRPSDTop() {
        super(Properties.of(Material.METAL, MaterialColor.QUARTZ).requiresCorrectToolForDrops().strength(2.0F).noOcclusion());
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return IBlock.checkHoldingItem(world, player, (item) -> {
            if (item == Items.BRUSH.get()) {
                world.setBlockAndUpdate(pos, state.cycle(ARROW_DIRECTION));
                propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).getClockWise(), ARROW_DIRECTION, 1);
                propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).getCounterClockWise(), ARROW_DIRECTION, 1);
            } else {
                boolean shouldBePersistent = IBlock.getStatePropertySafe(state, PERSISTENT) == EnumPersistent.NONE;
                setState(world, pos, shouldBePersistent);
                propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).getClockWise(), (offsetPos) -> setState(world, offsetPos, shouldBePersistent), 1);
                propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).getCounterClockWise(), (offsetPos) -> setState(world, offsetPos, shouldBePersistent), 1);
            }
        }, null, Items.BRUSH.get(), net.minecraft.world.item.Items.SHEARS);
    }

    private void setState(Level world, BlockPos pos, boolean shouldBePersistent) {
        Block blockBelow = world.getBlockState(pos.below()).getBlock();
        if (blockBelow instanceof BlockKCRPSDDoor || blockBelow instanceof BlockKCRPSDGlass || blockBelow instanceof BlockKCRPSDGlassEnd) {
            if (shouldBePersistent) {
                world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(PERSISTENT, blockBelow instanceof BlockKCRPSDDoor ? EnumPersistent.ARROW : (blockBelow instanceof BlockKCRPSDGlass ? EnumPersistent.ROUTE : EnumPersistent.BLANK)));
            } else {
                world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(PERSISTENT, EnumPersistent.NONE));
            }
        }
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state;
    }

    @Override
    public @NotNull Item asItem() {
        return IVRItems.KCR_PSD_GLASS_1.get();
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return new ItemStack(asItem());
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        Block blockDown = world.getBlockState(pos.below()).getBlock();
        if (blockDown instanceof BlockPSDAPGBase) {
            blockDown.playerWillDestroy(world, pos.below(), world.getBlockState(pos.below()), player);
            world.setBlockAndUpdate(pos.below(), Blocks.AIR.defaultBlockState());
        }
        super.playerWillDestroy(world, pos, state, player);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        return direction == Direction.DOWN && IBlock.getStatePropertySafe(state, PERSISTENT) == EnumPersistent.NONE && !(newState.getBlock() instanceof BlockPSDAPGBase) ? Blocks.AIR.defaultBlockState() : getActualState(world, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        VoxelShape baseShape = IBlock.getVoxelShapeByDirection(0.0D, IBlock.getStatePropertySafe(state, PERSISTENT) == EnumPersistent.NONE ? 0.0D : 7.5D, 0.0D, 16.0D, 16.0D, 6.0D, IBlock.getStatePropertySafe(state, FACING));
        boolean airLeft = IBlock.getStatePropertySafe(state, AIR_LEFT);
        boolean airRight = IBlock.getStatePropertySafe(state, AIR_RIGHT);
        return !airLeft && !airRight ? baseShape : BlockPSDAPGGlassEndBase.getEndOutlineShape(baseShape, state, 16, 6, airLeft, airRight);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.BLOCK;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SIDE_EXTENDED, AIR_LEFT, AIR_RIGHT, ARROW_DIRECTION, PERSISTENT);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityKCRPSDTop(pos, state);
    }

    public static BlockState getActualState(BlockGetter world, BlockPos pos) {
        Direction facing = null;
        EnumSide side = null;
        boolean airLeft = false;
        boolean airRight = false;
        BlockState stateBelow = world.getBlockState(pos.below());
        Block blockBelow = stateBelow.getBlock();
        if (blockBelow instanceof BlockKCRPSDGlass || blockBelow instanceof BlockKCRPSDDoor || blockBelow instanceof BlockKCRPSDGlassEnd) {
            if (blockBelow instanceof BlockKCRPSDDoor) {
                side = IBlock.getStatePropertySafe(stateBelow, SIDE);
            } else {
                side = IBlock.getStatePropertySafe(stateBelow, SIDE_EXTENDED);
            }
            if (blockBelow instanceof BlockKCRPSDGlassEnd) {
                if (IBlock.getStatePropertySafe(stateBelow, BlockKCRPSDGlassEnd.TOUCHING_LEFT) == BlockPSDAPGGlassEndBase.EnumPSDAPGGlassEndSide.AIR) {
                    airLeft = true;
                }
                if (IBlock.getStatePropertySafe(stateBelow, BlockKCRPSDGlassEnd.TOUCHING_RIGHT) == BlockPSDAPGGlassEndBase.EnumPSDAPGGlassEndSide.AIR) {
                    airRight = true;
                }
            }
            facing = IBlock.getStatePropertySafe(stateBelow, FACING);
        }
        BlockState oldState = world.getBlockState(pos);
        BlockState newState = (oldState.getBlock() instanceof BlockKCRPSDTop ? oldState : IVRBlocks.KCR_PSD_TOP.get().defaultBlockState()).setValue(AIR_LEFT, airLeft).setValue(AIR_RIGHT, airRight);
        if (facing != null) {
            newState = newState.setValue(FACING, facing);
        }
        if (side != null) {
            newState = newState.setValue(SIDE_EXTENDED, side);
        }
        return newState;
    }

    public enum EnumPersistent implements StringRepresentable {
        NONE("none"),
        ARROW("arrow"),
        ROUTE("route"),
        BLANK("blank");

        private final String name;

        EnumPersistent(String nameIn) {
            name = nameIn;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }

    public static class TileEntityKCRPSDTop extends TileEntityKCRRouteBase {
        public TileEntityKCRPSDTop(BlockPos pos, BlockState state) {
            super(IVRBlockEntityTypes.KCR_PSD_TOP_TILE_ENTITY.get(), pos, state);
        }
    }

    public static class TileEntityKCRRouteBase extends BlockEntityMapper {
        private long cachedRefreshTime;
        private long cachedPlatformId;

        public TileEntityKCRRouteBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }

        public long getPlatformId(Set<Platform> platforms, DataCache dataCache) {
            if (dataCache.needsRefresh(cachedRefreshTime)) {
                cachedPlatformId = RailwayData.getClosePlatformId(platforms, dataCache, getBlockPos());
                cachedRefreshTime = System.currentTimeMillis();
            }
            return cachedPlatformId;
        }
    }
}
