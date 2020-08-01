/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.perk.reader.*;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;

import static hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryPerkAttributeReaders
 * Created by HellFirePvP
 * Date: 25.08.2019 / 17:18
 */
public class RegistryPerkAttributeReaders {

    private RegistryPerkAttributeReaders() {}

    public static void init() {
        register(new ReaderVanillaAttribute(ATTR_TYPE_MELEE_DAMAGE, SharedMonsterAttributes.ATTACK_DAMAGE).formatAsDecimal());
        register(new ReaderVanillaAttribute(ATTR_TYPE_HEALTH, SharedMonsterAttributes.MAX_HEALTH));
        register(new ReaderVanillaAttribute(ATTR_TYPE_MOVESPEED, SharedMonsterAttributes.MOVEMENT_SPEED).formatAsDecimal());
        register(new ReaderVanillaAttribute(ATTR_TYPE_SWIMSPEED, LivingEntity.SWIM_SPEED).formatAsDecimal());
        register(new ReaderVanillaAttribute(ATTR_TYPE_ARMOR, SharedMonsterAttributes.ARMOR));
        register(new ReaderVanillaAttribute(ATTR_TYPE_ARMOR_TOUGHNESS, SharedMonsterAttributes.ARMOR_TOUGHNESS));
        register(new ReaderVanillaAttribute(ATTR_TYPE_ATTACK_SPEED, SharedMonsterAttributes.ATTACK_SPEED).formatAsDecimal());
        register(new ReaderVanillaAttribute(ATTR_TYPE_REACH, PlayerEntity.REACH_DISTANCE).formatAsDecimal());
        register(new ReaderVanillaAttribute(ATTR_TYPE_LUCK, SharedMonsterAttributes.LUCK).formatAsDecimal());
        register(new ReaderFlatAttribute(ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM, 1000F)).formatAsDecimal();
        register(new ReaderFlatAttribute(ATTR_TYPE_MINING_SIZE, 0F));

        registerDefaultReader(ATTR_TYPE_INC_PERK_EFFECT);
        registerDefaultReader(ATTR_TYPE_INC_PERK_EXP);
        registerDefaultReader(ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION);
        registerDefaultReader(ATTR_TYPE_INC_CRIT_MULTIPLIER);
        registerDefaultReader(ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        registerDefaultReader(ATTR_TYPE_PROJ_DAMAGE);
        registerDefaultReader(ATTR_TYPE_PROJ_SPEED);
        registerDefaultReader(ATTR_TYPE_LIFE_RECOVERY);
        registerDefaultReader(ATTR_TYPE_POTION_DURATION);
        registerDefaultReader(ATTR_TYPE_INC_ENCH_EFFECT);
        registerDefaultReader(ATTR_TYPE_INC_DODGE);
        registerDefaultReader(ATTR_TYPE_INC_CRIT_CHANCE);
        registerDefaultReader(ATTR_TYPE_ATTACK_LIFE_LEECH);
        registerDefaultReader(ATTR_TYPE_INC_THORNS);
        registerDefaultReader(ATTR_TYPE_COOLDOWN_REDUCTION);

        register(new ReaderBreakSpeed(ATTR_TYPE_INC_HARVEST_SPEED));
    }

    private static PerkAttributeReader registerDefaultReader(PerkAttributeType type) {
        if (type.isMultiplicative()) {
            return register(new ReaderPercentageAttribute(type));
        } else {
            return register(new ReaderAddedPercentage(type));
        }
    }

    private static <T extends PerkAttributeReader> T register(T reader) {
        AstralSorcery.getProxy().getRegistryPrimer().register(reader);
        return reader;
    }

}
