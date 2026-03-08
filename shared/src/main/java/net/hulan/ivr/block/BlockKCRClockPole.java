package net.hulan.ivr.block;

import mtr.mappings.BlockMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BlockKCRClockPole extends BlockMapper {

    public BlockKCRClockPole() {
        super(Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(1.0F));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        return Block.box(7.5D, 0.0D, 7.5D, 8.5D, 16.0D, 8.5D);
    }
}
