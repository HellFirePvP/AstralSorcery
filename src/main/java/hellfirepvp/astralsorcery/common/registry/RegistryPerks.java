package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
import hellfirepvp.astralsorcery.common.perk.modifier.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.perk.node.GemSlotMajorPerk;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import hellfirepvp.astralsorcery.common.perk.node.MajorPerk;
import hellfirepvp.astralsorcery.common.perk.node.RootPerk;
import hellfirepvp.astralsorcery.common.perk.node.focus.KeyAlcara;
import hellfirepvp.astralsorcery.common.perk.node.focus.KeyGelu;
import hellfirepvp.astralsorcery.common.perk.node.focus.KeyUlteria;
import hellfirepvp.astralsorcery.common.perk.node.focus.KeyVorux;
import hellfirepvp.astralsorcery.common.perk.node.key.*;
import hellfirepvp.astralsorcery.common.perk.node.root.*;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.ResourceLocation;

import static hellfirepvp.astralsorcery.AstralSorcery.key;
import static hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS.*;
import static hellfirepvp.astralsorcery.common.lib.PerkNamesAS.*;
import static hellfirepvp.astralsorcery.common.perk.PerkTree.PERK_TREE;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryPerks
 * Created by HellFirePvP
 * Date: 25.07.2020 / 13:31
 */
public class RegistryPerks {

    private static int travelNodeCount = 0;

    private RegistryPerks() {}

    //Names for initialize correspond with the layers on the perkTreeMap file.
    public static void init() {
        initializeRoots();
        initializeTravel();
        initializeInnerRoots();
        initializeCore();
        initializeOuterTravel();
        initializeRootConnector();
        initializeMetaPerks();
        initializeOuterRootPerks();
    }

    private static void initializeOuterRootPerks() {
        initializeAevitasOuter();
        initializeVicioOuter();
        initializeArmaraOuter();
        initializeDiscidiaOuter();
        initializeEvorsioOuter();
    }

