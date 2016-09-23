package hellfirepvp.astralsorcery.common.crafting.altar;

import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.init.Blocks;

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

    public static void registerRecipes() {
        registerDiscoveryRecipe(new ShapedRecipe(Blocks.GOLD_BLOCK).addPart(Blocks.STONE, ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.LEFT, ShapedRecipeSlot.LOWER_RIGHT));
    }

    public static void registerDiscoveryRecipe(AbstractCacheableRecipe recipe) {
        registerAltarRecipe(new AbstractAltarRecipe.DiscoveryRecipe(recipe));
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
