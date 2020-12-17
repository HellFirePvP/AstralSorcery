/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.world.event;

import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.data.config.entry.GeneralConfig;
import net.minecraft.world.World;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SolarEclipse
 * Created by HellFirePvP
 * Date: 26.11.2020 / 19:32
 */
public class SolarEclipse extends CelestialEvent {

    private boolean active = false;
    private boolean dayOfEvent = false;
    private int prevEventTick = 0;
    private int eventTick = 0;

    @Override
    public void tick(World world, Random rand, WorldContext ctx) {
        for (int i = 0; i < 12 + rand.nextInt(12); i++) {
            rand.nextLong(); //Flush
        }
        int rOffset = rand.nextInt(36);
        if (rOffset >= 18) {
            rOffset -= 36;
        }

        int halfTime = this.getEventDuration() / 2;

        int offset = 36 - rOffset;
        int repeat = 36;
        long wTime = world.getDayTime();

        int suggestedDayLength = GeneralConfig.CONFIG.dayLength.get();

        int solarTime = (int) ((wTime - offset * suggestedDayLength) % (repeat * suggestedDayLength));
        dayOfEvent = solarTime >= 0 && solarTime < suggestedDayLength;
        int midSOffset = suggestedDayLength / 4; //Rounding errors are not my fault.

        if (wTime > suggestedDayLength &&
                solarTime > (midSOffset - halfTime) &&
                solarTime < (midSOffset + halfTime)) {
            active = true;
            prevEventTick = eventTick;
            eventTick = solarTime - (midSOffset - halfTime);
        } else {
            active = false;
            prevEventTick = 0;
            eventTick = 0;
        }
    }

    @Override
    public boolean isActiveNow() {
        return this.active;
    }

    @Override
    public boolean isActiveDay() {
        return this.dayOfEvent;
    }

    @Override
    public float getEffectTick(float pTicks) {
        return this.prevEventTick + ((this.eventTick - this.prevEventTick) * pTicks);
    }

    @Override
    public long getSeedModifier() {
        return 0x8D8692645A136C53L;
    }

    public int getEventDuration() {
        return GeneralConfig.CONFIG.dayLength.get() / 5;
    }
}
