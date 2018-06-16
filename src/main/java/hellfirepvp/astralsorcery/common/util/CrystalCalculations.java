/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalCalculations
 * Created by HellFirePvP
 * Date: 04.08.2016 / 23:41
 */
public class CrystalCalculations {

    private static Map<IConstellation, Float> fractureModifierMap = new HashMap<IConstellation, Float>() {
        {
            put(Constellations.aevitas,     0.001F);
            put(Constellations.discidia,    0.005F);
            put(Constellations.evorsio,     0.001F);
            put(Constellations.armara,      0.001F);
            put(Constellations.vicio,       0.001F);

            put(Constellations.lucerna,     0.0007F);
            put(Constellations.bootes,      0.1F);
            put(Constellations.mineralis,   0.007F);
            put(Constellations.octans,      0.1F);
            put(Constellations.pelotrio,    4F);
            put(Constellations.horologium,  0.0007F);
            put(Constellations.fornax,      0.009F);
        }
    };

    //Depends on size and collectivity
    //Can collect up to 9F at max, 4F max for rock crystals
    public static float getCollectionAmt(CrystalProperties properties, float distribution) {
        float sizeDistr = (((float) properties.getSize()) / 100F);
        sizeDistr *= 0.6F;
        return distribution * sizeDistr * (((float) properties.getCollectiveCapability()) / 100F);
    }

    //Depends on purity alone - 1F -> all gets through, 0F -> none
    public static float getThroughputMultiplier(CrystalProperties properties) {
        return (float) Math.sqrt((((float) properties.getPurity()) / 100));
    }

    //Between 3 and 9-ish
    //Unused atm
    public static float getThroughputCap(CrystalProperties properties) {
        float sizeDistr = (((float) properties.getSize()) / 100F) / 2;
        return (float) (3 + (Math.pow(sizeDistr, 2)));
    }

    //1-4
    public static double getMaxRitualReduction(CrystalProperties properties) {
        double purity = Math.sqrt((((double) properties.getPurity()) / 100D));
        double cutting = Math.sqrt((((double) properties.getCollectiveCapability()) / 100D));
        return Math.max(1, purity * 2D + cutting * 2D);
    }

    public static int getChannelingCapacity(CrystalProperties properties) {
        float purity = (float) Math.sqrt((((float) properties.getPurity()) / 100));
        return Math.max(10, MathHelper.floor(purity * 20));
    }

    //Let's say you get 1 cycle over what the properties can do (20 at most free) that means you get about 40% fracture per irl day.
    public static float getFractureChance(int castTimes, int cap) {
        if(castTimes <= cap) {
            return 0F;
        }
        int remaining = castTimes - cap;
        return Math.max(1E-8F, remaining / 500_000F);
    }

    public static float getPerfection(CrystalProperties properties, int maxSize) {
        float purity = MathHelper.sqrt((((float) properties.getPurity()) / 100D));
        float cutting = MathHelper.sqrt((((float) properties.getCollectiveCapability()) / 100D));
        float size = MathHelper.sqrt((((float) properties.getSize()) / maxSize));
        return purity * cutting * size;
    }

    //Defines an additional modifier to determine if a single tick of a constellation effect is "strong" or "weak"
    public static float getCstFractureModifier(IWeakConstellation channeling) {
        if (fractureModifierMap.containsKey(channeling)) {
            return fractureModifierMap.get(channeling);
        }
        return 1F;
    }

    //Between 0-18
    /*public static double getMaxRitualEffect(CrystalProperties properties) {
        double purity = Math.sqrt((((float) properties.getPurity()) / 100F));
        double size = (double) properties.getSize() / 100D;
        double cutting = Math.sqrt((((float) properties.getCollectiveCapability()) / 100F));
        double res = size * purity;
        double loss = Math.max(0, (1D - cutting) * res);
        return (res - loss) + (res - loss);
    }*/

    //1F -> none, 0F -> ALL
    /*public static float getDischargePerc(CrystalProperties properties) {
        return (float) Math.sqrt(((float) properties.getPurity()) / 100F);
    }*/

}
