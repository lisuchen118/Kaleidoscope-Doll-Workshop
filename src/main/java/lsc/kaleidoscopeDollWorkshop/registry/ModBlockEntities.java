package lsc.kaleidoscopeDollWorkshop.registry;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.block.entity.ComputerBlockEntity;
import lsc.kaleidoscopeDollWorkshop.block.entity.DollBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, KaleidoscopeDollWorkshop.MOD_ID);

    // 注册电脑方块实体
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ComputerBlockEntity>> COMPUTER_BE =
            BLOCK_ENTITIES.register("computer_be", () ->
                    BlockEntityType.Builder.of(ComputerBlockEntity::new, ModBlocks.COMPUTER_BLOCK.get()).build(null));

    // 注册玩偶方块实体
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DollBlockEntity>> DOLL_BE =
            BLOCK_ENTITIES.register("doll_be", () ->
                    BlockEntityType.Builder.of(DollBlockEntity::new, ModBlocks.DOLL_BLOCK.get()).build(null));
}