/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.multiblock.*;
import hellfirepvp.astralsorcery.common.registry.structures.*;
import hellfirepvp.astralsorcery.common.structure.StructureMatcherRegistry;
import hellfirepvp.astralsorcery.common.structure.StructureRegistry;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import hellfirepvp.astralsorcery.common.structure.match.StructureMatcherPatternArray;
import net.minecraft.util.ResourceLocation;

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
        smallShrine = new StructureSmallShrine();
        treasureShrine = new StructureTreasureShrine();
        smallRuin = new StructureSmallRuin();

        patternRitualPedestal       = registerPattern(new MultiblockRitualPedestal());
        patternAltarAttunement      = registerPattern(new MultiblockAltarAttunement());
        patternAltarConstellation   = registerPattern(new MultiblockAltarConstellation());
        patternAltarTrait           = registerPattern(new MultiblockAltarTrait());
        patternAttunementFrame      = registerPattern(new MultiblockAttunementFrame());
        patternStarlightInfuser     = registerPattern(new MultiblockStarlightInfuser());
        patternCollectorRelay       = registerPattern(new MultiblockStarlightRelay());
        patternCelestialGateway     = registerPattern(new MultiblockGateway());
        patternCollectorEnhancement = registerPattern(new MultiblockCrystalEnhancement());
        patternFountain             = registerPattern(new MultiblockFountain());

        patternSmallRuin = new PatternBlockArray(
                new ResourceLocation(AstralSorcery.MODID,"pattern_small_ruin"));
        patternSmallRuin.addAll(smallRuin);
        registerPattern(patternSmallRuin);

        patternRitualPedestalWithLink = new MultiblockRitualPedestal(
                new ResourceLocation(AstralSorcery.MODID,"pattern_ritual_pedestal_link"));
        patternRitualPedestalWithLink.addBlock(0, 5, 0, BlocksAS.ritualLink.getDefaultState());
        registerPattern(patternRitualPedestalWithLink);
    }

    private static <T extends PatternBlockArray> T registerPattern(T pattern) {
        StructureRegistry.INSTANCE.register(pattern);
        StructureMatcherRegistry.INSTANCE.register(() -> new StructureMatcherPatternArray(pattern.getRegistryName()));
        return pattern;
    }

}
