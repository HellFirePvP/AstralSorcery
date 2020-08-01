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
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import net.minecraft.enchantment.EnchantmentType;
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
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.MENDING, 1, 1))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_HEALTH, ModifierType.ADDITION, 1F, 3F)
                        .addApplicableType(EnchantmentType.WEARABLE)
                        .formatResultAsInteger())
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_ATTACK_LIFE_LEECH, ModifierType.ADDITION, 2F, 4F)
                        .addApplicableType(EnchantmentType.WEAPON)
                        .addApplicableType(EnchantmentType.BOW)
                        .addApplicableType(EnchantmentType.CROSSBOW)
                        .addApplicableType(EnchantmentType.TRIDENT)
                        .formatResultAsInteger());
        ARMARA = newEffect(ConstellationsAS.armara)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.RESISTANCE, 0, 0))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.PROTECTION, 2, 5))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_ARMOR, ModifierType.STACKING_MULTIPLY, 1.1F, 1.2F)
                        .addApplicableType(EnchantmentType.WEARABLE))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_INC_DODGE, ModifierType.ADDITION, 5F, 10F)
                        .addApplicableType(EnchantmentType.WEAPON)
                        .addApplicableType(EnchantmentType.BOW)
                        .addApplicableType(EnchantmentType.CROSSBOW)
                        .addApplicableType(EnchantmentType.TRIDENT)
                        .formatResultAsInteger());
        DISCIDIA = newEffect(ConstellationsAS.discidia)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.STRENGTH, 0, 3))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.SHARPNESS, 3, 7))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.POWER, 3, 7))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_INC_CRIT_CHANCE, ModifierType.ADDITION, 3F, 6F)
                        .addApplicableType(EnchantmentType.WEARABLE))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_MELEE_DAMAGE, ModifierType.STACKING_MULTIPLY, 1.05F, 1.2F)
                        .addApplicableType(EnchantmentType.WEAPON))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_PROJ_DAMAGE, ModifierType.STACKING_MULTIPLY, 1.05F, 1.2F)
                        .addApplicableType(EnchantmentType.BOW)
                        .addApplicableType(EnchantmentType.CROSSBOW));
        EVORSIO = newEffect(ConstellationsAS.evorsio)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.HASTE, 1, 3))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.EFFICIENCY, 3, 6))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_INC_HARVEST_SPEED, ModifierType.ADDED_MULTIPLY, 0.1F, 0.25F)
                        .addApplicableType(EnchantmentType.DIGGER));
        VICIO = newEffect(ConstellationsAS.vicio)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.SPEED, 1, 3))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.FEATHER_FALLING, 0, 4))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_ATTACK_SPEED, ModifierType.ADDED_MULTIPLY, 0.15F, 0.25F)
                        .addApplicableType(EnchantmentType.WEAPON)
                        .addApplicableType(EnchantmentType.DIGGER)
                        .addApplicableType(EnchantmentType.BOW)
                        .addApplicableType(EnchantmentType.CROSSBOW)
                        .addApplicableType(EnchantmentType.TRIDENT))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_MOVESPEED, ModifierType.STACKING_MULTIPLY, 1.05F, 1.1F)
                        .addApplicableType(EnchantmentType.ARMOR_FEET));


        BOOTES = newEffect(ConstellationsAS.bootes)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.SATURATION, 1, 5))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.SILK_TOUCH, 1, 1))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_HEALTH, ModifierType.ADDITION, 1F, 2F)
                        .addApplicableType(EnchantmentType.WEARABLE)
                        .formatResultAsInteger())
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_REACH, ModifierType.ADDED_MULTIPLY, 1.05F, 1.1F)
                        .addApplicableType(EnchantmentType.DIGGER)
                        .addApplicableType(EnchantmentType.WEAPON)
                        .addApplicableType(EnchantmentType.TRIDENT));
        FORNAX = newEffect(ConstellationsAS.fornax)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.FIRE_RESISTANCE, 0, 0))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.FIRE_ASPECT, 1, 3))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.FLAME, 1, 2))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> EnchantmentsAS.SCORCHING_HEAT, 1, 1))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_INC_CRIT_CHANCE, ModifierType.ADDED_MULTIPLY, 0.1F, 0.2F)
                        .addApplicableType(EnchantmentType.WEAPON)
                        .addApplicableType(EnchantmentType.BOW)
                        .addApplicableType(EnchantmentType.CROSSBOW)
                        .addApplicableType(EnchantmentType.TRIDENT))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_INC_CRIT_MULTIPLIER, ModifierType.ADDED_MULTIPLY, 0.1F, 0.2F)
                        .addApplicableType(EnchantmentType.WEARABLE));
        HOROLOGIUM = newEffect(ConstellationsAS.horologium)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.LUCK, 3, 5))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.SPEED, 1, 2))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.HASTE, 2, 3))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.FORTUNE, 4, 6))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.LOOTING, 3, 6))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EXP, ModifierType.STACKING_MULTIPLY, 1.2F, 1.35F)
                        .addApplicableType(EnchantmentType.ARMOR_HEAD))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT, ModifierType.STACKING_MULTIPLY, 1.1F, 1.2F)
                        .addApplicableType(EnchantmentType.ARMOR_FEET)
                        .addApplicableType(EnchantmentType.ARMOR_CHEST)
                        .addApplicableType(EnchantmentType.ARMOR_LEGS));
        LUCERNA = newEffect(ConstellationsAS.lucerna)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.NIGHT_VISION, 0, 0))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> EnchantmentsAS.NIGHT_VISION, 1, 1))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM, ModifierType.ADDED_MULTIPLY, 0.1F, 0.2F)
                        .addApplicableType(EnchantmentType.WEARABLE))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION, ModifierType.STACKING_MULTIPLY, 1.1F, 1.2F)
                        .addApplicableType(EnchantmentType.DIGGER)
                        .addApplicableType(EnchantmentType.WEAPON)
                        .addApplicableType(EnchantmentType.BOW)
                        .addApplicableType(EnchantmentType.CROSSBOW)
                        .addApplicableType(EnchantmentType.TRIDENT));
        MINERALIS = newEffect(ConstellationsAS.mineralis)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.HASTE, 0, 2))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.FORTUNE, 1, 3))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_INC_HARVEST_SPEED, ModifierType.ADDED_MULTIPLY, 0.1F, 0.2F)
                        .addApplicableType(EnchantmentType.WEARABLE))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_MINING_SIZE, ModifierType.ADDITION, 1F, 1F)
                        .addApplicableType(EnchantmentType.DIGGER)
                        .formatResultAsInteger());
        OCTANS = newEffect(ConstellationsAS.octans)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.WATER_BREATHING, 1, 3))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.RESPIRATION, 2, 4))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_SWIMSPEED, ModifierType.ADDED_MULTIPLY, 0.05F, 0.08F)
                        .addApplicableType(EnchantmentType.WEARABLE));
        PELOTRIO = newEffect(ConstellationsAS.pelotrio)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.ABSORPTION, 1, 4))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.REGENERATION, 2, 4))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.LURE, 3, 5))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.INFINITY, 1, 1))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_POTION_DURATION, ModifierType.ADDED_MULTIPLY, 0.15F, 0.2F)
                        .addApplicableType(EnchantmentType.WEARABLE))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_INC_DODGE, ModifierType.ADDED_MULTIPLY, 0.05F, 0.15F)
                        .addApplicableType(EnchantmentType.WEARABLE));

        ALCARA = newEffect(ConstellationsAS.alcara)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.LUCK, 3, 6))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.INVISIBILITY, 0, 1))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.SPEED, 1, 2))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.SWEEPING, 3, 6))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.LURE, 2, 5).setIgnoreCompatibility())
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.LUCK_OF_THE_SEA, 3, 6).setIgnoreCompatibility())
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.SILK_TOUCH, 1, 1))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_RAMPAGE_DURATION, ModifierType.STACKING_MULTIPLY, 1.15F, 1.3F)
                        .addApplicableType(EnchantmentType.WEARABLE))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_BLEED_CHANCE, ModifierType.STACKING_MULTIPLY, 1.2F, 1.4F)
                        .addApplicableType(EnchantmentType.WEAPON)
                        .addApplicableType(EnchantmentType.TRIDENT))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_PROJ_SPEED, ModifierType.STACKING_MULTIPLY, 1.25F, 1.5F)
                        .addApplicableType(EnchantmentType.BOW)
                        .addApplicableType(EnchantmentType.CROSSBOW));
        GELU = newEffect(ConstellationsAS.gelu)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.RESISTANCE, 1, 2))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.FIRE_RESISTANCE, 0, 0))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.SLOWNESS, 0, 1))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.FROST_WALKER, 1, 1))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.FEATHER_FALLING, 1, 1))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.UNBREAKING, 2, 4))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_INC_ENCH_EFFECT, ModifierType.ADDED_MULTIPLY, 0.02F, 0.05F)
                        .addApplicableType(EnchantmentType.WEARABLE))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_REACH, ModifierType.ADDITION, 1F, 2F)
                        .addApplicableType(EnchantmentType.DIGGER)
                        .formatResultAsInteger())
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_INC_CRIT_MULTIPLIER, ModifierType.ADDED_MULTIPLY, 0.2F, 0.35F)
                        .addApplicableType(EnchantmentType.WEAPON)
                        .addApplicableType(EnchantmentType.TRIDENT)
                        .addApplicableType(EnchantmentType.BOW)
                        .addApplicableType(EnchantmentType.CROSSBOW));
        ULTERIA = newEffect(ConstellationsAS.ulteria)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.ABSORPTION, 0, 4))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.REGENERATION, 1, 3))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.WEAKNESS, 1, 2))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.UNBREAKING, 2, 3).setIgnoreCompatibility())
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.FIRE_PROTECTION, 4, 6).setIgnoreCompatibility())
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.BLAST_PROTECTION, 4, 6).setIgnoreCompatibility())
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.PROJECTILE_PROTECTION, 4, 6).setIgnoreCompatibility())
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST, ModifierType.ADDED_MULTIPLY, 0.05F, 0.1F)
                        .addApplicableType(EnchantmentType.WEARABLE))
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_INC_THORNS, ModifierType.ADDITION, 10F, 15F)
                        .addApplicableType(EnchantmentType.WEAPON)
                        .addApplicableType(EnchantmentType.TRIDENT)
                        .formatResultAsInteger())
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_INC_DODGE, ModifierType.ADDITION, 10F, 15F)
                        .addApplicableType(EnchantmentType.BOW)
                        .addApplicableType(EnchantmentType.CROSSBOW)
                        .formatResultAsInteger());
        VORUX = newEffect(ConstellationsAS.vorux)
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.STRENGTH, 2, 3))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.RESISTANCE, 0, 1))
                .addEffect(new EngravingEffect.PotionEffect(() -> Effects.MINING_FATIGUE, 1, 3))
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.SMITE, 4, 7).setIgnoreCompatibility())
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.BANE_OF_ARTHROPODS, 4, 7).setIgnoreCompatibility())
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.SHARPNESS, 3, 4).setIgnoreCompatibility())
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.POWER, 3, 4).setIgnoreCompatibility())
                .addEffect(new EngravingEffect.EnchantmentEffect(() -> Enchantments.CHANNELING, 3, 4).setIgnoreCompatibility())
                .addEffect(new EngravingEffect.ModifierEffect(() -> PerkAttributeTypesAS.ATTR_TYPE_MOVESPEED, ModifierType.ADDED_MULTIPLY, 0.05F, 0.1F)
                        .addApplicableType(EnchantmentType.WEARABLE));
    }

    private static EngravingEffect newEffect(IConstellation cst) {
        return register(new EngravingEffect(cst));
    }

    private static <T extends EngravingEffect> T register(T effect) {
        AstralSorcery.getProxy().getRegistryPrimer().register(effect);
        return effect;
    }

}
