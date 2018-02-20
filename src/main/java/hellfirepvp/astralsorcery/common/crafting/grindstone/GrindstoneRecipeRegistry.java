/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.grindstone;

import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GrindstoneRecipeRegistry
 * Created by HellFirePvP
 * Date: 19.11.2017 / 10:21
 */
public class GrindstoneRecipeRegistry {

    private static final Random rand = new Random();

    public static List<GrindstoneRecipe> recipes = new LinkedList<>();
    public static List<GrindstoneRecipe> mtRecipes = new LinkedList<>();

    private static List<GrindstoneRecipe> localFallback = new LinkedList<>();

    public static GrindstoneRecipe registerGrindstoneRecipe(ItemStack in, ItemStack out, int chance) {
        return registerGrindstoneRecipe(new GrindstoneRecipe(in, out, chance));
    }

    //Used for both CraftTweaker as well as internal registrations
    public static GrindstoneRecipe registerGrindstoneRecipe(GrindstoneRecipe recipe) {
        recipes.add(recipe);
        return recipe;
    }

    public static Collection<GrindstoneRecipe> getValidRecipes() {
        return recipes.stream().filter(GrindstoneRecipe::isValid)
                .filter(r -> !(r instanceof CrystalToolSharpeningRecipe ||
                        r instanceof CrystalSharpeningRecipe || r instanceof SwordSharpeningRecipe))
                .collect(Collectors.toList());
    }

    public static GrindstoneRecipe tryRemoveGrindstoneRecipe(ItemStack matchOut) {
        for (GrindstoneRecipe gr : recipes) {
            if(gr.isValid() && ItemUtils.matchStackLoosely(gr.getOutputForMatching(), matchOut)) {
                recipes.remove(gr);
                return gr;
            }
        }
        return null;
    }

    public static void cacheLocalFallback() {
        if(localFallback.isEmpty()) {
            localFallback.addAll(recipes);
        }
    }

    public static void loadFromFallback() {
        recipes.clear();
        recipes.addAll(localFallback);
    }

    @Nullable
    public static GrindstoneRecipe findMatchingRecipe(ItemStack stackIn) {
        List<GrindstoneRecipe> matching = new LinkedList<>();
        for (GrindstoneRecipe gr : recipes) {
            if(gr.isValid() && gr.matches(stackIn)) {
                matching.add(gr);
            }
        }
        for (GrindstoneRecipe gr : mtRecipes) {
            if(gr.isValid() && gr.matches(stackIn)) {
                matching.add(gr);
            }
        }
        if(matching.isEmpty()) return null;
        return matching.get(rand.nextInt(matching.size()));
    }

}
