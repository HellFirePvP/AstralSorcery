/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.crafting.helper.IHandlerRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.RecipeCraftingContext;
import hellfirepvp.astralsorcery.common.crafting.helper.ResolvingRecipeType;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInfusion;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInfusionContext;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefaction;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.items.IItemHandler;

import static hellfirepvp.astralsorcery.common.lib.RecipeTypesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryRecipeTypes
 * Created by HellFirePvP
 * Date: 13.08.2019 / 19:10
 */
public class RegistryRecipeTypes {

    private RegistryRecipeTypes() {}

    public static void init() {
        TYPE_WELL = new ResolvingRecipeType<>("well", WellLiquefaction.class, (recipe, context) -> recipe.matches(context.getInput()));
        TYPE_INFUSION = new ResolvingRecipeType<>("infusion", LiquidInfusion.class, (recipe, context) -> true);
        TYPE_ALTAR = new ResolvingRecipeType<>("simple_altar", SimpleAltarRecipe.class, (recipe, context) -> recipe.matches(context.getAltar()));
    }

    private static <C extends IItemHandler, T extends IHandlerRecipe<C>, R extends RecipeCraftingContext<T, C>, S extends ResolvingRecipeType<C, T, R>> S register(S recipeType) {
        Registry.register(Registry.RECIPE_TYPE, recipeType.getRegistryName(), recipeType.getType());
        return recipeType;
    }

}
