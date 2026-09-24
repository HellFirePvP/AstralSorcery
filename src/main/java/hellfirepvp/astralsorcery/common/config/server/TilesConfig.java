/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.config.server;

import hellfirepvp.astralsorcery.common.config.ConfigEntry;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TilesConfig
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TilesConfig extends ConfigEntry {
    
    public static final TilesConfig CONFIG = new TilesConfig();
    
    private TilesConfig() {
        super("tiles");
    }

    @Override
    public void createEntries(ModConfigSpec.Builder cfgBuilder) {}
}
