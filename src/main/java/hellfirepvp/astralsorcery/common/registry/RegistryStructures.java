/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.structure.PatternRitualPedestal;
import hellfirepvp.astralsorcery.common.structure.StructureDesertShrine;
import hellfirepvp.astralsorcery.common.structure.StructureMountainShrine;
import hellfirepvp.astralsorcery.common.structure.StructureSmallShrine;
import hellfirepvp.observerlib.api.structure.MatchableStructure;
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

    public static void registerStructures() {
        STRUCT_RITUAL_PEDESTAL = register(new PatternRitualPedestal());

        STRUCT_DESERT_SHRINE = register(new StructureDesertShrine());
        STRUCT_MOUNTAIN_SHRINE = register(new StructureMountainShrine());
        STRUCT_SMALL_SHRINE = register(new StructureSmallShrine());
    }

    private static <T extends MatchableStructure> T register(T struct) {
        AstralSorcery.getProxy().getRegistryPrimer().register(struct);
        ObserverProviderStructure structureProvider = new ObserverProviderStructure(struct.getRegistryName());
        AstralSorcery.getProxy().getRegistryPrimer().register(structureProvider);
        return struct;
    }

}
