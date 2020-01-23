/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.world;

import hellfirepvp.astralsorcery.common.data.config.entry.GeneralConfig;
import net.minecraft.world.World;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CelestialEventHandler
 * Created by HellFirePvP
 * Date: 01.07.2019 / 19:53
 */
public class CelestialEventHandler {

    private final WorldContext ctx;

    private boolean solarEclipse = false;
    private boolean dayOfSolarEclipse = false;
    private int prevSolarEclipseTick = 0;
    private int solarEclipseTick = 0;

    private boolean lunarEclipse = false;
    private boolean dayOfLunarEclipse = false;
    private int prevLunarEclipseTick = 0;
    private int lunarEclipseTick = 0;

    CelestialEventHandler(WorldContext context) {
        this.ctx = context;
    }

    public boolean isSolarEclipseActive() {
        return solarEclipse;
    }

    public boolean isLunarEclipseActive() {
        return lunarEclipse;
    }

    public boolean isDayOfSolarEclipse() {
        return dayOfSolarEclipse;
    }

    public boolean isDayOfLunarEclipse() {
        return dayOfLunarEclipse;
    }

    public int getSolarEclipseTick() {
        return solarEclipseTick;
    }

    public int getLunarEclipseTick() {
        return lunarEclipseTick;
    }

    public float getSolarEclipsePercent() {
        if (!this.isSolarEclipseActive()) {
            return 0F;
        }

        float tick = ctx.getCelestialHandler().getSolarEclipseTick() - DayTimeHelper.getSolarEclipseHalfDuration();
        tick /= DayTimeHelper.getSolarEclipseHalfDuration();
        return Math.abs(tick);
    }

    void tick(World world) {
        Random r = this.ctx.getRandom();

        for (int i = 0; i < 10 + r.nextInt(10); i++) {
            r.nextLong(); //Flush
        }
        int rand = r.nextInt(36);
        if (rand >= 18) {
            rand -= 36;
        }

        int offset = 36 - rand;
        int repeat = 36;
        long wTime = world.getDayTime();

        int suggestedDayLength = GeneralConfig.CONFIG.dayLength.get();

        int solarTime = (int) ((wTime - offset * suggestedDayLength) % (repeat * suggestedDayLength));
        dayOfSolarEclipse = solarTime >= 0 && solarTime < suggestedDayLength;
        int midSOffset = suggestedDayLength / 4; //Rounding errors are not my fault.

        if (wTime > suggestedDayLength &&
                solarTime > (midSOffset - DayTimeHelper.getSolarEclipseHalfDuration()) &&
                solarTime < (midSOffset + DayTimeHelper.getSolarEclipseHalfDuration())) {
            solarEclipse = true;
            prevSolarEclipseTick = solarEclipseTick;
            solarEclipseTick = solarTime - (midSOffset - DayTimeHelper.getSolarEclipseHalfDuration());
        } else {
            solarEclipse = false;
            solarEclipseTick = 0;
            prevSolarEclipseTick = 0;
        }

        repeat = 68;
        int lunarTime = (int) (wTime % (repeat * suggestedDayLength));
        dayOfLunarEclipse = lunarTime >= 0 && lunarTime < suggestedDayLength;
        int midLOffset = Math.round(suggestedDayLength * 0.75F); //Rounding errors are not my fault.

        if (wTime > suggestedDayLength &&
                lunarTime > (midLOffset - DayTimeHelper.getLunarEclipseHalfDuration()) &&
                lunarTime < (midLOffset + DayTimeHelper.getLunarEclipseHalfDuration())) {
            lunarEclipse = true;
            prevLunarEclipseTick = lunarEclipseTick;
            lunarEclipseTick = lunarTime - (midLOffset - DayTimeHelper.getLunarEclipseHalfDuration());
        } else {
            lunarEclipse = false;
            lunarEclipseTick = 0;
            prevLunarEclipseTick = 0;
        }
    }

}
