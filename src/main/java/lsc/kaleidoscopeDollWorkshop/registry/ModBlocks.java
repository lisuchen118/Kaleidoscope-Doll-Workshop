package lsc.kaleidoscopeDollWorkshop.registry;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.block.ComputerBlock;
import lsc.kaleidoscopeDollWorkshop.block.DollBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(KaleidoscopeDollWorkshop.MOD_ID);

    // 注册工坊电脑方块
    public static final DeferredBlock<ComputerBlock> COMPUTER_BLOCK = registerBlock("computer",
            () -> new ComputerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).strength(1.25F, 4.2F).noOcclusion()));

    // 注册玩家玩偶方块
    public static final DeferredBlock<DollBlock> DOLL_BLOCK = BLOCKS.register("player_doll",
            () -> new DollBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOL).instrument(NoteBlockInstrument.BASEDRUM).strength(0.5f, 1.0f).noOcclusion()));

    // 辅助方法：注册方块的同时注册对应的方块物品 (BlockItem)
    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties()));
        return toReturn;
    }
}