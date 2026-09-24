/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.level;

import hellfirepvp.astralsorcery.common.constellation.level.event.CelestialEvent;
import hellfirepvp.astralsorcery.common.constellation.level.event.LunarEclipseEvent;
import hellfirepvp.astralsorcery.common.constellation.level.event.SolarEclipseEvent;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CelestialEventHandler
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CelestialEventHandler {

    private final LevelSkyContext ctx;
    private final Set<CelestialEvent> events = new HashSet<>();

    private final SolarEclipseEvent solarEclipseEvent;
    private final LunarEclipseEvent lunarEclipseEvent;

    CelestialEventHandler(LevelSkyContext context) {
        this.ctx = context;

        this.solarEclipseEvent = this.addTrackedEvent(new SolarEclipseEvent());
        this.lunarEclipseEvent = this.addTrackedEvent(new LunarEclipseEvent());
    }

    public <T extends CelestialEvent> T addTrackedEvent(T event) {
        this.events.add(event);
        return event;
    }

    void tick(Level level) {
        for (CelestialEvent event : this.events) {
            event.tick(level, this.ctx.getRandom(event.getSeedModifier()), ctx);
        }
    }

    public SolarEclipseEvent getSolarEclipse() {
        return solarEclipseEvent;
    }

    public LunarEclipseEvent getLunarEclipse() {
        return lunarEclipseEvent;
    }

    public float getSolarEclipsePercent() {
        SolarEclipseEvent solarEclipse = this.getSolarEclipse();
        if (!solarEclipse.isActiveNow()) {
            return 0F;
        }

        float halfDuration = solarEclipse.getEventDuration() / 2F;
        float tick = solarEclipse.getEffectTick(0F) - halfDuration;
        tick /= halfDuration;
        return Math.abs(tick);
    }
}
