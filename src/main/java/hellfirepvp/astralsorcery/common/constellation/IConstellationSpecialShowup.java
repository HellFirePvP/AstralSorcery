/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.common.data.config.entry.GeneralConfig;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IConstellationSpecialShowup
 * Created by HellFirePvP
 * Date: 30.05.2019 / 00:02
 */
public interface IConstellationSpecialShowup extends IConstellation {

    //Will be called on either side.
    public boolean doesShowUp(World world, long day);

    //Fed directly into the worldSkyHandler's distribution, only use values 0-1.
    public float getDistribution(World world, long day, boolean showingUp);

    default public long dayToWorldTime(long day) {
        return day * GeneralConfig.CONFIG.dayLength.get();
    }

}
