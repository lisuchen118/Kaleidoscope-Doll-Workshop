package lsc.kaleidoscopeDollWorkshop.registry;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KaleidoscopeDollWorkshop.MOD_ID);

    public static final RegistryObject<CreativeModeTab> DOLL_GROUP = CREATIVE_MODE_TABS.register("doll_group",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModBlocks.COMPUTER_BLOCK.get()))
                    .title(Component.translatable("itemGroup.kaleidoscope_doll_workshop.group"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModBlocks.COMPUTER_BLOCK.get());
                        pOutput.accept(ModItems.PLAYER_DOLL.get());
                    })
                    .build());
}