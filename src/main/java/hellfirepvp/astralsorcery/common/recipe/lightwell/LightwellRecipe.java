/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.lightwell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.recipe.CustomRecipe;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.ResolvingRecipeTypeRegistryObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LightwellRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LightwellRecipe extends CustomRecipe<LightwellRecipe, LightwellRecipeInput> {

    public static final MapCodec<LightwellRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ColorWrapper.CODEC.fieldOf("catalyst_color").forGetter(LightwellRecipe::getCatalystColor),
            Ingredient.CODEC.fieldOf("input").forGetter(LightwellRecipe::getInput),
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("generated_fluid").forGetter(LightwellRecipe::getGeneratedFluid),
            Codec.FLOAT.fieldOf("production_multiplier").forGetter(LightwellRecipe::getProductionMultiplier),
            Codec.FLOAT.fieldOf("shatter_multiplier").forGetter(LightwellRecipe::getShatterMultiplier)
    ).apply(inst, LightwellRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LightwellRecipe> STREAM_CODEC = StreamCodec.composite(
            ColorWrapper.STREAM_CODEC,
            LightwellRecipe::getCatalystColor,
            Ingredient.CONTENTS_STREAM_CODEC,
            LightwellRecipe::getInput,
            ByteBufCodecs.registry(Registries.FLUID),
            LightwellRecipe::getGeneratedFluid,
            ByteBufCodecs.FLOAT,
            LightwellRecipe::getProductionMultiplier,
            ByteBufCodecs.FLOAT,
            LightwellRecipe::getShatterMultiplier,
            LightwellRecipe::new
    );

    private final ColorWrapper catalystColor;
    private final Ingredient input;
    private final Fluid generatedFluid;

    private final float productionMultiplier;
    private final float shatterMultiplier;

    public LightwellRecipe(ColorWrapper catalystColor, Ingredient input, Fluid generatedFluid, float productionMultiplier, float shatterMultiplier) {
        this.catalystColor = catalystColor;
        this.input = input;
        this.generatedFluid = generatedFluid;
        this.productionMultiplier = productionMultiplier;
        this.shatterMultiplier = shatterMultiplier;
    }

    @Override
    public boolean consumeInputs(LightwellRecipeInput input, HolderLookup.Provider registries) {
        throw new UnsupportedOperationException("Lightwell input consumption is not handled by its recipe.");
    }

    @Override
    public void createOutput(LightwellRecipeInput input, HolderLookup.Provider registries) {
    }

    @Override
    public boolean matches(LightwellRecipeInput input, Level level) {
        if (this.getInput().test(input.getItemInput())) {
            return input.getExistingFluidOutput().isEmpty() || input.getExistingFluidOutput().is(this.getGeneratedFluid());
        }
        return false;
    }

    public ColorWrapper getCatalystColor() {
        return this.catalystColor;
    }

    public Ingredient getInput() {
        return this.input;
    }

    public Fluid getGeneratedFluid() {
        return this.generatedFluid;
    }

    public float getProductionMultiplier() {
        return this.productionMultiplier;
    }

    public float getShatterMultiplier() {
        return this.shatterMultiplier;
    }

    @Override
    public Supplier<? extends RecipeSerializer<LightwellRecipe>> getRecipeSerializer() {
        return RecipeTypesAS.LIGHTWELL_SERIALIZER;
    }

    @Override
    public ResolvingRecipeTypeRegistryObject<LightwellRecipe> getRecipeType() {
        return RecipeTypesAS.LIGHTWELL_TYPE;
    }
}
