/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IConstellationSpecialShowup
 * Created by HellFirePvP
 * Date: 10.01.2017 / 17:18
 */
public interface IConstellationSpecialShowup extends IConstellation {

    //Will be called on either side.
    public boolean doesShowUp(WorldSkyHandler handle, World world, long day);

    //Fed directly into the worldSkyHandler's distribution, only use values 0-1.
    public float getDistribution(WorldSkyHandler handle, World world, long day, boolean showingUp);

    default public boolean isDayOfSolarEclipse(long day) {
        return day > 0 && day % 36 == 0;
    }

    default public long dayToWorldTime(long day) {
        return day * 24000L;
    }

}
