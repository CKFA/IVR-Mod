package net.hulan.ivr.block;

import mtr.block.IBlock;
import mtr.mappings.*;
import net.hulan.ivr.IVRBlockEntityTypes;
import net.hulan.ivr.IVRBlocks;
import net.hulan.ivr.packet.IVRPacketTrainDataGuiServer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BlockClassicalSign extends BlockDirectionalMapper implements EntityBlockMapper, IBlock {

    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public final int length;
    public final boolean isOdd;
    public static final float SMALL_SIGN_PERCENTAGE = 0.75F;

    public BlockClassicalSign(int length, boolean isOdd) {
        super(Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(2.0F).lightLevel(value -> value.getValue(LIT) ? 15 : 0));
        this.length = length;
        this.isOdd = isOdd;
        registerDefaultState(defaultBlockState().setValue(LIT, false));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult hit) {
        return IBlock.checkHoldingBrush(world, player, () -> {
            final Direction facing = IBlock.getStatePropertySafe(state, FACING);
            final Direction hitSide = hit.getDirection();
            if (hitSide == facing || hitSide == facing.getOpposite()) {
                final BlockPos checkPos = findEndWithDirection(world, pos, hitSide.getOpposite(), false);
                if (checkPos != null) {
                    if (this == IVRBlocks.CLASSICAL_SIGN_1_ODD.get()) {
                        IVRPacketTrainDataGuiServer.openClassicalSign1OddScreenS2C((ServerPlayer) player, checkPos);
                    } else {
                        IVRPacketTrainDataGuiServer.openClassicalSignScreenS2C((ServerPlayer) player, checkPos);
                    }
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final boolean isNext = direction == facing.getClockWise() || state.is(IVRBlocks.CLASSICAL_SIGN_MIDDLE.get()) && direction == facing.getCounterClockWise();
        if (isNext && !(newState.getBlock() instanceof BlockClassicalSign) && !equals(IVRBlocks.CLASSICAL_SIGN_1_ODD.get())) {
            return Blocks.AIR.defaultBlockState();
        } else {
            return state;
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final Direction facing = ctx.getHorizontalDirection();
        if (equals(IVRBlocks.CLASSICAL_SIGN_1_ODD.get())) return defaultBlockState().setValue(FACING, facing);
        return IBlock.isReplaceable(ctx, facing.getClockWise(), getMiddleLength() + 2) ? defaultBlockState().setValue(FACING, facing) : null;
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        if (!equals(IVRBlocks.CLASSICAL_SIGN_1_ODD.get())) {
            final BlockPos checkPos = findEndWithDirection(world, pos, facing, true);
            if (checkPos != null) {
                IBlock.onBreakCreative(world, player, checkPos);
            }
        }
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClientSide) {
            final Direction facing = IBlock.getStatePropertySafe(state, FACING);
            for (int i = 1; i <= getMiddleLength(); i++) {
                world.setBlock(pos.relative(facing.getClockWise(), i), IVRBlocks.CLASSICAL_SIGN_MIDDLE.get().defaultBlockState().setValue(FACING, facing), 3);
            }
            if (!equals(IVRBlocks.CLASSICAL_SIGN_1_ODD.get())) {
                world.setBlock(pos.relative(facing.getClockWise(), getMiddleLength() + 1), defaultBlockState().setValue(FACING, facing.getOpposite()), 3);
            }
            world.updateNeighborsAt(pos, Blocks.AIR);
            state.updateNeighbourShapes(world, pos, 3);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        if (state.is(IVRBlocks.CLASSICAL_SIGN_MIDDLE.get())) {
            return IBlock.getVoxelShapeByDirection(0, 0.25, 5, 16, 8.75, 11, facing);
        } else {
            final int xStart = getXStart();
            final VoxelShape main, pole;
            if (equals(IVRBlocks.CLASSICAL_SIGN_1_ODD.get())) {
                main = IBlock.getVoxelShapeByDirection(2.25, 0.25, 5, 13.75, 8.75, 11, facing);
                final VoxelShape poleL = IBlock.getVoxelShapeByDirection(6.25, 8, 7.5, 7.25, 16, 8.5, facing), poleR = IBlock.getVoxelShapeByDirection(8.75, 8, 7.5, 9.75, 16, 8.5, facing);
                return Shapes.or(main, poleL, poleR);
            } else {
                main = IBlock.getVoxelShapeByDirection(xStart - 4.375, 0.25, 5, 16, 8.75, 11, facing);
                pole = switch (length % 4) {
                    case 1 -> isOdd ? IBlock.getVoxelShapeByDirection(6.25, 8.75, 7.5, 7.25, 16, 8.5, facing) : IBlock.getVoxelShapeByDirection(14, 8.75, 7.5, 15, 16, 8.5, facing);
                    case 2 -> isOdd ? IBlock.getVoxelShapeByDirection(18.5, 8.75, 7.5, 19.5, 16, 8.5, facing) : IBlock.getVoxelShapeByDirection(9.25, 8.75, 7.5, 10.25, 16, 8.5, facing);
                    case 3 -> isOdd ? IBlock.getVoxelShapeByDirection(14, 8.75, 7.5, 15, 16, 8.5, facing) : IBlock.getVoxelShapeByDirection(6.25, 8.75, 7.5, 7.25, 16, 8.5, facing);
                    default -> isOdd ? IBlock.getVoxelShapeByDirection(9.25, 8.75, 7.5, 10.25, 16, 8.5, facing) : IBlock.getVoxelShapeByDirection(18.5, 8.75, 7.5, 19.5, 16, 8.5, facing);
                };
                return Shapes.or(main, pole);
            }
        }
    }

    @Override
    public @NotNull String getDescriptionId() {
        return "block.ivr.classical_sign";
    }

    @Override
    public void appendHoverText(ItemStack itemStack, BlockGetter blockGetter, List<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.add(Text.translatable("tooltip.mtr.railway_sign_length", length).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        tooltip.add(Text.translatable(isOdd ? "tooltip.mtr.railway_sign_odd" : "tooltip.mtr.railway_sign_even").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        if (this == IVRBlocks.CLASSICAL_SIGN_MIDDLE.get()) {
            return null;
        } else if (this == IVRBlocks.CLASSICAL_SIGN_1_ODD.get()) {
            return new TileEntityClassicalSign1Odd(pos, state);
        } else {
            return new TileEntityClassicalSign(length, isOdd, pos, state);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    public int getXStart() {
        return switch (length % 4) {
            case 1 -> isOdd ? 4 : 12;
            case 2 -> isOdd ? 16 : 8;
            case 3 -> isOdd ? 12 : 4;
            default -> isOdd ? 8 : 16;
        };
    }

    private int getMiddleLength() {
        int middleLength = (length - (4 - getXStart() / 4)) / 2;
        return Math.max(middleLength, 0);
    }

    private BlockPos findEndWithDirection(Level world, BlockPos startPos, Direction direction, boolean allowOpposite) {
        int i = 0;
        final BlockState state = world.getBlockState(startPos);
        if (state.is(IVRBlocks.CLASSICAL_SIGN_1_ODD.get())) {
            return startPos;
        }
        while (true) {
            final BlockPos checkPos = startPos.relative(direction.getCounterClockWise(), i);
            final BlockState checkState = world.getBlockState(checkPos);
            if (checkState.getBlock() instanceof BlockClassicalSign) {
                final Direction facing = IBlock.getStatePropertySafe(checkState, FACING);
                if ((!checkState.is(IVRBlocks.CLASSICAL_SIGN_MIDDLE.get()) && (facing == direction || allowOpposite && facing == direction.getOpposite()))) {
                    return checkPos;
                }
            } else {
                return null;
            }
            i++;
        }
    }

    public static class TileEntityClassicalSign extends BlockEntityClientSerializableMapper {

        private final Set<Long> selectedIds;
        private final String[] signIds;
        private boolean luminance;
        private static final String KEY_SELECTED_IDS = "selected_ids";
        private static final String KEY_SIGN_LENGTH = "sign_length";

        public TileEntityClassicalSign(int length, boolean isOdd, BlockPos pos, BlockState state) {
            super(getType(length, isOdd), pos, state);
            signIds = new String[length];
            selectedIds = new HashSet<>();
            luminance = false;
            setChanged();
            syncData();
        }

        @Override
        public void readCompoundTag(CompoundTag compoundTag) {
            selectedIds.clear();
            Arrays.stream(compoundTag.getLongArray(KEY_SELECTED_IDS)).forEach(selectedIds::add);
            for (int i = 0; i < signIds.length; i++) {
                final String signId = compoundTag.getString(KEY_SIGN_LENGTH + i);
                signIds[i] = signId.isEmpty() ? null : signId;
            }
            luminance = compoundTag.getBoolean("luminance");
        }

        @Override
        public void writeCompoundTag(CompoundTag compoundTag) {
            compoundTag.putLongArray(KEY_SELECTED_IDS, new ArrayList<>(selectedIds));
            for (int i = 0; i < signIds.length; i++) {
                compoundTag.putString(KEY_SIGN_LENGTH + i, signIds[i] == null ? "" : signIds[i]);
            }
            compoundTag.putBoolean("luminance", luminance);
        }

        public AABB getRenderBoundingBox() {
            return new AABB(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        }

        public void setData(Set<Long> selectedIds, String[] signTypes, boolean luminance) {
            this.selectedIds.clear();
            this.selectedIds.addAll(selectedIds);
            if (signIds.length == signTypes.length) {
                System.arraycopy(signTypes, 0, signIds, 0, signTypes.length);
            }
            this.luminance = luminance;
            light();
            setChanged();
            syncData();
        }

        public Set<Long> getSelectedIds() {
            return selectedIds;
        }

        public String[] getSignIds() {
            return signIds;
        }

        public boolean luminance() {
            return luminance;
        }

        public void light() {
            BlockPos firstPos = getBlockPos();
            BlockState firstState = getBlockState();
            Direction facing = IBlock.getStatePropertySafe(firstState, FACING);
            BlockClassicalSign sign = (BlockClassicalSign) firstState.getBlock();
            int middleLength = sign.getMiddleLength();
            BlockPos middlePos, lastPos = firstPos.relative(facing.getClockWise(), middleLength + 1);
            if (level != null) {
                BlockState middleState, lastState = level.getBlockState(lastPos);
                TileEntityClassicalSign lastEntity = (TileEntityClassicalSign) level.getBlockEntity(lastPos);
                level.setBlock(firstPos, firstState.setValue(LIT, luminance), 3);
                level.getLightEngine().checkBlock(firstPos);
                for (int i = 1; i <= middleLength + 1; i++) {
                    middlePos = firstPos.relative(facing.getClockWise(), i);
                    middleState = level.getBlockState(middlePos);
                    level.setBlock(middlePos, middleState.setValue(LIT, luminance), 3);
                    level.getLightEngine().checkBlock(middlePos);
                }
                if (!firstState.getBlock().equals(IVRBlocks.CLASSICAL_SIGN_1_ODD.get())) {
                    level.setBlock(lastPos, lastState.setValue(LIT, luminance), 3);
                    level.getLightEngine().checkBlock(lastPos);
                    assert lastEntity != null;
                    lastEntity.luminance = luminance;
                }
            }
        }

        private static BlockEntityType<?> getType(int length, boolean isOdd) {
            return switch (length) {
                case 1 -> isOdd ? IVRBlockEntityTypes.CLASSICAL_SIGN_1_ODD_TILE_ENTITY.get() : IVRBlockEntityTypes.CLASSICAL_SIGN_1_EVEN_TILE_ENTITY.get();
                case 2 -> isOdd ? IVRBlockEntityTypes.CLASSICAL_SIGN_2_ODD_TILE_ENTITY.get() : IVRBlockEntityTypes.CLASSICAL_SIGN_2_EVEN_TILE_ENTITY.get();
                case 3 -> isOdd ? IVRBlockEntityTypes.CLASSICAL_SIGN_3_ODD_TILE_ENTITY.get() : IVRBlockEntityTypes.CLASSICAL_SIGN_3_EVEN_TILE_ENTITY.get();
                case 4 -> isOdd ? IVRBlockEntityTypes.CLASSICAL_SIGN_4_ODD_TILE_ENTITY.get() : IVRBlockEntityTypes.CLASSICAL_SIGN_4_EVEN_TILE_ENTITY.get();
                case 5 -> isOdd ? IVRBlockEntityTypes.CLASSICAL_SIGN_5_ODD_TILE_ENTITY.get() : IVRBlockEntityTypes.CLASSICAL_SIGN_5_EVEN_TILE_ENTITY.get();
                case 6 -> isOdd ? IVRBlockEntityTypes.CLASSICAL_SIGN_6_ODD_TILE_ENTITY.get() : IVRBlockEntityTypes.CLASSICAL_SIGN_6_EVEN_TILE_ENTITY.get();
                case 7 -> isOdd ? IVRBlockEntityTypes.CLASSICAL_SIGN_7_ODD_TILE_ENTITY.get() : IVRBlockEntityTypes.CLASSICAL_SIGN_7_EVEN_TILE_ENTITY.get();
                default -> null;
            };
        }
    }

    public static class TileEntityClassicalSign1Odd extends BlockEntityClientSerializableMapper {

        private final Set<Long> selectedIds1;
        private final String[] signId1;
        private final Set<Long> selectedIds2;
        private final String[] signId2;
        private boolean luminance;
        private static final String KEY_SELECTED_IDS1 = "selected_ids1";
        private static final String KEY_SIGN_LENGTH1 = "sign_length1";
        private static final String KEY_SELECTED_IDS2 = "selected_ids2";
        private static final String KEY_SIGN_LENGTH2 = "sign_length2";

        public TileEntityClassicalSign1Odd(BlockPos pos, BlockState state) {
            super(IVRBlockEntityTypes.CLASSICAL_SIGN_1_ODD_TILE_ENTITY.get(), pos, state);
            signId1 = new String[1];
            selectedIds1 = new HashSet<>();
            signId2 = new String[1];
            selectedIds2 = new HashSet<>();
            luminance = false;
            setChanged();
            syncData();
        }

        @Override
        public void readCompoundTag(CompoundTag compoundTag) {
            selectedIds1.clear();
            Arrays.stream(compoundTag.getLongArray(KEY_SELECTED_IDS1)).forEach(selectedIds1::add);
            for (int i = 0; i < signId1.length; i++) {
                final String signId = compoundTag.getString(KEY_SIGN_LENGTH1 + i);
                signId1[i] = signId.isEmpty() ? null : signId;
            }
            selectedIds2.clear();
            Arrays.stream(compoundTag.getLongArray(KEY_SELECTED_IDS2)).forEach(selectedIds2::add);
            for (int i = 0; i < signId2.length; i++) {
                final String signId = compoundTag.getString(KEY_SIGN_LENGTH2 + i);
                signId2[i] = signId.isEmpty() ? null : signId;
            }
            luminance = compoundTag.getBoolean("luminance");
        }

        @Override
        public void writeCompoundTag(CompoundTag compoundTag) {
            compoundTag.putLongArray(KEY_SELECTED_IDS1, new ArrayList<>(selectedIds1));
            for (int i = 0; i < signId1.length; i++) {
                compoundTag.putString(KEY_SIGN_LENGTH1 + i, signId1[i] == null ? "" : signId1[i]);
            }
            compoundTag.putLongArray(KEY_SELECTED_IDS2, new ArrayList<>(selectedIds2));
            for (int i = 0; i < signId2.length; i++) {
                compoundTag.putString(KEY_SIGN_LENGTH2 + i, signId2[i] == null ? "" : signId2[i]);
            }
            compoundTag.putBoolean("luminance", luminance);
        }

        public AABB getRenderBoundingBox() {
            return new AABB(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        }

        public void setData(Set<Long> selectedIds1, String[] signType1, Set<Long> selectedIds2, String[] signType2, boolean luminance) {
            this.selectedIds1.clear();
            this.selectedIds1.addAll(selectedIds1);
            if (signId1.length == signType1.length) {
                System.arraycopy(signType1, 0, signId1, 0, signType1.length);
            }
            this.selectedIds2.clear();
            this.selectedIds2.addAll(selectedIds2);
            if (signId2.length == signType2.length) {
                System.arraycopy(signType2, 0, signId2, 0, signType2.length);
            }
            this.luminance = luminance;
            light();
            setChanged();
            syncData();
        }

        public Set<Long> getSelectedIds1() {
            return selectedIds1;
        }

        public Set<Long> getSelectedIds2() {
            return selectedIds2;
        }

        public String[] getSignId1() {
            return signId1;
        }

        public String[] getSignId2() {
            return signId2;
        }

        public boolean luminance() {
            return luminance;
        }

        public void light() {
            BlockPos firstPos = getBlockPos();
            BlockState firstState = getBlockState();
            if (level != null) {
                level.setBlock(firstPos, firstState.setValue(LIT, luminance), 3);
                level.getLightEngine().checkBlock(firstPos);
            }
        }
    }

    public enum SignType {
        ARROW_LEFT("arrow", true, false),
        ARROW_RIGHT("arrow", true, true),
        ARROW_UP("arrow_up", true, false),
        ARROW_DOWN("arrow_down", true, false),
        ARROW_UP_LEFT("arrow_up_left", true, false),
        ARROW_UP_RIGHT("arrow_up_left", true, true),
        ARROW_DOWN_LEFT("arrow_down_left", true, false),
        ARROW_DOWN_RIGHT("arrow_down_left", true, true),
        ARROW_TURN_BACK_LEFT("arrow_turn_back", true, false),
        ARROW_TURN_BACK_RIGHT("arrow_turn_back", true, true),
        EXIT_1("exit_1", false, false),
        EXIT_2("exit_2", true, false),
        EXIT_3("exit_3", true, false),
        ESCALATOR("escalator", true, false),
        ESCALATOR_FLIPPED("escalator", true, true),
        STAIRS_UP("stairs_up", true, false),
        STAIRS_UP_FLIPPED("stairs_up", true, true),
        STAIRS_DOWN_FLIPPED("stairs_down", true, true),
        STAIRS_DOWN("stairs_down", true, false),
        LIFT_1("lift_1", true, false),
        LIFT_2("lift_2", true, false),
        WHEELCHAIR("wheelchair", true, false),
        TOILET("toilets", false, false),
        FEMALE("female", true, false),
        MALE("male", true, false),
        TRAIN("train", true, false),
        TRAIN_OLD("train_old", true, false),
        AIRPORT_EXPRESS("airport_express", true, false),
        LIGHT_RAIL_1("light_rail_1", true, false),
        LIGHT_RAIL_2("light_rail_2", false, false),
        LIGHT_RAIL_3("light_rail_3", true, false),
        LIGHT_RAIL_4("light_rail_4", false, false),
        XRL_1("xrl_1", true, false),
        XRL_2("xrl_2", true, false),
        SP1900("sp1900", true, false),
        YELLOW_HEAD_1("yellow_head_1", true, false),
        YELLOW_HEAD_2("yellow_head_2", false, false),
        BOAT("boat", true, false),
        CABLE_CAR("cable_car", true, false),
        AIRPLANE("airplane", true, false),
        AIRPLANE_LEFT("airplane_left", true, false),
        AIRPLANE_RIGHT("airplane_left", true, true),
        AIRPLANE_UP_LEFT("airplane_up_left", true, false),
        AIRPLANE_UP_RIGHT("airplane_up_left", true, true),
        CROSS("cross", true, false),
        LOGO("logo", false, false),
        EXIT_LETTER("exit_letter", true, false, true),
        EXIT_LETTER_FLIPPED("exit_letter", true, true, true),
        ESCALATOR_TO_CONCOURSE_UP("escalator", "escalator_to_concourse_up", true, true, false, true, 0),
        ESCALATOR_TO_CONCOURSE_UP_FLIPPED("escalator", "escalator_to_concourse_up", true, false, true, true, 0),
        ESCALATOR_TO_CONCOURSE_DOWN("escalator", "escalator_to_concourse_down", true, false, false, true, 0),
        ESCALATOR_TO_CONCOURSE_DOWN_FLIPPED("escalator", "escalator_to_concourse_down", true, true, true, true, 0),
        PLATFORM("platform", true, false, true),
        PLATFORM_FLIPPED("platform", true, true, true),
        LINE("line", true, false, true),
        LINE_FLIPPED("line", true, true, true),
        STATION("station", false, false, true),
        STATION_FLIPPED("station", false, true, true),
        LIFT_1_TEXT("lift_1", true, false, true),
        LIFT_1_TEXT_FLIPPED("lift_1", true, true, true),
        LIFT_2_TEXT("lift_2", true, false, true),
        LIFT_2_TEXT_FLIPPED("lift_2", true, true, true),
        TOILETS("toilets", false, false, true),
        TOILETS_FLIPPED("toilets", false, true, true),
        FEMALE_TOILETS("female", true, false, true),
        FEMALE_TOILETS_FLIPPED("female", true, true, true),
        MALE_TOILETS("male", true, false, true),
        MALE_TOILETS_FLIPPED("male", true, true, true),
        WHEELCHAIR_TOILETS("wheelchair", true, false, true),
        WHEELCHAIR_TOILETS_FLIPPED("wheelchair", true, true, true),
        TRAINS("train", true, false, true),
        TRAINS_FLIPPED("train", true, true, true),
        TRAINS_OLD("train_old", true, false, true),
        TRAINS_OLD_FLIPPED("train_old", true, true, true),
        AIRPORT_EXPRESS_TRAINS("airport_express", true, false, true),
        AIRPORT_EXPRESS_TRAINS_FLIPPED("airport_express", true, true, true),
        AIRPORT_EXPRESS_TRAINS_CITY("airport_express", "airport_express_city", true, false, true),
        AIRPORT_EXPRESS_TRAINS_CITY_FLIPPED("airport_express", "airport_express_city", true, true, true),
        IN_TOWN_CHECK_IN("check_in", "in_town_check_in", true, false, true),
        IN_TOWN_CHECK_IN_FLIPPED("check_in", "in_town_check_in", true, true, true),
        CHECK_IN_PASSENGERS("check_in", "check_in_passengers", true, false, true),
        CHECK_IN_PASSENGERS_FLIPPED("check_in", "check_in_passengers", true, true, true),
        LIGHT_RAIL_1_TRAINS("light_rail_1", true, false, true),
        LIGHT_RAIL_1_TRAINS_FLIPPED("light_rail_1", true, true, true),
        LIGHT_RAIL_2_TRAINS("light_rail_2", false, false, true),
        LIGHT_RAIL_2_TRAINS_FLIPPED("light_rail_2", false, true, true),
        LIGHT_RAIL_3_TRAINS("light_rail_3", true, false, true),
        LIGHT_RAIL_3_TRAINS_FLIPPED("light_rail_3", true, true, true),
        LIGHT_RAIL_4_TRAINS("light_rail_4", false, false, true),
        LIGHT_RAIL_4_TRAINS_FLIPPED("light_rail_4", false, true, true),
        XRL_1_TRAINS("xrl_1", true, false, true),
        XRL_1_TRAINS_FLIPPED("xrl_1", true, true, true),
        XRL_2_TRAINS("xrl_2", true, false, true),
        XRL_2_TRAINS_FLIPPED("xrl_2", true, true, true),
        SP1900_TRAINS("sp1900", true, false, true),
        SP1900_TRAINS_FLIPPED("sp1900", true, true, true),
        YELLOW_HEAD_1_TRAINS("yellow_head_1", true, false, true),
        YELLOW_HEAD_1_TRAINS_FLIPPED("yellow_head_1", true, true, true),
        YELLOW_HEAD_2_TRAINS("yellow_head_2", false, false, true),
        YELLOW_HEAD_2_TRAINS_FLIPPED("yellow_head_2", false, true, true),
        BOAT_BOATS("boat", true, false, true),
        BOAT_BOATS_FLIPPED("boat", true, true, true),
        CABLE_CAR_CABLE_CARS("cable_car", true, false, true),
        CABLE_CAR_CABLE_CARS_FLIPPED("cable_car", true, true, true),
        AIRPORT("airplane", "airport", true, false, false, true, 0),
        AIRPORT_FLIPPED("airplane", "airport", true, false, true, true, 0),
        AIRPORT_LEFT("airplane_left", "airport", true, false, false, true, 0),
        AIRPORT_RIGHT("airplane_left", "airport", true, true, true, true, 0),
        AIRPORT_UP_LEFT("airplane_up_left", "airport", true, false, false, true, 0),
        AIRPORT_UP_RIGHT("airplane_up_left", "airport", true, true, true, true, 0),
        AIRPORT_ARRIVALS("airplane_down_left", "airport_arrivals", true, true, false, true, 0),
        AIRPORT_ARRIVALS_FLIPPED("airplane_down_left", "airport_arrivals", true, false, true, true, 0),
        AIRPORT_DEPARTURES("airplane_up_left", "airport_departures", true, false, false, true, 0),
        AIRPORT_DEPARTURES_FLIPPED("airplane_up_left", "airport_departures", true, true, true, true, 0),
        AIRPORT_TRANSFER("airport_transfer", true, false, true),
        AIRPORT_TRANSFER_FLIPPED("airport_transfer", true, true, true),
        BAGGAGE_CLAIM("baggage_claim", true, false, true),
        BAGGAGE_CLAIM_FLIPPED("baggage_claim", true, true, true),
        CUSTOMER_SERVICE_CENTRE("customer_service_centre", true, false, true),
        CUSTOMER_SERVICE_CENTRE_FLIPPED("customer_service_centre", true, true, true),
        TICKETS("tickets", true, false, true),
        TICKETS_FLIPPED("tickets", true, true, true),
        NO_ENTRY("cross", true, false, true),
        NO_ENTRY_FLIPPED("cross", true, true, true),
        EMERGENCY_EXIT("emergency_exit", "emergency_exit", false, false, false, true, 0x00944F),
        EMERGENCY_EXIT_FLIPPED("emergency_exit", "emergency_exit", false, true, true, true, 0x00944F),
        WIFI("wifi", "wifi", true, false, false, true, 0xFA7B22),
        WIFI_FLIPPED("wifi", "wifi", true, true, true, true, 0xFA7B22),
        LOGO_TEXT("logo", false, false, true),
        LOGO_TEXT_FLIPPED("logo", false, true, true);

        public final ResourceLocation textureId;
        public final String customText;
        public final boolean small;
        public final boolean flipTexture;
        public final boolean flipCustomText;
        public final int backgroundColor;

        SignType(String texture, String translation, boolean small, boolean flipTexture, boolean flipCustomText, boolean hasCustomText, int backgroundColor) {
            textureId = new ResourceLocation("mtr:textures/block/sign/" + texture + ".png");
            customText = hasCustomText ? mtr.mappings.Text.translatable("sign.mtr." + translation + "_cjk").append("|").append(mtr.mappings.Text.translatable("sign.mtr." + translation)).getString() : "";
            this.small = small;
            this.flipTexture = flipTexture;
            this.flipCustomText = flipCustomText;
            this.backgroundColor = backgroundColor;
        }

        SignType(String texture, String translation, boolean small, boolean flipTexture, boolean hasCustomText) {
            this(texture, translation, small, false, flipTexture, hasCustomText, 0);
        }

        SignType(String texture, boolean small, boolean flipCustomText, boolean hasCustomText) {
            this(texture, texture, small, false, flipCustomText, hasCustomText, 0);
        }

        SignType(String texture, boolean small, boolean flipTexture) {
            this(texture, texture, small, flipTexture, false, false, 0);
        }
    }
}
