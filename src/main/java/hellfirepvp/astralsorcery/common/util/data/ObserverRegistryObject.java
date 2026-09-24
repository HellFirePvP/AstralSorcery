/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import hellfirepvp.astralsorcery.common.structure.observer.CompoundObserverProviderStructure;
import hellfirepvp.observerlib.api.ChangeObserver;
import hellfirepvp.observerlib.api.ChangeSubscriber;
import hellfirepvp.observerlib.api.ObserverHelper;
import hellfirepvp.observerlib.api.ObserverProvider;
import hellfirepvp.observerlib.api.structure.MatchableStructure;
import hellfirepvp.observerlib.api.util.StructureBlockArray;
import hellfirepvp.observerlib.common.change.ObserverProviderStructure;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ObserverRegistryObject
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record ObserverRegistryObject(DeferredHolder<ObserverProvider<?>, ? extends ObserverProvider<?>> observer) {

    public boolean isProviderFor(ChangeSubscriber<?> subscriber) {
        return this.isProviderFor(subscriber.getObserver());
    }

    public boolean isProviderFor(ChangeObserver<?> observer) {
        return this.isProviderFor(observer.getProvider());
    }

    public boolean isProviderFor(ObserverProvider<?> provider) {
        return this.observer().get() == provider;
    }

    public Component getObserverName() {
        return Component.translatable(Util.makeDescriptionId("observer_provider", this.observer().getId()));
    }

    public ChangeSubscriber<?> createSubscriber(Level level, BlockPos pos) {
        return ObserverHelper.getHelper().observeArea(level, pos, this.observer().get());
    }

    public static Optional<StructureBlockArray> extractRequiredStructure(ObserverProvider<?> observerProvider) {
        return extractRequiredStructures(observerProvider).stream().findFirst();
    }

    public static List<StructureBlockArray> extractRequiredStructures(ObserverProvider<?> observerProvider) {
        List<MatchableStructure> structures = new ArrayList<>();
        if (observerProvider instanceof ObserverProviderStructure structureProvider) {
            structures.add(structureProvider.getStructure());
        } else if (observerProvider instanceof CompoundObserverProviderStructure compoundProvider) {
            structures.addAll(compoundProvider.getStructures());
        }
        return structures.stream()
                .filter(matchableStructure -> matchableStructure instanceof StructureBlockArray)
                .map(matchableStructure -> (StructureBlockArray) matchableStructure)
                .toList();
    }
}
