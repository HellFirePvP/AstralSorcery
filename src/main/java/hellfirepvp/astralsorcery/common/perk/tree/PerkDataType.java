/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkDataType
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record PerkDataType<D extends AbstractPerk.Data>(MapCodec<D> codec, StreamCodec<RegistryFriendlyByteBuf, D> syncCodec, Supplier<D> emptyDataFn) {

    public static final Codec<AbstractPerk.Data> DATA_CODEC = RegistriesAS.REGISTRY_PERK_DATA_TYPES.byNameCodec()
            .dispatch(AbstractPerk.Data::getType, PerkDataType::codec);
    public static final StreamCodec<RegistryFriendlyByteBuf, AbstractPerk.Data> DATA_SYNC_CODEC = ByteBufCodecs.registry(RegistriesAS.KEY_PERK_DATA_TYPES)
            .dispatch(AbstractPerk.Data::getType, PerkDataType::syncCodec);

}
