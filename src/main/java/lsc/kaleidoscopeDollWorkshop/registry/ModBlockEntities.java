package lsc.kaleidoscopeDollWorkshop.registry;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.block.entity.ComputerBlockEntity;
import lsc.kaleidoscopeDollWorkshop.block.entity.DollBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<ComputerBlockEntity> COMPUTER_BLOCK_ENTITY;
    public static BlockEntityType<DollBlockEntity> DOLL_BLOCK_ENTITY;

    public static void registerBlockEntities() {
        COMPUTER_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(KaleidoscopeDollWorkshop.MOD_ID, "computer_be"),
                FabricBlockEntityTypeBuilder.create(ComputerBlockEntity::new, ModBlocks.COMPUTER_BLOCK).build()
        );

        DOLL_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(KaleidoscopeDollWorkshop.MOD_ID, "doll_block_entity"),
                FabricBlockEntityTypeBuilder.create(DollBlockEntity::new, ModBlocks.DOLL_BLOCK).build()
        );
    }
}