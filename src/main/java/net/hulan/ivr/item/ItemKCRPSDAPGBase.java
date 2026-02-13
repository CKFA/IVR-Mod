package net.hulan.ivr.item;

import mtr.Blocks;
import mtr.block.BlockPSDAPGBase;
import mtr.block.IBlock;
import mtr.block.ITripleBlock;
import mtr.item.ItemWithCreativeTabBase;
import net.hulan.ivr.IVRBlocks;
import net.hulan.ivr.IVRCreativeModTabs;
import net.hulan.ivr.block.BlockKCRPSDTop;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class ItemKCRPSDAPGBase extends ItemWithCreativeTabBase implements IBlock, IVRBlocks {
    
    private final ItemKCRPSDAPGBase.EnumKCRPSDAPGItem item;
    private final ItemKCRPSDAPGBase.EnumKCRPSDAPGType type;

    public ItemKCRPSDAPGBase(ItemKCRPSDAPGBase.EnumKCRPSDAPGItem item, ItemKCRPSDAPGBase.EnumKCRPSDAPGType type) {
        super(IVRCreativeModTabs.IVR_);
        this.item = item;
        this.type = type;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        int horizontalBlocks = this.item.isDoor ? (this.type.isOdd ? 3 : 2) : 1;
        if (blocksNotReplaceable(context, horizontalBlocks, this.type.isPSD ? 3 : 2, this.getBlockStateFromItem().getBlock())) {
            return ActionResult.FAIL;
        } else {
            World world = context.getWorld();
            Direction playerFacing = context.getPlayerFacing();
            BlockPos pos = context.getBlockPos().offset(context.getSide());

            for(int x = 0; x < horizontalBlocks; ++x) {
                BlockPos newPos = pos.offset(playerFacing.rotateYClockwise(), x);

                for(int y = 0; y < 2; ++y) {
                    BlockState state = this.getBlockStateFromItem().with(BlockPSDAPGBase.FACING, playerFacing).with(HALF, y == 1 ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER);
                    if (this.item.isDoor) {
                        BlockState newState = state.with(SIDE, x == 0 ? EnumSide.LEFT : EnumSide.RIGHT);
                        if (this.type.isOdd) {
                            newState = newState.with(ITripleBlock.ODD, x > 0 && x < horizontalBlocks - 1);
                        }

                        world.setBlockState(newPos.up(y), newState);
                    } else {
                        world.setBlockState(newPos.up(y), state.with(SIDE_EXTENDED, EnumSide.SINGLE));
                    }
                }

                if (this.type.isPSD) {
                    world.setBlockState(newPos.up(2), BlockKCRPSDTop.getActualState(world, newPos.up(2)));
                }
            }

            context.getStack().decrement(1);
            return ActionResult.SUCCESS;
        }
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World level, List<Text> tooltip, TooltipContext tooltipFlag) {
        tooltip.add(mtr.mappings.Text.translatable(this.type.isLift ? (this.type.isOdd ? "tooltip.mtr.railway_sign_odd" : "tooltip.mtr.railway_sign_even") : "tooltip.mtr." + this.item.asString()).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
    }

    private BlockState getBlockStateFromItem() {
        return switch (this.type) {
            case PSD_1 -> switch (this.item) {
                case PSD_APG_DOOR -> KCR_PSD_DOOR_1.get().getDefaultState();
                case PSD_APG_GLASS -> KCR_PSD_GLASS_1.get().getDefaultState();
                case PSD_APG_GLASS_END -> KCR_PSD_GLASS_END_1.get().getDefaultState();
            };
            case PSD_2 -> switch (this.item) {
                case PSD_APG_DOOR -> KCR_PSD_DOOR_2.get().getDefaultState();
                case PSD_APG_GLASS -> KCR_PSD_GLASS_2.get().getDefaultState();
                case PSD_APG_GLASS_END -> KCR_PSD_GLASS_END_2.get().getDefaultState();
            };
            case APG -> switch (this.item) {
                case PSD_APG_DOOR -> KCR_APG_DOOR.get().getDefaultState();
                case PSD_APG_GLASS -> KCR_APG_GLASS.get().getDefaultState();
                case PSD_APG_GLASS_END -> KCR_APG_GLASS_END.get().getDefaultState();
            };
            case LIFT_DOOR_1 -> Blocks.LIFT_DOOR_EVEN_1.get().getDefaultState();
            case LIFT_DOOR_ODD_1 -> Blocks.LIFT_DOOR_ODD_1.get().getDefaultState();
        };
    }

    public static boolean blocksNotReplaceable(ItemUsageContext context, int width, int height, Block blacklistBlock) {
        Direction facing = context.getPlayerFacing();
        World world = context.getWorld();
        BlockPos startingPos = context.getBlockPos().offset(context.getSide());

        for(int x = 0; x < width; ++x) {
            BlockPos offsetPos = startingPos.offset(facing.rotateYClockwise(), x);
            if (blacklistBlock != null) {
                boolean isBlacklistedBelow = world.getBlockState(offsetPos.down()).isOf(blacklistBlock);
                boolean isBlacklistedAbove = world.getBlockState(offsetPos.up(height)).isOf(blacklistBlock);
                if (isBlacklistedBelow || isBlacklistedAbove) {
                    return true;
                }
            }

            for(int y = 0; y < height; ++y) {
                if (!world.getBlockState(offsetPos.up(y)).getMaterial().isReplaceable()) {
                    return true;
                }
            }
        }

        return false;
    }

    public enum EnumKCRPSDAPGType {
        PSD_1(true, false, false),
        PSD_2(true, false, false),
        APG(false, false, false),
        LIFT_DOOR_1(false, false, true),
        LIFT_DOOR_ODD_1(false, true, true);

        private final boolean isPSD;
        private final boolean isOdd;
        private final boolean isLift;

        EnumKCRPSDAPGType(boolean isPSD, boolean isOdd, boolean isLift) {
            this.isPSD = isPSD;
            this.isOdd = isOdd;
            this.isLift = isLift;
        }
    }

    public enum EnumKCRPSDAPGItem implements StringIdentifiable {
        PSD_APG_DOOR("psd_apg_door", true),
        PSD_APG_GLASS("psd_apg_glass", false),
        PSD_APG_GLASS_END("psd_apg_glass_end", false);

        private final String name;
        private final boolean isDoor;

        EnumKCRPSDAPGItem(String name, boolean isDoor) {
            this.name = name;
            this.isDoor = isDoor;
        }

        public String asString() {
            return this.name;
        }
    }
}
