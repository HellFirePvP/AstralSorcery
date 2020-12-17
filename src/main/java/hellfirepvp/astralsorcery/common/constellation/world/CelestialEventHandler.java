/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.world;

import hellfirepvp.astralsorcery.common.constellation.world.event.CelestialEvent;
import hellfirepvp.astralsorcery.common.constellation.world.event.LunarEclipse;
import hellfirepvp.astralsorcery.common.constellation.world.event.SolarEclipse;
import hellfirepvp.astralsorcery.common.constellation.world.event.StarFall;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CelestialEventHandler
 * Created by HellFirePvP
 * Date: 01.07.2019 / 19:53
 */
public class CelestialEventHandler {

    private final WorldContext ctx;
    private final Set<CelestialEvent> events = new HashSet<>();

    private final SolarEclipse solarEclipseEvent;
    private final LunarEclipse lunarEclipseEvent;
    private final StarFall starFallNight;

    CelestialEventHandler(WorldContext context) {
        this.ctx = context;

        this.solarEclipseEvent = this.addTrackedEvent(new SolarEclipse());
        this.lunarEclipseEvent = this.addTrackedEvent(new LunarEclipse());
        this.starFallNight     = this.addTrackedEvent(new StarFall());
    }

    public <T extends CelestialEvent> T addTrackedEvent(T event) {
        this.events.add(event);
        return event;
    }

    public SolarEclipse getSolarEclipse() {
        return solarEclipseEvent;
    }

    public LunarEclipse getLunarEclipse() {
        return lunarEclipseEvent;
    }

    public StarFall getStarFallEvent() {
        return starFallNight;
    }

    public float getSolarEclipsePercent() {
        SolarEclipse solarEclipse = this.getSolarEclipse();
        if (!solarEclipse.isActiveNow()) {
            return 0F;
        }

        float halfDuration = solarEclipse.getEventDuration() / 2F;
        float tick = solarEclipse.getEffectTick(0F) - halfDuration;
        tick /= halfDuration;
        return Math.abs(tick);
    }

    void tick(World world) {
        for (CelestialEvent event : this.events) {
            event.tick(world, this.ctx.getRandom(event.getSeedModifier()), ctx);
        }
    }

}
