package net.hulan.ivr.block;

import mtr.block.IBlock;
import mtr.data.IGui;
import mtr.mappings.BlockDirectionalMapper;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.EntityBlockMapper;
import net.hulan.ivr.IVRBlockEntityTypes;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import java.util.List;

public abstract class BlockKCRStationNameBase extends BlockDirectionalMapper implements EntityBlockMapper, IBlock, IVRBlockEntityTypes {

    public static final IntProperty COLOR = IntProperty.of("color", 0, 2);

    public BlockKCRStationNameBase() {
        super(AbstractBlock.Settings.of(Material.METAL, MapColor.GRAY).requiresTool().strength(2.0F).nonOpaque());
    }


    @Override
    public void appendTooltip(ItemStack itemStack, BlockView blockGetter, List<Text> tooltip, TooltipContext tooltipFlag) {
        tooltip.add(mtr.mappings.Text.translatable("tooltip.mtr.station_color_name").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
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
