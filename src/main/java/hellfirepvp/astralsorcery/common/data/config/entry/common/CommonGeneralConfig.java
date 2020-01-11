/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.entry.common;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommonGeneralConfig
 * Created by HellFirePvP
 * Date: 20.04.2019 / 13:26
 */
public class CommonGeneralConfig extends ConfigEntry {

    public static final CommonGeneralConfig CONFIG = new CommonGeneralConfig();

    private CommonGeneralConfig() {
        super("general");
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {

    }

}
