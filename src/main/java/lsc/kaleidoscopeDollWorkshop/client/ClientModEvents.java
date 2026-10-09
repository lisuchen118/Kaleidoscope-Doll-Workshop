package lsc.kaleidoscopeDollWorkshop.client;

import lsc.kaleidoscopeDollWorkshop.client.compat.AccessoriesCompat;
import lsc.kaleidoscopeDollWorkshop.client.compat.CuriosCompat;
import lsc.kaleidoscopeDollWorkshop.client.gui.ComputerScreen;
import lsc.kaleidoscopeDollWorkshop.client.renderer.DollBlockEntityRenderer;
import lsc.kaleidoscopeDollWorkshop.registry.ModBlockEntities;
import lsc.kaleidoscopeDollWorkshop.registry.ModMenuTypes;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ClientModEvents {

    // 客户端初始化: 检测并注册可选饰品栏 (Curios / Accessories) 的帽子槽位渲染
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 检测 Curios 模组是否已加载，若加载则注册饰品(帽子槽位)渲染逻辑
        if (ModList.get().isLoaded("curios")) {
            CuriosCompat.register();
        }

        // 检测 Accessories 模组是否已加载，若加载则注册饰品(帽子槽位)渲染逻辑
        if (ModList.get().isLoaded("accessories")) {
            AccessoriesCompat.register();
        }
    }

    // 注册 GUI 屏幕与菜单类型的绑定
    public static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.COMPUTER_MENU.get(), ComputerScreen::new);
    }

    // 注册方块实体渲染器
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.DOLL_BE.get(), DollBlockEntityRenderer::new);
    }
}