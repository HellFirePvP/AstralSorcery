package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.registry.multiblock.MultiblockAltarAttenuation;
import hellfirepvp.astralsorcery.common.registry.multiblock.MultiblockRitualPedestal;
import hellfirepvp.astralsorcery.common.registry.structures.StructureAncientShrine;

import static hellfirepvp.astralsorcery.common.lib.MultiBlockArrays.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryStructures
 * Created by HellFirePvP
 * Date: 16.05.2016 / 15:45
 */
public class RegistryStructures {

    public static void init() {
        ancientShrine = new StructureAncientShrine();

        patternRitualPedestal = new MultiblockRitualPedestal();
        patternAltarAttenuation = new MultiblockAltarAttenuation();
    }

}
