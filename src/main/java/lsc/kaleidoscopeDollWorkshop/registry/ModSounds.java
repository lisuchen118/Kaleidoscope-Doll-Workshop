package lsc.kaleidoscopeDollWorkshop.registry;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, KaleidoscopeDollWorkshop.MOD_ID);

    // 注册玩偶音效
    public static final DeferredHolder<SoundEvent, SoundEvent> DUCK_TOY = SOUNDS.register("block.duck_toy",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(KaleidoscopeDollWorkshop.MOD_ID, "block.duck_toy")));
}