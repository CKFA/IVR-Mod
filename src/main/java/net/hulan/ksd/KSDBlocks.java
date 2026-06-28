package net.hulan.ksd;

import mtr.RegistryObject;
import net.hulan.ksd.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public interface KSDBlocks {
    RegistryObject<Block> KP_POLE_NORMAL = new RegistryObject<>(() -> new BlockKCRPlatformPole(BlockKCRPlatformPole.Style.NORMAL));
    RegistryObject<Block> KP_POLE_1_SIDE = new RegistryObject<>(() -> new BlockKCRPlatformPole(BlockKCRPlatformPole.Style.SINGLE_SIDE));
    RegistryObject<Block> KP_POLE_2_SIDE = new RegistryObject<>(() -> new BlockKCRPlatformPole(BlockKCRPlatformPole.Style.DOUBLE_SIDE));
    RegistryObject<Block> KP_HORIZONTAL_CROSSBAR = new RegistryObject<>(() -> new BlockKCRPlatformCrossBar(BlockKCRPlatformCrossBar.Style.HORIZONTAL));
    RegistryObject<Block> KP_HORIZONTAL_LOWER_CROSSBAR = new RegistryObject<>(() -> new BlockKCRPlatformCrossBar(BlockKCRPlatformCrossBar.Style.HORIZONTAL_LOWER));
    RegistryObject<Block> KP_HORIZONTAL_ENDING_CROSSBAR = new RegistryObject<>(() -> new BlockKCRPlatformCrossBar(BlockKCRPlatformCrossBar.Style.HORIZONTAL_ENDING));
    RegistryObject<Block> KP_LONGITUDINAL_CROSSBAR = new RegistryObject<>(() -> new BlockKCRPlatformCrossBar(BlockKCRPlatformCrossBar.Style.LONGITUDINAL));
    RegistryObject<Block> KP_HB_WITH_LB = new RegistryObject<>(() -> new BlockKCRPlatformCrossBar(BlockKCRPlatformCrossBar.Style.HB_WITH_LB));
    RegistryObject<Block> KP_HB_WITH_HB = new RegistryObject<>(() -> new BlockKCRPlatformCrossBar(BlockKCRPlatformCrossBar.Style.HB_WITH_HB));
    RegistryObject<Block> KP_CELL_SIDE = new RegistryObject<>(BlockKCRPlatformCellSide::new);
    RegistryObject<Block> KP_CELL_SIDE_WITH_LIGHT = new RegistryObject<>(() -> new BlockKCRPlatformCellSide(BlockBehaviour.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(1).lightLevel(value -> 13).noOcclusion()));
    RegistryObject<Block> KP_CELL_TOP = new RegistryObject<>(BlockKCRPlatformCellTop::new);
    RegistryObject<Block> KP_LIGHT = new RegistryObject<>(BlockKCRPlatformCellLight::new);
    RegistryObject<Block> FIRST_CLASS_PROCESSOR = new RegistryObject<>(BlockKCRFirstClassProcessor::new);
}
