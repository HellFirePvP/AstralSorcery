/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.engraving.EngravingEffect;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.EnchantmentsAS;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.potion.Effects;

import static hellfirepvp.astralsorcery.common.lib.EngravingEffectsAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryEngravingEffects
 * Created by HellFirePvP
 * Date: 01.05.2020 / 12:30
 */
public class RegistryEngravingEffects {

    private RegistryEngravingEffects() {}

    public static void init() {
        AEVITAS = newEffect(ConstellationsAS.aevitas)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.REGENERATION, 0, 3))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.MENDING, 1, 1));
        ARMARA = newEffect(ConstellationsAS.armara)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.RESISTANCE, 0, 0))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.PROTECTION, 2, 5));
        DISCIDIA = newEffect(ConstellationsAS.discidia)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.STRENGTH, 0, 3))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.SHARPNESS, 3, 7))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.POWER, 3, 7));
        EVORSIO = newEffect(ConstellationsAS.evorsio)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.HASTE, 1, 3))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.EFFICIENCY, 3, 6));
        VICIO = newEffect(ConstellationsAS.vicio)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.SPEED, 1, 3))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.FEATHER_FALLING, 0, 4));


        BOOTES = newEffect(ConstellationsAS.bootes)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.SATURATION, 1, 5))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.SILK_TOUCH, 1, 1));
        FORNAX = newEffect(ConstellationsAS.fornax)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.FIRE_RESISTANCE, 0, 0))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.FIRE_ASPECT, 1, 3))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.FLAME, 1, 2))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> EnchantmentsAS.SCORCHING_HEAT, 1, 1));
        HOROLOGIUM = newEffect(ConstellationsAS.horologium)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.LUCK, 3, 5))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.SPEED, 1, 2))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.HASTE, 2, 3))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.FORTUNE, 4, 6))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.LOOTING, 3, 6));
        LUCERNA = newEffect(ConstellationsAS.lucerna)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.NIGHT_VISION, 0, 0))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> EnchantmentsAS.NIGHT_VISION, 1, 1));
        MINERALIS = newEffect(ConstellationsAS.mineralis)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.HASTE, 0, 2))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.FORTUNE, 1, 3));
        OCTANS = newEffect(ConstellationsAS.octans)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.WATER_BREATHING, 1, 3))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.RESPIRATION, 2, 4));
        PELOTRIO = newEffect(ConstellationsAS.pelotrio)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.ABSORPTION, 1, 4))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.REGENERATION, 2, 4))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.LURE, 3, 5))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.INFINITY, 1, 1));

        ALCARA = newEffect(ConstellationsAS.alcara)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.LUCK, 3, 6))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.INVISIBILITY, 0, 1))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.SPEED, 1, 2))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.SWEEPING, 3, 6))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.LURE, 2, 5).setIgnoreCompatibility())
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.LUCK_OF_THE_SEA, 3, 6).setIgnoreCompatibility())
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.SILK_TOUCH, 1, 1));
        GELU = newEffect(ConstellationsAS.gelu)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.RESISTANCE, 1, 2))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.FIRE_RESISTANCE, 0, 0))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.SLOWNESS, 0, 1))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.FROST_WALKER, 1, 1))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.FEATHER_FALLING, 1, 1))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.UNBREAKING, 2, 4));
        ULTERIA = newEffect(ConstellationsAS.ulteria)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.ABSORPTION, 0, 4))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.REGENERATION, 1, 3))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.WEAKNESS, 1, 2))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.UNBREAKING, 2, 3).setIgnoreCompatibility())
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.FIRE_PROTECTION, 4, 6).setIgnoreCompatibility())
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.BLAST_PROTECTION, 4, 6).setIgnoreCompatibility())
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.PROJECTILE_PROTECTION, 4, 6).setIgnoreCompatibility());
        VORUX = newEffect(ConstellationsAS.vorux)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.STRENGTH, 2, 3))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.RESISTANCE, 0, 1))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.MINING_FATIGUE, 1, 3))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.SMITE, 4, 7).setIgnoreCompatibility())
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.BANE_OF_ARTHROPODS, 4, 7).setIgnoreCompatibility())
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.SHARPNESS, 3, 4).setIgnoreCompatibility())
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.POWER, 3, 4).setIgnoreCompatibility());
    }

    private static EngravingEffect newEffect(IConstellation cst) {
        return register(new EngravingEffect(cst));
    }

    private static <T extends EngravingEffect> T register(T effect) {
        AstralSorcery.getProxy().getRegistryPrimer().register(effect);
        return effect;
    }

}
