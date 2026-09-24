/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure.observer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.observerlib.api.ObserverProvider;
import hellfirepvp.observerlib.api.structure.MatchableStructure;
import hellfirepvp.observerlib.common.change.ChangeObserverStructure;
import hellfirepvp.observerlib.common.change.ObserverProviderStructure;
import hellfirepvp.observerlib.common.registry.RegistryProviders;
import hellfirepvp.observerlib.common.util.CodecUtil;
import net.minecraft.core.BlockPos;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CompoundObserverProviderStructure
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CompoundObserverProviderStructure extends ObserverProvider<CompoundChangeObserverStructure> {

    public static final Codec<CompoundObserverProviderStructure> PROVIDER_CODEC = RegistryProviders.getRegistry().byNameCodec()
            .xmap(provider -> CodecUtil.informedCast(provider, CompoundObserverProviderStructure.class), Function.identity());
    public static final MapCodec<CompoundChangeObserverStructure> OBSERVER_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            PROVIDER_CODEC.fieldOf("provider").forGetter(CompoundChangeObserverStructure::getProvider),
            BlockPos.CODEC.listOf().listOf().fieldOf("mismatches").forGetter(CompoundChangeObserverStructure::getOrderedMismatches)
    ).apply(builder, (provider, mismatches) -> provider.newObserver().addOrderedMismatches(mismatches)));

    private final List<MatchableStructure> structures;

    public CompoundObserverProviderStructure(List<MatchableStructure> structures) {
        this.structures = structures;
    }

    public List<MatchableStructure> getStructures() {
        return Collections.unmodifiableList(this.structures);
    }

    @Override
    public MapCodec<CompoundChangeObserverStructure> codec() {
        return OBSERVER_CODEC;
    }

    @Nonnull
    @Override
    public CompoundChangeObserverStructure newObserver() {
        return new CompoundChangeObserverStructure(this, this.getStructures());
    }
}
