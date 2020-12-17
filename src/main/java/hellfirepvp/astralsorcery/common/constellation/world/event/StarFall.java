/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.world.event;

import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import net.minecraft.world.World;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarFall
 * Created by HellFirePvP
 * Date: 04.12.2020 / 22:14
 */
public class StarFall extends CelestialEvent {

    @Override
    public void tick(World world, Random rand, WorldContext ctx) {

    }

    @Override
    public boolean isActiveNow() {
        return false;
    }

    @Override
    public boolean isActiveDay() {
        return false;
    }

    @Override
    public float getEffectTick(float pTicks) {
        return 0;
    }

    @Override
    public long getSeedModifier() {
        return 0L;
    }
}