    private static void initializeEvorsioOuter() {
        AttributeModifierPerk lowlife1 = new AttributeModifierPerk(key("evorsio_outer_lowlife_1"), 28, 8)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED);
        AttributeModifierPerk lowlife2 = new AttributeModifierPerk(key("evorsio_outer_lowlife_2"), 29, 7)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED);
        KeyLastBreath lowlife3 = new KeyLastBreath(key("evorsio_outer_lowlife_3"), 30, 8)
                .setName(name("key.low_life"));
        tree(key("travel_53"))
                .chain(tree(lowlife1))
                .chain(tree(lowlife2))
                .chain(tree(lowlife3));

        AttributeModifierPerk size1 = new AttributeModifierPerk(key("evorsio_outer_size_1"), 17, 9)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
        AttributeModifierPerk size2 = new AttributeModifierPerk(key("evorsio_outer_size_2"), 18, 8)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
        AttributeModifierPerk size3 = new AttributeModifierPerk(key("evorsio_outer_size_3"), 17, 7)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
        MajorPerk size4 = new MajorPerk(key("evorsio_outer_size_4"), 16, 8)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_MINING_SIZE)
                .setName(name("named.geologic_prowess"));
        tree(key("travel_52"))
                .chain(tree(size1))
                .chain(tree(size2))
                .chain(tree(size3))
                .chain(tree(size4));

        AttributeModifierPerk luck1 = new AttributeModifierPerk(key("evorsio_outer_luck_1"), 10, 21)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_LUCK)
                .setName(NAME_ADD_LUCK);
        AttributeModifierPerk luck2 = new AttributeModifierPerk(key("evorsio_outer_luck_2"), 9, 20)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_LUCK)
                .setName(NAME_ADD_LUCK);
        KeyAddEnchantment luck3 = new KeyAddEnchantment(key("evorsio_outer_luck_3"), 10, 19)
                .addEnchantment(Enchantments.FORTUNE, 1)
                .setName(name("key.luck"));
        tree(key("travel_50"))
                .chain(tree(luck1))
                .chain(tree(luck2))
                .chain(tree(luck3));
    }

    private static void initializeDiscidiaOuter() {
        AttributeModifierPerk potionHit1 = new AttributeModifierPerk(key("discidia_outer_potionhit_1"), 67, 24)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED);
        AttributeModifierPerk potionHit2 = new AttributeModifierPerk(key("discidia_outer_potionhit_2"), 66, 25)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED);
        AttributeModifierPerk potionHit3 = new AttributeModifierPerk(key("discidia_outer_potionhit_3"), 65, 24)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED);
        KeyDamageEffects potionHit4 = new KeyDamageEffects(key("discidia_outer_potionhit_4"), 66, 23)
                .setName(name("key.damage_types"));
        tree(key("travel_34"))
                .chain(tree(potionHit1))
                .chain(tree(potionHit2))
                .chain(tree(potionHit3))
                .chain(tree(potionHit4));

        AttributeModifierPerk cull1 = new AttributeModifierPerk(key("discidia_outer_cull_1"), 64, 9)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE);
        AttributeModifierPerk cull2 = new AttributeModifierPerk(key("discidia_outer_cull_2"), 65, 8)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE);
        AttributeModifierPerk cull3 = new AttributeModifierPerk(key("discidia_outer_cull_3"), 64, 7)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE);
        KeyCullingAttack cull4 = new KeyCullingAttack(key("discidia_outer_cull_4"), 63, 8)
                .setName(name("key.cull_attack"));
        tree(key("travel_32"))
                .chain(tree(cull1))
                .chain(tree(cull2))
                .chain(tree(cull3))
                .chain(tree(cull4));

        KeyBleed bleed1 = new KeyBleed(key("discidia_outer_bleed_1"), 56, 8)
                .setName(name("key.bleed"));
        AttributeModifierPerk bleed2 = new AttributeModifierPerk(key("discidia_outer_bleed_2"), 58, 7)
                .addModifier(0.25F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_BLEED_CHANCE)
                .setName(NAME_INC_BLEED_CHANCE);
        AttributeModifierPerk bleed3 = new AttributeModifierPerk(key("discidia_outer_bleed_3"), 59, 8)
                .addModifier(0.25F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_BLEED_DURATION)
                .setName(NAME_INC_BLEED_DURATION);
        AttributeModifierPerk bleed4 = new AttributeModifierPerk(key("discidia_outer_bleed_4"), 55, 6)
                .addModifier(0.25F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_BLEED_CHANCE)
                .setName(NAME_INC_BLEED_CHANCE);
        AttributeModifierPerk bleed5 = new AttributeModifierPerk(key("discidia_outer_bleed_5"), 54, 7)
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_BLEED_STACKS)
                .setName(NAME_ADD_BLEED_STACKS);
        tree(bleed3)
                .chain(tree(bleed2))
                .chain(tree(bleed1))
                .connect(tree(key("travel_31")))
                .chain(tree(bleed4))
                .chain(tree(bleed5));

        KeyLightningArc arc1 = new KeyLightningArc(key("discidia_outer_arc_1"), 49, 7)
                .setName(name("key.arc"));
        AttributeModifierPerk arc2 = new AttributeModifierPerk(key("discidia_outer_arc_2"), 50, 5)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARC_CHAINS)
                .setName(name("special.arc_chain_1"));
        AttributeModifierPerk arc3 = new AttributeModifierPerk(key("discidia_outer_arc_3"), 51, 6)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARC_CHAINS)
                .setName(name("special.arc_chain_1"));
        MajorPerk arc4 = new MajorPerk(key("discidia_outer_arc_4"), 52, 4)
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_ARC_CHAINS)
                .setName(name("special.arc_chain_2"));
        tree(key("travel_30"))
                .chain(tree(arc1))
                .chain(tree(arc2))
                .chain(tree(arc3))
                .chain(tree(arc4));

        AttributeModifierPerk exp1 = new AttributeModifierPerk(key("discidia_outer_exp_1"), 70, 20)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_SPEED)
                .setName(NAME_INC_PROJ_SPEED);
        AttributeModifierPerk exp2 = new AttributeModifierPerk(key("discidia_outer_exp_2"), 71, 19)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_SPEED)
                .setName(NAME_INC_PROJ_SPEED);
        MajorPerk exp3 = new MajorPerk(key("discidia_outer_exp_3"), 70, 18)
                .addModifier(0.14F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_SPEED)
                .setName(name("named.finesse"));
        tree(key("travel_34"))
                .chain(tree(exp1))
                .chain(tree(exp2))
                .chain(tree(exp3));

        AttributeModifierPerk crit1 = new AttributeModifierPerk(key("discidia_outer_crit_1"), 46, 11)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_MULTIPLIER)
                .setName(NAME_INC_CRIT_MULTIPLIER);
        MajorPerk crit2 = new MajorPerk(key("discidia_outer_crit_2"), 47, 12)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_MULTIPLIER)
                .setName(name("named.lethality"));
        tree(key("travel_30"))
                .chain(tree(crit1))
                .chain(tree(crit2));
    }

    private static void initializeArmaraOuter() {
        AttributeModifierPerk reflect1 = new AttributeModifierPerk(key("armara_outer_reflect_1"), 75, 46)
                .addModifier(5F, ModifierType.ADDITION, ATTR_TYPE_INC_THORNS)
                .setName(NAME_INC_THORNS);
        AttributeModifierPerk reflect2 = new AttributeModifierPerk(key("armara_outer_reflect_2"), 76, 47)
                .addModifier(5F, ModifierType.ADDITION, ATTR_TYPE_INC_THORNS)
                .setName(NAME_INC_THORNS);
        AttributeModifierPerk reflect3 = new AttributeModifierPerk(key("armara_outer_reflect_3"), 77, 46)
                .addModifier(5F, ModifierType.ADDITION, ATTR_TYPE_INC_THORNS)
                .setName(NAME_INC_THORNS);
        MajorPerk reflect4 = new MajorPerk(key("armara_outer_reflect_4"), 76, 45)
                .addModifier(5F, ModifierType.ADDITION, ATTR_TYPE_INC_THORNS)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_THORNS_RANGED)
                .setName(name("key.thorns_ranged"));
        tree(key("travel_36"))
                .chain(tree(reflect1))
                .chain(tree(reflect2))
                .chain(tree(reflect3))
                .chain(tree(reflect4));

        AttributeModifierPerk revive1 = new AttributeModifierPerk(key("armara_outer_revive_1"), 72, 36)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE);
        AttributeModifierPerk revive2 = new AttributeModifierPerk(key("armara_outer_revive_2"), 71, 37)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE);
        AttributeModifierPerk revive3 = new AttributeModifierPerk(key("armara_outer_revive_3"), 70, 36)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE);
        KeyCheatDeath revive4 = new KeyCheatDeath(key("armara_outer_revive_4"), 69, 37)
                .setName(name("key.revive"));
        tree(key("travel_35"))
                .chain(tree(revive1))
                .chain(tree(revive2))
                .chain(tree(revive3))
                .chain(tree(revive4));

        AttributeModifierPerk wornArmor1 = new AttributeModifierPerk(key("armara_outer_worn_1"), 70, 62)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_ARMOR_TOUGHNESS)
                .setName(name("hybrid.armor_armor_toughness"));
        AttributeModifierPerk wornArmor2 = new AttributeModifierPerk(key("armara_outer_worn_2"), 71, 63)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_ARMOR_TOUGHNESS)
                .setName(name("hybrid.armor_armor_toughness"));
        AttributeModifierPerk wornArmor3 = new AttributeModifierPerk(key("armara_outer_worn_3"), 72, 62)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_ARMOR_TOUGHNESS)
                .setName(name("hybrid.armor_armor_toughness"));
        KeyNoArmor wornArmor4 = new KeyNoArmor(key("armara_outer_worn_4"), 71, 61)
                .setName(name("key.no_armor"));
        tree(key("travel_38"))
                .chain(tree(wornArmor1))
                .chain(tree(wornArmor2))
                .chain(tree(wornArmor3))
                .chain(tree(wornArmor4));

        AttributeModifierPerk exp1 = new AttributeModifierPerk(key("armara_outer_exp_1"), 64, 65)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP);
        AttributeModifierPerk exp2 = new AttributeModifierPerk(key("armara_outer_exp_2"), 65, 66)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP);
        MajorPerk exp3 = new MajorPerk(key("armara_outer_exp_3"), 66, 65)
                .addModifier(0.12F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("named.clarity"));
        tree(key("travel_39"))
                .chain(tree(exp1))
                .chain(tree(exp2))
                .chain(tree(exp3));

        AttributeModifierPerk recovery1 = new AttributeModifierPerk(key("armara_outer_recovery_1"), 62, 60)
                .addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .setName(name("hybrid.life_recovery_cooldown_reduction"));
        AttributeModifierPerk recovery2 = new AttributeModifierPerk(key("armara_outer_recovery_2"), 61, 59)
                .addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .setName(name("hybrid.life_recovery_cooldown_reduction"));
        MajorPerk recovery3 = new MajorPerk(key("armara_outer_recovery_3"), 60, 60)
                .addModifier(0.10F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .setName(name("named.osmosis"));
        tree(key("travel_39"))
                .chain(tree(recovery1))
                .chain(tree(recovery2))
                .chain(tree(recovery3));

        AttributeModifierPerk proj1 = new AttributeModifierPerk(key("armara_outer_proj_1"), 75, 32)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE);
        AttributeModifierPerk proj2 = new AttributeModifierPerk(key("armara_outer_proj_2"), 76, 31)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE);
        MajorPerk proj3 = new MajorPerk(key("armara_outer_proj_3"), 77, 32)
                .addModifier(0.06F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(name("named.arrow_slits"));
        tree(key("travel_35"))
                .chain(tree(proj1))
                .chain(tree(proj2))
                .chain(tree(proj3));
    }

    private static void initializeVicioOuter() {
        AttributeModifierPerk stepUp1 = new AttributeModifierPerk(key("vicio_outer_step_1"), 51, 69)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED);
        AttributeModifierPerk stepUp2 = new AttributeModifierPerk(key("vicio_outer_step_2"), 50, 68)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED);
        AttributeModifierPerk stepUp3 = new AttributeModifierPerk(key("vicio_outer_step_3"), 51, 67)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED);
        KeyStepAssist stepUp4 = new KeyStepAssist(key("vicio_outer_step_4"), 50, 66)
                .setName(name("key.step_assist"));
        tree(key("travel_40"))
                .chain(tree(stepUp1))
                .chain(tree(stepUp2))
                .chain(tree(stepUp3))
                .chain(tree(stepUp4));

        AttributeModifierPerk magnetDrops1 = new AttributeModifierPerk(key("vicio_outer_magnet_1"), 29, 73)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH);
        AttributeModifierPerk magnetDrops2 = new AttributeModifierPerk(key("vicio_outer_magnet_2"), 30, 74)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH);
        AttributeModifierPerk magnetDrops3 = new AttributeModifierPerk(key("vicio_outer_magnet_3"), 29, 75)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH);
        KeyMagnetDrops magnetDrops4 = new KeyMagnetDrops(key("vicio_outer_magnet_4"), 28, 74)
                .setName(name("key.magnet_drops"));
        tree(key("travel_44"))
                .chain(tree(magnetDrops1))
                .chain(tree(magnetDrops2))
                .chain(tree(magnetDrops3))
                .chain(tree(magnetDrops4));

        AttributeModifierPerk flight1 = new AttributeModifierPerk(key("vicio_outer_flight_1"), 42, 77)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_INC_ALL_RES);
        AttributeModifierPerk flight2 = new AttributeModifierPerk(key("vicio_outer_flight_2"), 43, 78)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_INC_ALL_RES);
        AttributeModifierPerk flight3 = new AttributeModifierPerk(key("vicio_outer_flight_3"), 42, 79)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_INC_ALL_RES);
        KeyMantleFlight flight4 = new KeyMantleFlight(key("vicio_outer_flight_4"), 43, 80)
                .setName(name("key.mantle_flight"));
        tree(key("travel_42"))
                .chain(tree(flight1))
                .chain(tree(flight2))
                .chain(tree(flight3))
                .chain(tree(flight4));

        AttributeModifierPerk cdr1 = new AttributeModifierPerk(key("vicio_outer_cdr_1"), 47, 76)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .setName(NAME_INC_COOLDOWN_RECOVERY);
        AttributeModifierPerk cdr2 = new AttributeModifierPerk(key("vicio_outer_cdr_2"), 46, 77)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .setName(NAME_INC_COOLDOWN_RECOVERY);
        MajorPerk cdr3 = new MajorPerk(key("vicio_outer_cdr_3"), 45, 76)
                .addModifier(0.06F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(name("named.fleet_footed"));
        tree(key("travel_41"))
                .chain(tree(cdr1))
                .chain(tree(cdr2))
                .chain(tree(cdr3));

        AttributeModifierPerk barrier1 = new AttributeModifierPerk(key("vicio_outer_charge_resist_1"), 36, 75)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("hybrid.charge_all_resist"));
        AttributeModifierPerk barrier2 = new AttributeModifierPerk(key("vicio_outer_charge_resist_2"), 37, 76)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("hybrid.charge_all_resist"));
        MajorPerk barrier3 = new MajorPerk(key("vicio_outer_charge_resist_3"), 36, 77)
                .addModifier(0.06F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("named.dazzling_barrier"));
        tree(key("travel_43"))
                .chain(tree(barrier1))
                .chain(tree(barrier2))
                .chain(tree(barrier3));

        AttributeModifierPerk ms1 = new AttributeModifierPerk(key("vicio_outer_movespeed_1"), 31, 68)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED);
        MajorPerk ms2 = new MajorPerk(key("vicio_outer_movespeed_2"), 30, 67)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(name("named.hushed_steps"));
        tree(key("travel_44"))
                .chain(tree(ms1))
                .chain(tree(ms2));

        AttributeModifierPerk meleedmg1 = new AttributeModifierPerk(key("vicio_outer_meleedmg_1"), 54, 73)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE);
        AttributeModifierPerk meleedmg2 = new AttributeModifierPerk(key("vicio_outer_meleedmg_2"), 55, 74)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE);
        MajorPerk meleedmg3 = new MajorPerk(key("vicio_outer_meleedmg_3"), 54, 75)
                .addModifier(0.07F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(name("named.dervish"));
        tree(key("travel_40"))
                .chain(tree(meleedmg1))
                .chain(tree(meleedmg2))
                .chain(tree(meleedmg3));
    }

    private static void initializeAevitasOuter() {
        AttributeModifierPerk trashToTreasure1 = new AttributeModifierPerk(key("aevitas_outer_ttt_1"), 21, 61)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED);
        AttributeModifierPerk trashToTreasure2 = new AttributeModifierPerk(key("aevitas_outer_ttt_2"), 22, 60)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED);
        AttributeModifierPerk trashToTreasure3 = new AttributeModifierPerk(key("aevitas_outer_ttt_3"), 23, 61)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED);
        KeyVoidTrash trashToTreasure4 = new KeyVoidTrash(key("aevitas_outer_ttt_4"), 22, 62)
                .setName(name("key.void_trash"));
        tree(key("travel_45"))
                .chain(tree(trashToTreasure1))
                .chain(tree(trashToTreasure2))
                .chain(tree(trashToTreasure3))
                .chain(tree(trashToTreasure4));

        AttributeModifierPerk stoneEnrich1 = new AttributeModifierPerk(key("aevitas_outer_enrich_1"), 12, 62)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE);
        AttributeModifierPerk stoneEnrich2 = new AttributeModifierPerk(key("aevitas_outer_enrich_2"), 11, 63)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE);
        AttributeModifierPerk stoneEnrich3 = new AttributeModifierPerk(key("aevitas_outer_enrich_3"), 12, 64)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE);
        KeyStoneEnrichment stoneEnrich4 = new KeyStoneEnrichment(key("aevitas_outer_enrich_4"), 13, 63)
                .setName(name("key.stone_enrichment"));
        tree(key("travel_46"))
                .chain(tree(stoneEnrich1))
                .chain(tree(stoneEnrich2))
                .chain(tree(stoneEnrich3))
                .chain(tree(stoneEnrich4));

        AttributeModifierPerk mending1 = new AttributeModifierPerk(key("aevitas_outer_mending_1"), 9, 33)
                .addModifier(0.07F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_INC_ALL_RES);
        AttributeModifierPerk mending2 = new AttributeModifierPerk(key("aevitas_outer_mending_2"), 10, 32)
                .addModifier(0.07F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_ADD_ARMOR);
        AttributeModifierPerk mending3 = new AttributeModifierPerk(key("aevitas_outer_mending_3"), 11, 33)
                .addModifier(0.07F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_ADD_ARMOR);
        KeyMending mending4 = new KeyMending(key("aevitas_outer_mending_4"), 10, 34)
                .setName(name("key.mending"));
        tree(key("travel_49"))
                .chain(tree(mending1))
                .chain(tree(mending2))
                .chain(tree(mending3))
                .chain(tree(mending4));

        AttributeModifierPerk vitality1 = new AttributeModifierPerk(key("aevitas_outer_vit_1"), 3, 42)
                .addModifier(0.12F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .setName(NAME_INC_LIFE_RECOVERY);
        AttributeModifierPerk vitality2 = new AttributeModifierPerk(key("aevitas_outer_vit_2"), 2, 41)
                .addModifier(0.12F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .setName(NAME_INC_LIFE_RECOVERY);
        MajorPerk vitality3 = new MajorPerk(key("aevitas_outer_vit_3"), 3, 40)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("named.vitality"));
        tree(key("travel_48"))
                .chain(tree(vitality1))
                .chain(tree(vitality2))
                .chain(tree(vitality3));

        AttributeModifierPerk perkExp1 = new AttributeModifierPerk(key("aevitas_outer_perkexp_1"), 3, 46)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP);
        MajorPerk perkExp2 = new MajorPerk(key("aevitas_outer_perkexp_2"), 4, 47)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("named.sage"));
        tree(key("travel_48"))
                .chain(tree(perkExp1))
                .chain(tree(perkExp2));

        AttributeModifierPerk cdr1 = new AttributeModifierPerk(key("aevitas_outer_cdr_1"), 18, 65)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .setName(NAME_INC_COOLDOWN_RECOVERY);
        AttributeModifierPerk cdr2 = new AttributeModifierPerk(key("aevitas_outer_cdr_2"), 17, 66)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .setName(NAME_INC_COOLDOWN_RECOVERY);
        MajorPerk cdr3 = new MajorPerk(key("aevitas_outer_cdr_3"), 16, 65)
                .addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(name("named.vivid_growth"));
        tree(key("travel_45"))
                .chain(tree(cdr1))
                .chain(tree(cdr2))
                .chain(tree(cdr3));

        AttributeModifierPerk dodge1 = new AttributeModifierPerk(key("aevitas_outer_dodge_1"), 8, 36)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE);
        AttributeModifierPerk dodge2 = new AttributeModifierPerk(key("aevitas_outer_dodge_2"), 9, 37)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE);
        MajorPerk dodge3 = new MajorPerk(key("aevitas_outer_dodge_3"), 8, 38)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(name("named.adaptive"));
        tree(key("travel_49"))
                .chain(tree(dodge1))
                .chain(tree(dodge2))
                .chain(tree(dodge3));
    }

    private static void initializeMetaPerks() {
        float metaSingleEffect = 0.04F;
        float metaSingleExp    = 0.04F;
        float metaMultiEffect  = 0.02F;
        float metaMultiExp     = 0.02F;

        initializeFocusPerks(metaSingleEffect, metaSingleExp);
        initializeConnectorPerks(metaMultiEffect, metaMultiExp);
    }

    private static void initializeConnectorPerks(float effect, float exp) {
        AttributeModifierPerk aevitasCC1 = new AttributeModifierPerk(key("aevitas_connector_1"), 22, 70)
                .addModifier(effect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(exp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.aevitas"));
        AttributeModifierPerk aevitasCC2 = new AttributeModifierPerk(key("aevitas_connector_2"), 21, 75)
                .addModifier(effect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(exp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.aevitas"));
        AttributeModifierPerk aevitasCC3 = new AttributeModifierPerk(key("aevitas_connector_3"), 17, 71)
                .addModifier(effect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(exp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.aevitas"));
        KeyTreeConnector aevitasCC = new KeyTreeConnector(key("aevitas_connector_4"), 20, 72)
                .setName(name("special.connector.key.aevitas"));
        tree(key("travel_22"))
                .chain(tree(aevitasCC1))
                .connect(tree(aevitasCC))
                .connect(tree(aevitasCC2))
                .chain(tree(aevitasCC3))
                .connect(tree(aevitasCC))
                .chain(tree(aevitasCC2))
                .connect(tree(aevitasCC));
        aevitasCC1.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);
        aevitasCC2.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);
        aevitasCC3.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);
        aevitasCC.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);

        AttributeModifierPerk vicioCC1 = new AttributeModifierPerk(key("vicio_connector_1"), 59, 69)
                .addModifier(effect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(exp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.vicio"));
        AttributeModifierPerk vicioCC2 = new AttributeModifierPerk(key("vicio_connector_2"), 64, 70)
                .addModifier(effect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(exp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.vicio"));
        AttributeModifierPerk vicioCC3 = new AttributeModifierPerk(key("vicio_connector_3"), 60, 74)
                .addModifier(effect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(exp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.vicio"));
        KeyTreeConnector vicioCC = new KeyTreeConnector(key("vicio_connector_4"), 61, 71)
                .setName(name("special.connector.key.vicio"));
        tree(key("travel_20"))
                .chain(tree(vicioCC1))
                .connect(tree(vicioCC))
                .connect(tree(vicioCC2))
                .chain(tree(vicioCC3))
                .connect(tree(vicioCC))
                .chain(tree(vicioCC2))
                .connect(tree(vicioCC));
        vicioCC1.setRequireDiscoveredConstellation(ConstellationsAS.vicio);
        vicioCC2.setRequireDiscoveredConstellation(ConstellationsAS.vicio);
        vicioCC3.setRequireDiscoveredConstellation(ConstellationsAS.vicio);
        vicioCC.setRequireDiscoveredConstellation(ConstellationsAS.vicio);

        AttributeModifierPerk armaraCC1 = new AttributeModifierPerk(key("armara_connector_1"), 74, 26)
                .addModifier(effect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(exp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.armara"));
        AttributeModifierPerk armaraCC2 = new AttributeModifierPerk(key("armara_connector_2"), 75, 21)
                .addModifier(effect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(exp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.armara"));
        AttributeModifierPerk armaraCC3 = new AttributeModifierPerk(key("armara_connector_3"), 79, 25)
                .addModifier(effect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(exp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.armara"));
        KeyTreeConnector armaraCC = new KeyTreeConnector(key("armara_connector_4"), 76, 24)
                .setName(name("special.connector.key.armara"));
        tree(key("travel_18"))
                .chain(tree(armaraCC1))
                .connect(tree(armaraCC))
                .connect(tree(armaraCC2))
                .chain(tree(armaraCC3))
                .connect(tree(armaraCC))
                .chain(tree(armaraCC2))
                .connect(tree(armaraCC));
        armaraCC1.setRequireDiscoveredConstellation(ConstellationsAS.armara);
        armaraCC2.setRequireDiscoveredConstellation(ConstellationsAS.armara);
        armaraCC3.setRequireDiscoveredConstellation(ConstellationsAS.armara);
        armaraCC.setRequireDiscoveredConstellation(ConstellationsAS.armara);

        AttributeModifierPerk discidiaCC1 = new AttributeModifierPerk(key("discidia_connector_1"), 39, 7)
                .addModifier(effect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(exp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.discidia"));
        AttributeModifierPerk discidiaCC2 = new AttributeModifierPerk(key("discidia_connector_2"), 36, 2)
                .addModifier(effect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(exp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.discidia"));
        AttributeModifierPerk discidiaCC3 = new AttributeModifierPerk(key("discidia_connector_3"), 42, 2)
                .addModifier(effect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(exp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.discidia"));
        KeyTreeConnector discidiaCC = new KeyTreeConnector(key("discidia_connector_4"), 39, 4)
                .setName(name("special.connector.key.discidia"));
        tree(key("travel_16"))
                .chain(tree(discidiaCC1))
                .connect(tree(discidiaCC))
                .connect(tree(discidiaCC2))
                .chain(tree(discidiaCC3))
                .connect(tree(discidiaCC))
                .chain(tree(discidiaCC2))
                .connect(tree(discidiaCC));
        discidiaCC1.setRequireDiscoveredConstellation(ConstellationsAS.discidia);
        discidiaCC2.setRequireDiscoveredConstellation(ConstellationsAS.discidia);
        discidiaCC3.setRequireDiscoveredConstellation(ConstellationsAS.discidia);
        discidiaCC.setRequireDiscoveredConstellation(ConstellationsAS.discidia);

        AttributeModifierPerk evorsioCC1 = new AttributeModifierPerk(key("evorsio_connector_1"), 6, 26)
                .addModifier(effect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(exp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.evorsio"));
        AttributeModifierPerk evorsioCC2 = new AttributeModifierPerk(key("evorsio_connector_2"), 1, 25)
                .addModifier(effect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(exp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.evorsio"));
        AttributeModifierPerk evorsioCC3 = new AttributeModifierPerk(key("evorsio_connector_3"), 5, 21)
                .addModifier(effect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(exp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.evorsio"));
        KeyTreeConnector evorsioCC = new KeyTreeConnector(key("evorsio_connector_4"), 4, 24)
                .setName(name("special.connector.key.evorsio"));
        tree(key("travel_24"))
                .chain(tree(evorsioCC1))
                .connect(tree(evorsioCC))
                .connect(tree(evorsioCC2))
                .chain(tree(evorsioCC3))
                .connect(tree(evorsioCC))
                .chain(tree(evorsioCC2))
                .connect(tree(evorsioCC));
        evorsioCC1.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);
        evorsioCC2.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);
        evorsioCC3.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);
        evorsioCC.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);
    }

    private static void initializeFocusPerks(float effect, float exp) {
        AttributeModifierPerk voruxEffect = new AttributeModifierPerk(key("vorux_effect"), 3, 54)
                .addModifier(effect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
        AttributeModifierPerk voruxExp = new AttributeModifierPerk(key("vorux_exp"), 5, 56)
                .addModifier(exp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EFFECT);
        KeyVorux vorux = new KeyVorux(key("focus_vorux"), 1, 58)
                .setName(name("special.focus.vorux"));
        tree(key("travel_47"))
                .chain(tree(voruxEffect))
                .chain(tree(vorux))
                .chain(tree(voruxExp))
                .chain(tree(key("travel_47")));

        voruxEffect.setRequireDiscoveredConstellation(ConstellationsAS.vorux);
        voruxExp.setRequireDiscoveredConstellation(ConstellationsAS.vorux);
        vorux.setRequireDiscoveredConstellation(ConstellationsAS.vorux);

        AttributeModifierPerk alcaraEffect = new AttributeModifierPerk(key("alcara_effect"), 75, 56)
                .addModifier(effect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
        AttributeModifierPerk alcaraExp = new AttributeModifierPerk(key("alcara_exp"), 77, 54)
                .addModifier(exp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EFFECT);
        KeyAlcara alcara = new KeyAlcara(key("focus_alcara"), 79, 58)
                .setName(name("special.focus.alcara"));
        tree(key("travel_37"))
                .chain(tree(alcaraEffect))
                .chain(tree(alcara))
                .chain(tree(alcaraExp))
                .chain(tree(key("travel_37")));

        alcaraEffect.setRequireDiscoveredConstellation(ConstellationsAS.alcara);
        alcaraExp.setRequireDiscoveredConstellation(ConstellationsAS.alcara);
        alcara.setRequireDiscoveredConstellation(ConstellationsAS.alcara);

        AttributeModifierPerk ulteriaEffect = new AttributeModifierPerk(key("ulteria_effect"), 68, 15)
                .addModifier(effect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
        AttributeModifierPerk ulteriaExp = new AttributeModifierPerk(key("ulteria_exp"), 66, 13)
                .addModifier(exp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EFFECT);
        KeyUlteria ulteria = new KeyUlteria(key("focus_ulteria"), 70, 11)
                .setName(name("special.focus.ulteria"));
        tree(key("travel_33"))
                .chain(tree(ulteriaEffect))
                .chain(tree(ulteria))
                .chain(tree(ulteriaExp))
                .chain(tree(key("travel_33")));

        ulteriaEffect.setRequireDiscoveredConstellation(ConstellationsAS.ulteria);
        ulteriaExp.setRequireDiscoveredConstellation(ConstellationsAS.ulteria);
        ulteria.setRequireDiscoveredConstellation(ConstellationsAS.ulteria);

        AttributeModifierPerk geluEffect = new AttributeModifierPerk(key("gelu_effect"), 14, 13)
                .addModifier(effect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
        AttributeModifierPerk geluExp = new AttributeModifierPerk(key("gelu_exp"), 12, 15)
                .addModifier(exp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EFFECT);
        KeyGelu gelu = new KeyGelu(key("focus_gelu"), 10, 11)
                .setName(name("special.focus.gelu"));
        tree(key("travel_51"))
                .chain(tree(geluEffect))
                .chain(tree(gelu))
                .chain(tree(geluExp))
                .chain(tree(key("travel_51")));

        geluEffect.setRequireDiscoveredConstellation(ConstellationsAS.gelu);
        geluExp.setRequireDiscoveredConstellation(ConstellationsAS.gelu);
        gelu.setRequireDiscoveredConstellation(ConstellationsAS.gelu);
    }

    private static void initializeRootConnector() {
        AttributeModifierPerk aevitasArmor1 = new AttributeModifierPerk(key("aevitas_co_armor_1"), 12, 43)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_ADD_ARMOR);
        AttributeModifierPerk aevitasArmor2 = new AttributeModifierPerk(key("aevitas_co_armor_2"), 9, 45)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_ADD_ARMOR);
        tree(key("aevitas_m_life_armor"))
                .chain(tree(aevitasArmor1))
                .chain(tree(aevitasArmor2))
                .chain(tree(key("travel_48")));

        AttributeModifierPerk aevitasAts1 = new AttributeModifierPerk(key("aevitas_co_ats_1"), 18, 55)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_ATTACK_SPEED);
        AttributeModifierPerk aevitasAts2 = new AttributeModifierPerk(key("aevitas_co_ats_2"), 15, 56)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_ATTACK_SPEED);
        tree(key("aevitas_m_life_resist"))
                .chain(tree(aevitasAts1))
                .chain(tree(aevitasAts2))
                .chain(tree(key("travel_46")));

        AttributeModifierPerk vicioMs1 = new AttributeModifierPerk(key("vicio_co_ms_1"), 33, 66)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_MOVESPEED);
        AttributeModifierPerk vicioMs2 = new AttributeModifierPerk(key("vicio_co_ms_2"), 34, 70)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_MOVESPEED);
        tree(key("vicio_m_reach_movespeed"))
                .chain(tree(vicioMs1))
                .chain(tree(vicioMs2))
                .chain(tree(key("travel_43")));

        AttributeModifierPerk vicioDodge1 = new AttributeModifierPerk(key("vicio_co_dodge_1"), 47, 66)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_DODGE);
        AttributeModifierPerk vicioDodge2 = new AttributeModifierPerk(key("vicio_co_dodge_2"), 45, 69)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_DODGE);
        tree(key("vicio_m_dodge_movespeed"))
                .chain(tree(vicioDodge1))
                .chain(tree(vicioDodge2))
                .chain(tree(key("travel_41")));

        AttributeModifierPerk armaraLife1 = new AttributeModifierPerk(key("armara_co_life_1"), 63, 55)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_ADD_LIFE);
        AttributeModifierPerk armaraLife2 = new AttributeModifierPerk(key("armara_co_life_2"), 67, 56)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_ADD_LIFE);
        tree(key("armara_m_life_armor"))
                .chain(tree(armaraLife1))
                .chain(tree(armaraLife2))
                .chain(tree(key("travel_38")));

        AttributeModifierPerk armaraMining1 = new AttributeModifierPerk(key("armara_co_mining_1"), 67, 43)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_MINING_SPEED);
        AttributeModifierPerk armaraMining2 = new AttributeModifierPerk(key("armara_co_mining_2"), 70, 45)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_MINING_SPEED);
        tree(key("armara_m_resist_armor"))
                .chain(tree(armaraMining1))
                .chain(tree(armaraMining2))
                .chain(tree(key("travel_36")));

        AttributeModifierPerk discidiaProj1 = new AttributeModifierPerk(key("discidia_co_projectiles_1"), 59, 21)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_SPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(name("hybrid.proj_dmg_speed"));
        AttributeModifierPerk discidiaProj2 = new AttributeModifierPerk(key("discidia_co_projectiles_2"), 62, 18)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_SPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(name("hybrid.proj_dmg_speed"));
        tree(key("discidia_m_proj_dmg_speed"))
                .chain(tree(discidiaProj1))
                .chain(tree(discidiaProj2))
                .chain(tree(key("travel_33")));

        AttributeModifierPerk discidiaMelee1 = new AttributeModifierPerk(key("discidia_co_melee_1"), 49, 15)
                .addModifier(0.10F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(name("hybrid.melee_crit_chance"));
        AttributeModifierPerk discidiaMelee2 = new AttributeModifierPerk(key("discidia_co_melee_2"), 51, 12)
                .addModifier(0.10F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(name("hybrid.melee_crit_chance"));
        tree(key("discidia_m_melee_reach"))
                .chain(tree(discidiaMelee1))
                .chain(tree(discidiaMelee2))
                .chain(tree(key("travel_31")));

        AttributeModifierPerk evorsioCdr1 = new AttributeModifierPerk(key("evorsio_co_cdr_1"), 31, 15)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(name("hybrid.life_cooldown_reduction"));
        AttributeModifierPerk evorsioCdr2 = new AttributeModifierPerk(key("evorsio_co_cdr_2"), 28, 13)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(name("hybrid.life_cooldown_reduction"));
        tree(key("evorsio_m_dmg_ats"))
                .chain(tree(evorsioCdr1))
                .chain(tree(evorsioCdr2))
                .chain(tree(key("travel_53")));

        AttributeModifierPerk evorsioReach1 = new AttributeModifierPerk(key("evorsio_co_reach_1"), 21, 22)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(name("hybrid.reach_movespeed"));
        AttributeModifierPerk evorsioReach2 = new AttributeModifierPerk(key("evorsio_co_reach_2"), 17, 20)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(name("hybrid.reach_movespeed"));
        tree(key("evorsio_m_mining_reach"))
                .chain(tree(evorsioReach1))
                .chain(tree(evorsioReach2))
                .chain(tree(key("travel_51")));
    }

    private static void initializeOuterTravel() {
        tree(key("travel_16"))
                .chain(createTravelNode(47, 9))
                .chain(createTravelNode(54, 10))
                .chain(createTravelNode(62, 11))
                .chain(createTravelNode(65, 16))
                .chain(createTravelNode(69, 22))
                .chain(tree(key("travel_18")))
                .chain(createTravelNode(73, 35))
                .chain(createTravelNode(73, 44))
                .chain(createTravelNode(74, 53))
                .chain(createTravelNode(69, 59))
                .chain(createTravelNode(61, 63))
                .chain(tree(key("travel_20")))
                .chain(createTravelNode(52, 71))
                .chain(createTravelNode(46, 73))
                .chain(createTravelNode(41, 74))
                .chain(createTravelNode(35, 73))
                .chain(createTravelNode(30, 71))
                .chain(tree(key("travel_22")))
                .chain(createTravelNode(20, 64))
                .chain(createTravelNode(13, 59))
                .chain(createTravelNode(6, 53))
                .chain(createTravelNode(5, 44))
                .chain(createTravelNode(6, 34))
                .chain(tree(key("travel_24")))
                .chain(createTravelNode(12, 23))
                .chain(createTravelNode(15, 16))
                .chain(createTravelNode(19, 11))
                .chain(createTravelNode(26, 10))
                .chain(createTravelNode(33, 11))
                .chain(tree(key("travel_16")));
    }

    private static void initializeCore() {
        GemSlotMajorPerk core = new GemSlotMajorPerk(key("core_m_gem"), 40, 40);

        tree(core).chain(createTravelNode(41, 34))
                .chain(tree(key("travel_0")));
        tree(core).chain(createTravelNode(45, 39))
                .chain(tree(key("travel_3")));
        tree(core).chain(createTravelNode(43, 45))
                .chain(tree(key("travel_6")));
        tree(core).chain(createTravelNode(37, 46))
                .chain(tree(key("travel_9")));
        tree(core).chain(createTravelNode(35, 38))
                .chain(tree(key("travel_12")));

        AttributeModifierPerk chargeKey1 = new AttributeModifierPerk(key("core_charge_cave_1"), 44, 33)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .setName(NAME_INC_CHARGE_REGEN);
        AttributeModifierPerk chargeKey2 = new AttributeModifierPerk(key("core_charge_cave_2"), 45, 34)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .setName(NAME_INC_CHARGE_REGEN);
        KeyChargeBalancing chargeKey3 = new KeyChargeBalancing(key("core_charge_cave_3"), 44, 35)
                .addModifier(0.7F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .setName(name("key.charge_regen"));
        tree(key("travel_25"))
                .chain(tree(chargeKey1))
                .chain(tree(chargeKey2))
                .chain(tree(chargeKey3));

        AttributeModifierPerk aoeReach1 = new AttributeModifierPerk(key("core_aoe_reach_1"), 34, 41)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH);
        AttributeModifierPerk aoeReach2 = new AttributeModifierPerk(key("core_aoe_reach_2"), 33, 42)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH);
        KeyAreaOfEffect aoeReach3 = new KeyAreaOfEffect(key("core_aoe_reach_3"), 32, 41)
                .setName(name("key.aoe_effect"));
        tree(key("travel_29"))
                .chain(tree(aoeReach1))
                .chain(tree(aoeReach2))
                .chain(tree(aoeReach3));

        AttributeModifierPerk potionDur1 = new AttributeModifierPerk(key("core_potion_dur_1"), 43, 37)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_POTION_DURATION)
                .setName(NAME_INC_POTION_DURATION);
        MajorPerk potionDur2 = new MajorPerk(key("core_potion_dur_2"), 42, 38)
                .addModifier(0.3F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_POTION_DURATION)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(name("named.alchemists_flasks"));
        tree(key("travel_26"))
                .chain(tree(potionDur1))
                .chain(tree(potionDur2));

        AttributeModifierPerk potionDur3 = new AttributeModifierPerk(key("core_potion_dur_3"), 39, 45)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_POTION_DURATION)
                .setName(NAME_INC_POTION_DURATION);
        MajorPerk potionDur4 = new MajorPerk(key("core_potion_dur_4"), 40, 44)
                .addModifier(0.4F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_POTION_DURATION)
                .addModifier(0.85F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("named.profane_chemistry"));
        tree(key("travel_28"))
                .chain(tree(potionDur3))
                .chain(tree(potionDur4));

        AttributeModifierPerk unbreaking1 = new AttributeModifierPerk(key("core_unbreaking_1"), 34, 44)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_INC_ALL_RES);
        AttributeModifierPerk unbreaking2 = new AttributeModifierPerk(key("core_unbreaking_2"), 33, 45)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_INC_ALL_RES);
        KeyPerk unbreaking3 = new KeyAddEnchantment(key("core_unbreaking_3"), 34, 46)
                .addEnchantment(Enchantments.UNBREAKING, 1)
                .setName(name("key.enduring"));
        tree(key("travel_28"))
                .chain(tree(unbreaking1))
                .chain(tree(unbreaking2))
                .chain(tree(unbreaking3));

        AttributeModifierPerk enchEffect1 = new AttributeModifierPerk(key("core_ench_effect_1"), 34, 36)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ENCH_EFFECT)
                .setName(NAME_INC_ENCH_EFFECT);
        AttributeModifierPerk enchEffect2 = new AttributeModifierPerk(key("core_ench_effect_2"), 33, 35)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ENCH_EFFECT)
                .setName(NAME_INC_ENCH_EFFECT);
        MajorPerk enchEffect3 = new MajorPerk(key("core_ench_effect_3"), 34, 34)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ENCH_EFFECT)
                .setName(name("named.prismatic_shimmer"));
        tree(key("travel_29"))
                .chain(tree(enchEffect1))
                .chain(tree(enchEffect2))
                .chain(tree(enchEffect3));

        AttributeModifierPerk cdr1 = new AttributeModifierPerk(key("core_cdr_1"), 42, 47)
                .addModifier(0.07F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .setName(NAME_INC_COOLDOWN_RECOVERY);
        AttributeModifierPerk cdr2 = new AttributeModifierPerk(key("core_cdr_2"), 41, 48)
                .addModifier(0.07F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .setName(NAME_INC_COOLDOWN_RECOVERY);
        MajorPerk cdr3 = new MajorPerk(key("core_cdr_3"), 40, 47)
                .addModifier(0.20F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .setName(name("named.tilted_pendulum"));
        tree(key("travel_27"))
                .chain(tree(cdr1))
                .chain(tree(cdr2))
                .chain(tree(cdr3));

        AttributeModifierPerk leech1 = new AttributeModifierPerk(key("core_lifeleech_1"), 47, 40)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_ATTACK_LIFE_LEECH)
                .setName(NAME_ADD_LIFE_LEECH);
        AttributeModifierPerk leech2 = new AttributeModifierPerk(key("core_lifeleech_2"), 48, 41)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_ATTACK_LIFE_LEECH)
                .setName(NAME_ADD_LIFE_LEECH);
        MajorPerk leech3 = new MajorPerk(key("core_lifeleech_3"), 47, 42)
                .addModifier(1.5F, ModifierType.ADDITION, ATTR_TYPE_ATTACK_LIFE_LEECH)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(name("named.vampirism"));
        tree(key("travel_26"))
                .chain(tree(leech1))
                .chain(tree(leech2))
                .chain(tree(leech3));

        AttributeModifierPerk perkExp1 = new AttributeModifierPerk(key("core_perk_exp_1"), 39, 32)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE);
        MajorPerk perkExp2 = new MajorPerk(key("core_perk_exp_2"), 38, 33)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(name("named.precision"));
        tree(key("travel_25"))
                .chain(tree(perkExp1))
                .chain(tree(perkExp2));

        AttributeModifierPerk perkExp3 = new AttributeModifierPerk(key("core_perk_exp_3"), 45, 44)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP);
        MajorPerk perkExp4 = new MajorPerk(key("core_perk_exp_4"), 46, 45)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("named.focused_mind"));
        tree(key("travel_27"))
                .chain(tree(perkExp3))
                .chain(tree(perkExp4));

        AttributeModifierPerk luck1 = new AttributeModifierPerk(key("core_luck_1"), 38, 36)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_LUCK)
                .setName(NAME_ADD_LUCK);
        MajorPerk luck2 = new MajorPerk(key("core_luck_2"), 39, 37)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_LUCK)
                .setName(name("named.cunning"));
        tree(key("travel_25"))
                .chain(tree(luck1))
                .chain(tree(luck2));

        AttributeModifierPerk smite1 = new AttributeModifierPerk(key("core_smite_1"), 36, 42)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE);
        KeyPerk smite2 = new KeyAddEnchantment(key("core_smite_2"), 37, 41)
                .addEnchantment(Enchantments.SMITE, 1)
                .setName(name("key.undead_bane"));
        tree(key("travel_28"))
                .chain(tree(smite1))
                .chain(tree(smite2));

        AttributeModifierPerk infinity1 = new AttributeModifierPerk(key("core_infinity_1"), 44, 42)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE);
        KeyPerk infinity2 = new KeyAddEnchantment(key("core_infinity_2"), 43, 41)
                .addEnchantment(Enchantments.INFINITY, 1)
                .setName(name("key.endless_munitions"));
        tree(key("travel_26"))
                .chain(tree(infinity1))
                .chain(tree(infinity2));
    }

    private static void initializeInnerRoots() {
        initAevitasInner();
        initVicioInner();
        initArmaraInner();
        initDiscidiaInner();
        initEvorsioInner();
    }

    private static void initEvorsioInner() {
        AttributeModifierPerk innerEff1 = new AttributeModifierPerk(key("evorsio_inner_1"), 29, 27)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED);
        AttributeModifierPerk innerEff2 = new AttributeModifierPerk(key("evorsio_inner_2"), 31, 29)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED);
        AttributeModifierPerk innerEx1 = new AttributeModifierPerk(key("evorsio_inner_3"), 32, 25)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED);
        AttributeModifierPerk innerEx2 = new AttributeModifierPerk(key("evorsio_inner_4"), 35, 27)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED);
        tree(key("evorsio_mining_bridge_3"))
                .chain(tree(innerEff1))
                .chain(tree(innerEff2))
                .chain(tree(key("travel_13")));
        tree(key("evorsio_mining_bridge_1"))
                .chain(tree(innerEx1))
                .chain(tree(innerEx2))
                .chain(tree(key("travel_14")));

        AttributeModifierPerk critDisarm1 = new AttributeModifierPerk(key("evorsio_inner_crit1"), 30, 31)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE);
        AttributeModifierPerk critDisarm2 = new AttributeModifierPerk(key("evorsio_inner_crit2"), 29, 32)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE);
        KeyDisarm critDisarm3 = new KeyDisarm(key("evorsio_inner_crit3"), 28, 31)
                .setName(name("key.disarm"));
        tree(innerEff2)
                .chain(tree(critDisarm1))
                .chain(tree(critDisarm2))
                .chain(tree(critDisarm3));

        AttributeModifierPerk digTypes1 = new AttributeModifierPerk(key("evorsio_digtypes_1"), 34, 23)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH);
        AttributeModifierPerk digTypes2 = new AttributeModifierPerk(key("evorsio_digtypes_2"), 35, 22)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH);
        KeyDigTypes digTypes3 = new KeyDigTypes(key("evorsio_digtypes_3"), 34, 21)
                .setName(name("key.dig_types"));
        tree(innerEx1)
                .chain(tree(digTypes1))
                .chain(tree(digTypes2))
                .chain(tree(digTypes3));

        AttributeModifierPerk charge1 = new AttributeModifierPerk(key("evorsio_charge_inner_1"), 37, 26)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("hybrid.charge_max_regen"));
        AttributeModifierPerk charge2 = new AttributeModifierPerk(key("evorsio_charge_inner_2"), 38, 25)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("hybrid.charge_max_regen"));
        MajorPerk charge3 = new MajorPerk(key("evorsio_charge_inner_3"), 37, 24)
                .addModifier(0.2F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("named.bloom"));
        tree(innerEx2)
                .chain(tree(charge1))
                .chain(tree(charge2))
                .chain(tree(charge3));

        AttributeModifierPerk miningSize1 = new AttributeModifierPerk(key("evorsio_mining_size_1"), 26, 29)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED);
        AttributeModifierPerk miningSize2 = new AttributeModifierPerk(key("evorsio_mining_size_2"), 25, 28)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED);
        MajorPerk miningSize3 = new MajorPerk(key("evorsio_mining_size_3"), 26, 27)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_MINING_SIZE)
                .setName(name("named.illusory_hammer"));
        tree(innerEff1)
                .chain(tree(miningSize1))
                .chain(tree(miningSize2))
                .chain(tree(miningSize3));
    }

    private static void initDiscidiaInner() {
        AttributeModifierPerk innerEff1 = new AttributeModifierPerk(key("discidia_inner_1"), 47, 24)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE);
        AttributeModifierPerk innerEff2 = new AttributeModifierPerk(key("discidia_inner_2"), 46, 27)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE);
        AttributeModifierPerk innerEx1 = new AttributeModifierPerk(key("discidia_inner_3"), 51, 26)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE);
        AttributeModifierPerk innerEx2 = new AttributeModifierPerk(key("discidia_inner_4"), 49, 29)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE);
        tree(key("discidia_crit_bridge_3"))
                .chain(tree(innerEff1))
                .chain(tree(innerEff2))
                .chain(tree(key("travel_1")));
        tree(key("discidia_crit_bridge_1"))
                .chain(tree(innerEx1))
                .chain(tree(innerEx2))
                .chain(tree(key("travel_2")));

        AttributeModifierPerk perkCloseRange1 = new AttributeModifierPerk(key("discidia_close_range_1"), 45, 21)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE);
        AttributeModifierPerk perkCloseRange2 = new AttributeModifierPerk(key("discidia_close_range_2"), 46, 20)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE);
        KeyProjectileProximity perkCloseRange3 = new KeyProjectileProximity(key("discidia_close_range_3"), 47, 21)
                .setName(name("key.projectile_close_range"));
        tree(innerEff1)
                .chain(tree(perkCloseRange1))
                .chain(tree(perkCloseRange2))
                .chain(tree(perkCloseRange3));

        AttributeModifierPerk perkDistance1 = new AttributeModifierPerk(key("discidia_distance_1"), 54, 28)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE);
        AttributeModifierPerk perkDistance2 = new AttributeModifierPerk(key("discidia_distance_2"), 55, 27)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE);
        KeyProjectileDistance perkDistance3 = new KeyProjectileDistance(key("discidia_distance_3"), 54, 26)
                .setName(name("key.projectile_distance"));
        tree(innerEx1)
                .chain(tree(perkDistance1))
                .chain(tree(perkDistance2))
                .chain(tree(perkDistance3));

        AttributeModifierPerk perkRampage1 = new AttributeModifierPerk(key("discidia_rampage_1"), 44, 26)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_RAMPAGE_DURATION)
                .setName(name("hybrid.attack_speed_rampage"));
        AttributeModifierPerk perkRampage2 = new AttributeModifierPerk(key("discidia_rampage_2"), 43, 25)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_RAMPAGE_DURATION)
                .setName(name("hybrid.attack_speed_rampage"));
        KeyRampage perkRampage3 = new KeyRampage(key("discidia_rampage_3"), 44, 24)
                .setName(name("key.rampage"));
        tree(innerEff2)
                .chain(tree(perkRampage1))
                .chain(tree(perkRampage2))
                .chain(tree(perkRampage3));

        AttributeModifierPerk perkMulti1 = new AttributeModifierPerk(key("discidia_multi_1"), 51, 30)
                .addModifier(0.07F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_MULTIPLIER)
                .setName(NAME_INC_CRIT_MULTIPLIER);
        AttributeModifierPerk perkMulti2 = new AttributeModifierPerk(key("discidia_multi_2"), 52, 31)
                .addModifier(0.07F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_MULTIPLIER)
                .setName(NAME_INC_CRIT_MULTIPLIER);
        MajorPerk perkMulti3 = new MajorPerk(key("discidia_multi_3"), 53, 30)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_MULTIPLIER)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(name("named.combat_focus"));
        tree(innerEx2)
                .chain(tree(perkMulti1))
                .chain(tree(perkMulti2))
                .chain(tree(perkMulti3));
    }

    private static void initArmaraInner() {
        AttributeModifierPerk innerEff1 = new AttributeModifierPerk(key("armara_inner_1"), 57, 44)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE)
                .setName(NAME_INC_DODGE);
        AttributeModifierPerk innerEff2 = new AttributeModifierPerk(key("armara_inner_2"), 54, 41)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE)
                .setName(NAME_INC_DODGE);
        AttributeModifierPerk innerEx1 = new AttributeModifierPerk(key("armara_inner_3"), 56, 48)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE)
                .setName(NAME_INC_DODGE);
        AttributeModifierPerk innerEx2 = new AttributeModifierPerk(key("armara_inner_4"), 52, 47)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE)
                .setName(NAME_INC_DODGE);
        tree(key("armara_dodge_bridge_3"))
                .chain(tree(innerEff1))
                .chain(tree(innerEff2))
                .chain(tree(key("travel_4")));
        tree(key("armara_dodge_bridge_1"))
                .chain(tree(innerEx1))
                .chain(tree(innerEx2))
                .chain(tree(key("travel_5")));

        AttributeModifierPerk armorDmg1 = new AttributeModifierPerk(key("armara_dmgarmor_1"), 51, 49)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR);
        AttributeModifierPerk armorDmg2 = new AttributeModifierPerk(key("armara_dmgarmor_2"), 52, 50)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR);
        KeyDamageArmor armorDmg3 = new KeyDamageArmor(key("armara_dmgarmor_3"), 51, 51)
                .setName(name("key.damage_armor"));
        tree(innerEx2)
                .chain(tree(armorDmg1))
                .chain(tree(armorDmg2))
                .chain(tree(armorDmg3));

        AttributeModifierPerk noKnockback1 = new AttributeModifierPerk(key("armara_noknockback_1"), 59, 41)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE);
        AttributeModifierPerk noKnockback2 = new AttributeModifierPerk(key("armara_noknockback_2"), 60, 40)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE);
        KeyNoKnockback noKnockback3 = new KeyNoKnockback(key("armara_noknockback_3"), 59, 39)
                .setName(name("key.no_knockback"));
        tree(innerEff1)
                .chain(tree(noKnockback1))
                .chain(tree(noKnockback2))
                .chain(tree(noKnockback3));

        AttributeModifierPerk maxCharge1 = new AttributeModifierPerk(key("amara_maxcharge_inner_1"), 53, 38)
                .addModifier(0.07F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(NAME_INC_CHARGE_MAX);
        AttributeModifierPerk maxCharge2 = new AttributeModifierPerk(key("amara_maxcharge_inner_2"), 54, 37)
                .addModifier(0.07F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(NAME_INC_CHARGE_MAX);
        MajorPerk maxCharge3 = new MajorPerk(key("amara_maxcharge_inner_3"), 55, 38)
                .addModifier(0.2F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("named.ample_semblance"));
        tree(innerEff2)
                .chain(tree(maxCharge1))
                .chain(tree(maxCharge2))
                .chain(tree(maxCharge3));

        AttributeModifierPerk armors1 = new AttributeModifierPerk(key("armara_armor_inner_1"), 57, 52)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_ARMOR_TOUGHNESS)
                .setName(name("hybrid.armor_armor_toughness"));
        AttributeModifierPerk armors2 = new AttributeModifierPerk(key("armara_armor_inner_2"), 56, 53)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_ARMOR_TOUGHNESS)
                .setName(name("hybrid.armor_armor_toughness"));
        MajorPerk armors3 = new MajorPerk(key("armara_armor_inner_3"), 55, 52)
                .addModifier(4F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_ARMOR_TOUGHNESS)
                .setName(name("named.tough"));
        tree(innerEx1)
                .chain(tree(armors1))
                .chain(tree(armors2))
                .chain(tree(armors3));
    }

    private static void initVicioInner() {
        AttributeModifierPerk innerEff1 = new AttributeModifierPerk(key("vicio_inner_1"), 42, 57)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED);
        AttributeModifierPerk innerEff2 = new AttributeModifierPerk(key("vicio_inner_2"), 43, 54)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED);
        AttributeModifierPerk innerEx1 = new AttributeModifierPerk(key("vicio_inner_3"), 38, 57)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED);
        AttributeModifierPerk innerEx2 = new AttributeModifierPerk(key("vicio_inner_4"), 39, 53)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED);
        tree(key("vicio_ats_bridge_3"))
                .chain(tree(innerEff1))
                .chain(tree(innerEff2))
                .chain(tree(key("travel_7")));
        tree(key("vicio_ats_bridge_1"))
                .chain(tree(innerEx1))
                .chain(tree(innerEx2))
                .chain(tree(key("travel_8")));

        AttributeModifierPerk foodConsumption1 = new AttributeModifierPerk(key("vicio_food_reduction_1"), 45, 58)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED);
        AttributeModifierPerk foodConsumption2 = new AttributeModifierPerk(key("vicio_food_reduction_2"), 46, 57)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED);
        KeyReducedFood foodConsumption3 = new KeyReducedFood(key("vicio_food_reduction_3"), 45, 56)
                .setName(name("key.reduced_food"));
        tree(innerEff1)
                .chain(tree(foodConsumption1))
                .chain(tree(foodConsumption2))
                .chain(tree(foodConsumption3));

        AttributeModifierPerk placeLights1 = new AttributeModifierPerk(key("vicio_place_lights_1"), 36, 54)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED);
        AttributeModifierPerk placeLights2 = new AttributeModifierPerk(key("vicio_place_lights_2"), 35, 53)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED);
        KeySpawnLights placeLights3 = new KeySpawnLights(key("vicio_place_lights_3"), 36, 52)
                .setName(name("key.place_lights"));
        tree(innerEx2)
                .chain(tree(placeLights1))
                .chain(tree(placeLights2))
                .chain(tree(placeLights3));

        AttributeModifierPerk ats1 = new AttributeModifierPerk(key("vicio_ats_inner_1"), 35, 58)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED);
        AttributeModifierPerk ats2 = new AttributeModifierPerk(key("vicio_ats_inner_2"), 34, 57)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED);
        MajorPerk ats3 = new MajorPerk(key("vicio_ats_inner_3"), 33, 58)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(name("named.zeal"));
        tree(innerEx1)
                .chain(tree(ats1))
                .chain(tree(ats2))
                .chain(tree(ats3));

        AttributeModifierPerk charge1 = new AttributeModifierPerk(key("vicio_charge_inner_1"), 45, 53)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(name("hybrid.charge_regen_mining"));
        AttributeModifierPerk charge2 = new AttributeModifierPerk(key("vicio_charge_inner_2"), 44, 52)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(name("hybrid.charge_regen_mining"));
        MajorPerk charge3 = new MajorPerk(key("vicio_charge_inner_3"), 45, 51)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(name("named.haste"));
        tree(innerEff2)
                .chain(tree(charge1))
                .chain(tree(charge2))
                .chain(tree(charge3));
    }

    private static void initAevitasInner() {
        AttributeModifierPerk innerEff1 = new AttributeModifierPerk(key("aevitas_inner_1"), 24, 49)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE);
        AttributeModifierPerk innerEff2 = new AttributeModifierPerk(key("aevitas_inner_2"), 28, 47)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE);
        AttributeModifierPerk innerEx1 = new AttributeModifierPerk(key("aevitas_inner_3"), 23, 43)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE);
        AttributeModifierPerk innerEx2 = new AttributeModifierPerk(key("aevitas_inner_4"), 27, 42)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE);
        tree(key("aevitas_life_bridge_3"))
                .chain(tree(innerEff1))
                .chain(tree(innerEff2))
                .chain(tree(key("travel_10")));
        tree(key("aevitas_life_bridge_1"))
                .chain(tree(innerEx1))
                .chain(tree(innerEx2))
                .chain(tree(key("travel_11")));

        AttributeModifierPerk cleanse1 = new AttributeModifierPerk(key("aevitas_cleanse_1"), 21, 41)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .setName(NAME_INC_LIFE_RECOVERY);
        AttributeModifierPerk cleanse2 = new AttributeModifierPerk(key("aevitas_cleanse_2"), 20, 40)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .setName(NAME_INC_LIFE_RECOVERY);
        KeyCleanseBadPotions cleanse3 = new KeyCleanseBadPotions(key("aevitas_cleanse_3"), 21, 39)
                .setName(name("key.cleansing"));
        tree(innerEx1)
                .chain(tree(cleanse1))
                .chain(tree(cleanse2))
                .chain(tree(cleanse3));

        AttributeModifierPerk plantGrowth1 = new AttributeModifierPerk(key("aevitas_plant_growth_1"), 23, 53)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(name("hybrid.reach_mining"));
        AttributeModifierPerk plantGrowth2 = new AttributeModifierPerk(key("aevitas_plant_growth_2"), 24, 54)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(name("hybrid.reach_mining"));
        KeyGrowables plantGrowth3 = new KeyGrowables(key("aevitas_plant_growth_3"), 25, 53)
                .setName(name("key.plant_growth"));
        tree(innerEff1)
                .chain(tree(plantGrowth1))
                .chain(tree(plantGrowth2))
                .chain(tree(plantGrowth3));

        AttributeModifierPerk charge1 = new AttributeModifierPerk(key("aevitas_inner_charge_1"), 29, 49)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("hybrid.charge_max_regen"));
        AttributeModifierPerk charge2 = new AttributeModifierPerk(key("aevitas_inner_charge_2"), 30, 50)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("hybrid.charge_max_regen"));
        MajorPerk charge3 = new MajorPerk(key("aevitas_inner_charge_3"), 31, 49)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .setName(name("named.stellar_vessel"));
        tree(innerEff2)
                .chain(tree(charge1))
                .chain(tree(charge2))
                .chain(tree(charge3));

        AttributeModifierPerk armorLife1 = new AttributeModifierPerk(key("aevitas_armor_life_1"), 26, 39)
                .addModifier(-0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_movespeed"));
        AttributeModifierPerk armorLife2 = new AttributeModifierPerk(key("aevitas_armor_life_2"), 27, 38)
                .addModifier(-0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_movespeed"));
        MajorPerk armorLife3 = new MajorPerk(key("aevitas_armor_life_3"), 28, 39)
                .addModifier(-0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(name("named.iron_heart"));
        tree(innerEx2)
                .chain(tree(armorLife1))
                .chain(tree(armorLife2))
                .chain(tree(armorLife3));
    }

    private static void initializeTravel() {
        float largeTravelEffect = 0.04F;
        float largeTravelExp    = 0.04F;

        createTravelNode(40, 29)
                .chain(createTravelNode(44, 30))
                .chain(createTravelNode(48, 33))
                .chain(createTravelNode(50, 36))
                .chain(createTravelNode(50, 40))
                .chain(createTravelNode(49, 45))
                .chain(createTravelNode(46, 49))
                .chain(createTravelNode(42, 50))
                .chain(createTravelNode(38, 50))
                .chain(createTravelNode(34, 49))
                .chain(createTravelNode(31, 45))
                .chain(createTravelNode(30, 40))
                .chain(createTravelNode(30, 36))
                .chain(createTravelNode(32, 33))
                .chain(createTravelNode(36, 30))
                .chain(tree(40, 29));

        AttributeModifierPerk evDsInnerEffect = new AttributeModifierPerk(key("ev_ds_effect_1"), 41, 25)
                .addModifier(largeTravelEffect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
        AttributeModifierPerk evDsInnerExp = new AttributeModifierPerk(key("ev_ds_exp_1"), 39, 23)
                .addModifier(largeTravelExp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP);
        AttributeModifierPerk evDsOuterEffect = new AttributeModifierPerk(key("ev_ds_effect_2"), 41, 14)
                .addModifier(largeTravelEffect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
        AttributeModifierPerk evDsOuterExp = new AttributeModifierPerk(key("ev_ds_exp_2"), 39, 16)
                .addModifier(largeTravelExp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP);
        tree(40, 29)
                .connect(tree(evDsInnerEffect))
                .connect(tree(evDsInnerExp));
        createTravelNode(40, 20)
                .connect(tree(evDsInnerEffect))
                .connect(tree(evDsInnerExp))
                .connect(tree(evDsOuterEffect))
                .connect(tree(evDsOuterExp))
                .connect(tree(key("discidia_c_ats_bridge_2")))
                .connect(tree(key("evorsio_c_armor_2")));
        createTravelNode(40, 10)
                .connect(tree(evDsOuterEffect))
                .connect(tree(evDsOuterExp));

        AttributeModifierPerk arDsInnerEffect = new AttributeModifierPerk(key("ar_ds_effect_1"), 55, 35)
                .addModifier(largeTravelEffect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
        AttributeModifierPerk arDsInnerExp = new AttributeModifierPerk(key("ar_ds_exp_1"), 53, 33)
                .addModifier(largeTravelExp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP);
        AttributeModifierPerk arDsOuterEffect = new AttributeModifierPerk(key("ar_ds_effect_2"), 66, 31)
                .addModifier(largeTravelEffect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
        AttributeModifierPerk arDsOuterExp = new AttributeModifierPerk(key("ar_ds_exp_2"), 64, 29)
                .addModifier(largeTravelExp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP);
        tree(50, 36)
                .connect(tree(arDsInnerEffect))
                .connect(tree(arDsInnerExp));
        createTravelNode(59, 32)
                .connect(tree(arDsInnerEffect))
                .connect(tree(arDsInnerExp))
                .connect(tree(arDsOuterEffect))
                .connect(tree(arDsOuterExp))
                .connect(tree(key("discidia_c_damage_2")))
                .connect(tree(key("armara_c_recovery_2")));
        createTravelNode(71, 28)
                .connect(tree(arDsOuterEffect))
                .connect(tree(arDsOuterExp));

        AttributeModifierPerk arViInnerEffect = new AttributeModifierPerk(key("ar_vi_effect_1"), 47, 54)
                .addModifier(largeTravelEffect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
        AttributeModifierPerk arViInnerExp = new AttributeModifierPerk(key("ar_vi_exp_1"), 49, 52)
                .addModifier(largeTravelExp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP);
        AttributeModifierPerk arViOuterEffect = new AttributeModifierPerk(key("ar_vi_effect_2"), 52, 64)
                .addModifier(largeTravelEffect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
        AttributeModifierPerk arViOuterExp = new AttributeModifierPerk(key("ar_vi_exp_2"), 54, 62)
                .addModifier(largeTravelExp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP);
        tree(46, 49)
                .connect(tree(arViInnerEffect))
                .connect(tree(arViInnerExp));
        createTravelNode(50, 58)
                .connect(tree(arViInnerEffect))
                .connect(tree(arViInnerExp))
                .connect(tree(arViOuterEffect))
                .connect(tree(arViOuterExp))
                .connect(tree(key("armara_c_movespeed_2")))
                .connect(tree(key("vicio_c_armor_2")));
        createTravelNode(55, 68)
                .connect(tree(arViOuterEffect))
                .connect(tree(arViOuterExp));

        AttributeModifierPerk aeViInnerEffect = new AttributeModifierPerk(key("ae_vi_effect_1"), 31, 52)
                .addModifier(largeTravelEffect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
        AttributeModifierPerk aeViInnerExp = new AttributeModifierPerk(key("ae_vi_exp_1"), 33, 54)
                .addModifier(largeTravelExp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP);
        AttributeModifierPerk aeViOuterEffect = new AttributeModifierPerk(key("ae_vi_effect_2"), 26, 62)
                .addModifier(largeTravelEffect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
        AttributeModifierPerk aeViOuterExp = new AttributeModifierPerk(key("ae_vi_exp_2"), 28, 64)
                .addModifier(largeTravelExp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP);
        tree(34, 49)
                .connect(tree(aeViInnerEffect))
                .connect(tree(aeViInnerExp));
        createTravelNode(30, 58)
                .connect(tree(aeViInnerEffect))
                .connect(tree(aeViInnerExp))
                .connect(tree(aeViOuterEffect))
                .connect(tree(aeViOuterExp))
                .connect(tree(key("vicio_c_mining_2")))
                .connect(tree(key("aevitas_c_reach_2")));
        createTravelNode(25, 68)
                .connect(tree(aeViOuterEffect))
                .connect(tree(aeViOuterExp));

        AttributeModifierPerk aeEvInnerEffect = new AttributeModifierPerk(key("ae_ev_effect_1"), 27, 33)
                .addModifier(largeTravelEffect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
        AttributeModifierPerk aeEvInnerExp = new AttributeModifierPerk(key("ae_ev_exp_1"), 25, 35)
                .addModifier(largeTravelExp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP);
        AttributeModifierPerk aeEvOuterEffect = new AttributeModifierPerk(key("ae_ev_effect_2"), 16, 29)
                .addModifier(largeTravelEffect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
        AttributeModifierPerk aeEvOuterExp = new AttributeModifierPerk(key("ae_ev_exp_2"), 14, 31)
                .addModifier(largeTravelExp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP);
        tree(30, 36)
                .connect(tree(aeEvInnerEffect))
                .connect(tree(aeEvInnerExp));
        createTravelNode(21, 32)
                .connect(tree(aeEvInnerEffect))
                .connect(tree(aeEvInnerExp))
                .connect(tree(aeEvOuterEffect))
                .connect(tree(aeEvOuterExp))
                .connect(tree(key("aevitas_c_armor_2")))
                .connect(tree(key("evorsio_c_mining_2")));
        createTravelNode(9, 28)
                .connect(tree(aeEvOuterEffect))
                .connect(tree(aeEvOuterExp));
    }

    private static void initializeRoots() {
        initAevitas();
        initVicio();
        initArmara();
        initDiscidia();
        initEvorsio();
    }

    private static void initEvorsio() {
        RootPerk rEvorsio = new RootEvorsio(key("evorsio"), 21, 14)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_REACH);

        AttributeModifierPerk r1EvorsioMeleeDamage = new AttributeModifierPerk(key("evorsio_damage_1"), 25, 16)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE);
        AttributeModifierPerk r2EvorsioMeleeDamage = new AttributeModifierPerk(key("evorsio_damage_2"), 27, 17)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE);
        AttributeModifierPerk r1EvorsioMiningSpeed = new AttributeModifierPerk(key("evorsio_mining_1"), 23, 18)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED);
        AttributeModifierPerk r2EvorsioMiningSpeed = new AttributeModifierPerk(key("evorsio_mining_2"), 24, 20)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED);

        MajorPerk mEvorsioDmgAts = new MajorPerk(key("evorsio_m_dmg_ats"), 30, 18)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(name("named.expertise"));
        MajorPerk mEvorsioMiningReach = new MajorPerk(key("evorsio_m_mining_reach"), 25, 23)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(name("named.tunneling"));

        GemSlotMajorPerk gemSocket = new GemSlotMajorPerk(key("evorsio_m_gem"), 28, 21);

        AttributeModifierPerk r1Bridge = new AttributeModifierPerk(key("evorsio_mining_bridge_1"), 31, 20)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED);
        AttributeModifierPerk r2Bridge = new AttributeModifierPerk(key("evorsio_mining_bridge_2"), 30, 23)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED);
        AttributeModifierPerk r3Bridge = new AttributeModifierPerk(key("evorsio_mining_bridge_3"), 27, 24)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED);

        AttributeModifierPerk r1EvorsioConnectArmor = new AttributeModifierPerk(key("evorsio_c_armor_1"), 33, 17)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR);
        AttributeModifierPerk r2EvorsioConnectArmor = new AttributeModifierPerk(key("evorsio_c_armor_2"), 37, 18)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR);

        AttributeModifierPerk r1EvorsioConnectMining = new AttributeModifierPerk(key("evorsio_c_mining_1"), 23, 26)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED);
        AttributeModifierPerk r2EvorsioConnectMining = new AttributeModifierPerk(key("evorsio_c_mining_2"), 22, 29)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED);

        rEvorsio.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);
        r1EvorsioMeleeDamage.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);
        r2EvorsioMeleeDamage.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);
        r1EvorsioMiningSpeed.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);
        r2EvorsioMiningSpeed.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);
        mEvorsioDmgAts.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);
        mEvorsioMiningReach.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);
        r1Bridge.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);
        r2Bridge.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);
        r3Bridge.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);
        r1EvorsioConnectArmor.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);
        r2EvorsioConnectArmor.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);
        r1EvorsioConnectMining.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);
        r2EvorsioConnectMining.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);

        tree(rEvorsio)
                .chain(tree(r1EvorsioMeleeDamage))
                .chain(tree(r2EvorsioMeleeDamage))
                .chain(tree(mEvorsioDmgAts));
        tree(rEvorsio)
                .chain(tree(r1EvorsioMiningSpeed))
                .chain(tree(r2EvorsioMiningSpeed))
                .chain(tree(mEvorsioMiningReach));
        tree(mEvorsioDmgAts)
                .chain(tree(r1Bridge))
                .chain(tree(r2Bridge))
                .chain(tree(r3Bridge))
                .chain(tree(mEvorsioMiningReach));

        tree(r2EvorsioMeleeDamage)
                .chain(tree(gemSocket))
                .chain(tree(r1Bridge));
        tree(r2EvorsioMiningSpeed)
                .chain(tree(gemSocket))
                .chain(tree(r3Bridge));

        tree(mEvorsioDmgAts)
                .chain(tree(r1EvorsioConnectArmor))
                .chain(tree(r2EvorsioConnectArmor));
        tree(mEvorsioMiningReach)
                .chain(tree(r1EvorsioConnectMining))
                .chain(tree(r2EvorsioConnectMining));
    }

    private static void initDiscidia() {
        RootPerk rDiscidia = new RootDiscidia(key("discidia"), 59, 14)
                .addModifier(1.1F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(1.1F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);

        AttributeModifierPerk r1DiscidiaProj = new AttributeModifierPerk(key("discidia_proj_1"), 57, 18)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE);
        AttributeModifierPerk r2DiscidiaProj = new AttributeModifierPerk(key("discidia_proj_2"), 56, 20)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE);
        AttributeModifierPerk r1DiscidiaMelee = new AttributeModifierPerk(key("discidia_melee_1"), 55, 16)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE);
        AttributeModifierPerk r2DiscidiaMelee = new AttributeModifierPerk(key("discidia_melee_2"), 53, 17)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE);

        MajorPerk mDiscidiaProjDmgSpeed = new MajorPerk(key("discidia_m_proj_dmg_speed"), 55, 23)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .addModifier(0.2F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_SPEED)
                .setName(name("named.deadly_draw"));
        MajorPerk mDiscidiaMeleeReach = new MajorPerk(key("discidia_m_melee_reach"), 50, 18)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(name("named.strong_arms"));

        GemSlotMajorPerk gemSocket = new GemSlotMajorPerk(key("discidia_m_gem"), 52, 21);

        AttributeModifierPerk r1Bridge = new AttributeModifierPerk(key("discidia_crit_bridge_1"), 53, 24)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_ADD_CRIT_CHANCE);
        AttributeModifierPerk r2Bridge = new AttributeModifierPerk(key("discidia_crit_bridge_2"), 50, 23)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_ADD_CRIT_CHANCE);
        AttributeModifierPerk r3Bridge = new AttributeModifierPerk(key("discidia_crit_bridge_3"), 49, 20)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_ADD_CRIT_CHANCE);

        AttributeModifierPerk r1DiscidiaConnectDmg = new AttributeModifierPerk(key("discidia_c_damage_1"), 57, 26)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(name("hybrid.melee_proj_dmg"));
        AttributeModifierPerk r2DiscidiaConnectDmg = new AttributeModifierPerk(key("discidia_c_damage_2"), 58, 29)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(name("hybrid.melee_proj_dmg"));

        AttributeModifierPerk r1DiscidiaConnectAts = new AttributeModifierPerk(key("discidia_c_ats_bridge_1"), 47, 17)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED);
        AttributeModifierPerk r2DiscidiaConnectAts = new AttributeModifierPerk(key("discidia_c_ats_bridge_2"), 43, 18)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED);

        rDiscidia.setRequireDiscoveredConstellation(ConstellationsAS.discidia);
        r1DiscidiaProj.setRequireDiscoveredConstellation(ConstellationsAS.discidia);
        r2DiscidiaProj.setRequireDiscoveredConstellation(ConstellationsAS.discidia);
        r1DiscidiaMelee.setRequireDiscoveredConstellation(ConstellationsAS.discidia);
        r2DiscidiaMelee.setRequireDiscoveredConstellation(ConstellationsAS.discidia);
        mDiscidiaProjDmgSpeed.setRequireDiscoveredConstellation(ConstellationsAS.discidia);
        mDiscidiaMeleeReach.setRequireDiscoveredConstellation(ConstellationsAS.discidia);
        r1Bridge.setRequireDiscoveredConstellation(ConstellationsAS.discidia);
        r2Bridge.setRequireDiscoveredConstellation(ConstellationsAS.discidia);
        r3Bridge.setRequireDiscoveredConstellation(ConstellationsAS.discidia);
        r1DiscidiaConnectDmg.setRequireDiscoveredConstellation(ConstellationsAS.discidia);
        r2DiscidiaConnectDmg.setRequireDiscoveredConstellation(ConstellationsAS.discidia);
        r1DiscidiaConnectAts.setRequireDiscoveredConstellation(ConstellationsAS.discidia);
        r2DiscidiaConnectAts.setRequireDiscoveredConstellation(ConstellationsAS.discidia);

        tree(rDiscidia)
                .chain(tree(r1DiscidiaProj))
                .chain(tree(r2DiscidiaProj))
                .chain(tree(mDiscidiaProjDmgSpeed));
        tree(rDiscidia)
                .chain(tree(r1DiscidiaMelee))
                .chain(tree(r2DiscidiaMelee))
                .chain(tree(mDiscidiaMeleeReach));
        tree(mDiscidiaProjDmgSpeed)
                .chain(tree(r1Bridge))
                .chain(tree(r2Bridge))
                .chain(tree(r3Bridge))
                .chain(tree(mDiscidiaMeleeReach));

        tree(r2DiscidiaProj)
                .chain(tree(gemSocket))
                .chain(tree(r1Bridge));
        tree(r2DiscidiaMelee)
                .chain(tree(gemSocket))
                .chain(tree(r3Bridge));

        tree(mDiscidiaProjDmgSpeed)
                .chain(tree(r1DiscidiaConnectDmg))
                .chain(tree(r2DiscidiaConnectDmg));
        tree(mDiscidiaMeleeReach)
                .chain(tree(r1DiscidiaConnectAts))
                .chain(tree(r2DiscidiaConnectAts));
    }

    private static void initArmara() {
        RootPerk rArmara = new RootArmara(key("armara"), 70, 51)
                .addModifier(1.20F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_ARMOR);

        AttributeModifierPerk r1ArmaraArmor = new AttributeModifierPerk(key("armara_armor_1"), 66, 52)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(NAME_ADD_ARMOR);
        AttributeModifierPerk r2ArmaraArmor = new AttributeModifierPerk(key("armara_armor_2"), 64, 51)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(NAME_ADD_ARMOR);
        AttributeModifierPerk r1ArmaraResist = new AttributeModifierPerk(key("armara_resist_1"), 68, 48)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_INC_ALL_RES);
        AttributeModifierPerk r2ArmaraResist = new AttributeModifierPerk(key("armara_resist_2"), 66, 47)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_INC_ALL_RES);

        MajorPerk mArmaraLifeArmor = new MajorPerk(key("armara_m_life_armor"), 61, 52)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(name("named.resilience"));
        MajorPerk mArmaraResistArmor = new MajorPerk(key("armara_m_resist_armor"), 64, 44)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(name("named.bulwark"));

        GemSlotMajorPerk gemSocket = new GemSlotMajorPerk(key("armara_m_gem"), 63, 48);

        AttributeModifierPerk r1Bridge = new AttributeModifierPerk(key("armara_dodge_bridge_1"), 60, 50)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE);
        AttributeModifierPerk r2Bridge = new AttributeModifierPerk(key("armara_dodge_bridge_2"), 59, 47)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE);
        AttributeModifierPerk r3Bridge = new AttributeModifierPerk(key("armara_dodge_bridge_3"), 61, 45)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE);

        AttributeModifierPerk r1ArmaraConnectMovespeed = new AttributeModifierPerk(key("armara_c_movespeed_1"), 58, 56)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED);
        AttributeModifierPerk r2ArmaraConnectMovespeed = new AttributeModifierPerk(key("armara_c_movespeed_2"), 54, 57)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED);

        AttributeModifierPerk r1ArmaraConnectRecovery = new AttributeModifierPerk(key("armara_c_recovery_1"), 63, 40)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .setName(NAME_INC_LIFE_RECOVERY);
        AttributeModifierPerk r2ArmaraConnectRecovery = new AttributeModifierPerk(key("armara_c_recovery_2"), 61, 36)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .setName(NAME_INC_LIFE_RECOVERY);

        rArmara.setRequireDiscoveredConstellation(ConstellationsAS.armara);
        r1ArmaraArmor.setRequireDiscoveredConstellation(ConstellationsAS.armara);
        r2ArmaraArmor.setRequireDiscoveredConstellation(ConstellationsAS.armara);
        r1ArmaraResist.setRequireDiscoveredConstellation(ConstellationsAS.armara);
        r2ArmaraResist.setRequireDiscoveredConstellation(ConstellationsAS.armara);
        mArmaraLifeArmor.setRequireDiscoveredConstellation(ConstellationsAS.armara);
        mArmaraResistArmor.setRequireDiscoveredConstellation(ConstellationsAS.armara);
        r1Bridge.setRequireDiscoveredConstellation(ConstellationsAS.armara);
        r2Bridge.setRequireDiscoveredConstellation(ConstellationsAS.armara);
        r3Bridge.setRequireDiscoveredConstellation(ConstellationsAS.armara);
        r1ArmaraConnectMovespeed.setRequireDiscoveredConstellation(ConstellationsAS.armara);
        r2ArmaraConnectMovespeed.setRequireDiscoveredConstellation(ConstellationsAS.armara);
        r1ArmaraConnectRecovery.setRequireDiscoveredConstellation(ConstellationsAS.armara);
        r2ArmaraConnectRecovery.setRequireDiscoveredConstellation(ConstellationsAS.armara);

        tree(rArmara)
                .chain(tree(r1ArmaraArmor))
                .chain(tree(r2ArmaraArmor))
                .chain(tree(mArmaraLifeArmor));
        tree(rArmara)
                .chain(tree(r1ArmaraResist))
                .chain(tree(r2ArmaraResist))
                .chain(tree(mArmaraResistArmor));
        tree(mArmaraLifeArmor)
                .chain(tree(r1Bridge))
                .chain(tree(r2Bridge))
                .chain(tree(r3Bridge))
                .chain(tree(mArmaraResistArmor));

        tree(r2ArmaraArmor)
                .chain(tree(gemSocket))
                .chain(tree(r1Bridge));
        tree(r2ArmaraResist)
                .chain(tree(gemSocket))
                .chain(tree(r3Bridge));

        tree(mArmaraLifeArmor)
                .chain(tree(r1ArmaraConnectMovespeed))
                .chain(tree(r2ArmaraConnectMovespeed));
        tree(mArmaraResistArmor)
                .chain(tree(r1ArmaraConnectRecovery))
                .chain(tree(r2ArmaraConnectRecovery));
    }

    private static void initVicio() {
        RootPerk rVicio = new RootVicio(key("vicio"), 40, 70)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);

        AttributeModifierPerk r1VicioReach = new AttributeModifierPerk(key("vicio_reach_1"), 38, 67)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH);
        AttributeModifierPerk r2VicioReach = new AttributeModifierPerk(key("vicio_reach_2"), 37, 65)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH);
        AttributeModifierPerk r1VicioDodge = new AttributeModifierPerk(key("vicio_dodge_1"), 42, 67)
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE);
        AttributeModifierPerk r2VicioDodge = new AttributeModifierPerk(key("vicio_dodge_2"), 43, 65)
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE);

        MajorPerk mVicioReachMovespeed = new MajorPerk(key("vicio_m_reach_movespeed"), 35, 63)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(name("named.swiftness"));
        MajorPerk mVicioDodgeMovespeed = new MajorPerk(key("vicio_m_dodge_movespeed"), 45, 63)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE)
                .setName(name("named.agility"));

        GemSlotMajorPerk gemSocket = new GemSlotMajorPerk(key("vicio_m_gem"), 40, 64);

        AttributeModifierPerk r1Bridge = new AttributeModifierPerk(key("vicio_ats_bridge_1"), 37, 61)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED);
        AttributeModifierPerk r2Bridge = new AttributeModifierPerk(key("vicio_ats_bridge_2"), 40, 59)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED);
        AttributeModifierPerk r3Bridge = new AttributeModifierPerk(key("vicio_ats_bridge_3"), 43, 61)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED);

        AttributeModifierPerk r1VicioConnectMining = new AttributeModifierPerk(key("vicio_c_mining_1"), 34, 60)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED);
        AttributeModifierPerk r2VicioConnectMining = new AttributeModifierPerk(key("vicio_c_mining_2"), 31, 61)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED);

        AttributeModifierPerk r1VicioConnectArmor = new AttributeModifierPerk(key("vicio_c_armor_1"), 46, 60)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR);
        AttributeModifierPerk r2VicioConnectArmor = new AttributeModifierPerk(key("vicio_c_armor_2"), 49, 61)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR);

        rVicio.setRequireDiscoveredConstellation(ConstellationsAS.vicio);
        r1VicioReach.setRequireDiscoveredConstellation(ConstellationsAS.vicio);
        r2VicioReach.setRequireDiscoveredConstellation(ConstellationsAS.vicio);
        r1VicioDodge.setRequireDiscoveredConstellation(ConstellationsAS.vicio);
        r2VicioDodge.setRequireDiscoveredConstellation(ConstellationsAS.vicio);
        mVicioReachMovespeed.setRequireDiscoveredConstellation(ConstellationsAS.vicio);
        mVicioDodgeMovespeed.setRequireDiscoveredConstellation(ConstellationsAS.vicio);
        r1Bridge.setRequireDiscoveredConstellation(ConstellationsAS.vicio);
        r2Bridge.setRequireDiscoveredConstellation(ConstellationsAS.vicio);
        r3Bridge.setRequireDiscoveredConstellation(ConstellationsAS.vicio);
        r1VicioConnectMining.setRequireDiscoveredConstellation(ConstellationsAS.vicio);
        r2VicioConnectMining.setRequireDiscoveredConstellation(ConstellationsAS.vicio);
        r1VicioConnectArmor.setRequireDiscoveredConstellation(ConstellationsAS.vicio);
        r2VicioConnectArmor.setRequireDiscoveredConstellation(ConstellationsAS.vicio);

        tree(rVicio)
                .chain(tree(r1VicioReach))
                .chain(tree(r2VicioReach))
                .chain(tree(mVicioReachMovespeed));
        tree(rVicio)
                .chain(tree(r1VicioDodge))
                .chain(tree(r2VicioDodge))
                .chain(tree(mVicioDodgeMovespeed));
        tree(mVicioReachMovespeed)
                .chain(tree(r1Bridge))
                .chain(tree(r2Bridge))
                .chain(tree(r3Bridge))
                .chain(tree(mVicioDodgeMovespeed));

        tree(r2VicioReach)
                .chain(tree(gemSocket))
                .chain(tree(r1Bridge));
        tree(r2VicioDodge)
                .chain(tree(gemSocket))
                .chain(tree(r3Bridge));

        tree(mVicioReachMovespeed)
                .chain(tree(r1VicioConnectMining))
                .chain(tree(r2VicioConnectMining));
        tree(mVicioDodgeMovespeed)
                .chain(tree(r1VicioConnectArmor))
                .chain(tree(r2VicioConnectArmor));
    }

    private static void initAevitas() {
        RootPerk rAevitas = new RootAevitas(key("aevitas"), 10, 51)
                .addModifier(2, ModifierType.ADDITION, ATTR_TYPE_HEALTH);

        AttributeModifierPerk r1AevitasLifeArmor = new AttributeModifierPerk(key("aevitas_life_armor_1"), 12, 48)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_armor"));
        AttributeModifierPerk r2AevitasLifeArmor = new AttributeModifierPerk(key("aevitas_life_armor_2"), 14, 47)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_armor"));
        AttributeModifierPerk r1AevitasLifeReach = new AttributeModifierPerk(key("aevitas_life_reach_1"), 14, 52)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_reach"));
        AttributeModifierPerk r2AevitasLifeReach = new AttributeModifierPerk(key("aevitas_life_reach_2"), 16, 51)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_reach"));

        MajorPerk mAevitasLifeArmor = new MajorPerk(key("aevitas_m_life_armor"), 16, 44)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .addModifier(1, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(name("named.thick_skin"));
        MajorPerk mAevitasLifeResist = new MajorPerk(key("aevitas_m_life_resist"), 19, 52)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .addModifier(1, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(name("named.melding"));

        GemSlotMajorPerk gemSocket = new GemSlotMajorPerk(key("aevitas_m_gem"), 17, 48);

        AttributeModifierPerk r1Bridge = new AttributeModifierPerk(key("aevitas_life_bridge_1"), 19, 45)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE);
        AttributeModifierPerk r2Bridge = new AttributeModifierPerk(key("aevitas_life_bridge_2"), 21, 47)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE);
        AttributeModifierPerk r3Bridge = new AttributeModifierPerk(key("aevitas_life_bridge_3"), 20, 50)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE);

        AttributeModifierPerk r1AevitasConnectArmor = new AttributeModifierPerk(key("aevitas_c_armor_1"), 17, 40)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR);
        AttributeModifierPerk r2AevitasConnectArmor = new AttributeModifierPerk(key("aevitas_c_armor_2"), 19, 36)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR);

        AttributeModifierPerk r1AevitasConnectReach = new AttributeModifierPerk(key("aevitas_c_reach_1"), 22, 56)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH);
        AttributeModifierPerk r2AevitasConnectReach = new AttributeModifierPerk(key("aevitas_c_reach_2"), 26, 57)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH);

        rAevitas.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);
        r1AevitasLifeArmor.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);
        r2AevitasLifeArmor.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);
        r1AevitasLifeReach.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);
        r2AevitasLifeReach.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);
        mAevitasLifeArmor.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);
        mAevitasLifeResist.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);
        r1Bridge.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);
        r2Bridge.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);
        r3Bridge.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);
        r1AevitasConnectArmor.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);
        r2AevitasConnectArmor.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);
        r1AevitasConnectReach.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);
        r2AevitasConnectReach.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);

        tree(rAevitas)
                .chain(tree(r1AevitasLifeArmor))
                .chain(tree(r2AevitasLifeArmor))
                .chain(tree(mAevitasLifeArmor));
        tree(rAevitas)
                .chain(tree(r1AevitasLifeReach))
                .chain(tree(r2AevitasLifeReach))
                .chain(tree(mAevitasLifeResist));
        tree(mAevitasLifeArmor)
                .chain(tree(r1Bridge))
                .chain(tree(r2Bridge))
                .chain(tree(r3Bridge))
                .chain(tree(mAevitasLifeResist));

        tree(r2AevitasLifeArmor)
                .chain(tree(gemSocket))
                .chain(tree(r1Bridge));
        tree(r2AevitasLifeReach)
                .chain(tree(gemSocket))
                .chain(tree(r3Bridge));

        tree(mAevitasLifeArmor)
                .chain(tree(r1AevitasConnectArmor))
                .chain(tree(r2AevitasConnectArmor));
        tree(mAevitasLifeResist)
                .chain(tree(r1AevitasConnectReach))
                .chain(tree(r2AevitasConnectReach));
    }

    private static PerkTree.PointConnector createTravelNode(int x, int y) {
        return createTravelNode(x, y, key(String.format("travel_%s", travelNodeCount++)));
    }

    private static PerkTree.PointConnector createTravelNode(int x, int y, ResourceLocation registryName) {
        AttributeModifierPerk node = new AttributeModifierPerk(registryName, x, y)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
        return tree(node);
    }

    private static <T extends AbstractPerk> PerkTree.PointConnector tree(T perk) {
        if (PERK_TREE.getPerk(perk.getRegistryName()) == null) {
            AstralSorcery.getProxy().getRegistryPrimer().register(perk);
            PERK_TREE.addPerk(perk);
        }
        return PERK_TREE.getConnector(perk);
    }

    private static PerkTree.PointConnector tree(float x, float y) {
        return PERK_TREE.getConnector(PERK_TREE.getPerk(x, y));
    }

    private static PerkTree.PointConnector tree(ResourceLocation key) {
        return PERK_TREE.getConnector(PERK_TREE.getPerk(key));
    }
}
