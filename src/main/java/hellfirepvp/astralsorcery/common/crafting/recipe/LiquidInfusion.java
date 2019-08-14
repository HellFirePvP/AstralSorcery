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
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidInfusion
 * Created by HellFirePvP
 * Date: 26.07.2019 / 21:29
 */
public class LiquidInfusion extends CustomMatcherRecipe {

    private final Fluid liquidInput;
    private final Ingredient itemInput;
    private final Ingredient itemOutput;

    private final float consumptionChance;
    private final boolean consumeMultipleFluids;
    private final boolean acceptChaliceInput;

    public LiquidInfusion(ResourceLocation recipeId, Fluid liquidInput, Ingredient itemInput, Ingredient itemOutput, float consumptionChance, boolean consumeMultipleFluids, boolean acceptChaliceInput) {
        super(recipeId);
        this.liquidInput = liquidInput;
        this.itemInput = itemInput;
        this.itemOutput = itemOutput;
        this.consumptionChance = consumptionChance;
        this.consumeMultipleFluids = consumeMultipleFluids;
        this.acceptChaliceInput = acceptChaliceInput;
    }

    @Nonnull
    public Fluid getLiquidInput() {
        return liquidInput;
    }

    @Nonnull
    public Ingredient getItemInput() {
        return itemInput;
    }

    @Nonnull
    public Ingredient getItemOutput() {
        return itemOutput;
    }

    public float getConsumptionChance() {
        return MathHelper.clamp(consumptionChance, 0F, 1F);
    }

    public boolean doesConsumeMultipleFluids() {
        return consumeMultipleFluids;
    }

    public boolean acceptsChaliceInput() {
        return acceptChaliceInput;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypesAS.TYPE_INFUSION.getType();
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializersAS.LIQUID_INFUSION_SERIALIZER;
    }
}
