/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.level.event;

import hellfirepvp.astralsorcery.common.constellation.level.LevelSkyContext;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CelestialEvent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class CelestialEvent {

    public abstract void tick(Level world, RandomSource rand, LevelSkyContext ctx);

    public abstract boolean isActiveNow();

    public abstract boolean isActiveDay();

    public abstract float getEffectTick(float pTicks);

    public abstract long getSeedModifier();
}
