/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.world;

import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldContext
 * Created by HellFirePvP
 * Date: 01.07.2019 / 19:50
 */
public class WorldContext {

    private final long seed;

    private final CelestialEventHandler celestialHandler;
    private final ActiveCelestialsHandler activeCelestialsHandler;

    private final ConstellationHandler constellationHandler;
    private final DistributionHandler distributionHandler;

    public WorldContext(long randSeed) {
        this.seed = randSeed;
        this.celestialHandler = new CelestialEventHandler(this);
        this.activeCelestialsHandler = new ActiveCelestialsHandler();

        this.constellationHandler = new ConstellationHandler(this);
        this.distributionHandler = new DistributionHandler(this);
    }

    public long getSeed() {
        return seed;
    }

    @Nonnull
    public Random getRandom() {
        return this.getRandom(0L);
    }

    public Random getRandom(long seedModifier) {
        return new Random(this.seed + seedModifier);
    }

    @Nonnull
    public Random getDayRandom() {
        int track = this.getConstellationHandler().getLastTrackedDay();
        return new Random(this.getSeed() * 31 + track * 31);
    }

    @Nonnull
    public CelestialEventHandler getCelestialEventHandler() {
        return celestialHandler;
    }

    @Nonnull
    public ConstellationHandler getConstellationHandler() {
        return constellationHandler;
    }

    @Nonnull
    public DistributionHandler getDistributionHandler() {
        return distributionHandler;
    }

    @Nonnull
    public ActiveCelestialsHandler getActiveCelestialsHandler() {
        return activeCelestialsHandler;
    }

    public void tick(World world) {
        this.celestialHandler.tick(world);
        this.constellationHandler.tick(world);
        this.distributionHandler.tick(world);
    }

}
