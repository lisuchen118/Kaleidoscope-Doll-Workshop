package lsc.kaleidoscopeDollWorkshop.client.compat;

import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import lsc.kaleidoscopeDollWorkshop.registry.ModItems;

/**
 * Accessories 客户端兼容入口。
 * 仅当检测到 Accessories 模组已加载时才调用, 避免缺失依赖时产生类加载错误。
 */
public class AccessoriesCompat {

    public static void register() {
        // 将 Accessories 渲染器与玩偶物品进行绑定
        AccessoriesRendererRegistry.registerRenderer(ModItems.PLAYER_DOLL.get(), DollAccessoryRenderer::new);
    }
}
