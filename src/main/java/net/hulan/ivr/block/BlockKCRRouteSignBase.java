package net.hulan.ivr.block;

import mtr.block.BlockDirectionalDoubleBlockBase;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityClientSerializableMapper;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.EntityBlockMapper;
import net.hulan.ivr.IVRBlockEntityTypes;
import net.hulan.ivr.packet.IVRPacketTrainDataGuiServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public abstract class BlockKCRRouteSignBase extends BlockDirectionalDoubleBlockBase implements EntityBlockMapper, IBlock, IVRBlockEntityTypes {

    public static final IntegerProperty ARROW_DIRECTION = IntegerProperty.create("propagate_property", 0, 3);

    public BlockKCRRouteSignBase() {
        super(Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(2.0F).lightLevel((state) -> 15).noOcclusion());
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult hit) {
        double y = hit.getBlockPos().getY();
        boolean isUpper = IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.UPPER;
        return IBlock.checkHoldingBrush(world, player, () -> {
            if (isUpper && y - Math.floor(y) > 0.8125D) {
                world.setBlockAndUpdate(pos, state.cycle(ARROW_DIRECTION));
                propagate(world, pos, Direction.DOWN, ARROW_DIRECTION, 1);
            } else {
                BlockEntity entity = world.getBlockEntity(pos.below(isUpper ? 1 : 0));
                if (entity instanceof BlockKCRRouteSignBase.TileEntityKCRRouteSignBase) {
                    IVRPacketTrainDataGuiServer.openModernSignScreenS2C((ServerPlayer)player, entity.getBlockPos());
                }
            }
        });
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
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
        public void readCompoundTag(CompoundTag compoundTag) {
            platformId = compoundTag.getLong("platform_id");
        }

        @Override
        public void writeCompoundTag(CompoundTag compoundTag) {
            compoundTag.putLong("platform_id", platformId);
        }

        public void setPlatformId(long platformId) {
            this.platformId = platformId;
            setChanged();
            syncData();
        }

        public long getPlatformId() {
            return platformId;
        }
    }
}
