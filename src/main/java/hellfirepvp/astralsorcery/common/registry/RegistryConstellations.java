/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.constellation.ConstellationBase;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.*;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.event.APIRegistryEvent;
import hellfirepvp.astralsorcery.common.lib.EnchantmentsAS;
import hellfirepvp.astralsorcery.common.util.OreDictAlias;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.awt.*;
import java.util.Arrays;

import static hellfirepvp.astralsorcery.common.constellation.starmap.ConstellationMapEffectRegistry.*;
import static hellfirepvp.astralsorcery.common.lib.Constellations.*;
import static hellfirepvp.astralsorcery.common.constellation.cape.CapeEffectRegistry.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryConstellations
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:40
 */
public class RegistryConstellations {

    public static void init() {
        buildConstellations();

        registerConstellations();

        MinecraftForge.EVENT_BUS.post(new APIRegistryEvent.ConstellationRegister());
    }

    public static void initMapEffects() {
        registerMapEffects();

        //registerSpellEffects();

        registerCapeEffects();
    }

    public static void initConstellationSignatures() {
        registerSignatureItems();
    }

    private static void registerCapeEffects() {
        registerCapeArmorEffect(discidia, CapeEffectDiscidia.class);
        registerCapeArmorEffect(aevitas, CapeEffectAevitas.class);
        registerCapeArmorEffect(evorsio, CapeEffectEvorsio.class);
        registerCapeArmorEffect(armara, CapeEffectArmara.class);
        registerCapeArmorEffect(vicio, CapeEffectVicio.class);

        registerCapeArmorEffect(lucerna, CapeEffectLucerna.class);
        registerCapeArmorEffect(fornax, CapeEffectFornax.class);
        registerCapeArmorEffect(mineralis, CapeEffectMineralis.class);
        registerCapeArmorEffect(pelotrio, CapeEffectPelotrio.class);
        registerCapeArmorEffect(octans, CapeEffectOctans.class);
        registerCapeArmorEffect(horologium, CapeEffectHorologium.class);
        registerCapeArmorEffect(bootes, CapeEffectBootes.class);
    }

    //private static void registerSpellEffects() {
    //    registerControllerEffect(discidia, EffectControllerDiscidia::new);
    //    registerControllerEffect(aevitas,  EffectControllerAevitas::new);
    //}

