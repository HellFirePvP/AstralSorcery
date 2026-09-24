/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.perk.reader.*;
import hellfirepvp.astralsorcery.common.perk.reader.custom.ReaderAttackLifeLeech;
import hellfirepvp.astralsorcery.common.perk.source.provider.lumen.LumenBindingSourceProvider;
import hellfirepvp.astralsorcery.common.perk.type.AttributeTypeSafeFallDistance;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeLimiter;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSourceProvider;
import hellfirepvp.astralsorcery.common.perk.source.provider.PerkSourceProvider;
import hellfirepvp.astralsorcery.common.perk.source.provider.equipment.EquipmentSourceProvider;
import hellfirepvp.astralsorcery.common.perk.type.*;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerksAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerksAS {

    public static final DeferredRegister<PerkAttributeType> PERK_ATTRIBUTE_TYPE_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_PERK_ATTRIBUTE_TYPES, AstralSorcery.MODID);
    public static final DeferredRegister<PerkAttributeTypeReader.Type> PERK_ATTRIBUTE_TYPE_READER_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_PERK_ATTRIBUTE_TYPE_READERS, AstralSorcery.MODID);
    public static final DeferredRegister<PerkAttributeConverter> PERK_CONVERTER_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_PERK_CONVERTERS, AstralSorcery.MODID);
    public static final DeferredRegister<PerkAttributeModifier> PERK_CUSTOM_MODIFIER_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_PERK_CUSTOM_MODIFIERS, AstralSorcery.MODID);
    public static final DeferredRegister<ModifierSourceProvider<?>> MODIFIER_SOURCES =
            DeferredRegister.create(RegistriesAS.KEY_PERK_MODIFIER_SOURCES, AstralSorcery.MODID);
    public static final DeferredRegister<PerkAttributeLimiter.Limit> PERK_ATTRIBUTE_LIMITS =
            DeferredRegister.create(RegistriesAS.KEY_PERK_ATTRIBUTE_LIMITS, AstralSorcery.MODID);

    public static class AttributeTypes {

        private static void init() {}

        public static final DeferredHolder<PerkAttributeType, AttributeTypePerkEffect> PERK_EFFECT =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("perk_effect", AttributeTypePerkEffect::new);
        public static final DeferredHolder<PerkAttributeType, PerkAttributeType> PERK_EXPERIENCE =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("perk_experience", () -> PerkAttributeType.create(true));

        public static final DeferredHolder<PerkAttributeType, AttributeTypeArmor> ARMOR =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("armor", AttributeTypeArmor::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeArmorToughness> ARMOR_TOUGHNESS =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("armor_toughness", AttributeTypeArmorToughness::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeAttackDamage> ATTACK_DAMAGE =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("attack_damage", AttributeTypeAttackDamage::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeAttackReach> ATTACK_REACH =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("attack_reach", AttributeTypeAttackReach::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeAttackSpeed> ATTACK_SPEED =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("attack_speed", AttributeTypeAttackSpeed::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeBlockBreakSpeed> BLOCK_BREAK_SPEED =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("block_break_speed", AttributeTypeBlockBreakSpeed::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeBlockReach> BLOCK_REACH =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("block_reach", AttributeTypeBlockReach::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeFallDamage> FALL_DAMAGE =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("fall_damage", AttributeTypeFallDamage::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeLuck> LUCK =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("luck", AttributeTypeLuck::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeMaxHealth> MAX_HEALTH =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("max_health", AttributeTypeMaxHealth::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeMovementSpeed> MOVEMENT_SPEED =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("movement_speed", AttributeTypeMovementSpeed::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeSafeFallDistance> SAFE_FALL_DISTANCE =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("safe_fall_distance", AttributeTypeSafeFallDistance::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeScale> SCALE =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("scale", AttributeTypeScale::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeStepHeight> STEP_HEIGHT =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("step_height", AttributeTypeStepHeight::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeSwimSpeed> SWIM_SPEED =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("swim_speed", AttributeTypeSwimSpeed::new);

        public static final DeferredHolder<PerkAttributeType, AttributeTypeBlockChance> BLOCK_CHANCE =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("block_chance", AttributeTypeBlockChance::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeCooldownReduction> COOLDOWN_REDUCTION =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("cooldown_reduction", AttributeTypeCooldownReduction::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeCriticalHitChance> CRITICAL_HIT_CHANCE =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("critical_hit_chance", AttributeTypeCriticalHitChance::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeCriticalHitDamage> CRITICAL_HIT_DAMAGE =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("critical_hit_damage", AttributeTypeCriticalHitDamage::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeDamageReduction> DAMAGE_REDUCTION =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("damage_reduction", AttributeTypeDamageReduction::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeDamageReflect> DAMAGE_REFLECT =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("damage_reflect", AttributeTypeDamageReflect::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeDynamicEnchantmentEffect> ENCHANTMENT_EFFECT =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("dynamic_enchantment_effect", AttributeTypeDynamicEnchantmentEffect::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeElementalResistance> ELEMENTAL_RESISTANCE =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("elemental_resistance", AttributeTypeElementalResistance::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeAttackLifeLeech> LIFE_LEECH =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("life_leech", AttributeTypeAttackLifeLeech::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeLifeRecovery> LIFE_RECOVERY =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("life_recovery", AttributeTypeLifeRecovery::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeMiningSize> MINING_SIZE =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("mining_size", AttributeTypeMiningSize::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypePierceArmor> PIERCE_ARMOR =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("pierce_armor", AttributeTypePierceArmor::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypePotionDuration> POTION_DURATION =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("potion_duration", AttributeTypePotionDuration::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeProjectileDamage> PROJECTILE_DAMAGE =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("projectile_damage", AttributeTypeProjectileDamage::new);
        public static final DeferredHolder<PerkAttributeType, AttributeTypeProjectileSpeed> PROJECTILE_SPEED =
                PERK_ATTRIBUTE_TYPE_REGISTER.register("projectile_speed", AttributeTypeProjectileSpeed::new);

    }

    public static class Readers {

        private static void init() {}

        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> PERK_EFFECT =
                type("perk_effect", AttributeTypes.PERK_EFFECT, ReaderPercentageAttribute::new);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> PERK_EXPERIENCE =
                type("perk_experience", AttributeTypes.PERK_EXPERIENCE, ReaderPercentageAttribute::new);

        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> ARMOR =
                vanillaType("armor", AttributeTypes.ARMOR, Attributes.ARMOR);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> ARMOR_TOUGHNESS =
                vanillaType("armor_toughness", AttributeTypes.ARMOR_TOUGHNESS, Attributes.ARMOR_TOUGHNESS);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> ATTACK_DAMAGE =
                vanillaDecimalType("attack_damage", AttributeTypes.ATTACK_DAMAGE, Attributes.ATTACK_DAMAGE);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> ATTACK_REACH =
                vanillaDecimalType("attack_reach", AttributeTypes.ATTACK_REACH, Attributes.ENTITY_INTERACTION_RANGE);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> ATTACK_SPEED =
                vanillaDecimalType("attack_speed", AttributeTypes.ATTACK_SPEED, Attributes.ATTACK_SPEED);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> BLOCK_BREAK_SPEED =
                vanillaType("block_break_speed", AttributeTypes.BLOCK_BREAK_SPEED, Attributes.BLOCK_BREAK_SPEED);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> BLOCK_REACH =
                vanillaDecimalType("block_reach", AttributeTypes.BLOCK_REACH, Attributes.BLOCK_INTERACTION_RANGE);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> FALL_DAMAGE =
                vanillaDecimalType("fall_damage", AttributeTypes.FALL_DAMAGE, Attributes.FALL_DAMAGE_MULTIPLIER);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> LUCK =
                vanillaDecimalType("luck", AttributeTypes.LUCK, Attributes.LUCK);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> MAX_HEALTH =
                vanillaType("max_health", AttributeTypes.MAX_HEALTH, Attributes.MAX_HEALTH);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> MOVEMENT_SPEED =
                vanillaDecimalType("movement_speed", AttributeTypes.MOVEMENT_SPEED, Attributes.MOVEMENT_SPEED);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> SAFE_FALL_DISTANCE =
                vanillaType("safe_fall_distance", AttributeTypes.SAFE_FALL_DISTANCE, Attributes.SAFE_FALL_DISTANCE);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> SCALE =
                vanillaDecimalType("scale", AttributeTypes.SCALE, Attributes.SCALE);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> STEP_HEIGHT =
                vanillaType("step_height", AttributeTypes.STEP_HEIGHT, Attributes.STEP_HEIGHT);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> SWIM_SPEED =
                vanillaDecimalType("swim_speed", AttributeTypes.SWIM_SPEED, NeoForgeMod.SWIM_SPEED);

        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> BLOCK_CHANCE =
                type("block_chance", AttributeTypes.BLOCK_CHANCE, ReaderAddedPercentage::withPercent);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> COOLDOWN_REDUCTION =
                type("cooldown_reduction", AttributeTypes.COOLDOWN_REDUCTION, ReaderAddedSecondsPercentage::new);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> CRITICAL_HIT_CHANCE =
                type("critical_hit_chance", AttributeTypes.CRITICAL_HIT_CHANCE, ReaderAddedPercentage::withPercent);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> CRITICAL_HIT_DAMAGE =
                type("critical_hit_damage", AttributeTypes.CRITICAL_HIT_DAMAGE, ReaderPercentageAttribute::new);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> DAMAGE_REDUCTION =
                type("damage_reduction", AttributeTypes.DAMAGE_REDUCTION, type -> new ReaderPercentageAttribute(type).negate());
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> DAMAGE_REFLECT =
                type("damage_reflect", AttributeTypes.DAMAGE_REFLECT, ReaderAddedPercentage::withoutPercent);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> ENCHANTMENT_EFFECT =
                type("dynamic_enchantment_effect", AttributeTypes.ENCHANTMENT_EFFECT, ReaderPercentageAttribute::new);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> ELEMENTAL_RESISTANCE =
                type("elemental_resistance", AttributeTypes.ELEMENTAL_RESISTANCE, ReaderPercentageAttribute::new);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> LIFE_LEECH =
                type("life_leech", AttributeTypes.LIFE_LEECH, ReaderAttackLifeLeech::new);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> LIFE_RECOVERY =
                type("life_recovery", AttributeTypes.LIFE_RECOVERY, ReaderPercentageAttribute::new);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> MINING_SIZE =
                type("mining_size", AttributeTypes.MINING_SIZE, ReaderFlatAttribute.withDefault(1));
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> PIERCE_ARMOR =
                type("pierce_armor", AttributeTypes.PIERCE_ARMOR, ReaderAddedPercentage::withPercent);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> POTION_DURATION =
                type("potion_duration", AttributeTypes.POTION_DURATION, ReaderPercentageAttribute::new);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> PROJECTILE_DAMAGE =
                type("projectile_damage", AttributeTypes.PROJECTILE_DAMAGE, ReaderPercentageAttribute::new);
        public static final DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> PROJECTILE_SPEED =
                type("projectile_speed", AttributeTypes.PROJECTILE_SPEED, ReaderPercentageAttribute::new);


        private static DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> type(String name,
                                                                                                       Supplier<? extends PerkAttributeType> type,
                                                                                                       Function<Supplier<? extends PerkAttributeType>, ? extends PerkAttributeTypeReader> reader) {
            return PERK_ATTRIBUTE_TYPE_READER_REGISTER.register(name, () -> new PerkAttributeTypeReader.Type(type, reader.apply(type)));
        }

        private static DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> vanillaType(String name,
                                                                                                              Supplier<? extends PerkAttributeType> type,
                                                                                                              Holder<Attribute> attribute) {
            return PERK_ATTRIBUTE_TYPE_READER_REGISTER.register(name, () -> {
                return new PerkAttributeTypeReader.Type(type, new ReaderVanillaAttribute(type, attribute));
            });
        }

        private static DeferredHolder<PerkAttributeTypeReader.Type, PerkAttributeTypeReader.Type> vanillaDecimalType(String name,
                                                                                                                     Supplier<? extends PerkAttributeType> type,
                                                                                                                     Holder<Attribute> attribute) {
            return PERK_ATTRIBUTE_TYPE_READER_REGISTER.register(name, () -> {
                return new PerkAttributeTypeReader.Type(type, new ReaderVanillaAttribute(type, attribute).formatAsDecimal());
            });
        }
    }

    public static class Converters {

        private static void init() {}



    }

    public static class CustomModifiers {

        private static void init() {}



    }

    public static class Sources {

        private static void init() {}

        public static final DeferredHolder<ModifierSourceProvider<?>, PerkSourceProvider> PERKS =
                MODIFIER_SOURCES.register("perks", PerkSourceProvider::new);
        public static final DeferredHolder<ModifierSourceProvider<?>, EquipmentSourceProvider> EQUIPMENT =
                MODIFIER_SOURCES.register("equipment", EquipmentSourceProvider::new);
        public static final DeferredHolder<ModifierSourceProvider<?>, LumenBindingSourceProvider> LUMEN_BINDING =
                MODIFIER_SOURCES.register("lumen_binding", LumenBindingSourceProvider::new);

    }

    public static class Limits {

        private static void init() {}

    }

    static {
        // Java doesn't load subclasses out the box, so we gotta manually reference them to run the registrations
        AttributeTypes.init();
        Readers.init();
        Converters.init();
        CustomModifiers.init();
        Sources.init();
        Limits.init();
    }
}
