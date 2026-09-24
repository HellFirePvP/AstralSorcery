/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.level;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LevelSkyContext
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LevelSkyContext {

    private final long effectSeed;

    private final CelestialEventHandler celestialEventHandler;
    private final ConstellationHandler constellationHandler;
    private final ShootingStarHandler shootingStarHandler;

    LevelSkyContext(long effectSeed) {
        this.effectSeed = effectSeed;

        this.celestialEventHandler = new CelestialEventHandler(this);
        this.constellationHandler = new ConstellationHandler(this);
        this.shootingStarHandler = new ShootingStarHandler(this);
    }

    public long getEffectSeed() {
        return this.effectSeed;
    }

    void tick(Level level) {
        this.celestialEventHandler.tick(level);
        this.constellationHandler.tick(level);
        this.shootingStarHandler.tick(level);
    }

    @Nonnull
    public RandomSource getRandom() {
        return this.getRandom(0L);
    }

    public RandomSource getRandom(long seedModifier) {
        return RandomSource.create(this.getEffectSeed() + seedModifier);
    }

    @Nonnull
    public RandomSource getDayRandom() {
        int track = this.getConstellationHandler().getLastTrackedDay();
        return RandomSource.create(this.getEffectSeed() * 31 + track * 31L);
    }

    public CelestialEventHandler getCelestialEventHandler() {
        return this.celestialEventHandler;
    }

    public ConstellationHandler getConstellationHandler() {
        return this.constellationHandler;
    }
}
