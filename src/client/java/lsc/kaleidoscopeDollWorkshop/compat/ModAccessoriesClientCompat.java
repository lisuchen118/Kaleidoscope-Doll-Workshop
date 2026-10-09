package lsc.kaleidoscopeDollWorkshop.compat;

import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import lsc.kaleidoscopeDollWorkshop.registry.ModItems;

public class ModAccessoriesClientCompat {
    public static void register() {
        // 将 Accessories 渲染器与玩偶物品进行绑定
        AccessoriesRendererRegistry.registerRenderer(ModItems.PLAYER_DOLL, DollAccessoryRenderer::new);
    }
}