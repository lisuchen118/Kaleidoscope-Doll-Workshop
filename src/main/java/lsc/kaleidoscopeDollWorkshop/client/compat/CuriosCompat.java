package lsc.kaleidoscopeDollWorkshop.client.compat;

import lsc.kaleidoscopeDollWorkshop.registry.ModItems;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

/**
 * Curios 客户端兼容入口。
 * 仅当检测到 Curios 模组已加载时才调用, 避免缺失依赖时产生类加载错误。
 */
public class CuriosCompat {

    public static void register() {
        // 将 Curios 渲染器与玩偶物品进行绑定 (注册于 FMLClientSetupEvent)
        CuriosRendererRegistry.register(ModItems.PLAYER_DOLL.get(), DollCurioRenderer::new);
    }
}
