package lsc.kaleidoscopeDollWorkshop.registry;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    // 注册创造模式物品栏
    public static final ItemGroup DOLL_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(KaleidoscopeDollWorkshop.MOD_ID, "doll_group"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemGroup.kaleidoscope_doll_workshop.group"))
                    .icon(() -> new ItemStack(ModBlocks.COMPUTER_BLOCK))
                    .entries((displayContext, entries) -> {
                        entries.add(ModBlocks.COMPUTER_BLOCK);
                        entries.add(ModItems.PLAYER_DOLL);
                    }).build());

    public static void registerItemGroups() {
    }
}