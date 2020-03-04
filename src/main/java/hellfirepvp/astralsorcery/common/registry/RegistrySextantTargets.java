/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.astralsorcery.common.util.StructureFinder;
import hellfirepvp.astralsorcery.common.util.sextant.TargetObject;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.common.BiomeDictionary;

import static hellfirepvp.astralsorcery.common.lib.SextantTargetsAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistrySextantTargets
 * Created by HellFirePvP
 * Date: 02.06.2019 / 11:00
 */
public class RegistrySextantTargets {

    private RegistrySextantTargets() {}

    public static void init() {
        TARGET_MOUNTAIN_SHRINE = register(new TargetObject.ASStructure(AssetLoader.TextureLocation.GUI, "gridsextant", "mountain_shrine",
                0xFF5CE8D6, false, StructureTypesAS.STYPE_MOUNTAIN,
                280F / 312F, 64F / 280F, 16F / 312F, 16F / 280F));
        TARGET_MOUNTAIN_SHRINE = register(new TargetObject.ASStructure(AssetLoader.TextureLocation.GUI, "gridsextant", "desert_shrine",
                0xFFFFC82E, false, StructureTypesAS.STYPE_DESERT,
                296F / 312F, 48F / 280F, 16F / 312F, 16F / 280F));
        TARGET_MOUNTAIN_SHRINE = register(new TargetObject.ASStructure(AssetLoader.TextureLocation.GUI, "gridsextant", "small_shrine",
                0xFF2ED400, false, StructureTypesAS.STYPE_SMALL,
                280F / 312F, 48F / 280F, 16F / 312F, 16F / 280F));

        TARGET_VANILLA_VILLAGE = register(new TargetObject.VanillaStructure(AssetLoader.TextureLocation.GUI, "gridsextant", Feature.VILLAGE,
                0xFFC39909, true,
                296F / 312F, 32F / 280F, 16F / 312F, 16F / 280F));
        TARGET_VANILLA_MONUMENT = register(new TargetObject.VanillaStructure(AssetLoader.TextureLocation.GUI,"gridsextant", Feature.OCEAN_MONUMENT,
                0xFF8F38E6, true,
                296F / 312F, 16F / 280F, 16F / 312F, 16F / 280F));
        TARGET_VANILLA_DESERT_PYRAMID = register(new TargetObject.VanillaStructure(AssetLoader.TextureLocation.GUI, "gridsextant", Feature.DESERT_PYRAMID,
                0xFFC3C3C3, true,
                280F / 312F, 0F, 16F / 312F, 16F / 280F));
        TARGET_VANILLA_JUNGLE_TEMPLE = register(new TargetObject.VanillaStructure(AssetLoader.TextureLocation.GUI, "gridsextant", Feature.JUNGLE_TEMPLE,
                0xFFC3C3C3, true,
                280F / 312F, 0F, 16F / 312F, 16F / 280F));
        TARGET_VANILLA_FORTRESS = register(new TargetObject.VanillaStructure(AssetLoader.TextureLocation.GUI,"gridsextant", Feature.NETHER_BRIDGE,
                0xFF9C1D15, true,
                280F / 312F, 32F / 280F, 16F / 312F, 16F / 280F));
        TARGET_VANILLA_ENDCITY = register(new TargetObject.VanillaStructure(AssetLoader.TextureLocation.GUI, "gridsextant", Feature.END_CITY,
                0xFF96066F, true,
                280F / 312F, 16F / 280F, 16F / 312F, 16F / 280F));

        TARGET_BIOME_PLAINS = register(new TargetObject.Biome(AssetLoader.TextureLocation.GUI, "gridsextant", "biome_plains",
                0xFF00FF47, true, BiomeDictionary.Type.PLAINS,
                280F / 312F, 216F / 280F, 16F / 312F, 16F / 280F));
        TARGET_BIOME_DESERT = register(new TargetObject.Biome(AssetLoader.TextureLocation.GUI, "gridsextant", "biome_desert",
                0xFFFFE105, true, BiomeDictionary.Type.SANDY,
                280F / 312F, 232F / 280F, 16F / 312F, 16F / 280F));
        TARGET_BIOME_COLD = register(new TargetObject.Biome(AssetLoader.TextureLocation.GUI, "gridsextant", "biome_cold",
                0xFF22F9E7, true, BiomeDictionary.Type.COLD,
                296F / 312F, 248F / 280F, 16F / 312F, 16F / 280F));
        TARGET_BIOME_FOREST = register(new TargetObject.Biome(AssetLoader.TextureLocation.GUI, "gridsextant", "biome_forest",
                0xFF009105, true, BiomeDictionary.Type.FOREST,
                296F / 312F, 216F / 280F, 16F / 312F, 16F / 280F));
        TARGET_BIOME_JUNGLE = register(new TargetObject.Biome(AssetLoader.TextureLocation.GUI, "gridsextant", "biome_jungle",
                0xFF2EE400, true, BiomeDictionary.Type.JUNGLE,
                296F / 312F, 232F / 280F, 16F / 312F, 16F / 280F));
        TARGET_BIOME_MESA = register(new TargetObject.Biome(AssetLoader.TextureLocation.GUI, "gridsextant", "biome_mesa",
                0xFFC5481E, true, BiomeDictionary.Type.MESA,
                280F / 312F, 264F / 280F, 16F / 312F, 16F / 280F));
        TARGET_BIOME_OCEAN = register(new TargetObject.Biome(AssetLoader.TextureLocation.GUI, "gridsextant", "biome_ocean",
                0xFF200CE6, true, BiomeDictionary.Type.OCEAN,
                296F / 312F, 264F / 280F, 16F / 312F, 16F / 280F));
        TARGET_BIOME_MOUNTAINS = register(new TargetObject.Biome(AssetLoader.TextureLocation.GUI, "gridsextant", "biome_mountains",
                0xFF94F9CF, true, BiomeDictionary.Type.MOUNTAIN,
                280F / 312F, 248F / 280F, 16F / 312F, 16F / 280F));
    }

    private static <T extends TargetObject> T register(T target) {
        AstralSorcery.getProxy().getRegistryPrimer().register(target);
        return target;
    }

}
