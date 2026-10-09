package lsc.kaleidoscopeDollWorkshop;

import lsc.kaleidoscopeDollWorkshop.renderer.DollBlockEntityRenderer;
import lsc.kaleidoscopeDollWorkshop.renderer.DollItemRenderer;
import lsc.kaleidoscopeDollWorkshop.gui.ComputerScreen;
import lsc.kaleidoscopeDollWorkshop.item.DollItem;
import lsc.kaleidoscopeDollWorkshop.registry.ModBlockEntities;
import lsc.kaleidoscopeDollWorkshop.registry.ModScreenHandlers;
import lsc.kaleidoscopeDollWorkshop.compat.ModTrinketsClientCompat;
import lsc.kaleidoscopeDollWorkshop.compat.ModAccessoriesClientCompat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import software.bernie.geckolib.animatable.client.RenderProvider;

public class KaleidoscopeDollWorkshopClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // 注册 GUI 界面
        HandledScreens.register(ModScreenHandlers.COMPUTER_SCREEN_HANDLER, ComputerScreen::new);

        // 注册方块实体渲染器 (Geo)
        BlockEntityRendererRegistry.register(ModBlockEntities.DOLL_BLOCK_ENTITY, DollBlockEntityRenderer::new);

        // 注册物品动态渲染器
        DollItem.RENDERER_REGISTER = (consumer) -> {
            consumer.accept(new RenderProvider() {
                private final DollItemRenderer renderer = new DollItemRenderer();

                @Override
                public BuiltinModelItemRenderer getCustomRenderer() {
                    return this.renderer;
                }
            });
        };

        // 检测 Trinkets 模组是否已加载，若加载则注册饰品渲染逻辑
        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            ModTrinketsClientCompat.register();
        }

        // 检测 Accessories 模组是否已加载，若加载则注册饰品渲染逻辑
        if (FabricLoader.getInstance().isModLoaded("accessories")) {
            ModAccessoriesClientCompat.register();
        }
    }
}