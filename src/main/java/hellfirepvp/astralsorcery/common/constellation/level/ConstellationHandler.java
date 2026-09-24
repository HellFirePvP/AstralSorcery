/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.level;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.config.server.GeneralConfig;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.property.ShowUpConditionProperty;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ConstellationHandler
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ConstellationHandler {

    private static final float defaultDistanceDropOff = 0.5F;

    private final LevelSkyContext ctx;
    private final Map<MoonPhase, List<BaseConstellation>> indexedActiveMap = new HashMap<>();
    private final Map<MoonPhase, Map<BaseConstellation, Float>> indexedDistributionMap = new HashMap<>();
    private final Map<BaseConstellation, Float> focalPointAngleMap = new HashMap<>();

    private int lastRecordedDay = -1;
    private final List<BaseConstellation> activeConstellations = new ArrayList<>();

    ConstellationHandler(LevelSkyContext context) {
        this.ctx = context;
    }

    public List<MoonPhase> getActiveIndexedPhases(BaseConstellation cst) {
        List<MoonPhase> phases = new LinkedList<>();
        for (MoonPhase phase : MoonPhase.values()) {
            if (this.isActiveIndexedPhase(cst, phase)) {
                phases.add(phase);
            }
        }
        return phases;
    }

    public boolean isActiveIndexedPhase(BaseConstellation cst, MoonPhase phase) {
        return this.indexedActiveMap.get(phase).contains(cst);
    }

    public int getLastTrackedDay() {
        return this.lastRecordedDay;
    }

    public float getAngle(BaseConstellation cst) {
        return this.focalPointAngleMap.getOrDefault(cst, 45F);
    }

    public float getDistributionMultiplier(Level level, BaseConstellation cst) {
        MoonPhase phase = MoonPhase.fromWorld(level);
        if (this.indexedDistributionMap.getOrDefault(phase, Collections.emptyMap()).containsKey(cst)) {
            return this.indexedDistributionMap.get(phase).get(cst);
        } else {
            if (cst.hasProperty(ShowUpConditionProperty.KEY)) {
                ShowUpConditionProperty prop = cst.getProperty(ShowUpConditionProperty.KEY);
                if (prop != null) {
                    return prop.getDistribution(level, lastRecordedDay, ctx.getEffectSeed(), this.isCurrentlyActive(cst));
                }
            }
        }
        return 0F;
    }

    public List<BaseConstellation> getActiveConstellations() {
        return Collections.unmodifiableList(this.activeConstellations);
    }

    public boolean isCurrentlyActive(BaseConstellation cst) {
        return this.getActiveConstellations().contains(cst);
    }

    public void tick(Level level) {
        if (this.indexedActiveMap.isEmpty()) {
            initialize(level);
        }
        if (this.focalPointAngleMap.isEmpty()) {
            initializeFocalPointAngles(level);
        }

        int currentDay = (int) (level.dayTime() / GeneralConfig.CONFIG.dayLength.get());

        int dayDifference = currentDay - lastRecordedDay;
        if (dayDifference != 0) {
            lastRecordedDay = currentDay;
            this.updateActiveConstellations(level, currentDay);
        }
    }

    private void updateActiveConstellations(Level world, int currentDay) {
        this.activeConstellations.clear();

        MoonPhase ph = MoonPhase.fromWorld(world);
        this.activeConstellations.addAll(this.indexedActiveMap.computeIfAbsent(ph, p -> new ArrayList<>()));
        RegistriesAS.REGISTRY_CONSTELLATIONS.forEach(cst -> {
            cst.getPropertyOpt(ShowUpConditionProperty.KEY).ifPresent(prop -> {
                if (!prop.canIndexPhaseDistribution() && prop.doesShowUp(world, currentDay, ctx.getEffectSeed())) {
                    this.activeConstellations.add(cst);
                }
            });
        });
    }

    private void initializeFocalPointAngles(Level level) {
        this.focalPointAngleMap.clear();

        RandomSource rand = ctx.getRandom();
        List<BaseConstellation> constellations = Lists.newArrayList(RegistriesAS.REGISTRY_CONSTELLATIONS.stream()
                .filter(cst -> cst.is(TagsAS.Constellations.MAY_BE_FOCAL_POINT))
                .toList());
        MiscUtil.shuffle(constellations, rand);
        constellations.forEach(cst -> {

            Collection<Float> usedAngles = this.focalPointAngleMap.values();

            int attempt = 200;
            float distance, newDegree;
            do {
                attempt--;

                newDegree = rand.nextInt(91); //0-90
                distance = distance(usedAngles, newDegree);
            } while (attempt > 0 && distance < 8F);
            this.focalPointAngleMap.put(cst, newDegree);
        });
    }

    private float distance(Collection<Float> angles, float newAngle) {
        float minDistance = Float.MAX_VALUE;
        for (Float angle : angles) {
            float distance = Math.abs(angle - newAngle);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        return minDistance;
    }

    private void initialize(Level level) {
        this.indexedActiveMap.clear();
        for (MoonPhase ph : MoonPhase.values()) {
            this.indexedActiveMap.put(ph, new LinkedList<>());
        }

        RandomSource rand = ctx.getRandom();

        boolean[] occupiedSlots = new boolean[MoonPhase.values().length];
        Arrays.fill(occupiedSlots, false);

        List<BaseConstellation> constellations = Lists.newArrayList(RegistriesAS.REGISTRY_CONSTELLATIONS);
        MiscUtil.shuffle(constellations, rand);
        constellations.forEach(cst -> addConstellationCycle(cst, rand, occupiedSlots));


        this.indexedDistributionMap.clear();
        for (MoonPhase ph : MoonPhase.values()) {
            this.indexedDistributionMap.put(ph, new HashMap<>());

            this.indexedActiveMap.get(ph).forEach(cst -> {
                int distance = ShowUpConditionProperty.getDistance(ph, this.getActiveIndexedPhases(cst));
                float distribution = Mth.clamp(1F - distance * defaultDistanceDropOff, 0F, 1F);
                if (cst.hasProperty(ShowUpConditionProperty.KEY)) {
                    ShowUpConditionProperty prop = cst.getProperty(ShowUpConditionProperty.KEY);
                    if (prop != null && prop.canIndexPhaseDistribution()) {
                        distribution = prop.getDistribution(level, lastRecordedDay, ctx.getEffectSeed(), true);
                    }
                }
                this.indexedDistributionMap.get(ph).put(cst, distribution);
            });
        }
    }

    private void addConstellationCycle(BaseConstellation cst, RandomSource rand, boolean[] slots) {
        if (cst.hasProperty(ShowUpConditionProperty.KEY)) {
            cst.getPropertyOpt(ShowUpConditionProperty.KEY).ifPresent(prop -> {
                if (prop.canIndexPhaseDistribution()) {
                    for (MoonPhase phase : MoonPhase.values()) {
                        if (prop.doesShowUp(null, phase.ordinal(), ctx.getEffectSeed())) {
                            this.indexedActiveMap.get(phase).add(cst);
                        }
                    }
                }
            });
        } else {
            //Index with 5 active phases.
            int start = searchForSpot(rand, slots);
            occupySlots(start, slots);
            if (getSlots(slots) <= 0) {
                Arrays.fill(slots, false);
            }

            for (int i = 0; i < 5; i++) {
                MoonPhase ph = getPhase(start + i);
                this.indexedActiveMap.get(ph).add(cst);
            }
        }
    }

    private MoonPhase getPhase(int rIndex) {
        int moonPhaseCount = MoonPhase.values().length;
        while (rIndex < 0) {
            rIndex += moonPhaseCount;
        }
        return MoonPhase.values()[rIndex % moonPhaseCount];
    }

    private int searchForSpot(RandomSource r, boolean[] occupied) {
        int start;
        boolean foundFree = false;
        int tries = 5;
        do {
            tries--;
            start = r.nextInt(8);

            int count = getSlots(occupied);
            if (count >= 3) {
                foundFree = true;
            }
        } while (!foundFree && tries > 0);
        return start;
    }

    private void occupySlots(int start, boolean[] occupied) {
        for (int i = 0; i < 5; i++) {
            int index = (start + i) % 8;
            if (!occupied[index]) occupied[index] = true;
        }
    }

    private int getSlots(boolean[] array) {
        int it = 0;
        for (boolean b : array) {
            if (!b) it++;
        }
        return it;
    }
}
