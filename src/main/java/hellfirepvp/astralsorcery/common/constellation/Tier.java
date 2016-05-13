package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.common.util.Vector3;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Tier
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:36
 */
public class Tier {

    private final RInformation renderinfo;
    private final int tierNumber;
    private final float chance;
    private final AppearanceCondition condition;
    private LinkedList<Constellation> constellations = new LinkedList<Constellation>();

    public Tier(int tierNumber, float chance, RInformation renderinfo, AppearanceCondition condition) {
        this.tierNumber = tierNumber;
        this.renderinfo = renderinfo;
        this.chance = chance;
        this.condition = condition;
    }

    public int tierNumber() {
        return tierNumber;
    }

    public void addConstellation(Constellation constellation) {
        this.constellations.addLast(constellation);
    }

    public List<Constellation> getConstellations() {
        return Collections.unmodifiableList(constellations);
    }

    public RInformation getRenderInformation() {
        return renderinfo;
    }

    public Color calcRenderColor() {
        float perc = ((float) tierNumber) / ((float) ConstellationRegistry.getHighestTierNumber());
        return new Color(Color.HSBtoRGB((230F + (50F * perc)) / 360F, 0.8F, 0.8F - (0.3F * perc)));
    }

    public float getShowupChance() {
        return chance;
    }

    public boolean areAppearanceConditionsMet(CelestialHandler.MoonPhase currentMoonPhase, boolean isDayOfSolarEclipse, boolean isDayOfLunarEclipse) {
        if(condition.needsLunarEclipse && !isDayOfLunarEclipse) return false;
        if(condition.needsSolarEclipse && !isDayOfSolarEclipse) return false;
        boolean lunarCondition;
        if(condition.ignoresLunarCycles) {
            lunarCondition = true;
        } else {
            int eOrdinal = condition.endingPhaseIncl.ordinal();
            int bOrdinal = condition.beginningPhaseIncl.ordinal();
            int cOrdinal = currentMoonPhase.ordinal();
            if(bOrdinal > eOrdinal) {
                int mov = 8 - bOrdinal;
                bOrdinal += mov;
                eOrdinal += mov;
                cOrdinal += mov;
            }
            lunarCondition = cOrdinal >= bOrdinal && cOrdinal <= eOrdinal;
        }
        return lunarCondition;
    }

    public static class AppearanceCondition {

        public final boolean ignoresLunarCycles;
        public final CelestialHandler.MoonPhase beginningPhaseIncl, endingPhaseIncl;
        public final boolean needsLunarEclipse, needsSolarEclipse;

        public AppearanceCondition(boolean ignoresLunarCycles, CelestialHandler.MoonPhase beginningPhaseIncl, CelestialHandler.MoonPhase endingPhaseIncl, boolean needsLunarEclipse, boolean needsSolarEclipse) {
            this.ignoresLunarCycles = ignoresLunarCycles;
            this.beginningPhaseIncl = beginningPhaseIncl;
            this.endingPhaseIncl = endingPhaseIncl;
            this.needsLunarEclipse = needsLunarEclipse;
            this.needsSolarEclipse = needsSolarEclipse;
        }
    }

    public static class RInformation {

        public final Vector3 offset, incU, incV;

        public RInformation(Vector3 offsetVecUV00, Vector3 vecUV10, Vector3 vecUV01) {
            this.offset = offsetVecUV00;
            this.incU = vecUV10;
            this.incV = vecUV01;
        }

        // #WeCareAboutRenderPerformance
        // There are things in life, you just don't need to understand. That's one of them.
        // Pass coordinates between -1 and 1, size may be between 1 and ~6 (don't ask) but try yourself if needed.
        public static RInformation createRenderInfoFor(double x, double y, double z, double rSize) {
            double modSize = 0.5 * rSize;
            double fx = x * 100.0D;
            double fy = y * 100.0D;
            double fz = z * 100.0D;
            double d8 = Math.atan2(x, z);
            double d9 = Math.sin(d8);
            double d10 = Math.cos(d8);
            double d11 = Math.atan2(Math.sqrt(x * x + z * z), y);
            double d12 = Math.sin(d11);
            double d13 = Math.cos(d11);

            double d23 = modSize * d12;
            double d24 = -(modSize * d13);
            double d25 = d24 * d9 - modSize * d10;
            double d26 = modSize * d9 + d24 * d10;
            Vector3 vecUV00 = new Vector3(fx + d25, fy + d23, fz + d26);
            d23 = -(modSize * d12);
            d24 = (modSize * d13);
            d25 = d24 * d9 - modSize * d10;
            d26 = modSize * d9 + d24 * d10;
            Vector3 vecUV10 = new Vector3(fx + d25, fy + d23, fz + d26);
            d23 = modSize * d12;
            d24 = -(modSize * d13);
            d25 = d24 * d9 + modSize * d10;
            d26 = -(modSize * d9) + d24 * d10;
            Vector3 vecUV01 = new Vector3(fx + d25, fy + d23, fz + d26);
            return new RInformation(vecUV00, vecUV10, vecUV01);
        }

    }

}
