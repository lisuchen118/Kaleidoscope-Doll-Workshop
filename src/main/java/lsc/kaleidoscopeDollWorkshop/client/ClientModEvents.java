package lsc.kaleidoscopeDollWorkshop.client;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.client.compat.AccessoriesCompat;
import lsc.kaleidoscopeDollWorkshop.client.compat.CuriosCompat;
import lsc.kaleidoscopeDollWorkshop.client.renderer.DollBlockEntityRenderer;
import lsc.kaleidoscopeDollWorkshop.client.screen.ComputerScreen;
import lsc.kaleidoscopeDollWorkshop.registry.ModBlockEntities;
import lsc.kaleidoscopeDollWorkshop.registry.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = KaleidoscopeDollWorkshop.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 注册屏幕菜单
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.COMPUTER_MENU.get(), ComputerScreen::new);
        });

        // 检测 Curios 模组是否已加载，若加载则注册饰品(帽子槽位)渲染逻辑
        if (ModList.get().isLoaded("curios")) {
            CuriosCompat.register();
        }

        // 检测 Accessories 模组是否已加载，若加载则注册饰品(帽子槽位)渲染逻辑
        if (ModList.get().isLoaded("accessories")) {
            AccessoriesCompat.register();
        }
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 注册方块实体渲染器
        event.registerBlockEntityRenderer(ModBlockEntities.DOLL_BLOCK_ENTITY.get(), DollBlockEntityRenderer::new);
    }
}