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
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.recipe.CustomRecipe;
import hellfirepvp.astralsorcery.common.util.data.ResolvingRecipeTypeRegistryObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

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
            RegistriesAS.REGISTRY_LUMEN.byNameCodec().fieldOf("produced_lumen").forGetter(LumenGenerationRecipe::getProducedLumen),
            Codec.INT.fieldOf("produced_lumen_amount").forGetter(LumenGenerationRecipe::getProducedLumenAmount),
            Codec.FLOAT.fieldOf("production_attempt_multiplier").forGetter(LumenGenerationRecipe::getProductionAttemptMultiplier),
            Codec.FLOAT.fieldOf("attempt_starlight_consumption").forGetter(LumenGenerationRecipe::getAttemptStarlightConsumption),
            Codec.FLOAT.fieldOf("catalyst_shatter_multiplier").forGetter(LumenGenerationRecipe::getCatalystShatterMultiplier),
            Codec.unboundedMap(RegistriesAS.REGISTRY_LUMEN.byNameCodec(), Codec.INT).fieldOf("lumen_combination_inputs").orElse(Collections.emptyMap()).forGetter(LumenGenerationRecipe::getLumenCombinationInputs)
    ).apply(inst, LumenGenerationRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, LumenGenerationRecipe> STREAM_CODEC = StreamCodec.of(LumenGenerationRecipe::write, LumenGenerationRecipe::read);

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

    private static void write(RegistryFriendlyByteBuf buf, LumenGenerationRecipe recipe) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getInput());
        ByteBufCodecs.fromCodecWithRegistriesTrusted(RegistriesAS.REGISTRY_LUMEN.byNameCodec()).encode(buf, recipe.getProducedLumen());
        ByteBufCodecs.INT.encode(buf, recipe.getProducedLumenAmount());
        ByteBufCodecs.FLOAT.encode(buf, recipe.getProductionAttemptMultiplier());
        ByteBufCodecs.FLOAT.encode(buf, recipe.getAttemptStarlightConsumption());
        ByteBufCodecs.FLOAT.encode(buf, recipe.getCatalystShatterMultiplier());
        ByteBufCodecs.map(i -> (Map<Lumen, Integer>) new HashMap<Lumen, Integer>(), ByteBufCodecs.fromCodecWithRegistriesTrusted(RegistriesAS.REGISTRY_LUMEN.byNameCodec()), ByteBufCodecs.INT)
                .encode(buf, recipe.getLumenCombinationInputs());
    }

    private static LumenGenerationRecipe read(RegistryFriendlyByteBuf buf) {
        Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
        Lumen producedLumen = ByteBufCodecs.fromCodecWithRegistriesTrusted(RegistriesAS.REGISTRY_LUMEN.byNameCodec()).decode(buf);
        int producedLumenAmount = ByteBufCodecs.INT.decode(buf);
        float productionAttemptMultiplier = ByteBufCodecs.FLOAT.decode(buf);
        float attemptStarlightConsumption = ByteBufCodecs.FLOAT.decode(buf);
        float catalystShatterMultiplier = ByteBufCodecs.FLOAT.decode(buf);
        Map<Lumen, Integer> lumenCombinationInputs = ByteBufCodecs.map(i -> new HashMap<>(), ByteBufCodecs.fromCodecWithRegistriesTrusted(RegistriesAS.REGISTRY_LUMEN.byNameCodec()), ByteBufCodecs.INT).decode(buf);
        return new LumenGenerationRecipe(input, producedLumen, producedLumenAmount, productionAttemptMultiplier, attemptStarlightConsumption, catalystShatterMultiplier, lumenCombinationInputs);
    }
}
