package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalCalculations
 * Created by HellFirePvP
 * Date: 04.08.2016 / 23:41
 */
public class CrystalCalculations {

    //Depends on size and collectivity
    //Can collect up to 5F at max.
    public static float getCollectionAmt(CrystalProperties properties, float distribution) {
        float sizeDistr = (((float) properties.getSize()) / 100F);
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

    //1F -> none, 0F -> ALL
    /*public static float getDischargePerc(CrystalProperties properties) {
        return (float) Math.sqrt(((float) properties.getPurity()) / 100F);
    }*/

}
