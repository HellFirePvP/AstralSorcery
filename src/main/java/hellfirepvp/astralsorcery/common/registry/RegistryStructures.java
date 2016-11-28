package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.registry.multiblock.MultiblockAltarAttenuation;
import hellfirepvp.astralsorcery.common.registry.multiblock.MultiblockAltarConstellation;
import hellfirepvp.astralsorcery.common.registry.multiblock.MultiblockAttunementFrame;
import hellfirepvp.astralsorcery.common.registry.multiblock.MultiblockRitualPedestal;
import hellfirepvp.astralsorcery.common.registry.structures.StructureAncientShrine;
import hellfirepvp.astralsorcery.common.registry.structures.StructureDesertShrine;

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
        desertShrine = new StructureDesertShrine();

        patternRitualPedestal = new MultiblockRitualPedestal();
        patternAltarAttenuation = new MultiblockAltarAttenuation();
        patternAltarConstellation = new MultiblockAltarConstellation();
        patternAttunementFrame = new MultiblockAttunementFrame();
    }

}
