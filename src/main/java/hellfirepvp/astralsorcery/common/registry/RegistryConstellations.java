package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.constellation.CelestialHandler;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.Tier;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;

import static hellfirepvp.astralsorcery.common.lib.Constellations.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryConstellations
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:40
 */
public class RegistryConstellations {

    public static void init() {
        ConstellationRegistry.registerTier(0, createRInfo(0.2, -0.2, 0, 5), 1.0F,
                new Tier.AppearanceCondition(true, null, null, false, false));

        //Only visible during full, new, and waning moon.
        ConstellationRegistry.registerTier(1, createRInfo(-0.2, -0.2, -0.05, 6), 1.0F,
                new Tier.AppearanceCondition(false, CelestialHandler.MoonPhase.FULL, CelestialHandler.MoonPhase.NEW, false, false));

        bigDipper = new Constellation();
        StarLocation sl1 = bigDipper.addStar(1, 3);
        StarLocation sl2 = bigDipper.addStar(10, 7);
        StarLocation sl3 = bigDipper.addStar(13, 13);
        StarLocation sl4 = bigDipper.addStar(15, 19);
        StarLocation sl5 = bigDipper.addStar(12, 27);
        StarLocation sl6 = bigDipper.addStar(25, 31);
        StarLocation sl7 = bigDipper.addStar(31, 25);

        bigDipper.addConnection(sl1, sl2);
        bigDipper.addConnection(sl2, sl3);
        bigDipper.addConnection(sl3, sl4);
        bigDipper.addConnection(sl4, sl5);
        bigDipper.addConnection(sl5, sl6);
        bigDipper.addConnection(sl6, sl7);
        bigDipper.addConnection(sl7, sl4);

        bigDipper.register("bigDipper", 0);

        orion = new Constellation();
        sl1 = orion.addStar(8, 0);
        sl2 = orion.addStar(20, 2);
        sl3 = orion.addStar(12, 18);
        sl4 = orion.addStar(15, 17);
        sl5 = orion.addStar(18, 15);
        sl6 = orion.addStar(8, 31);
        sl7 = orion.addStar(24, 29);

        orion.addConnection(sl1, sl3);
        orion.addConnection(sl3, sl4);
        orion.addConnection(sl4, sl5);
        orion.addConnection(sl2, sl5);
        orion.addConnection(sl6, sl3);
        orion.addConnection(sl7, sl5);

        orion.register("orion", 1);
    }

    private static Tier.RInformation createRInfo(double x, double y, double z, double size) {
        return Tier.RInformation.createRenderInfoFor(x, y, z, size);
    }

}
