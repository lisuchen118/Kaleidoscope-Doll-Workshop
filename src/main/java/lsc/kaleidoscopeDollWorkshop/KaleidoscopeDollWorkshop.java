package lsc.kaleidoscopeDollWorkshop;

import lsc.kaleidoscopeDollWorkshop.network.ModMessages;
import lsc.kaleidoscopeDollWorkshop.registry.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib.GeckoLib;

@Mod(KaleidoscopeDollWorkshop.MOD_ID)
public class KaleidoscopeDollWorkshop {
    public static final String MOD_ID = "kaleidoscope_doll_workshop";

    @SuppressWarnings("removal")
    public KaleidoscopeDollWorkshop() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 初始化 GeckoLib 动画库
        GeckoLib.initialize();

        // 注册模组内容到事件总线
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModTabs.CREATIVE_MODE_TABS.register(modEventBus);

        // 注册网络通信渠道
        ModMessages.register();

        // 注册到 Forge 全局事件总线
        MinecraftForge.EVENT_BUS.register(this);
    }
}