/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.world.FeatureGenerationConfig;
import hellfirepvp.astralsorcery.common.world.StructureGenerationConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;

import java.util.Arrays;
import java.util.Collections;

import static net.minecraft.world.biome.Biome.Category.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldGenerationAS
 * Created by HellFirePvP
 * Date: 23.07.2019 / 20:22
 */
public class WorldGenerationAS {

    public static class Structures {

        public static final ResourceLocation KEY_ANCIENT_SHRINE = AstralSorcery.key("ancient_shrine");
        public static final ResourceLocation KEY_DESERT_SHRINE = AstralSorcery.key("desert_shrine");
        public static final ResourceLocation KEY_SMALL_SHRINE = AstralSorcery.key("small_shrine");

        public static IStructurePieceType ANCIENT_SHRINE_PIECE;
        public static IStructurePieceType DESERT_SHRINE_PIECE;
        public static IStructurePieceType SMALL_SHRINE_PIECE;

        public static Structure<NoFeatureConfig> STRUCTURE_ANCIENT_SHRINE;
        public static Structure<NoFeatureConfig> STRUCTURE_DESERT_SHRINE;
        public static Structure<NoFeatureConfig> STRUCTURE_SMALL_SHRINE;

    }

    public static class Features {

        public static final ResourceLocation KEY_GLOW_FLOWER = AstralSorcery.key("glow_flower");
        public static final ResourceLocation KEY_ROCK_CRYSTAL = AstralSorcery.key("rock_crystal");
        public static final ResourceLocation KEY_AQUAMARINE = AstralSorcery.key("aquamarine");
        public static final ResourceLocation KEY_MARBLE = AstralSorcery.key("marble");

        public static ConfiguredFeature<?, ?> MARBLE;

    }

    public static class Config {

        public static StructureGenerationConfig CFG_ANCIENT_SHRINE =
                new StructureGenerationConfig(Structures.KEY_ANCIENT_SHRINE, 12, 5, Arrays.asList(ICY, EXTREME_HILLS));
        public static StructureGenerationConfig CFG_DESERT_SHRINE =
                new StructureGenerationConfig(Structures.KEY_DESERT_SHRINE, 12, 5, Arrays.asList(MESA, DESERT, SAVANNA));
        public static StructureGenerationConfig CFG_SMALL_SHRINE =
                new StructureGenerationConfig(Structures.KEY_SMALL_SHRINE, 12, 8, Arrays.asList(FOREST, PLAINS));

        public static FeatureGenerationConfig CFG_MARBLE =
                new FeatureGenerationConfig(Features.KEY_MARBLE, Collections.emptyList())
                        .setGenerateEverywhere();

    }
}
