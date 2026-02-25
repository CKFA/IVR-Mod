package net.hulan.ivr;

import mtr.RegistryObject;
import net.hulan.ivr.item.ItemKCRPSDAPGBase;
import net.minecraft.world.item.Item;

public interface IVRItems {
    RegistryObject<Item> KCR_APG_DOOR = new RegistryObject<>(() -> new ItemKCRPSDAPGBase(ItemKCRPSDAPGBase.EnumKCRPSDAPGItem.PSD_APG_DOOR, ItemKCRPSDAPGBase.EnumKCRPSDAPGType.APG));
    RegistryObject<Item> KCR_APG_GLASS = new RegistryObject<>(() -> new ItemKCRPSDAPGBase(ItemKCRPSDAPGBase.EnumKCRPSDAPGItem.PSD_APG_GLASS, ItemKCRPSDAPGBase.EnumKCRPSDAPGType.APG));
    RegistryObject<Item> KCR_APG_GLASS_END = new RegistryObject<>(() -> new ItemKCRPSDAPGBase(ItemKCRPSDAPGBase.EnumKCRPSDAPGItem.PSD_APG_GLASS_END, ItemKCRPSDAPGBase.EnumKCRPSDAPGType.APG));
    RegistryObject<Item> KCR_PSD_DOOR_1 = new RegistryObject<>(() -> new ItemKCRPSDAPGBase(ItemKCRPSDAPGBase.EnumKCRPSDAPGItem.PSD_APG_DOOR, ItemKCRPSDAPGBase.EnumKCRPSDAPGType.PSD_1));
    RegistryObject<Item> KCR_PSD_GLASS_1 = new RegistryObject<>(() -> new ItemKCRPSDAPGBase(ItemKCRPSDAPGBase.EnumKCRPSDAPGItem.PSD_APG_GLASS, ItemKCRPSDAPGBase.EnumKCRPSDAPGType.PSD_1));
    RegistryObject<Item> KCR_PSD_GLASS_END_1 = new RegistryObject<>(() -> new ItemKCRPSDAPGBase(ItemKCRPSDAPGBase.EnumKCRPSDAPGItem.PSD_APG_GLASS_END, ItemKCRPSDAPGBase.EnumKCRPSDAPGType.PSD_1));
    RegistryObject<Item> KCR_PSD_DOOR_2 = new RegistryObject<>(() -> new ItemKCRPSDAPGBase(ItemKCRPSDAPGBase.EnumKCRPSDAPGItem.PSD_APG_DOOR, ItemKCRPSDAPGBase.EnumKCRPSDAPGType.PSD_2));
    RegistryObject<Item> KCR_PSD_GLASS_2 = new RegistryObject<>(() -> new ItemKCRPSDAPGBase(ItemKCRPSDAPGBase.EnumKCRPSDAPGItem.PSD_APG_GLASS, ItemKCRPSDAPGBase.EnumKCRPSDAPGType.PSD_2));
    RegistryObject<Item> KCR_PSD_GLASS_END_2 = new RegistryObject<>(() -> new ItemKCRPSDAPGBase(ItemKCRPSDAPGBase.EnumKCRPSDAPGItem.PSD_APG_GLASS_END, ItemKCRPSDAPGBase.EnumKCRPSDAPGType.PSD_2));
}
