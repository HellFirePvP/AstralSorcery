/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.structure.*;
import hellfirepvp.observerlib.api.structure.MatchableStructure;
import hellfirepvp.observerlib.api.util.PatternBlockArray;
import hellfirepvp.observerlib.common.change.ObserverProviderStructure;

import static hellfirepvp.astralsorcery.common.lib.StructuresAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryStructures
 * Created by HellFirePvP
 * Date: 09.07.2019 / 19:36
 */
public class RegistryStructures {

    private RegistryStructures() {}

    public static void init() {
        EMPTY = register(new PatternBlockArray(AstralSorcery.key("empty")));

        STRUCT_ALTAR_ATTUNEMENT = register(new PatternAltarAttunement());
        STRUCT_ALTAR_CONSTELLATION = register(new PatternAltarConstellation());
        STRUCT_ALTAR_TRAIT = register(new PatternAltarTrait());
        STRUCT_RITUAL_PEDESTAL = register(new PatternRitualPedestal());
        STRUCT_INFUSER = register(new PatternInfuser());
        STRUCT_ENHANCED_COLLECTOR_CRYSTAL = register(new PatternEnhancedCollectorCrystal());
        STRUCT_SPECTRAL_RELAY = register(new PatternSpectralRelay());
        STRUCT_ATTUNEMENT_ALTAR = register(new PatternAttunementAltar());
    }

    private static <T extends MatchableStructure> T register(T struct) {
        AstralSorcery.getProxy().getRegistryPrimer().register(struct);
        ObserverProviderStructure structureProvider = new ObserverProviderStructure(struct.getRegistryName());
        AstralSorcery.getProxy().getRegistryPrimer().register(structureProvider);
        return struct;
    }

}
