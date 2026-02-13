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
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Set;

public class BlockKCRPSDTop extends BlockDirectionalMapper implements EntityBlockMapper, IBlock {

    public static final BooleanProperty AIR_LEFT = BooleanProperty.of("air_left");
    public static final BooleanProperty AIR_RIGHT = BooleanProperty.of("air_right");
    public static final IntProperty ARROW_DIRECTION = IntProperty.of("propagate_property", 0, 3);
    public static final EnumProperty<BlockKCRPSDTop.EnumPersistent> PERSISTENT = EnumProperty.of("persistent", BlockKCRPSDTop.EnumPersistent.class);

    public BlockKCRPSDTop() {
        super(Settings.of(Material.METAL, MapColor.OFF_WHITE).requiresTool().strength(2.0F).nonOpaque());
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand interactionHand, BlockHitResult blockHitResult) {
        return IBlock.checkHoldingItem(world, player, (item) -> {
            if (item == Items.BRUSH.get()) {
                world.setBlockState(pos, state.cycle(ARROW_DIRECTION));
                this.propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).rotateYClockwise(), ARROW_DIRECTION, 1);
                this.propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).rotateYCounterclockwise(), ARROW_DIRECTION, 1);
            } else {
                boolean shouldBePersistent = IBlock.getStatePropertySafe(state, PERSISTENT) == BlockKCRPSDTop.EnumPersistent.NONE;
                this.setState(world, pos, shouldBePersistent);
                this.propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).rotateYClockwise(), (offsetPos) -> this.setState(world, offsetPos, shouldBePersistent), 1);
                this.propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).rotateYCounterclockwise(), (offsetPos) -> this.setState(world, offsetPos, shouldBePersistent), 1);
            }

        }, null, Items.BRUSH.get(), net.minecraft.item.Items.SHEARS);
    }

    private void setState(World world, BlockPos pos, boolean shouldBePersistent) {
        Block blockBelow = world.getBlockState(pos.down()).getBlock();
        if (blockBelow instanceof BlockKCRPSDDoor || blockBelow instanceof BlockKCRPSDGlass || blockBelow instanceof BlockKCRPSDGlassEnd) {
            if (shouldBePersistent) {
                world.setBlockState(pos, world.getBlockState(pos).with(PERSISTENT, blockBelow instanceof BlockKCRPSDDoor ? BlockKCRPSDTop.EnumPersistent.ARROW : (blockBelow instanceof BlockKCRPSDGlass ? BlockKCRPSDTop.EnumPersistent.ROUTE : BlockKCRPSDTop.EnumPersistent.BLANK)));
            } else {
                world.setBlockState(pos, world.getBlockState(pos).with(PERSISTENT, BlockKCRPSDTop.EnumPersistent.NONE));
            }
        }

    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state;
    }

    @Override
    public Item asItem() {
        return IVRItems.KCR_PSD_GLASS_1.get();
    }

    @Override
    public ItemStack getPickStack(BlockView blockGetter, BlockPos blockPos, BlockState blockState) {
        return new ItemStack(this.asItem());
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        Block blockDown = world.getBlockState(pos.down()).getBlock();
        if (blockDown instanceof BlockPSDAPGBase) {
            blockDown.onBreak(world, pos.down(), world.getBlockState(pos.down()), player);
            world.setBlockState(pos.down(), Blocks.AIR.getDefaultState());
        }

        super.onBreak(world, pos, state, player);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        return direction == Direction.DOWN && IBlock.getStatePropertySafe(state, PERSISTENT) == BlockKCRPSDTop.EnumPersistent.NONE && !(newState.getBlock() instanceof BlockPSDAPGBase) ? Blocks.AIR.getDefaultState() : getActualState(world, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView blockGetter, BlockPos pos, ShapeContext collisionContext) {
        VoxelShape baseShape = IBlock.getVoxelShapeByDirection(0.0D, IBlock.getStatePropertySafe(state, PERSISTENT) == BlockKCRPSDTop.EnumPersistent.NONE ? 0.0D : 7.5D, 0.0D, 16.0D, 16.0D, 6.0D, IBlock.getStatePropertySafe(state, FACING));
        boolean airLeft = IBlock.getStatePropertySafe(state, AIR_LEFT);
        boolean airRight = IBlock.getStatePropertySafe(state, AIR_RIGHT);
        return !airLeft && !airRight ? baseShape : BlockPSDAPGGlassEndBase.getEndOutlineShape(baseShape, state, 16, 6, airLeft, airRight);
    }

    @SuppressWarnings("deprecation")
    @Override
    public PistonBehavior getPistonBehavior(BlockState blockState) {
        return PistonBehavior.BLOCK;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, SIDE_EXTENDED, AIR_LEFT, AIR_RIGHT, ARROW_DIRECTION, PERSISTENT);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityKCRPSDTop(pos, state);
    }

    public static BlockState getActualState(BlockView world, BlockPos pos) {
        Direction facing = null;
        EnumSide side = null;
        boolean airLeft = false;
        boolean airRight = false;
        BlockState stateBelow = world.getBlockState(pos.down());
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
        BlockState newState = (oldState.getBlock() instanceof BlockKCRPSDTop ? oldState : IVRBlocks.KCR_PSD_TOP.get().getDefaultState()).with(AIR_LEFT, airLeft).with(AIR_RIGHT, airRight);
        if (facing != null) {
            newState = newState.with(FACING, facing);
        }

        if (side != null) {
            newState = newState.with(SIDE_EXTENDED, side);
        }

        return newState;
    }

    public enum EnumPersistent implements StringIdentifiable {
        NONE("none"),
        ARROW("arrow"),
        ROUTE("route"),
        BLANK("blank");

        private final String name;

        EnumPersistent(String nameIn) {
            this.name = nameIn;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }

    public static class TileEntityKCRPSDTop extends BlockKCRPSDTop.TileEntityKCRRouteBase {
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
            if (dataCache.needsRefresh(this.cachedRefreshTime)) {
                this.cachedPlatformId = RailwayData.getClosePlatformId(platforms, dataCache, this.getPos());
                this.cachedRefreshTime = System.currentTimeMillis();
            }

            return this.cachedPlatformId;
        }
    }
}
