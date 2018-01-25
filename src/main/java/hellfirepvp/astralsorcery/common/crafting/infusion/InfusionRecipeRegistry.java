/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.infusion;

import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.crafting.infusion.recipes.BasicInfusionRecipe;
import hellfirepvp.astralsorcery.common.crafting.infusion.recipes.LowConsumptionInfusionRecipe;
import hellfirepvp.astralsorcery.common.tile.TileStarlightInfuser;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Iterator;
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

    public static List<AbstractInfusionRecipe> mtRecipes = new LinkedList<>();
    public static List<AbstractInfusionRecipe> recipes = new LinkedList<>();
    private static AbstractInfusionRecipe[] compiledRecipes = null;

    private static List<AbstractInfusionRecipe> localFallbackCache = new LinkedList<>();

    //NEVER call this. this should only get called once at post init to compile all recipes for fast access on both
    //client and serverside!
    //After this is called, changes to recipe registry might break stuff.
    public static void compileRecipes() {
        compiledRecipes = null;

        int totalNeeded = recipes.size() + mtRecipes.size();
        int i = 0;
        compiledRecipes = new AbstractInfusionRecipe[totalNeeded];
        for (AbstractInfusionRecipe rec : recipes) {
            compiledRecipes[i] = rec;
            rec.updateUniqueId(i);
            i++;
        }
        for (AbstractInfusionRecipe rec : mtRecipes) {
            compiledRecipes[i] = rec;
            rec.updateUniqueId(i);
            i++;
        }

    }

    public static void cacheLocalRecipes() {
        if(localFallbackCache.isEmpty()) {
            localFallbackCache.addAll(recipes);
        }
    }

    public static void loadFromFallback() {
        if(!localFallbackCache.isEmpty()) {
            recipes.addAll(localFallbackCache);
        }
    }

    @Nullable
    public static AbstractInfusionRecipe getRecipe(int id) {
        if(id < 0 || id >= compiledRecipes.length) return null;
        return compiledRecipes[id];
    }

    /*
     * Returns the Recipe that was removed if successful.
     */
    @Nullable
    public static AbstractInfusionRecipe removeFindRecipeByOutput(ItemStack output) {
        Iterator<AbstractInfusionRecipe> iterator = recipes.iterator();
        while (iterator.hasNext()) {
            AbstractInfusionRecipe recipe = iterator.next();
            ItemStack out = recipe.getOutputForMatching();
            if (!out.isEmpty() && ItemUtils.matchStackLoosely(recipe.getOutputForMatching(), output)) {
                iterator.remove();
                return recipe;
            }
        }
        iterator = mtRecipes.iterator();
        while (iterator.hasNext()) {
            AbstractInfusionRecipe recipe = iterator.next();
            ItemStack out = recipe.getOutputForMatching();
            if (!out.isEmpty() && ItemUtils.matchStackLoosely(recipe.getOutputForMatching(), output)) {
                iterator.remove();
                return recipe;
            }
        }
        return null;
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
        for (AbstractInfusionRecipe rec : mtRecipes) {
            if(rec.matches(ti)) {
                return rec;
            }
        }
        return null;
    }

}
