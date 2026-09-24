/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.constellation.DebugConstellation;
import hellfirepvp.astralsorcery.common.ingredient.IngredientBridge;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import hellfirepvp.astralsorcery.common.recipe.CustomRecipe;
import hellfirepvp.astralsorcery.common.recipe.altar.effect.AltarEffect;
import hellfirepvp.astralsorcery.common.recipe.altar.output.AltarRecipeOutputModifier;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.IngredientUtil;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import hellfirepvp.astralsorcery.common.util.data.CountIngredient;
import hellfirepvp.astralsorcery.common.util.data.ResolvingRecipeTypeRegistryObject;
import hellfirepvp.astralsorcery.common.util.level.DayTimeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarRecipe extends CustomRecipe<AltarRecipe, AltarCraftingInput> {

    public static final MapCodec<AltarRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            TileAltar.AltarType.CODEC.fieldOf("requiredType").forGetter(AltarRecipe::getRequiredType),
            AltarRecipeGrid.CODEC.fieldOf("grid").forGetter(AltarRecipe::getGrid),
            Codec.list(ItemStack.CODEC).fieldOf("outputs").forGetter(AltarRecipe::getOutputs),
            RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec().optionalFieldOf("focusConstellation").forGetter(AltarRecipe::getFocusConstellation),
            Codec.FLOAT.fieldOf("baseFocusShatterChance").forGetter(AltarRecipe::getBaseFocusShatterChance),
            Codec.INT.fieldOf("duration").forGetter(AltarRecipe::getDuration),
            Codec.BOOL.fieldOf("onlyNight").forGetter(AltarRecipe::isOnlyNight),
            Codec.BOOL.fieldOf("mayChain").forGetter(AltarRecipe::mayChain),
            SetCodec.of(RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec()).fieldOf("requiredStarlight").forGetter(AltarRecipe::getRequiredStarlight),
            Codec.list(LumenStack.CODEC).fieldOf("requiredLumen").forGetter(AltarRecipe::getRequiredLumen),
            Codec.list(FluidStack.CODEC).fieldOf("requiredFluid").forGetter(AltarRecipe::getRequiredFluid),
            Codec.list(CountIngredient.CODEC_NONEMPTY).fieldOf("requiredAdditionalInputs").forGetter(AltarRecipe::getRequiredAdditionalInputs),
            SetCodec.of(RegistriesAS.REGISTRY_ALTAR_EFFECTS.byNameCodec()).fieldOf("effects").forGetter(AltarRecipe::getEffects),
            Codec.list(AltarRecipeOutputModifier.CODEC).fieldOf("outputModifiers").forGetter(AltarRecipe::getOutputModifiers)
    ).apply(inst, AltarRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AltarRecipe> STREAM_CODEC = StreamCodec.of(AltarRecipe::write, AltarRecipe::read);

    private final TileAltar.AltarType requiredType;
    private final AltarRecipeGrid grid;
    private final List<ItemStack> outputs = new ArrayList<>();
    @Nullable
    private final BaseConstellation focusConstellation;
    private final float baseFocusShatterChance;
    private final int duration;
    private final boolean onlyNight;
    private final boolean mayChain;
    private final Set<BaseConstellation> requiredStarlight = new LinkedHashSet<>();
    private final List<LumenStack> requiredLumen = new ArrayList<>();
    private final List<FluidStack> requiredFluid = new ArrayList<>();
    private final List<CountIngredient> requiredAdditionalInputs = new ArrayList<>();

    private final Set<AltarEffect> effects = new HashSet<>();
    private final List<AltarRecipeOutputModifier> outputModifiers = new ArrayList<>();

    public AltarRecipe(
            TileAltar.AltarType requiredType,
            AltarRecipeGrid grid,
            List<ItemStack> outputs,
            Optional<BaseConstellation> focusConstellation,
            float baseFocusShatterChance,
            int duration,
            boolean onlyNight,
            boolean mayChain,
            Set<BaseConstellation> requiredStarlight,
            List<LumenStack> requiredLumen,
            List<FluidStack> requiredFluid,
            List<CountIngredient> requiredAdditionalInputs,
            Set<AltarEffect> effects,
            List<AltarRecipeOutputModifier> outputModifiers) {
        this.requiredType = requiredType;
        this.grid = grid;
        this.outputs.addAll(outputs);
        this.focusConstellation = focusConstellation.orElse(null);
        this.baseFocusShatterChance = baseFocusShatterChance;
        this.duration = Math.max(20, duration);
        this.onlyNight = onlyNight;
        this.mayChain = mayChain;
        this.requiredStarlight.addAll(requiredStarlight);
        this.requiredLumen.addAll(requiredLumen);
        this.requiredFluid.addAll(requiredFluid);
        this.requiredAdditionalInputs.addAll(requiredAdditionalInputs);
        this.effects.addAll(effects);
        this.outputModifiers.addAll(outputModifiers);
    }

    @Override
    public boolean matches(AltarCraftingInput input, Level level) {
        if (this.isOnlyNight() && !DayTimeHelper.isNight(level)) {
            return false;
        }

        if (!input.getAltar().getTileData().getAltarType().isThisLaterOrEqual(this.getRequiredType()) &&
                input.getAltar().getTileData().hasStructure()) {
            return false;
        }
        if (this.getFocusConstellation().isPresent() &&
                (input.getFocusConstellation().isEmpty() || !input.getFocusConstellation().get().equals(this.getFocusConstellation().get()))) {
            return false;
        }

        return this.getGrid().matches(input.getGridInputs(), input.getRelayInputs());
    }

    @Override
    public boolean consumeInputs(AltarCraftingInput input, HolderLookup.Provider registries) {
        throw new UnsupportedOperationException("Altar recipe consumption is handled by or during the active altar recipe.");
    }

    @Override
    public void createOutput(AltarCraftingInput input, HolderLookup.Provider registries) {
        if (input.getAltar().getLevel() instanceof ServerLevel sLevel) {
            List<ItemStack> craftedOutputs = this.getOutputs(input, registries);

            BlockPos pos = input.getAltar().getBlockPos().above();
            craftedOutputs.forEach(stack -> ItemUtil.dropItem(sLevel, pos, stack));
        }
    }

    public List<ItemStack> getOutputs(AltarCraftingInput input, HolderLookup.Provider registries) {
        List<ItemStack> craftedOutputs = this.getOutputs();

        for (AltarRecipeOutputModifier modifier : this.outputModifiers) {
            List<ItemStack> modifiedOutputs = new ArrayList<>();
            for (ItemStack output : craftedOutputs) {
                modifiedOutputs.add(modifier.modifyOutput(output.copy(), input, registries));
            }
            modifiedOutputs.removeIf(ItemStack::isEmpty);
            craftedOutputs = modifiedOutputs;
        }
        return craftedOutputs;
    }

    public List<ItemStack> getOutputsForDisplay(AltarCraftingInput input, HolderLookup.Provider registries) {
        List<ItemStack> craftedOutputs = this.getOutputs();

        for (AltarRecipeOutputModifier modifier : this.outputModifiers) {
            List<ItemStack> modifiedOutputs = new ArrayList<>();
            for (ItemStack output : craftedOutputs) {
                modifiedOutputs.add(modifier.modifyOutputForDisplay(output.copy(), input, registries));
            }
            modifiedOutputs.removeIf(ItemStack::isEmpty);
            craftedOutputs = modifiedOutputs;
        }
        return craftedOutputs;
    }

    public AltarCraftingInput createInputForDisplay(long tick) {
        return AltarCraftingInput.createDisplay(this.getFocusConstellation().orElse(null),
                this.getGrid().getInputs().stream().map(in -> IngredientUtil.getRandomDisplayStack(in, tick)).toList(),
                this.getGrid().getRelayInputs().stream().map(in -> IngredientUtil.getRandomDisplayStack(in, tick)).toList());
    }

    public TileAltar.AltarType getRequiredType() {
        return this.requiredType;
    }

    public AltarRecipeGrid getGrid() {
        return this.grid;
    }

    public List<ItemStack> getOutputs() {
        return this.outputs.stream().map(ItemStack::copy).toList();
    }

    public Optional<BaseConstellation> getFocusConstellation() {
        return Optional.ofNullable(this.focusConstellation);
    }

    public float getBaseFocusShatterChance() {
        return this.getFocusConstellation().isEmpty() ? 0F : this.baseFocusShatterChance;
    }

    public int getDuration() {
        return this.duration;
    }

    public boolean isOnlyNight() {
        return this.onlyNight;
    }

    public boolean mayChain() {
        return this.mayChain;
    }

    public Set<BaseConstellation> getRequiredStarlight() {
        return Collections.unmodifiableSet(this.requiredStarlight);
    }

    public List<LumenStack> getRequiredLumen() {
        return Collections.unmodifiableList(this.requiredLumen);
    }

    public List<FluidStack> getRequiredFluid() {
        return Collections.unmodifiableList(this.requiredFluid);
    }

    public List<CountIngredient> getRequiredAdditionalInputs() {
        return Collections.unmodifiableList(this.requiredAdditionalInputs);
    }

    public Set<AltarEffect> getEffects() {
        return Collections.unmodifiableSet(this.effects);
    }

    public List<AltarRecipeOutputModifier> getOutputModifiers() {
        return Collections.unmodifiableList(this.outputModifiers);
    }

    @Override
    public Supplier<? extends RecipeSerializer<AltarRecipe>> getRecipeSerializer() {
        return RecipeTypesAS.ALTAR_CRAFTING_SERIALIZER;
    }

    @Override
    public ResolvingRecipeTypeRegistryObject<AltarRecipe> getRecipeType() {
        return RecipeTypesAS.ALTAR_CRAFTING_TYPE;
    }

    private static void write(RegistryFriendlyByteBuf buf, AltarRecipe recipe) {
        TileAltar.AltarType.STREAM_CODEC.encode(buf, recipe.getRequiredType());
        AltarRecipeGrid.STREAM_CODEC.encode(buf, recipe.getGrid());
        ItemStack.LIST_STREAM_CODEC.encode(buf, recipe.getOutputs());
        ByteBufCodecs.optional(ByteBufCodecs.registry(RegistriesAS.KEY_CONSTELLATIONS)).encode(buf, recipe.getFocusConstellation());
        ByteBufCodecs.FLOAT.encode(buf, recipe.getBaseFocusShatterChance());
        ByteBufCodecs.INT.encode(buf, recipe.getDuration());
        ByteBufCodecs.BOOL.encode(buf, recipe.isOnlyNight());
        ByteBufCodecs.BOOL.encode(buf, recipe.mayChain());
        ByteBufCodecs.registry(RegistriesAS.KEY_CONSTELLATIONS).apply(SetCodec.streamOp()).encode(buf, recipe.getRequiredStarlight());
        LumenStack.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.getRequiredLumen());
        FluidStack.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.getRequiredFluid());
        CountIngredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.getRequiredAdditionalInputs());
        ByteBufCodecs.registry(RegistriesAS.KEY_ALTAR_EFFECTS).apply(SetCodec.streamOp()).encode(buf, recipe.getEffects());
        AltarRecipeOutputModifier.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.getOutputModifiers());
    }

    private static AltarRecipe read(RegistryFriendlyByteBuf buf) {
        TileAltar.AltarType requiredType = TileAltar.AltarType.STREAM_CODEC.decode(buf);
        AltarRecipeGrid grid = AltarRecipeGrid.STREAM_CODEC.decode(buf);
        List<ItemStack> outputs = ItemStack.LIST_STREAM_CODEC.decode(buf);
        Optional<BaseConstellation> focusConstellation = ByteBufCodecs.optional(ByteBufCodecs.registry(RegistriesAS.KEY_CONSTELLATIONS)).decode(buf);
        float baseFocusShatterChance = ByteBufCodecs.FLOAT.decode(buf);
        int duration = ByteBufCodecs.INT.decode(buf);
        boolean onlyNight = ByteBufCodecs.BOOL.decode(buf);
        boolean mayChain = ByteBufCodecs.BOOL.decode(buf);
        Set<BaseConstellation> requiredStarlightLevels = ByteBufCodecs.registry(RegistriesAS.KEY_CONSTELLATIONS).apply(SetCodec.streamOp()).decode(buf);
        List<LumenStack> requiredLumen = LumenStack.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
        List<FluidStack> requiredFluid = FluidStack.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
        List<CountIngredient> requiredAdditionalInputs = CountIngredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
        Set<AltarEffect> effects = ByteBufCodecs.registry(RegistriesAS.KEY_ALTAR_EFFECTS).apply(SetCodec.streamOp()).decode(buf);
        List<AltarRecipeOutputModifier> outputModifiers = AltarRecipeOutputModifier.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
        return new AltarRecipe(requiredType, grid, outputs, focusConstellation, baseFocusShatterChance, duration, onlyNight, mayChain, requiredStarlightLevels, requiredLumen, requiredFluid, requiredAdditionalInputs, effects, outputModifiers);
    }
}
