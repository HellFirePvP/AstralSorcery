/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.liquid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.recipe.CustomRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.output.LiquidStarlightRecipeOutputModifier;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.CountIngredient;
import hellfirepvp.astralsorcery.common.util.data.ResolvingRecipeTypeRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.*;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidStarlightRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LiquidStarlightRecipe extends CustomRecipe<LiquidStarlightRecipe, LiquidStarlightRecipeInput> {

    public static final MapCodec<LiquidStarlightRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            CountIngredient.CODEC.fieldOf("input").forGetter(LiquidStarlightRecipe::getInput),
            CountIngredient.CODEC.listOf().fieldOf("otherInputs").forGetter(LiquidStarlightRecipe::getOtherInputs),
            Codec.INT.fieldOf("duration").forGetter(LiquidStarlightRecipe::getDuration),
            Codec.INT.fieldOf("randomAdditionalDuration").forGetter(LiquidStarlightRecipe::getRandomAdditionalDuration),
            ColorWrapper.CODEC.fieldOf("color").forGetter(LiquidStarlightRecipe::getColor),
            LiquidStarlightRecipeOutputModifier.CODEC.listOf().fieldOf("outputModifiers").forGetter(LiquidStarlightRecipe::getOutputModifiers),
            Codec.BOOL.fieldOf("consumesLiquid").orElse(false).forGetter(LiquidStarlightRecipe::consumesLiquid),
            Codec.BOOL.fieldOf("consumesInputs").orElse(true).forGetter(LiquidStarlightRecipe::consumesInputs)
    ).apply(inst, LiquidStarlightRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LiquidStarlightRecipe> STREAM_CODEC = StreamCodec.of(LiquidStarlightRecipe::write, LiquidStarlightRecipe::read);

    private final CountIngredient input;
    private final List<CountIngredient> otherInputs;
    private final int duration;
    private final int randomAdditionalDuration;
    private final ColorWrapper color;
    private final List<LiquidStarlightRecipeOutputModifier> outputModifiers;
    private final boolean consumesLiquid;
    private final boolean consumesInputs;

    public LiquidStarlightRecipe(CountIngredient input, List<CountIngredient> otherInputs, int duration, int randomAdditionalDuration, ColorWrapper color, List<LiquidStarlightRecipeOutputModifier> outputModifiers, boolean consumesLiquid, boolean consumesInputs) {
        this.input = input;
        this.otherInputs = otherInputs;
        this.duration = duration;
        this.randomAdditionalDuration = randomAdditionalDuration;
        this.color = color;
        this.outputModifiers = outputModifiers;
        this.consumesLiquid = consumesLiquid;
        this.consumesInputs = consumesInputs;
    }

    @Override
    public boolean consumeInputs(LiquidStarlightRecipeInput input, HolderLookup.Provider registries) {
        List<ItemEntity> filteredOtherEntities = input.getFilteredOtherEntitiesByOutput(this.getOutputModifiers());
        if (this.getOtherInputs().isEmpty() && !filteredOtherEntities.isEmpty()) {
            return false;
        }
        Map<CountIngredient, List<ItemEntity>> splitInputs = this.splitOtherInputs(filteredOtherEntities);
        if (splitInputs.isEmpty() && !this.getOtherInputs().isEmpty()) return false;

        ItemStack inputStack = input.getTriggerEntity().getItem();
        if (inputStack.isEmpty() || !this.getInput().ingredient().test(inputStack) || inputStack.getCount() < this.getInput().count()) {
            return false;
        }

        if (this.consumesInputs()) {
            this.consumeItemInputs(input);
        }

        if (this.consumesLiquid()) {
            Level level = input.getTriggerEntity().level();
            BlockPos pos = input.getTriggerEntity().blockPosition();
            if (FluidsAS.LIQUID_STARLIGHT.isSource(level.getFluidState(pos))) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
        return true;
    }

    @Override
    public void createOutput(LiquidStarlightRecipeInput input, HolderLookup.Provider registries) {
        this.getOutputModifiers().forEach(output -> output.createOutput(this, input));
    }

    @Override
    public boolean matches(LiquidStarlightRecipeInput input, Level level) {
        ItemStack inputStack = input.getTriggerEntity().getItem();
        if (inputStack.isEmpty() || !this.getInput().ingredient().test(inputStack) || inputStack.getCount() < this.getInput().count()) {
            return false;
        }
        List<ItemEntity> filteredOtherEntities = input.getFilteredOtherEntitiesByOutput(this.getOutputModifiers());
        if (this.getOtherInputs().isEmpty()) {
            return filteredOtherEntities.isEmpty() && this.isValidForOutputModifiers(input, filteredOtherEntities);
        }
        return !this.splitOtherInputs(filteredOtherEntities).isEmpty() && this.isValidForOutputModifiers(input, filteredOtherEntities);
    }

    private boolean isValidForOutputModifiers(LiquidStarlightRecipeInput input, List<ItemEntity> validOtherInputs) {
        return this.getOutputModifiers().stream().allMatch(modifier -> modifier.isValidInputForOutput(input, validOtherInputs));
    }

    public void consumeItemInputs(LiquidStarlightRecipeInput input) {
        ItemStack inputStack = input.getTriggerEntity().getItem();
        inputStack.shrink(this.getInput().count());
        input.getTriggerEntity().setItem(inputStack);

        this.splitOtherInputs(input.getFilteredOtherEntitiesByOutput(this.getOutputModifiers())).forEach((ingredient, items) -> {
            int toConsume = ingredient.count();
            for (ItemEntity itemEntity : items) {
                ItemStack entityStack = itemEntity.getItem();

                if (entityStack.getCount() <= toConsume) {
                    toConsume -= entityStack.getCount();
                    itemEntity.setItem(ItemStack.EMPTY);
                } else {
                    entityStack.shrink(toConsume);
                    itemEntity.setItem(entityStack);
                    toConsume = 0;
                }
                if (toConsume <= 0) {
                    break;
                }
            }
        });
    }

    private Map<CountIngredient, List<ItemEntity>> splitOtherInputs(List<ItemEntity> otherInputs) {
        List<ItemEntity> modifiableInputs = new ArrayList<>(otherInputs);
        Map<CountIngredient, List<ItemEntity>> foundInputs = new HashMap<>();
        for (CountIngredient requiredInput : this.getOtherInputs()) {
            Ingredient ingredient = requiredInput.ingredient();
            int requiredCount = requiredInput.count();
            if (requiredCount <= 0) {
                continue;
            }

            List<ItemEntity> matchedEntities = modifiableInputs.stream()
                    .filter(e -> ingredient.test(e.getItem()))
                    .toList();

            //Direct failure if not enough amoutn
            int totalCount = matchedEntities.stream().mapToInt(e -> e.getItem().getCount()).sum();
            if (totalCount < requiredCount) {
                return Collections.emptyMap();
            }

            List<ItemEntity> usedEntities = new ArrayList<>();
            int gathered = 0;
            for (ItemEntity entity : matchedEntities) {
                usedEntities.add(entity);
                gathered += entity.getItem().getCount();
                if (gathered >= requiredCount) {
                    break;
                }
            }
            foundInputs.put(requiredInput, usedEntities);
            modifiableInputs.removeAll(usedEntities);
        }
        return modifiableInputs.isEmpty() ? foundInputs : Collections.emptyMap();
    }

    public CountIngredient getInput() {
        return this.input;
    }

    public List<CountIngredient> getOtherInputs() {
        return Collections.unmodifiableList(this.otherInputs);
    }

    public int getDuration() {
        return this.duration;
    }

    public int getRandomAdditionalDuration() {
        return this.randomAdditionalDuration;
    }

    public ColorWrapper getColor() {
        return this.color;
    }

    public List<LiquidStarlightRecipeOutputModifier> getOutputModifiers() {
        return Collections.unmodifiableList(this.outputModifiers);
    }

    public boolean consumesLiquid() {
        return this.consumesLiquid;
    }

    public boolean consumesInputs() {
        return this.consumesInputs;
    }

    @Override
    public Supplier<? extends RecipeSerializer<LiquidStarlightRecipe>> getRecipeSerializer() {
        return RecipeTypesAS.LIQUID_STARLIGHT_SERIALIZER;
    }

    @Override
    public ResolvingRecipeTypeRegistryObject<LiquidStarlightRecipe> getRecipeType() {
        return RecipeTypesAS.LIQUID_STARLIGHT_TYPE;
    }

    private static void write(RegistryFriendlyByteBuf buf, LiquidStarlightRecipe recipe) {
        CountIngredient.STREAM_CODEC.encode(buf, recipe.getInput());
        CountIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.getOtherInputs());
        buf.writeInt(recipe.getDuration());
        buf.writeInt(recipe.getRandomAdditionalDuration());
        ColorWrapper.STREAM_CODEC.encode(buf, recipe.getColor());
        LiquidStarlightRecipeOutputModifier.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.getOutputModifiers());
        buf.writeBoolean(recipe.consumesLiquid());
        buf.writeBoolean(recipe.consumesInputs());
    }

    private static LiquidStarlightRecipe read(RegistryFriendlyByteBuf buf) {
        CountIngredient input = CountIngredient.STREAM_CODEC.decode(buf);
        List<CountIngredient> otherInputs = CountIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
        int duration = buf.readInt();
        int randomAdditionalDuration = buf.readInt();
        ColorWrapper color = ColorWrapper.STREAM_CODEC.decode(buf);
        List<LiquidStarlightRecipeOutputModifier> outputModifiers = LiquidStarlightRecipeOutputModifier.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
        boolean consumesLiquid = buf.readBoolean();
        boolean consumesInputs = buf.readBoolean();
        return new LiquidStarlightRecipe(input, otherInputs, duration, randomAdditionalDuration, color, outputModifiers, consumesLiquid, consumesInputs);
    }
}
