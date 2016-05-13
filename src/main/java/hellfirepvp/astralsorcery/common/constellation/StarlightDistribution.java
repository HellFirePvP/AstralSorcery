package hellfirepvp.astralsorcery.common.constellation;

import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarlightDistribution
 * Created by HellFirePvP
 * Date: 11.05.2016 / 23:20
 */
public class StarlightDistribution {

    private final Map<Tier, ConstellationDistribution> currentDistributions;

    public StarlightDistribution(Map<Tier, ConstellationDistribution> currentDistributions) {
        this.currentDistributions = currentDistributions;
    }

    public Float getDistributionCharge(Constellation c) {
        Tier t = c.queryTier();
        if (t == null) return 0F;
        ConstellationDistribution cd = currentDistributions.get(t);
        if (!cd.distributionPercentage.containsKey(c)) return 0F;
        return cd.distributionPercentage.get(c);
    }

    public static class ConstellationDistribution {

        private final Map<Constellation, Float> distributionPercentage;

        public ConstellationDistribution(Map<Constellation, Float> distributionPercentage) {
            this.distributionPercentage = distributionPercentage;
        }
    }

}
