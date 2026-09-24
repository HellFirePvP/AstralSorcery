/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.liquid.interaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.recipe.CustomRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.result.LiquidInteractionResult;
import hellfirepvp.astralsorcery.common.util.data.ResolvingRecipeTypeRegistryObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidInteractionRecipe
 * Created by HellFirePvP
 * Date: 11.04.2026
 */
public class LiquidInteractionRecipe extends CustomRecipe<LiquidInteractionRecipe, LiquidInteractionInput> {

    public static final MapCodec<LiquidInteractionRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            SizedFluidIngredient.NESTED_CODEC.fieldOf("reactantA").forGetter(LiquidInteractionRecipe::getReactantA),
            SizedFluidIngredient.NESTED_CODEC.fieldOf("reactantB").forGetter(LiquidInteractionRecipe::getReactantB),
            Codec.FLOAT.fieldOf("chanceConsumeA").forGetter(LiquidInteractionRecipe::getChanceConsumeA),
            Codec.FLOAT.fieldOf("chanceConsumeB").forGetter(LiquidInteractionRecipe::getChanceConsumeB),
            Codec.INT.fieldOf("weight").forGetter(LiquidInteractionRecipe::getWeight),
            LiquidInteractionResult.CODEC.fieldOf("result").forGetter(LiquidInteractionRecipe::getResult)
    ).apply(inst, LiquidInteractionRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LiquidInteractionRecipe> STREAM_CODEC = StreamCodec.of(
            LiquidInteractionRecipe::write,
            LiquidInteractionRecipe::read);

    private final SizedFluidIngredient reactantA;
    private final SizedFluidIngredient reactantB;
    private final float chanceConsumeA;
    private final float chanceConsumeB;
    private final int weight;
    private final LiquidInteractionResult result;

    public LiquidInteractionRecipe(SizedFluidIngredient reactantA,
                                   SizedFluidIngredient reactantB,
                                   float chanceConsumeA,
                                   float chanceConsumeB,
                                   int weight,
                                   LiquidInteractionResult result) {
        this.reactantA = reactantA;
        this.reactantB = reactantB;
        this.chanceConsumeA = chanceConsumeA;
        this.chanceConsumeB = chanceConsumeB;
        this.weight = weight;
        this.result = result;
    }

    public SizedFluidIngredient getReactantA() {
        return this.reactantA;
    }

    public SizedFluidIngredient getReactantB() {
        return this.reactantB;
    }

    public float getChanceConsumeA() {
        return this.chanceConsumeA;
    }

    public float getChanceConsumeB() {
        return this.chanceConsumeB;
    }

    public int getWeight() {
        return this.weight;
    }

    public LiquidInteractionResult getResult() {
        return this.result;
    }

    @Override
    public boolean matches(LiquidInteractionInput input, Level level) {
        return this.matches(input.getFluidA(), input.getFluidB());
    }

    public boolean matches(FluidStack inputA, FluidStack inputB) {
        return (satisfies(inputA, this.reactantA) && satisfies(inputB, this.reactantB))
                || (satisfies(inputA, this.reactantB) && satisfies(inputB, this.reactantA));
    }

    private static boolean satisfies(FluidStack contained, SizedFluidIngredient required) {
        if (contained.isEmpty()) {
            return false;
        }
        return required.ingredient().test(contained) && contained.getAmount() >= required.amount();
    }

    public boolean consumeInputs(RandomSource random, IFluidHandler handlerA, IFluidHandler handlerB) {
        if (tryConsumeOriented(random, handlerA, handlerB, this.reactantA, this.reactantB, this.chanceConsumeA, this.chanceConsumeB)) {
            return true;
        }
        return tryConsumeOriented(random, handlerA, handlerB, this.reactantB, this.reactantA, this.chanceConsumeB, this.chanceConsumeA);
    }

    private static boolean tryConsumeOriented(RandomSource random,
                                              IFluidHandler handlerA, IFluidHandler handlerB,
                                              SizedFluidIngredient requiredA, SizedFluidIngredient requiredB,
                                              float chanceA, float chanceB) {
        FluidStack matchA = findMatching(handlerA, requiredA);
        if (matchA.isEmpty()) {
            return false;
        }
        FluidStack matchB = findMatching(handlerB, requiredB);
        if (matchB.isEmpty()) {
            return false;
        }
        if (random.nextFloat() < chanceA) {
            handlerA.drain(matchA.copyWithAmount(requiredA.amount()), IFluidHandler.FluidAction.EXECUTE);
        }
        if (random.nextFloat() < chanceB) {
            handlerB.drain(matchB.copyWithAmount(requiredB.amount()), IFluidHandler.FluidAction.EXECUTE);
        }
        return true;
    }

    private static FluidStack findMatching(IFluidHandler handler, SizedFluidIngredient required) {
        for (int i = 0; i < handler.getTanks(); i++) {
            FluidStack contained = handler.getFluidInTank(i);
            if (contained.isEmpty() || !required.ingredient().test(contained) || contained.getAmount() < required.amount()) {
                continue;
            }
            FluidStack drainable = handler.drain(contained.copyWithAmount(required.amount()), IFluidHandler.FluidAction.SIMULATE);
            if (!drainable.isEmpty() && drainable.getAmount() >= required.amount()) {
                return contained;
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public boolean consumeInputs(LiquidInteractionInput input, HolderLookup.Provider registries) {
        return false;
    }

    @Override
    public void createOutput(LiquidInteractionInput input, HolderLookup.Provider registries) {
    }

    @Override
    public ResolvingRecipeTypeRegistryObject<LiquidInteractionRecipe> getRecipeType() {
        return RecipeTypesAS.LIQUID_INTERACTION_TYPE;
    }

    @Override
    public Supplier<? extends RecipeSerializer<LiquidInteractionRecipe>> getRecipeSerializer() {
        return RecipeTypesAS.LIQUID_INTERACTION_SERIALIZER;
    }

    public static List<LiquidInteractionRecipe> findMatching(Level level, FluidStack fluidA, FluidStack fluidB) {
        List<LiquidInteractionRecipe> matches = new ArrayList<>();
        for (RecipeHolder<LiquidInteractionRecipe> holder :
                level.getRecipeManager().getAllRecipesFor(RecipeTypesAS.LIQUID_INTERACTION_TYPE.get())) {
            if (holder.value().matches(fluidA, fluidB)) {
                matches.add(holder.value());
            }
        }
        return matches;
    }

    @Nullable
    public static LiquidInteractionRecipe pickWeighted(Collection<LiquidInteractionRecipe> recipes, RandomSource random) {
        int totalWeight = 0;
        for (LiquidInteractionRecipe recipe : recipes) {
            totalWeight += Math.max(0, recipe.weight);
        }
        if (totalWeight <= 0) {
            return null;
        }
        int roll = random.nextInt(totalWeight);
        for (LiquidInteractionRecipe recipe : recipes) {
            roll -= Math.max(0, recipe.weight);
            if (roll < 0) {
                return recipe;
            }
        }
        return null;
    }

    private static void write(RegistryFriendlyByteBuf buf, LiquidInteractionRecipe recipe) {
        SizedFluidIngredient.STREAM_CODEC.encode(buf, recipe.reactantA);
        SizedFluidIngredient.STREAM_CODEC.encode(buf, recipe.reactantB);
        ByteBufCodecs.FLOAT.encode(buf, recipe.chanceConsumeA);
        ByteBufCodecs.FLOAT.encode(buf, recipe.chanceConsumeB);
        ByteBufCodecs.INT.encode(buf, recipe.weight);
        LiquidInteractionResult.STREAM_CODEC.encode(buf, recipe.result);
    }

    private static LiquidInteractionRecipe read(RegistryFriendlyByteBuf buf) {
        SizedFluidIngredient reactantA = SizedFluidIngredient.STREAM_CODEC.decode(buf);
        SizedFluidIngredient reactantB = SizedFluidIngredient.STREAM_CODEC.decode(buf);
        float chanceA = ByteBufCodecs.FLOAT.decode(buf);
        float chanceB = ByteBufCodecs.FLOAT.decode(buf);
        int weight = ByteBufCodecs.INT.decode(buf);
        LiquidInteractionResult result = LiquidInteractionResult.STREAM_CODEC.decode(buf);
        return new LiquidInteractionRecipe(reactantA, reactantB, chanceA, chanceB, weight, result);
    }
}
