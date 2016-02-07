package hellfire.astralSorcery.common.registry;

import hellfire.astralSorcery.api.constellation.IConstellationTier;
import hellfire.astralSorcery.api.constellation.StarLocation;
import hellfire.astralSorcery.common.constellation.Constellation;
import hellfire.astralSorcery.common.constellation.ConstellationRegistry;
import hellfire.astralSorcery.common.util.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 06.02.2016 02:14
 */
public class RegistryConstellations {

    //Yea. don't ask how i got those coordinates. or how to get those. or how you may change them. just. leave. it.
    //never. touch. it. again.
    public static void init() {
        IConstellationTier.RInformation renderInfo = new IConstellationTier.RInformation(
                new Vector3(21.76776695296637, -18.23223304703363, 2.5),
                new Vector3(18.23223304703363, -21.76776695296637, 2.5),
                new Vector3(21.76776695296637, -18.23223304703363, -2.5), 5);
        ConstellationRegistry.registerTier(0, renderInfo, 1.0F);

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

}
