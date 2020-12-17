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
 * Class: CelestialEvent
 * Created by HellFirePvP
 * Date: 26.11.2020 / 19:32
 */
public abstract class CelestialEvent {

    public abstract void tick(World world, Random rand, WorldContext ctx);

    public abstract boolean isActiveNow();

    public abstract boolean isActiveDay();

    public abstract float getEffectTick(float pTicks);

    public abstract long getSeedModifier();

}
