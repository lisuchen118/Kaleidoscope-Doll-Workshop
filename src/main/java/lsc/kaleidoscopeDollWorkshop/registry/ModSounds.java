package lsc.kaleidoscopeDollWorkshop.registry;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, KaleidoscopeDollWorkshop.MOD_ID);

    @SuppressWarnings("removal")
    public static final RegistryObject<SoundEvent> DUCK_TOY = SOUND_EVENTS.register("block.duck_toy",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(KaleidoscopeDollWorkshop.MOD_ID, "block.duck_toy")));
}