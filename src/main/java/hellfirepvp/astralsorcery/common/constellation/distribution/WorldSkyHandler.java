package hellfirepvp.astralsorcery.common.constellation.distribution;

import hellfirepvp.astralsorcery.client.util.mappings.ClientConstellationPositionMapping;
import hellfirepvp.astralsorcery.common.constellation.CelestialEvent;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;
import hellfirepvp.astralsorcery.common.data.DataActiveCelestials;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    //Only contains distributions > 0 if the constellation is a major.
    private Map<IConstellation, Float> activeDistributionMap = new HashMap<>();
    private LinkedList<IConstellation> activeConstellations = new LinkedList<>();

    private Object clientConstellationPositionMapping;

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
            //+1 because we start from 'day -1'
            scheduleDayProgression(w, currentDay + 1);
        }
    }

    private void scheduleDayProgression(World w, int days) {
        for (int i = 0; i < days; i++) {
            doConstellationIteration();
        }

        if(!w.isRemote) {
            DataActiveCelestials celestials = SyncDataHolder.getDataServer(SyncDataHolder.DATA_CONSTELLATIONS);
            celestials.setNewConstellations(w.provider.getDimension(), activeConstellations);
        } else {
            if(clientConstellationPositionMapping == null) {
                clientConstellationPositionMapping = new ClientConstellationPositionMapping();
            }
            ((ClientConstellationPositionMapping) clientConstellationPositionMapping)
                    .updatePositions(activeConstellations);
        }
    }

    private void doConstellationIteration() {
        activeDistributionMap.clear();
        activeConstellations.clear();

        //TODO do iterations, compute the distributions.
    }

    public Map<IConstellation, Float> getCurrentDistributions() {
        return Collections.unmodifiableMap(activeDistributionMap);
    }

    public boolean isActive(IConstellation c) {
        return getActiveConstellations().contains(c);
    }

    public List<IConstellation> getActiveConstellations() {
        return Collections.unmodifiableList(activeConstellations);
    }

    @SideOnly(Side.CLIENT)
    @Nullable
    public ClientConstellationPositionMapping getConstellationPositionMapping() {
        if(clientConstellationPositionMapping == null) return null;
        return (ClientConstellationPositionMapping) clientConstellationPositionMapping;
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
        if(!activeDistributionMap.containsKey(c)) return 0F;
        return func.apply(activeDistributionMap.get(c));
    }

}
