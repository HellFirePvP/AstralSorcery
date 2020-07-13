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
import hellfirepvp.astralsorcery.common.constellation.ConstellationBase;
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
import net.minecraftforge.fml.LogicalSide;

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
        StarLocation sl1, sl2, sl3, sl4, sl5, sl6, sl7, sl8, sl9;

        discidia = new ConstellationBase.Major("discidia", CONSTELLATION_DISCIDIA);
        sl1 = discidia.addStar(7, 2);
        sl2 = discidia.addStar(3, 6);
        sl3 = discidia.addStar(5, 12);
        sl4 = discidia.addStar(20, 11);
        sl5 = discidia.addStar(15, 17);
        sl6 = discidia.addStar(26, 21);
        sl7 = discidia.addStar(23, 27);
        sl8 = discidia.addStar(15, 25);

        discidia.addConnection(sl1, sl2);
        discidia.addConnection(sl2, sl3);
        discidia.addConnection(sl2, sl4);
        discidia.addConnection(sl4, sl5);
        discidia.addConnection(sl5, sl7);
        discidia.addConnection(sl6, sl7);
        discidia.addConnection(sl7, sl8);
        register(discidia);

        armara = new ConstellationBase.Major("armara", CONSTELLATION_ARMARA);
        sl1 = armara.addStar(8, 4);
        sl2 = armara.addStar(9, 15);
        sl3 = armara.addStar(11, 26);
        sl4 = armara.addStar(19, 25);
        sl5 = armara.addStar(23, 14);
        sl6 = armara.addStar(23, 4);
        sl7 = armara.addStar(15, 7);

        armara.addConnection(sl1, sl2);
        armara.addConnection(sl2, sl3);
        armara.addConnection(sl3, sl4);
        armara.addConnection(sl4, sl5);
        armara.addConnection(sl5, sl6);
        armara.addConnection(sl6, sl7);
        armara.addConnection(sl7, sl1);
        armara.addConnection(sl2, sl5);
        armara.addConnection(sl2, sl7);
        armara.addConnection(sl5, sl7);
        register(armara);

        vicio = new ConstellationBase.Major("vicio", CONSTELLATION_VICIO);
        sl1 = vicio.addStar(3,  8);
        sl2 = vicio.addStar(13, 9);
        sl3 = vicio.addStar(6,  23);
        sl4 = vicio.addStar(14, 16);
        sl5 = vicio.addStar(23, 24);
        sl6 = vicio.addStar(22, 16);
        sl7 = vicio.addStar(24, 4);

        vicio.addConnection(sl1, sl2);
        vicio.addConnection(sl2, sl7);
        vicio.addConnection(sl3, sl4);
        vicio.addConnection(sl4, sl7);
        vicio.addConnection(sl5, sl6);
        vicio.addConnection(sl6, sl7);
        register(vicio);

        aevitas = new ConstellationBase.Major("aevitas", CONSTELLATION_AEVITAS);
        sl1 = aevitas.addStar(15, 14);
        sl2 = aevitas.addStar(7, 12);
        sl3 = aevitas.addStar(3, 6);
        sl4 = aevitas.addStar(21, 8);
        sl5 = aevitas.addStar(25, 2);
        sl6 = aevitas.addStar(13, 21);
        sl7 = aevitas.addStar(9, 26);
        sl8 = aevitas.addStar(17, 28);
        sl9 = aevitas.addStar(27, 17);

        aevitas.addConnection(sl1, sl2);
        aevitas.addConnection(sl2, sl3);
        aevitas.addConnection(sl1, sl4);
        aevitas.addConnection(sl4, sl5);
        aevitas.addConnection(sl1, sl6);
        aevitas.addConnection(sl6, sl7);
        aevitas.addConnection(sl6, sl8);
        aevitas.addConnection(sl4, sl9);
        register(aevitas);

        evorsio = new ConstellationBase.Major("evorsio", CONSTELLATION_EVORSIO);
        sl1 = evorsio.addStar(13, 16);
        sl2 = evorsio.addStar(18, 6);
        sl3 = evorsio.addStar(26, 4);
        sl4 = evorsio.addStar(24, 13);
        sl5 = evorsio.addStar(2, 18);
        sl6 = evorsio.addStar(4, 27);
        sl7 = evorsio.addStar(11, 24);

        evorsio.addConnection(sl1, sl2);
        evorsio.addConnection(sl1, sl3);
        evorsio.addConnection(sl1, sl4);
        evorsio.addConnection(sl1, sl5);
        evorsio.addConnection(sl1, sl6);
        evorsio.addConnection(sl1, sl7);
        register(evorsio);

        lucerna = new ConstellationBase.Weak("lucerna", CONSTELLATION_LUCERNA);
        sl1 = lucerna.addStar(15, 13);
        sl2 = lucerna.addStar(3, 5);
        sl3 = lucerna.addStar(25, 3);
        sl4 = lucerna.addStar(28, 16);
        sl5 = lucerna.addStar(22, 27);
        sl6 = lucerna.addStar(6, 26);

        lucerna.addConnection(sl1, sl2);
        lucerna.addConnection(sl1, sl3);
        lucerna.addConnection(sl1, sl4);
        lucerna.addConnection(sl1, sl5);
        lucerna.addConnection(sl1, sl6);
        register(lucerna);

        mineralis = new ConstellationBase.Weak("mineralis", CONSTELLATION_MINERALIS);
        sl1 = mineralis.addStar(16, 2);
        sl2 = mineralis.addStar(8, 8);
        sl3 = mineralis.addStar(9, 22);
        sl4 = mineralis.addStar(15, 29);
        sl5 = mineralis.addStar(23, 21);
        sl6 = mineralis.addStar(24, 9);

        mineralis.addConnection(sl1, sl2);
        mineralis.addConnection(sl2, sl3);
        mineralis.addConnection(sl3, sl4);
        mineralis.addConnection(sl4, sl5);
        mineralis.addConnection(sl5, sl6);
        mineralis.addConnection(sl6, sl1);
        mineralis.addConnection(sl1, sl4);
        register(mineralis);

        horologium = new ConstellationBase.WeakSpecial("horologium", CONSTELLATION_HOROLOGIUM) {
            @Override
            public boolean doesShowUp(World world, long day) {
                WorldContext ctx = SkyHandler.getContext(world, LogicalSide.CLIENT);
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
        sl1 = horologium.addStar(7, 6);
        sl2 = horologium.addStar(22, 5);
        sl3 = horologium.addStar(5, 27);
        sl4 = horologium.addStar(23, 25);

        horologium.addConnection(sl1, sl2);
        horologium.addConnection(sl2, sl3);
        horologium.addConnection(sl3, sl4);
        horologium.addConnection(sl4, sl1);
        register(horologium);

        octans = new ConstellationBase.Weak("octans", CONSTELLATION_OCTANS);
        sl1 = octans.addStar(3, 6);
        sl2 = octans.addStar(11, 11);
        sl3 = octans.addStar(18, 4);
        sl4 = octans.addStar(18, 29);

        octans.addConnection(sl1, sl2);
        octans.addConnection(sl2, sl3);
        octans.addConnection(sl3, sl4);
        octans.addConnection(sl2, sl4);
        register(octans);

        bootes = new ConstellationBase.Weak("bootes", CONSTELLATION_BOOTES);
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

        fornax = new ConstellationBase.Weak("fornax", CONSTELLATION_FORNAX);
        sl1 = fornax.addStar(4, 25);
        sl2 = fornax.addStar(14, 28);
        sl3 = fornax.addStar(28, 21);
        sl4 = fornax.addStar(12, 18);
        sl5 = fornax.addStar(16, 16);

        fornax.addConnection(sl1, sl2);
        fornax.addConnection(sl2, sl3);
        fornax.addConnection(sl2, sl4);
        fornax.addConnection(sl2, sl5);
        register(fornax);

        pelotrio = new ConstellationBase.WeakSpecial("pelotrio", CONSTELLATION_PELOTRIO) {
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
        sl1 = pelotrio.addStar(4, 7);
        sl2 = pelotrio.addStar(12, 2);
        sl3 = pelotrio.addStar(20, 3);
        sl4 = pelotrio.addStar(15, 13);
        sl5 = pelotrio.addStar(10, 23);
        sl6 = pelotrio.addStar(26, 11);

        pelotrio.addConnection(sl1, sl2);
        pelotrio.addConnection(sl2, sl4);
        pelotrio.addConnection(sl3, sl4);
        pelotrio.addConnection(sl4, sl5);
        pelotrio.addConnection(sl4, sl6);
        pelotrio.addConnection(sl6, sl3);
        register(pelotrio);

        gelu = new ConstellationBase.Minor("gelu", CONSTELLATION_GELU, MoonPhase.NEW, MoonPhase.WAXING_1_4, MoonPhase.WAXING_1_2);
        sl1 = gelu.addStar(8, 7);
        sl2 = gelu.addStar(28, 8);
        sl3 = gelu.addStar(23, 21);
        sl4 = gelu.addStar(3, 22);
        sl5 = gelu.addStar(17, 17);
        sl6 = gelu.addStar(16, 13);

        gelu.addConnection(sl1, sl2);
        gelu.addConnection(sl3, sl4);
        gelu.addConnection(sl2, sl5);
        gelu.addConnection(sl4, sl6);
        register(gelu);

        ulteria = new ConstellationBase.Minor("ulteria", CONSTELLATION_ULTERIA, MoonPhase.WANING_1_2, MoonPhase.WANING_3_4, MoonPhase.NEW);
        sl1 = ulteria.addStar(14, 9);
        sl2 = ulteria.addStar(17, 16);
        sl3 = ulteria.addStar(25, 19);
        sl4 = ulteria.addStar(7, 21);
        sl5 = ulteria.addStar(22, 25);

        ulteria.addConnection(sl1, sl2);
        ulteria.addConnection(sl2, sl3);
        ulteria.addConnection(sl4, sl5);
        register(ulteria);

        alcara = new ConstellationBase.Minor("alcara", CONSTELLATION_ALCARA, MoonPhase.WANING_1_2, MoonPhase.WAXING_1_2);
        sl1 = alcara.addStar(6, 27);
        sl2 = alcara.addStar(14, 20);
        sl3 = alcara.addStar(17, 24);
        sl4 = alcara.addStar(10, 18);
        sl5 = alcara.addStar(7, 5);
        sl6 = alcara.addStar(17, 9);

        alcara.addConnection(sl1, sl2);
        alcara.addConnection(sl2, sl3);
        alcara.addConnection(sl1, sl4);
        alcara.addConnection(sl4, sl5);
        alcara.addConnection(sl4, sl6);
        register(alcara);

        vorux = new ConstellationBase.Minor("vorux", CONSTELLATION_VORUX, MoonPhase.FULL, MoonPhase.WAXING_3_4, MoonPhase.WANING_3_4);
        sl1 = vorux.addStar(3, 21);
        sl2 = vorux.addStar(7, 7);
        sl3 = vorux.addStar(14, 15);
        sl4 = vorux.addStar(18, 5);
        sl5 = vorux.addStar(25, 16);
        sl6 = vorux.addStar(16, 26);
        sl7 = vorux.addStar(27, 2);

        vorux.addConnection(sl1, sl2);
        vorux.addConnection(sl2, sl3);
        vorux.addConnection(sl3, sl4);
        vorux.addConnection(sl4, sl5);
        vorux.addConnection(sl5, sl6);
        vorux.addConnection(sl4, sl7);
        register(vorux);
    }

    private static void register(IConstellation cst) {
        AstralSorcery.getProxy().getRegistryPrimer().register(cst);
    }
}

