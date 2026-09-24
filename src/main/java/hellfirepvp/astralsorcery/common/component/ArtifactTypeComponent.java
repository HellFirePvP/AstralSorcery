/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.component;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.artifact.ArtifactType;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactTypeComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record ArtifactTypeComponent(ArtifactType type) {

    public static final Codec<ArtifactTypeComponent> CODEC =
            RegistriesAS.REGISTRY_ARTIFACT_TYPES.byNameCodec().xmap(ArtifactTypeComponent::new, ArtifactTypeComponent::type);
    public static final StreamCodec<RegistryFriendlyByteBuf, ArtifactTypeComponent> STREAM_CODEC =
            ByteBufCodecs.registry(RegistriesAS.KEY_ARTIFACT_TYPES).map(ArtifactTypeComponent::new, ArtifactTypeComponent::type);

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ArtifactTypeComponent that = (ArtifactTypeComponent) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type);
    }
}
