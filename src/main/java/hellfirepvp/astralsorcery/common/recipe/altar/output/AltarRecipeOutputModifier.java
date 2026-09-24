/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar.output;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarRecipeOutputModifier
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class AltarRecipeOutputModifier {

    public static final Codec<AltarRecipeOutputModifier> CODEC = RegistriesAS.REGISTRY_ALTAR_OUTPUT_MODIFIER_TYPES.byNameCodec()
            .dispatch(AltarRecipeOutputModifier::getType, AltarRecipeOutputModifier.Type::codec);
    public static final StreamCodec<RegistryFriendlyByteBuf, AltarRecipeOutputModifier> STREAM_CODEC = ByteBufCodecs.registry(RegistriesAS.KEY_ALTAR_OUTPUT_MODIFIER_TYPES)
            .dispatch(AltarRecipeOutputModifier::getType, AltarRecipeOutputModifier.Type::streamCodec);

    public abstract AltarRecipeOutputModifier.Type<?> getType();

    public abstract ItemStack modifyOutput(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries);

    public ItemStack modifyOutputForDisplay(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        return this.modifyOutput(output, input, registries);
    }

    public record Type<T extends AltarRecipeOutputModifier>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
    }
}
