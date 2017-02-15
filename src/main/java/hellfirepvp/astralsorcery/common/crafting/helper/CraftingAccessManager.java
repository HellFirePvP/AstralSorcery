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
 * This class is part of the 1.11.2 port of Reika's mods.
 * Original code for this project on Minecraft 1.7.10
 * is available under the same licence on Github:
 * https://github.com/ReikaKalseki/DragonAPI
 * Class: CraftingAccessManager
 * Author: HellFirePvP
 * Owner & Author: Reika Kalseki
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
