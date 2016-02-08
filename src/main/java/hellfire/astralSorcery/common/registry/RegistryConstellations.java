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

    //Yea. don't ask how i got those coordinates. or how to get those. or how you may change them. just. leave. it.
    //never. touch. it. again.
    public static void init() {
        ConstellationRegistry.registerTier(0, createRInfo(0.2, -0.2, 0, 5), 1.0F);
        ConstellationRegistry.registerTier(1, createRInfo(0, 0, 0, 0), 0.0F); //TODO Fill with actual data.

        Constellation bigDipper = new Constellation();
        StarLocation sl1 = bigDipper.addStar(1, 3);
        StarLocation sl2 = bigDipper.addStar(11, 7);
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

    }

    private static IConstellationTier.RInformation createRInfo(double x, double y, double z, double size) {
        return IConstellationTier.RInformation.createRenderInfoFor(x, y, z, size);
    }

}
