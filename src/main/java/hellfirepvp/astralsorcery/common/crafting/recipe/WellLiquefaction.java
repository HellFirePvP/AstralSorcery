/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe;

import hellfirepvp.astralsorcery.common.crafting.helper.CustomMatcherRecipe;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileWell;
import hellfirepvp.astralsorcery.common.util.tile.TileInventory;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WellLiquefaction
 * Created by HellFirePvP
 * Date: 30.06.2019 / 23:30
 */
public class WellLiquefaction extends CustomMatcherRecipe {

    private final Ingredient input;
    private final FluidStack output;

    public WellLiquefaction(ResourceLocation recipeId, Ingredient input, FluidStack output) {
        super(recipeId);
        this.input = input;
        this.output = output;
    }

    public boolean matches(TileWell tile) {
        return this.input.test(tile.getInventory().getStackInSlot(0));
    }

    public Ingredient getInput() {
        return input;
    }

    public FluidStack getFluidOutput() {
        return output;
    }

    @Override
    public IRecipeType<?> getType() {
        return (IRecipeType<?>) RecipeTypesAS.TYPE_WELL;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializersAS.WELL_LIQUEFACTION_SERIALIZER;
    }
}
