/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.integrations.IntegrationBase;

import static hellfirepvp.astralsorcery.common.lib.Integrations.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryIntegrations
 * Created by HellFirePvP
 * Date: 10.01.2017 / 23:16
 */
public class RegistryIntegrations {

    private static final String defaultDirectory = "hellfirepvp.astralsorcery.common.integrations.mods.";

    //During post init
    public static void init() {

    }

    private static IntegrationBase initDefaultIntegration(String modid, String defaultDirName) {
        IntegrationBase i = new IntegrationBase(modid, defaultDirectory + defaultDirName);
        if(i.instantiateAndInit()) {
            AstralSorcery.log.info("Loaded integration for " + modid);
        } else {
            AstralSorcery.log.info("Could not find mod " + modid + " - didn't load integration.");
        }
        return i;
    }

}
