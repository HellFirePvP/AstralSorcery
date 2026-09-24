/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.focal.drop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.recipe.CustomRecipe;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.ResolvingRecipeTypeRegistryObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalCombineRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocalCombineRecipe extends CustomRecipe<FocalCombineRecipe, FocalCombineCraftingInput> {

    public static final MapCodec<FocalCombineRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("duration").forGetter(FocalCombineRecipe::getDuration),
            ColorWrapper.CODEC.fieldOf("color").forGetter(FocalCombineRecipe::getColor),
            Ingredient.CODEC.listOf().fieldOf("inputs").forGetter(FocalCombineRecipe::getInputs),
            ItemStack.CODEC.listOf().fieldOf("outputs").forGetter(FocalCombineRecipe::getOutputs),
            RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec().optionalFieldOf("required_constellation").forGetter(FocalCombineRecipe::getRequiredConstellation)
    ).apply(instance, FocalCombineRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FocalCombineRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            FocalCombineRecipe::getDuration,
            ColorWrapper.STREAM_CODEC,
            FocalCombineRecipe::getColor,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
            FocalCombineRecipe::getInputs,
            ItemStack.LIST_STREAM_CODEC,
            FocalCombineRecipe::getOutputs,
            ByteBufCodecs.optional(ByteBufCodecs.registry(RegistriesAS.KEY_CONSTELLATIONS)),
            FocalCombineRecipe::getRequiredConstellation,
            FocalCombineRecipe::new);

    private final int duration;
    private final ColorWrapper color;
    private final List<Ingredient> inputs;
    private final List<ItemStack> outputs;
    private final BaseConstellation requiredConstellation;

    public FocalCombineRecipe(int duration, ColorWrapper color, List<Ingredient> inputs, List<ItemStack> outputs, Optional<BaseConstellation> requiredConstellation) {
        this.duration = duration;
        this.color = color;
        this.inputs = inputs;
        this.outputs = outputs;
        this.requiredConstellation = requiredConstellation.orElse(null);
    }

    public int getDuration() {
        return this.duration;
    }

    public ColorWrapper getColor() {
        return this.color;
    }

    public List<Ingredient> getInputs() {
        return Collections.unmodifiableList(this.inputs);
    }

    public List<ItemStack> getOutputs() {
        return Collections.unmodifiableList(this.outputs);
    }

    public Optional<BaseConstellation> getRequiredConstellation() {
        return Optional.ofNullable(this.requiredConstellation);
    }

    public boolean isRequiredConstellation(BaseConstellation constellation) {
        return this.requiredConstellation == null || this.requiredConstellation.equals(constellation);
    }

    @Override
    public boolean consumeInputs(FocalCombineCraftingInput input, HolderLookup.Provider registries) {
        return this.getValidInputMapping(input).map(inputMapping -> {
            inputMapping.forEach(tpl -> {
                tpl.getB().getItem().shrink(1);
            });
            return true;
        }).orElse(false);
    }

    @Override
    public void createOutput(FocalCombineCraftingInput input, HolderLookup.Provider registries) {
        Vec3 dropPosition = input.getCenter().add(0, 0.5, 0);
        this.getOutputs().forEach(output -> ItemUtil.dropItem(input.getLevel(), dropPosition, output.copy()));
    }

    @Override
    public boolean matches(FocalCombineCraftingInput input, Level level) {
        if (input.isEmpty() || this.getInputs().isEmpty()) {
            return false;
        }
        if (!this.isRequiredConstellation(input.getConstellation())) {
            return false;
        }
        return this.getValidInputMapping(input).isPresent();
    }

    public List<ItemEntity> filterNecessaryItems(FocalCombineCraftingInput input) {
        return this.getValidInputMapping(input)
                .map(mappings -> new ArrayList<>(mappings.stream().map(Tuple::getB).collect(Collectors.toSet())))
                .orElse(new ArrayList<>());
    }

    private Optional<List<Tuple<Ingredient, ItemEntity>>> getValidInputMapping(FocalCombineCraftingInput input) {
        List<Tuple<Ingredient, ItemEntity>> inputMapping = new ArrayList<>();
        Map<ItemEntity, ItemStack> availableInputs = input.getAssociatedInputs();
        for (Ingredient ingredient : this.getInputs()) {
            ItemEntity foundEntity = null;
            for (Map.Entry<ItemEntity, ItemStack> entry : availableInputs.entrySet()) {
                ItemEntity entity = entry.getKey();
                ItemStack stack = entry.getValue();
                if (!stack.isEmpty() && ingredient.test(stack)) {
                    stack.shrink(1);
                    foundEntity = entity;
                    break;
                }
            }
            if (foundEntity == null) {
                return Optional.empty();
            }
            inputMapping.add(new Tuple<>(ingredient, foundEntity));
        }
        return Optional.of(inputMapping);
    }

    @Override
    public Supplier<? extends RecipeSerializer<FocalCombineRecipe>> getRecipeSerializer() {
        return RecipeTypesAS.FOCAL_COMBINE_SERIALIZER;
    }

    @Override
    public ResolvingRecipeTypeRegistryObject<FocalCombineRecipe> getRecipeType() {
        return RecipeTypesAS.FOCAL_COMBINE_TYPE;
    }
}
