/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

/**
* This class is part of the Astral Sorcery Mod
* The complete source code for this mod can be found on github.
* Class: MoonPhase
* Created by HellFirePvP
* Date: 17.11.2016 / 02:45
*/
public enum MoonPhase {

    FULL, WANING3_4, WANING1_2, WANING1_4,
    NEW, WAXING1_4, WAXING1_2, WAXING3_4;

    public static MoonPhase fromWorld(IWorld world) {
        return values()[MathHelper.clamp(world.getMoonPhase(), 0, values().length)];
    }

}
