/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.init;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.level.CelestialEventHandler;
import hellfirepvp.astralsorcery.common.constellation.level.LevelSkyContext;
import hellfirepvp.astralsorcery.common.constellation.level.LevelSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.level.event.SolarEclipseEvent;
import hellfirepvp.astralsorcery.common.constellation.property.AttunePlayerProperty;
import hellfirepvp.astralsorcery.common.constellation.property.FocusCrystalProperty;
import hellfirepvp.astralsorcery.common.constellation.property.FocusCrystalSortProperty;
import hellfirepvp.astralsorcery.common.constellation.property.ShowUpConditionProperty;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.focal.FocusCrystalPlacementHelper;
import hellfirepvp.astralsorcery.common.focal.FocusCrystalVisualSortHelper;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static hellfirepvp.astralsorcery.common.lib.constants.ColorsAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: InitConstellations
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class InitConstellations {

    // ARA/Ara
    public static DeferredHolder<BaseConstellation, BaseConstellation> aevitas(DeferredRegister<BaseConstellation> register) {
        var builder = BaseConstellation.builder(CONSTELLATION_AEVITAS)
                .withProperty(FocusCrystalProperty.KEY, FocusCrystalProperty.of(FocusCrystalPlacementHelper::hasOppositeButNoAxisSymmetry))
                .withProperty(FocusCrystalSortProperty.KEY, FocusCrystalSortProperty.of(FocusCrystalVisualSortHelper::sortIntoContinuousPolygon))
                .withProperty(AttunePlayerProperty.KEY, AttunePlayerProperty.defaultRoot())
                .tier(BaseConstellation.Tier.MAJOR)
                .sorted();
        StarLocation sl1 = builder.addStar(13, 4);
        StarLocation sl2 = builder.addStar(15, 18);
        StarLocation sl3 = builder.addStar(10, 27);
        StarLocation sl4 = builder.addStar(27, 24);
        StarLocation sl5 = builder.addStar(24, 19);
        StarLocation sl6 = builder.addStar(25, 10);
        StarLocation sl7 = builder.addStar(3, 6);

        builder.addConnection(sl1, sl2);
        builder.addConnection(sl2, sl3);
        builder.addConnection(sl3, sl4);
        builder.addConnection(sl4, sl5);
        builder.addConnection(sl5, sl6);
        builder.addConnection(sl6, sl1);
        builder.addConnection(sl1, sl7);

        //builder.addSignatureItem(ItemTags.SAPLINGS);
        //builder.addSignatureItem(TagsAS.Items.DUSTS_STARDUST);
        //builder.addSignatureItem(Tags.Items.SEEDS_WHEAT);
        //builder.addSignatureItem(Blocks.SUGAR_CANE);

        return register.register("aevitas", builder.build(BaseConstellation::new));
    }

    // CEP/Cepheus
    public static DeferredHolder<BaseConstellation, BaseConstellation> armara(DeferredRegister<BaseConstellation> register) {
        var builder = BaseConstellation.builder(CONSTELLATION_ARMARA)
                .withProperty(FocusCrystalProperty.KEY, FocusCrystalProperty.of(FocusCrystalPlacementHelper::hasOneAxisSymmetryButNotOther))
                .withProperty(FocusCrystalSortProperty.KEY, FocusCrystalSortProperty.of(FocusCrystalVisualSortHelper::sortIntoContinuousPolygon))
                .withProperty(AttunePlayerProperty.KEY, AttunePlayerProperty.defaultRoot())
                .tier(BaseConstellation.Tier.MAJOR)
                .sorted();

        StarLocation sl1 = builder.addStar(26, 26);
        StarLocation sl2 = builder.addStar(24, 15);
        StarLocation sl3 = builder.addStar(16, 22);
        StarLocation sl4 = builder.addStar(20, 5);
        StarLocation sl5 = builder.addStar(15, 4);
        StarLocation sl6 = builder.addStar(8, 12);
        StarLocation sl7 = builder.addStar(5, 18);

        builder.addConnection(sl1, sl2);
        builder.addConnection(sl2, sl3);
        builder.addConnection(sl1, sl3);
        builder.addConnection(sl2, sl4);
        builder.addConnection(sl4, sl5);
        builder.addConnection(sl5, sl6);
        builder.addConnection(sl3, sl6);
        builder.addConnection(sl6, sl7);

        return register.register("armara", builder.build(BaseConstellation::new));
    }

    // DOR/Dorado
    public static DeferredHolder<BaseConstellation, BaseConstellation> discidia(DeferredRegister<BaseConstellation> register) {
        var builder = BaseConstellation.builder(CONSTELLATION_DISCIDIA)
                .withProperty(FocusCrystalProperty.KEY, FocusCrystalProperty.of(FocusCrystalPlacementHelper::noneHaveRightAngles))
                .withProperty(FocusCrystalSortProperty.KEY, FocusCrystalSortProperty.of(FocusCrystalVisualSortHelper::sortIntoContinuousPolygon))
                .withProperty(AttunePlayerProperty.KEY, AttunePlayerProperty.defaultRoot())
                .tier(BaseConstellation.Tier.MAJOR)
                .sorted();

        StarLocation sl1 = builder.addStar(9, 25);
        StarLocation sl2 = builder.addStar(6, 18);
        StarLocation sl3 = builder.addStar(13, 16);
        StarLocation sl4 = builder.addStar(15, 9);
        StarLocation sl5 = builder.addStar(22, 8);
        StarLocation sl6 = builder.addStar(27, 2);

        builder.addConnection(sl1, sl2);
        builder.addConnection(sl2, sl3);
        builder.addConnection(sl1, sl3);
        builder.addConnection(sl3, sl4);
        builder.addConnection(sl3, sl5);
        builder.addConnection(sl4, sl5);
        builder.addConnection(sl5, sl6);

        return register.register("discidia", builder.build(BaseConstellation::new));
    }

    // TAU/Taurus
    public static DeferredHolder<BaseConstellation, BaseConstellation> evorsio(DeferredRegister<BaseConstellation> register) {
        var builder = BaseConstellation.builder(CONSTELLATION_EVORSIO)
                .withProperty(FocusCrystalProperty.KEY, FocusCrystalProperty.of(FocusCrystalPlacementHelper::noDotNoAxisSymmetry))
                .withProperty(FocusCrystalSortProperty.KEY, FocusCrystalSortProperty.of(FocusCrystalVisualSortHelper::sortIntoContinuousPolygon))
                .withProperty(AttunePlayerProperty.KEY, AttunePlayerProperty.defaultRoot())
                .tier(BaseConstellation.Tier.MAJOR)
                .sorted();

        StarLocation sl1 = builder.addStar(27, 17);
        StarLocation sl2 = builder.addStar(19, 23);
        StarLocation sl3 = builder.addStar(25, 27);
        StarLocation sl4 = builder.addStar(22, 12);
        StarLocation sl5 = builder.addStar(13, 3);
        StarLocation sl6 = builder.addStar(16, 11);
        StarLocation sl7 = builder.addStar(6, 5);

        builder.addConnection(sl1, sl2);
        builder.addConnection(sl1, sl3);
        builder.addConnection(sl1, sl4);
        builder.addConnection(sl4, sl5);
        builder.addConnection(sl4, sl6);
        builder.addConnection(sl6, sl7);

        return register.register("evorsio", builder.build(BaseConstellation::new));
    }

    // PHE/Phoenix
    public static DeferredHolder<BaseConstellation, BaseConstellation> vicio(DeferredRegister<BaseConstellation> register) {
        var builder = BaseConstellation.builder(CONSTELLATION_VICIO)
                .withProperty(FocusCrystalProperty.KEY, FocusCrystalProperty.of(FocusCrystalPlacementHelper::allHaveUniqueDistances))
                .withProperty(FocusCrystalSortProperty.KEY, FocusCrystalSortProperty.of(FocusCrystalVisualSortHelper::sortByClosestFirst))
                .withProperty(AttunePlayerProperty.KEY, AttunePlayerProperty.defaultRoot())
                .tier(BaseConstellation.Tier.MAJOR)
                .sorted();

        StarLocation sl1 = builder.addStar(13, 11);
        StarLocation sl2 = builder.addStar(26, 10);
        StarLocation sl3 = builder.addStar(23, 4);
        StarLocation sl4 = builder.addStar(4, 6);
        StarLocation sl5 = builder.addStar(5, 20);
        StarLocation sl6 = builder.addStar(12, 25);

        builder.addConnection(sl1, sl2);
        builder.addConnection(sl2, sl3);
        builder.addConnection(sl1, sl3);
        builder.addConnection(sl1, sl4);
        builder.addConnection(sl4, sl5);
        builder.addConnection(sl5, sl6);
        builder.addConnection(sl6, sl1);

        return register.register("vicio", builder.build(BaseConstellation::new));
    }

    // CRB/Corona Borealis
    public static DeferredHolder<BaseConstellation, BaseConstellation> lucerna(DeferredRegister<BaseConstellation> register) {
        var builder = BaseConstellation.builder(CONSTELLATION_LUCERNA)
                .tier(BaseConstellation.Tier.MINOR)
                .sorted();

        StarLocation sl1 = builder.addStar(19, 4);
        StarLocation sl2 = builder.addStar(25, 14);
        StarLocation sl3 = builder.addStar(22, 22);
        StarLocation sl4 = builder.addStar(15, 25);
        StarLocation sl5 = builder.addStar(8, 23);
        StarLocation sl6 = builder.addStar(4, 12);

        builder.addConnection(sl1, sl2);
        builder.addConnection(sl2, sl3);
        builder.addConnection(sl3, sl4);
        builder.addConnection(sl4, sl5);
        builder.addConnection(sl5, sl6);

        return register.register("lucerna", builder.build(BaseConstellation::new));
    }

    // LAC/Lacerta
    public static DeferredHolder<BaseConstellation, BaseConstellation> mineralis(DeferredRegister<BaseConstellation> register) {
        var builder = BaseConstellation.builder(CONSTELLATION_MINERALIS)
                .tier(BaseConstellation.Tier.MINOR)
                .sorted();

        StarLocation sl1 = builder.addStar(17, 2);
        StarLocation sl2 = builder.addStar(19, 10);
        StarLocation sl3 = builder.addStar(13, 7);
        StarLocation sl4 = builder.addStar(15, 15);
        StarLocation sl5 = builder.addStar(22, 19);
        StarLocation sl6 = builder.addStar(11, 25);
        StarLocation sl7 = builder.addStar(18, 28);

        builder.addConnection(sl1, sl2);
        builder.addConnection(sl1, sl3);
        builder.addConnection(sl4, sl2);
        builder.addConnection(sl4, sl3);
        builder.addConnection(sl4, sl5);
        builder.addConnection(sl4, sl6);
        builder.addConnection(sl7, sl5);
        builder.addConnection(sl7, sl6);

        return register.register("mineralis", builder.build(BaseConstellation::new));
    }

    // HOR/Horologium
    public static DeferredHolder<BaseConstellation, BaseConstellation> horologium(DeferredRegister<BaseConstellation> register) {
        var builder = BaseConstellation.builder(CONSTELLATION_HOROLOGIUM)
                .withProperty(ShowUpConditionProperty.KEY, ShowUpConditionProperty.create(new ShowUpConditionProperty.DynamicShowUpCondition() {
                    @Override
                    public boolean doesShowUp(Level world, long day, long seed) {
                        return LevelSkyHandler.getContext(world).map(LevelSkyContext::getCelestialEventHandler)
                                .map(CelestialEventHandler::getSolarEclipse)
                                .map(SolarEclipseEvent::isActiveDay)
                                .orElse(false);
                    }

                    @Override
                    public float getDistribution(Level world, long day, long seed, boolean active) {
                        return active ? 1F : 0.1F;
                    }
                }))
                .tier(BaseConstellation.Tier.MINOR)
                .sorted();

        StarLocation sl1 = builder.addStar(28, 6);
        StarLocation sl2 = builder.addStar(22, 10);
        StarLocation sl3 = builder.addStar(16, 6);
        StarLocation sl4 = builder.addStar(10, 4);
        StarLocation sl5 = builder.addStar(6, 8);
        StarLocation sl6 = builder.addStar(3, 27);

        builder.addConnection(sl1, sl2);
        builder.addConnection(sl2, sl3);
        builder.addConnection(sl3, sl4);
        builder.addConnection(sl4, sl5);
        builder.addConnection(sl5, sl6);

        return register.register("horologium", builder.build(BaseConstellation::new));
    }

    // OCT/Octans
    public static DeferredHolder<BaseConstellation, BaseConstellation> octans(DeferredRegister<BaseConstellation> register) {
        var builder = BaseConstellation.builder(CONSTELLATION_OCTANS)
                .tier(BaseConstellation.Tier.MINOR)
                .sorted();
        StarLocation sl1 = builder.addStar(25, 25);
        StarLocation sl2 = builder.addStar(17, 5);
        StarLocation sl3 = builder.addStar(11, 10);
        StarLocation sl4 = builder.addStar(4, 6);

        builder.addConnection(sl1, sl2);
        builder.addConnection(sl1, sl3);
        builder.addConnection(sl2, sl3);
        builder.addConnection(sl3, sl4);

        return register.register("octans", builder.build(BaseConstellation::new));
    }

    // BOO/Bootes
    public static DeferredHolder<BaseConstellation, BaseConstellation> bootes(DeferredRegister<BaseConstellation> register) {
        var builder = BaseConstellation.builder(CONSTELLATION_BOOTES)
                .tier(BaseConstellation.Tier.MINOR)
                .sorted();

        StarLocation sl1 = builder.addStar(9, 22);
        StarLocation sl2 = builder.addStar(3, 14);
        StarLocation sl3 = builder.addStar(22, 27);
        StarLocation sl4 = builder.addStar(16, 5);
        StarLocation sl5 = builder.addStar(26, 3);
        StarLocation sl6 = builder.addStar(24, 11);

        builder.addConnection(sl1, sl2);
        builder.addConnection(sl1, sl3);
        builder.addConnection(sl1, sl4);
        builder.addConnection(sl1, sl6);
        builder.addConnection(sl4, sl5);
        builder.addConnection(sl5, sl6);

        return register.register("bootes", builder.build(BaseConstellation::new));
    }

    // Fornax itself is boring gameplay-wise. so have another one instead.
    // CRT/Crater
    public static DeferredHolder<BaseConstellation, BaseConstellation> fornax(DeferredRegister<BaseConstellation> register) {
        var builder = BaseConstellation.builder(CONSTELLATION_FORNAX)
                .tier(BaseConstellation.Tier.MINOR)
                .sorted();

        StarLocation sl1 = builder.addStar(18, 29);
        StarLocation sl2 = builder.addStar(28, 18);
        StarLocation sl3 = builder.addStar(21, 13);
        StarLocation sl4 = builder.addStar(16, 18);
        StarLocation sl5 = builder.addStar(19, 6);
        StarLocation sl6 = builder.addStar(13, 2);
        StarLocation sl7 = builder.addStar(9, 21);
        StarLocation sl8 = builder.addStar(2, 17);

        builder.addConnection(sl1, sl2);
        builder.addConnection(sl2, sl3);
        builder.addConnection(sl3, sl4);
        builder.addConnection(sl4, sl1);
        builder.addConnection(sl3, sl5);
        builder.addConnection(sl5, sl6);
        builder.addConnection(sl4, sl7);
        builder.addConnection(sl7, sl8);

        return register.register("fornax", builder.build(BaseConstellation::new));
    }

    // LEP/Lepus
    public static DeferredHolder<BaseConstellation, BaseConstellation> pelotrio(DeferredRegister<BaseConstellation> register) {
        var builder = BaseConstellation.builder(CONSTELLATION_PELOTRIO)
                .withProperty(ShowUpConditionProperty.KEY, ShowUpConditionProperty.forFixedMoonPhases(0.35F, MoonPhase.NEW, MoonPhase.FULL))
                .tier(BaseConstellation.Tier.MINOR)
                .sorted();

        StarLocation sl1 = builder.addStar(17, 24);
        StarLocation sl2 = builder.addStar(27, 25);
        StarLocation sl3 = builder.addStar(22, 8);
        StarLocation sl4 = builder.addStar(14, 14);
        StarLocation sl5 = builder.addStar(8, 29);
        StarLocation sl6 = builder.addStar(3, 8);
        StarLocation sl7 = builder.addStar(9, 10);

        builder.addConnection(sl1, sl2);
        builder.addConnection(sl2, sl3);
        builder.addConnection(sl3, sl4);
        builder.addConnection(sl4, sl1);
        builder.addConnection(sl1, sl5);
        builder.addConnection(sl5, sl6);
        builder.addConnection(sl6, sl7);
        builder.addConnection(sl7, sl4);

        return register.register("pelotrio", builder.build(BaseConstellation::new));
    }
}
