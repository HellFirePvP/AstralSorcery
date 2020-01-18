/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.world;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.base.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IConstellationSpecialShowup;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DistributionHandler
 * Created by HellFirePvP
 * Date: 01.07.2019 / 20:01
 */
public class DistributionHandler {

    private final WorldContext ctx;

    private Map<Integer, Map<IConstellation, Float>> dayDistributionMap = Maps.newHashMap();
    private Map<IConstellation, Float> activeDistribution = Maps.newHashMap();

    private int lastRecordedDay = -1;

    DistributionHandler(WorldContext ctx) {
        this.ctx = ctx;
    }

    public void tick(World world) {
        ConstellationHandler cst = this.ctx.getConstellationHandler();
        int tracked = cst.getLastTrackedDay();

        if (this.dayDistributionMap.isEmpty()) {
            initialize();
        }

        //Nothing changed. Skip tick.
        if (this.lastRecordedDay == tracked) {
            return;
        }

        this.lastRecordedDay = tracked;
        this.updateDistribution(world);
    }

    public float getDistribution(IConstellation cst) {
        return this.activeDistribution.getOrDefault(cst, 0F);
    }

    private void updateDistribution(World world) {
        MoonPhase current = MoonPhase.fromWorld(world);
        Map<IConstellation, Float> distribution = new HashMap<>(this.dayDistributionMap.get(current.ordinal()));

        for (IConstellationSpecialShowup special : ConstellationRegistry.getSpecialShowupConstellations()) {
            if (special.doesShowUp(world, lastRecordedDay)) {
                distribution.put(special, MathHelper.clamp(
                        special.getDistribution(world, lastRecordedDay, true),
                        0F,
                        1F));
            } else {
                distribution.put(special, MathHelper.clamp(
                        special.getDistribution(world, lastRecordedDay, false),
                        0F,
                        1F));
            }
        }

        this.activeDistribution = distribution;
    }

    private void initialize() {
        this.dayDistributionMap.clear();
        for (MoonPhase ph : MoonPhase.values()) {
            this.dayDistributionMap.put(ph.ordinal(), Maps.newHashMap());
        }
        int phaseCount = MoonPhase.values().length;

        for (IConstellation cst : RegistriesAS.REGISTRY_CONSTELLATIONS) {
            if (cst instanceof IWeakConstellation) {
                MoonPhase offsetPhase = this.ctx.getConstellationHandler().getOffset(cst);
                // If it didn't get a mapping by the constellation handler it won't show up either.
                if (offsetPhase == null) {
                    return;
                }
                int offset = offsetPhase.ordinal();

                for (MoonPhase ph : MoonPhase.values()) {
                    int index = (offset + ph.ordinal()) % phaseCount;
                    float distr = sineDistance(offset, index);
                    dayDistributionMap.get(index).put(cst, distr);
                }
            } else {
                for (MoonPhase ph : MoonPhase.values()) {
                    dayDistributionMap.get(ph.ordinal()).put(cst, 0F);
                }
            }
        }
    }

    private float sineDistance(int dayStart, int dayIn) {
        int phaseCount = MoonPhase.values().length;
        int dist = Math.min(Math.abs(dayStart - dayIn), Math.abs(dayStart - (dayIn + phaseCount)));
        float part = ((float) dist) / ((float) (phaseCount / 2));
        return MathHelper.cos((float) ((part / 2) * Math.PI)) * 0.5F + 0.5F;
    }
}
