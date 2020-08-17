package hellfirepvp.astralsorcery.datagen.data.perks;

import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.perk.data.PerkTypeHandler;
import hellfirepvp.astralsorcery.common.perk.data.builder.PerkDataBuilder;
import hellfirepvp.astralsorcery.common.perk.data.builder.PerkDataProvider;
import hellfirepvp.astralsorcery.common.perk.modifier.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static hellfirepvp.astralsorcery.AstralSorcery.key;
import static hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS.*;
import static hellfirepvp.astralsorcery.common.lib.PerkNamesAS.*;
import static hellfirepvp.astralsorcery.common.perk.data.PerkTypeHandler.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralPerkTreeProvider
 * Created by HellFirePvP
 * Date: 14.08.2020 / 19:13
 */
public class AstralPerkTreeProvider extends PerkDataProvider {

    public AstralPerkTreeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    public void registerPerks(Consumer<FinishedPerk> registrar) {
        registerRoots(registrar);
        registerTravel(registrar);
        registerInnerRoots(registrar);
    }

    private void registerInnerRoots(Consumer<FinishedPerk> registrar) {
        registerAevitasInner(registrar);
        registerVicioInner(registrar);
        registerArmaraInner(registrar);
        registerDiscidiaInner(registrar);
        registerEvorsioInner(registrar);
    }

