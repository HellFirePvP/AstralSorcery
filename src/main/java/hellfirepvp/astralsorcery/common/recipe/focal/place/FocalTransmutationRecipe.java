/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.focal.place;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.recipe.CustomRecipe;
import hellfirepvp.astralsorcery.common.util.IngredientUtil;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.ResolvingRecipeTypeRegistryObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalTransmutationRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocalTransmutationRecipe extends CustomRecipe<FocalTransmutationRecipe, FocalTransmutationCraftingInput> {

    public static final MapCodec<FocalTransmutationRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.INT.fieldOf("duration").forGetter(FocalTransmutationRecipe::getDuration),
            ColorWrapper.CODEC.fieldOf("color").forGetter(FocalTransmutationRecipe::getColor),
            BlockPredicate.CODEC.listOf().fieldOf("input_predicates").forGetter(FocalTransmutationRecipe::getInputPredicates),
            Ingredient.CODEC.fieldOf("input_display_stacks").forGetter(FocalTransmutationRecipe::getInputDisplay),
            WeightedEntry.Wrapper.codec(BlockState.CODEC).listOf().fieldOf("output_states").forGetter(FocalTransmutationRecipe::getOutputStates),
            RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec().optionalFieldOf("required_constellation").forGetter(FocalTransmutationRecipe::getRequiredConstellation),
            Codec.BOOL.fieldOf("requires_focused_starlight").forGetter(FocalTransmutationRecipe::requiresFocusedStarlight)
    ).apply(inst, FocalTransmutationRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FocalTransmutationRecipe> STREAM_CODEC = CodecUtil.streamComposite(
            ByteBufCodecs.INT,
            FocalTransmutationRecipe::getDuration,
            ColorWrapper.STREAM_CODEC,
            FocalTransmutationRecipe::getColor,
            ByteBufCodecs.fromCodecWithRegistriesTrusted(BlockPredicate.CODEC).apply(ByteBufCodecs.list()),
            FocalTransmutationRecipe::getInputPredicates,
            Ingredient.CONTENTS_STREAM_CODEC,
            FocalTransmutationRecipe::getInputDisplay,
            ByteBufCodecs.fromCodecWithRegistriesTrusted(WeightedEntry.Wrapper.codec(BlockState.CODEC)).apply(ByteBufCodecs.list()),
            FocalTransmutationRecipe::getOutputStates,
            ByteBufCodecs.optional(ByteBufCodecs.registry(RegistriesAS.KEY_CONSTELLATIONS)),
            FocalTransmutationRecipe::getRequiredConstellation,
            ByteBufCodecs.BOOL,
            FocalTransmutationRecipe::requiresFocusedStarlight,
            FocalTransmutationRecipe::new);

    private final int duration;
    private final ColorWrapper color;
    private final List<BlockPredicate> inputPredicates;
    private final Ingredient inputDisplay;
    private final List<WeightedEntry.Wrapper<BlockState>> outputStates;
    private final BaseConstellation requiredConstellation;
    private final boolean requiresFocusedStarlight;

    public FocalTransmutationRecipe(int duration, ColorWrapper color, List<BlockPredicate> inputPredicates, Ingredient inputDisplay, List<WeightedEntry.Wrapper<BlockState>> outputStates, Optional<BaseConstellation> requiredConstellation, boolean requiresFocusedStarlight) {
        this.duration = duration;
        this.color = color;
        this.inputPredicates = inputPredicates;
        this.inputDisplay = inputDisplay;
        this.outputStates = outputStates;
        this.requiredConstellation = requiredConstellation.orElse(null);
        this.requiresFocusedStarlight = requiresFocusedStarlight;
    }

    public int getDuration() {
        return this.duration;
    }

    public ColorWrapper getColor() {
        return this.color;
    }

    public List<BlockPredicate> getInputPredicates() {
        return Collections.unmodifiableList(this.inputPredicates);
    }

    public Ingredient getInputDisplay() {
        return this.inputDisplay;
    }

    public List<WeightedEntry.Wrapper<BlockState>> getOutputStates() {
        return Collections.unmodifiableList(this.outputStates);
    }

    public Optional<BaseConstellation> getRequiredConstellation() {
        return Optional.ofNullable(this.requiredConstellation);
    }

    public boolean isRequiredConstellation(BaseConstellation constellation) {
        return this.requiredConstellation == null || this.requiredConstellation.equals(constellation);
    }

    public BlockState getOutputState(RandomSource rand) {
        return WeightedRandom.getRandomItem(rand, this.outputStates)
                .map(WeightedEntry.Wrapper::data)
                .orElse(Blocks.AIR.defaultBlockState());

    }

    public ItemStack getOutputForDisplay(long tick) {
        List<ItemStack> applicable = this.outputStates.stream()
                .map(WeightedEntry.Wrapper::data)
                .map(ItemUtil::createBlockStack)
                .filter(stack -> !stack.isEmpty())
                .toList();
        return IngredientUtil.getRandomDisplayStack(applicable, tick);
    }

    public boolean requiresFocusedStarlight() {
        return this.requiresFocusedStarlight;
    }

    @Override
    public boolean consumeInputs(FocalTransmutationCraftingInput input, HolderLookup.Provider registries) {
        //input.getLevel().setBlock(input.getPos(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        return true;
    }

    @Override
    public void createOutput(FocalTransmutationCraftingInput input, HolderLookup.Provider registries) {
        input.getLevel().setBlock(input.getPos(), this.getOutputState(input.getLevel().getRandom()), Block.UPDATE_ALL);
    }

    @Override
    public boolean matches(FocalTransmutationCraftingInput input, Level level) {
        if (input.isEmpty() || this.getInputPredicates().isEmpty() || !(input.getLevel() instanceof ServerLevel sLevel)) {
            return false;
        }
        if (this.requiresFocusedStarlight() && !input.isFocusedStarlight()) {
            return false;
        }
        if (!this.isRequiredConstellation(input.getConstellation())) {
            return false;
        }
        for (BlockPredicate predicate : this.getInputPredicates()) {
            if (predicate.test(sLevel, input.getPos())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Supplier<? extends RecipeSerializer<FocalTransmutationRecipe>> getRecipeSerializer() {
        return RecipeTypesAS.FOCAL_TRANSMUTATION_SERIALIZER;
    }

    @Override
    public ResolvingRecipeTypeRegistryObject<FocalTransmutationRecipe> getRecipeType() {
        return RecipeTypesAS.FOCAL_TRANSMUTATION_TYPE;
    }
}
