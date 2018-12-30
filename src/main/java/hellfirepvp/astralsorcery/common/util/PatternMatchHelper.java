/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.StructureMatchingBuffer;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import hellfirepvp.astralsorcery.common.structure.change.ChangeSubscriber;
import hellfirepvp.astralsorcery.common.structure.match.StructureMatcherPatternArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatternMatchHelper
 * Created by HellFirePvP
 * Date: 30.12.2018 / 12:47
 */
public class PatternMatchHelper {

    public static ChangeSubscriber<StructureMatcherPatternArray> getOrCreateMatcher(World world, BlockPos pos,
                                                                                    PatternBlockArray pattern) {
        StructureMatchingBuffer buf = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.STRUCTURE_MATCH);
        ChangeSubscriber<?> existingSubscriber = buf.getSubscriber(pos);
        if (existingSubscriber != null) {
            return (ChangeSubscriber<StructureMatcherPatternArray>) existingSubscriber;
        } else {
            return buf.observeAndInitializePattern(world, pos, pattern);
        }
    }

}
