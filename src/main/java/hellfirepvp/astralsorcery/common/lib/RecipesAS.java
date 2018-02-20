/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.CapeAttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationPaperRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipeAdapater;
import hellfirepvp.astralsorcery.common.crafting.helper.SmeltingRecipe;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RecipesAS
 * Created by HellFirePvP
 * Date: 18.06.2017 / 14:37
 */
public class RecipesAS {

    public static Map<IConstellation, ConstellationPaperRecipe> paperCraftingRecipes = new HashMap<>();
    public static Map<IConstellation, CapeAttunementRecipe> capeCraftingRecipes = new HashMap<>();

    //Smelting
    public static SmeltingRecipe rSmeltStarmetalOre;
    public static SmeltingRecipe rSmeltAquamarineOre;

    //CraftingTable recipes
    public static AccessibleRecipeAdapater rMarbleRuned, rMarbleEngraved, rMarbleChiseled, rMarbleArch,
            rMarblePillar, rMarbleBricks, rMarbleStairs, rMarbleSlab;
    public static AccessibleRecipeAdapater rBlackMarbleRaw, rBlackMarbleRuned, rBlackMarbleEngraved,
            rBlackMarbleChiseled, rBlackMarbleArch, rBlackMarblePillar, rBlackMarbleBricks;
    public static AccessibleRecipeAdapater rRJournal;
    public static AccessibleRecipeAdapater rCCParchment;

    //Light Proximity
    public static AccessibleRecipeAdapater rLPRAltar;
    public static AccessibleRecipeAdapater rLPRWand;

}