    private static void registerMapEffects() {
        registerMapEffect(discidia,
                Arrays.asList(
                        new EnchantmentMapEffect(Enchantments.SHARPNESS, 3, 7),
                        new EnchantmentMapEffect(Enchantments.POWER, 3, 7)),
                Arrays.asList(new PotionMapEffect(MobEffects.STRENGTH, 0, 3)));
        registerMapEffect(armara,
                Arrays.asList(new EnchantmentMapEffect(Enchantments.PROTECTION, 3, 5)),
                Arrays.asList(new PotionMapEffect(MobEffects.RESISTANCE)));
        registerMapEffect(vicio,
                Arrays.asList(new EnchantmentMapEffect(Enchantments.FEATHER_FALLING, 3, 5)),
                Arrays.asList(new PotionMapEffect(MobEffects.SPEED, 1, 3)));
        registerMapEffect(aevitas,
                Arrays.asList(new EnchantmentMapEffect(Enchantments.MENDING, 1, 3)),
                Arrays.asList(new PotionMapEffect(MobEffects.REGENERATION, 0, 3)));
        registerMapEffect(evorsio,
                Arrays.asList(new EnchantmentMapEffect(Enchantments.EFFICIENCY, 3, 5)),
                Arrays.asList(new PotionMapEffect(MobEffects.HASTE, 1, 3)));

        registerMapEffect(lucerna,
                Arrays.asList(new EnchantmentMapEffect(EnchantmentsAS.enchantmentNightVision, 1, 1)),
                Arrays.asList(new PotionMapEffect(MobEffects.NIGHT_VISION)));
        registerMapEffect(mineralis,
                Arrays.asList(new EnchantmentMapEffect(Enchantments.FORTUNE, 1, 3)),
                Arrays.asList(new PotionMapEffect(MobEffects.HASTE, 0, 3)));
        registerMapEffect(horologium,
                Arrays.asList(
                        new EnchantmentMapEffect(Enchantments.FORTUNE, 4, 6),
                        new EnchantmentMapEffect(Enchantments.LOOTING, 3, 5)),
                Arrays.asList(
                        new PotionMapEffect(MobEffects.HASTE, 5, 8),
                        new PotionMapEffect(MobEffects.SPEED, 1, 4)));
        registerMapEffect(octans,
                Arrays.asList(new EnchantmentMapEffect(Enchantments.RESPIRATION, 2, 4)),
                Arrays.asList(new PotionMapEffect(MobEffects.WATER_BREATHING, 2, 4)));
        registerMapEffect(bootes,
                Arrays.asList(new EnchantmentMapEffect(Enchantments.SILK_TOUCH, 1, 1)),
                Arrays.asList(new PotionMapEffect(MobEffects.SATURATION, 2, 5)));
        registerMapEffect(fornax,
                Arrays.asList(
                        new EnchantmentMapEffect(Enchantments.FIRE_ASPECT, 1, 3),
                        new EnchantmentMapEffect(Enchantments.FLAME, 1, 2),
                        new EnchantmentMapEffect(EnchantmentsAS.enchantmentScorchingHeat, 1, 1)),
                Arrays.asList(new PotionMapEffect(MobEffects.FIRE_RESISTANCE, 0, 0)));
        registerMapEffect(pelotrio,
                Arrays.asList(
                        new EnchantmentMapEffect(Enchantments.MENDING, 2, 3),
                        new EnchantmentMapEffect(Enchantments.INFINITY, 1, 1),
                        new EnchantmentMapEffect(Enchantments.LURE, 4, 6)),
                Arrays.asList(
                        new PotionMapEffect(MobEffects.REGENERATION, 2, 4),
                        new PotionMapEffect(MobEffects.ABSORPTION, 1, 4)));

        registerMapEffect(gelu,
                Arrays.asList(
                        new EnchantmentMapEffect(Enchantments.FROST_WALKER),
                        new EnchantmentMapEffect(Enchantments.FEATHER_FALLING),
                        new EnchantmentMapEffect(Enchantments.UNBREAKING, 1, 4)
                        ),
                Arrays.asList(
                        new PotionMapEffect(MobEffects.RESISTANCE, 1, 2),
                        new PotionMapEffect(MobEffects.FIRE_RESISTANCE, 0, 0),
                        new PotionMapEffect(MobEffects.SLOWNESS, 0, 0)));
        registerMapEffect(ulteria,
                Arrays.asList(
                        new EnchantmentMapEffect(Enchantments.UNBREAKING, 2, 4).setIgnoreCompaibility(),
                        new EnchantmentMapEffect(Enchantments.FIRE_PROTECTION, 4, 6).setIgnoreCompaibility(),
                        new EnchantmentMapEffect(Enchantments.BLAST_PROTECTION, 4, 6).setIgnoreCompaibility(),
                        new EnchantmentMapEffect(Enchantments.PROJECTILE_PROTECTION, 4, 6).setIgnoreCompaibility()),
                Arrays.asList(
                        new PotionMapEffect(MobEffects.ABSORPTION, 0, 3),
                        new PotionMapEffect(MobEffects.REGENERATION, 1, 2),
                        new PotionMapEffect(MobEffects.WEAKNESS, 1, 2)));
        registerMapEffect(alcara,
                Arrays.asList(
                        new EnchantmentMapEffect(Enchantments.SWEEPING, 3, 6),
                        new EnchantmentMapEffect(Enchantments.LURE, 2, 5).setIgnoreCompaibility(),
                        new EnchantmentMapEffect(Enchantments.LUCK_OF_THE_SEA, 3, 6).setIgnoreCompaibility(),
                        new EnchantmentMapEffect(Enchantments.SILK_TOUCH, 1, 1)),
                Arrays.asList(
                        new PotionMapEffect(MobEffects.LUCK, 2, 4),
                        new PotionMapEffect(MobEffects.INVISIBILITY, 0, 1),
                        new PotionMapEffect(MobEffects.HUNGER, 0, 1)));
        registerMapEffect(vorux,
                Arrays.asList(
                        new EnchantmentMapEffect(Enchantments.SMITE, 4, 6).setIgnoreCompaibility(),
                        new EnchantmentMapEffect(Enchantments.BANE_OF_ARTHROPODS, 4, 6).setIgnoreCompaibility(),
                        new EnchantmentMapEffect(Enchantments.SHARPNESS, 3, 5).setIgnoreCompaibility(),
                        new EnchantmentMapEffect(Enchantments.POWER, 3, 5).setIgnoreCompaibility()),
                Arrays.asList(
                        new PotionMapEffect(MobEffects.STRENGTH, 2, 3),
                        new PotionMapEffect(MobEffects.RESISTANCE, 0, 1),
                        new PotionMapEffect(MobEffects.MINING_FATIGUE, 0, 1)));
    }

