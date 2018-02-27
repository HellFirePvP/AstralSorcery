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

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SextantTargets
 * Created by HellFirePvP
 * Date: 25.02.2018 / 23:30
 */
public class SextantTargets {

    public static final SextantFinder.ASStructure TARGET_MOUNTAIN_SHRINE =
            new SextantFinder.ASStructure("mountain_shrine", false, StructureGenBuffer.StructureType.MOUNTAIN);

    public static final SextantFinder.ASStructure TARGET_DESERT_SHRINE =
            new SextantFinder.ASStructure("desert_shrine", false, StructureGenBuffer.StructureType.DESERT);

    public static final SextantFinder.ASStructure TARGET_SMALL_SHRINE =
            new SextantFinder.ASStructure("small_shrine", false, StructureGenBuffer.StructureType.SMALL);

    public static final SextantFinder.Structure TARGET_VANILLA_VILLAGE =
            new SextantFinder.Structure("village", true, StructureFinder.STRUCT_VILLAGE);

    public static final SextantFinder.Structure TARGET_VANILLA_MONUMENT =
            new SextantFinder.Structure("oceanmonument", true, StructureFinder.STRUCT_MONUMENT);

    public static final SextantFinder.Structure TARGET_VANILLA_TEMPLE =
            new SextantFinder.Structure("temple", true, StructureFinder.STRUCT_TEMPLE);

    public static final SextantFinder.Structure TARGET_VANILLA_FORTRESS =
            new SextantFinder.Structure("fortress", true, StructureFinder.STRUCT_FORTRESS);

    public static final SextantFinder.Structure TARGET_VANILLA_ENDCITY =
            new SextantFinder.Structure("endcity", true, StructureFinder.STRUCT_ENDCITY);

    public static final SextantFinder.Biome TARGET_BIOME_PLAINS =
            new SextantFinder.Biome("biome_plains", true, BiomeDictionary.Type.PLAINS);

    public static final SextantFinder.Biome TARGET_BIOME_FOREST =
            new SextantFinder.Biome("biome_forest", true, BiomeDictionary.Type.FOREST);

    public static final SextantFinder.Biome TARGET_BIOME_DESERT =
            new SextantFinder.Biome("biome_desert", true, BiomeDictionary.Type.SANDY);

    public static final SextantFinder.Biome TARGET_BIOME_JUNGLE =
            new SextantFinder.Biome("biome_jungle", true, BiomeDictionary.Type.JUNGLE);

    public static final SextantFinder.Biome TARGET_BIOME_MOUNTAINS =
            new SextantFinder.Biome("biome_mountains", true, BiomeDictionary.Type.MOUNTAIN);

    public static final SextantFinder.Biome TARGET_BIOME_COLD =
            new SextantFinder.Biome("biome_cold", true, BiomeDictionary.Type.COLD);

    public static final SextantFinder.Biome TARGET_BIOME_MESA =
            new SextantFinder.Biome("biome_mesa", true, BiomeDictionary.Type.MESA);

    public static final SextantFinder.Biome TARGET_BIOME_OCEAN =
            new SextantFinder.Biome("biome_ocean", true, BiomeDictionary.Type.OCEAN);


}
