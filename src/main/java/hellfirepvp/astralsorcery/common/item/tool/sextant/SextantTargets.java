/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool.sextant;

import hellfirepvp.astralsorcery.common.data.world.data.StructureGenBuffer;
import hellfirepvp.astralsorcery.common.util.StructureFinder;
import net.minecraftforge.common.BiomeDictionary;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SextantTargets
 * Created by HellFirePvP
 * Date: 25.02.2018 / 23:30
 */
public class SextantTargets {

    public static final SextantFinder.ASStructure TARGET_MOUNTAIN_SHRINE =
            new SextantFinder.ASStructure("mountain_shrine", 0xFF5CE8D6, false, StructureGenBuffer.StructureType.MOUNTAIN);

    public static final SextantFinder.ASStructure TARGET_DESERT_SHRINE =
            new SextantFinder.ASStructure("desert_shrine", 0xFFFFC82E, false, StructureGenBuffer.StructureType.DESERT);

    public static final SextantFinder.ASStructure TARGET_SMALL_SHRINE =
            new SextantFinder.ASStructure("small_shrine", 0xFF2ED400, false, StructureGenBuffer.StructureType.SMALL);

    public static final SextantFinder.Structure TARGET_VANILLA_VILLAGE =
            new SextantFinder.Structure("village", 0xFFC39909, true, StructureFinder.STRUCT_VILLAGE);

    public static final SextantFinder.Structure TARGET_VANILLA_MONUMENT =
            new SextantFinder.Structure("oceanmonument", 0xFF8F38E6, true, StructureFinder.STRUCT_MONUMENT);

    public static final SextantFinder.Structure TARGET_VANILLA_TEMPLE =
            new SextantFinder.Structure("temple", 0xFFC3C3C3, true, StructureFinder.STRUCT_TEMPLE);

    public static final SextantFinder.Structure TARGET_VANILLA_FORTRESS =
            new SextantFinder.Structure("fortress", 0xFF9C1D15, true, StructureFinder.STRUCT_FORTRESS);

    public static final SextantFinder.Structure TARGET_VANILLA_ENDCITY =
            new SextantFinder.Structure("endcity", 0xFF96066F, true, StructureFinder.STRUCT_ENDCITY);

    public static final SextantFinder.Biome TARGET_BIOME_PLAINS =
            new SextantFinder.Biome("biome_plains", 0xFF00FF47, true, BiomeDictionary.Type.PLAINS);

    public static final SextantFinder.Biome TARGET_BIOME_FOREST =
            new SextantFinder.Biome("biome_forest", 0xFF009105, true, BiomeDictionary.Type.FOREST);

    public static final SextantFinder.Biome TARGET_BIOME_DESERT =
            new SextantFinder.Biome("biome_desert", 0xFFFFE105, true, BiomeDictionary.Type.SANDY);

    public static final SextantFinder.Biome TARGET_BIOME_JUNGLE =
            new SextantFinder.Biome("biome_jungle", 0xFF2EE400, true, BiomeDictionary.Type.JUNGLE);

    public static final SextantFinder.Biome TARGET_BIOME_MOUNTAINS =
            new SextantFinder.Biome("biome_mountains", 0xFF94F9CF, true, BiomeDictionary.Type.MOUNTAIN);

    public static final SextantFinder.Biome TARGET_BIOME_COLD =
            new SextantFinder.Biome("biome_cold", 0xFF22F9E7, true, BiomeDictionary.Type.COLD);

    public static final SextantFinder.Biome TARGET_BIOME_MESA =
            new SextantFinder.Biome("biome_mesa", 0xFFC5481E, true, BiomeDictionary.Type.MESA);

    public static final SextantFinder.Biome TARGET_BIOME_OCEAN =
            new SextantFinder.Biome("biome_ocean", 0xFF200CE6, true, BiomeDictionary.Type.OCEAN);


}
