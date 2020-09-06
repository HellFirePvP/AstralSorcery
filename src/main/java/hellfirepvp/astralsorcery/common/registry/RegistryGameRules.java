/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import net.minecraft.world.GameRules;

import static hellfirepvp.astralsorcery.common.lib.GameRulesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryGameRules
 * Created by HellFirePvP
 * Date: 04.07.2019 / 18:53
 */
public class RegistryGameRules {

    private RegistryGameRules() {}

    public static void init() {
        IGNORE_SKYLIGHT_CHECK_RULE = GameRules.register("asIgnoreSkylightCheck", GameRules.BooleanValue.create(false));
    }

}
