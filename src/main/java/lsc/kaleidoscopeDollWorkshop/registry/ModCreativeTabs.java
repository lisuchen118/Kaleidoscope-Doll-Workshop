package lsc.kaleidoscopeDollWorkshop.registry;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KaleidoscopeDollWorkshop.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DOLL_TAB = CREATIVE_MODE_TABS.register("doll_group",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.kaleidoscope_doll_workshop.group")) // 标签页标题
                    .icon(() -> new ItemStack(ModBlocks.COMPUTER_BLOCK.get())) // 标签页图标
                    .displayItems((parameters, output) -> {
                        // 向标签页添加物品
                        output.accept(ModBlocks.COMPUTER_BLOCK.get());
                        output.accept(ModItems.PLAYER_DOLL_ITEM.get());
                    }).build());
}