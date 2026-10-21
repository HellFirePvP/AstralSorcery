/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.lumen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.recipe.CustomRecipe;
import hellfirepvp.astralsorcery.common.util.data.ResolvingRecipeTypeRegistryObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenGenerationRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenGenerationRecipe extends CustomRecipe<LumenGenerationRecipe, LumenGenerationRecipeInput> {

    public static final MapCodec<LumenGenerationRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("input").forGetter(LumenGenerationRecipe::getInput),
            Lumen.NON_EMPTY_CODEC.fieldOf("produced_lumen").forGetter(LumenGenerationRecipe::getProducedLumen),
            Codec.INT.fieldOf("produced_lumen_amount").forGetter(LumenGenerationRecipe::getProducedLumenAmount),
            Codec.FLOAT.fieldOf("production_attempt_multiplier").forGetter(LumenGenerationRecipe::getProductionAttemptMultiplier),
            Codec.FLOAT.fieldOf("attempt_starlight_consumption").forGetter(LumenGenerationRecipe::getAttemptStarlightConsumption),
            Codec.FLOAT.fieldOf("catalyst_shatter_multiplier").forGetter(LumenGenerationRecipe::getCatalystShatterMultiplier),
            Codec.unboundedMap(Lumen.NON_EMPTY_CODEC, Codec.INT).fieldOf("lumen_combination_inputs").orElse(Collections.emptyMap()).forGetter(LumenGenerationRecipe::getLumenCombinationInputs)
    ).apply(inst, LumenGenerationRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, LumenGenerationRecipe> STREAM_CODEC = NeoForgeStreamCodecs.composite(
            Ingredient.CONTENTS_STREAM_CODEC, LumenGenerationRecipe::getInput,
            Lumen.STREAM_CODEC, LumenGenerationRecipe::getProducedLumen,
            ByteBufCodecs.VAR_INT, LumenGenerationRecipe::getProducedLumenAmount,
            ByteBufCodecs.FLOAT, LumenGenerationRecipe::getProductionAttemptMultiplier,
            ByteBufCodecs.FLOAT, LumenGenerationRecipe::getAttemptStarlightConsumption,
            ByteBufCodecs.FLOAT, LumenGenerationRecipe::getCatalystShatterMultiplier,
            ByteBufCodecs.map(HashMap::new, Lumen.STREAM_CODEC, ByteBufCodecs.VAR_INT), LumenGenerationRecipe::getLumenCombinationInputs,
            LumenGenerationRecipe::new
    );

    private final Ingredient input;
    private final Lumen producedLumen;
    private final int producedLumenAmount;
    private final float productionAttemptMultiplier;
    private final float attemptStarlightConsumption;
    private final float catalystShatterMultiplier;
    private final Map<Lumen, Integer> lumenCombinationInputs;

    public LumenGenerationRecipe(Ingredient input,
                                 Lumen producedLumen,
                                 int producedLumenAmount,
                                 float productionAttemptMultiplier,
                                 float attemptStarlightConsumption,
                                 float catalystShatterMultiplier,
                                 Map<Lumen, Integer> lumenCombinationInputs) {
        this.input = input;
        this.producedLumen = producedLumen;
        this.producedLumenAmount = producedLumenAmount;
        this.productionAttemptMultiplier = productionAttemptMultiplier;
        this.attemptStarlightConsumption = attemptStarlightConsumption;
        this.catalystShatterMultiplier = catalystShatterMultiplier;
        this.lumenCombinationInputs = lumenCombinationInputs;
    }

    @Override
    public boolean consumeInputs(LumenGenerationRecipeInput input, HolderLookup.Provider registries) {
        throw new UnsupportedOperationException("Lumen generation input consumption is not handled by its recipe.");
    }

    @Override
    public void createOutput(LumenGenerationRecipeInput input, HolderLookup.Provider registries) {
    }

    @Override
    public boolean matches(LumenGenerationRecipeInput input, Level level) {
        return this.input.test(input.getInputStack());
    }

    public Ingredient getInput() {
        return this.input;
    }

    public Lumen getProducedLumen() {
        return this.producedLumen;
    }

    public int getProducedLumenAmount() {
        return this.producedLumenAmount;
    }

    public float getProductionAttemptMultiplier() {
        return this.productionAttemptMultiplier;
    }

    public float getAttemptStarlightConsumption() {
        return this.attemptStarlightConsumption;
    }

    public float getCatalystShatterMultiplier() {
        return this.catalystShatterMultiplier;
    }

    public Map<Lumen, Integer> getLumenCombinationInputs() {
        return Collections.unmodifiableMap(this.lumenCombinationInputs);
    }

    @Override
    public Supplier<? extends RecipeSerializer<LumenGenerationRecipe>> getRecipeSerializer() {
        return RecipeTypesAS.LUMEN_GENERATION_SERIALIZER;
    }

    @Override
    public ResolvingRecipeTypeRegistryObject<LumenGenerationRecipe> getRecipeType() {
        return RecipeTypesAS.LUMEN_GENERATION_TYPE;
    }
}