    private void registerEvorsioInner(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_inner_1"), 29, 27)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("evorsio_mining_bridge_3"))
                .connect(key("evorsio_inner_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_inner_2"), 31, 29)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("travel_13"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_inner_3"), 32, 25)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("evorsio_mining_bridge_1"))
                .connect(key("evorsio_inner_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_inner_4"), 35, 27)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("travel_14"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_inner_crit1"), 30, 31)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE)
                .connect(key("evorsio_inner_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_inner_crit2"), 29, 32)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE)
                .connect(key("evorsio_inner_crit1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_DISARM)
                .create(key("evorsio_inner_crit3"), 28, 31)
                .setName(name("key.disarm"))
                .connect(key("evorsio_inner_crit2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_digtypes_1"), 34, 23)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .connect(key("evorsio_inner_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_digtypes_2"), 35, 22)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .connect(key("evorsio_digtypes_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_DIG_TYPES)
                .create(key("evorsio_digtypes_3"), 34, 21)
                .setName(name("key.dig_types"))
                .connect(key("evorsio_digtypes_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_charge_inner_1"), 37, 26)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("hybrid.charge_max_regen"))
                .connect(key("evorsio_inner_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_charge_inner_2"), 38, 25)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("hybrid.charge_max_regen"))
                .connect(key("evorsio_charge_inner_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("evorsio_charge_inner_3"), 37, 24)
                .addModifier(0.2F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("named.bloom"))
                .connect(key("evorsio_charge_inner_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_mining_size_1"), 26, 29)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .connect(key("evorsio_inner_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_mining_size_2"), 25, 28)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .connect(key("evorsio_mining_size_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("evorsio_mining_size_3"), 26, 27)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_MINING_SIZE)
                .setName(name("named.illusory_hammer"))
                .connect(key("evorsio_mining_size_2"))
                .build(registrar);
    }

    private void registerDiscidiaInner(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_inner_1"), 47, 24)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE)
                .connect(key("discidia_crit_bridge_3"))
                .connect(key("discidia_inner_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_inner_2"), 46, 27)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE)
                .connect(key("travel_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_inner_3"), 51, 26)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE)
                .connect(key("discidia_crit_bridge_1"))
                .connect(key("discidia_inner_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_inner_4"), 49, 29)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE)
                .connect(key("travel_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_close_range_1"), 45, 21)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE)
                .connect(key("discidia_inner_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_close_range_2"), 46, 20)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE)
                .connect(key("discidia_close_range_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_PROJ_PROXIMITY)
                .create(key("discidia_close_range_3"), 47, 21)
                .setName(name("key.projectile_close_range"))
                .connect(key("discidia_close_range_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_distance_1"), 54, 28)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE)
                .connect(key("discidia_inner_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_distance_2"), 55, 27)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE)
                .connect(key("discidia_distance_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_RPOJ_DISTANCE)
                .create(key("discidia_distance_3"), 54, 26)
                .setName(name("key.projectile_distance"))
                .connect(key("discidia_distance_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_rampage_1"), 44, 26)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .addModifier(0.2F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_RAMPAGE_DURATION)
                .setName(name("hybrid.attack_speed_rampage"))
                .connect(key("discidia_inner_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_rampage_2"), 43, 25)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .addModifier(0.2F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_RAMPAGE_DURATION)
                .setName(name("hybrid.attack_speed_rampage"))
                .connect(key("discidia_rampage_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_RAMPAGE)
                .create(key("discidia_rampage_3"), 44, 24)
                .setName(name("key.rampage"))
                .connect(key("discidia_rampage_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_multi_1"), 51, 30)
                .addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_MULTIPLIER)
                .setName(NAME_INC_CRIT_MULTIPLIER)
                .connect(key("discidia_inner_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_multi_2"), 52, 31)
                .addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_MULTIPLIER)
                .setName(NAME_INC_CRIT_MULTIPLIER)
                .connect(key("discidia_multi_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("discidia_multi_3"), 53, 30)
                .addModifier(0.16F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_MULTIPLIER)
                .addModifier(1.5F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(name("named.combat_focus"))
                .connect(key("discidia_multi_2"))
                .build(registrar);
    }

    private void registerArmaraInner(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_inner_1"), 57, 44)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE)
                .setName(NAME_INC_DODGE)
                .connect(key("armara_dodge_bridge_3"))
                .connect(key("armara_inner_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_inner_2"), 54, 41)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE)
                .setName(NAME_INC_DODGE)
                .connect(key("travel_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_inner_3"), 56, 48)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE)
                .setName(NAME_INC_DODGE)
                .connect(key("armara_dodge_bridge_1"))
                .connect(key("armara_inner_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_inner_4"), 52, 47)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE)
                .setName(NAME_INC_DODGE)
                .connect(key("travel_5"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_dmgarmor_1"), 51, 49)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .connect(key("armara_inner_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_dmgarmor_2"), 52, 50)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .connect(key("armara_dmgarmor_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_DAMAGE_ARMOR)
                .create(key("armara_dmgarmor_3"), 51, 51)
                .setName(name("key.damage_armor"))
                .connect(key("armara_dmgarmor_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_noknockback_1"), 59, 41)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("armara_inner_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_noknockback_2"), 60, 40)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("armara_noknockback_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_NO_KNOCKBACK)
                .create(key("armara_noknockback_3"), 59, 39)
                .setName(name("key.no_knockback"))
                .connect(key("armara_noknockback_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("amara_maxcharge_inner_1"), 53, 38)
                .addModifier(0.07F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(NAME_INC_CHARGE_MAX)
                .connect(key("armara_inner_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("amara_maxcharge_inner_2"), 54, 37)
                .addModifier(0.07F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(NAME_INC_CHARGE_MAX)
                .connect(key("amara_maxcharge_inner_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("amara_maxcharge_inner_3"), 55, 38)
                .addModifier(0.2F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("named.ample_semblance"))
                .connect(key("amara_maxcharge_inner_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_armor_inner_1"), 57, 52)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_ARMOR_TOUGHNESS)
                .setName(name("hybrid.armor_armor_toughness"))
                .connect(key("armara_inner_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_armor_inner_2"), 56, 53)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_ARMOR_TOUGHNESS)
                .setName(name("hybrid.armor_armor_toughness"))
                .connect(key("armara_armor_inner_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("armara_armor_inner_3"), 55, 52)
                .addModifier(4F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_ARMOR_TOUGHNESS)
                .setName(name("named.tough"))
                .connect(key("armara_armor_inner_2"))
                .build(registrar);
    }

    private void registerVicioInner(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_inner_1"), 42, 57)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("vicio_ats_bridge_3"))
                .connect(key("vicio_inner_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_inner_2"), 43, 54)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("travel_7"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_inner_3"), 38, 57)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("vicio_ats_bridge_1"))
                .connect(key("vicio_inner_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_inner_4"), 39, 53)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("travel_8"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_food_reduction_1"), 45, 58)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .connect(key("vicio_inner_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_food_reduction_2"), 46, 57)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .connect(key("vicio_food_reduction_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_REDUCED_FOOD)
                .create(key("vicio_food_reduction_3"), 45, 56)
                .setName(name("key.reduced_food"))
                .connect(key("vicio_food_reduction_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_place_lights_1"), 36, 54)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("vicio_inner_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_place_lights_2"), 35, 53)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("vicio_place_lights_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_SPAWN_LIGHTS)
                .create(key("vicio_place_lights_3"), 36, 52)
                .setName(name("key.place_lights"))
                .connect(key("vicio_place_lights_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_ats_inner_1"), 35, 58)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("vicio_inner_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_ats_inner_2"), 34, 57)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("vicio_ats_inner_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("vicio_ats_inner_3"), 33, 58)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(name("named.zeal"))
                .connect(key("vicio_ats_inner_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_charge_inner_1"), 45, 53)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(name("hybrid.charge_regen_mining"))
                .connect(key("vicio_inner_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_charge_inner_2"), 44, 52)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(name("hybrid.charge_regen_mining"))
                .connect(key("vicio_charge_inner_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("vicio_charge_inner_3"), 45, 51)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(name("named.haste"))
                .connect(key("vicio_charge_inner_2"))
                .build(registrar);
    }

    private void registerAevitasInner(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_inner_1"), 24, 49)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("aevitas_life_bridge_3"))
                .connect(key("aevitas_inner_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_inner_2"), 28, 47)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("travel_10"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_inner_3"), 23, 43)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("aevitas_life_bridge_1"))
                .connect(key("aevitas_inner_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_inner_4"), 27, 42)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("travel_11"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_cleanse_1"), 21, 41)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .setName(NAME_INC_LIFE_RECOVERY)
                .connect(key("aevitas_inner_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_cleanse_2"), 20, 40)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .setName(NAME_INC_LIFE_RECOVERY)
                .connect(key("aevitas_cleanse_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_REMOVE_BAD_POTIONS)
                .create(key("aevitas_cleanse_3"), 21, 39)
                .setName(name("key.cleansing"))
                .connect(key("aevitas_cleanse_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_plant_growth_1"), 23, 53)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(name("hybrid.reach_mining"))
                .connect(key("aevitas_inner_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_plant_growth_2"), 24, 54)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(name("hybrid.reach_mining"))
                .connect(key("aevitas_plant_growth_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_GROWABLES)
                .create(key("aevitas_plant_growth_3"), 25, 53)
                .setName(name("key.plant_growth"))
                .connect(key("aevitas_plant_growth_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_inner_charge_1"), 29, 49)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("hybrid.charge_max_regen"))
                .connect(key("aevitas_inner_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_inner_charge_2"), 30, 50)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("hybrid.charge_max_regen"))
                .connect(key("aevitas_inner_charge_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("aevitas_inner_charge_3"), 31, 49)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .setName(name("named.stellar_vessel"))
                .connect(key("aevitas_inner_charge_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_armor_life_1"), 26, 39)
                .addModifier(-0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_movespeed"))
                .connect(key("aevitas_inner_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_armor_life_2"), 27, 38)
                .addModifier(-0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_movespeed"))
                .connect(key("aevitas_armor_life_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("aevitas_armor_life_3"), 28, 39)
                .addModifier(-0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(name("named.iron_heart"))
                .connect(key("aevitas_armor_life_2"))
                .build(registrar);
    }

    private void registerTravel(Consumer<FinishedPerk> registrar) {
        this.makeTravelNode(40, 29, key("travel_0"))
                .connect(key("travel_1"))
                .connect(key("travel_14"))
                .connect(key("ev_ds_effect_1"))
                .connect(key("ev_ds_exp_1"))
                .build(registrar);
        this.makeTravelNode(44, 30, key("travel_1"))
                .connect(key("travel_0"))
                .connect(key("travel_2"))
                .build(registrar);
        this.makeTravelNode(48, 33, key("travel_2"))
                .connect(key("travel_1"))
                .connect(key("travel_3"))
                .build(registrar);
        this.makeTravelNode(50, 36, key("travel_3"))
                .connect(key("travel_2"))
                .connect(key("travel_4"))
                .connect(key("ar_ds_effect_1"))
                .connect(key("ar_ds_exp_1"))
                .build(registrar);
        this.makeTravelNode(50, 40, key("travel_4"))
                .connect(key("travel_3"))
                .connect(key("travel_5"))
                .build(registrar);
        this.makeTravelNode(49, 45, key("travel_5"))
                .connect(key("travel_4"))
                .connect(key("travel_6"))
                .build(registrar);
        this.makeTravelNode(46, 49, key("travel_6"))
                .connect(key("travel_5"))
                .connect(key("travel_7"))
                .connect(key("ar_vi_effect_1"))
                .connect(key("ar_vi_exp_1"))
                .build(registrar);
        this.makeTravelNode(42, 50, key("travel_7"))
                .connect(key("travel_6"))
                .connect(key("travel_8"))
                .build(registrar);
        this.makeTravelNode(38, 50, key("travel_8"))
                .connect(key("travel_7"))
                .connect(key("travel_9"))
                .build(registrar);
        this.makeTravelNode(34, 49, key("travel_9"))
                .connect(key("travel_8"))
                .connect(key("travel_10"))
                .connect(key("ae_vi_effect_1"))
                .connect(key("ae_vi_exp_1"))
                .build(registrar);
        this.makeTravelNode(31, 45, key("travel_10"))
                .connect(key("travel_9"))
                .connect(key("travel_11"))
                .build(registrar);
        this.makeTravelNode(30, 40, key("travel_11"))
                .connect(key("travel_10"))
                .connect(key("travel_12"))
                .build(registrar);
        this.makeTravelNode(30, 36, key("travel_12"))
                .connect(key("travel_11"))
                .connect(key("travel_13"))
                .connect(key("ae_ev_effect_1"))
                .connect(key("ae_ev_exp_1"))
                .build(registrar);
        this.makeTravelNode(32, 33, key("travel_13"))
                .connect(key("travel_12"))
                .connect(key("travel_14"))
                .build(registrar);
        this.makeTravelNode(36, 30, key("travel_14"))
                .connect(key("travel_13"))
                .connect(key("travel_0"))
                .build(registrar);


        this.makeTravelNode(40, 20, key("travel_15"))
                .connect(key("ev_ds_effect_1"))
                .connect(key("ev_ds_exp_1"))
                .connect(key("ev_ds_effect_2"))
                .connect(key("ev_ds_exp_2"))
                .connect(key("discidia_c_ats_bridge_2"))
                .connect(key("evorsio_c_armor_2"))
                .build(registrar);
        this.makeTravelNode(40, 10, key("travel_16"))
                .connect(key("ev_ds_effect_2"))
                .connect(key("ev_ds_exp_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ev_ds_effect_1"), 41, 25)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_0"))
                .connect(key("travel_15"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ev_ds_exp_1"), 39, 23)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_0"))
                .connect(key("travel_15"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ev_ds_effect_2"), 41, 14)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_15"))
                .connect(key("travel_16"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ev_ds_exp_2"), 39, 16)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_15"))
                .connect(key("travel_16"))
                .build(registrar);


        this.makeTravelNode(59, 32, key("travel_17"))
                .connect(key("ar_ds_effect_1"))
                .connect(key("ar_ds_exp_1"))
                .connect(key("ar_ds_effect_2"))
                .connect(key("ar_ds_exp_2"))
                .connect(key("discidia_c_damage_2"))
                .connect(key("armara_c_recovery_2"))
                .build(registrar);
        this.makeTravelNode(71, 28, key("travel_18"))
                .connect(key("ar_ds_effect_2"))
                .connect(key("ar_ds_exp_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ar_ds_effect_1"), 55, 35)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_3"))
                .connect(key("travel_17"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ar_ds_exp_1"), 53, 33)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_3"))
                .connect(key("travel_17"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ar_ds_effect_2"), 66, 31)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_17"))
                .connect(key("travel_18"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ar_ds_exp_2"), 64, 29)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_17"))
                .connect(key("travel_18"))
                .build(registrar);


        this.makeTravelNode(50, 58, key("travel_19"))
                .connect(key("ar_vi_effect_1"))
                .connect(key("ar_vi_exp_1"))
                .connect(key("ar_vi_effect_2"))
                .connect(key("ar_vi_exp_2"))
                .connect(key("armara_c_movespeed_2"))
                .connect(key("vicio_c_armor_2"))
                .build(registrar);
        this.makeTravelNode(55, 68, key("travel_20"))
                .connect(key("ar_vi_effect_2"))
                .connect(key("ar_vi_exp_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ar_vi_effect_1"), 47, 54)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_6"))
                .connect(key("travel_19"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ar_vi_exp_1"), 49, 52)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_6"))
                .connect(key("travel_19"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ar_vi_effect_2"), 52, 64)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_19"))
                .connect(key("travel_20"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ar_vi_exp_2"), 54, 62)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_19"))
                .connect(key("travel_20"))
                .build(registrar);


        this.makeTravelNode(30, 58, key("travel_21"))
                .connect(key("ae_vi_effect_1"))
                .connect(key("ae_vi_exp_1"))
                .connect(key("ae_vi_effect_2"))
                .connect(key("ae_vi_exp_2"))
                .connect(key("vicio_c_mining_2"))
                .connect(key("aevitas_c_reach_2"))
                .build(registrar);
        this.makeTravelNode(25, 68, key("travel_22"))
                .connect(key("ae_vi_effect_2"))
                .connect(key("ae_vi_exp_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ae_vi_effect_1"), 31, 52)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_9"))
                .connect(key("travel_21"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ae_vi_exp_1"), 33, 54)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_9"))
                .connect(key("travel_21"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ae_vi_effect_2"), 26, 62)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_21"))
                .connect(key("travel_22"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ae_vi_exp_2"), 28, 64)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_21"))
                .connect(key("travel_22"))
                .build(registrar);

        this.makeTravelNode(21, 32, key("travel_23"))
                .connect(key("ae_ev_effect_1"))
                .connect(key("ae_ev_exp_1"))
                .connect(key("ae_ev_effect_2"))
                .connect(key("ae_ev_exp_2"))
                .connect(key("aevitas_c_armor_2"))
                .connect(key("evorsio_c_mining_2"))
                .build(registrar);
        this.makeTravelNode(9, 28, key("travel_24"))
                .connect(key("ae_ev_effect_2"))
                .connect(key("ae_ev_exp_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ae_ev_effect_1"), 27, 33)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_12"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ae_ev_exp_1"), 25, 35)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_12"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ae_ev_effect_2"), 16, 29)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ae_ev_exp_2"), 14, 31)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .build(registrar);
    }

    private void registerRoots(Consumer<FinishedPerk> registrar) {
        registerAevitasRoot(registrar);
        registerVicioRoot(registrar);
        registerArmaraRoot(registrar);
        registerDiscidiaRoot(registrar);
        registerEvorsioRoot(registrar);
    }

    private void registerEvorsioRoot(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(ROOT_EVORSIO)
                .create(key("evorsio"), 21, 14)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_REACH)
                .connect(key("evorsio_damage_1"))
                .connect(key("evorsio_mining_1"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_damage_1"), 25, 16)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE)
                .connect(key("evorsio_damage_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_damage_2"), 27, 17)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE)
                .connect(key("evorsio_m_dmg_ats"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_mining_1"), 23, 18)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("evorsio_mining_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_mining_2"), 24, 20)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("evorsio_m_mining_reach"))
                .build(registrar);

        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("evorsio_m_dmg_ats"), 30, 18)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(name("named.expertise"))
                .connect(key("evorsio_mining_bridge_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("evorsio_m_mining_reach"), 25, 23)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(name("named.tunneling"))
                .connect(key("evorsio_mining_bridge_3"))
                .build(registrar);

        PerkDataBuilder.ofType(GEM_SLOT_PERK)
                .create(key("evorsio_m_gem"), 28, 21)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .connect(key("evorsio_damage_2"))
                .connect(key("evorsio_mining_2"))
                .connect(key("evorsio_mining_bridge_1"))
                .connect(key("evorsio_mining_bridge_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_mining_bridge_1"), 31, 20)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_mining_bridge_2"), 30, 23)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .connect(key("evorsio_mining_bridge_1"))
                .connect(key("evorsio_mining_bridge_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_mining_bridge_3"), 27, 24)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_c_armor_1"), 33, 17)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .connect(key("evorsio_m_dmg_ats"))
                .connect(key("evorsio_c_armor_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_c_armor_2"), 37, 18)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .connect(key("travel_15"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_c_mining_1"), 23, 26)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("evorsio_m_mining_reach"))
                .connect(key("evorsio_c_mining_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_c_mining_2"), 22, 29)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("travel_23"))
                .build(registrar);
    }

    private void registerDiscidiaRoot(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(ROOT_DISCIDIA)
                .create(key("discidia"), 59, 14)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(1.1F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(1.1F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .connect(key("discidia_proj_1"))
                .connect(key("discidia_melee_1"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_proj_1"), 57, 18)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE)
                .connect(key("discidia_proj_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_proj_2"), 56, 20)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE)
                .connect(key("discidia_m_proj_dmg_speed"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_melee_1"), 55, 16)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE)
                .connect(key("discidia_melee_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_melee_2"), 53, 17)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE)
                .connect(key("discidia_m_melee_reach"))
                .build(registrar);

        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("discidia_m_proj_dmg_speed"), 55, 23)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .addModifier(0.2F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_SPEED)
                .setName(name("named.deadly_draw"))
                .connect(key("discidia_crit_bridge_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("discidia_m_melee_reach"), 50, 18)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(name("named.strong_arms"))
                .connect(key("discidia_crit_bridge_3"))
                .build(registrar);

        PerkDataBuilder.ofType(GEM_SLOT_PERK)
                .create(key("discidia_m_gem"), 52, 21)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .connect(key("discidia_proj_2"))
                .connect(key("discidia_melee_2"))
                .connect(key("discidia_crit_bridge_1"))
                .connect(key("discidia_crit_bridge_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_crit_bridge_1"), 53, 24)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_ADD_CRIT_CHANCE)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_crit_bridge_2"), 50, 23)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_ADD_CRIT_CHANCE)
                .connect(key("discidia_crit_bridge_1"))
                .connect(key("discidia_crit_bridge_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_crit_bridge_3"), 49, 20)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_ADD_CRIT_CHANCE)
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_c_damage_1"), 57, 26)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(name("hybrid.melee_proj_dmg"))
                .connect(key("discidia_m_proj_dmg_speed"))
                .connect(key("discidia_c_damage_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_c_damage_2"), 58, 29)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(name("hybrid.melee_proj_dmg"))
                .connect(key("travel_17"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_c_ats_bridge_1"), 47, 17)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("discidia_m_melee_reach"))
                .connect(key("discidia_c_ats_bridge_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_c_ats_bridge_2"), 43, 18)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("travel_15"))
                .build(registrar);
    }

    private void registerArmaraRoot(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(ROOT_ARMARA)
                .create(key("armara"), 70, 51)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(1.20F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_ARMOR)
                .connect(key("armara_armor_1"))
                .connect(key("armara_resist_1"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_armor_1"), 66, 52)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(NAME_ADD_ARMOR)
                .connect(key("armara_armor_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_armor_2"), 64, 51)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(NAME_ADD_ARMOR)
                .connect(key("armara_m_life_armor"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_resist_1"), 68, 48)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_INC_ALL_RES)
                .connect(key("armara_resist_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_resist_2"), 66, 47)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_INC_ALL_RES)
                .connect(key("armara_m_resist_armor"))
                .build(registrar);

        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("armara_m_life_armor"), 61, 52)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(name("named.resilience"))
                .connect(key("armara_dodge_bridge_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("armara_m_resist_armor"), 64, 44)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(name("named.bulwark"))
                .connect(key("armara_dodge_bridge_3"))
                .build(registrar);

        PerkDataBuilder.ofType(GEM_SLOT_PERK)
                .create(key("armara_m_gem"), 63, 48)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .connect(key("armara_armor_2"))
                .connect(key("armara_resist_2"))
                .connect(key("armara_dodge_bridge_1"))
                .connect(key("armara_dodge_bridge_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_dodge_bridge_1"), 60, 50)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_dodge_bridge_2"), 59, 47)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE)
                .connect(key("armara_dodge_bridge_1"))
                .connect(key("armara_dodge_bridge_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_dodge_bridge_3"), 61, 45)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE)
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_c_movespeed_1"), 58, 56)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .connect(key("armara_m_life_armor"))
                .connect(key("armara_c_movespeed_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_c_movespeed_2"), 54, 57)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .connect(key("travel_19"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_c_recovery_1"), 63, 40)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .setName(NAME_INC_LIFE_RECOVERY)
                .connect(key("armara_m_resist_armor"))
                .connect(key("armara_c_recovery_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_c_recovery_2"), 61, 36)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .setName(NAME_INC_LIFE_RECOVERY)
                .connect(key("travel_17"))
                .build(registrar);
    }

    private void registerVicioRoot(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(ROOT_VICIO)
                .create(key("vicio"), 40, 70)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .connect(key("vicio_reach_1"))
                .connect(key("vicio_dodge_1"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_reach_1"), 38, 67)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .connect(key("vicio"))
                .connect(key("vicio_reach_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_reach_2"), 37, 65)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .connect(key("vicio_reach_1"))
                .connect(key("vicio_m_reach_movespeed"))
                .connect(key("vicio_m_gem"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_dodge_1"), 42, 67)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE)
                .connect(key("vicio"))
                .connect(key("vicio_dodge_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_dodge_2"), 43, 65)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE)
                .connect(key("vicio_dodge_1"))
                .connect(key("vicio_m_dodge_movespeed"))
                .connect(key("vicio_m_gem"))
                .build(registrar);

        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("vicio_m_reach_movespeed"), 35, 63)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(name("named.swiftness"))
                .connect(key("vicio_reach_2"))
                .connect(key("vicio_ats_bridge_1"))
                .connect(key("vicio_c_mining_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("vicio_m_dodge_movespeed"), 45, 63)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE)
                .setName(name("named.agility"))
                .connect(key("vicio_dodge_2"))
                .connect(key("vicio_ats_bridge_3"))
                .connect(key("vicio_c_armor_1"))
                .build(registrar);

        PerkDataBuilder.ofType(GEM_SLOT_PERK)
                .create(key("vicio_m_gem"), 40, 64)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .connect(key("vicio_reach_2"))
                .connect(key("vicio_dodge_2"))
                .connect(key("vicio_ats_bridge_1"))
                .connect(key("vicio_ats_bridge_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_ats_bridge_1"), 37, 61)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("vicio_m_reach_movespeed"))
                .connect(key("vicio_ats_bridge_2"))
                .connect(key("vicio_m_gem"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_ats_bridge_2"), 40, 59)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("vicio_ats_bridge_1"))
                .connect(key("vicio_ats_bridge_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_ats_bridge_3"), 43, 61)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("vicio_m_dodge_movespeed"))
                .connect(key("vicio_ats_bridge_2"))
                .connect(key("vicio_m_gem"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_c_mining_1"), 34, 60)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("vicio_m_reach_movespeed"))
                .connect(key("vicio_c_mining_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_c_mining_2"), 31, 61)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("travel_21"))
                .connect(key("vicio_c_mining_1"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_c_armor_1"), 46, 60)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .connect(key("vicio_m_dodge_movespeed"))
                .connect(key("vicio_c_armor_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_c_armor_2"), 49, 61)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .connect(key("vicio_c_armor_1"))
                .connect(key("travel_19"))
                .build(registrar);
    }

    private void registerAevitasRoot(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(ROOT_AEVITAS)
                .create(key("aevitas"), 10, 51)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(2, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .connect(key("aevitas_life_armor_1"))
                .connect(key("aevitas_life_reach_1"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_armor_1"), 12, 48)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_armor"))
                .connect(key("aevitas"))
                .connect(key("aevitas_life_armor_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_armor_2"), 14, 47)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_armor"))
                .connect(key("aevitas_life_armor_1"))
                .connect(key("aevitas_m_life_armor"))
                .connect(key("aevitas_m_gem"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_reach_1"), 14, 52)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_reach"))
                .connect(key("aevitas"))
                .connect(key("aevitas_life_reach_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_reach_2"), 16, 51)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_reach"))
                .connect(key("aevitas_life_reach_1"))
                .connect(key("aevitas_m_life_resist"))
                .connect(key("aevitas_m_gem"))
                .build(registrar);

        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("aevitas_m_life_armor"), 16, 44)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .addModifier(1, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(name("named.thick_skin"))
                .connect(key("aevitas_life_armor_2"))
                .connect(key("aevitas_life_bridge_1"))
                .connect(key("aevitas_c_armor_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("aevitas_m_life_resist"), 19, 52)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .addModifier(1, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(name("named.melding"))
                .connect(key("aevitas_life_reach_2"))
                .connect(key("aevitas_life_bridge_3"))
                .connect(key("aevitas_c_reach_1"))
                .build(registrar);

        PerkDataBuilder.ofType(GEM_SLOT_PERK)
                .create(key("aevitas_m_gem"), 17, 48)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .connect(key("aevitas_life_armor_2"))
                .connect(key("aevitas_life_reach_2"))
                .connect(key("aevitas_life_bridge_1"))
                .connect(key("aevitas_life_bridge_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_bridge_1"), 19, 45)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("aevitas_m_life_armor"))
                .connect(key("aevitas_life_bridge_2"))
                .connect(key("aevitas_m_gem"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_bridge_2"), 21, 47)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("aevitas_life_bridge_1"))
                .connect(key("aevitas_life_bridge_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_bridge_3"), 20, 50)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("aevitas_m_life_resist"))
                .connect(key("aevitas_life_bridge_2"))
                .connect(key("aevitas_m_gem"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_c_armor_1"), 17, 40)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .connect(key("aevitas_m_life_armor"))
                .connect(key("aevitas_c_armor_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_c_armor_2"), 19, 36)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .connect(key("travel_23"))
                .connect(key("aevitas_c_armor_1"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_c_reach_1"), 22, 56)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .connect(key("aevitas_m_life_resist"))
                .connect(key("aevitas_c_reach_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_c_reach_2"), 26, 57)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .connect(key("travel_21"))
                .connect(key("aevitas_c_reach_1"))
                .build(registrar);
    }

    private PerkDataBuilder<AttributeModifierPerk> makeTravelNode(float x, float y, ResourceLocation perkKey) {
        return PerkDataBuilder.ofType(PerkTypeHandler.MODIFIER_PERK)
                .create(perkKey, x, y)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
    }
}
