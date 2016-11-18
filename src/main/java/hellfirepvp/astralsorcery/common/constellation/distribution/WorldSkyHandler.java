package hellfirepvp.astralsorcery.common.constellation.distribution;

import hellfirepvp.astralsorcery.common.constellation.CelestialEvent;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldSkyHandler
 * Created by HellFirePvP
 * Date: 16.11.2016 / 20:50
 */
public class WorldSkyHandler {

    public int lastRecordedDay = -1;

    private Random seededRand = null;
    private long savedSeed;
    private boolean isSeedInitialized = false;

    public boolean solarEclipse = false;
    public boolean dayOfSolarEclipse = false;
    public int prevSolarEclipseTick = 0;
    public int solarEclipseTick = 0;

    public boolean lunarEclipse = false;
    public boolean dayOfLunarEclipse = false;
    public int prevLunarEclipseTick = 0;
    public int lunarEclipseTick = 0;

    //Fired on client and serverside - client only if it's the world the client is in obviously.
    public void tick(World w) {
        if(!isSeedInitialized) {
            savedSeed = w.getSeed();
            isSeedInitialized = true;
        }
        if(seededRand == null) {
            seededRand = new Random(savedSeed);
        }

        evaluateCelestialEventTimes(w);

        int currentDay = (int) (w.getWorldTime() / 24000);

        int trackingDifference = currentDay - lastRecordedDay;
        lastRecordedDay = currentDay;
        if (trackingDifference > 0) {
            //Calculating until that day is reached.
            scheduleDayProgression(w, trackingDifference);
        } else if (trackingDifference < 0) {
            //Resetting and recalculating until specified day is reached!
            //Refreshing random
            seededRand = new Random(savedSeed);

            //Iterate back to current day.
            scheduleDayProgression(w, currentDay + 1);
        }
    }

    private void scheduleDayProgression(World w, int days) {
        for (int i = 0; i < days; i++) {
            toConstellationIteration();
        }
    }

    private void toConstellationIteration() {
        //TODO do iterations, compute the distributions and update data holder to sync to clients.
    }

    public MoonPhase getCurrentMoonPhase() {
        return MoonPhase.values()[lastRecordedDay % 8];
    }

    /**
     * Only means at this day. Not currently active.
     */
    public List<CelestialEvent> getCurrentDayCelestialEvents() {
        List<CelestialEvent> list = new LinkedList<>();
        if(dayOfSolarEclipse) list.add(CelestialEvent.SOLAR_ECLIPSE);
        if(dayOfLunarEclipse) list.add(CelestialEvent.LUNAR_ECLIPSE);
        return list;
    }

    /**
     * Checks if one of the events are currently ACTIVE
     * Solar and lunar eclipse are mutually exclusive in your case.
     */
    public CelestialEvent getCurrentlyActiveEvent() {
        if(solarEclipse) {
            return CelestialEvent.SOLAR_ECLIPSE;
        }
        if(lunarEclipse) {
            return CelestialEvent.LUNAR_ECLIPSE;
        }
        return null;
    }

    public float getCurrentDaytimeDistribution(World world) {
        float dayPart = world.getWorldTime() % 24000;
        if(dayPart < 11000) return 0F;
        if(dayPart < 15000) return (dayPart - 11000F) / 4000F;
        if(dayPart > 20000) return 1F - (dayPart - 20000F) / 4000F;
        return 1F;
    }

    private void evaluateCelestialEventTimes(World world) {
        int solarTime = (int) ((world.getWorldTime() % 888000) - 864000);
        dayOfSolarEclipse = solarTime > 0;
        if (solarTime > 3600 && solarTime < 8400) {
            solarEclipse = true;
            prevSolarEclipseTick = solarEclipseTick;
            solarEclipseTick = solarTime - 3600;
        } else {
            solarEclipse = false;
            solarEclipseTick = 0;
            prevSolarEclipseTick = 0;
        }

        int lunarTime = (int) ((world.getWorldTime() % 1656000) - 1632000);
        dayOfLunarEclipse = lunarTime > 0;
        if (lunarTime > 15600 && lunarTime < 20400) {
            lunarEclipse = true;
            prevLunarEclipseTick = lunarEclipseTick;
            lunarEclipseTick = lunarTime - 15600;
        } else {
            lunarEclipse = false;
            lunarEclipseTick = 0;
            prevLunarEclipseTick = 0;
        }
    }

    public Float getCurrentDistribution(IMajorConstellation c, Function<Float, Float> func) {
        return 1F; //TODO fix and implement
    }

}
