package hellfire.astralSorcery.common.registry;

import hellfire.astralSorcery.api.constellation.IConstellationTier;
import hellfire.astralSorcery.api.constellation.StarLocation;
import hellfire.astralSorcery.common.constellation.Constellation;
import hellfire.astralSorcery.common.constellation.ConstellationRegistry;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 06.02.2016 02:14
 */
public class RegistryConstellations {

    public static void init() {
        ConstellationRegistry.registerTier(0, createRInfo(0.2, -0.2, 0, 5), 1.0F);
        ConstellationRegistry.registerTier(1, createRInfo(-0.2, -0.2, -0.05, 6), 1.0F);

        Constellation bigDipper = new Constellation();
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

        bigDipper.register(0);

        Constellation orion = new Constellation();
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

        orion.register(1);
    }

    private static IConstellationTier.RInformation createRInfo(double x, double y, double z, double size) {
        return IConstellationTier.RInformation.createRenderInfoFor(x, y, z, size);
    }

}
