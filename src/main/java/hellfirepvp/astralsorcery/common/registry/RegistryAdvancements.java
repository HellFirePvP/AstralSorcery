/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.advancement.*;

import static hellfirepvp.astralsorcery.common.lib.AdvancementsAS.*;
import static net.minecraft.advancements.CriteriaTriggers.register;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryAdvancements
 * Created by HellFirePvP
 * Date: 11.05.2020 / 20:34
 */
public class RegistryAdvancements {

    public static void init() {
        register(DISCOVER_CONSTELLATION = new DiscoverConstellationTrigger());
        register(ATTUNE_SELF = new AttuneSelfTrigger());
        register(ATTUNE_CRYSTAL = new AttuneCrystalTrigger());
        register(ALTAR_CRAFT = new AltarCraftTrigger());
        register(PERK_LEVEL = new PerkLevelTrigger());
    }

}
