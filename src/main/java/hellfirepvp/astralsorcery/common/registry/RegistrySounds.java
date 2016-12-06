package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.CategorizedSoundEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static hellfirepvp.astralsorcery.common.lib.Sounds.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistrySounds
 * Created by HellFirePvP
 * Date: 06.12.2016 / 12:54
 */
public class RegistrySounds {

    public static void init() {
        clipSwitch = registerSound("clipSwitch", SoundCategory.BLOCKS);
    }

    private static <T extends SoundEvent> T registerSound(String jsonName, SoundCategory predefinedCategory) {
        return registerSound((T) new CategorizedSoundEvent(new ResourceLocation(AstralSorcery.MODID, jsonName), predefinedCategory));
    }

    private static <T extends SoundEvent> T registerSound(String jsonName) {
        return registerSound((T) new SoundEvent(new ResourceLocation(AstralSorcery.MODID, jsonName)));
    }

    private static <T extends SoundEvent> T registerSound(T soundEvent) {
        GameRegistry.register(soundEvent.setRegistryName(soundEvent.getSoundName()));
        return soundEvent;
    }

}
