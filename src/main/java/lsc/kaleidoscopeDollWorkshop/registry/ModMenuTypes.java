package lsc.kaleidoscopeDollWorkshop.registry;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.menu.ComputerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, KaleidoscopeDollWorkshop.MOD_ID);

    // 注册电脑菜单类型
    public static final DeferredHolder<MenuType<?>, MenuType<ComputerMenu>> COMPUTER_MENU =
            MENUS.register("computer_menu", () -> IMenuTypeExtension.create(ComputerMenu::new));
}