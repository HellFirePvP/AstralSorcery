/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.base.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.TagsAS;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import static hellfirepvp.astralsorcery.common.lib.ColorsAS.*;
import static hellfirepvp.astralsorcery.common.lib.ConstellationsAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryConstellations
 * Created by HellFirePvP
 * Date: 01.06.2019 / 15:58
 */
public class RegistryConstellations {

    public static void init() {
        buildConstellations();

        registerSignatureItems();
    }

    private static void registerSignatureItems() {
        aevitas.addSignatureItem(ItemTags.SAPLINGS);
        aevitas.addSignatureItem(TagsAS.Items.DUSTS_STARDUST);
        aevitas.addSignatureItem(Tags.Items.SEEDS_WHEAT);
        aevitas.addSignatureItem(Blocks.SUGAR_CANE);

        discidia.addSignatureItem(Items.FLINT);
        discidia.addSignatureItem(Tags.Items.INGOTS_IRON);
        discidia.addSignatureItem(ItemTags.ARROWS);
        discidia.addSignatureItem(Tags.Items.DUSTS_REDSTONE);

        armara.addSignatureItem(Tags.Items.INGOTS_IRON);
        armara.addSignatureItem(Tags.Items.LEATHER);
        armara.addSignatureItem(Items.CLAY_BALL);
        armara.addSignatureItem(Tags.Items.DUSTS_GLOWSTONE);

        vicio.addSignatureItem(Tags.Items.FEATHERS);
        vicio.addSignatureItem(Items.SUGAR);
        vicio.addSignatureItem(Items.STRING);
        vicio.addSignatureItem(ItemTags.FISHES);

        evorsio.addSignatureItem(Tags.Items.COBBLESTONE);
        evorsio.addSignatureItem(Items.FLINT);
        evorsio.addSignatureItem(Tags.Items.GUNPOWDER);
        evorsio.addSignatureItem(Blocks.TNT);


        lucerna.addSignatureItem(Tags.Items.DUSTS_GLOWSTONE);
        lucerna.addSignatureItem(Blocks.TORCH);
        lucerna.addSignatureItem(Tags.Items.DUSTS_REDSTONE);
        lucerna.addSignatureItem(ItemTags.COALS);

        mineralis.addSignatureItem(Tags.Items.ORES_IRON);
        mineralis.addSignatureItem(Tags.Items.INGOTS_GOLD);
        mineralis.addSignatureItem(Tags.Items.INGOTS_IRON);
        mineralis.addSignatureItem(Tags.Items.DUSTS_REDSTONE);

        horologium.addSignatureItem(TagsAS.Items.DUSTS_STARDUST);
        horologium.addSignatureItem(Tags.Items.GEMS_DIAMOND);
        horologium.addSignatureItem(Tags.Items.ENDER_PEARLS);
        horologium.addSignatureItem(Items.CLOCK);

        octans.addSignatureItem(ItemTags.FISHES);
        octans.addSignatureItem(Items.FISHING_ROD);
        octans.addSignatureItem(Tags.Items.DYES_BLUE);
        octans.addSignatureItem(Items.CLAY_BALL);

        bootes.addSignatureItem(Tags.Items.CROPS_WHEAT);
        bootes.addSignatureItem(Tags.Items.BONES);
        bootes.addSignatureItem(Items.APPLE);
        bootes.addSignatureItem(Tags.Items.LEATHER);

        fornax.addSignatureItem(Items.COAL);
        fornax.addSignatureItem(Tags.Items.DUSTS_REDSTONE);
        fornax.addSignatureItem(Tags.Items.INGOTS_IRON);
        fornax.addSignatureItem(Tags.Items.GUNPOWDER);

        pelotrio.addSignatureItem(Items.ROTTEN_FLESH);
        pelotrio.addSignatureItem(Items.BLAZE_POWDER);
        pelotrio.addSignatureItem(Items.APPLE);
        pelotrio.addSignatureItem(Tags.Items.EGGS);


        gelu.addSignatureItem(Items.SNOWBALL);
        gelu.addSignatureItem(Blocks.ICE);
        gelu.addSignatureItem(Tags.Items.GEMS_QUARTZ);
        gelu.addSignatureItem(Tags.Items.FEATHERS);

        ulteria.addSignatureItem(TagsAS.Items.INGOTS_STARMETAL);
        ulteria.addSignatureItem(Tags.Items.LEATHER);
        ulteria.addSignatureItem(Tags.Items.GEMS_DIAMOND);
        ulteria.addSignatureItem(Tags.Items.RODS_BLAZE);

        alcara.addSignatureItem(Tags.Items.CROPS_NETHER_WART);
        alcara.addSignatureItem(Tags.Items.ENDER_PEARLS);
        alcara.addSignatureItem(Blocks.SOUL_SAND);
        alcara.addSignatureItem(ItemTags.COALS);

        vorux.addSignatureItem(Items.BLAZE_POWDER);
        vorux.addSignatureItem(ItemsAS.NOCTURNAL_POWDER);
        vorux.addSignatureItem(Tags.Items.GUNPOWDER);
        vorux.addSignatureItem(Items.NETHER_BRICK);
    }

