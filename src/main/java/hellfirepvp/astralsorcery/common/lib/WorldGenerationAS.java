/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.common.world.placement.config.EvenStructurePlacementConfig;
import hellfirepvp.astralsorcery.common.world.placement.config.FeaturePlacementConfig;
import hellfirepvp.astralsorcery.common.world.placement.config.ReplacingFeaturePlacementConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldGenerationAS
 * Created by HellFirePvP
 * Date: 23.07.2019 / 20:22
 */
public class WorldGenerationAS {

    public static class Placement {

        public static EvenStructurePlacementConfig DESERT_SHRINE;
        public static EvenStructurePlacementConfig MOUNTAIN_SHRINE;
        public static EvenStructurePlacementConfig SMALL_SHRINE;

        public static FeaturePlacementConfig GLOW_FLOWER;
        public static ReplacingFeaturePlacementConfig ROCK_CRYSTAL;
        public static ReplacingFeaturePlacementConfig AQUAMARINE;
        public static CountRangeConfig MARBLE;

    }

}
