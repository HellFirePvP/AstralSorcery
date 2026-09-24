/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.component;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

import java.util.Objects;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record LumenComponent(Holder<Lumen> lumen) {

    public static final LumenComponent EMPTY = new LumenComponent(LumenAS.NONE);

    public static final Codec<LumenComponent> CODEC =
            RegistriesAS.REGISTRY_LUMEN.holderByNameCodec().xmap(LumenComponent::new, LumenComponent::lumen);
    public static final StreamCodec<RegistryFriendlyByteBuf, LumenComponent> STREAM_CODEC =
            ByteBufCodecs.holderRegistry(RegistriesAS.KEY_LUMEN).map(LumenComponent::new, LumenComponent::lumen);

    public static Optional<LumenComponent> of(Lumen lumen) {
        return RegistriesAS.REGISTRY_LUMEN.getResourceKey(lumen)
                .flatMap(RegistriesAS.REGISTRY_LUMEN::getHolder)
                .map(LumenComponent::new);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LumenComponent that = (LumenComponent) o;
        return Objects.equals(lumen, that.lumen);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(lumen);
    }

}
