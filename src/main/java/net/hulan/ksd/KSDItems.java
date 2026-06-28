package net.hulan.ksd;

import mtr.RegistryObject;
import mtr.data.TransportMode;
import net.hulan.ksd.item.*;
import net.minecraft.world.item.Item;

public interface KSDItems {

    RegistryObject<Item> KP_CELL_SIDE_IS = new RegistryObject<>(ItemKCRPlatformCellSideIS::new);
    RegistryObject<Item> KP_CELL_SIDE_OS = new RegistryObject<>(ItemKCRPlatformCellSideOS::new);
    RegistryObject<Item> KP_CELL_SIDE_OS_WITH_LIGHT = new RegistryObject<>(ItemKCRPlatformCellSideOSWithLight::new);
    RegistryObject<Item> KSD_DASHBOARD = new RegistryObject<>(() -> new ItemKSDDashBoard(TransportMode.TRAIN));
    RegistryObject<Item> OCTOPUS = new RegistryObject<>(ItemOctopus::new);
}

