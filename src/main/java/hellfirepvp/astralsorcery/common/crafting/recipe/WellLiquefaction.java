/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe;

import hellfirepvp.astralsorcery.common.crafting.helper.CustomMatcherRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeSerializer;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WellLiquefaction
 * Created by HellFirePvP
 * Date: 30.06.2019 / 23:30
 */
public class WellLiquefaction extends CustomMatcherRecipe {

    private final Color catalystColor;
    private final Ingredient input;
    private final Fluid output;

    private final float productionMultiplier;
    private final float shatterMultiplier;

    public WellLiquefaction(ResourceLocation recipeId, Ingredient input, Fluid output, float productionMultiplier, float shatterMultiplier) {
        this(recipeId, input, output, null, productionMultiplier, shatterMultiplier);
    }

    public WellLiquefaction(ResourceLocation recipeId, Ingredient input, Fluid output, @Nullable Color catalystColor, float productionMultiplier, float shatterMultiplier) {
        super(recipeId);
        this.input = input;
        this.output = output;
        this.catalystColor = catalystColor;
        this.productionMultiplier = productionMultiplier;
        this.shatterMultiplier = shatterMultiplier;
    }

    public boolean matches(ItemStack input) {
        return this.input.test(input);
    }

    @Nonnull
    public Ingredient getInput() {
        return input;
    }

    @Nonnull
    public Fluid getFluidOutput() {
        return output;
    }

    @Nullable
    public Color getCatalystColor() {
        return catalystColor;
    }

    public float getProductionMultiplier() {
        return productionMultiplier;
    }

    public float getShatterMultiplier() {
        return shatterMultiplier;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypesAS.TYPE_WELL.getType();
    }

    @Override
    public CustomRecipeSerializer<?> getSerializer() {
        return RecipeSerializersAS.WELL_LIQUEFACTION_SERIALIZER;
    }
}
