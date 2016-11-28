package hellfirepvp.astralsorcery.common.constellation.distribution;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.client.util.mappings.ClientConstellationPositionMapping;
import hellfirepvp.astralsorcery.common.constellation.CelestialEvent;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;
import hellfirepvp.astralsorcery.common.data.DataActiveCelestials;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
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

    private LinkedList<IConstellation> activeConstellations = new LinkedList<>();

    private Map<Integer, LinkedList<IConstellation>> initialValueMappings = new HashMap<>();
    private Map<Integer, Map<IConstellation, Float>> dayDistributionMap = new HashMap<>();

    private Map<IConstellation, Float> activeDistributions = new HashMap<>();

    private Object clientConstellationPositionMapping;

    private Random seededRand = null;
    private final long savedSeed;

    public boolean solarEclipse = false;
    public boolean dayOfSolarEclipse = false;
    public int prevSolarEclipseTick = 0;
    public int solarEclipseTick = 0;

    public boolean lunarEclipse = false;
    public boolean dayOfLunarEclipse = false;
    public int prevLunarEclipseTick = 0;
    public int lunarEclipseTick = 0;

    public WorldSkyHandler(World world) {
        this.savedSeed = world.getSeed();
    }

    private void refreshRandom() {
        this.seededRand = new Random(savedSeed);
    }

    //Fired on client and serverside - client only if it's the world the client is in obviously.
    public void tick(World w) {
        if(initialValueMappings.isEmpty()) {
            setupInitialFunctions();
        }

        if(w.isRemote && clientConstellationPositionMapping == null) {
            clientConstellationPositionMapping = new ClientConstellationPositionMapping();
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

            //Iterate back to current day.
            //+1 because we start from 'day -1'
            scheduleDayProgression(w, currentDay + 1);
        }
    }

    private void setupInitialFunctions() {
        initialValueMappings.clear();
        dayDistributionMap.clear();
        for (int i = 0; i < 8; i++) {
            initialValueMappings.put(i, new LinkedList<>());
            dayDistributionMap.put(i, new HashMap<>());
        }

        refreshRandom();

        boolean[] occupied = new boolean[8];
        Arrays.fill(occupied, false);
        LinkedList<IConstellation> constellations = new LinkedList<>(ConstellationRegistry.getMinorConstellations());
        Collections.shuffle(constellations, seededRand);
        LinkedList<IMajorConstellation> majors = new LinkedList<>(ConstellationRegistry.getMajorConstellations());
        Collections.shuffle(majors, seededRand);
        majors.forEach(constellations::addFirst);

        for (IConstellation c : constellations) {
            int start;
            boolean foundFree = false;
            int tries = 5;
            do {
                tries--;
                start = seededRand.nextInt(8);

                int needed = Math.min(3, getFreeSlots(occupied));
                int count = collect(start, occupied);
                if(count >= needed) {
                    foundFree = true;
                }
            } while (!foundFree && tries > 0);
            occupySlots(start, occupied);
            if(getFreeSlots(occupied) <= 0) {
                Arrays.fill(occupied, false);
            }
            for (int i = 0; i < 5; i++) {
                int index = (start + i) % 8;
                initialValueMappings.get(index).addLast(c);
            }
            if(c instanceof IMajorConstellation) {
                for (int i = 0; i < 8; i++) {
                    int index = (start + i) % 8;
                    float distr = spSine(start, index);
                    dayDistributionMap.get(index).put(c, distr);
                }
            } else {
                for (int i = 0; i < 8; i++) {
                    dayDistributionMap.get(i).put(c, 0F);
                }
            }
        }
    }

    //1F to 0F
    private float spSine(int dayStart, int dayIn) {
        int v = dayStart > dayIn ? (dayIn + 8) - dayStart : dayIn;
        float part = ((float) v) / 4F;
        return MathHelper.sin((float) (part * Math.PI)) * 0.25F + 0.75F;
    }

    private void scheduleDayProgression(World w, int days) {
        for (int i = 0; i < days; i++) {
            doConstellationIteration();
        }

        if(!w.isRemote) {
            DataActiveCelestials celestials = SyncDataHolder.getDataServer(SyncDataHolder.DATA_CONSTELLATIONS);
            celestials.setNewConstellations(w.provider.getDimension(), activeConstellations);
        } else {
            ((ClientConstellationPositionMapping) clientConstellationPositionMapping)
                    .updatePositions(activeConstellations);
        }
    }

    private void doConstellationIteration() {
        activeConstellations.clear();
        activeDistributions.clear();

        int activeDay = lastRecordedDay % 8;
        LinkedList<IConstellation> linkedConstellations = initialValueMappings.get(activeDay);
        for (int i = 0; i < Math.min(10, linkedConstellations.size()); i++) {
            activeConstellations.addLast(linkedConstellations.get(i));
        }
        activeDistributions = Maps.newHashMap(dayDistributionMap.get(activeDay));
    }

    private void occupySlots(int start, boolean[] occupied) {
        for (int i = start; i < start + 8; i++) {
            int index = start % 8;
            if(!occupied[index]) occupied[index] = true;
        }
    }

    private int collect(int start, boolean[] occupied) {
        int found = 0;
        for (int i = start; i < start + 8; i++) {
            int index = start % 8;
            if(!occupied[index]) found++;
        }
        return found;
    }

    private int getFreeSlots(boolean[] array) {
        int it = 0;
        for (boolean b : array) {
            if(!b) it++;
        }
        return it;
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
        if(!activeDistributions.containsKey(c)) return func.apply(0F);
        return func.apply(activeDistributions.get(c));
    }

}
