/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.constellation.ConstellationBase;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.constellation.starmap.ConstellationMapEffectRegistry;
import hellfirepvp.astralsorcery.common.event.APIRegistryEvent;
import hellfirepvp.astralsorcery.common.lib.EnchantmentsAS;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

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
        buildConstellations();

        registerConstellations();

        registerEffects();

        MinecraftForge.EVENT_BUS.post(new APIRegistryEvent.ConstellationRegister());
    }

    private static void registerEffects() {
        ConstellationMapEffectRegistry.registerMapEffect(discidia, Enchantments.SHARPNESS, MobEffects.STRENGTH).setMaxEnchLevel(6);
        ConstellationMapEffectRegistry.registerMapEffect(armara, Enchantments.PROTECTION, MobEffects.RESISTANCE).setMaxEnchLevel(5);
        ConstellationMapEffectRegistry.registerMapEffect(vicio, Enchantments.FEATHER_FALLING, MobEffects.SPEED).setMaxEnchLevel(3);
        ConstellationMapEffectRegistry.registerMapEffect(aevitas, Enchantments.MENDING, MobEffects.REGENERATION).setMaxEnchLevel(3);

        ConstellationMapEffectRegistry.registerMapEffect(lucerna, EnchantmentsAS.enchantmentNightVision, MobEffects.NIGHT_VISION).setMaxEnchLevel(1);
        ConstellationMapEffectRegistry.registerMapEffect(mineralis, Enchantments.EFFICIENCY, MobEffects.HASTE).setMaxEnchLevel(6).setMaxPotionAmplifier(3);
        ConstellationMapEffectRegistry.registerMapEffect(horologium, Enchantments.FORTUNE, MobEffects.HASTE).setMinEnchLevel(4).setMaxEnchLevel(6).setMinPotionAmplifier(5).setMaxPotionAmplifier(8);
        ConstellationMapEffectRegistry.registerMapEffect(octans, Enchantments.RESPIRATION, MobEffects.WATER_BREATHING).setMinEnchLevel(2).setMaxEnchLevel(4).setMinPotionAmplifier(2).setMaxPotionAmplifier(4);
        ConstellationMapEffectRegistry.registerMapEffect(bootes, Enchantments.SILK_TOUCH, MobEffects.SATURATION).setMaxEnchLevel(1).setMinPotionAmplifier(3).setMaxPotionAmplifier(8);

        ConstellationMapEffectRegistry.registerMapEffect(fornax, Enchantments.FIRE_ASPECT, MobEffects.FIRE_RESISTANCE).setMaxEnchLevel(3).setMaxPotionAmplifier(0);
        ConstellationMapEffectRegistry.registerMapEffect(gelu, Enchantments.FROST_WALKER, MobEffects.RESISTANCE).setMaxPotionAmplifier(1);
        ConstellationMapEffectRegistry.registerMapEffect(ulteria, Enchantments.UNBREAKING, MobEffects.ABSORPTION).setMaxEnchLevel(4).setMaxPotionAmplifier(4);
        ConstellationMapEffectRegistry.registerMapEffect(alcara, Enchantments.POWER, MobEffects.JUMP_BOOST).setMaxEnchLevel(4).setMaxEnchLevel(6).setMaxPotionAmplifier(1);
    }

    private static void registerConstellations() {
        //Major
        ConstellationRegistry.registerConstellation(discidia);
        ConstellationRegistry.registerConstellation(armara);
        ConstellationRegistry.registerConstellation(vicio);
        ConstellationRegistry.registerConstellation(aevitas);

        //Weak
        ConstellationRegistry.registerConstellation(lucerna);
        ConstellationRegistry.registerConstellation(mineralis);
        ConstellationRegistry.registerConstellation(horologium);
        ConstellationRegistry.registerConstellation(octans);
        ConstellationRegistry.registerConstellation(bootes);

        //Minor
        ConstellationRegistry.registerConstellation(fornax);
        ConstellationRegistry.registerConstellation(gelu);
        ConstellationRegistry.registerConstellation(ulteria);
        ConstellationRegistry.registerConstellation(alcara);
    }

    private static void buildConstellations() {
        StarLocation sl1, sl2, sl3, sl4, sl5, sl6, sl7, sl8, sl9;

        discidia = new ConstellationBase.Major("discidia");
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

        armara = new ConstellationBase.Major("armara");
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

        vicio = new ConstellationBase.Major("vicio");
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

        aevitas = new ConstellationBase.Major("aevitas");
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

        lucerna = new ConstellationBase.Weak("lucerna");
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

        mineralis = new ConstellationBase.Weak("mineralis");
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

        horologium = new ConstellationBase.WeakSpecial("horologium") {
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

        octans = new ConstellationBase.Weak("octans");
        sl1 = octans.addStar(3, 6);
        sl2 = octans.addStar(11, 11);
        sl3 = octans.addStar(18, 4);
        sl4 = octans.addStar(18, 29);

        octans.addConnection(sl1, sl2);
        octans.addConnection(sl2, sl3);
        octans.addConnection(sl3, sl4);
        octans.addConnection(sl2, sl4);

        bootes = new ConstellationBase.Weak("bootes");
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

        fornax = new ConstellationBase.Weak("fornax");
        sl1 = fornax.addStar(3, 20);
        sl2 = fornax.addStar(13, 23);
        sl3 = fornax.addStar(27, 16);
        sl4 = fornax.addStar(10, 13);
        sl5 = fornax.addStar(15, 11);

        fornax.addConnection(sl1, sl2);
        fornax.addConnection(sl2, sl3);
        fornax.addConnection(sl2, sl4);
        fornax.addConnection(sl2, sl5);

        gelu = new ConstellationBase.Minor("gelu");
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

        ulteria = new ConstellationBase.Minor("ulteria");
        sl1 = ulteria.addStar(14, 9);
        sl2 = ulteria.addStar(17, 16);
        sl3 = ulteria.addStar(25, 19);
        sl4 = ulteria.addStar(7, 21);
        sl5 = ulteria.addStar(22, 25);

        ulteria.addConnection(sl1, sl2);
        ulteria.addConnection(sl2, sl3);
        ulteria.addConnection(sl4, sl5);

        alcara = new ConstellationBase.Minor("alcara");
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
    }

}
