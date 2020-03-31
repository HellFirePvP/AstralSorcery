/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.PerkConverter;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
import hellfirepvp.astralsorcery.common.perk.modifier.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
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
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import static hellfirepvp.astralsorcery.AstralSorcery.key;
import static hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS.*;
import static hellfirepvp.astralsorcery.common.perk.PerkTree.PERK_TREE;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryPerks
 * Created by HellFirePvP
 * Date: 25.08.2019 / 19:15
 */
public class RegistryPerks {

    private RegistryPerks() {}

    public static void init() {
        initializeRoot();

        initializeAevitasRoot();
        initializeVicioRoot();
        initializeArmaraRoot();
        initializeDiscidiaRoot();
        initializeEvorsioRoot();

        initializeRootPerkWheel();
        initializePerkInteriorTravelWheel();
        initializePerkCore();

        initializeAevitasBranch();
        initializeVicioBranch();
        initializeArmaraBranch();
        initializeDiscidiaBranch();
        initializeEvorsioBranch();

        initializePerkExteriorTravelWheel();

        initializeAevitasKeyPerks();
        initializeEvorsioKeyPerks();
        initializeDiscidiaKeyPerks();
        initializeArmaraKeyPerks();
        initializeVicioKeyPerks();

        initializePerkEffectPerks();

        initializeOuterAevitasPerks();
        initializeOuterEvorsioPerks();
        initializeOuterDiscidiaPerks();
        initializeOuterArmaraPerks();
        initializeOuterVicioPerks();

        initializeMinorConstellationPerks();
        initializeTreeConnectorPerks();
    }

    private static <T extends AbstractPerk> PerkTree.PointConnector register(T perk) {
        AstralSorcery.getProxy().getRegistryPrimer().register(perk);
        PERK_TREE.addPerk(perk);
        return PERK_TREE.getConnector(perk);
    }

