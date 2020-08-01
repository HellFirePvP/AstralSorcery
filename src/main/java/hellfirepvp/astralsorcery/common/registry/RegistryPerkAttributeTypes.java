/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeLimiter;
import hellfirepvp.astralsorcery.common.perk.type.*;

import static hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryPerkAttributeTypes
 * Created by HellFirePvP
 * Date: 08.08.2019 / 16:58
 */
public class RegistryPerkAttributeTypes {

    private RegistryPerkAttributeTypes() {}

    public static void init() {
        ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST = register(new AttributeTypeAllElementalResist());
        ATTR_TYPE_ARMOR = register(new AttributeTypeArmor());
        ATTR_TYPE_ARMOR_TOUGHNESS = register(new AttributeTypeArmorToughness());
        ATTR_TYPE_PROJ_SPEED = register(new AttributeTypeArrowSpeed());
        ATTR_TYPE_ATTACK_SPEED = register(new AttributeTypeAttackSpeed());
        ATTR_TYPE_INC_HARVEST_SPEED = register(new AttributeTypeBreakSpeed());
        ATTR_TYPE_INC_CRIT_CHANCE = register(new AttributeTypeCritChance());
        ATTR_TYPE_INC_CRIT_MULTIPLIER = register(new AttributeTypeCritMultiplier());
        ATTR_TYPE_INC_DODGE = register(new AttributeTypeDodge());
        ATTR_TYPE_INC_ENCH_EFFECT = register(new AttributeTypeEnchantmentEffectiveness());
        ATTR_TYPE_ATTACK_LIFE_LEECH = register(new AttributeTypeLifeLeech());
        ATTR_TYPE_LIFE_RECOVERY = register(new AttributeTypeLifeRecovery());
        ATTR_TYPE_HEALTH = register(new AttributeTypeMaxHealth());
        ATTR_TYPE_REACH = register(new AttributeTypeMaxReach());
        ATTR_TYPE_MELEE_DAMAGE = register(new AttributeTypeMeleeAttackDamage());
        ATTR_TYPE_MOVESPEED = register(new AttributeTypeMovementSpeed());
        ATTR_TYPE_INC_PERK_EFFECT = register(new AttributeTypePerkEffect());
        ATTR_TYPE_POTION_DURATION = register(new AttributeTypePotionDuration());
        ATTR_TYPE_PROJ_DAMAGE = register(new AttributeTypeProjectileAttackDamage());
        ATTR_TYPE_SWIMSPEED = register(new AttributeTypeSwimSpeed());
        ATTR_TYPE_INC_THORNS = register(new AttributeTypeThorns());
        ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM = register(new AttributeTypeChargeMaximum());
        ATTR_TYPE_MINING_SIZE = register(new AttributeTypeMiningSize());
        ATTR_TYPE_COOLDOWN_REDUCTION = register(new AttributeTypeCooldown());
        ATTR_TYPE_LUCK = register(new AttributeTypeLuck());

        ATTR_TYPE_BLEED_DURATION = register(PerkAttributeType.makeDefault(KEY_ATTR_TYPE_BLEED_DURATION, false));
        ATTR_TYPE_BLEED_CHANCE = register(PerkAttributeType.makeDefault(KEY_ATTR_TYPE_BLEED_CHANCE, false));
        ATTR_TYPE_BLEED_STACKS = register(PerkAttributeType.makeDefault(KEY_ATTR_TYPE_BLEED_STACKS, false));
        ATTR_TYPE_RAMPAGE_DURATION = register(PerkAttributeType.makeDefault(KEY_ATTR_TYPE_RAMPAGE_DURATION, false));
        ATTR_TYPE_INC_THORNS_RANGED = register(PerkAttributeType.makeDefault(KEY_ATTR_TYPE_INC_THORNS_RANGED, false));
        ATTR_TYPE_ARC_CHAINS = register(PerkAttributeType.makeDefault(KEY_ATTR_TYPE_ARC_CHAINS, false));
        ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION = register(PerkAttributeType.makeDefault(KEY_ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION, true));

        ATTR_TYPE_INC_PERK_EXP = register(PerkAttributeType.makeDefault(KEY_ATTR_TYPE_INC_PERK_EXP, true));

        PerkAttributeLimiter.limit(ATTR_TYPE_INC_DODGE, () -> 0.0, () -> 0.75);
        PerkAttributeLimiter.limit(ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST, () -> 0.0, () -> 0.6);
        PerkAttributeLimiter.limit(ATTR_TYPE_ATTACK_LIFE_LEECH, () -> 0.0, () -> 0.2);
        PerkAttributeLimiter.limit(ATTR_TYPE_COOLDOWN_REDUCTION, () -> 0.0, () -> 0.8);
    }

    private static <T extends PerkAttributeType> T register(T type) {
        AstralSorcery.getProxy().getRegistryPrimer().register(type);
        return type;
    }

}
