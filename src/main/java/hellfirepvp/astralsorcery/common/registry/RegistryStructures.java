/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
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
import net.minecraft.util.ResourceLocation;

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
        EMPTY = register(new PatternBlockArray(AstralSorcery.key("empty")));

        STRUCT_RITUAL_PEDESTAL = register(new PatternRitualPedestal());
        STRUCT_ENHANCED_COLLECTOR_CRYSTAL = register(new PatternEnhancedCollectorCrystal());

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
