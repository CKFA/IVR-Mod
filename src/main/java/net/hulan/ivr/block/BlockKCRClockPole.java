package net.hulan.ivr.block;

import mtr.mappings.BlockMapper;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BlockKCRClockPole extends BlockMapper {

    public BlockKCRClockPole() {
        super(Settings.of(Material.METAL).requiresTool().strength(1.0F));
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView blockGetter, BlockPos pos, ShapeContext collisionContext) {
        return Block.createCuboidShape(7.5D, 0.0D, 7.5D, 8.5D, 16.0D, 8.5D);
    }
}
