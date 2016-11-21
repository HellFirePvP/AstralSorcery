package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.constellation.ConstellationBase;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryConstellations
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:40
 */
public class RegistryConstellations {

    public static void init() {
        rebuildConstellations();

        registerConstellations();
    }

    private static void registerConstellations() {

    }

    private static void rebuildConstellations() {
        StarLocation sl1, sl2, sl3, sl4, sl5, sl6, sl7, sl8, sl9, sl10, sl11, sl12, sl13, sl14;

        /*orion = new ConstellationBase.Major("orion");
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
        orion.addConnection(sl7, sl5);*/
    }

}
