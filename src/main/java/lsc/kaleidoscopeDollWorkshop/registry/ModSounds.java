package lsc.kaleidoscopeDollWorkshop.registry;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final Identifier DUCK_TOY_ID = new Identifier(KaleidoscopeDollWorkshop.MOD_ID, "block.duck_toy");
    public static final SoundEvent DUCK_TOY = SoundEvent.of(DUCK_TOY_ID);

    public static void registerSounds() {
        Registry.register(Registries.SOUND_EVENT, DUCK_TOY_ID, DUCK_TOY);
    }
}