    private static void registerConstellations() {
        //Major
        ConstellationRegistry.registerConstellation(discidia);
        ConstellationRegistry.registerConstellation(armara);
        ConstellationRegistry.registerConstellation(vicio);
        ConstellationRegistry.registerConstellation(aevitas);
        ConstellationRegistry.registerConstellation(evorsio);

        //Weak
        ConstellationRegistry.registerConstellation(lucerna);
        ConstellationRegistry.registerConstellation(mineralis);
        ConstellationRegistry.registerConstellation(horologium);
        ConstellationRegistry.registerConstellation(octans);
        ConstellationRegistry.registerConstellation(bootes);
        ConstellationRegistry.registerConstellation(fornax);
        ConstellationRegistry.registerConstellation(pelotrio);

        //Minor
        ConstellationRegistry.registerConstellation(gelu);
        ConstellationRegistry.registerConstellation(ulteria);
        ConstellationRegistry.registerConstellation(alcara);
        ConstellationRegistry.registerConstellation(vorux);
    }

    private static void registerSignatureItems() {
        discidia.addSignatureItem(new ItemHandle(new ItemStack(Items.FLINT)));
        discidia.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_IRON_INGOT));
        discidia.addSignatureItem(new ItemHandle(new ItemStack(Items.ARROW)));
        discidia.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_REDSTONE_DUST));

        armara.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_IRON_INGOT));
        armara.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_LEATHER));
        armara.addSignatureItem(new ItemHandle(new ItemStack(Items.CLAY_BALL)));
        armara.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_GLOWSTONE_DUST));

        vicio.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_FEATHER));
        vicio.addSignatureItem(new ItemHandle(new ItemStack(Items.SUGAR)));
        vicio.addSignatureItem(new ItemHandle(new ItemStack(Items.STRING)));
        vicio.addSignatureItem(new ItemHandle(new ItemStack(Items.FISH)));

        aevitas.addSignatureItem(new ItemHandle(OreDictAlias.BLOCK_SAPLING));
        aevitas.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_STARMETAL_DUST));
        aevitas.addSignatureItem(new ItemHandle(new ItemStack(Items.WHEAT_SEEDS)));
        aevitas.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_SUGAR_CANE));

        evorsio.addSignatureItem(new ItemHandle(OreDictAlias.BLOCK_COBBLESTONE));
        evorsio.addSignatureItem(new ItemHandle(new ItemStack(Items.FLINT)));
        evorsio.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_GUNPOWDER));
        evorsio.addSignatureItem(new ItemHandle(new ItemStack(Blocks.TNT)));


        lucerna.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_GLOWSTONE_DUST));
        lucerna.addSignatureItem(new ItemHandle(new ItemStack(Blocks.TORCH)));
        lucerna.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_REDSTONE_DUST));
        lucerna.addSignatureItem(new ItemHandle(new ItemStack(Items.COAL)));

        mineralis.addSignatureItem(new ItemHandle(OreDictAlias.BLOCK_IRON_ORE));
        mineralis.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_GOLD_INGOT));
        mineralis.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_IRON_INGOT));
        mineralis.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_REDSTONE_DUST));

        horologium.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_STARMETAL_DUST));
        horologium.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_DIAMOND));
        horologium.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_ENDERPEARL));
        horologium.addSignatureItem(new ItemHandle(new ItemStack(Items.CLOCK)));

        octans.addSignatureItem(new ItemHandle(new ItemStack(Items.FISH)));
        octans.addSignatureItem(new ItemHandle(new ItemStack(Items.FISHING_ROD)));
        octans.addSignatureItem(new ItemHandle(OreDictAlias.getDyeOreDict(EnumDyeColor.BLUE)));
        octans.addSignatureItem(new ItemHandle(new ItemStack(Items.CLAY_BALL)));

        bootes.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_WHEAT));
        bootes.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_BONE));
        bootes.addSignatureItem(new ItemHandle(new ItemStack(Items.APPLE)));
        bootes.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_LEATHER));

        fornax.addSignatureItem(new ItemHandle(new ItemStack(Items.COAL)));
        fornax.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_REDSTONE_DUST));
        fornax.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_IRON_INGOT));
        fornax.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_GUNPOWDER));

        pelotrio.addSignatureItem(new ItemHandle(new ItemStack(Items.BLAZE_POWDER)));
        pelotrio.addSignatureItem(new ItemHandle(new ItemStack(Items.APPLE)));
        pelotrio.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_EGG));
        pelotrio.addSignatureItem(new ItemHandle(new ItemStack(Items.ROTTEN_FLESH)));
    }

    private static void buildConstellations() {
        StarLocation sl1, sl2, sl3, sl4, sl5, sl6, sl7, sl8, sl9;

        discidia = new ConstellationBase.Major("discidia", new Color(0xE01903));
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

        armara = new ConstellationBase.Major("armara", new Color(0xB7BBB8));
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

        vicio = new ConstellationBase.Major("vicio", new Color(0x00BDAD));
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

        aevitas = new ConstellationBase.Major("aevitas", new Color(0x29F615));
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

        evorsio = new ConstellationBase.Major("evorsio", new Color(0xA00100));
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

        lucerna = new ConstellationBase.Weak("lucerna", new Color(0xFFE709));
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

        mineralis = new ConstellationBase.Weak("mineralis", new Color(0xCB7D0A));
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

        horologium = new ConstellationBase.WeakSpecial("horologium", new Color(0x7D16B4)) {
            @Override
            public boolean doesShowUp(WorldSkyHandler handle, World world, long day) {
                return isDayOfSolarEclipse(day);
            }

            @Override
            public float getDistribution(WorldSkyHandler handle, World world, long day, boolean showsUp) {
                return showsUp ? 1F : 0.6F;
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

        octans = new ConstellationBase.Weak("octans", new Color(0x706EFF));
        sl1 = octans.addStar(3, 6);
        sl2 = octans.addStar(11, 11);
        sl3 = octans.addStar(18, 4);
        sl4 = octans.addStar(18, 29);

        octans.addConnection(sl1, sl2);
        octans.addConnection(sl2, sl3);
        octans.addConnection(sl3, sl4);
        octans.addConnection(sl2, sl4);

        bootes = new ConstellationBase.Weak("bootes", new Color(0xD41CD6));
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

        fornax = new ConstellationBase.Weak("fornax", new Color(0xFF4E1B));
        sl1 = fornax.addStar(4, 25);
        sl2 = fornax.addStar(14, 28);
        sl3 = fornax.addStar(28, 21);
        sl4 = fornax.addStar(12, 18);
        sl5 = fornax.addStar(16, 16);

        fornax.addConnection(sl1, sl2);
        fornax.addConnection(sl2, sl3);
        fornax.addConnection(sl2, sl4);
        fornax.addConnection(sl2, sl5);

        pelotrio = new ConstellationBase.WeakSpecial("pelotrio", new Color(0xEC006B)) {
            @Override
            public boolean doesShowUp(WorldSkyHandler handle, World world, long day) {
                return handle.getCurrentMoonPhase() == MoonPhase.NEW || handle.getCurrentMoonPhase() == MoonPhase.FULL;
            }

            @Override
            public float getDistribution(WorldSkyHandler handle, World world, long day, boolean showingUp) {
                if(showingUp) return 1F;
                MoonPhase current = handle.getCurrentMoonPhase();
                if(current == MoonPhase.WANING1_2 || current == MoonPhase.WAXING1_2) {
                    return 0.5F;
                }
                return 0.75F;
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

        gelu = new ConstellationBase.Minor("gelu", new Color(0x758BA8));
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

        ulteria = new ConstellationBase.Minor("ulteria", new Color(0x347463));
        sl1 = ulteria.addStar(14, 9);
        sl2 = ulteria.addStar(17, 16);
        sl3 = ulteria.addStar(25, 19);
        sl4 = ulteria.addStar(7, 21);
        sl5 = ulteria.addStar(22, 25);

        ulteria.addConnection(sl1, sl2);
        ulteria.addConnection(sl2, sl3);
        ulteria.addConnection(sl4, sl5);

        alcara = new ConstellationBase.Minor("alcara", new Color(0x802952));
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

        vorux = new ConstellationBase.Minor("vorux", new Color(0xA8881E));
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
    }

}