    private static void buildConstellations() {
        StarLocation sl1, sl2, sl3, sl4, sl5, sl6, sl7, sl8;

        // DOR/Dorado
        discidia = new Constellation.Major("discidia", CONSTELLATION_DISCIDIA);
        sl1 = discidia.addStar(9, 25);
        sl2 = discidia.addStar(6, 18);
        sl3 = discidia.addStar(13, 16);
        sl4 = discidia.addStar(15, 9);
        sl5 = discidia.addStar(22, 8);
        sl6 = discidia.addStar(27, 2);

        discidia.addConnection(sl1, sl2);
        discidia.addConnection(sl2, sl3);
        discidia.addConnection(sl1, sl3);
        discidia.addConnection(sl3, sl4);
        discidia.addConnection(sl3, sl5);
        discidia.addConnection(sl4, sl5);
        discidia.addConnection(sl5, sl6);
        register(discidia);

        // CEP/Cepheus
        armara = new Constellation.Major("armara", CONSTELLATION_ARMARA);
        sl1 = armara.addStar(5, 5);
        sl2 = armara.addStar(7, 16);
        sl3 = armara.addStar(15, 9);
        sl4 = armara.addStar(11, 26);
        sl5 = armara.addStar(16, 27);
        sl6 = armara.addStar(23, 19);
        sl7 = armara.addStar(26, 13);

        armara.addConnection(sl1, sl2);
        armara.addConnection(sl2, sl3);
        armara.addConnection(sl1, sl3);
        armara.addConnection(sl2, sl4);
        armara.addConnection(sl4, sl5);
        armara.addConnection(sl5, sl6);
        armara.addConnection(sl3, sl6);
        armara.addConnection(sl6, sl7);
        register(armara);

        // PHE/Phoenix
        vicio = new Constellation.Major("vicio", CONSTELLATION_VICIO);
        sl1 = vicio.addStar(13, 11);
        sl2 = vicio.addStar(26, 10);
        sl3 = vicio.addStar(23, 4);
        sl4 = vicio.addStar(4, 6);
        sl5 = vicio.addStar(5, 20);
        sl6 = vicio.addStar(12, 25);

        vicio.addConnection(sl1, sl2);
        vicio.addConnection(sl2, sl3);
        vicio.addConnection(sl1, sl3);
        vicio.addConnection(sl1, sl4);
        vicio.addConnection(sl4, sl5);
        vicio.addConnection(sl5, sl6);
        vicio.addConnection(sl6, sl1);
        register(vicio);

        // ARA/Ara
        aevitas = new Constellation.Major("aevitas", CONSTELLATION_AEVITAS);
        sl1 = aevitas.addStar(13, 4);
        sl2 = aevitas.addStar(15, 18);
        sl3 = aevitas.addStar(10, 27);
        sl4 = aevitas.addStar(27, 24);
        sl5 = aevitas.addStar(24, 19);
        sl6 = aevitas.addStar(25, 10);
        sl7 = aevitas.addStar(3, 6);

        aevitas.addConnection(sl1, sl2);
        aevitas.addConnection(sl2, sl3);
        aevitas.addConnection(sl3, sl4);
        aevitas.addConnection(sl4, sl5);
        aevitas.addConnection(sl5, sl6);
        aevitas.addConnection(sl6, sl1);
        aevitas.addConnection(sl1, sl7);
        register(aevitas);

        // TAU/Taurus
        evorsio = new Constellation.Major("evorsio", CONSTELLATION_EVORSIO);
        sl1 = evorsio.addStar(27, 17);
        sl2 = evorsio.addStar(19, 23);
        sl3 = evorsio.addStar(25, 27);
        sl4 = evorsio.addStar(22, 12);
        sl5 = evorsio.addStar(13, 3);
        sl6 = evorsio.addStar(16, 11);
        sl7 = evorsio.addStar(6, 5);

        evorsio.addConnection(sl1, sl2);
        evorsio.addConnection(sl1, sl3);
        evorsio.addConnection(sl1, sl4);
        evorsio.addConnection(sl4, sl5);
        evorsio.addConnection(sl4, sl6);
        evorsio.addConnection(sl6, sl7);
        register(evorsio);

        // CRB/Corona Borealis
        lucerna = new Constellation.Weak("lucerna", CONSTELLATION_LUCERNA);
        sl1 = lucerna.addStar(19, 4);
        sl2 = lucerna.addStar(25, 14);
        sl3 = lucerna.addStar(22, 22);
        sl4 = lucerna.addStar(15, 25);
        sl5 = lucerna.addStar(8, 23);
        sl6 = lucerna.addStar(4, 12);

        lucerna.addConnection(sl1, sl2);
        lucerna.addConnection(sl2, sl3);
        lucerna.addConnection(sl3, sl4);
        lucerna.addConnection(sl4, sl5);
        lucerna.addConnection(sl5, sl6);
        register(lucerna);

        // LAC/Lacerta
        mineralis = new Constellation.Weak("mineralis", CONSTELLATION_MINERALIS);
        sl1 = mineralis.addStar(17, 2);
        sl2 = mineralis.addStar(19, 10);
        sl3 = mineralis.addStar(13, 7);
        sl4 = mineralis.addStar(15, 15);
        sl5 = mineralis.addStar(22, 19);
        sl6 = mineralis.addStar(11, 25);
        sl7 = mineralis.addStar(18, 28);

        mineralis.addConnection(sl1, sl2);
        mineralis.addConnection(sl1, sl3);
        mineralis.addConnection(sl4, sl2);
        mineralis.addConnection(sl4, sl3);
        mineralis.addConnection(sl4, sl5);
        mineralis.addConnection(sl4, sl6);
        mineralis.addConnection(sl7, sl5);
        mineralis.addConnection(sl7, sl6);
        register(mineralis);

        // HOR/Horologium
        horologium = new Constellation.WeakSpecial("horologium", CONSTELLATION_HOROLOGIUM) {
            @Override
            public boolean doesShowUp(World world, long day) {
                WorldContext ctx = SkyHandler.getContext(world);
                if (ctx != null) {
                    return ctx.getCelestialHandler().isDayOfSolarEclipse();
                }
                return false;
            }

            @Override
            public float getDistribution(World world, long day, boolean showsUp) {
                return showsUp ? 1F : 0.25F;
            }
        };
        sl1 = horologium.addStar(25, 28);
        sl2 = horologium.addStar(20, 22);
        sl3 = horologium.addStar(25, 16);
        sl4 = horologium.addStar(27, 10);
        sl5 = horologium.addStar(23, 6);
        sl6 = horologium.addStar(4, 3);

        horologium.addConnection(sl1, sl2);
        horologium.addConnection(sl2, sl3);
        horologium.addConnection(sl3, sl4);
        horologium.addConnection(sl4, sl5);
        horologium.addConnection(sl5, sl6);
        register(horologium);

        // OCT/Octans
        octans = new Constellation.Weak("octans", CONSTELLATION_OCTANS);
        sl1 = octans.addStar(25, 25);
        sl2 = octans.addStar(17, 5);
        sl3 = octans.addStar(11, 10);
        sl4 = octans.addStar(4, 6);

        octans.addConnection(sl1, sl2);
        octans.addConnection(sl1, sl3);
        octans.addConnection(sl2, sl3);
        octans.addConnection(sl3, sl4);
        register(octans);

        // BOO/Bootes
        bootes = new Constellation.Weak("bootes", CONSTELLATION_BOOTES);
        sl1 = bootes.addStar(9, 22);
        sl2 = bootes.addStar(3, 14);
        sl3 = bootes.addStar(22, 27);
        sl4 = bootes.addStar(16, 5);
        sl5 = bootes.addStar(26, 3);
        sl6 = bootes.addStar(24, 11);

        bootes.addConnection(sl1, sl2);
        bootes.addConnection(sl1, sl3);
        bootes.addConnection(sl1, sl4);
        bootes.addConnection(sl1, sl6);
        bootes.addConnection(sl4, sl5);
        bootes.addConnection(sl5, sl6);
        register(bootes);

        // Fornax itself is boring gameplay-wise. so have another one instead.
        // CRT/Crater
        fornax = new Constellation.Weak("fornax", CONSTELLATION_FORNAX);
        sl1 = fornax.addStar(18, 29);
        sl2 = fornax.addStar(28, 18);
        sl3 = fornax.addStar(21, 13);
        sl4 = fornax.addStar(16, 18);
        sl5 = fornax.addStar(19, 6);
        sl6 = fornax.addStar(13, 2);
        sl7 = fornax.addStar(9, 21);
        sl8 = fornax.addStar(2, 17);

        fornax.addConnection(sl1, sl2);
        fornax.addConnection(sl2, sl3);
        fornax.addConnection(sl3, sl4);
        fornax.addConnection(sl4, sl1);
        fornax.addConnection(sl3, sl5);
        fornax.addConnection(sl5, sl6);
        fornax.addConnection(sl4, sl7);
        fornax.addConnection(sl7, sl8);
        register(fornax);

        // LEP/Lepus
        pelotrio = new Constellation.WeakSpecial("pelotrio", CONSTELLATION_PELOTRIO) {
            @Override
            public boolean doesShowUp(World world, long day) {
                MoonPhase phase = MoonPhase.fromWorld(world);
                return phase == MoonPhase.NEW || phase == MoonPhase.FULL;
            }

            @Override
            public float getDistribution(World world, long day, boolean showingUp) {
                if (showingUp) return 1F;
                MoonPhase current = MoonPhase.fromWorld(world);
                if (current == MoonPhase.WANING_1_2 || current == MoonPhase.WAXING_1_2) {
                    return 0.3F;
                }
                return 0.65F;
            }
        };
        sl1 = pelotrio.addStar(17, 24);
        sl2 = pelotrio.addStar(27, 25);
        sl3 = pelotrio.addStar(22, 8);
        sl4 = pelotrio.addStar(14, 14);
        sl5 = pelotrio.addStar(8, 29);
        sl6 = pelotrio.addStar(3, 8);
        sl7 = pelotrio.addStar(9, 10);

        pelotrio.addConnection(sl1, sl2);
        pelotrio.addConnection(sl2, sl3);
        pelotrio.addConnection(sl3, sl4);
        pelotrio.addConnection(sl4, sl1);
        pelotrio.addConnection(sl1, sl5);
        pelotrio.addConnection(sl5, sl6);
        pelotrio.addConnection(sl6, sl7);
        pelotrio.addConnection(sl7, sl4);
        register(pelotrio);

        // CAP/Capricornus
        gelu = new Constellation.Minor("gelu", CONSTELLATION_GELU, MoonPhase.NEW, MoonPhase.WAXING_1_4, MoonPhase.WAXING_1_2);
        sl1 = gelu.addStar(21, 28);
        sl2 = gelu.addStar(27, 13);
        sl3 = gelu.addStar(29, 4);
        sl4 = gelu.addStar(12, 14);
        sl5 = gelu.addStar(3, 12);
        sl6 = gelu.addStar(9, 21);

        gelu.addConnection(sl1, sl2);
        gelu.addConnection(sl2, sl3);
        gelu.addConnection(sl3, sl4);
        gelu.addConnection(sl4, sl5);
        gelu.addConnection(sl5, sl6);
        gelu.addConnection(sl6, sl1);
        register(gelu);

        // GRU/Grus
        ulteria = new Constellation.Minor("ulteria", CONSTELLATION_ULTERIA, MoonPhase.WANING_1_2, MoonPhase.WANING_3_4, MoonPhase.NEW);
        sl1 = ulteria.addStar(4, 28);
        sl2 = ulteria.addStar(7, 22);
        sl3 = ulteria.addStar(9, 14);
        sl4 = ulteria.addStar(21, 15);
        sl5 = ulteria.addStar(15, 8);
        sl6 = ulteria.addStar(24, 3);

        ulteria.addConnection(sl1, sl2);
        ulteria.addConnection(sl2, sl3);
        ulteria.addConnection(sl3, sl4);
        ulteria.addConnection(sl3, sl5);
        ulteria.addConnection(sl4, sl5);
        ulteria.addConnection(sl5, sl6);
        register(ulteria);

        // CYG/Cygnus
        alcara = new Constellation.Minor("alcara", CONSTELLATION_ALCARA, MoonPhase.WANING_1_2, MoonPhase.WAXING_1_2);
        sl1 = alcara.addStar(24, 27);
        sl2 = alcara.addStar(17, 16);
        sl3 = alcara.addStar(12, 9);
        sl4 = alcara.addStar(23, 9);
        sl5 = alcara.addStar(21, 2);
        sl6 = alcara.addStar(10, 21);
        sl7 = alcara.addStar(3, 20);

        alcara.addConnection(sl1, sl2);
        alcara.addConnection(sl2, sl3);
        alcara.addConnection(sl2, sl4);
        alcara.addConnection(sl4, sl5);
        alcara.addConnection(sl5, sl3);
        alcara.addConnection(sl2, sl6);
        alcara.addConnection(sl6, sl7);
        alcara.addConnection(sl7, sl3);
        register(alcara);

        // COL/Columba
        vorux = new Constellation.Minor("vorux", CONSTELLATION_VORUX, MoonPhase.FULL, MoonPhase.WAXING_3_4, MoonPhase.WANING_3_4);
        sl1 = vorux.addStar(7, 29);
        sl2 = vorux.addStar(15, 12);
        sl3 = vorux.addStar(23, 7);
        sl4 = vorux.addStar(28, 14);
        sl5 = vorux.addStar(3, 6);

        vorux.addConnection(sl1, sl2);
        vorux.addConnection(sl2, sl3);
        vorux.addConnection(sl3, sl4);
        vorux.addConnection(sl2, sl5);
        register(vorux);
    }

    private static void register(IConstellation cst) {
        AstralSorcery.getProxy().getRegistryPrimer().register(cst);
    }
}

