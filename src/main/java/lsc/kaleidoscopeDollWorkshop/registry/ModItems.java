package lsc.kaleidoscopeDollWorkshop.registry;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.item.DollItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    // 注册玩家玩偶物品，最大堆叠64，可装备在头部
    public static final Item PLAYER_DOLL = registerItem("player_doll",
            new DollItem(new FabricItemSettings()
                    .maxCount(64)
                    .equipmentSlot(stack -> EquipmentSlot.HEAD)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(KaleidoscopeDollWorkshop.MOD_ID, name), item);
    }

    public static void registerModItems() {
    }
}