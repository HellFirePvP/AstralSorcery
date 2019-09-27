/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.sound.CategorizedSoundEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

import static hellfirepvp.astralsorcery.common.lib.SoundsAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistrySounds
 * Created by HellFirePvP
 * Date: 02.08.2019 / 21:20
 */
public class RegistrySounds {

    private RegistrySounds() {}

    public static void init() {
        BLOCK_COLOREDLENS_ATTACH = registerSound("block_coloredlens_attach", SoundCategory.BLOCKS);
        CRAFT_ATTUNEMENT = registerSound("craft_attunement", SoundCategory.MASTER);
        CRAFT_FINISH = registerSound("craft_finish", SoundCategory.BLOCKS);
        GUI_JOURNAL_CLOSE = registerSound("gui_journal_close", SoundCategory.MASTER);
        GUI_JOURNAL_PAGE = registerSound("gui_journal_page", SoundCategory.MASTER);
    }

    private static <T extends SoundEvent> T registerSound(String jsonName, SoundCategory predefinedCategory) {
        ResourceLocation res = AstralSorcery.key(jsonName);
        CategorizedSoundEvent se = new CategorizedSoundEvent(res, predefinedCategory);
        se.setRegistryName(res);
        return registerSound((T) se);
    }

    private static <T extends SoundEvent> T registerSound(String jsonName) {
        ResourceLocation res = AstralSorcery.key(jsonName);
        SoundEvent se = new SoundEvent(res);
        se.setRegistryName(res);
        return registerSound((T) se);
    }

    private static <T extends SoundEvent> T registerSound(T soundEvent) {
        AstralSorcery.getProxy().getRegistryPrimer().register(soundEvent);
        return soundEvent;
    }

}
