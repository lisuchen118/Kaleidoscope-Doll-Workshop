package lsc.kaleidoscopeDollWorkshop.registry;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.block.ComputerBlock;
import lsc.kaleidoscopeDollWorkshop.block.DollBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, KaleidoscopeDollWorkshop.MOD_ID);

    // 注册工坊电脑方块
    public static final RegistryObject<Block> COMPUTER_BLOCK = registerBlock("computer",
            () -> new ComputerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.25F, 4.2F).sound(SoundType.METAL).noOcclusion()));

    // 注册玩偶方块
    public static final RegistryObject<Block> DOLL_BLOCK = registerBlock("player_doll",
            () -> new DollBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOL).strength(0f, 10f).sound(SoundType.WOOL).noOcclusion()));

    // 辅助方法：同时注册方块和对应的物品
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        // player_doll 有特殊的 Item 类，在此跳过自动注册
        if(name.equals("player_doll")) return;
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}