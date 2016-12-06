package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.SoundUtils;
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
        attunement = registerSound("attunement", SoundCategory.MASTER, 63);
    }

    private static <T extends SoundEvent> T registerSound(String jsonName, SoundCategory predefinedCategory, int tickLength) {
        ResourceLocation res = new ResourceLocation(AstralSorcery.MODID, jsonName);
        SoundUtils.LoopableSoundEvent se = new SoundUtils.LoopableSoundEvent(res, predefinedCategory, tickLength);
        se.setRegistryName(res);
        return registerSound((T) se);
    }

    private static <T extends SoundEvent> T registerSound(String jsonName, SoundCategory predefinedCategory) {
        ResourceLocation res = new ResourceLocation(AstralSorcery.MODID, jsonName);
        SoundUtils.CategorizedSoundEvent se = new SoundUtils.CategorizedSoundEvent(res, predefinedCategory);
        se.setRegistryName(res);
        return registerSound((T) se);
    }

    private static <T extends SoundEvent> T registerSound(String jsonName) {
        ResourceLocation res = new ResourceLocation(AstralSorcery.MODID, jsonName);
        SoundEvent se = new SoundEvent(res);
        se.setRegistryName(res);
        return registerSound((T) se);
    }

    private static <T extends SoundEvent> T registerSound(T soundEvent) {
        GameRegistry.register(soundEvent);
        return soundEvent;
    }

}
