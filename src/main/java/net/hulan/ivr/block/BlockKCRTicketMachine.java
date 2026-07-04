package net.hulan.ivr.block;

import mtr.mappings.BlockDirectionalMapper;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class BlockKCRTicketMachine extends BlockDirectionalMapper {

    public BlockKCRTicketMachine() {
        super(Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(2.0F).lightLevel((state) -> 5).noOcclusion());
    }
}
