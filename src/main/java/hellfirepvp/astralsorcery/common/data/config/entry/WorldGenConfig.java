/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.entry;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldGenConfig
 * Created by HellFirePvP
 * Date: 19.11.2020 / 20:59
 */
public class WorldGenConfig extends ConfigEntry {

    public static final WorldGenConfig CONFIG = new WorldGenConfig();

    private WorldGenConfig() {
        super("worldgen");
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {}
}
