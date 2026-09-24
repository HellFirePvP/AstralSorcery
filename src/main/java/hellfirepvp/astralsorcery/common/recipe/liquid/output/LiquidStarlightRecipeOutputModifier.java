/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.liquid.output;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipeInput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidStarlightRecipeOutputModifier
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class LiquidStarlightRecipeOutputModifier {

    public static final Codec<LiquidStarlightRecipeOutputModifier> CODEC = RegistriesAS.REGISTRY_LIQUID_STARLIGHT_OUTPUT_MODIFIER_TYPES.byNameCodec()
            .dispatch(LiquidStarlightRecipeOutputModifier::getType, LiquidStarlightRecipeOutputModifier.Type::codec);
    public static final StreamCodec<RegistryFriendlyByteBuf, LiquidStarlightRecipeOutputModifier> STREAM_CODEC = ByteBufCodecs.registry(RegistriesAS.KEY_LIQUID_STARLIGHT_OUTPUT_MODIFIER_TYPES)
            .dispatch(LiquidStarlightRecipeOutputModifier::getType, LiquidStarlightRecipeOutputModifier.Type::streamCodec);

    public abstract LiquidStarlightRecipeOutputModifier.Type<?> getType();

    public boolean isValidInputForOutput(LiquidStarlightRecipeInput input, List<ItemEntity> otherValidInputs) {
        return true;
    }

    public abstract boolean isOutput(LiquidStarlightRecipeInput input, ItemEntity output);

    public abstract void createOutput(LiquidStarlightRecipe recipe, LiquidStarlightRecipeInput input);

    @OnlyIn(Dist.CLIENT)
    public void playCraftingEffects(LiquidStarlightRecipe recipe, LiquidStarlightRecipeInput input, RandomSource rand, int craftingTick) {}

    public record Type<T extends LiquidStarlightRecipeOutputModifier>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
    }
}
