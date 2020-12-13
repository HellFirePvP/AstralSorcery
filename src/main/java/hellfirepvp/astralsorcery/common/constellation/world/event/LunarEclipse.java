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
 * Class: LunarEclipse
 * Created by HellFirePvP
 * Date: 26.11.2020 / 19:32
 */
public class LunarEclipse extends CelestialEvent {

    private boolean active = false;
    private boolean dayOfEvent = false;
    private int prevEventTick = 0;
    private int eventTick = 0;

    @Override
    public void tick(World world, Random rand, WorldContext ctx) {
        for (int i = 0; i < 12 + rand.nextInt(12); i++) {
            rand.nextLong(); //Flush
        }
        int halfTime = this.getEventDuration() / 2;

        int repeat = 68;
        long wTime = world.getDayTime();
        int suggestedDayLength = GeneralConfig.CONFIG.dayLength.get();
        int lunarTime = (int) (wTime % (repeat * suggestedDayLength));
        dayOfEvent = lunarTime >= 0 && lunarTime < suggestedDayLength;
        int midLOffset = Math.round(suggestedDayLength * 0.75F); //Rounding errors are not my fault.

        if (wTime > suggestedDayLength &&
                lunarTime > (midLOffset - halfTime) &&
                lunarTime < (midLOffset + halfTime)) {
            active = true;
            prevEventTick = eventTick;
            eventTick = lunarTime - (midLOffset - halfTime);
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
        return 0xA595C3B735D5BFB9L;
    }

    public int getEventDuration() {
        return GeneralConfig.CONFIG.dayLength.get() / 5;
    }
}
