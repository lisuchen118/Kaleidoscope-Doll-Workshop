package lsc.kaleidoscopeDollWorkshop.registry;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.gui.ComputerScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static ScreenHandlerType<ComputerScreenHandler> COMPUTER_SCREEN_HANDLER;

    public static void registerScreenHandlers() {
        // 使用 ExtendedScreenHandlerType 支持服务端向客户端同步数据
        COMPUTER_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER,
                new Identifier(KaleidoscopeDollWorkshop.MOD_ID, "computer_screen_handler"),
                new ExtendedScreenHandlerType<>(ComputerScreenHandler::new));
    }
}