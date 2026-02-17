package net.hulan.ivr.block;

import mtr.block.IBlock;
import mtr.mappings.BlockDirectionalMapper;
import mtr.mappings.BlockEntityClientSerializableMapper;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.EntityBlockMapper;
import net.hulan.ivr.IVRBlockEntityTypes;
import net.hulan.ivr.IVRBlocks;
import net.hulan.ivr.packet.IVRPacketTrainDataGuiServer;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.*;

public class BlockClassicalSign extends BlockDirectionalMapper implements EntityBlockMapper, IBlock {

    public static final BooleanProperty LIT = BooleanProperty.of("lit");
    public final int length;
    public final boolean isOdd;
    public static final float SMALL_SIGN_PERCENTAGE = 0.75F;

    public BlockClassicalSign(int length, boolean isOdd) {
        super(Settings.of(Material.METAL, MapColor.GRAY).requiresTool().strength(2.0F).luminance(value -> value.get(LIT) ? 15 : 0));
        this.length = length;
        this.isOdd = isOdd;
        setDefaultState(getDefaultState().with(LIT, false));
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand interactionHand, BlockHitResult hit) {
        return IBlock.checkHoldingBrush(world, player, () -> {
            final Direction facing = IBlock.getStatePropertySafe(state, FACING);
            final Direction hitSide = hit.getSide();
            if (hitSide == facing || hitSide == facing.getOpposite()) {
                final BlockPos checkPos = findEndWithDirection(world, pos, hitSide.getOpposite(), false);
                if (checkPos != null) {
                    IVRPacketTrainDataGuiServer.openClassicalSignScreenS2C((ServerPlayerEntity) player, checkPos);
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final boolean isNext = direction == facing.rotateYClockwise() || state.isOf(IVRBlocks.CLASSICAL_SIGN_MIDDLE.get()) && direction == facing.rotateYCounterclockwise();
        if (isNext && !(newState.getBlock() instanceof BlockClassicalSign) && !equals(IVRBlocks.CLASSICAL_SIGN_1_ODD.get())) {
            return Blocks.AIR.getDefaultState();
        } else {
            return state;
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        final Direction facing = ctx.getPlayerFacing();
        if (equals(IVRBlocks.CLASSICAL_SIGN_1_ODD.get())) return getDefaultState().with(FACING, facing);
        return IBlock.isReplaceable(ctx, facing.rotateYClockwise(), getMiddleLength() + 2) ? getDefaultState().with(FACING, facing) : null;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);

        if (!equals(IVRBlocks.CLASSICAL_SIGN_1_ODD.get())) {
            final BlockPos checkPos = findEndWithDirection(world, pos, facing, true);
            if (checkPos != null) {
                IBlock.onBreakCreative(world, player, checkPos);
            }
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            final Direction facing = IBlock.getStatePropertySafe(state, FACING);
            for (int i = 1; i <= getMiddleLength(); i++) {
                world.setBlockState(pos.offset(facing.rotateYClockwise(), i), IVRBlocks.CLASSICAL_SIGN_MIDDLE.get().getDefaultState().with(FACING, facing), 3);
            }
            if (!equals(IVRBlocks.CLASSICAL_SIGN_1_ODD.get())) {
                world.setBlockState(pos.offset(facing.rotateYClockwise(), getMiddleLength() + 1), getDefaultState().with(FACING, facing.getOpposite()), 3);
            }
            world.updateNeighborsAlways(pos, Blocks.AIR);
            state.updateNeighbors(world, pos, 3);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView blockGetter, BlockPos pos, ShapeContext collisionContext) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        if (state.isOf(IVRBlocks.CLASSICAL_SIGN_MIDDLE.get())) {
            return IBlock.getVoxelShapeByDirection(0, 0.25, 4, 16, 8.75, 12, facing);
        } else {
            final int xStart = getXStart();
            final VoxelShape main, pole;
            if (equals(IVRBlocks.CLASSICAL_SIGN_1_ODD.get())) {
                main = IBlock.getVoxelShapeByDirection(2.25, 0.25, 4, 13.75, 8.75, 12, facing);
                final VoxelShape poleL = IBlock.getVoxelShapeByDirection(6.25, 8, 7.5, 7.25, 16, 8.5, facing), poleR = IBlock.getVoxelShapeByDirection(8.75, 8, 7.5, 9.75, 16, 8.5, facing);
                return VoxelShapes.union(main, poleL, poleR);
            } else {
                main = IBlock.getVoxelShapeByDirection(xStart - 1.75, 0.25, 4, 16, 8.75, 12, facing);
                pole = switch (length % 4) {
                    case 1 -> isOdd ? IBlock.getVoxelShapeByDirection(6.25, 8.75, 7.5, 7.25, 16, 8.5, facing) : IBlock.getVoxelShapeByDirection(14, 8.75, 7.5, 15, 16, 8.5, facing);
                    case 2 -> isOdd ? IBlock.getVoxelShapeByDirection(18.5, 8.75, 7.5, 19.5, 16, 8.5, facing) : IBlock.getVoxelShapeByDirection(9.25, 8.75, 7.5, 10.25, 16, 8.5, facing);
                    case 3 -> isOdd ? IBlock.getVoxelShapeByDirection(14, 8.75, 7.5, 15, 16, 8.5, facing) : IBlock.getVoxelShapeByDirection(6.25, 8.75, 7.5, 7.25, 16, 8.5, facing);
                    default -> isOdd ? IBlock.getVoxelShapeByDirection(9.25, 8.75, 7.5, 10.25, 16, 8.5, facing) : IBlock.getVoxelShapeByDirection(18.5, 8.75, 7.5, 19.5, 16, 8.5, facing);
                };
                return VoxelShapes.union(main, pole);
            }
        }
    }

    @Override
    public String getTranslationKey() {
        return "block.ivr.classical_sign";
    }

    @Override
    public void appendTooltip(ItemStack itemStack, BlockView blockGetter, List<Text> tooltip, TooltipContext tooltipFlag) {
        tooltip.add(mtr.mappings.Text.translatable("tooltip.mtr.railway_sign_length", length).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        tooltip.add(mtr.mappings.Text.translatable(isOdd ? "tooltip.mtr.railway_sign_odd" : "tooltip.mtr.railway_sign_even").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        if (this == IVRBlocks.CLASSICAL_SIGN_MIDDLE.get()) {
            return null;
        } else {
            return new TileEntityClassicalSign(length, isOdd, pos, state);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
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

    private BlockPos findEndWithDirection(World world, BlockPos startPos, Direction direction, boolean allowOpposite) {
        int i = 0;
        final BlockState state = world.getBlockState(startPos);
        if (state.isOf(IVRBlocks.CLASSICAL_SIGN_1_ODD.get())) {
            return startPos;
        }
        while (true) {
            final BlockPos checkPos = startPos.offset(direction.rotateYCounterclockwise(), i);
            final BlockState checkState = world.getBlockState(checkPos);
            if (checkState.getBlock() instanceof BlockClassicalSign) {
                final Direction facing = IBlock.getStatePropertySafe(checkState, FACING);
                if ((!checkState.isOf(IVRBlocks.CLASSICAL_SIGN_MIDDLE.get()) && (facing == direction || allowOpposite && facing == direction.getOpposite()))) {
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
            markDirty();
            syncData();
        }

        @Override
        public void readCompoundTag(NbtCompound compoundTag) {
            selectedIds.clear();
            Arrays.stream(compoundTag.getLongArray(KEY_SELECTED_IDS)).forEach(selectedIds::add);
            for (int i = 0; i < signIds.length; i++) {
                final String signId = compoundTag.getString(KEY_SIGN_LENGTH + i);
                signIds[i] = signId.isEmpty() ? null : signId;
            }
            luminance = compoundTag.getBoolean("luminance");
        }

        @Override
        public void writeCompoundTag(NbtCompound compoundTag) {
            compoundTag.putLongArray(KEY_SELECTED_IDS, new ArrayList<>(selectedIds));
            for (int i = 0; i < signIds.length; i++) {
                compoundTag.putString(KEY_SIGN_LENGTH + i, signIds[i] == null ? "" : signIds[i]);
            }
            compoundTag.putBoolean("luminance", luminance);
        }

        public Box getRenderBoundingBox() {
            return new Box(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        }

        public void setData(Set<Long> selectedIds, String[] signTypes, boolean luminance) {
            this.selectedIds.clear();
            this.selectedIds.addAll(selectedIds);
            if (signIds.length == signTypes.length) {
                System.arraycopy(signTypes, 0, signIds, 0, signTypes.length);
            }
            this.luminance = luminance;
            light();
            markDirty();
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
            BlockPos firstPos = getPos();
            BlockState firstState = getCachedState();
            Direction facing = IBlock.getStatePropertySafe(firstState, FACING);
            BlockClassicalSign sign = (BlockClassicalSign) firstState.getBlock();
            int middleLength = sign.getMiddleLength();
            BlockPos middlePos, lastPos = firstPos.offset(facing.rotateYClockwise(), middleLength + 1);
            if (world != null) {
                BlockState middleState, lastState = world.getBlockState(lastPos);
                TileEntityClassicalSign lastEntity = (TileEntityClassicalSign) world.getBlockEntity(lastPos);
                world.setBlockState(firstPos, firstState.with(LIT, luminance), 3);
                world.getLightingProvider().checkBlock(firstPos);
                for (int i = 1; i <= middleLength + 1; i++) {
                    middlePos = firstPos.offset(facing.rotateYClockwise(), i);
                    middleState = world.getBlockState(middlePos);
                    world.setBlockState(middlePos, middleState.with(LIT, luminance), 3);
                    world.getLightingProvider().checkBlock(middlePos);
                }
                if (!firstState.getBlock().equals(IVRBlocks.CLASSICAL_SIGN_1_ODD.get())) {
                    world.setBlockState(lastPos, lastState.with(LIT, luminance), 3);
                    world.getLightingProvider().checkBlock(lastPos);
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

        public final Identifier textureId;
        public final String customText;
        public final boolean small;
        public final boolean flipTexture;
        public final boolean flipCustomText;
        public final int backgroundColor;

        SignType(String texture, String translation, boolean small, boolean flipTexture, boolean flipCustomText, boolean hasCustomText, int backgroundColor) {
            textureId = new Identifier("mtr:textures/block/sign/" + texture + ".png");
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
