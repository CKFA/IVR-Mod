package net.hulan.ivr.block;

import mtr.block.BlockCeilingAuto;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class BlockKCRCeilingAuto extends BlockCeilingAuto {

    public BlockKCRCeilingAuto() {
        super(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.QUARTZ).requiresCorrectToolForDrops().strength(2.0F).lightLevel((state) -> 15));
    }
}
