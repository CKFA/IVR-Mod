package net.hulan.ivr.block;

import mtr.mappings.BlockDirectionalMapper;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import org.jetbrains.annotations.Nullable;

public class BlockKCRPlatform extends BlockDirectionalMapper {

    public BlockKCRPlatform() {
        super(Settings.of(Material.METAL, MapColor.YELLOW).requiresTool().strength(2.0F));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayer().getHorizontalFacing());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
