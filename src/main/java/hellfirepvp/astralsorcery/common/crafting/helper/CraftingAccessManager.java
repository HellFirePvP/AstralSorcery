/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CraftingAccessManager
 * Created by HellFirePvP
 * Date: 15.02.2017 / 14:23
 */
public class CraftingAccessManager {

    private static boolean completed = false;

    public static boolean hasCompletedSetup() {
        return completed;
    }

    public static void compile() {
        AltarRecipeRegistry.compileRecipes();
        InfusionRecipeRegistry.compileRecipes();
        completed = true;
    }

}
