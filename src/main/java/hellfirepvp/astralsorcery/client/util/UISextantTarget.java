/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.common.item.tool.sextant.SextantFinder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: UISextantTarget
 * Created by HellFirePvP
 * Date: 23.04.2018 / 17:40
 */
public class UISextantTarget {

    private World world;
    private BlockPos pos;
    private SextantFinder.TargetObject sextantTarget;

    private UISextantTarget(World world, BlockPos target, SextantFinder.TargetObject sextantTarget) {
        this.world = world;
        this.pos = target;
        this.sextantTarget = sextantTarget;
    }

    public static UISextantTarget initialize(World world, BlockPos actualTarget, SextantFinder.TargetObject sextantTarget) {
        UISextantTarget target = new UISextantTarget(world, actualTarget, sextantTarget);
        return target;
    }

    public void renderStar() {
        //no-op
    }

    public World getWorld() {
        return world;
    }
}
