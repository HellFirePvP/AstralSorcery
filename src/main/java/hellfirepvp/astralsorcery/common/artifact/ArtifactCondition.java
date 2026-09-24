/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.entity.item.ItemEntityArtifact;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactCondition
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ArtifactCondition {

    public static final Codec<ArtifactCondition> CODEC = RegistriesAS.REGISTRY_ARTIFACT_CONDITION_TYPES.byNameCodec()
            .dispatch(ArtifactCondition::unwrapType, ArtifactCondition.Type::codec);
    public static final StreamCodec<RegistryFriendlyByteBuf, ArtifactCondition> STREAM_CODEC = ByteBufCodecs.registry(RegistriesAS.KEY_ARTIFACT_CONDITION_TYPES)
            .dispatch(ArtifactCondition::unwrapType, ArtifactCondition.Type::streamCodec);

    private final ResourceLocation id;

    protected ArtifactCondition(ResourceLocation id) {
        this.id = id;
    }

    public final ResourceLocation getId() {
        return this.id;
    }

    public abstract DeferredType<?> getType();

    public Type<?> unwrapType() {
        return this.getType().holder().get();
    }

    public abstract List<Vector3> isFulfilled(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifactEntity);

    public abstract void fulfillCondition(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifactEntity);

    public abstract Component getDisplayHint(RandomSource rand, boolean wasSuccessful);

    public record Type<T extends ArtifactCondition>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {}

    public record DeferredType<T extends ArtifactCondition>(DeferredHolder<ArtifactCondition.Type<?>, ArtifactCondition.Type<T>> holder) {}

    @FunctionalInterface
    public interface Provider extends Function<ResourceLocation, ArtifactCondition> {}
}
