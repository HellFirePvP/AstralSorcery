/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.distribution;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.client.util.mappings.ClientConstellationPositionMapping;
import hellfirepvp.astralsorcery.common.constellation.*;
import hellfirepvp.astralsorcery.common.data.DataActiveCelestials;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

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

    public WorldSkyHandler(long seed) {
        this.savedSeed = seed;
    }

    private void refreshRandom() {
        this.seededRand = new Random(savedSeed);
    }

    //Fired on client and serverside - client only if it's the world the client is in obviously.
    public void tick(World w) {
        if(!w.getGameRules().getBoolean("doDaylightCycle")) {
            return;
        }

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
        LinkedList<IWeakConstellation> weakAndMajor = new LinkedList<>(ConstellationRegistry.getWeakConstellations());
        Collections.shuffle(weakAndMajor, seededRand);
        weakAndMajor.forEach(constellations::addFirst);

        for (IConstellation c : constellations) {
            if(c instanceof IConstellationSpecialShowup) continue;

            if(c instanceof IMinorConstellation) {
                for (MoonPhase ph : ((IMinorConstellation) c).getShowupMoonPhases()) {
                    initialValueMappings.get(ph.ordinal()).add(c);
                }
                for (int i = 0; i < 8; i++) {
                    dayDistributionMap.get(i).put(c, 0F);
                }
            } else {
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

                if(c instanceof IWeakConstellation) {
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
    }

    //1F to 0F
    private float spSine(int dayStart, int dayIn) {
        int v = dayStart > dayIn ? (dayIn + 8) - dayStart : dayIn;
        float part = ((float) v) / 4F;
        return MathHelper.sin((float) (part * Math.PI)) * 0.25F + 0.75F;
    }

    private void scheduleDayProgression(World w, int days) {
        for (int i = 0; i < days; i++) {
            doConstellationIteration(w);
        }

        if(!w.isRemote) {
            DataActiveCelestials celestials = SyncDataHolder.getDataServer(SyncDataHolder.DATA_CONSTELLATIONS);
            celestials.setNewConstellations(w.provider.getDimension(), activeConstellations);
        } else {
            ((ClientConstellationPositionMapping) clientConstellationPositionMapping)
                    .updatePositions(activeConstellations);
        }
    }

    private void doConstellationIteration(World w) {
        activeConstellations.clear();
        activeDistributions.clear();

        int activeDay = lastRecordedDay % 8;
        LinkedList<IConstellation> linkedConstellations = initialValueMappings.get(activeDay);
        for (int i = 0; i < Math.min(10, linkedConstellations.size()); i++) {
            activeConstellations.addLast(linkedConstellations.get(i));
        }

        activeDistributions = Maps.newHashMap(dayDistributionMap.get(activeDay));

        for (IConstellationSpecialShowup special : ConstellationRegistry.getSpecialShowupConstellations()) {
            if(special.doesShowUp(this, w, lastRecordedDay)) {
                activeConstellations.addLast(special);
                activeDistributions.put(special, MathHelper.clamp(special.getDistribution(this, w, lastRecordedDay, true), 0F, 1F));
            } else {
                activeDistributions.put(special, MathHelper.clamp(special.getDistribution(this, w, lastRecordedDay, false), 0F, 1F));
            }
        }
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

    public LinkedList<IConstellation> getSortedActiveConstellations() {
        LinkedList<IConstellation> out = new LinkedList<>();
        LinkedList<Map.Entry<IConstellation, Float>> entries = new LinkedList<>();
        activeDistributions.entrySet().forEach(entries::add);
        entries.sort((e1, e2) -> MathHelper.floor(e2.getValue() * 1000) - MathHelper.floor(e1.getValue() * 1000));
        entries.forEach((e) -> out.addLast(e.getKey()));
        return out;
    }

    @Nullable
    public IConstellation getHighestDistributionConstellation(Random random) {
        return getHighestDistributionConstellation(random, (c) -> true);
    }

    @Nullable
    public IConstellation getHighestDistributionConstellation(Random random, Predicate<IConstellation> acceptorFunc) {
        float highest = -1F;
        List<IConstellation> highestVal = new LinkedList<>();
        for (Map.Entry<IConstellation, Float> entry : activeDistributions.entrySet()) {
            if(entry.getValue() > highest) {
                if(acceptorFunc.test(entry.getKey())) {
                    highest = entry.getValue();
                    highestVal.clear();
                    highestVal.add(entry.getKey());
                }
            } else if(entry.getValue() == highest) {
                if(acceptorFunc.test(entry.getKey())) {
                    highestVal.add(entry.getKey());
                }
            }
        }
        if(highestVal.isEmpty()) return null;
        return highestVal.get(random.nextInt(highestVal.size()));
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

    public List<IConstellation> getConstellationsForMoonPhase(MoonPhase phase) {
        return initialValueMappings.get(phase.ordinal());
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

    private void evaluateCelestialEventTimes(World world) {
        int solarTime = (int) (world.getWorldTime() % 864000);
        dayOfSolarEclipse = solarTime < 24000;
        if (world.getWorldTime() > 24000 && solarTime > 3600 && solarTime < 8400) {
            solarEclipse = true;
            prevSolarEclipseTick = solarEclipseTick;
            solarEclipseTick = solarTime - 3600;
        } else {
            solarEclipse = false;
            solarEclipseTick = 0;
            prevSolarEclipseTick = 0;
        }

        int lunarTime = (int) (world.getWorldTime() % 1632000);
        dayOfLunarEclipse = lunarTime < 24000;
        if (world.getWorldTime() > 24000 && lunarTime > 15600 && lunarTime < 20400) {
            lunarEclipse = true;
            prevLunarEclipseTick = lunarEclipseTick;
            lunarEclipseTick = lunarTime - 15600;
        } else {
            lunarEclipse = false;
            lunarEclipseTick = 0;
            prevLunarEclipseTick = 0;
        }
    }

    public Float getCurrentDistribution(IWeakConstellation c, Function<Float, Float> func) {
        if(!activeDistributions.containsKey(c)) return func.apply(0F);
        return func.apply(activeDistributions.get(c));
    }

}
