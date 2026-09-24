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

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenCrystallizationRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenCrystallizationRecipe extends CustomRecipe<LumenCrystallizationRecipe, LumenCrystallizationRecipeInput> {

    public static final MapCodec<LumenCrystallizationRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("input").forGetter(LumenCrystallizationRecipe::getInput),
            RegistriesAS.REGISTRY_LUMEN.byNameCodec().fieldOf("lumen_to_crystallize").forGetter(LumenCrystallizationRecipe::getLumenToCrystallize),
            Codec.FLOAT.fieldOf("catalyst_shatter_multiplier").forGetter(LumenCrystallizationRecipe::getCatalystShatterMultiplier),
            Codec.intRange(1, 1900).fieldOf("lumen_consumed_per_operation").forGetter(LumenCrystallizationRecipe::getLumenConsumedPerOperation)
    ).apply(inst, LumenCrystallizationRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LumenCrystallizationRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            LumenCrystallizationRecipe::getInput,
            ByteBufCodecs.fromCodecWithRegistriesTrusted(RegistriesAS.REGISTRY_LUMEN.byNameCodec()),
            LumenCrystallizationRecipe::getLumenToCrystallize,
            ByteBufCodecs.FLOAT,
            LumenCrystallizationRecipe::getCatalystShatterMultiplier,
            ByteBufCodecs.INT,
            LumenCrystallizationRecipe::getLumenConsumedPerOperation,
            LumenCrystallizationRecipe::new
    );

    private final Ingredient input;
    private final Lumen lumenToCrystallize;
    private final float catalystShatterMultiplier;
    private final int lumenConsumedPerOperation;

    public LumenCrystallizationRecipe(Ingredient input, Lumen lumenToCrystallize, float catalystShatterMultiplier, int lumenConsumedPerOperation) {
        this.input = input;
        this.lumenToCrystallize = lumenToCrystallize;
        this.lumenConsumedPerOperation = lumenConsumedPerOperation;
        this.catalystShatterMultiplier = catalystShatterMultiplier;
    }

    public Ingredient getInput() {
        return this.input;
    }

    public Lumen getLumenToCrystallize() {
        return this.lumenToCrystallize;
    }

    public float getCatalystShatterMultiplier() {
        return this.catalystShatterMultiplier;
    }

    public int getLumenConsumedPerOperation() {
        return this.lumenConsumedPerOperation;
    }

    @Override
    public boolean consumeInputs(LumenCrystallizationRecipeInput input, HolderLookup.Provider registries) {
        throw new UnsupportedOperationException("Recipe inputs are consumed in the crystallizer tile directly.");
    }

    @Override
    public void createOutput(LumenCrystallizationRecipeInput input, HolderLookup.Provider registries) {
        throw new UnsupportedOperationException("Recipe outputs are provided in the crystallizer tile directly.");
    }

    @Override
    public boolean matches(LumenCrystallizationRecipeInput input, Level level) {
        return input.getCatalystInput().map(this.input::test, this.lumenToCrystallize::equals);
    }

    @Override
    public Supplier<? extends RecipeSerializer<LumenCrystallizationRecipe>> getRecipeSerializer() {
        return RecipeTypesAS.LUMEN_CRYSTALLIZATION_SERIALIZER;
    }

    @Override
    public ResolvingRecipeTypeRegistryObject<LumenCrystallizationRecipe> getRecipeType() {
        return RecipeTypesAS.LUMEN_CRYSTALLIZATION_TYPE;
    }
}
