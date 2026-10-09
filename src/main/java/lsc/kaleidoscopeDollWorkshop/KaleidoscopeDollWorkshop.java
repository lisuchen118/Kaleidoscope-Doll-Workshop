package lsc.kaleidoscopeDollWorkshop;

import lsc.kaleidoscopeDollWorkshop.network.ModMessages;
import lsc.kaleidoscopeDollWorkshop.registry.*;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

public class KaleidoscopeDollWorkshop implements ModInitializer {
	public static final String MOD_ID = "kaleidoscope_doll_workshop";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// 初始化动画库
		GeckoLib.initialize();

		// 注册模组内容
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
		ModSounds.registerSounds();
		ModScreenHandlers.registerScreenHandlers();
		ModMessages.registerC2SPackets();
	}
}