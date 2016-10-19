package hellfirepvp.astralsorcery.common.crafting.altar;

import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttenuationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarRecipeRegistry
 * Created by HellFirePvP
 * Date: 22.09.2016 / 13:13
 */
public class AltarRecipeRegistry {

    public static Map<TileAltar.AltarLevel, List<AbstractAltarRecipe>> recipes = new HashMap<>();
    private static AbstractAltarRecipe[] compiledRecipeArray = null;

    //NEVER call this. this should only get called once at post init to compile all recipes for fast access.
    //After this is called, changes to recipe registry might break stuff.
    public static void compileRecipes() {
        compiledRecipeArray = null;

        int totalNeeded = 0;
        for (TileAltar.AltarLevel level : recipes.keySet()) {
            totalNeeded += recipes.get(level).size();
        }
        int i = 0;
        compiledRecipeArray = new AbstractAltarRecipe[totalNeeded];
        for (TileAltar.AltarLevel l : TileAltar.AltarLevel.values()) {
            List<AbstractAltarRecipe> recipeList = recipes.get(l);
            for (AbstractAltarRecipe rec : recipeList) {
                compiledRecipeArray[i] = rec;
                rec.updateUniqueId(i);
                i++;
            }
        }
    }

    @Nullable
    public static AbstractAltarRecipe getRecipe(int id) {
        if(id < 0 || id >= compiledRecipeArray.length) return null;
        return compiledRecipeArray[id];
    }

    public static AttenuationRecipe registerAttenuationRecipe(AbstractCacheableRecipe recipe) {
        AttenuationRecipe dr = new AttenuationRecipe(recipe);
        registerAltarRecipe(dr);
        return dr;
    }

    public static DiscoveryRecipe registerDiscoveryRecipe(AbstractCacheableRecipe recipe) {
        DiscoveryRecipe dr = new DiscoveryRecipe(recipe);
        registerAltarRecipe(dr);
        return dr;
    }

    public static <T extends AbstractAltarRecipe> T registerAltarRecipe(T recipe) {
        TileAltar.AltarLevel level = recipe.getNeededLevel();
        recipes.get(level).add(recipe);
        return recipe;
    }

    @Nullable
    public static AbstractAltarRecipe findMatchingRecipe(TileAltar ta) {
        TileAltar.AltarLevel level = ta.getAltarLevel();
        List<TileAltar.AltarLevel> levels = new ArrayList<>();
        List<AbstractAltarRecipe> validRecipes = new LinkedList<>();
        for (int i = 0; i < level.ordinal() + 1; i++) {
            levels.add(TileAltar.AltarLevel.values()[i]);
        }
        for (TileAltar.AltarLevel valid : levels) {
            validRecipes.addAll(recipes.get(valid));
        }
        for (AbstractAltarRecipe recipe : validRecipes) {
            if(recipe.matches(ta)) {
                return recipe;
            }
        }
        return null;
    }

    static {
        for (TileAltar.AltarLevel al : TileAltar.AltarLevel.values()) {
            recipes.put(al, new LinkedList<>());
        }
    }

}
