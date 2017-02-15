/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.infusion;

import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.crafting.infusion.recipes.BasicInfusionRecipe;
import hellfirepvp.astralsorcery.common.crafting.infusion.recipes.LowConsumptionInfusionRecipe;
import hellfirepvp.astralsorcery.common.tile.TileStarlightInfuser;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InfusionRecipeRegistry
 * Created by HellFirePvP
 * Date: 11.12.2016 / 17:19
 */
public class InfusionRecipeRegistry {

    public static List<AbstractInfusionRecipe> recipes = new LinkedList<>();
    private static AbstractInfusionRecipe[] compiledRecipes = null;

    //NEVER call this. this should only get called once at post init to compile all recipes for fast access on both
    //client and serverside!
    //After this is called, changes to recipe registry might break stuff.
    public static void compileRecipes() {
        compiledRecipes = null;

        int totalNeeded = recipes.size();
        int i = 0;
        compiledRecipes = new AbstractInfusionRecipe[totalNeeded];
        for (AbstractInfusionRecipe rec : recipes) {
            compiledRecipes[i] = rec;
            rec.updateUniqueId(i);
            i++;
        }
    }

    @Nullable
    public static AbstractInfusionRecipe getRecipe(int id) {
        if(id < 0 || id >= compiledRecipes.length) return null;
        return compiledRecipes[id];
    }

    public static BasicInfusionRecipe registerBasicInfusion(ItemStack output, ItemStack input) {
        return registerInfusionRecipe(new BasicInfusionRecipe(output, input));
    }

    public static LowConsumptionInfusionRecipe registerLowConsumptionInfusion(ItemStack output, ItemStack input) {
        return registerInfusionRecipe(new LowConsumptionInfusionRecipe(output, input));
    }

    public static <T extends AbstractInfusionRecipe> T registerInfusionRecipe(T recipe) {
        recipes.add(recipe);
        if(CraftingAccessManager.hasCompletedSetup()) {
            CraftingAccessManager.compile();
        }
        return recipe;
    }

    @Nullable
    public static AbstractInfusionRecipe findMatchingRecipe(TileStarlightInfuser ti) {
        for (AbstractInfusionRecipe rec : recipes) {
            if(rec.matches(ti)) {
                return rec;
            }
        }
        return null;
    }

}
