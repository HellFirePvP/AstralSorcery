/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks;

import hellfirepvp.astralsorcery.common.integrations.ModIntegrationCrafttweaker;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.BaseTweaker;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network.OreTypeAdd;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network.OreTypeRemove;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RitualMineralis
 * Created by HellFirePvP
 * Date: 27.02.2017 / 04:26
 */
@ZenClass("mods.astralsorcery.RitualMineralis")
public class RitualMineralis extends BaseTweaker {

    protected static final String name = "AstralSorcery Ritual Mineralis";

    @ZenMethod
    public static void addOre(String oreDictOreName, double weight) {
        ModIntegrationCrafttweaker.recipeModifications.add(new OreTypeAdd(oreDictOreName, weight));
    }

    @ZenMethod
    public static void removeOre(String oreDictOreName) {
        ModIntegrationCrafttweaker.recipeModifications.add(new OreTypeRemove(oreDictOreName));
    }

}
