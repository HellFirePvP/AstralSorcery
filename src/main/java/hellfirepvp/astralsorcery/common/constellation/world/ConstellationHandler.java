/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.base.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.*;
import hellfirepvp.astralsorcery.common.data.config.entry.GeneralConfig;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationHandler
 * Created by HellFirePvP
 * Date: 01.07.2019 / 19:52
 */
public class ConstellationHandler {

    private final WorldContext ctx;

    private Map<MoonPhase, LinkedList<IConstellation>> activeMap = Maps.newHashMap();
    private Map<IConstellation, MoonPhase> directOffsetMap = Maps.newHashMap();

    private int lastRecordedDay = -1;
    private List<IConstellation> visibleSpecialConstellations = Lists.newArrayList();

    ConstellationHandler(WorldContext context) {
        this.ctx = context;
    }

    @Nullable
    public MoonPhase getOffset(IConstellation cst) {
        return this.directOffsetMap.get(cst);
    }

    public boolean isActiveCurrently(IConstellation cst, MoonPhase phase) {
        return isActiveInPhase(cst, phase) || this.visibleSpecialConstellations.contains(cst);
    }

    public boolean isActiveInPhase(IConstellation cst, MoonPhase phase) {
        return this.activeMap.get(phase).contains(cst);
    }

    public int getLastTrackedDay() {
        return lastRecordedDay;
    }

    public void tick(World world) {
        if (activeMap.isEmpty()) {
            initialize();
        }

        int currentDay = (int) (world.getDayTime() / GeneralConfig.CONFIG.dayLength.get());

        int dayDifference = currentDay - lastRecordedDay;
        if (dayDifference != 0) {
            lastRecordedDay = currentDay;
            updateActiveConstellations(world);
        }
    }

    private void updateActiveConstellations(World world) {
        this.visibleSpecialConstellations.clear();
        MoonPhase ph = MoonPhase.fromWorld(world);

        LinkedList<IConstellation> active = new LinkedList<>(this.activeMap.computeIfAbsent(ph, p -> Lists.newLinkedList()));
        for (IConstellationSpecialShowup cst : ConstellationRegistry.getSpecialShowupConstellations()) {
            if (cst.doesShowUp(world, lastRecordedDay)) {
                this.visibleSpecialConstellations.add(cst);
            }
        }
        active.addAll(this.visibleSpecialConstellations);

        this.ctx.getActiveCelestialsHandler().updatePositions(active);
    }

    private void initialize() {
        this.activeMap.clear();
        this.directOffsetMap.clear();
        for (MoonPhase ph : MoonPhase.values()) {
            this.activeMap.put(ph, Lists.newLinkedList());
        }

        Random rand = ctx.getRandom();

        boolean[] occupiedSlots = new boolean[MoonPhase.values().length];
        Arrays.fill(occupiedSlots, false);

        LinkedList<IWeakConstellation> weakAndMajor = Lists.newLinkedList(ConstellationRegistry.getWeakConstellations());
        Collections.shuffle(weakAndMajor, rand);
        weakAndMajor.forEach(c -> addConstellationCycle(c, rand, occupiedSlots));

        LinkedList<IConstellation> constellations = Lists.newLinkedList(ConstellationRegistry.getMinorConstellations());
        Collections.shuffle(constellations, rand);
        constellations.forEach(c -> addConstellationCycle(c, rand, occupiedSlots));
    }

    private void addConstellationCycle(IConstellation cst, Random rand, boolean[] slots) {
        if (cst instanceof IConstellationSpecialShowup) {
            return;
        }

        if (cst instanceof IMinorConstellation) {
            for (MoonPhase ph : ((IMinorConstellation) cst).getShowupMoonPhases(ctx.getSeed())) {
                this.activeMap.get(ph).add(cst);
            }
        } else {
            int start = searchForSpot(rand, slots);
            occupySlots(start, slots);
            if (getSlots(slots) <= 0) {
                Arrays.fill(slots, false);
            }


            for (int i = 0; i < 5; i++) {
                MoonPhase ph = getPhase(start + i);
                this.activeMap.get(ph).add(cst);
            }

            this.directOffsetMap.put(cst, getPhase(start));
        }
    }

    private MoonPhase getPhase(int rIndex) {
        int moonPhaseCount = MoonPhase.values().length;
        while (rIndex < 0) {
            rIndex += moonPhaseCount;
        }
        return MoonPhase.values()[rIndex % moonPhaseCount];
    }

    private int searchForSpot(Random r, boolean[] occupied) {
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