    private static void initializeMinorConstellationPerks() {
        AttributeModifierPerk perkGelu1 = new AttributeModifierPerk(key("gelu_inc_perkexp"), 23, 18);
        perkGelu1.addModifier(0.06F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        AttributeModifierPerk perkGelu2 = new AttributeModifierPerk(key("gelu_inc_perkexp_1"), 25, 16).setNameOverride(perkGelu1);
        perkGelu2.addModifier(0.06F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        KeyGelu gelu = new KeyGelu(key("focus_gelu"), 26, 19);

        register(perkGelu1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_def_3")));
        register(perkGelu2)
                .connect(perkGelu1);
        register(gelu)
                .connect(perkGelu2);

        perkGelu1.setRequireDiscoveredConstellation(ConstellationsAS.gelu);
        perkGelu2.setRequireDiscoveredConstellation(ConstellationsAS.gelu);
        gelu.setRequireDiscoveredConstellation(ConstellationsAS.gelu);

        AttributeModifierPerk perkUlteria1 = new AttributeModifierPerk(key("ulteria_more_perkexp"), -28, 15);
        perkUlteria1.addModifier(1.05F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        AttributeModifierPerk perkUlteria2 = new AttributeModifierPerk(key("ulteria_more_perkexp_1"), -26, 17).setNameOverride(perkUlteria1);
        perkUlteria2.addModifier(1.05F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        KeyUlteria ulteria = new KeyUlteria(key("focus_ulteria"), -29, 18);

        register(perkUlteria1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_life_2")));
        register(perkUlteria2)
                .connect(perkUlteria1);
        register(ulteria)
                .connect(perkUlteria2);

        perkUlteria1.setRequireDiscoveredConstellation(ConstellationsAS.ulteria);
        perkUlteria2.setRequireDiscoveredConstellation(ConstellationsAS.ulteria);
        ulteria.setRequireDiscoveredConstellation(ConstellationsAS.ulteria);

        AttributeModifierPerk perkVorux1 = new AttributeModifierPerk(key("vorux_inc_perkeff"), 14, -27);
        perkVorux1.addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkVorux2 = new AttributeModifierPerk(key("vorux_inc_perkeff_1"), 12, -29).setNameOverride(perkVorux1);
        perkVorux2.addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        KeyVorux vorux = new KeyVorux(key("focus_vorux"), 15, -30);

        register(perkVorux1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_dmg_2")));
        register(perkVorux2)
                .connect(perkVorux1);
        register(vorux)
                .connect(perkVorux2);

        perkVorux1.setRequireDiscoveredConstellation(ConstellationsAS.vorux);
        perkVorux2.setRequireDiscoveredConstellation(ConstellationsAS.vorux);
        vorux.setRequireDiscoveredConstellation(ConstellationsAS.vorux);

        AttributeModifierPerk perkAlcara1 = new AttributeModifierPerk(key("alcara_more_perkeff"), -25, -17);
        perkAlcara1.addModifier(1.04F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkAlcara2 = new AttributeModifierPerk(key("alcara_more_perkeff_1"), -27, -15).setNameOverride(perkAlcara1);
        perkAlcara2.addModifier(1.04F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        KeyAlcara alcara = new KeyAlcara(key("focus_alcara"), -28, -18);

        register(perkAlcara1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_mine_2")));
        register(perkAlcara2)
                .connect(perkAlcara1);
        register(alcara)
                .connect(perkAlcara2);

        perkAlcara1.setRequireDiscoveredConstellation(ConstellationsAS.alcara);
        perkAlcara2.setRequireDiscoveredConstellation(ConstellationsAS.alcara);
        alcara.setRequireDiscoveredConstellation(ConstellationsAS.alcara);
    }

    private static void initializeOuterVicioPerks() {
        float addedIncMsReach = 0.02F;

        AttributeModifierPerk perkVR1 = new AttributeModifierPerk(key("outer_s_inc_trv"), 13, 23);
        perkVR1.addModifier(addedIncMsReach, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        perkVR1.addModifier(addedIncMsReach, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        AttributeModifierPerk perkVR2 = new AttributeModifierPerk(key("outer_s_inc_trv_1"), 7, 26).setNameOverride(perkVR1);
        perkVR2.addModifier(addedIncMsReach, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        perkVR2.addModifier(addedIncMsReach, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        AttributeModifierPerk perkVR3 = new AttributeModifierPerk(key("outer_s_inc_trv_2"), 1, 29).setNameOverride(perkVR1);
        perkVR3.addModifier(addedIncMsReach, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        perkVR3.addModifier(addedIncMsReach, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        AttributeModifierPerk perkVR4 = new AttributeModifierPerk(key("outer_s_inc_trv_3"), -7, 27).setNameOverride(perkVR1);
        perkVR4.addModifier(addedIncMsReach, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        perkVR4.addModifier(addedIncMsReach, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        AttributeModifierPerk perkVR5 = new AttributeModifierPerk(key("outer_s_inc_trv_4"), -12, 24).setNameOverride(perkVR1);
        perkVR5.addModifier(addedIncMsReach, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        perkVR5.addModifier(addedIncMsReach, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH);

        register(perkVR1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_def_4")));
        register(perkVR2)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_12")))
                .connect(perkVR1);
        register(perkVR3)
                .connect(perkVR2);
        register(perkVR4)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_13")))
                .connect(perkVR3);
        register(perkVR5)
                .connect(perkVR4)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_life")));

        AttributeModifierPerk lssArmorLife1 = new AttributeModifierPerk(key("flight_life_armor"), 4, 30);
        lssArmorLife1.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        AttributeModifierPerk lssArmorLife2 = new AttributeModifierPerk(key("flight_life_armor_1"), 5, 31).setNameOverride(lssArmorLife1);
        lssArmorLife2.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        AttributeModifierPerk lssArmorLife3 = new AttributeModifierPerk(key("flight_life_armor_2"), 4, 32).setNameOverride(lssArmorLife1);
        lssArmorLife3.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        AttributeModifierPerk lssDodgeMs = new AttributeModifierPerk(key("flight_ms_dodge"), 5, 33);
        lssDodgeMs.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE);
        KeyMantleFlight mantleFlight = new KeyMantleFlight(key("key_mantle_flight"), 4, 34);

        register(lssArmorLife1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_trv_2")));
        register(lssArmorLife2)
                .connect(lssArmorLife1);
        register(lssArmorLife3)
                .connect(lssArmorLife2);
        register(lssDodgeMs)
                .connect(lssArmorLife3);
        register(mantleFlight)
                .connect(lssDodgeMs);

        AttributeModifierPerk atsReach1 = new AttributeModifierPerk(key("magnet_ats_reach"), -10, 23);
        atsReach1.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED);
        atsReach1.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        AttributeModifierPerk atsReach2 = new AttributeModifierPerk(key("magnet_ats_reach_1"), -9, 24).setNameOverride(atsReach1);
        atsReach2.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED);
        atsReach2.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        KeyMagnetDrops magnetDrops = new KeyMagnetDrops(key("key_magnet_drops"), -8, 23);

        register(atsReach1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_trv_4")));
        register(atsReach2)
                .connect(atsReach1);
        register(magnetDrops)
                .connect(atsReach2);
    }

    private static void initializeOuterArmaraPerks() {
        float addedIncArmorEle = 0.02F;

        AttributeModifierPerk perkDef1 = new AttributeModifierPerk(key("outer_s_inc_def"), 26, -5);
        perkDef1.addModifier(addedIncArmorEle, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        perkDef1.addModifier(addedIncArmorEle, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        AttributeModifierPerk perkDef2 = new AttributeModifierPerk(key("outer_s_inc_def_1"), 24, 1).setNameOverride(perkDef1);
        perkDef2.addModifier(addedIncArmorEle, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        perkDef2.addModifier(addedIncArmorEle, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        AttributeModifierPerk perkDef3 = new AttributeModifierPerk(key("outer_s_inc_def_2"), 26, 9).setNameOverride(perkDef1);
        perkDef3.addModifier(addedIncArmorEle, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        perkDef3.addModifier(addedIncArmorEle, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        AttributeModifierPerk perkDef4 = new AttributeModifierPerk(key("outer_s_inc_def_3"), 22, 15).setNameOverride(perkDef1);
        perkDef4.addModifier(addedIncArmorEle, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        perkDef4.addModifier(addedIncArmorEle, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        AttributeModifierPerk perkDef5 = new AttributeModifierPerk(key("outer_s_inc_def_4"), 20, 20).setNameOverride(perkDef1);
        perkDef5.addModifier(addedIncArmorEle, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        perkDef5.addModifier(addedIncArmorEle, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);

        register(perkDef1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_dmg_5")));
        register(perkDef2)
                .connect(perkDef1);
        register(perkDef3)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_7")))
                .connect(perkDef2);
        register(perkDef4)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_9")))
                .connect(perkDef3);
        register(perkDef5)
                .connect(perkDef4);

        AttributeModifierPerk perkPerkEffSlot1 = new AttributeModifierPerk(key("def_gem_path"), 15, 20);
        perkPerkEffSlot1.addModifier(1.02F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkPerkEffSlot2 = new AttributeModifierPerk(key("def_gem_path_1"), 14, 21).setNameOverride(perkPerkEffSlot1);
        perkPerkEffSlot2.addModifier(1.02F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        GemSlotMajorPerk perkDefGemSlot = new GemSlotMajorPerk(key("def_gem_slot"), 13, 20);

        register(perkPerkEffSlot1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_10")));
        register(perkPerkEffSlot2)
                .connect(perkPerkEffSlot1);
        register(perkDefGemSlot)
                .connect(perkPerkEffSlot2);

        AttributeModifierPerk perkIncLRedA1 = new AttributeModifierPerk(key("unwav_life_armor"), 27, 11);
        perkIncLRedA1.addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        perkIncLRedA1.addModifier(0.75F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_ARMOR);
        AttributeModifierPerk perkIncLRedA2 = new AttributeModifierPerk(key("unwav_life_armor_1"), 28, 12).setNameOverride(perkIncLRedA1);
        perkIncLRedA2.addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        perkIncLRedA2.addModifier(0.75F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_ARMOR);
        KeyNoKnockback noKnockBack = new KeyNoKnockback(key("key_no_knockback"), 29, 11);

        register(perkIncLRedA1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_def_2")));
        register(perkIncLRedA2)
                .connect(perkIncLRedA1);
        register(noKnockBack)
                .connect(perkIncLRedA2);

        AttributeModifierPerk perkIncArmor1 = new AttributeModifierPerk(key("bol_red_inc_armor"), 26, 2);
        perkIncArmor1.addModifier(0.06F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        AttributeModifierPerk perkIncArmor2 = new AttributeModifierPerk(key("bol_red_inc_armor_1"), 27, 3).setNameOverride(perkIncArmor1);
        perkIncArmor2.addModifier(0.06F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        AttributeModifierPerk perkIncArmor3 = new AttributeModifierPerk(key("bol_red_inc_armor_2"), 28, 2).setNameOverride(perkIncArmor1);
        perkIncArmor3.addModifier(0.06F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        KeyDamageArmor keyDamageArmor = new KeyDamageArmor(key("key_damage_armor"), 29, 3);

        register(perkIncArmor1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_def_1")));
        register(perkIncArmor2)
                .connect(perkIncArmor1);
        register(perkIncArmor3)
                .connect(perkIncArmor2);
        register(keyDamageArmor)
                .connect(perkIncArmor3);
    }

    private static void initializeOuterDiscidiaPerks() {
        float addedIncDmg = 0.02F;

        AttributeModifierPerk perkDmg1 = new AttributeModifierPerk(key("outer_s_inc_dmg"), -5, -27);
        perkDmg1.addModifier(addedIncDmg, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkDmg1.addModifier(addedIncDmg, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        AttributeModifierPerk perkDmg2 = new AttributeModifierPerk(key("outer_s_inc_dmg_1"), 3, -28).setNameOverride(perkDmg1);
        perkDmg2.addModifier(addedIncDmg, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkDmg2.addModifier(addedIncDmg, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        AttributeModifierPerk perkDmg3 = new AttributeModifierPerk(key("outer_s_inc_dmg_2"), 11, -25).setNameOverride(perkDmg1);
        perkDmg3.addModifier(addedIncDmg, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkDmg3.addModifier(addedIncDmg, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        AttributeModifierPerk perkDmg4 = new AttributeModifierPerk(key("outer_s_inc_dmg_3"), 19, -23).setNameOverride(perkDmg1);
        perkDmg4.addModifier(addedIncDmg, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkDmg4.addModifier(addedIncDmg, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        AttributeModifierPerk perkDmg5 = new AttributeModifierPerk(key("outer_s_inc_dmg_4"), 23, -17).setNameOverride(perkDmg1);
        perkDmg5.addModifier(addedIncDmg, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkDmg5.addModifier(addedIncDmg, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        AttributeModifierPerk perkDmg6 = new AttributeModifierPerk(key("outer_s_inc_dmg_5"), 25, -11).setNameOverride(perkDmg1);
        perkDmg6.addModifier(addedIncDmg, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkDmg6.addModifier(addedIncDmg, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);

        register(perkDmg1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_mine_4")));
        register(perkDmg2)
                .connect(perkDmg1);
        register(perkDmg3)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_3")))
                .connect(perkDmg2);
        register(perkDmg4)
                .connect(perkDmg3);
        register(perkDmg5)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_4")))
                .connect(perkDmg4);
        register(perkDmg6)
                .connect(perkDmg5);

        AttributeModifierPerk perkPerkEffSlot1 = new AttributeModifierPerk(key("dsc_gem_path"), 18, -19);
        perkPerkEffSlot1.addModifier(1.02F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkPerkEffSlot2 = new AttributeModifierPerk(key("dsc_gem_path_1"), 17, -21).setNameOverride(perkPerkEffSlot1);
        perkPerkEffSlot2.addModifier(1.02F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        GemSlotMajorPerk perkDscGemSlot = new GemSlotMajorPerk(key("dsc_gem_slot"), 15, -22);

        register(perkPerkEffSlot1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_dmg_3")));
        register(perkPerkEffSlot2)
                .connect(perkPerkEffSlot1);
        register(perkDscGemSlot)
                .connect(perkPerkEffSlot2);

        AttributeModifierPerk perkIncAts1 = new AttributeModifierPerk(key("inc_ats_ailm"), 9, -24);
        perkIncAts1.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED);
        AttributeModifierPerk perkIncAts2 = new AttributeModifierPerk(key("inc_ats_ailm_1"), 8, -23).setNameOverride(perkIncAts1);
        perkIncAts2.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED);
        KeyDamageEffects keyAilments = new KeyDamageEffects(key("key_ailments"), 7, -24);

        register(perkIncAts1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_dmg_2")));
        register(perkIncAts2)
                .connect(perkIncAts1);
        register(keyAilments)
                .connect(perkIncAts2);

        AttributeModifierPerk perkIncMs1 = new AttributeModifierPerk(key("inc_cull_ms"), 23, -10);
        perkIncMs1.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        AttributeModifierPerk perkIncMs2 = new AttributeModifierPerk(key("inc_cull_ms_1"), 22, -11).setNameOverride(perkIncMs1);
        perkIncMs2.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        AttributeModifierPerk perkIncCrit = new AttributeModifierPerk(key("inc_cull_crit"), 21, -10);
        perkIncCrit.addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE);
        KeyCullingAttack keyCull = new KeyCullingAttack(key("key_cull_attack"), 20, -11);

        register(perkIncMs1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_dmg_5")));
        register(perkIncMs2)
                .connect(perkIncMs1);
        register(perkIncCrit)
                .connect(perkIncMs2);
        register(keyCull)
                .connect(perkIncCrit);

        AttributeModifierPerk perkCrJ1 = new AttributeModifierPerk(key("crit_inc_chance_proj"), 2, -26);
        perkCrJ1.addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE);
        perkCrJ1.addModifier(0.2F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        AttributeModifierPerk perkCrJ2 = new AttributeModifierPerk(key("crit_inc_chance_proj_1"), 1, -25).setNameOverride(perkCrJ1);
        perkCrJ2.addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE);
        perkCrJ2.addModifier(0.2F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        MajorPerk perkProjMul = new MajorPerk(key("major_crit_proj"), 2, -24);
        perkProjMul.addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_MULTIPLIER);
        perkProjMul.addModifier(0.3F , ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);

        register(perkCrJ1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_dmg_1")));
        register(perkCrJ2)
                .connect(perkCrJ1);
        register(perkProjMul)
                .connect(perkCrJ2);

        AttributeModifierPerk perkCrM1 = new AttributeModifierPerk(key("crit_inc_chance_melee"), 25, -18);
        perkCrM1.addModifier(3F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE);
        perkCrM1.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        AttributeModifierPerk perkCrM2 = new AttributeModifierPerk(key("crit_inc_chance_melee_1"), 26, -19).setNameOverride(perkCrM1);
        perkCrM2.addModifier(3F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE);
        perkCrM2.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        MajorPerk perkMeleeMul = new MajorPerk(key("major_crit_melee"), 27, -18);
        perkMeleeMul.addModifier(0.25F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_MULTIPLIER);
        perkMeleeMul.addModifier(0.2F , ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);

        register(perkCrM1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_dmg_4")));
        register(perkCrM2)
                .connect(perkCrM1);
        register(perkMeleeMul)
                .connect(perkCrM2);
    }

    private static void initializeOuterEvorsioPerks() {
        float addedIncMining = 0.02F;

        AttributeModifierPerk perkMine1 = new AttributeModifierPerk(key("outer_s_inc_mine"), -29, -3);
        perkMine1.addModifier(addedIncMining, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkMine2 = new AttributeModifierPerk(key("outer_s_inc_mine_1"), -27, -9).setNameOverride(perkMine1);
        perkMine2.addModifier(addedIncMining, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkMine3 = new AttributeModifierPerk(key("outer_s_inc_mine_2"), -23, -15).setNameOverride(perkMine1);
        perkMine3.addModifier(addedIncMining, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkMine4 = new AttributeModifierPerk(key("outer_s_inc_mine_3"), -18, -19).setNameOverride(perkMine1);
        perkMine4.addModifier(addedIncMining, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkMine5 = new AttributeModifierPerk(key("outer_s_inc_mine_4"), -12, -24).setNameOverride(perkMine1);
        perkMine5.addModifier(addedIncMining, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);

        register(perkMine1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_life_4")));
        register(perkMine2)
                .connect(perkMine1);
        register(perkMine3)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_20")))
                .connect(perkMine2);
        register(perkMine4)
                .connect(perkMine3);
        register(perkMine5)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4")))
                .connect(perkMine4);

        AttributeModifierPerk perkPerkEffSlot1 = new AttributeModifierPerk(key("ev_gem_path"), -19, -16);
        perkPerkEffSlot1.addModifier(1.02F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkPerkEffSlot2 = new AttributeModifierPerk(key("ev_gem_path_1"), -18, -15).setNameOverride(perkPerkEffSlot1);
        perkPerkEffSlot2.addModifier(1.02F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        GemSlotMajorPerk perkEvGemSlot = new GemSlotMajorPerk(key("ev_gem_slot"), -17, -16);

        register(perkPerkEffSlot1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_mine_3")));
        register(perkPerkEffSlot2)
                .connect(perkPerkEffSlot1);
        register(perkEvGemSlot)
                .connect(perkPerkEffSlot2);

        AttributeModifierPerk perkReach1 = new AttributeModifierPerk(key("inc_reach_sweep"), -20, -20);
        perkReach1.addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        AttributeModifierPerk perkReach2 = new AttributeModifierPerk(key("inc_reach_sweep_1"), -19, -21).setNameOverride(perkReach1);
        perkReach2.addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        AttributeModifierPerk perkReach3 = new AttributeModifierPerk(key("inc_reach_sweep_2"), -20, -22).setNameOverride(perkReach1);
        perkReach3.addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        KeyAreaOfEffect aoeSweep = new KeyAreaOfEffect(key("key_sweep_range"), -19, -23);

        register(perkReach1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_mine_3")));
        register(perkReach2)
                .connect(perkReach1);
        register(perkReach3)
                .connect(perkReach2);
        register(aoeSweep)
                .connect(perkReach3);

        AttributeModifierPerk perkLessMine1 = new AttributeModifierPerk(key("less_hrv_speed"), -31, -4);
        perkLessMine1.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkLessMine2 = new AttributeModifierPerk(key("less_hrv_speed_1"), -32, -5).setNameOverride(perkLessMine1);
        perkLessMine2.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        KeyAddEnchantment addLuck = new KeyAddEnchantment(key("key_add_fortune"), -31, -6)
                .addEnchantment(Enchantments.FORTUNE, 1);

        register(perkLessMine1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_mine")));
        register(perkLessMine2)
                .connect(perkLessMine1);
        register(addLuck)
                .connect(perkLessMine2);
    }

    private static void initializeOuterAevitasPerks() {
        float addedIncLife = 0.02F;

        AttributeModifierPerk perkLife1 = new AttributeModifierPerk(key("outer_s_inc_life"), -17, 22);
        perkLife1.addModifier(addedIncLife, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        AttributeModifierPerk perkLife2 = new AttributeModifierPerk(key("outer_s_inc_life_1"), -20, 18).setNameOverride(perkLife1);
        perkLife2.addModifier(addedIncLife, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        AttributeModifierPerk perkLife3 = new AttributeModifierPerk(key("outer_s_inc_life_2"), -25, 14).setNameOverride(perkLife1);
        perkLife3.addModifier(addedIncLife, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        AttributeModifierPerk perkLife4 = new AttributeModifierPerk(key("outer_s_inc_life_3"), -26, 8).setNameOverride(perkLife1);
        perkLife4.addModifier(addedIncLife, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        AttributeModifierPerk perkLife5 = new AttributeModifierPerk(key("outer_s_inc_life_4"), -28, 2).setNameOverride(perkLife1);
        perkLife5.addModifier(addedIncLife, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);

        register(perkLife1);
        register(perkLife2)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_16")))
                .connect(perkLife1);
        register(perkLife3)
                .connect(perkLife2);
        register(perkLife4)
                .connect(perkLife3)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_17")));
        register(perkLife5)
                .connect(perkLife4);

        AttributeModifierPerk perkPerkEffSlot1 = new AttributeModifierPerk(key("life_gem_path"), -16, 15);
        perkPerkEffSlot1.addModifier(1.02F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkPerkEffSlot2 = new AttributeModifierPerk(key("life_gem_path_1"), -17, 17).setNameOverride(perkPerkEffSlot1);
        perkPerkEffSlot2.addModifier(1.02F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        GemSlotMajorPerk perkLifeGemSlot = new GemSlotMajorPerk(key("life_gem_slot"), -16, 19);

        register(perkPerkEffSlot1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_15")));
        register(perkPerkEffSlot2)
                .connect(perkPerkEffSlot1);
        register(perkLifeGemSlot)
                .connect(perkPerkEffSlot2);

        AttributeModifierPerk perkEffectMine1 = new AttributeModifierPerk(key("inc_mine_perk"), -16, 25);
        perkEffectMine1.addModifier(0.06F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkEffectMine2 = new AttributeModifierPerk(key("inc_mine_perk_1"), -17, 26).setNameOverride(perkEffectMine1);
        perkEffectMine2.addModifier(0.06F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        KeyVoidTrash voidTrash = new KeyVoidTrash(key("key_void_trash"), -16, 27);

        register(perkEffectMine1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_life")));
        register(perkEffectMine2)
                .connect(perkEffectMine1);
        register(voidTrash)
                .connect(perkEffectMine2);

        AttributeModifierPerk perkIncRec1 = new AttributeModifierPerk(key("inc_life_rec"), -28, 9);
        perkIncRec1.addModifier(0.12F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY);
        AttributeModifierPerk perkIncRec2 = new AttributeModifierPerk(key("inc_life_rec_1"), -29, 8).setNameOverride(perkIncRec1);
        perkIncRec2.addModifier(0.12F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY);
        KeyCleanseBadPotions cureBadEffects = new KeyCleanseBadPotions(key("key_rem_badeffects"), -30, 9);

        register(perkIncRec1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_life_3")));
        register(perkIncRec2)
                .connect(perkIncRec1);
        register(cureBadEffects)
                .connect(perkIncRec2);
    }

    private static void initializePerkEffectPerks() {
        float addedIncPerkEffect = 0.15F;
        float addedIncPerkExp = 0.2F;

        MajorPerk majorPerkEffect1 = new MajorPerk(key("major_perk_eff_nt"), 9, 9);
        majorPerkEffect1.addModifier(addedIncPerkEffect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        MajorPerk majorPerkEffect2 = new MajorPerk(key("major_perk_eff_nt_1"), 10, -4).setNameOverride(majorPerkEffect1);
        majorPerkEffect2.addModifier(addedIncPerkEffect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        MajorPerk majorPerkEffect3 = new MajorPerk(key("major_perk_eff_nt_2"), -3, -11).setNameOverride(majorPerkEffect1);
        majorPerkEffect3.addModifier(addedIncPerkEffect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        MajorPerk majorPerkEffect4 = new MajorPerk(key("major_perk_eff_nt_3"), -11, 0).setNameOverride(majorPerkEffect1);
        majorPerkEffect4.addModifier(addedIncPerkEffect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        MajorPerk majorPerkEffect5 = new MajorPerk(key("major_perk_eff_nt_4"), -5, 11).setNameOverride(majorPerkEffect1);
        majorPerkEffect5.addModifier(addedIncPerkEffect, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        register(majorPerkEffect1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_7")));
        register(majorPerkEffect2)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_4")));
        register(majorPerkEffect3)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_1")));
        register(majorPerkEffect4)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_13")));
        register(majorPerkEffect5)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_10")));

        MajorPerk majorPerkExp1 = new MajorPerk(key("major_perk_exp_nt"), 5, 11);
        majorPerkExp1.addModifier(addedIncPerkExp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        MajorPerk majorPerkExp2 = new MajorPerk(key("major_perk_exp_nt_1"), 12, 0).setNameOverride(majorPerkExp1);
        majorPerkExp2.addModifier(addedIncPerkExp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        MajorPerk majorPerkExp3 = new MajorPerk(key("major_perk_exp_nt_2"), 2, -11).setNameOverride(majorPerkExp1);
        majorPerkExp3.addModifier(addedIncPerkExp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        MajorPerk majorPerkExp4 = new MajorPerk(key("major_perk_exp_nt_3"), -10, -5).setNameOverride(majorPerkExp1);
        majorPerkExp4.addModifier(addedIncPerkExp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        MajorPerk majorPerkExp5 = new MajorPerk(key("major_perk_exp_nt_4"), -9, 9).setNameOverride(majorPerkExp1);
        majorPerkExp5.addModifier(addedIncPerkExp, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);

        register(majorPerkExp1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_7")));
        register(majorPerkExp2)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_4")));
        register(majorPerkExp3)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_1")));
        register(majorPerkExp4)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_13")));
        register(majorPerkExp5)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_10")));

        MajorPerk perkEE1 = new MajorPerk(key("major_inc_encheffect"), -2, -3);
        perkEE1.addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ENCH_EFFECT);
        MajorPerk perkEE2 = new MajorPerk(key("major_inc_encheffect_1"), -3, 1).setNameOverride(perkEE1);
        perkEE2.addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ENCH_EFFECT);

        register(perkEE1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t2_14")))
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t2_2")));
        register(perkEE2)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t2_11")))
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t2_14")));
    }

    private static void initializeVicioKeyPerks() {
        AttributeModifierPerk perkSwimSpeed1 = new AttributeModifierPerk(key("key_path_swim_conversion"), -2, 23);
        perkSwimSpeed1.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_SWIMSPEED);
        AttributeModifierPerk perkSwimSpeed2 = new AttributeModifierPerk(key("key_path_swim_conversion_1"), -3, 24).setNameOverride(perkSwimSpeed1);
        perkSwimSpeed2.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_SWIMSPEED);
        AttributeModifierPerk perkSwimSpeed3 = new AttributeModifierPerk(key("key_path_swim_conversion_2"), -2, 25).setNameOverride(perkSwimSpeed1);
        perkSwimSpeed3.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_SWIMSPEED);
        KeyPerk swimSpeedConversion = new KeyPerk(key("key_swim_conversion"), -3, 26);
        swimSpeedConversion.addConverter(new PerkConverter() {
            @Nonnull
            @Override
            public PerkAttributeModifier convertModifier(PlayerEntity player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable ModifierSource owningSource) {
                return modifier;
            }

            @Nonnull
            @Override
            public Collection<PerkAttributeModifier> gainExtraModifiers(PlayerEntity player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable ModifierSource owningSource) {
                List<PerkAttributeModifier> modifiers = Lists.newArrayList();
                if (modifier.getAttributeType().equals(ATTR_TYPE_MOVESPEED)) {
                    PerkAttributeModifier mod;
                    switch (modifier.getMode()) {
                        case ADDITION:
                        case ADDED_MULTIPLY:
                            mod = modifier.gainAsExtraModifier(this, ATTR_TYPE_SWIMSPEED, modifier.getMode(), modifier.getValue(player, progress) / 2F);
                            modifiers.add(mod);
                            break;
                        case STACKING_MULTIPLY:
                            float val = modifier.getValue(player, progress) - 1;
                            val /= 2F; //Halve the actual value
                            mod = modifier.gainAsExtraModifier(this, ATTR_TYPE_SWIMSPEED, modifier.getMode(), val + 1);
                            modifiers.add(mod);
                            break;
                        default:
                            break;
                    }
                }
                return modifiers;
            }
        });

        register(perkSwimSpeed1)
                .connect(PERK_TREE.getPerk(key("med_add_ats_dodge")));
        register(perkSwimSpeed2)
                .connect(perkSwimSpeed1);
        register(perkSwimSpeed3)
                .connect(perkSwimSpeed2);
        register(swimSpeedConversion)
                .connect(perkSwimSpeed3);

        AttributeModifierPerk incAttackSpeed1 = new AttributeModifierPerk(key("major_ats_inc_ats"), 9, 14);
        incAttackSpeed1.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED);
        AttributeModifierPerk incAttackSpeed2 = new AttributeModifierPerk(key("major_ats_inc_ats_1"), 8, 15).setNameOverride(incAttackSpeed1);
        incAttackSpeed2.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED);
        MajorPerk perkZeal = new MajorPerk(key("major_increased_ats_zeal"), 7, 14);
        perkZeal.addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED);

        register(incAttackSpeed1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_7")));
        register(incAttackSpeed2)
                .connect(incAttackSpeed1);
        register(perkZeal)
                .connect(incAttackSpeed2);

        AttributeModifierPerk incReachStepAssist = new AttributeModifierPerk(key("key_stepassist_path_reach"), -7, 15);
        incReachStepAssist.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        AttributeModifierPerk incMovespeedStepAssist1 = new AttributeModifierPerk(key("key_stepassist_path_movespeed"), -6, 18);
        incMovespeedStepAssist1.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        AttributeModifierPerk incMovespeedStepAssist2 = new AttributeModifierPerk(key("key_stepassist_path_movespeed_1"), -7, 17).setNameOverride(incMovespeedStepAssist1);
        incMovespeedStepAssist2.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        KeyStepAssist stepAssistKey = new KeyStepAssist(key("key_step_assist"), -6, 16);

        register(incReachStepAssist)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_10")));
        register(stepAssistKey)
                .connect(incReachStepAssist);
        register(incMovespeedStepAssist2)
                .connect(stepAssistKey);
        register(incMovespeedStepAssist1)
                .connect(incMovespeedStepAssist2)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_13")));

        AttributeModifierPerk lightsMs1 = new AttributeModifierPerk(key("key_lights_path_ms"), 6, 17);
        lightsMs1.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        AttributeModifierPerk lightsMs2 = new AttributeModifierPerk(key("key_lights_path_ms_1"), 7, 16).setNameOverride(lightsMs1);
        lightsMs2.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        KeySpawnLights spawnLightsKey = new KeySpawnLights(key("key_spawn_lights"), 6, 15);

        register(lightsMs1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_12")));
        register(lightsMs2)
                .connect(lightsMs1);
        register(spawnLightsKey)
                .connect(lightsMs2);

        AttributeModifierPerk redFoodPathDodge1 = new AttributeModifierPerk(key("key_redfood_path_dodge"), 3, 22);
        redFoodPathDodge1.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE);
        AttributeModifierPerk redFoodPathDodge2 = new AttributeModifierPerk(key("key_redfood_path_dodge_1"), 2, 23).setNameOverride(redFoodPathDodge1);
        redFoodPathDodge2.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE);
        AttributeModifierPerk redFoodPathDodge3 = new AttributeModifierPerk(key("key_redfood_path_dodge_2"), 3, 24).setNameOverride(redFoodPathDodge1);
        redFoodPathDodge3.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE);
        KeyReducedFood reducedFoodKey = new KeyReducedFood(key("key_reduced_food"), 4, 23);

        register(redFoodPathDodge1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_12")));
        register(redFoodPathDodge2)
                .connect(redFoodPathDodge1);
        register(redFoodPathDodge3)
                .connect(redFoodPathDodge2);
        register(reducedFoodKey)
                .connect(redFoodPathDodge3);

        AttributeModifierPerk perkVChargeReg1 = new AttributeModifierPerk(key("major_vic_charge_path_node"), 10, 20);
        perkVChargeReg1.addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION);
        AttributeModifierPerk perkVChargeReg2 = new AttributeModifierPerk(key("major_vic_charge_path_node_1"), 9, 21).setNameOverride(perkVChargeReg1);
        perkVChargeReg2.addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION);
        MajorPerk majorReachReg = new MajorPerk(key("major_vic_charge"), 10, 22);
        majorReachReg.addModifier(0.3F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION);
        majorReachReg.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH);

        register(perkVChargeReg1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_11")));
        register(perkVChargeReg2)
                .connect(perkVChargeReg1);
        register(majorReachReg)
                .connect(perkVChargeReg2);
    }

    private static void initializeArmaraKeyPerks() {
        AttributeModifierPerk perkALC1 = new AttributeModifierPerk(key("key_alc_inc_armor"), 15, -4);
        perkALC1.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        AttributeModifierPerk perkALC2 = new AttributeModifierPerk(key("key_alc_inc_armor_1"), 16, -3).setNameOverride(perkALC1);
        perkALC2.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        KeyPerk keyArmorConversion = new KeyPerk(key("key_armor_life_conversion"), 17, -4);
        keyArmorConversion.addConverter(new PerkConverter() {
            @Nonnull
            @Override
            public PerkAttributeModifier convertModifier(PlayerEntity player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable ModifierSource owningSource) {
                if (modifier.getAttributeType().equals(ATTR_TYPE_ARMOR)) {
                    return modifier.convertModifier(ATTR_TYPE_HEALTH, modifier.getMode(), modifier.getValue(player, progress));
                }
                return modifier;
            }

            @Nonnull
            @Override
            public Collection<PerkAttributeModifier> gainExtraModifiers(PlayerEntity player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable ModifierSource owningSource) {
                Collection<PerkAttributeModifier> modifiers = Lists.newArrayList();
                if (modifier.getAttributeType().equals(ATTR_TYPE_ARMOR)) {
                    PerkAttributeModifier mod;
                    mod = modifier.gainAsExtraModifier(this, ATTR_TYPE_ARMOR, ModifierType.STACKING_MULTIPLY, 0F);
                    modifiers.add(mod);
                    mod = modifier.gainAsExtraModifier(this, ATTR_TYPE_ARMOR_TOUGHNESS, ModifierType.STACKING_MULTIPLY, 0F);
                    modifiers.add(mod);
                }
                return modifiers;
            }
        });

        register(perkALC1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_4")));
        register(perkALC2)
                .connect(perkALC1);
        register(keyArmorConversion)
                .connect(perkALC2);

        AttributeModifierPerk perkTh1 = new AttributeModifierPerk(key("thorns_inc_dmg"), 16, 1);
        perkTh1.addModifier(5F, ModifierType.ADDITION, ATTR_TYPE_INC_THORNS);
        AttributeModifierPerk perkTh2 = new AttributeModifierPerk(key("thorns_inc_dmg_gr"), 17, 0);
        perkTh2.addModifier(10F, ModifierType.ADDITION, ATTR_TYPE_INC_THORNS);
        MajorPerk perkRangedThorns = new MajorPerk(key("thorns_ranged"), 16, -1);
        perkRangedThorns.addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_THORNS_RANGED);
        perkRangedThorns.addModifier(10F, ModifierType.ADDITION, ATTR_TYPE_INC_THORNS);

        register(perkTh1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_5")));
        register(perkTh2)
                .connect(perkTh1);
        register(perkRangedThorns)
                .connect(perkTh2);

        AttributeModifierPerk perkPhEle1 = new AttributeModifierPerk(key("key_phoenix_path"), 16, 16);
        perkPhEle1.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        AttributeModifierPerk perkPhEle2 = new AttributeModifierPerk(key("key_phoenix_path_1"), 17, 15).setNameOverride(perkPhEle1);
        perkPhEle2.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        AttributeModifierPerk perkPhEle3 = new AttributeModifierPerk(key("key_phoenix_path_2"), 18, 16).setNameOverride(perkPhEle1);
        perkPhEle3.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        KeyCheatDeath cheatDeathKey = new KeyCheatDeath(key("key_cheat_death"), 17, 17);

        register(perkPhEle1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_10")));
        register(perkPhEle2)
                .connect(perkPhEle1);
        register(perkPhEle3)
                .connect(perkPhEle2);
        register(cheatDeathKey)
                .connect(perkPhEle3);

        AttributeModifierPerk perkArmor1 = new AttributeModifierPerk(key("inc_added_armor"), 21, 5);
        perkArmor1.addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR);
        AttributeModifierPerk perkArmor2 = new AttributeModifierPerk(key("inc_added_armor_1"), 22, 4).setNameOverride(perkArmor1);
        perkArmor2.addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR);
        AttributeModifierPerk perkArmor3 = new AttributeModifierPerk(key("inc_added_armor_2"), 23, 5).setNameOverride(perkArmor1);
        perkArmor3.addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR);
        MajorPerk perkArmor4 = new MajorPerk(key("major_flat_armor"), 22, 6);
        perkArmor4.addModifier(6F, ModifierType.ADDITION, ATTR_TYPE_ARMOR);
        perkArmor4.addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_ARMOR_TOUGHNESS);

        register(perkArmor1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_7")));
        register(perkArmor2)
                .connect(perkArmor1);
        register(perkArmor3)
                .connect(perkArmor2);
        register(perkArmor4)
                .connect(perkArmor3);

        AttributeModifierPerk perkNoArmorP1 = new AttributeModifierPerk(key("key_no_armor_armor"), 12, 15);
        perkNoArmorP1.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        AttributeModifierPerk perkNoArmorP2 = new AttributeModifierPerk(key("key_no_armor_resist"), 11, 14);
        perkNoArmorP2.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        KeyNoArmor noArmorKey = new KeyNoArmor(key("key_no_armor"), 12, 13);

        register(perkNoArmorP1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_9")));
        register(perkNoArmorP2)
                .connect(perkNoArmorP1);
        register(noArmorKey)
                .connect(perkNoArmorP2);

        AttributeModifierPerk perkArmRegenP1 = new AttributeModifierPerk(key("key_charge_generation_balancing_path"), 21, 0);
        perkArmRegenP1.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION);
        AttributeModifierPerk perkArmRegenP2 = new AttributeModifierPerk(key("key_charge_generation_balancing_path_1"), 22, -1).setNameOverride(perkArmRegenP1);
        perkArmRegenP2.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION);
        KeyChargeBalancing keyBalance = new KeyChargeBalancing(key("key_charge_generation_balancing"), 21, -2);
        keyBalance.addModifier(0.6F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM);
        keyBalance.addModifier(0.2F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION);

        register(perkArmRegenP1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_6")));
        register(perkArmRegenP2)
                .connect(perkArmRegenP1);
        register(keyBalance)
                .connect(perkArmRegenP2);
    }

    private static void initializeDiscidiaKeyPerks() {
        KeyBleed bleedKey = new KeyBleed(key("key_bleed"), 15, -6);
        AttributeModifierPerk perkBl1 = new AttributeModifierPerk(key("key_bleed_inc_duration"), 16, -7);
        perkBl1.addModifier(0.3F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_BLEED_DURATION);
        AttributeModifierPerk perkBl2 = new AttributeModifierPerk(key("key_bleed_inc_duration_greater"), 15, -8);
        perkBl2.addModifier(0.4F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_BLEED_DURATION);

        register(bleedKey)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_3")));
        register(perkBl1)
                .connect(bleedKey);
        register(perkBl2)
                .connect(perkBl1);

        AttributeModifierPerk perkDst1 = new AttributeModifierPerk(key("key_dst_inc_dmg"), 17, -15);
        perkDst1.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        AttributeModifierPerk perkDst2 = new AttributeModifierPerk(key("key_dst_inc_dmg_2"), 18, -16).setNameOverride(perkDst1);
        perkDst2.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        KeyProjectileProximity projectileProximityKey = new KeyProjectileProximity(key("key_projectile_proximity"), 17, -17);

        register(perkDst1)
                .connect(PERK_TREE.getPerk(key("med_reach_arrowspeed")));
        register(perkDst2)
                .connect(perkDst1);
        register(projectileProximityKey)
                .connect(perkDst2);

        AttributeModifierPerk perkDst3 = new AttributeModifierPerk(key("key_dst_inc_dmg_3"), 13, -19).setNameOverride(perkDst1);
        perkDst3.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        AttributeModifierPerk perkDst4 = new AttributeModifierPerk(key("key_dst_inc_dmg_4"), 14, -20).setNameOverride(perkDst1);
        perkDst4.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        KeyProjectileDistance projectileDistanceKey = new KeyProjectileDistance(key("key_projectile_distance"), 15, -19);

        register(perkDst3)
                .connect(PERK_TREE.getPerk(key("med_reach_arrowspeed")));
        register(perkDst4)
                .connect(perkDst3);
        register(projectileDistanceKey)
                .connect(perkDst4);

        AttributeModifierPerk perkRPCrit = new AttributeModifierPerk(key("key_rampage_path_node_crit"), 4, -18);
        perkRPCrit.addModifier(3, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE);
        AttributeModifierPerk perkRPDmg = new AttributeModifierPerk(key("key_rampage_path_node_dmg"), 3, -15);
        perkRPDmg.addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkRPDmg.addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        AttributeModifierPerk perkRPDmg2 = new AttributeModifierPerk(key("key_rampage_path_node_dmg_1"), 4, -16).setNameOverride(perkRPDmg);
        perkRPDmg2.addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkRPDmg2.addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        KeyRampage rampageKey = new KeyRampage(key("key_rampage"), 3, -17);

        register(perkRPCrit)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_2")));
        register(rampageKey)
                .connect(perkRPCrit);
        register(perkRPDmg2)
                .connect(rampageKey);
        register(perkRPDmg)
                .connect(perkRPDmg2)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_1")));

        AttributeModifierPerk perkDChargeReg1 = new AttributeModifierPerk(key("key_chargereg_path_node"), 4, -23);
        perkDChargeReg1.addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION);
        AttributeModifierPerk perkDChargeReg2 = new AttributeModifierPerk(key("key_chargereg_path_node_1"), 5, -24).setNameOverride(perkDChargeReg1);
        perkDChargeReg2.addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION);
        MajorPerk perkChargeBeacon = new MajorPerk(key("major_dsc_charge"), 4, -25);
        perkChargeBeacon.addModifier(0.2F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION);
        perkChargeBeacon.addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM);

        register(perkDChargeReg1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_2")));
        register(perkDChargeReg2)
                .connect(perkDChargeReg1);
        register(perkChargeBeacon)
                .connect(perkDChargeReg2);


        AttributeModifierPerk perkLL1 = new AttributeModifierPerk(key("inc_leech_vamp"), -1, -15);
        perkLL1.addModifier(3F, ModifierType.ADDITION, ATTR_TYPE_ATTACK_LIFE_LEECH);
        AttributeModifierPerk perkLL2 = new AttributeModifierPerk(key("inc_leech_vamp_1"), -2, -16).setNameOverride(perkLL1);
        perkLL2.addModifier(3F, ModifierType.ADDITION, ATTR_TYPE_ATTACK_LIFE_LEECH);
        MajorPerk perkVampirism = new MajorPerk(key("major_leech_vamp"), -1, -17);
        perkVampirism.addModifier(5F, ModifierType.ADDITION, ATTR_TYPE_ATTACK_LIFE_LEECH);
        perkVampirism.addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_HEALTH);

        register(perkLL1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_1")));
        register(perkLL2)
                .connect(perkLL1);
        register(perkVampirism)
                .connect(perkLL2);

        AttributeModifierPerk perkFD1 = new AttributeModifierPerk(key("ds_inc_potion_duration"), 21, -6);
        perkFD1.addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_POTION_DURATION);
        AttributeModifierPerk perkFD2 = new AttributeModifierPerk(key("ds_inc_potion_duration_1"), 22, -5).setNameOverride(perkFD1);
        perkFD2.addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_POTION_DURATION);
        MajorPerk perkFD3 = new MajorPerk(key("major_ds_inc_potion_duration"), 23, -6);
        perkFD3.addModifier(0.3F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_POTION_DURATION);
        perkFD3.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);

        register(perkFD1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_5")));
        register(perkFD2)
                .connect(perkFD1);
        register(perkFD3)
                .connect(perkFD2);
    }

    private static void initializeEvorsioKeyPerks() {
        AttributeModifierPerk perkLL1 = new AttributeModifierPerk(key("key_lastbreath_path_node"), -7, -17);
        perkLL1.addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkLL2 = new AttributeModifierPerk(key("key_lastbreath_path_node_1"), -6, -16).setNameOverride(perkLL1);
        perkLL2.addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        KeyLastBreath lastBreathKey = new KeyLastBreath(key("key_lastbreath"), -5, -17);

        register(perkLL1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4")));
        register(perkLL2)
                .connect(perkLL1);
        register(lastBreathKey)
                .connect(perkLL2);

        AttributeModifierPerk perkDTM1 = new AttributeModifierPerk(key("key_digtypes_path_node_inc"), -15, -8);
        perkDTM1.addModifier(0.06F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkDTM2 = new AttributeModifierPerk(key("key_digtypes_path_node_inc_1"), -14, -7).setNameOverride(perkDTM1);
        perkDTM2.addModifier(0.06F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkDTM3 = new AttributeModifierPerk(key("key_digtypes_path_add"), -14, -5);
        perkDTM3.addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_HARVEST_SPEED);
        KeyDigTypes digTypesKey = new KeyDigTypes(key("key_digtypes"), -15, -6);

        register(perkDTM3)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_13")));
        register(digTypesKey)
                .connect(perkDTM3);
        register(perkDTM2)
                .connect(digTypesKey);
        register(perkDTM1)
                .connect(perkDTM2)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_20")));

        AttributeModifierPerk perkAD1 = new AttributeModifierPerk(key("key_disarm_path_node"), -16, -2);
        perkAD1.addModifier(1.05F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        AttributeModifierPerk perkAD2 = new AttributeModifierPerk(key("key_disarm_path_node_1"), -17, -1).setNameOverride(perkAD1);
        perkAD2.addModifier(1.05F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        AttributeModifierPerk perkAD3 = new AttributeModifierPerk(key("key_disarm_path_node_2"), -18, -2).setNameOverride(perkAD1);
        perkAD3.addModifier(1.05F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        KeyDisarm disarmKey = new KeyDisarm(key("key_disarm"), -17, -3);

        register(perkAD1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_13")));
        register(perkAD2)
                .connect(perkAD1);
        register(perkAD3)
                .connect(perkAD2)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_18")));
        register(disarmKey)
                .connect(perkAD3);


        AttributeModifierPerk perkACH1 = new AttributeModifierPerk(key("key_arc_chains"), -5, -24);
        perkACH1.addModifier(1, ModifierType.ADDITION, ATTR_TYPE_ARC_CHAINS);
        AttributeModifierPerk perkACH2 = new MajorPerk(key("key_arc_chains_major"), -6, -23);
        perkACH2.addModifier(2, ModifierType.ADDITION, ATTR_TYPE_ARC_CHAINS);
        AttributeModifierPerk perkACH3 = new AttributeModifierPerk(key("key_arc_chains_1"), -5, -22).setNameOverride(perkACH1);
        perkACH3.addModifier(1, ModifierType.ADDITION, ATTR_TYPE_ARC_CHAINS);
        KeyLightningArc arcKey = new KeyLightningArc(key("key_lightning_arc"), -2, -23);

        register(arcKey)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_1")));
        register(perkACH1)
                .connect(arcKey);
        register(perkACH2)
                .connect(perkACH1);
        register(perkACH3)
                .connect(perkACH2);

        AttributeModifierPerk perkChainL1 = new AttributeModifierPerk(key("key_chain_mining_length"), -21, -9);
        perkChainL1.addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_MINING_CHAIN_LENGTH);
        AttributeModifierPerk perkChainL2 = new AttributeModifierPerk(key("key_chain_mining_length_1"), -22, -10).setNameOverride(perkChainL1);
        perkChainL2.addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_MINING_CHAIN_LENGTH);
        MajorPerk perkChainL3 = new MajorPerk(key("key_chain_mining_length_greater"), -23, -12);
        perkChainL3.addModifier(3F, ModifierType.ADDITION, ATTR_TYPE_MINING_CHAIN_LENGTH);
        AttributeModifierPerk perkChanceL1 = new AttributeModifierPerk(key("key_chain_mining_chance"), -22, -6);
        perkChanceL1.addModifier(0.15F, ModifierType.ADDITION, ATTR_TYPE_MINING_CHAIN_CHANCE);
        AttributeModifierPerk perkChanceL2 = new AttributeModifierPerk(key("key_chain_mining_chance_1"), -23, -5).setNameOverride(perkChanceL1);
        perkChanceL2.addModifier(0.15F, ModifierType.ADDITION, ATTR_TYPE_MINING_CHAIN_CHANCE);
        MajorPerk perkChanceL3 = new MajorPerk(key("key_chain_mining_chance_greater"), -24, -3);
        perkChanceL3.addModifier(1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MINING_CHAIN_CHANCE);
        MajorPerk perkDoubleL1 = new MajorPerk(key("key_chain_mining_double"), -24, -8);
        perkDoubleL1.addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_MINING_CHAIN_SUCCESSIVECHAIN);
        KeyChainMining chainMiningKey = new KeyChainMining(key("key_chain_mining"), -20, -7);

        register(chainMiningKey)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_19")));
        register(perkChanceL1)
                .connect(chainMiningKey);
        register(perkChanceL2)
                .connect(perkChanceL1);
        register(perkChanceL3)
                .connect(perkChanceL2);
        register(perkChainL1)
                .connect(chainMiningKey);
        register(perkChainL2)
                .connect(perkChainL1);
        register(perkChainL3)
                .connect(perkChainL2);
        register(perkDoubleL1)
                .connect(perkChainL2)
                .connect(perkChanceL2);

        AttributeModifierPerk perkFD1 = new AttributeModifierPerk(key("ev_inc_potion_duration"), -15, -16);
        perkFD1.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_POTION_DURATION);
        AttributeModifierPerk perkFD2 = new AttributeModifierPerk(key("ev_inc_potion_duration_1"), -14, -17).setNameOverride(perkFD1);
        perkFD2.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_POTION_DURATION);
        MajorPerk perkFD3 = new MajorPerk(key("major_ev_inc_potion_duration"), -15, -18);
        perkFD3.addModifier(0.4F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_POTION_DURATION);
        perkFD3.addModifier(0.75F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_HEALTH);

        register(perkFD1)
                .connect(PERK_TREE.getPerk(key("med_added_hrv_speed")));
        register(perkFD2)
                .connect(perkFD1);
        register(perkFD3)
                .connect(perkFD2);
    }

    private static void initializeAevitasKeyPerks() {
        AttributeModifierPerk perkReachP1 = new AttributeModifierPerk(key("key_reach_path_node"), -12, 11);
        perkReachP1.addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        AttributeModifierPerk perkReachP2 = new AttributeModifierPerk(key("key_reach_path_node_1"), -11, 12).setNameOverride(perkReachP1);
        perkReachP2.addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        KeyReach reachKey = new KeyReach(key("key_reach"), -12, 13);
        reachKey.addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_REACH);
        reachKey.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);

        register(perkReachP1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_16")));
        register(perkReachP2)
                .connect(perkReachP1);
        register(reachKey)
                .connect(perkReachP2);

        AttributeModifierPerk perkSEP1 = new AttributeModifierPerk(key("key_enrich_path_node"), -18, 13);
        perkSEP1.addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkSEP2 = new AttributeModifierPerk(key("key_enrich_path_node_1"), -19, 12).setNameOverride(perkSEP1);
        perkSEP2.addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkSEP3 = new AttributeModifierPerk(key("key_enrich_path_node_2"), -20, 13).setNameOverride(perkSEP1);
        perkSEP3.addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        KeyStoneEnrichment stoneEnrichmentKey = new KeyStoneEnrichment(key("key_stone_enrichment"), -19, 14);

        register(perkSEP1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_16")));
        register(perkSEP2)
                .connect(perkSEP1);
        register(perkSEP3)
                .connect(perkSEP2);
        register(stoneEnrichmentKey)
                .connect(perkSEP3);

        AttributeModifierPerk perkMD1 = new AttributeModifierPerk(key("key_mending_path_node"), -21, 3);
        perkMD1.addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE);
        AttributeModifierPerk perkMD2 = new AttributeModifierPerk(key("key_mending_path_node_1"), -22, 4);
        perkMD2.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE);
        AttributeModifierPerk perkMD3 = new AttributeModifierPerk(key("key_mending_path_node_2"), -23, 3).setNameOverride(perkMD2);
        perkMD3.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE);
        KeyMending mendingKey = new KeyMending(key("key_mending"), -22, 2);

        register(perkMD1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_17")));
        register(perkMD2)
                .connect(perkMD1);
        register(perkMD3)
                .connect(perkMD2);
        register(mendingKey)
                .connect(perkMD3);

        AttributeModifierPerk perkGP1 = new AttributeModifierPerk(key("key_growables_path_node"), -9, 15);
        perkGP1.addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY);
        AttributeModifierPerk perkGP2 = new AttributeModifierPerk(key("key_growables_path_node_1"), -10, 14).setNameOverride(perkGP1);
        perkGP2.addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY);
        AttributeModifierPerk perkGP3 = new AttributeModifierPerk(key("key_growables_path_node_2"), -11, 15).setNameOverride(perkGP1);
        perkGP3.addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY);
        KeyGrowables growableKey = new KeyGrowables(key("key_growables"), -10, 16);

        register(perkGP1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_10")));
        register(perkGP2)
                .connect(perkGP1);
        register(perkGP3)
                .connect(perkGP2)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_15")));
        register(growableKey)
                .connect(perkGP3);

        AttributeModifierPerk perkAChargeReg1 = new AttributeModifierPerk(key("major_ae_charge_reg_path_node"), -20, 6);
        perkAChargeReg1.addModifier(0.25F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION);
        AttributeModifierPerk perkAChargeMaxReg1 = new AttributeModifierPerk(key("major_ae_charge_max_path_node"), -21, 7);
        perkAChargeMaxReg1.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION);
        perkAChargeMaxReg1.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM);
        MajorPerk perkChargeCap = new MajorPerk(key("major_aev_charge"), -20, 8);
        perkChargeCap.addModifier(0.3F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM);

        register(perkAChargeReg1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t4_17")));
        register(perkAChargeMaxReg1)
                .connect(perkAChargeReg1);
        register(perkChargeCap)
                .connect(perkAChargeMaxReg1);
    }

    private static void initializeTreeConnectorPerks() {
        float more_ch = 0.15F;

        AttributeModifierPerk perkEvorsioCh1 = new AttributeModifierPerk(key("threshold_evorsio"), -13, -27);
        perkEvorsioCh1.addModifier(more_ch, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        AttributeModifierPerk perkEvorsioCh2 = new AttributeModifierPerk(key("threshold_evorsio_1"), -15, -30).setNameOverride(perkEvorsioCh1);
        perkEvorsioCh2.addModifier(more_ch, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        AttributeModifierPerk perkEvorsioCh3 = new AttributeModifierPerk(key("threshold_evorsio_2"), -11, -30).setNameOverride(perkEvorsioCh1);
        perkEvorsioCh3.addModifier(more_ch, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        KeyTreeConnector thresholdEvorsio = new KeyTreeConnector(key("epi_evorsio"), -13, -29);

        register(perkEvorsioCh1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_mine_4")));
        register(perkEvorsioCh2)
                .connect(perkEvorsioCh1);
        register(perkEvorsioCh3)
                .connect(perkEvorsioCh2)
                .connect(perkEvorsioCh1);
        register(thresholdEvorsio)
                .connect(perkEvorsioCh1)
                .connect(perkEvorsioCh2)
                .connect(perkEvorsioCh3);

        AttributeModifierPerk perkArmaraCh1 = new AttributeModifierPerk(key("threshold_armara"), 29, -4);
        perkArmaraCh1.addModifier(more_ch, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        AttributeModifierPerk perkArmaraCh2 = new AttributeModifierPerk(key("threshold_armara_1"), 32, -2).setNameOverride(perkArmaraCh1);
        perkArmaraCh2.addModifier(more_ch, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        AttributeModifierPerk perkArmaraCh3 = new AttributeModifierPerk(key("threshold_armara_2"), 32, -6).setNameOverride(perkArmaraCh1);
        perkArmaraCh3.addModifier(more_ch, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        KeyTreeConnector thresholdArmara = new KeyTreeConnector(key("epi_armara"), 31, -4);

        register(perkArmaraCh1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_def")));
        register(perkArmaraCh2)
                .connect(perkArmaraCh1);
        register(perkArmaraCh3)
                .connect(perkArmaraCh2)
                .connect(perkArmaraCh1);
        register(thresholdArmara)
                .connect(perkArmaraCh1)
                .connect(perkArmaraCh2)
                .connect(perkArmaraCh3);

        AttributeModifierPerk perkVicioCh1 = new AttributeModifierPerk(key("threshold_vicio"), -6, 30);
        perkVicioCh1.addModifier(more_ch, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        AttributeModifierPerk perkVicioCh2 = new AttributeModifierPerk(key("threshold_vicio_1"), -4, 33).setNameOverride(perkVicioCh1);
        perkVicioCh2.addModifier(more_ch, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        AttributeModifierPerk perkVicioCh3 = new AttributeModifierPerk(key("threshold_vicio_2"), -8, 33).setNameOverride(perkVicioCh1);
        perkVicioCh3.addModifier(more_ch, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        KeyTreeConnector thresholdVicio = new KeyTreeConnector(key("epi_vicio"), -6, 32);

        register(perkVicioCh1)
                .connect(PERK_TREE.getPerk(key("outer_s_inc_trv_3")));
        register(perkVicioCh2)
                .connect(perkVicioCh1);
        register(perkVicioCh3)
                .connect(perkVicioCh2)
                .connect(perkVicioCh1);
        register(thresholdVicio)
                .connect(perkVicioCh1)
                .connect(perkVicioCh2)
                .connect(perkVicioCh3);
    }

    private static void initializePerkExteriorTravelWheel() {
        float inc_t4 = 0.03F;

        // Evorsio -> Discidia
        AttributeModifierPerk perkEff1 = new AttributeModifierPerk(key("base_inc_perkeffect_t4"), -10, -18);
        perkEff1.addModifier(inc_t4, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff2 = new AttributeModifierPerk(key("base_inc_perkeffect_t4_1"), -3, -20).setNameOverride(perkEff1);
        perkEff2.addModifier(inc_t4, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff3 = new AttributeModifierPerk(key("base_inc_perkeffect_t4_2"), 5, -21).setNameOverride(perkEff1);
        perkEff3.addModifier(inc_t4, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff4 = new AttributeModifierPerk(key("base_inc_perkeffect_t4_3"), 9, -18).setNameOverride(perkEff1);
        perkEff4.addModifier(inc_t4, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        register(perkEff1)
                .connect(PERK_TREE.getPerk(key("med_added_hrv_speed")))
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3")));
        register(perkEff2)
                .connect(perkEff1);
        register(perkEff3)
                .connect(perkEff2);
        register(perkEff4)
                .connect(perkEff3)
                .connect(PERK_TREE.getPerk(key("med_reach_arrowspeed")))
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_2")));

        // Discidia -> Armara
        AttributeModifierPerk perkEff5 = new AttributeModifierPerk(key("base_inc_perkeffect_t4_4"), 17, -12).setNameOverride(perkEff1);
        perkEff5.addModifier(inc_t4, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff6 = new AttributeModifierPerk(key("base_inc_perkeffect_t4_5"), 19, -7).setNameOverride(perkEff1);
        perkEff6.addModifier(inc_t4, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff7 = new AttributeModifierPerk(key("base_inc_perkeffect_t4_6"), 18, -1).setNameOverride(perkEff1);
        perkEff7.addModifier(inc_t4, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff8 = new AttributeModifierPerk(key("base_inc_perkeffect_t4_7"), 19, 6).setNameOverride(perkEff1);
        perkEff8.addModifier(inc_t4, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        register(perkEff5)
                .connect(PERK_TREE.getPerk(key("med_reach_arrowspeed")))
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_3")));
        register(perkEff6)
                .connect(perkEff5);
        register(perkEff7)
                .connect(perkEff6);
        register(perkEff8)
                .connect(perkEff7)
                .connect(PERK_TREE.getPerk(key("med_more_res")))
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_5")));

        // Armara -> Vicio
        AttributeModifierPerk perkEff9 = new AttributeModifierPerk(key("base_inc_perkeffect_t4_9"), 15, 13).setNameOverride(perkEff1);
        perkEff9.addModifier(inc_t4, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff10 = new AttributeModifierPerk(key("base_inc_perkeffect_t4_10"), 14, 17).setNameOverride(perkEff1);
        perkEff10.addModifier(inc_t4, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff11 = new AttributeModifierPerk(key("base_inc_perkeffect_t4_11"), 9, 18).setNameOverride(perkEff1);
        perkEff11.addModifier(inc_t4, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff12 = new AttributeModifierPerk(key("base_inc_perkeffect_t4_12"), 5, 19).setNameOverride(perkEff1);
        perkEff12.addModifier(inc_t4, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        register(perkEff9)
                .connect(PERK_TREE.getPerk(key("med_more_res")))
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_6")));
        register(perkEff10)
                .connect(perkEff9);
        register(perkEff11)
                .connect(perkEff10);
        register(perkEff12)
                .connect(perkEff11)
                .connect(PERK_TREE.getPerk(key("med_add_ats_dodge")))
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_8")));

        // Vicio -> Aevitas
        AttributeModifierPerk perkEff13 = new AttributeModifierPerk(key("base_inc_perkeffect_t4_13"), -5, 20).setNameOverride(perkEff1);
        perkEff13.addModifier(inc_t4, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff14 = new AttributeModifierPerk(key("base_inc_perkeffect_t4_14"), -9, 19).setNameOverride(perkEff1);
        perkEff14.addModifier(inc_t4, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff15 = new AttributeModifierPerk(key("base_inc_perkeffect_t4_15"), -14, 17).setNameOverride(perkEff1);
        perkEff15.addModifier(inc_t4, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff16 = new AttributeModifierPerk(key("base_inc_perkeffect_t4_16"), -16, 12).setNameOverride(perkEff1);
        perkEff16.addModifier(inc_t4, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        register(perkEff13)
                .connect(PERK_TREE.getPerk(key("med_add_ats_dodge")))
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_9")));
        register(perkEff14)
                .connect(perkEff13);
        register(perkEff15)
                .connect(perkEff14);
        register(perkEff16)
                .connect(perkEff15)
                .connect(PERK_TREE.getPerk(key("med_add_life")))
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_11")));

        // Aevitas -> Evorsio
        AttributeModifierPerk perkEff17 = new AttributeModifierPerk(key("base_inc_perkeffect_t4_17"), -19, 4).setNameOverride(perkEff1);
        perkEff17.addModifier(inc_t4, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff18 = new AttributeModifierPerk(key("base_inc_perkeffect_t4_18"), -20, -1).setNameOverride(perkEff1);
        perkEff18.addModifier(inc_t4, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff19 = new AttributeModifierPerk(key("base_inc_perkeffect_t4_19"), -18, -6).setNameOverride(perkEff1);
        perkEff19.addModifier(inc_t4, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff20 = new AttributeModifierPerk(key("base_inc_perkeffect_t4_20"), -16, -10).setNameOverride(perkEff1);
        perkEff20.addModifier(inc_t4, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        register(perkEff17)
                .connect(PERK_TREE.getPerk(key("med_add_life")))
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_12")));
        register(perkEff18)
                .connect(perkEff17);
        register(perkEff19)
                .connect(perkEff18);
        register(perkEff20)
                .connect(perkEff19)
                .connect(PERK_TREE.getPerk(key("med_added_hrv_speed")))
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_14")));
    }

    private static void initializeEvorsioBranch() {
        AttributeModifierPerk perkM1 = new AttributeModifierPerk(key("med_inc_hrv_speed"), -11, -11);
        perkM1.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkM2 = new AttributeModifierPerk(key("med_inc_hrv_speed_1"), -13, -11).setNameOverride(perkM1);
        perkM2.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);

        AttributeModifierPerk perkHrvAts = new MajorPerk(key("not_evo_hrv_ats"), -12, -10);
        perkHrvAts.addModifier(1.1F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        perkHrvAts.addModifier(1.1F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_ATTACK_SPEED);

        AttributeModifierPerk perkS1 = new AttributeModifierPerk(key("med_inc_hrv_speed_2"), -10, -13).setNameOverride(perkM1);
        perkS1.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkS2 = new AttributeModifierPerk(key("med_inc_hrv_speed_3"), -12, -13).setNameOverride(perkM1);
        perkS2.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);

        AttributeModifierPerk perkHrvReach = new MajorPerk(key("not_evo_hrv_reach"), -11, -14);
        perkHrvReach.addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        perkHrvReach.addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH);

        AttributeModifierPerk perkAddedHrvSpeed = new MajorPerk(key("med_added_hrv_speed"), -14, -14);
        perkAddedHrvSpeed.addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_HARVEST_SPEED);
        perkAddedHrvSpeed.addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH);

        register(perkM1)
                .connect(PERK_TREE.getPerk(key("major_inc_harvest")));
        register(perkHrvAts)
                .connect(perkM1);
        register(perkM2)
                .connect(perkHrvAts);

        register(perkS1)
                .connect(PERK_TREE.getPerk(key("major_inc_harvest")));
        register(perkHrvReach)
                .connect(perkS1);
        register(perkS2)
                .connect(perkHrvReach);

        register(perkAddedHrvSpeed)
                .connect(perkS2)
                .connect(perkM2);
    }

    private static void initializeDiscidiaBranch() {
        AttributeModifierPerk perkP1 = new AttributeModifierPerk(key("med_inc_proj_damage"), 11, -12);
        perkP1.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        AttributeModifierPerk perkP2 = new AttributeModifierPerk(key("med_inc_proj_damage_1"), 13, -12).setNameOverride(perkP1);
        perkP2.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);

        AttributeModifierPerk perkProjCrit = new MajorPerk(key("not_dsc_proj_crit"), 12, -11);
        perkProjCrit.addModifier(1.05F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        perkProjCrit.addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE);

        AttributeModifierPerk perkM1 = new AttributeModifierPerk(key("med_inc_melee_damage"), 10, -14);
        perkM1.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        AttributeModifierPerk perkM2 = new AttributeModifierPerk(key("med_inc_melee_damage_1"), 12, -14).setNameOverride(perkM1);
        perkM2.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);

        AttributeModifierPerk perkMeleeMulti = new MajorPerk(key("not_dsc_melee_multi"), 11, -15);
        perkMeleeMulti.addModifier(1.05F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkMeleeMulti.addModifier(0.3F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_MULTIPLIER);

        AttributeModifierPerk perkReachProjSpeed = new MajorPerk(key("med_reach_arrowspeed"), 14, -16);
        perkReachProjSpeed.addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_REACH);
        perkReachProjSpeed.addModifier(1.15F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_PROJ_SPEED);

        register(perkP1)
                .connect(PERK_TREE.getPerk(key("major_inc_damage")));
        register(perkProjCrit)
                .connect(perkP1);
        register(perkP2)
                .connect(perkProjCrit);

        register(perkM1)
                .connect(PERK_TREE.getPerk(key("major_inc_damage")));
        register(perkMeleeMulti)
                .connect(perkM1);
        register(perkM2)
                .connect(perkMeleeMulti);

        register(perkReachProjSpeed)
                .connect(perkP2)
                .connect(perkM2);
    }

    private static void initializeArmaraBranch() {
        AttributeModifierPerk perkAr1 = new AttributeModifierPerk(key("med_inc_armor"), 14, 8);
        perkAr1.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        AttributeModifierPerk perkAr2 = new AttributeModifierPerk(key("med_inc_armor_1"), 14, 10).setNameOverride(perkAr1);
        perkAr2.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);

        AttributeModifierPerk perkArmorDodge = new MajorPerk(key("not_arm_armor_dodge"), 13, 9);
        perkArmorDodge.addModifier(1.05F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_ARMOR);
        perkArmorDodge.addModifier(3F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE);

        AttributeModifierPerk perkRes1 = new AttributeModifierPerk(key("med_inc_resist"), 16, 7);
        perkRes1.addModifier(0.06F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        AttributeModifierPerk perkRes2 = new AttributeModifierPerk(key("med_inc_resist_1"), 16, 9).setNameOverride(perkRes1);
        perkRes2.addModifier(0.06F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);

        AttributeModifierPerk perkResistLife = new MajorPerk(key("not_arm_res_life"), 17, 8);
        perkResistLife.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        perkResistLife.addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH);

        AttributeModifierPerk perkResArmor = new MajorPerk(key("med_more_res"), 18, 11);
        perkResArmor.addModifier(1.06F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        perkResArmor.addModifier(1.06F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_ARMOR);

        register(perkAr1)
                .connect(PERK_TREE.getPerk(key("major_inc_armor")));
        register(perkArmorDodge)
                .connect(perkAr1);
        register(perkAr2)
                .connect(perkArmorDodge);

        register(perkRes1)
                .connect(PERK_TREE.getPerk(key("major_inc_armor")));
        register(perkResistLife)
                .connect(perkRes1);
        register(perkRes2)
                .connect(perkResistLife);

        register(perkResArmor)
                .connect(perkAr2)
                .connect(perkRes2);
    }

    private static void initializeVicioBranch() {
        AttributeModifierPerk perkM1 = new AttributeModifierPerk(key("med_inc_ms"), 1, 16);
        perkM1.addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        AttributeModifierPerk perkM2 = new AttributeModifierPerk(key("med_inc_ms_1"), 1, 18).setNameOverride(perkM1);
        perkM2.addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);

        AttributeModifierPerk perkDodgeMs = new MajorPerk(key("not_vic_dodge_ms"), 2, 17);
        perkDodgeMs.addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        perkDodgeMs.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE);

        AttributeModifierPerk perkM3 = new AttributeModifierPerk(key("mec_inc_ms_2"), -1, 17).setNameOverride(perkM1);
        perkM3.addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        AttributeModifierPerk perkM4 = new AttributeModifierPerk(key("mec_inc_ms_3"), -1, 19).setNameOverride(perkM1);
        perkM4.addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);

        AttributeModifierPerk perkAtsMs = new MajorPerk(key("not_vic_ats"), -2, 18);
        perkAtsMs.addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED);

        AttributeModifierPerk perkAddAts = new MajorPerk(key("med_add_ats_dodge"), 0, 21);
        perkAddAts.addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ATTACK_SPEED);
        perkAddAts.addModifier(5F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE);

        register(perkM1)
                .connect(PERK_TREE.getPerk(key("major_inc_ms_fs")));
        register(perkDodgeMs)
                .connect(perkM1);
        register(perkM2)
                .connect(perkDodgeMs);

        register(perkM3)
                .connect(PERK_TREE.getPerk(key("major_inc_ms_fs")));
        register(perkAtsMs)
                .connect(perkM3);
        register(perkM4)
                .connect(perkAtsMs);

        register(perkAddAts)
                .connect(perkM2)
                .connect(perkM4);
    }

    private static void initializeAevitasBranch() {
        AttributeModifierPerk perkL1 = new AttributeModifierPerk(key("med_inc_life"), -15, 6);
        perkL1.addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        AttributeModifierPerk perkL2 = new AttributeModifierPerk(key("med_inc_life_1"), -17, 6).setNameOverride(perkL1);
        perkL2.addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);

        AttributeModifierPerk perkArmorLife = new MajorPerk(key("not_aev_armor_life"), -16, 5);
        perkArmorLife.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        perkArmorLife.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);

        AttributeModifierPerk perkL3 = new AttributeModifierPerk(key("med_inc_life_2"), -14, 8).setNameOverride(perkL1);
        perkL3.addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        AttributeModifierPerk perkL4 = new AttributeModifierPerk(key("med_inc_life_3"), -16, 8).setNameOverride(perkL1);
        perkL4.addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);

        AttributeModifierPerk perkAllResLife = new MajorPerk(key("not_aev_res_life"), -15, 9);
        perkAllResLife.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        perkAllResLife.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);

        AttributeModifierPerk perkAddLife = new MajorPerk(key("med_add_life"), -18, 9);
        perkAddLife.addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_HEALTH);

        register(perkL1)
                .connect(PERK_TREE.getPerk(key("major_inc_life")));
        register(perkArmorLife)
                .connect(perkL1);
        register(perkL2)
                .connect(perkArmorLife);

        register(perkL3)
                .connect(PERK_TREE.getPerk(key("major_inc_life")));
        register(perkAllResLife)
                .connect(perkL3);
        register(perkL4)
                .connect(perkAllResLife);

        register(perkAddLife)
                .connect(perkL2)
                .connect(perkL4);
    }

    //Registers T1 perk-effect perks
    private static void initializePerkCore() {
        float inc_t1 = 0.07F;

        AttributeModifierPerk perkEff1 = new AttributeModifierPerk(key("base_inc_perkeffect_t1"), 1, -2);
        perkEff1.addModifier(inc_t1, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff2 = new AttributeModifierPerk(key("base_inc_perkeffect_t1_1"), 2, 1).setNameOverride(perkEff1);
        perkEff2.addModifier(inc_t1, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff3 = new AttributeModifierPerk(key("base_inc_perkeffect_t1_2"), -1, 2).setNameOverride(perkEff1);
        perkEff3.addModifier(inc_t1, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff4 = new AttributeModifierPerk(key("base_inc_perkeffect_t1_3"), -2, -1).setNameOverride(perkEff1);
        perkEff4.addModifier(inc_t1, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        KeyCore core = new KeyCore();

        register(perkEff1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t2_2")));
        register(perkEff2)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t2_5")))
                .connect(perkEff1);
        register(perkEff3)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t2_8")))
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t2_11")))
                .connect(perkEff2);
        register(perkEff4)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t2_14")))
                .connect(perkEff3)
                .connect(perkEff1);
        register(core)
                .connect(perkEff1)
                .connect(perkEff3);
    }

    //Registers T2 perk-effect perks
    private static void initializePerkInteriorTravelWheel() {
        float inc_t2 = 0.05F;

        AttributeModifierPerk perkEffectEvDis1 = new AttributeModifierPerk(key("base_inc_perkeffect_t2"), -1, -10);
        perkEffectEvDis1.addModifier(inc_t2, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectEvDis2 = new AttributeModifierPerk(key("base_inc_perkeffect_t2_1"), 1, -8).setNameOverride(perkEffectEvDis1);
        perkEffectEvDis2.addModifier(inc_t2, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectEvDis3 = new AttributeModifierPerk(key("base_inc_perkeffect_t2_2"), 0, -5).setNameOverride(perkEffectEvDis1);
        perkEffectEvDis3.addModifier(inc_t2, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectDisArm1 = new AttributeModifierPerk(key("base_inc_perkeffect_t2_3"), 10, -2).setNameOverride(perkEffectEvDis1);
        perkEffectDisArm1.addModifier(inc_t2, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectDisArm2 = new AttributeModifierPerk(key("base_inc_perkeffect_t2_4"), 7, -3).setNameOverride(perkEffectEvDis1);
        perkEffectDisArm2.addModifier(inc_t2, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectDisArm3 = new AttributeModifierPerk(key("base_inc_perkeffect_t2_5"), 4, -1).setNameOverride(perkEffectEvDis1);
        perkEffectDisArm3.addModifier(inc_t2, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectArmVic1 = new AttributeModifierPerk(key("base_inc_perkeffect_t2_6"), 6, 9).setNameOverride(perkEffectEvDis1);
        perkEffectArmVic1.addModifier(inc_t2, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectArmVic2 = new AttributeModifierPerk(key("base_inc_perkeffect_t2_7"), 4, 6).setNameOverride(perkEffectEvDis1);
        perkEffectArmVic2.addModifier(inc_t2, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectArmVic3 = new AttributeModifierPerk(key("base_inc_perkeffect_t2_8"), 1, 4).setNameOverride(perkEffectEvDis1);
        perkEffectArmVic3.addModifier(inc_t2, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectVicAev1 = new AttributeModifierPerk(key("base_inc_perkeffect_t2_9"), -6, 9).setNameOverride(perkEffectEvDis1);
        perkEffectVicAev1.addModifier(inc_t2, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectVicAev2 = new AttributeModifierPerk(key("base_inc_perkeffect_t2_10"), -5, 5).setNameOverride(perkEffectEvDis1);
        perkEffectVicAev2.addModifier(inc_t2, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectVicAev3 = new AttributeModifierPerk(key("base_inc_perkeffect_t2_11"), -2, 4).setNameOverride(perkEffectEvDis1);
        perkEffectVicAev3.addModifier(inc_t2, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectAevEv1 = new AttributeModifierPerk(key("base_inc_perkeffect_t2_12"), -10, -2).setNameOverride(perkEffectEvDis1);
        perkEffectAevEv1.addModifier(inc_t2, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectAevEv2 = new AttributeModifierPerk(key("base_inc_perkeffect_t2_13"), -7, -1).setNameOverride(perkEffectEvDis1);
        perkEffectAevEv2.addModifier(inc_t2, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectAevEv3 = new AttributeModifierPerk(key("base_inc_perkeffect_t2_14"), -5, -2).setNameOverride(perkEffectEvDis1);
        perkEffectAevEv3.addModifier(inc_t2, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        register(perkEffectEvDis1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_1")));
        register(perkEffectEvDis2)
                .connect(perkEffectEvDis1);
        register(perkEffectEvDis3)
                .connect(perkEffectEvDis2);

        register(perkEffectDisArm1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_4")));
        register(perkEffectDisArm2)
                .connect(perkEffectDisArm1);
        register(perkEffectDisArm3)
                .connect(perkEffectDisArm2);

        register(perkEffectArmVic1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_7")));
        register(perkEffectArmVic2)
                .connect(perkEffectArmVic1);
        register(perkEffectArmVic3)
                .connect(perkEffectArmVic2);

        register(perkEffectVicAev1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_10")));
        register(perkEffectVicAev2)
                .connect(perkEffectVicAev1);
        register(perkEffectVicAev3)
                .connect(perkEffectVicAev2);

        register(perkEffectAevEv1)
                .connect(PERK_TREE.getPerk(key("base_inc_perkeffect_t3_13")));
        register(perkEffectAevEv2)
                .connect(perkEffectAevEv1);
        register(perkEffectAevEv3)
                .connect(perkEffectAevEv2);
    }

    //Registers T3 perk-effect perks
    private static void initializeRootPerkWheel() {
        float inc_t3 = 0.04F;

        AttributeModifierPerk perkEffectEvDis1 = new AttributeModifierPerk(key("base_inc_perkeffect_t3"), -5, -12);
        perkEffectEvDis1.addModifier(inc_t3, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectEvDis2 = new AttributeModifierPerk(key("base_inc_perkeffect_t3_1"), 0, -13).setNameOverride(perkEffectEvDis1);
        perkEffectEvDis2.addModifier(inc_t3, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectEvDis3 = new AttributeModifierPerk(key("base_inc_perkeffect_t3_2"), 5, -12).setNameOverride(perkEffectEvDis1);
        perkEffectEvDis3.addModifier(inc_t3, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectDisArm1 = new AttributeModifierPerk(key("base_inc_perkeffect_t3_3"), 11, -7).setNameOverride(perkEffectEvDis1);
        perkEffectDisArm1.addModifier(inc_t3, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectDisArm2 = new AttributeModifierPerk(key("base_inc_perkeffect_t3_4"), 13, -3).setNameOverride(perkEffectEvDis1);
        perkEffectDisArm2.addModifier(inc_t3, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectDisArm3 = new AttributeModifierPerk(key("base_inc_perkeffect_t3_5"), 14, 2).setNameOverride(perkEffectEvDis1);
        perkEffectDisArm3.addModifier(inc_t3, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectArmVic1 = new AttributeModifierPerk(key("base_inc_perkeffect_t3_6"), 11, 9).setNameOverride(perkEffectEvDis1);
        perkEffectArmVic1.addModifier(inc_t3, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectArmVic2 = new AttributeModifierPerk(key("base_inc_perkeffect_t3_7"), 8, 12).setNameOverride(perkEffectEvDis1);
        perkEffectArmVic2.addModifier(inc_t3, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectArmVic3 = new AttributeModifierPerk(key("base_inc_perkeffect_t3_8"), 4, 13).setNameOverride(perkEffectEvDis1);
        perkEffectArmVic3.addModifier(inc_t3, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectVicAev1 = new AttributeModifierPerk(key("base_inc_perkeffect_t3_9"), -3, 13).setNameOverride(perkEffectEvDis1);
        perkEffectVicAev1.addModifier(inc_t3, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectVicAev2 = new AttributeModifierPerk(key("base_inc_perkeffect_t3_10"), -8, 12).setNameOverride(perkEffectEvDis1);
        perkEffectVicAev2.addModifier(inc_t3, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectVicAev3 = new AttributeModifierPerk(key("base_inc_perkeffect_t3_11"), -11, 9).setNameOverride(perkEffectEvDis1);
        perkEffectVicAev3.addModifier(inc_t3, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectAevEv1 = new AttributeModifierPerk(key("base_inc_perkeffect_t3_12"), -14, 2).setNameOverride(perkEffectEvDis1);
        perkEffectAevEv1.addModifier(inc_t3, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectAevEv2 = new AttributeModifierPerk(key("base_inc_perkeffect_t3_13"), -13, -3).setNameOverride(perkEffectEvDis1);
        perkEffectAevEv2.addModifier(inc_t3, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectAevEv3 = new AttributeModifierPerk(key("base_inc_perkeffect_t3_14"), -11, -7).setNameOverride(perkEffectEvDis1);
        perkEffectAevEv3.addModifier(inc_t3, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        register(perkEffectEvDis1).connect(PERK_TREE.getPerk(key("major_inc_harvest")));
        register(perkEffectEvDis2).connect(perkEffectEvDis1);
        register(perkEffectEvDis3).connect(perkEffectEvDis2).connect(PERK_TREE.getPerk(key("major_inc_damage")));

        register(perkEffectDisArm1).connect(PERK_TREE.getPerk(key("major_inc_damage")));
        register(perkEffectDisArm2).connect(perkEffectDisArm1);
        register(perkEffectDisArm3).connect(perkEffectDisArm2).connect(PERK_TREE.getPerk(key("major_inc_armor")));

        register(perkEffectArmVic1).connect(PERK_TREE.getPerk(key("major_inc_armor")));
        register(perkEffectArmVic2).connect(perkEffectArmVic1);
        register(perkEffectArmVic3).connect(perkEffectArmVic2).connect(PERK_TREE.getPerk(key("major_inc_ms_fs")));

        register(perkEffectVicAev1).connect(PERK_TREE.getPerk(key("major_inc_ms_fs")));
        register(perkEffectVicAev2).connect(perkEffectVicAev1);
        register(perkEffectVicAev3).connect(perkEffectVicAev2).connect(PERK_TREE.getPerk(key("major_inc_life")));

        register(perkEffectAevEv1).connect(PERK_TREE.getPerk(key("major_inc_life")));
        register(perkEffectAevEv2).connect(perkEffectAevEv1);
        register(perkEffectAevEv3).connect(perkEffectAevEv2).connect(PERK_TREE.getPerk(key("major_inc_harvest")));
    }

    private static void initializeEvorsioRoot() {
        AttributeModifierPerk breakRoot1 = new AttributeModifierPerk(key("base_inc_harvest"), -7, -7);
        breakRoot1.addModifier(0.10F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk breakRoot2 = new AttributeModifierPerk(key("base_inc_harvest_1"), -6, -9).setNameOverride(breakRoot1);
        breakRoot2.addModifier(0.10F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        MajorPerk evorsio = new MajorPerk(key("major_inc_harvest"), -9, -10);
        evorsio.addModifier(1.05F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        evorsio.addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);

        PerkTree.PointConnector ctHarvest1 = register(breakRoot1);
        PerkTree.PointConnector ctHarvest2 = register(breakRoot2);
        PerkTree.PointConnector ctMajorHarvest = register(evorsio);

        breakRoot1.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);
        breakRoot2.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);
        evorsio.setRequireDiscoveredConstellation(ConstellationsAS.evorsio);

        ctHarvest1.connect(PERK_TREE.getRootPerk(ConstellationsAS.evorsio));
        ctHarvest2.connect(ctHarvest1);
        ctMajorHarvest.connect(ctHarvest2);
    }

    private static void initializeDiscidiaRoot() {
        AttributeModifierPerk dmgRoot1 = new AttributeModifierPerk(key("base_inc__melee_damage"), 7, -7);
        dmgRoot1.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        AttributeModifierPerk dmgRoot2 = new AttributeModifierPerk(key("base_inc_proj_damage"), 6, -9);
        dmgRoot2.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        MajorPerk discidia = new MajorPerk(key("major_inc_damage"), 9, -10);
        discidia.addModifier(1.05F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        discidia.addModifier(1.05F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        discidia.addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE);

        PerkTree.PointConnector ctDamage1 = register(dmgRoot1);
        PerkTree.PointConnector ctDamage2 = register(dmgRoot2);
        PerkTree.PointConnector ctMajorDamage = register(discidia);

        dmgRoot1.setRequireDiscoveredConstellation(ConstellationsAS.discidia);
        dmgRoot2.setRequireDiscoveredConstellation(ConstellationsAS.discidia);
        discidia.setRequireDiscoveredConstellation(ConstellationsAS.discidia);

        ctDamage1.connect(PERK_TREE.getRootPerk(ConstellationsAS.discidia));
        ctDamage2.connect(ctDamage1);
        ctMajorDamage.connect(ctDamage2);
    }

    private static void initializeArmaraRoot() {
        AttributeModifierPerk armorRoot1 = new AttributeModifierPerk(key("base_inc_armor"), 9, 3);
        armorRoot1.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        AttributeModifierPerk armorRoot2 = new AttributeModifierPerk(key("base_inc_armor_1"), 8, 5).setNameOverride(armorRoot1);
        armorRoot2.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        MajorPerk armara = new MajorPerk(key("major_inc_armor"), 12, 6);
        armara.addModifier(1.20F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_ARMOR);

        PerkTree.PointConnector ctArmor1 = register(armorRoot1);
        PerkTree.PointConnector ctArmor2 = register(armorRoot2);
        PerkTree.PointConnector ctMajorArmor = register(armara);

        armorRoot1.setRequireDiscoveredConstellation(ConstellationsAS.armara);
        armorRoot2.setRequireDiscoveredConstellation(ConstellationsAS.armara);
        armara.setRequireDiscoveredConstellation(ConstellationsAS.armara);

        ctArmor1.connect(PERK_TREE.getRootPerk(ConstellationsAS.armara));
        ctArmor2.connect(ctArmor1);
        ctMajorArmor.connect(ctArmor2);
    }

    private static void initializeVicioRoot() {
        AttributeModifierPerk moveRoot1 = new AttributeModifierPerk(key("base_inc_ms"), 1, 10);
        moveRoot1.addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        AttributeModifierPerk moveRoot2 = new AttributeModifierPerk(key("base_inc_ms_1"), -1, 11).setNameOverride(moveRoot1);
        moveRoot2.addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        MajorPerk vicio = new MajorPerk(key("major_inc_ms_fs"), 0, 14);
        vicio.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        vicio.addModifier(5F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE);

        PerkTree.PointConnector ctMove1 = register(moveRoot1);
        PerkTree.PointConnector ctMove2 = register(moveRoot2);
        PerkTree.PointConnector ctMajorMobility = register(vicio);

        moveRoot1.setRequireDiscoveredConstellation(ConstellationsAS.vicio);
        moveRoot2.setRequireDiscoveredConstellation(ConstellationsAS.vicio);
        vicio.setRequireDiscoveredConstellation(ConstellationsAS.vicio);

        ctMove1.connect(PERK_TREE.getRootPerk(ConstellationsAS.vicio));
        ctMove2.connect(ctMove1);
        ctMajorMobility.connect(ctMove2);
    }

    private static void initializeAevitasRoot() {
        AttributeModifierPerk lifeRoot1 = new AttributeModifierPerk(key("base_inc_life"), -9, 3);
        lifeRoot1.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        AttributeModifierPerk lifeRoot2 = new AttributeModifierPerk(key("base_inc_life_1"), -8, 5).setNameOverride(lifeRoot1);
        lifeRoot2.addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        MajorPerk aevitas = new MajorPerk(key("major_inc_life"), -12, 6);
        aevitas.addModifier(1.15F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_HEALTH);

        lifeRoot1.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);
        lifeRoot2.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);
        aevitas.setRequireDiscoveredConstellation(ConstellationsAS.aevitas);

        PerkTree.PointConnector ctLife1 = register(lifeRoot1);
        PerkTree.PointConnector ctLife2 = register(lifeRoot2);
        PerkTree.PointConnector ctMajorLife = register(aevitas);

        ctLife1.connect(PERK_TREE.getRootPerk(ConstellationsAS.aevitas));
        ctLife2.connect(ctLife1);
        ctMajorLife.connect(ctLife2);
    }

    private static void initializeRoot() {
        RootPerk rootAevitas = new RootAevitas(key("aevitas"), -6, 2);
        rootAevitas.addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_HEALTH);

        RootPerk rootVicio = new RootVicio(key("vicio"), 0, 7);
        rootVicio.addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_REACH);

        RootPerk rootArmara = new RootArmara(key("armara"), 6, 2);
        rootArmara.addModifier(1.15F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);

        RootPerk rootDiscidia = new RootDiscidia(key("discidia"), 4, -5);
        rootDiscidia.addModifier(10F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE);

        RootPerk rootEvorsio = new RootEvorsio(key("evorsio"), -4, -5);
        rootEvorsio.addModifier(1.2F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);

        register(rootAevitas);
        register(rootVicio);
        register(rootArmara);
        register(rootDiscidia);
        register(rootEvorsio);
    }

}
