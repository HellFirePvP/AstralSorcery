package hellfire.astralSorcery.common.constellation;

import hellfire.astralSorcery.api.constellation.IConstellation;
import hellfire.astralSorcery.api.constellation.IConstellationTier;
import hellfire.astralSorcery.common.util.Tuple;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 06.02.2016 21:39
 */
public class ConstellationHandler {

    private static int lastTrackedDate = -1;
    private static Map<IConstellationTier, TierIteration> iteration = new HashMap<IConstellationTier, TierIteration>();

    private static long savedSeed;
    private static Random rand = null;
    private static boolean seedInit = false; //Problem is, we can't write seed = -1 for default, since -1 could be a valid seed.

    public static Collection<Tuple<IConstellationTier, IConstellation>> getActiveConstellations() {
        List<Tuple<IConstellationTier, IConstellation>> constellations = new ArrayList<Tuple<IConstellationTier, IConstellation>>();
        for (IConstellationTier tier : iteration.keySet()) {
            TierIteration ti = iteration.get(tier);
            IConstellation constellation = ti.getConstellationToShow(tier);
            if(constellation != null) {
                constellations.add(new Tuple<IConstellationTier, IConstellation>(tier, constellation));
            }
        }
        return constellations;
    }

    public static void informTick(World world) {
        if(world.provider.getDimensionId() != 0) return;

        if(!seedInit) {
            savedSeed = world.getSeed();
            seedInit = true;
        }
        if(rand == null) {
            rand = new Random(savedSeed);
        }

        int days = (int) (world.getWorldTime() / 24000);

        /*if(lastTrackedDate < 0) {
            if(days >= 0) lastTrackedDate = 0;
        }*/

        int trackingDifference = days - lastTrackedDate;

        if(trackingDifference > 0) {
            //Calculating until that day is reached.
            scheduleDayProgression(trackingDifference);
            lastTrackedDate = days;
        } else if(trackingDifference < 0) {
            //Resetting and recalculating until specified day is reached!
            rand = new Random(savedSeed);
            iteration.clear();
            scheduleDayProgression(days + 1);
            lastTrackedDate = days;
        }

    }

    private static void scheduleDayProgression(int loop) {
        for (int i = 0; i < loop; i++) {
            scheduleDayProgression();
        }
    }

    private static void scheduleDayProgression() {
        for(IConstellationTier tier : iteration.keySet()) {
            iteration.get(tier).informDayProgression();
        }

        for(IConstellationTier t : ConstellationRegistry.ascendingTiers()) {
            if(!iteration.containsKey(t)) {
                iteration.put(t, new TierIteration());
            }

            if(rand.nextFloat() < t.getShowupChance()) {
                iteration.get(t).increment();
            }
        }
    }

    public static class TierIteration {

        private int counter = 0;
        private boolean doesShow = false;

        public void increment() {
            counter++;
            doesShow = true;
        }

        public boolean shouldShow() {
            return doesShow;
        }

        public IConstellation getConstellationToShow(IConstellationTier tier) {
            if(!shouldShow()) return null;
            List<IConstellation> constellations = tier.getConstellations();
            if(constellations.size() == 0) return null;
            int toShow = counter % constellations.size();
            return constellations.get(toShow);
        }

        public void informDayProgression() {
            doesShow = false;
        }
    }

}
