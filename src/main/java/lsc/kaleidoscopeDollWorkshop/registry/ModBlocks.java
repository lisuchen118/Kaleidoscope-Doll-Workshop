package lsc.kaleidoscopeDollWorkshop.registry;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.block.ComputerBlock;
import lsc.kaleidoscopeDollWorkshop.block.DollBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.enums.Instrument;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
    // 注册电脑方块：金属材质，非透明
    public static final Block COMPUTER_BLOCK = registerBlock("computer",
            new ComputerBlock(FabricBlockSettings.create()
                    .instrument(Instrument.BASEDRUM)
                    .sounds(BlockSoundGroup.METAL)
                    .strength(1.25F, 4.2F)
                    .nonOpaque()));

    // 注册玩偶方块：羊毛材质，非透明
    public static final Block DOLL_BLOCK = registerBlockWithoutItem("player_doll",
            new DollBlock(FabricBlockSettings.create()
                    .instrument(Instrument.BASEDRUM)
                    .sounds(BlockSoundGroup.WOOL)
                    .strength(0f, 10f)
                    .nonOpaque()));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(KaleidoscopeDollWorkshop.MOD_ID, name), block);
    }

    private static Block registerBlockWithoutItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(KaleidoscopeDollWorkshop.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, new Identifier(KaleidoscopeDollWorkshop.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks() {
    }
}