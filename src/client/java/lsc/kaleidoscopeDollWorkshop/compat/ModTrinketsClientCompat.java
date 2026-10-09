package lsc.kaleidoscopeDollWorkshop.compat;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import lsc.kaleidoscopeDollWorkshop.registry.ModItems;

public class ModTrinketsClientCompat {
    public static void register() {
        // 将 Trinkets 渲染器与玩偶物品进行绑定
        TrinketRendererRegistry.registerRenderer(ModItems.PLAYER_DOLL, new DollTrinketRenderer());
    }
}