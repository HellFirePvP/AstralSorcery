/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.mantle.effect.MantleEffectOctans;
import hellfirepvp.astralsorcery.common.constellation.mantle.effect.MantleEffectVicio;
import hellfirepvp.astralsorcery.common.data.config.ServerConfig;
import net.minecraftforge.common.MinecraftForge;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MantleEffectRegistry
 * Created by HellFirePvP
 * Date: 19.02.2020 / 20:44
 */
public class MantleEffectRegistry {

    public static void addConfigEntries(ServerConfig config) {
        config.addConfigEntry(MantleEffectOctans.CONFIG);
        config.addConfigEntry(MantleEffectVicio.CONFIG);
    }

    public static void setup(MantleEffect effect) {
        effect.attachEventListeners(MinecraftForge.EVENT_BUS);
        effect.attachTickHandlers(AstralSorcery.getProxy().getTickManager()::register);
        effect.init();
    }
}
