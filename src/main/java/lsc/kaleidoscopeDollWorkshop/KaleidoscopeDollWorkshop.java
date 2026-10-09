package lsc.kaleidoscopeDollWorkshop;

import lsc.kaleidoscopeDollWorkshop.client.ClientModEvents;
import lsc.kaleidoscopeDollWorkshop.network.ModPacketHandler;
import lsc.kaleidoscopeDollWorkshop.registry.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(KaleidoscopeDollWorkshop.MOD_ID)
public class KaleidoscopeDollWorkshop {
    public static final String MOD_ID = "kaleidoscope_doll_workshop";

    public KaleidoscopeDollWorkshop(ModContainer container, IEventBus modEventBus) {
        // 注册模组内容
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        // 注册网络包处理器
        modEventBus.addListener(ModPacketHandler::register);

        // 客户端专用逻辑
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(ClientModEvents::onClientSetup);
            modEventBus.addListener(ClientModEvents::onRegisterScreens);
            modEventBus.addListener(ClientModEvents::onRegisterRenderers);
        }
    }
}