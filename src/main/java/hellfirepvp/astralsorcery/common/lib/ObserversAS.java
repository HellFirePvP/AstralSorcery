/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.focal.observer.FocusCrystalFilamentProvider;
import hellfirepvp.astralsorcery.common.structure.*;
import hellfirepvp.astralsorcery.common.structure.observer.CompoundObserverProviderStructure;
import hellfirepvp.astralsorcery.common.util.data.ObserverRegistryObject;
import hellfirepvp.observerlib.api.ObserverProvider;
import hellfirepvp.observerlib.api.structure.MatchableStructure;
import hellfirepvp.observerlib.api.util.StructureBlockArray;
import hellfirepvp.observerlib.common.change.ObserverProviderStructure;
import hellfirepvp.observerlib.common.registry.RegistryProviders;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ObserversAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ObserversAS {

    public static final DeferredRegister<ObserverProvider<?>> OBSERVER_REGISTER =
            DeferredRegister.create(RegistryProviders.REGISTRY_KEY, AstralSorcery.MODID);

    public static final ObserverRegistryObject STRUCTURE_EMPTY = registerStructure("empty", StructureBlockArray::new);
    public static final ObserverRegistryObject STRUCTURE_ALTAR_T2 =
            registerCompoundStructure("altar_t2", PatternAltarT2::new, PatternAltarT2Expanded::new);
    public static final ObserverRegistryObject STRUCTURE_ALTAR_T3 =
            registerStructure("altar_t3", PatternAltarT3::new);
    public static final ObserverRegistryObject STRUCTURE_ALTAR_T4 =
            registerStructure("altar_t4", PatternAltarT4::new);
    public static final ObserverRegistryObject STRUCTURE_INFUSER =
            registerStructure("infuser", PatternInfuser::new);
    public static final ObserverRegistryObject STRUCTURE_ATTUNEMENT_ALTAR =
            registerStructure("attunement_altar", PatternAttunementAltar::new);
    public static final ObserverRegistryObject STRUCTURE_CELESTIAL_GATEWAY =
            registerStructure("celestial_gateway", PatternCelestialGateway::new);

    public static final ObserverRegistryObject FOCUS_CRYSTAL_FILAMENTS =
            register("focus_crystal_filaments", FocusCrystalFilamentProvider::new);

    private static ObserverRegistryObject registerCompoundStructure(String name, Supplier<StructureBlockArray>... structuresSupplier) {
        DeferredHolder<ObserverProvider<?>, ObserverProvider<?>> providerObj =
                OBSERVER_REGISTER.register(name, () -> {
                    List<MatchableStructure> structures = Arrays.stream(structuresSupplier)
                            .map(Supplier::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    return new CompoundObserverProviderStructure(structures);
                });
        return new ObserverRegistryObject(providerObj);
    }

    private static ObserverRegistryObject registerStructure(String name, Supplier<StructureBlockArray> structureSupplier) {
        return register(name, () -> new ObserverProviderStructure(structureSupplier.get()));
    }

    private static <T extends ObserverProvider<?>> ObserverRegistryObject register(String name, Supplier<T> provider) {
        DeferredHolder<ObserverProvider<?>, ObserverProvider<?>> providerObj = OBSERVER_REGISTER.register(name, provider);
        return new ObserverRegistryObject(providerObj);
    }

    public static Optional<ObserverRegistryObject> getByName(ResourceKey<ObserverProvider<?>> key) {
        return OBSERVER_REGISTER.getEntries().stream()
                .filter(entry -> entry.getKey().equals(key))
                .findFirst()
                .map(ObserverRegistryObject::new);
    }
}
