package lsc.kaleidoscopeDollWorkshop.registry;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.item.DollItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(KaleidoscopeDollWorkshop.MOD_ID);

    // 注册自定义的玩偶物品 DollItem
    public static final DeferredItem<DollItem> PLAYER_DOLL_ITEM = ITEMS.register("player_doll",
            () -> new DollItem(new Item.Properties().stacksTo(64)));
}