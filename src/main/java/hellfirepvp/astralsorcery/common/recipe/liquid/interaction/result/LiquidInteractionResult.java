/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.liquid.interaction.result;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidInteractionResult
 * Created by HellFirePvP
 * Date: 11.04.2026
 */
public abstract class LiquidInteractionResult {

    public static final Codec<LiquidInteractionResult> CODEC = RegistriesAS.REGISTRY_LIQUID_INTERACTION_RESULT_TYPES.byNameCodec()
            .dispatch(LiquidInteractionResult::getType, LiquidInteractionResult.Type::codec);
    public static final StreamCodec<RegistryFriendlyByteBuf, LiquidInteractionResult> STREAM_CODEC =
            ByteBufCodecs.registry(RegistriesAS.KEY_LIQUID_INTERACTION_RESULT_TYPES)
                    .dispatch(LiquidInteractionResult::getType, LiquidInteractionResult.Type::streamCodec);

    public abstract ItemStack getDisplayOutput();

    public abstract Type<?> getType();

    public abstract void apply(ServerLevel level, Vec3 at);

    public record Type<T extends LiquidInteractionResult>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {}
}
