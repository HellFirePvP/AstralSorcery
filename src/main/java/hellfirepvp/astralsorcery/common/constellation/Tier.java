package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

import java.awt.*;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
    private final ProgressionTier progressionNeeded;
    private final AppearanceCondition condition;
    private LinkedList<Constellation> constellations = new LinkedList<>();

    public Tier(int tierNumber, ProgressionTier visibleTier, float chance, RInformation renderinfo) {
        this(tierNumber, visibleTier, chance, renderinfo, null);
    }

    public Tier(int tierNumber, ProgressionTier visibleTier, float chance, RInformation renderinfo, AppearanceCondition condition) {
        this.tierNumber = tierNumber;
        this.renderinfo = renderinfo;
        this.chance = chance;
        this.condition = condition;
        this.progressionNeeded = visibleTier;
    }

    public int tierNumber() {
        return tierNumber;
    }

    public ProgressionTier getProgressionNeeded() {
        return progressionNeeded;
    }

    public void addConstellation(Constellation constellation) {
        this.constellations.addLast(constellation);
    }

    public List<Constellation> getConstellations() {
        return Collections.unmodifiableList(constellations);
    }

    public Constellation getRandomConstellation(Random rand) {
        List<Constellation> cList = getConstellations();
        if(cList.isEmpty()) return null;
        return cList.get(rand.nextInt(cList.size()));
    }

    public RInformation getRenderInformation() {
        return renderinfo;
    }

    public Color calcRenderColor() {
        float perc = ((float) tierNumber) / ((float) ConstellationRegistry.getHighestTierNumber());
        return new Color(Color.HSBtoRGB((230F + (50F * perc)) / 360F, 0.8F, 0.8F - (0.3F * perc)));
    }

    public String chanceAsRarityUnlocName() {
        String unloc = "tier.chance.";
        if(chance <= 0.1) {
            unloc += "rare";
        } else if(chance <= 0.25) {
            unloc += "scarce";
        } else if(chance <= 0.5) {
            unloc += "uncommon";
        } else if(chance < 1) {
            unloc += "common";
        } else {
            unloc += "always";
        }
        return unloc;
    }

    public String getUnlocalizedName() {
        return "tier." + tierNumber;
    }

    public float getShowupChance() {
        return chance;
    }

    public boolean areAppearanceConditionsMet(CelestialHandler.MoonPhase currentMoonPhase, EnumSet<CelestialHandler.CelestialEvent> specialEvents) {
        return condition == null || condition.shouldAppearThisNight(currentMoonPhase, specialEvents);
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
