package net.hulan.ivr.block;

import mtr.block.IBlock;
import mtr.data.IGui;
import mtr.mappings.BlockDirectionalMapper;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.EntityBlockMapper;
import mtr.mappings.Text;
import net.hulan.ivr.IVRBlockEntityTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.List;

public abstract class BlockKCRStationNameBase extends BlockDirectionalMapper implements EntityBlockMapper, IBlock, IVRBlockEntityTypes {

    public static final IntegerProperty COLOR = IntegerProperty.create("color", 0, 2);

    public BlockKCRStationNameBase() {
        super(Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(2.0F).noOcclusion());
    }

    @Override
    public void appendHoverText(ItemStack itemStack, BlockGetter blockGetter, List<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.add(Text.translatable("tooltip.mtr.station_color_name").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    @Override
    public abstract BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state);

    public abstract static class TileEntityKCRStationNameBase extends BlockEntityMapper implements IGui {
        public final float yOffset;
        public final float zOffset;
        public final boolean isDoubleSided;

        public TileEntityKCRStationNameBase(BlockEntityType<?> type, BlockPos pos, BlockState state, float yOffset, float zOffset, boolean isDoubleSided) {
            super(type, pos, state);
            this.yOffset = yOffset;
            this.zOffset = zOffset;
            this.isDoubleSided = isDoubleSided;
        }

        public abstract int getColor(BlockState state);
    }
}
