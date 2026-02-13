package net.hulan.ivr.block;

import mtr.block.BlockDirectionalDoubleBlockBase;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityClientSerializableMapper;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.EntityBlockMapper;
import net.hulan.ivr.IVRBlockEntityTypes;
import net.hulan.ivr.packet.IVRPacketTrainDataGuiServer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class BlockKCRRouteSignBase extends BlockDirectionalDoubleBlockBase implements EntityBlockMapper, IBlock, IVRBlockEntityTypes {

    public static final IntProperty ARROW_DIRECTION = IntProperty.of("propagate_property", 0, 3);

    public BlockKCRRouteSignBase() {
        super(Settings.of(Material.METAL, MapColor.GRAY).requiresTool().strength(2.0F).luminance((state) -> 15).nonOpaque());
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand interactionHand, BlockHitResult hit) {
        double y = hit.getPos().y;
        boolean isUpper = IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.UPPER;
        return IBlock.checkHoldingBrush(world, player, () -> {
            if (isUpper && y - Math.floor(y) > 0.8125D) {
                world.setBlockState(pos, state.cycle(ARROW_DIRECTION));
                this.propagate(world, pos, Direction.DOWN, ARROW_DIRECTION, 1);
            } else {
                BlockEntity entity = world.getBlockEntity(pos.down(isUpper ? 1 : 0));
                if (entity instanceof BlockKCRRouteSignBase.TileEntityKCRRouteSignBase) {
                    IVRPacketTrainDataGuiServer.openModernSignScreenS2C((ServerPlayerEntity)player, entity.getPos());
                }
            }
        });
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, ARROW_DIRECTION);
    }

    @Override
    public abstract BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state);

    public abstract static class TileEntityKCRRouteSignBase extends BlockEntityClientSerializableMapper {

        private long platformId;

        public TileEntityKCRRouteSignBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }

        @Override
        public void readCompoundTag(NbtCompound compoundTag) {
            this.platformId = compoundTag.getLong("platform_id");
        }

        @Override
        public void writeCompoundTag(NbtCompound compoundTag) {
            compoundTag.putLong("platform_id", this.platformId);
        }

        public void setPlatformId(long platformId) {
            this.platformId = platformId;
            this.markDirty();
            this.syncData();
        }

        public long getPlatformId() {
            return this.platformId;
        }
    }
}
