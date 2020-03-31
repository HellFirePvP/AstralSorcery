/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle;

import hellfirepvp.astralsorcery.common.constellation.mantle.effect.*;
import hellfirepvp.astralsorcery.common.data.config.ServerConfig;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MantleEffectRegistry
 * Created by HellFirePvP
 * Date: 19.02.2020 / 20:44
 */
public class MantleEffectRegistry {

    public static void addConfigEntries(ServerConfig config) {
        config.addConfigEntry(MantleEffectAevitas.CONFIG);
        config.addConfigEntry(MantleEffectArmara.CONFIG);
        config.addConfigEntry(MantleEffectBootes.CONFIG);
        config.addConfigEntry(MantleEffectDiscidia.CONFIG);
        config.addConfigEntry(MantleEffectEvorsio.CONFIG);
        config.addConfigEntry(MantleEffectFornax.CONFIG);
        config.addConfigEntry(MantleEffectHorologium.CONFIG);
        config.addConfigEntry(MantleEffectLucerna.CONFIG);
        config.addConfigEntry(MantleEffectMineralis.CONFIG);
        config.addConfigEntry(MantleEffectOctans.CONFIG);
        config.addConfigEntry(MantleEffectPelotrio.CONFIG);
        config.addConfigEntry(MantleEffectVicio.CONFIG);
    }
}
