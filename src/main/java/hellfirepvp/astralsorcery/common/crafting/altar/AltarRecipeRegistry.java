package hellfirepvp.astralsorcery.common.crafting.altar;

import hellfirepvp.astralsorcery.common.crafting.altar.recipes.CrystalToolRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

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

    public static void registerRecipes() {
        registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalSword)
                        .addPart(Items.STICK, ShapedRecipeSlot.LOWER_CENTER)
                        .addPart(ItemsAS.rockCrystal, ShapedRecipeSlot.CENTER, ShapedRecipeSlot.UPPER_CENTER),
                ShapedRecipeSlot.CENTER, ShapedRecipeSlot.UPPER_CENTER));
        registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalShovel)
                        .addPart(Items.STICK, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.rockCrystal, ShapedRecipeSlot.UPPER_CENTER),
                ShapedRecipeSlot.UPPER_CENTER));
        registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalPickaxe)
                        .addPart(Items.STICK, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.rockCrystal, ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.UPPER_RIGHT),
                ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.UPPER_RIGHT));
        registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalAxe)
                        .addPart(Items.STICK, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.rockCrystal, ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.LEFT),
                ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.LEFT));
    }

    public static void registerDiscoveryRecipe(AbstractCacheableRecipe recipe) {
        registerAltarRecipe(new DiscoveryRecipe(recipe));
    }

    public static void registerAltarRecipe(AbstractAltarRecipe recipe) {
        TileAltar.AltarLevel level = recipe.getNeededLevel();
        recipes.get(level).add(recipe);
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
