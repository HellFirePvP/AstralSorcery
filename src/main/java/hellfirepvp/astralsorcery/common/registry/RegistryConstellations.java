package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.constellation.AppearanceCondition;
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
        ConstellationRegistry.registerTier(0, createRInfo(0.2, -0.2, 0, 5), 1.0F);

        //Only visible during full, new, and waning moon.
        ConstellationRegistry.registerTier(1, createRInfo(-0.2, -0.2, -0.05, 6), 1.0F,
                AppearanceCondition.buildChainedCondition(new AppearanceCondition.ConditionBetweenPhases(CelestialHandler.MoonPhase.FULL, CelestialHandler.MoonPhase.NEW)));


        StarLocation sl8, sl9, sl10, sl11, sl12, sl13, sl14;

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

        orion.register("orion", 0);

        aquila = new Constellation();
        sl1 = aquila.addStar(8, 5);
        sl2 = aquila.addStar(4, 20);
        sl3 = aquila.addStar(27, 1);
        sl4 = aquila.addStar(20, 15);
        sl5 = aquila.addStar(29, 29);

        aquila.addConnection(sl1, sl2);
        aquila.addConnection(sl1, sl3);
        aquila.addConnection(sl2, sl4);
        aquila.addConnection(sl1, sl4);
        aquila.addConnection(sl3, sl4);
        aquila.addConnection(sl4, sl5);

        aquila.register("aquila", 0);

        ara = new Constellation();
        sl1 = ara.addStar(9, 1);
        sl2 = ara.addStar(25, 8);
        sl3 = ara.addStar(23, 16);
        sl4 = ara.addStar(27, 25);
        sl5 = ara.addStar(5, 29);
        sl6 = ara.addStar(12, 18);
        sl7 = ara.addStar(13, 14);

        ara.addConnection(sl1, sl2);
        ara.addConnection(sl2, sl3);
        ara.addConnection(sl3, sl4);
        ara.addConnection(sl4, sl5);
        ara.addConnection(sl5, sl6);
        ara.addConnection(sl6, sl7);
        ara.addConnection(sl7, sl1);

        ara.register("ara", 0);

        bootes = new Constellation();
        sl1 = bootes.addStar(14, 30);
        sl2 = bootes.addStar(20, 24);
        sl3 = bootes.addStar(28, 26);
        sl4 = bootes.addStar(30, 28);
        sl5 = bootes.addStar(12, 17);
        sl6 = bootes.addStar(4, 10);
        sl7 = bootes.addStar(7, 1);
        sl8 = bootes.addStar(16, 5);
        sl9 = bootes.addStar(17, 13);

        bootes.addConnection(sl1, sl2);
        bootes.addConnection(sl2, sl3);
        bootes.addConnection(sl3, sl4);
        bootes.addConnection(sl2, sl5);
        bootes.addConnection(sl5, sl6);
        bootes.addConnection(sl6, sl7);
        bootes.addConnection(sl7, sl8);
        bootes.addConnection(sl8, sl9);
        bootes.addConnection(sl9, sl2);

        bootes.register("bootes", 0);

        caelum = new Constellation();
        sl1 = caelum.addStar(2, 3);
        sl2 = caelum.addStar(13, 6);
        sl3 = caelum.addStar(16, 18);
        sl4 = caelum.addStar(23, 30);

        caelum.addConnection(sl1, sl2);
        caelum.addConnection(sl2, sl3);
        caelum.addConnection(sl3, sl4);

        caelum.register("caelum", 0);

        circinus = new Constellation();
        sl1 = circinus.addStar(6, 2);
        sl2 = circinus.addStar(18, 11);
        sl3 = circinus.addStar(27, 18);
        sl4 = circinus.addStar(2, 7);

        circinus.addConnection(sl1, sl2);
        circinus.addConnection(sl2, sl3);
        circinus.addConnection(sl3, sl4);

        circinus.register("circinus", 0);

        fornax = new Constellation();
        sl1 = fornax.addStar(3, 11);
        sl2 = fornax.addStar(13, 16);
        sl3 = fornax.addStar(29, 8);

        fornax.addConnection(sl1, sl2);
        fornax.addConnection(sl2, sl3);

        fornax.register("fornax", 0);

        horologium = new Constellation();
        sl1 = horologium.addStar(1, 1);
        sl2 = horologium.addStar(27, 7);
        sl3 = horologium.addStar(30, 12);
        sl4 = horologium.addStar(28, 15);
        sl5 = horologium.addStar(12, 22);
        sl6 = horologium.addStar(15, 30);

        horologium.addConnection(sl1, sl2);
        horologium.addConnection(sl2, sl3);
        horologium.addConnection(sl3, sl4);
        horologium.addConnection(sl4, sl5);
        horologium.addConnection(sl5, sl6);

        horologium.register("horologium", 0);

        libra = new Constellation();
        sl1 = libra.addStar(5, 30);
        sl2 = libra.addStar(6, 26);
        sl3 = libra.addStar(7, 8);
        sl4 = libra.addStar(13, 1);
        sl5 = libra.addStar(27, 10);
        sl6 = libra.addStar(22, 21);

        libra.addConnection(sl1, sl2);
        libra.addConnection(sl2, sl3);
        libra.addConnection(sl3, sl4);
        libra.addConnection(sl4, sl5);
        libra.addConnection(sl5, sl6);
        libra.addConnection(sl3, sl5);

        libra.register("libra", 0);

        octans = new Constellation();
        sl1 = octans.addStar(1, 28);
        sl2 = octans.addStar(17, 1);
        sl3 = octans.addStar(20, 3);
        sl4 = octans.addStar(29, 4);

        octans.addConnection(sl1, sl2);
        octans.addConnection(sl2, sl3);
        octans.addConnection(sl3, sl4);
        octans.addConnection(sl4, sl1);

        octans.register("octans", 0);

        phoenix = new Constellation();
        sl1 = phoenix.addStar(1, 1);
        sl2 = phoenix.addStar(2, 16);
        sl3 = phoenix.addStar(11, 30);
        sl4 = phoenix.addStar(11, 9);
        sl5 = phoenix.addStar(21, 1);
        sl6 = phoenix.addStar(29, 10);

        phoenix.addConnection(sl1, sl2);
        phoenix.addConnection(sl2, sl3);
        phoenix.addConnection(sl3, sl4);
        phoenix.addConnection(sl4, sl1);
        phoenix.addConnection(sl4, sl5);
        phoenix.addConnection(sl5, sl6);
        phoenix.addConnection(sl6, sl4);

        phoenix.register("phoenix", 0);

        scutum = new Constellation();
        sl1 = scutum.addStar(3, 1);
        sl2 = scutum.addStar(8, 13);
        sl3 = scutum.addStar(10, 17);
        sl4 = scutum.addStar(21, 29);
        sl5 = scutum.addStar(16, 9);

        scutum.addConnection(sl1, sl2);
        scutum.addConnection(sl2, sl3);
        scutum.addConnection(sl3, sl4);
        scutum.addConnection(sl4, sl5);
        scutum.addConnection(sl5, sl1);

        scutum.register("scutum", 0);

        noctua = new Constellation();
        sl1 = noctua.addStar(29, 1);
        sl2 = noctua.addStar(25, 14);
        sl3 = noctua.addStar(17, 12);
        sl4 = noctua.addStar(21, 24);
        sl5 = noctua.addStar(24, 23);
        sl6 = noctua.addStar(10, 10);
        sl7 = noctua.addStar(5, 13);
        sl8 = noctua.addStar(1, 15);
        sl9 = noctua.addStar(3, 20);

        noctua.addConnection(sl1, sl2);
        noctua.addConnection(sl2, sl3);
        noctua.addConnection(sl3, sl4);
        noctua.addConnection(sl4, sl5);
        noctua.addConnection(sl3, sl6);
        noctua.addConnection(sl6, sl7);
        noctua.addConnection(sl7, sl8);
        noctua.addConnection(sl7, sl9);

        noctua.register("noctua", 0);

        rohini = new Constellation();
        sl1 = rohini.addStar(10, 1);
        sl2 = rohini.addStar(24, 9);
        sl3 = rohini.addStar(28, 13);
        sl4 = rohini.addStar(30, 18);
        sl5 = rohini.addStar(25, 19);
        sl6 = rohini.addStar(18, 18);
        sl7 = rohini.addStar(1, 12);

        rohini.addConnection(sl1, sl2);
        rohini.addConnection(sl2, sl3);
        rohini.addConnection(sl3, sl4);
        rohini.addConnection(sl4, sl5);
        rohini.addConnection(sl5, sl6);
        rohini.addConnection(sl6, sl7);

        rohini.register("rohini", 0);

        chitra = new Constellation();
        sl1 = chitra.addStar(29, 1);
        sl2 = chitra.addStar(29, 6);
        sl3 = chitra.addStar(23, 12);
        sl4 = chitra.addStar(24, 2);
        sl5 = chitra.addStar(18, 5);
        sl6 = chitra.addStar(13, 12);
        sl7 = chitra.addStar(19, 22);
        sl8 = chitra.addStar(3, 10);
        sl9 = chitra.addStar(6, 22);
        sl10 = chitra.addStar(9, 26);

        chitra.addConnection(sl1, sl2);
        chitra.addConnection(sl2, sl3);
        chitra.addConnection(sl3, sl4);
        chitra.addConnection(sl4, sl1);
        chitra.addConnection(sl3, sl5);
        chitra.addConnection(sl5, sl6);
        chitra.addConnection(sl6, sl7);
        chitra.addConnection(sl7, sl3);
        chitra.addConnection(sl6, sl8);
        chitra.addConnection(sl8, sl9);
        chitra.addConnection(sl9, sl10);
        chitra.addConnection(sl10, sl7);

        chitra.register("chitra", 0);

        draco = new Constellation();
        sl1 = draco.addStar(1, 29);
        sl2 = draco.addStar(6, 30);
        sl3 = draco.addStar(7, 26);
        sl4 = draco.addStar(4, 24);
        sl5 = draco.addStar(2, 9);
        sl6 = draco.addStar(2, 3);
        sl7 = draco.addStar(8, 9);
        sl8 = draco.addStar(10, 6);
        sl9 = draco.addStar(12, 17);
        sl10 = draco.addStar(18, 27);
        sl11 = draco.addStar(24, 25);
        sl12 = draco.addStar(27, 19);
        sl13 = draco.addStar(28, 8);
        sl14 = draco.addStar(30, 1);

        draco.addConnection(sl1, sl2);
        draco.addConnection(sl2, sl3);
        draco.addConnection(sl3, sl4);
        draco.addConnection(sl4, sl1);
        draco.addConnection(sl4, sl5);
        draco.addConnection(sl5, sl6);
        draco.addConnection(sl5, sl7);
        draco.addConnection(sl7, sl8);
        draco.addConnection(sl7, sl9);
        draco.addConnection(sl9, sl10);
        draco.addConnection(sl10, sl11);
        draco.addConnection(sl11, sl12);
        draco.addConnection(sl12, sl13);
        draco.addConnection(sl13, sl14);

        draco.register("draco", 0);

    }

    private static Tier.RInformation createRInfo(double x, double y, double z, double size) {
        return Tier.RInformation.createRenderInfoFor(x, y, z, size);
    }

}
