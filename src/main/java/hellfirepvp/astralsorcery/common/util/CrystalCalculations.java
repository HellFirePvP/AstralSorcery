/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import net.minecraft.util.math.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalCalculations
 * Created by HellFirePvP
 * Date: 04.08.2016 / 23:41
 */
public class CrystalCalculations {

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

    public static float getPerfection(CrystalProperties properties) {
        float purity = MathHelper.sqrt((((float) properties.getPurity()) / 100D));
        float cutting = MathHelper.sqrt((((float) properties.getCollectiveCapability()) / 100D));
        float size = MathHelper.sqrt((((float) properties.getSize()) / 900D));
        return purity * cutting * size;
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
