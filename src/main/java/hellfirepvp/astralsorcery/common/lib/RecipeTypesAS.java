/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import com.google.common.collect.Iterables;
import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.item.LumenCrystalItem;
import hellfirepvp.astralsorcery.common.recipe.RecipeChangeColor;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarRecipe;
import hellfirepvp.astralsorcery.common.recipe.focal.drop.FocalCombineRecipe;
import hellfirepvp.astralsorcery.common.recipe.focal.place.FocalTransmutationRecipe;
import hellfirepvp.astralsorcery.common.recipe.infusion.InfusionRecipe;
import hellfirepvp.astralsorcery.common.recipe.lightwell.LightwellRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.LiquidInteractionRecipe;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenCrystallizationRecipe;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenGenerationRecipe;
import hellfirepvp.astralsorcery.common.util.data.ResolvingRecipeTypeRegistryObject;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RecipeTypesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RecipeTypesAS {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPE_REGISTER =
            DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, AstralSorcery.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER_REGISTER =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, AstralSorcery.MODID);

    public static final ResolvingRecipeTypeRegistryObject<AltarRecipe> ALTAR_CRAFTING_TYPE = resolvableType("altar_crafting",
            recipe -> Iterables.getFirst(recipe.getOutputs(), ItemStack.EMPTY).copy());
    public static final ResolvingRecipeTypeRegistryObject<FocalCombineRecipe> FOCAL_COMBINE_TYPE = resolvableType("focal_combine",
            recipe -> Iterables.getFirst(recipe.getOutputs(), ItemStack.EMPTY).copy());
    public static final ResolvingRecipeTypeRegistryObject<FocalTransmutationRecipe> FOCAL_TRANSMUTATION_TYPE = resolvableType("focal_transmutation",
            recipe -> recipe.getOutputForDisplay(0));
    public static final ResolvingRecipeTypeRegistryObject<LumenGenerationRecipe> LUMEN_GENERATION_TYPE = resolvableType("lumen_generation",
            recipe -> ItemStack.EMPTY);
    public static final ResolvingRecipeTypeRegistryObject<LumenCrystallizationRecipe> LUMEN_CRYSTALLIZATION_TYPE = resolvableType("lumen_crystallization",
            recipe -> LumenCrystalItem.getCrystal(Holder.direct(recipe.getLumenToCrystallize())));
    public static final ResolvingRecipeTypeRegistryObject<LightwellRecipe> LIGHTWELL_TYPE = resolvableType("lightwell",
            recipe -> FluidUtil.getFilledBucket(new FluidStack(recipe.getGeneratedFluid(), FluidType.BUCKET_VOLUME)));
    public static final ResolvingRecipeTypeRegistryObject<InfusionRecipe> INFUSION_TYPE = resolvableType("infusion",
            recipe -> recipe.getOutput().copy());
    public static final ResolvingRecipeTypeRegistryObject<LiquidStarlightRecipe> LIQUID_STARLIGHT_TYPE = resolvableType("liquid_starlight",
            recipe -> ItemStack.EMPTY);
    public static final ResolvingRecipeTypeRegistryObject<LiquidInteractionRecipe> LIQUID_INTERACTION_TYPE = resolvableType("liquid_interaction",
            recipe -> recipe.getResult().getDisplayOutput());


    public static DeferredHolder<RecipeSerializer<?>, CodecSerializer<AltarRecipe>> ALTAR_CRAFTING_SERIALIZER =
            codecSerializer("altar_crafting", AltarRecipe.CODEC, AltarRecipe.STREAM_CODEC);
    public static DeferredHolder<RecipeSerializer<?>, CodecSerializer<FocalCombineRecipe>> FOCAL_COMBINE_SERIALIZER =
            codecSerializer("focal_combine", FocalCombineRecipe.CODEC, FocalCombineRecipe.STREAM_CODEC);
    public static DeferredHolder<RecipeSerializer<?>, CodecSerializer<FocalTransmutationRecipe>> FOCAL_TRANSMUTATION_SERIALIZER =
            codecSerializer("focal_transmutation", FocalTransmutationRecipe.CODEC, FocalTransmutationRecipe.STREAM_CODEC);
    public static DeferredHolder<RecipeSerializer<?>, CodecSerializer<LumenGenerationRecipe>> LUMEN_GENERATION_SERIALIZER =
            codecSerializer("lumen_generation", LumenGenerationRecipe.CODEC, LumenGenerationRecipe.STREAM_CODEC);
    public static DeferredHolder<RecipeSerializer<?>, CodecSerializer<LumenCrystallizationRecipe>> LUMEN_CRYSTALLIZATION_SERIALIZER =
            codecSerializer("lumen_crystallization", LumenCrystallizationRecipe.CODEC, LumenCrystallizationRecipe.STREAM_CODEC);
    public static DeferredHolder<RecipeSerializer<?>, CodecSerializer<LightwellRecipe>> LIGHTWELL_SERIALIZER =
            codecSerializer("lightwell", LightwellRecipe.CODEC, LightwellRecipe.STREAM_CODEC);
    public static DeferredHolder<RecipeSerializer<?>, CodecSerializer<InfusionRecipe>> INFUSION_SERIALIZER =
            codecSerializer("infusion", InfusionRecipe.CODEC, InfusionRecipe.STREAM_CODEC);
    public static DeferredHolder<RecipeSerializer<?>, CodecSerializer<LiquidStarlightRecipe>> LIQUID_STARLIGHT_SERIALIZER =
            codecSerializer("liquid_starlight", LiquidStarlightRecipe.CODEC, LiquidStarlightRecipe.STREAM_CODEC);
    public static DeferredHolder<RecipeSerializer<?>, CodecSerializer<LiquidInteractionRecipe>> LIQUID_INTERACTION_SERIALIZER =
            codecSerializer("liquid_interaction", LiquidInteractionRecipe.CODEC, LiquidInteractionRecipe.STREAM_CODEC);
    public static DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<RecipeChangeColor.IlluminationWandChangeColor>> ILLUMINATION_WAND_CHANGE_COLOR_SERIALIZER =
            simpleSerializer("illumination_wand_change_color", RecipeChangeColor.IlluminationWandChangeColor::new);
    public static DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<RecipeChangeColor.CelestialGatewayChangeColor>> CELESTIAL_GATEWAY_CHANGE_COLOR_SERIALIZER =
            simpleSerializer("celestial_gateway_change_color", RecipeChangeColor.CelestialGatewayChangeColor::new);

    private static <T extends Recipe<?>> ResolvingRecipeTypeRegistryObject<T> resolvableType(String name, Function<T, ItemStack> outputMatchProvider) {
        RecipeType<T> type = RecipeType.simple(AstralSorcery.key(name));
        return new ResolvingRecipeTypeRegistryObject<>(RECIPE_TYPE_REGISTER.register(name, () -> type), outputMatchProvider);
    }

    private static <T extends CraftingRecipe> DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<T>> simpleSerializer(String name,
                                                                                                                                      SimpleCraftingRecipeSerializer.Factory<T> factory) {
        return RECIPE_SERIALIZER_REGISTER.register(name, () -> new SimpleCraftingRecipeSerializer<>(factory));
    }

    private static <T extends Recipe<?>> DeferredHolder<RecipeSerializer<?>, CodecSerializer<T>> codecSerializer(String name,
                                                                                                                 MapCodec<T> codec,
                                                                                                                 StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return RECIPE_SERIALIZER_REGISTER.register(name, () -> new CodecSerializer<>(codec, streamCodec));
    }

    public record CodecSerializer<T extends Recipe<?>>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) implements RecipeSerializer<T> {}
}
