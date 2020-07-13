/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.world.config.FeaturePlacementConfig;
import hellfirepvp.astralsorcery.common.world.config.ReplacingFeaturePlacementConfig;
import hellfirepvp.astralsorcery.common.world.config.StructurePlacementConfig;
import hellfirepvp.astralsorcery.common.world.structure.feature.FeatureAncientShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.feature.FeatureDesertShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.feature.FeatureSmallShrineStructure;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.placement.CountRangeConfig;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldGenerationAS
 * Created by HellFirePvP
 * Date: 23.07.2019 / 20:22
 */
public class WorldGenerationAS {

    public static final ResourceLocation KEY_ANCIENT_SHRINE = AstralSorcery.key("ancient_shrine");
    public static final ResourceLocation KEY_DESERT_SHRINE = AstralSorcery.key("desert_shrine");
    public static final ResourceLocation KEY_SMALL_SHRINE = AstralSorcery.key("small_shrine");

    public static IStructurePieceType ANCIENT_SHRINE_PIECE;
    public static IStructurePieceType DESERT_SHRINE_PIECE;
    public static IStructurePieceType SMALL_SHRINE_PIECE;

    public static FeatureAncientShrineStructure STRUCTURE_ANCIENT_SHRINE;
    public static FeatureDesertShrineStructure  STRUCTURE_DESERT_SHRINE;
    public static FeatureSmallShrineStructure   STRUCTURE_SMALL_SHRINE;

    public static class Placement {

        public static StructurePlacementConfig DESERT_SHRINE;
        public static StructurePlacementConfig MOUNTAIN_SHRINE;
        public static StructurePlacementConfig SMALL_SHRINE;

        public static FeaturePlacementConfig GLOW_FLOWER;
        public static ReplacingFeaturePlacementConfig ROCK_CRYSTAL;
        public static ReplacingFeaturePlacementConfig AQUAMARINE;
        public static CountRangeConfig MARBLE;

    }

}
