/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkConverter;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.type.*;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree.PointConnector;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.constellation.PerkAlcara;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.constellation.PerkGelu;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.constellation.PerkUlteria;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.constellation.PerkVorux;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.*;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key.*;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.root.*;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.event.APIRegistryEvent;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry.*;
import static hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree.PERK_TREE;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryPerks
 * Created by HellFirePvP
 * Date: 22.11.2016 / 12:25
 */
public class RegistryPerks {

    private static AttributeModifierPerk perkRootMajorDamage,
                    perkRootMajorHealth,
                    perkRootMajorMovespeed,
                    perkRootMajorArmor,
                    perkRootMajorHarvest;

    public static void initPerkTree() {
        initializeAttributeTypes();

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

        MinecraftForge.EVENT_BUS.post(new APIRegistryEvent.PerkRegister());
    }

    public static void postProcessPerks() {
        List<AbstractPerk> copyPerks = PerkTree.PERK_TREE.getPerkPoints().stream().map(PerkTreePoint::getPerk).collect(Collectors.toList());
        if (Mods.CRAFTTWEAKER.isPresent()) {
            applyCraftTweakerModifications(copyPerks);
        }

        for (AbstractPerk perk : copyPerks) {
            APIRegistryEvent.PerkPostRemove remove = new APIRegistryEvent.PerkPostRemove(perk);
            MinecraftForge.EVENT_BUS.post(remove);
            if (remove.isRemoved()) {
                PERK_TREE.removePerk(perk);
            }
        }

        PerkTree.PERK_TREE.freeze();
    }

    @Optional.Method(modid = "crafttweaker")
    private static void applyCraftTweakerModifications(List<AbstractPerk> perks) {
        perks.forEach(AbstractPerk::adjustMultipliers);
    }

    private static void initializeMinorConstellationPerks() {
        AttributeModifierPerk perkGelu1 = new AttributeModifierPerk("gelu_inc_perkexp", 23, 18);
        perkGelu1.addModifier(0.06F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        AttributeModifierPerk perkGelu2 = new AttributeModifierPerk("gelu_inc_perkexp_1", 25, 16).setNameOverride(perkGelu1);
        perkGelu2.addModifier(0.06F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        PerkGelu gelu = new PerkGelu(26, 19);

        PERK_TREE.registerPerk(perkGelu1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_def_3"));
        PERK_TREE.registerPerk(perkGelu2)
                .connect(perkGelu1);
        PERK_TREE.registerPerk(gelu)
                .connect(perkGelu2);

        perkGelu1.setRequireDiscoveredConstellation(Constellations.gelu);
        perkGelu2.setRequireDiscoveredConstellation(Constellations.gelu);
        gelu.setRequireDiscoveredConstellation(Constellations.gelu);

        AttributeModifierPerk perkUlteria1 = new AttributeModifierPerk("ulteria_more_perkexp", -28, 15);
        perkUlteria1.addModifier(1.05F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        AttributeModifierPerk perkUlteria2 = new AttributeModifierPerk("ulteria_more_perkexp_1", -26, 17).setNameOverride(perkUlteria1);
        perkUlteria2.addModifier(1.05F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        PerkUlteria ulteria = new PerkUlteria(-29, 18);

        PERK_TREE.registerPerk(perkUlteria1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_life_2"));
        PERK_TREE.registerPerk(perkUlteria2)
                .connect(perkUlteria1);
        PERK_TREE.registerPerk(ulteria)
                .connect(perkUlteria2);

        perkUlteria1.setRequireDiscoveredConstellation(Constellations.ulteria);
        perkUlteria2.setRequireDiscoveredConstellation(Constellations.ulteria);
        ulteria.setRequireDiscoveredConstellation(Constellations.ulteria);

        AttributeModifierPerk perkVorux1 = new AttributeModifierPerk("vorux_inc_perkeff", 14, -27);
        perkVorux1.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkVorux2 = new AttributeModifierPerk("vorux_inc_perkeff_1", 12, -29).setNameOverride(perkVorux1);
        perkVorux2.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        PerkVorux vorux = new PerkVorux(15, -30);

        PERK_TREE.registerPerk(perkVorux1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_dmg_2"));
        PERK_TREE.registerPerk(perkVorux2)
                .connect(perkVorux1);
        PERK_TREE.registerPerk(vorux)
                .connect(perkVorux2);

        perkVorux1.setRequireDiscoveredConstellation(Constellations.vorux);
        perkVorux2.setRequireDiscoveredConstellation(Constellations.vorux);
        vorux.setRequireDiscoveredConstellation(Constellations.vorux);

        AttributeModifierPerk perkAlcara1 = new AttributeModifierPerk("alcara_more_perkeff", -25, -17);
        perkAlcara1.addModifier(1.04F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkAlcara2 = new AttributeModifierPerk("alcara_more_perkeff_1", -27, -15).setNameOverride(perkAlcara1);
        perkAlcara2.addModifier(1.04F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        PerkAlcara alcara = new PerkAlcara(-28, -18);

        PERK_TREE.registerPerk(perkAlcara1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_mine_2"));
        PERK_TREE.registerPerk(perkAlcara2)
                .connect(perkAlcara1);
        PERK_TREE.registerPerk(alcara)
                .connect(perkAlcara2);

        perkAlcara1.setRequireDiscoveredConstellation(Constellations.alcara);
        perkAlcara2.setRequireDiscoveredConstellation(Constellations.alcara);
        alcara.setRequireDiscoveredConstellation(Constellations.alcara);
    }

    private static void initializeOuterVicioPerks() {
        float addedIncMsReach = 0.02F;

        AttributeModifierPerk perkVR1 = new AttributeModifierPerk("outer_s_inc_trv", 13, 23);
        perkVR1.addModifier(addedIncMsReach, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        perkVR1.addModifier(addedIncMsReach, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        AttributeModifierPerk perkVR2 = new AttributeModifierPerk("outer_s_inc_trv_1", 7, 26).setNameOverride(perkVR1);
        perkVR2.addModifier(addedIncMsReach, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        perkVR2.addModifier(addedIncMsReach, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        AttributeModifierPerk perkVR3 = new AttributeModifierPerk("outer_s_inc_trv_2", 1, 29).setNameOverride(perkVR1);
        perkVR3.addModifier(addedIncMsReach, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        perkVR3.addModifier(addedIncMsReach, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        AttributeModifierPerk perkVR4 = new AttributeModifierPerk("outer_s_inc_trv_3", -7, 27).setNameOverride(perkVR1);
        perkVR4.addModifier(addedIncMsReach, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        perkVR4.addModifier(addedIncMsReach, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        AttributeModifierPerk perkVR5 = new AttributeModifierPerk("outer_s_inc_trv_4", -12, 24).setNameOverride(perkVR1);
        perkVR5.addModifier(addedIncMsReach, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        perkVR5.addModifier(addedIncMsReach, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_REACH);

        PERK_TREE.registerPerk(perkVR1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_def_4"));
        PERK_TREE.registerPerk(perkVR2)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_12"))
                .connect(perkVR1);
        PERK_TREE.registerPerk(perkVR3)
                .connect(perkVR2);
        PERK_TREE.registerPerk(perkVR4)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_13"))
                .connect(perkVR3);
        PERK_TREE.registerPerk(perkVR5)
                .connect(perkVR4)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_life"));

        AttributeModifierPerk lssArmorLife1 = new AttributeModifierPerk("flight_life_armor", 4, 30);
        lssArmorLife1.addModifier(0.8F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_HEALTH);
        lssArmorLife1.addModifier(0.8F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_ARMOR);
        AttributeModifierPerk lssArmorLife2 = new AttributeModifierPerk("flight_life_armor_1", 5, 31).setNameOverride(lssArmorLife1);
        lssArmorLife2.addModifier(0.8F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_HEALTH);
        lssArmorLife2.addModifier(0.8F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_ARMOR);
        AttributeModifierPerk lssArmorLife3 = new AttributeModifierPerk("flight_life_armor_2", 4, 32).setNameOverride(lssArmorLife1);
        lssArmorLife3.addModifier(0.8F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_HEALTH);
        lssArmorLife3.addModifier(0.8F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_ARMOR);
        AttributeModifierPerk lssDodgeMs = new AttributeModifierPerk("flight_ms_dodge", 5, 33);
        lssDodgeMs.addModifier(0.7F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_DODGE);
        lssDodgeMs.addModifier(0.7F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_MOVESPEED);
        KeyMantleFlight mantleFlight = new KeyMantleFlight("key_mantle_flight", 4, 34);

        PERK_TREE.registerPerk(lssArmorLife1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_trv_2"));
        PERK_TREE.registerPerk(lssArmorLife2)
                .connect(lssArmorLife1);
        PERK_TREE.registerPerk(lssArmorLife3)
                .connect(lssArmorLife2);
        PERK_TREE.registerPerk(lssDodgeMs)
                .connect(lssArmorLife3);
        PERK_TREE.registerPerk(mantleFlight)
                .connect(lssDodgeMs);

        AttributeModifierPerk atsReach1 = new AttributeModifierPerk("magnet_ats_reach", -10, 23);
        atsReach1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED);
        atsReach1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        AttributeModifierPerk atsReach2 = new AttributeModifierPerk("magnet_ats_reach_1", -9, 24).setNameOverride(atsReach1);
        atsReach2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED);
        atsReach2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        KeyMagnetDrops magnetDrops = new KeyMagnetDrops("key_magnet_drops", -8, 23);

        PERK_TREE.registerPerk(atsReach1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_trv_4"));
        PERK_TREE.registerPerk(atsReach2)
                .connect(atsReach1);
        PERK_TREE.registerPerk(magnetDrops)
                .connect(atsReach2);
    }

    private static void initializeOuterArmaraPerks() {
        float addedIncArmorEle = 0.02F;

        AttributeModifierPerk perkDef1 = new AttributeModifierPerk("outer_s_inc_def", 26, -5);
        perkDef1.addModifier(addedIncArmorEle, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        perkDef1.addModifier(addedIncArmorEle, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        AttributeModifierPerk perkDef2 = new AttributeModifierPerk("outer_s_inc_def_1", 24, 1).setNameOverride(perkDef1);
        perkDef2.addModifier(addedIncArmorEle, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        perkDef2.addModifier(addedIncArmorEle, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        AttributeModifierPerk perkDef3 = new AttributeModifierPerk("outer_s_inc_def_2", 26, 9).setNameOverride(perkDef1);
        perkDef3.addModifier(addedIncArmorEle, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        perkDef3.addModifier(addedIncArmorEle, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        AttributeModifierPerk perkDef4 = new AttributeModifierPerk("outer_s_inc_def_3", 22, 15).setNameOverride(perkDef1);
        perkDef4.addModifier(addedIncArmorEle, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        perkDef4.addModifier(addedIncArmorEle, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        AttributeModifierPerk perkDef5 = new AttributeModifierPerk("outer_s_inc_def_4", 20, 20).setNameOverride(perkDef1);
        perkDef5.addModifier(addedIncArmorEle, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        perkDef5.addModifier(addedIncArmorEle, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);

        PERK_TREE.registerPerk(perkDef1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_dmg_5"));
        PERK_TREE.registerPerk(perkDef2)
                .connect(perkDef1);
        PERK_TREE.registerPerk(perkDef3)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_7"))
                .connect(perkDef2);
        PERK_TREE.registerPerk(perkDef4)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_9"))
                .connect(perkDef3);
        PERK_TREE.registerPerk(perkDef5)
                .connect(perkDef4);

        AttributeModifierPerk perkPerkEffSlot1 = new AttributeModifierPerk("def_gem_path", 15, 20);
        perkPerkEffSlot1.addModifier(1.02F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkPerkEffSlot2 = new AttributeModifierPerk("def_gem_path_1", 14, 21).setNameOverride(perkPerkEffSlot1);
        perkPerkEffSlot2.addModifier(1.02F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        GemSlotMajorPerk perkDefGemSlot = new GemSlotMajorPerk("def_gem_slot", 13, 20);

        PERK_TREE.registerPerk(perkPerkEffSlot1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_10"));
        PERK_TREE.registerPerk(perkPerkEffSlot2)
                .connect(perkPerkEffSlot1);
        PERK_TREE.registerPerk(perkDefGemSlot)
                .connect(perkPerkEffSlot2);

        AttributeModifierPerk perkIncLRedA1 = new AttributeModifierPerk("unwav_life_armor", 27, 11);
        perkIncLRedA1.addModifier(0.08F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        perkIncLRedA1.addModifier(0.75F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_ARMOR);
        AttributeModifierPerk perkIncLRedA2 = new AttributeModifierPerk("unwav_life_armor_1", 28, 12).setNameOverride(perkIncLRedA1);
        perkIncLRedA2.addModifier(0.08F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        perkIncLRedA2.addModifier(0.75F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_ARMOR);
        KeyNoKnockBack noKnockBack = new KeyNoKnockBack("key_no_knockback", 29, 11);

        PERK_TREE.registerPerk(perkIncLRedA1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_def_2"));
        PERK_TREE.registerPerk(perkIncLRedA2)
                .connect(perkIncLRedA1);
        PERK_TREE.registerPerk(noKnockBack)
                .connect(perkIncLRedA2);

        AttributeModifierPerk perkIncArmor1 = new AttributeModifierPerk("bol_red_inc_armor", 26, 2);
        perkIncArmor1.addModifier(0.06F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        AttributeModifierPerk perkIncArmor2 = new AttributeModifierPerk("bol_red_inc_armor_1", 27, 3).setNameOverride(perkIncArmor1);
        perkIncArmor2.addModifier(0.06F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        AttributeModifierPerk perkIncArmor3 = new AttributeModifierPerk("bol_red_inc_armor_2", 28, 2).setNameOverride(perkIncArmor1);
        perkIncArmor3.addModifier(0.06F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        KeyDamageArmor keyDamageArmor = new KeyDamageArmor("key_damage_armor", 29, 3, 0.05F);

        PERK_TREE.registerPerk(perkIncArmor1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_def_1"));
        PERK_TREE.registerPerk(perkIncArmor2)
                .connect(perkIncArmor1);
        PERK_TREE.registerPerk(perkIncArmor3)
                .connect(perkIncArmor2);
        PERK_TREE.registerPerk(keyDamageArmor)
                .connect(perkIncArmor3);
    }

    private static void initializeOuterDiscidiaPerks() {
        float addedIncDmg = 0.02F;

        AttributeModifierPerk perkDmg1 = new AttributeModifierPerk("outer_s_inc_dmg", -5, -27);
        perkDmg1.addModifier(addedIncDmg, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkDmg1.addModifier(addedIncDmg, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        AttributeModifierPerk perkDmg2 = new AttributeModifierPerk("outer_s_inc_dmg_1", 3, -28).setNameOverride(perkDmg1);
        perkDmg2.addModifier(addedIncDmg, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkDmg2.addModifier(addedIncDmg, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        AttributeModifierPerk perkDmg3 = new AttributeModifierPerk("outer_s_inc_dmg_2", 11, -25).setNameOverride(perkDmg1);
        perkDmg3.addModifier(addedIncDmg, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkDmg3.addModifier(addedIncDmg, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        AttributeModifierPerk perkDmg4 = new AttributeModifierPerk("outer_s_inc_dmg_3", 19, -23).setNameOverride(perkDmg1);
        perkDmg4.addModifier(addedIncDmg, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkDmg4.addModifier(addedIncDmg, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        AttributeModifierPerk perkDmg5 = new AttributeModifierPerk("outer_s_inc_dmg_4", 23, -17).setNameOverride(perkDmg1);
        perkDmg5.addModifier(addedIncDmg, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkDmg5.addModifier(addedIncDmg, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        AttributeModifierPerk perkDmg6 = new AttributeModifierPerk("outer_s_inc_dmg_5", 25, -11).setNameOverride(perkDmg1);
        perkDmg6.addModifier(addedIncDmg, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkDmg6.addModifier(addedIncDmg, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);

        PERK_TREE.registerPerk(perkDmg1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_mine_4"));
        PERK_TREE.registerPerk(perkDmg2)
                .connect(perkDmg1);
        PERK_TREE.registerPerk(perkDmg3)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_3"))
                .connect(perkDmg2);
        PERK_TREE.registerPerk(perkDmg4)
                .connect(perkDmg3);
        PERK_TREE.registerPerk(perkDmg5)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_4"))
                .connect(perkDmg4);
        PERK_TREE.registerPerk(perkDmg6)
                .connect(perkDmg5);

        AttributeModifierPerk perkPerkEffSlot1 = new AttributeModifierPerk("dsc_gem_path", 18, -19);
        perkPerkEffSlot1.addModifier(1.02F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkPerkEffSlot2 = new AttributeModifierPerk("dsc_gem_path_1", 17, -21).setNameOverride(perkPerkEffSlot1);
        perkPerkEffSlot2.addModifier(1.02F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        GemSlotMajorPerk perkDscGemSlot = new GemSlotMajorPerk("dsc_gem_slot", 15, -22);

        PERK_TREE.registerPerk(perkPerkEffSlot1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_dmg_3"));
        PERK_TREE.registerPerk(perkPerkEffSlot2)
                .connect(perkPerkEffSlot1);
        PERK_TREE.registerPerk(perkDscGemSlot)
                .connect(perkPerkEffSlot2);

        AttributeModifierPerk perkIncAts1 = new AttributeModifierPerk("inc_ats_ailm", 9, -24);
        perkIncAts1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED);
        AttributeModifierPerk perkIncAts2 = new AttributeModifierPerk("inc_ats_ailm_1", 8, -23).setNameOverride(perkIncAts1);
        perkIncAts2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED);
        KeyDamageEffect keyAilments = new KeyDamageEffect("key_ailments", 7, -24);

        PERK_TREE.registerPerk(perkIncAts1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_dmg_2"));
        PERK_TREE.registerPerk(perkIncAts2)
                .connect(perkIncAts1);
        PERK_TREE.registerPerk(keyAilments)
                .connect(perkIncAts2);

        AttributeModifierPerk perkIncMs1 = new AttributeModifierPerk("inc_cull_ms", 23, -10);
        perkIncMs1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        AttributeModifierPerk perkIncMs2 = new AttributeModifierPerk("inc_cull_ms_1", 22, -11).setNameOverride(perkIncMs1);
        perkIncMs2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        AttributeModifierPerk perkIncCrit = new AttributeModifierPerk("inc_cull_crit", 21, -10);
        perkIncCrit.addModifier(0.08F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE);
        KeyCullAttack keyCull = new KeyCullAttack("key_cull_attack", 20, -11);

        PERK_TREE.registerPerk(perkIncMs1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_dmg_5"));
        PERK_TREE.registerPerk(perkIncMs2)
                .connect(perkIncMs1);
        PERK_TREE.registerPerk(perkIncCrit)
                .connect(perkIncMs2);
        PERK_TREE.registerPerk(keyCull)
                .connect(perkIncCrit);
    }

    private static void initializeOuterEvorsioPerks() {
        float addedIncMining = 0.02F;

        AttributeModifierPerk perkMine1 = new AttributeModifierPerk("outer_s_inc_mine", -29, -3);
        perkMine1.addModifier(addedIncMining, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkMine2 = new AttributeModifierPerk("outer_s_inc_mine_1", -27, -9).setNameOverride(perkMine1);
        perkMine2.addModifier(addedIncMining, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkMine3 = new AttributeModifierPerk("outer_s_inc_mine_2", -23, -15).setNameOverride(perkMine1);
        perkMine3.addModifier(addedIncMining, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkMine4 = new AttributeModifierPerk("outer_s_inc_mine_3", -18, -19).setNameOverride(perkMine1);
        perkMine4.addModifier(addedIncMining, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkMine5 = new AttributeModifierPerk("outer_s_inc_mine_4", -12, -24).setNameOverride(perkMine1);
        perkMine5.addModifier(addedIncMining, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);

        PERK_TREE.registerPerk(perkMine1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_life_4"));
        PERK_TREE.registerPerk(perkMine2)
                .connect(perkMine1);
        PERK_TREE.registerPerk(perkMine3)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_20"))
                .connect(perkMine2);
        PERK_TREE.registerPerk(perkMine4)
                .connect(perkMine3);
        PERK_TREE.registerPerk(perkMine5)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4"))
                .connect(perkMine4);

        AttributeModifierPerk perkPerkEffSlot1 = new AttributeModifierPerk("ev_gem_path", -19, -16);
        perkPerkEffSlot1.addModifier(1.02F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkPerkEffSlot2 = new AttributeModifierPerk("ev_gem_path_1", -18, -15).setNameOverride(perkPerkEffSlot1);
        perkPerkEffSlot2.addModifier(1.02F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        GemSlotMajorPerk perkEvGemSlot = new GemSlotMajorPerk("ev_gem_slot", -17, -16);

        PERK_TREE.registerPerk(perkPerkEffSlot1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_mine_3"));
        PERK_TREE.registerPerk(perkPerkEffSlot2)
                .connect(perkPerkEffSlot1);
        PERK_TREE.registerPerk(perkEvGemSlot)
                .connect(perkPerkEffSlot2);

        AttributeModifierPerk perkReach1 = new AttributeModifierPerk("inc_reach_sweep", -20, -20);
        perkReach1.addModifier(0.03F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        AttributeModifierPerk perkReach2 = new AttributeModifierPerk("inc_reach_sweep_1", -19, -21).setNameOverride(perkReach1);
        perkReach2.addModifier(0.03F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        AttributeModifierPerk perkReach3 = new AttributeModifierPerk("inc_reach_sweep_2", -20, -22).setNameOverride(perkReach1);
        perkReach3.addModifier(0.03F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        KeyAreaOfEffect aoeSweep = new KeyAreaOfEffect("key_sweep_range", -19, -23);

        PERK_TREE.registerPerk(perkReach1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_mine_3"));
        PERK_TREE.registerPerk(perkReach2)
                .connect(perkReach1);
        PERK_TREE.registerPerk(perkReach3)
                .connect(perkReach2);
        PERK_TREE.registerPerk(aoeSweep)
                .connect(perkReach3);

        AttributeModifierPerk perkLessMine1 = new AttributeModifierPerk("less_hrv_speed", -31, -4);
        perkLessMine1.addModifier(0.8F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkLessMine2 = new AttributeModifierPerk("less_hrv_speed_1", -32, -5).setNameOverride(perkLessMine1);
        perkLessMine2.addModifier(0.8F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        KeyAddEnchantment addLuck = new KeyAddEnchantment("key_add_fortune", -31, -6)
                .addEnchantment(Enchantments.FORTUNE, 1);

        PERK_TREE.registerPerk(perkLessMine1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_mine"));
        PERK_TREE.registerPerk(perkLessMine2)
                .connect(perkLessMine1);
        PERK_TREE.registerPerk(addLuck)
                .connect(perkLessMine2);
    }

    private static void initializeOuterAevitasPerks() {
        float addedIncLife = 0.02F;

        AttributeModifierPerk perkLife1 = new AttributeModifierPerk("outer_s_inc_life", -17, 22);
        perkLife1.addModifier(addedIncLife, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        AttributeModifierPerk perkLife2 = new AttributeModifierPerk("outer_s_inc_life_1", -20, 18).setNameOverride(perkLife1);
        perkLife1.addModifier(addedIncLife, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        AttributeModifierPerk perkLife3 = new AttributeModifierPerk("outer_s_inc_life_2", -25, 14).setNameOverride(perkLife1);
        perkLife2.addModifier(addedIncLife, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        AttributeModifierPerk perkLife4 = new AttributeModifierPerk("outer_s_inc_life_3", -26, 8).setNameOverride(perkLife1);
        perkLife3.addModifier(addedIncLife, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        AttributeModifierPerk perkLife5 = new AttributeModifierPerk("outer_s_inc_life_4", -28, 2).setNameOverride(perkLife1);
        perkLife4.addModifier(addedIncLife, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);

        PERK_TREE.registerPerk(perkLife1);
        PERK_TREE.registerPerk(perkLife2)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_16"))
                .connect(perkLife1);
        PERK_TREE.registerPerk(perkLife3)
                .connect(perkLife2);
        PERK_TREE.registerPerk(perkLife4)
                .connect(perkLife3)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_17"));
        PERK_TREE.registerPerk(perkLife5)
                .connect(perkLife4);

        AttributeModifierPerk perkPerkEffSlot1 = new AttributeModifierPerk("life_gem_path", -16, 15);
        perkPerkEffSlot1.addModifier(1.02F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkPerkEffSlot2 = new AttributeModifierPerk("life_gem_path_1", -17, 17).setNameOverride(perkPerkEffSlot1);
        perkPerkEffSlot2.addModifier(1.02F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        GemSlotMajorPerk perkLifeGemSlot = new GemSlotMajorPerk("life_gem_slot", -16, 19);

        PERK_TREE.registerPerk(perkPerkEffSlot1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_15"));
        PERK_TREE.registerPerk(perkPerkEffSlot2)
                .connect(perkPerkEffSlot1);
        PERK_TREE.registerPerk(perkLifeGemSlot)
                .connect(perkPerkEffSlot2);

        AttributeModifierPerk perkEffectMine1 = new AttributeModifierPerk("inc_mine_perk", -16, 25);
        perkEffectMine1.addModifier(0.06F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkEffectMine2 = new AttributeModifierPerk("inc_mine_perk_1", -17, 26).setNameOverride(perkEffectMine1);
        perkEffectMine2.addModifier(0.06F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        KeyVoidTrash voidTrash = new KeyVoidTrash("key_void_trash", -16, 27);

        PERK_TREE.registerPerk(perkEffectMine1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_life"));
        PERK_TREE.registerPerk(perkEffectMine2)
                .connect(perkEffectMine1);
        PERK_TREE.registerPerk(voidTrash)
                .connect(perkEffectMine2);

        AttributeModifierPerk perkIncRec1 = new AttributeModifierPerk("inc_life_rec", -28, 9);
        perkIncRec1.addModifier(0.12F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY);
        AttributeModifierPerk perkIncRec2 = new AttributeModifierPerk("inc_life_rec_1", -29, 8).setNameOverride(perkIncRec1);
        perkIncRec2.addModifier(0.12F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY);
        KeyCleanseBadPotions cureBadEffects = new KeyCleanseBadPotions("key_rem_badeffects", -30, 9);

        PERK_TREE.registerPerk(perkIncRec1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_life_3"));
        PERK_TREE.registerPerk(perkIncRec2)
                .connect(perkIncRec1);
        PERK_TREE.registerPerk(cureBadEffects)
                .connect(perkIncRec2);
    }

    private static void initializePerkEffectPerks() {
        float addedIncPerkEffect = 0.15F;
        float addedIncPerkExp = 0.2F;

        MajorPerk majorPerkEffect1 = new MajorPerk("major_perk_eff_nt", 9, 9);
        majorPerkEffect1.addModifier(addedIncPerkEffect, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        MajorPerk majorPerkEffect2 = new MajorPerk("major_perk_eff_nt_1", 10, -4).setNameOverride(majorPerkEffect1);
        majorPerkEffect2.addModifier(addedIncPerkEffect, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        MajorPerk majorPerkEffect3 = new MajorPerk("major_perk_eff_nt_2", -3, -11).setNameOverride(majorPerkEffect1);
        majorPerkEffect3.addModifier(addedIncPerkEffect, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        MajorPerk majorPerkEffect4 = new MajorPerk("major_perk_eff_nt_3", -11, 0).setNameOverride(majorPerkEffect1);
        majorPerkEffect4.addModifier(addedIncPerkEffect, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        MajorPerk majorPerkEffect5 = new MajorPerk("major_perk_eff_nt_4", -5, 11).setNameOverride(majorPerkEffect1);
        majorPerkEffect5.addModifier(addedIncPerkEffect, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        PERK_TREE.registerPerk(majorPerkEffect1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_7"));
        PERK_TREE.registerPerk(majorPerkEffect2)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_4"));
        PERK_TREE.registerPerk(majorPerkEffect3)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_1"));
        PERK_TREE.registerPerk(majorPerkEffect4)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_13"));
        PERK_TREE.registerPerk(majorPerkEffect5)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_10"));

        MajorPerk majorPerkExp1 = new MajorPerk("major_perk_exp_nt", 5, 11);
        majorPerkExp1.addModifier(addedIncPerkExp, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        MajorPerk majorPerkExp2 = new MajorPerk("major_perk_exp_nt_1", 12, 0).setNameOverride(majorPerkExp1);
        majorPerkExp2.addModifier(addedIncPerkExp, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        MajorPerk majorPerkExp3 = new MajorPerk("major_perk_exp_nt_2", 2, -11).setNameOverride(majorPerkExp1);
        majorPerkExp3.addModifier(addedIncPerkExp, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        MajorPerk majorPerkExp4 = new MajorPerk("major_perk_exp_nt_3", -10, -5).setNameOverride(majorPerkExp1);
        majorPerkExp4.addModifier(addedIncPerkExp, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        MajorPerk majorPerkExp5 = new MajorPerk("major_perk_exp_nt_4", -9, 9).setNameOverride(majorPerkExp1);
        majorPerkExp5.addModifier(addedIncPerkExp, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);

        PERK_TREE.registerPerk(majorPerkExp1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_7"));
        PERK_TREE.registerPerk(majorPerkExp2)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_4"));
        PERK_TREE.registerPerk(majorPerkExp3)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_1"));
        PERK_TREE.registerPerk(majorPerkExp4)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_13"));
        PERK_TREE.registerPerk(majorPerkExp5)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_10"));

        MajorPerk perkEE1 = new MajorPerk("major_inc_encheffect", -2, -3);
        perkEE1.addModifier(0.15F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_ENCH_EFFECT);
        MajorPerk perkEE2 = new MajorPerk("major_inc_encheffect_1", -3, 1).setNameOverride(perkEE1);
        perkEE2.addModifier(0.15F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_ENCH_EFFECT);

        PERK_TREE.registerPerk(perkEE1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t2_14"))
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t2_2"));
        PERK_TREE.registerPerk(perkEE2)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t2_11"))
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t2_14"));
    }

    private static void initializeVicioKeyPerks() {
        AttributeModifierPerk perkSwimSpeed1 = new AttributeModifierPerk("key_path_swim_conversion", -2, 23);
        perkSwimSpeed1.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_SWIMSPEED);
        AttributeModifierPerk perkSwimSpeed2 = new AttributeModifierPerk("key_path_swim_conversion_1", -3, 24).setNameOverride(perkSwimSpeed1);
        perkSwimSpeed2.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_SWIMSPEED);
        AttributeModifierPerk perkSwimSpeed3 = new AttributeModifierPerk("key_path_swim_conversion_2", -2, 25).setNameOverride(perkSwimSpeed1);
        perkSwimSpeed3.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_SWIMSPEED);
        KeyPerk swimSpeedConversion = new KeyPerk("key_swim_conversion", -3, 26);
        swimSpeedConversion.addConverter(new PerkConverter() {
            @Nonnull
            @Override
            public PerkAttributeModifier convertModifier(EntityPlayer player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable AbstractPerk owningPerk) {
                return modifier;
            }

            @Nonnull
            @Override
            public Collection<PerkAttributeModifier> gainExtraModifiers(EntityPlayer player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable AbstractPerk owningPerk) {
                List<PerkAttributeModifier> modifiers = Lists.newArrayList();
                if (modifier.getAttributeType().equals(ATTR_TYPE_MOVESPEED)) {
                    PerkAttributeModifier mod;
                    switch (modifier.getMode()) {
                        case ADDITION:
                        case ADDED_MULTIPLY:
                            mod = modifier.gainAsExtraModifier(this, ATTR_TYPE_SWIMSPEED, modifier.getMode(), modifier.getValue(player, progress) / 2F);
                            if (mod != null) {
                                modifiers.add(mod);
                            }
                            break;
                        case STACKING_MULTIPLY:
                            float val = modifier.getValue(player, progress) - 1;
                            val /= 2F; //Halve the actual value
                            mod = modifier.gainAsExtraModifier(this, ATTR_TYPE_SWIMSPEED, modifier.getMode(), val + 1);
                            if (mod != null) {
                                modifiers.add(mod);
                            }
                            break;
                    }
                }
                return modifiers;
            }
        });

        PERK_TREE.registerPerk(perkSwimSpeed1)
                .connect(PERK_TREE.getAstralSorceryPerk("med_add_ats_dodge"));
        PERK_TREE.registerPerk(perkSwimSpeed2)
                .connect(perkSwimSpeed1);
        PERK_TREE.registerPerk(perkSwimSpeed3)
                .connect(perkSwimSpeed2);
        PERK_TREE.registerPerk(swimSpeedConversion)
                .connect(perkSwimSpeed3);

        AttributeModifierPerk incAttackSpeed1 = new AttributeModifierPerk("major_ats_inc_ats", 9, 14);
        incAttackSpeed1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED);
        AttributeModifierPerk incAttackSpeed2 = new AttributeModifierPerk("major_ats_inc_ats_1", 8, 15).setNameOverride(incAttackSpeed1);
        incAttackSpeed2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED);
        MajorPerk perkZeal = new MajorPerk("major_increased_ats_zeal", 7, 14);
        perkZeal.addModifier(0.15F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED);

        PERK_TREE.registerPerk(incAttackSpeed1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_7"));
        PERK_TREE.registerPerk(incAttackSpeed2)
                .connect(incAttackSpeed1);
        PERK_TREE.registerPerk(perkZeal)
                .connect(incAttackSpeed2);

        AttributeModifierPerk incReachStepAssist = new AttributeModifierPerk("key_stepassist_path_reach", -7, 15);
        incReachStepAssist.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        AttributeModifierPerk incMovespeedStepAssist1 = new AttributeModifierPerk("key_stepassist_path_movespeed", -6, 18);
        incMovespeedStepAssist1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        AttributeModifierPerk incMovespeedStepAssist2 = new AttributeModifierPerk("key_stepassist_path_movespeed_1", -7, 17).setNameOverride(incMovespeedStepAssist1);
        incMovespeedStepAssist2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        KeyStepAssist stepAssistKey = new KeyStepAssist("key_step_assist", -6, 16);

        PERK_TREE.registerPerk(incReachStepAssist)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_10"));
        PERK_TREE.registerPerk(stepAssistKey)
                .connect(incReachStepAssist);
        PERK_TREE.registerPerk(incMovespeedStepAssist2)
                .connect(stepAssistKey);
        PERK_TREE.registerPerk(incMovespeedStepAssist1)
                .connect(incMovespeedStepAssist2)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_13"));

        AttributeModifierPerk lightsMs1 = new AttributeModifierPerk("key_lights_path_ms", 6, 17);
        lightsMs1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        AttributeModifierPerk lightsMs2 = new AttributeModifierPerk("key_lights_path_ms_1", 7, 16).setNameOverride(lightsMs1);
        lightsMs2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        KeySpawnLights spawnLightsKey = new KeySpawnLights("key_spawn_lights", 6, 15);

        PERK_TREE.registerPerk(lightsMs1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_12"));
        PERK_TREE.registerPerk(lightsMs2)
                .connect(lightsMs1);
        PERK_TREE.registerPerk(spawnLightsKey)
                .connect(lightsMs2);

        AttributeModifierPerk redFoodPathDodge1 = new AttributeModifierPerk("key_redfood_path_dodge", 3, 22);
        redFoodPathDodge1.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE);
        AttributeModifierPerk redFoodPathDodge2 = new AttributeModifierPerk("key_redfood_path_dodge_1", 2, 23).setNameOverride(redFoodPathDodge1);
        redFoodPathDodge2.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE);
        AttributeModifierPerk redFoodPathDodge3 = new AttributeModifierPerk("key_redfood_path_dodge_2", 3, 24).setNameOverride(redFoodPathDodge1);
        redFoodPathDodge3.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE);
        KeyReducedFood reducedFoodKey = new KeyReducedFood("key_reduced_food", 4, 23);

        PERK_TREE.registerPerk(redFoodPathDodge1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_12"));
        PERK_TREE.registerPerk(redFoodPathDodge2)
                .connect(redFoodPathDodge1);
        PERK_TREE.registerPerk(redFoodPathDodge3)
                .connect(redFoodPathDodge2);
        PERK_TREE.registerPerk(reducedFoodKey)
                .connect(redFoodPathDodge3);
    }

    private static void initializeArmaraKeyPerks() {
        AttributeModifierPerk perkALC1 = new AttributeModifierPerk("key_alc_inc_armor", 15, -4);
        perkALC1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        AttributeModifierPerk perkALC2 = new AttributeModifierPerk("key_alc_inc_armor_1", 16, -3).setNameOverride(perkALC1);
        perkALC2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        KeyPerk keyArmorConversion = new KeyPerk("key_armor_life_conversion", 17, -4);
        keyArmorConversion.addConverter(new PerkConverter() {
            @Nonnull
            @Override
            public PerkAttributeModifier convertModifier(EntityPlayer player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable AbstractPerk owningPerk) {
                if (modifier.getAttributeType().equals(ATTR_TYPE_ARMOR)) {
                    return modifier.convertModifier(ATTR_TYPE_HEALTH, modifier.getMode(), modifier.getValue(player, progress));
                }
                return modifier;
            }

            @Nonnull
            @Override
            public Collection<PerkAttributeModifier> gainExtraModifiers(EntityPlayer player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable AbstractPerk owningPerk) {
                Collection<PerkAttributeModifier> modifiers = Lists.newArrayList();
                if (modifier.getAttributeType().equals(ATTR_TYPE_ARMOR)) {
                    PerkAttributeModifier mod;
                    mod = modifier.gainAsExtraModifier(this, ATTR_TYPE_ARMOR, PerkAttributeModifier.Mode.STACKING_MULTIPLY, 0F);
                    if (mod != null) {
                        modifiers.add(mod);
                    }
                    mod = modifier.gainAsExtraModifier(this, ATTR_TYPE_ARMOR_TOUGHNESS, PerkAttributeModifier.Mode.STACKING_MULTIPLY, 0F);
                    if (mod != null) {
                        modifiers.add(mod);
                    }
                }
                return modifiers;
            }
        });

        PERK_TREE.registerPerk(perkALC1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_4"));
        PERK_TREE.registerPerk(perkALC2)
                .connect(perkALC1);
        PERK_TREE.registerPerk(keyArmorConversion)
                .connect(perkALC2);

        AttributeModifierPerk perkTh1 = new AttributeModifierPerk("thorns_inc_dmg", 16, 1);
        perkTh1.addModifier(5F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_THORNS);
        AttributeModifierPerk perkTh2 = new AttributeModifierPerk("thorns_inc_dmg_gr", 17, 0);
        perkTh2.addModifier(10F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_THORNS);
        MajorPerk perkRangedThorns = new MajorPerk("thorns_ranged", 16, -1);
        perkRangedThorns.addModifier(1F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_THORNS_RANGED);
        perkRangedThorns.addModifier(10F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_THORNS);

        PERK_TREE.registerPerk(perkTh1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_5"));
        PERK_TREE.registerPerk(perkTh2)
                .connect(perkTh1);
        PERK_TREE.registerPerk(perkRangedThorns)
                .connect(perkTh2);

        AttributeModifierPerk perkPhEle1 = new AttributeModifierPerk("key_phoenix_path", 16, 16);
        perkPhEle1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        AttributeModifierPerk perkPhEle2 = new AttributeModifierPerk("key_phoenix_path_1", 17, 15).setNameOverride(perkPhEle1);
        perkPhEle2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        AttributeModifierPerk perkPhEle3 = new AttributeModifierPerk("key_phoenix_path_2", 18, 16).setNameOverride(perkPhEle1);
        perkPhEle3.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        KeyCheatDeath cheatDeathKey = new KeyCheatDeath("key_cheat_death", 17, 17);

        PERK_TREE.registerPerk(perkPhEle1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_10"));
        PERK_TREE.registerPerk(perkPhEle2)
                .connect(perkPhEle1);
        PERK_TREE.registerPerk(perkPhEle3)
                .connect(perkPhEle2);
        PERK_TREE.registerPerk(cheatDeathKey)
                .connect(perkPhEle3);

        AttributeModifierPerk perkArmor1 = new AttributeModifierPerk("inc_added_armor", 21, 5);
        perkArmor1.addModifier(1F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_ARMOR);
        AttributeModifierPerk perkArmor2 = new AttributeModifierPerk("inc_added_armor_1", 22, 4).setNameOverride(perkArmor1);
        perkArmor2.addModifier(1F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_ARMOR);
        AttributeModifierPerk perkArmor3 = new AttributeModifierPerk("inc_added_armor_2", 23, 5).setNameOverride(perkArmor1);
        perkArmor3.addModifier(1F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_ARMOR);
        MajorPerk perkArmor4 = new MajorPerk("major_flat_armor", 22, 6);
        perkArmor4.addModifier(6F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_ARMOR);
        perkArmor4.addModifier(2F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_ARMOR_TOUGHNESS);

        PERK_TREE.registerPerk(perkArmor1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_7"));
        PERK_TREE.registerPerk(perkArmor2)
                .connect(perkArmor1);
        PERK_TREE.registerPerk(perkArmor3)
                .connect(perkArmor2);
        PERK_TREE.registerPerk(perkArmor4)
                .connect(perkArmor3);

        AttributeModifierPerk perkNoArmorP1 = new AttributeModifierPerk("key_no_armor_armor", 12, 15);
        perkNoArmorP1.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        AttributeModifierPerk perkNoArmorP2 = new AttributeModifierPerk("key_no_armor_resist", 11, 14);
        perkNoArmorP2.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        KeyNoArmor noArmorKey = new KeyNoArmor("key_no_armor", 12, 13);

        PERK_TREE.registerPerk(perkNoArmorP1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_9"));
        PERK_TREE.registerPerk(perkNoArmorP2)
                .connect(perkNoArmorP1);
        PERK_TREE.registerPerk(noArmorKey)
                .connect(perkNoArmorP2);
    }

    private static void initializeDiscidiaKeyPerks() {
        KeyBleed bleedKey = new KeyBleed("key_bleed", 15, -6);
        AttributeModifierPerk perkBl1 = new AttributeModifierPerk("key_bleed_inc_duration", 16, -7);
        perkBl1.addModifier(0.3F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_BLEED_DURATION);
        AttributeModifierPerk perkBl2 = new AttributeModifierPerk("key_bleed_inc_duration_greater", 15, -8);
        perkBl2.addModifier(0.4F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_BLEED_DURATION);

        PERK_TREE.registerPerk(bleedKey)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_3"));
        PERK_TREE.registerPerk(perkBl1)
                .connect(bleedKey);
        PERK_TREE.registerPerk(perkBl2)
                .connect(perkBl1);

        AttributeModifierPerk perkDst1 = new AttributeModifierPerk("key_dst_less_dmg", 17, -15);
        perkDst1.addModifier(0.9F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        AttributeModifierPerk perkDst2 = new AttributeModifierPerk("key_dst_less_dmg_2", 18, -16).setNameOverride(perkDst1);
        perkDst2.addModifier(0.9F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        KeyProjectileProximity projectileProximityKey = new KeyProjectileProximity("key_projectile_proximity", 17, -17);

        PERK_TREE.registerPerk(perkDst1)
                .connect(PERK_TREE.getAstralSorceryPerk("med_reach_arrowspeed"));
        PERK_TREE.registerPerk(perkDst2)
                .connect(perkDst1);
        PERK_TREE.registerPerk(projectileProximityKey)
                .connect(perkDst2);

        AttributeModifierPerk perkDst3 = new AttributeModifierPerk("key_dst_less_dmg_3", 13, -19).setNameOverride(perkDst1);
        perkDst3.addModifier(0.9F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        AttributeModifierPerk perkDst4 = new AttributeModifierPerk("key_dst_less_dmg_4", 14, -20).setNameOverride(perkDst1);
        perkDst4.addModifier(0.9F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        KeyProjectileDistance projectileDistanceKey = new KeyProjectileDistance("key_projectile_distance", 15, -19);

        PERK_TREE.registerPerk(perkDst3)
                .connect(PERK_TREE.getAstralSorceryPerk("med_reach_arrowspeed"));
        PERK_TREE.registerPerk(perkDst4)
                .connect(perkDst3);
        PERK_TREE.registerPerk(projectileDistanceKey)
                .connect(perkDst4);

        AttributeModifierPerk perkRPCrit = new AttributeModifierPerk("key_rampage_path_node_crit", 4, -18);
        perkRPCrit.addModifier(3, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE);
        AttributeModifierPerk perkRPDmg = new AttributeModifierPerk("key_rampage_path_node_dmg", 3, -15);
        perkRPDmg.addModifier(0.08F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkRPDmg.addModifier(0.08F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        AttributeModifierPerk perkRPDmg2 = new AttributeModifierPerk("key_rampage_path_node_dmg_1", 4, -16).setNameOverride(perkRPDmg);
        perkRPDmg2.addModifier(0.08F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkRPDmg2.addModifier(0.08F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        KeyRampage rampageKey = new KeyRampage("key_rampage", 3, -17);

        PERK_TREE.registerPerk(perkRPCrit)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_2"));
        PERK_TREE.registerPerk(rampageKey)
                .connect(perkRPCrit);
        PERK_TREE.registerPerk(perkRPDmg2)
                .connect(rampageKey);
        PERK_TREE.registerPerk(perkRPDmg)
                .connect(perkRPDmg2)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_1"));

        AttributeModifierPerk perkLL1 = new AttributeModifierPerk("inc_leech_vamp", -1, -15);
        perkLL1.addModifier(3F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_ATTACK_LIFE_LEECH);
        AttributeModifierPerk perkLL2 = new AttributeModifierPerk("inc_leech_vamp_1", -2, -16).setNameOverride(perkLL1);
        perkLL2.addModifier(3F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_ATTACK_LIFE_LEECH);
        MajorPerk perkVampirism = new MajorPerk("major_leech_vamp", -1, -17);
        perkVampirism.addModifier(5F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_ATTACK_LIFE_LEECH);
        perkVampirism.addModifier(2F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_HEALTH);

        PERK_TREE.registerPerk(perkLL1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_1"));
        PERK_TREE.registerPerk(perkLL2)
                .connect(perkLL1);
        PERK_TREE.registerPerk(perkVampirism)
                .connect(perkLL2);

        AttributeModifierPerk perkFD1 = new AttributeModifierPerk("ds_inc_potion_duration", 21, -6);
        perkFD1.addModifier(0.15F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_POTION_DURATION);
        AttributeModifierPerk perkFD2 = new AttributeModifierPerk("ds_inc_potion_duration_1", 22, -5).setNameOverride(perkFD1);
        perkFD2.addModifier(0.15F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_POTION_DURATION);
        MajorPerk perkFD3 = new MajorPerk("major_ds_inc_potion_duration", 23, -6);
        perkFD3.addModifier(0.3F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_POTION_DURATION);
        perkFD3.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);

        PERK_TREE.registerPerk(perkFD1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_5"));
        PERK_TREE.registerPerk(perkFD2)
                .connect(perkFD1);
        PERK_TREE.registerPerk(perkFD3)
                .connect(perkFD2);
    }

    private static void initializeEvorsioKeyPerks() {
        AttributeModifierPerk perkLL1 = new AttributeModifierPerk("key_lastbreath_path_node", -7, -17);
        perkLL1.addModifier(0.08F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkLL2 = new AttributeModifierPerk("key_lastbreath_path_node_1", -6, -16).setNameOverride(perkLL1);
        perkLL2.addModifier(0.08F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        KeyLastBreath lastBreathKey = new KeyLastBreath("key_lastbreath", -5, -17);

        PERK_TREE.registerPerk(perkLL1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4"));
        PERK_TREE.registerPerk(perkLL2)
                .connect(perkLL1);
        PERK_TREE.registerPerk(lastBreathKey)
                .connect(perkLL2);

        AttributeModifierPerk perkDTM1 = new AttributeModifierPerk("key_digtypes_path_node_inc", -15, -8);
        perkDTM1.addModifier(0.06F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkDTM2 = new AttributeModifierPerk("key_digtypes_path_node_inc_1", -14, -7).setNameOverride(perkDTM1);
        perkDTM2.addModifier(0.06F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkDTM3 = new AttributeModifierPerk("key_digtypes_path_add", -14, -5);
        perkDTM3.addModifier(1F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_HARVEST_SPEED);
        KeyDigTypes digTypesKey = new KeyDigTypes("key_digtypes", -15, -6);

        PERK_TREE.registerPerk(perkDTM3)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_13"));
        PERK_TREE.registerPerk(digTypesKey)
                .connect(perkDTM3);
        PERK_TREE.registerPerk(perkDTM2)
                .connect(digTypesKey);
        PERK_TREE.registerPerk(perkDTM1)
                .connect(perkDTM2)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_20"));

        AttributeModifierPerk perkAD1 = new AttributeModifierPerk("key_disarm_path_node", -16, -2);
        perkAD1.addModifier(1.05F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        AttributeModifierPerk perkAD2 = new AttributeModifierPerk("key_disarm_path_node_1", -17, -1).setNameOverride(perkAD1);
        perkAD2.addModifier(1.05F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        AttributeModifierPerk perkAD3 = new AttributeModifierPerk("key_disarm_path_node_2", -18, -2).setNameOverride(perkAD1);
        perkAD3.addModifier(1.05F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        KeyDisarm disarmKey = new KeyDisarm("key_disarm", -17, -3);

        PERK_TREE.registerPerk(perkAD1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_13"));
        PERK_TREE.registerPerk(perkAD2)
                .connect(perkAD1);
        PERK_TREE.registerPerk(perkAD3)
                .connect(perkAD2)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_18"));
        PERK_TREE.registerPerk(disarmKey)
                .connect(perkAD3);


        AttributeModifierPerk perkACH1 = new AttributeModifierPerk("key_arc_chains", -5, -24);
        perkACH1.addModifier(1, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_ARC_CHAINS);
        AttributeModifierPerk perkACH2 = new MajorPerk("key_arc_chains_major", -6, -23);
        perkACH2.addModifier(2, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_ARC_CHAINS);
        AttributeModifierPerk perkACH3 = new AttributeModifierPerk("key_arc_chains_1", -5, -22).setNameOverride(perkACH1);
        perkACH3.addModifier(1, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_ARC_CHAINS);
        KeyLightningArc arcKey = new KeyLightningArc("key_lightning_arc", -2, -23);

        PERK_TREE.registerPerk(arcKey)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_1"));
        PERK_TREE.registerPerk(perkACH1)
                .connect(arcKey);
        PERK_TREE.registerPerk(perkACH2)
                .connect(perkACH1);
        PERK_TREE.registerPerk(perkACH3)
                .connect(perkACH2);

        AttributeModifierPerk perkChainL1 = new AttributeModifierPerk("key_chain_mining_length", -21, -9);
        perkChainL1.addModifier(1F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_MINING_CHAIN_LENGTH);
        AttributeModifierPerk perkChainL2 = new AttributeModifierPerk("key_chain_mining_length_1", -22, -10).setNameOverride(perkChainL1);
        perkChainL2.addModifier(1F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_MINING_CHAIN_LENGTH);
        MajorPerk perkChainL3 = new MajorPerk("key_chain_mining_length_greater", -23, -12);
        perkChainL3.addModifier(3F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_MINING_CHAIN_LENGTH);
        AttributeModifierPerk perkChanceL1 = new AttributeModifierPerk("key_chain_mining_chance", -22, -6);
        perkChanceL1.addModifier(0.15F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_MINING_CHAIN_CHANCE);
        AttributeModifierPerk perkChanceL2 = new AttributeModifierPerk("key_chain_mining_chance_1", -23, -5).setNameOverride(perkChanceL1);
        perkChanceL2.addModifier(0.15F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_MINING_CHAIN_CHANCE);
        MajorPerk perkChanceL3 = new MajorPerk("key_chain_mining_chance_greater", -24, -3);
        perkChanceL3.addModifier(1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MINING_CHAIN_CHANCE);
        MajorPerk perkDoubleL1 = new MajorPerk("key_chain_mining_double", -24, -8);
        perkDoubleL1.addModifier(0.5F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_MINING_CHAIN_SUCCESSIVECHAIN);
        KeyChainMining chainMiningKey = new KeyChainMining("key_chain_mining", -20, -7);

        PERK_TREE.registerPerk(chainMiningKey)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_19"));
        PERK_TREE.registerPerk(perkChanceL1)
                .connect(chainMiningKey);
        PERK_TREE.registerPerk(perkChanceL2)
                .connect(perkChanceL1);
        PERK_TREE.registerPerk(perkChanceL3)
                .connect(perkChanceL2);
        PERK_TREE.registerPerk(perkChainL1)
                .connect(chainMiningKey);
        PERK_TREE.registerPerk(perkChainL2)
                .connect(perkChainL1);
        PERK_TREE.registerPerk(perkChainL3)
                .connect(perkChainL2);
        PERK_TREE.registerPerk(perkDoubleL1)
                .connect(perkChainL2)
                .connect(perkChanceL2);

        AttributeModifierPerk perkFD1 = new AttributeModifierPerk("ev_inc_potion_duration", -15, -16);
        perkFD1.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_POTION_DURATION);
        AttributeModifierPerk perkFD2 = new AttributeModifierPerk("ev_inc_potion_duration_1", -14, -17).setNameOverride(perkFD1);
        perkFD2.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_POTION_DURATION);
        MajorPerk perkFD3 = new MajorPerk("major_ev_inc_potion_duration", -15, -18);
        perkFD3.addModifier(0.4F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_POTION_DURATION);
        perkFD3.addModifier(0.75F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_HEALTH);

        PERK_TREE.registerPerk(perkFD1)
                .connect(PERK_TREE.getAstralSorceryPerk("med_added_hrv_speed"));
        PERK_TREE.registerPerk(perkFD2)
                .connect(perkFD1);
        PERK_TREE.registerPerk(perkFD3)
                .connect(perkFD2);
    }

    private static void initializeAevitasKeyPerks() {
        AttributeModifierPerk perkReachP1 = new AttributeModifierPerk("key_reach_path_node", -12, 11);
        perkReachP1.addModifier(0.08F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        AttributeModifierPerk perkReachP2 = new AttributeModifierPerk("key_reach_path_node_1", -11, 12).setNameOverride(perkReachP1);
        perkReachP2.addModifier(0.08F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_REACH);
        KeyReach reachKey = new KeyReach("key_reach", -12, 13);
        reachKey.addModifier(1F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_REACH);
        reachKey.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);

        PERK_TREE.registerPerk(perkReachP1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_16"));
        PERK_TREE.registerPerk(perkReachP2)
                .connect(perkReachP1);
        PERK_TREE.registerPerk(reachKey)
                .connect(perkReachP2);

        AttributeModifierPerk perkSEP1 = new AttributeModifierPerk("key_enrich_path_node", -18, 13);
        perkSEP1.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkSEP2 = new AttributeModifierPerk("key_enrich_path_node_1", -19, 12).setNameOverride(perkSEP1);
        perkSEP2.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkSEP3 = new AttributeModifierPerk("key_enrich_path_node_2", -20, 13).setNameOverride(perkSEP1);
        perkSEP3.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        KeyStoneEnrichment stoneEnrichmentKey = new KeyStoneEnrichment("key_stone_enrichment", -19, 14);

        PERK_TREE.registerPerk(perkSEP1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_16"));
        PERK_TREE.registerPerk(perkSEP2)
                .connect(perkSEP1);
        PERK_TREE.registerPerk(perkSEP3)
                .connect(perkSEP2);
        PERK_TREE.registerPerk(stoneEnrichmentKey)
                .connect(perkSEP3);

        AttributeModifierPerk perkMD1 = new AttributeModifierPerk("key_mending_path_node", -21, 3);
        perkMD1.addModifier(2F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_DODGE);
        AttributeModifierPerk perkMD2 = new AttributeModifierPerk("key_mending_path_node_1", -22, 4);
        perkMD2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE);
        AttributeModifierPerk perkMD3 = new AttributeModifierPerk("key_mending_path_node_2", -23, 3).setNameOverride(perkMD2);
        perkMD3.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE);
        KeyMending mendingKey = new KeyMending("key_mending", -22, 2);

        PERK_TREE.registerPerk(perkMD1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_17"));
        PERK_TREE.registerPerk(perkMD2)
                .connect(perkMD1);
        PERK_TREE.registerPerk(perkMD3)
                .connect(perkMD2);
        PERK_TREE.registerPerk(mendingKey)
                .connect(perkMD3);

        AttributeModifierPerk perkGP1 = new AttributeModifierPerk("key_growables_path_node", -9, 15);
        perkGP1.addModifier(0.15F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY);
        AttributeModifierPerk perkGP2 = new AttributeModifierPerk("key_growables_path_node_1", -10, 14).setNameOverride(perkGP1);
        perkGP2.addModifier(0.15F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY);
        AttributeModifierPerk perkGP3 = new AttributeModifierPerk("key_growables_path_node_2", -11, 15).setNameOverride(perkGP1);
        perkGP3.addModifier(0.15F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY);
        KeyGrowable growableKey = new KeyGrowable("key_growables", -10, 16);

        PERK_TREE.registerPerk(perkGP1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_10"));
        PERK_TREE.registerPerk(perkGP2)
                .connect(perkGP1);
        PERK_TREE.registerPerk(perkGP3)
                .connect(perkGP2)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_15"));
        PERK_TREE.registerPerk(growableKey)
                .connect(perkGP3);
    }

    private static void initializeTreeConnectorPerks() {
        float more_ch = 0.15F;

        AttributeModifierPerk perkEvorsioCh1 = new AttributeModifierPerk("threshold_evorsio", -13, -27);
        perkEvorsioCh1.addModifier(more_ch, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        AttributeModifierPerk perkEvorsioCh2 = new AttributeModifierPerk("threshold_evorsio_1", -15, -30).setNameOverride(perkEvorsioCh1);
        perkEvorsioCh2.addModifier(more_ch, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        AttributeModifierPerk perkEvorsioCh3 = new AttributeModifierPerk("threshold_evorsio_2", -11, -30).setNameOverride(perkEvorsioCh1);
        perkEvorsioCh3.addModifier(more_ch, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        PerkTreeConnector thresholdEvorsio = new PerkTreeConnector("epi_evorsio", -13, -29);

        PERK_TREE.registerPerk(perkEvorsioCh1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_mine_4"));
        PERK_TREE.registerPerk(perkEvorsioCh2)
                .connect(perkEvorsioCh1);
        PERK_TREE.registerPerk(perkEvorsioCh3)
                .connect(perkEvorsioCh2)
                .connect(perkEvorsioCh1);
        PERK_TREE.registerPerk(thresholdEvorsio)
                .connect(perkEvorsioCh1)
                .connect(perkEvorsioCh2)
                .connect(perkEvorsioCh3);

        AttributeModifierPerk perkArmaraCh1 = new AttributeModifierPerk("threshold_armara", 29, -4);
        perkArmaraCh1.addModifier(more_ch, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        AttributeModifierPerk perkArmaraCh2 = new AttributeModifierPerk("threshold_armara_1", 32, -2).setNameOverride(perkArmaraCh1);
        perkArmaraCh2.addModifier(more_ch, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        AttributeModifierPerk perkArmaraCh3 = new AttributeModifierPerk("threshold_armara_2", 32, -6).setNameOverride(perkArmaraCh1);
        perkArmaraCh3.addModifier(more_ch, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        PerkTreeConnector thresholdArmara = new PerkTreeConnector("epi_armara", 31, -4);

        PERK_TREE.registerPerk(perkArmaraCh1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_def"));
        PERK_TREE.registerPerk(perkArmaraCh2)
                .connect(perkArmaraCh1);
        PERK_TREE.registerPerk(perkArmaraCh3)
                .connect(perkArmaraCh2)
                .connect(perkArmaraCh1);
        PERK_TREE.registerPerk(thresholdArmara)
                .connect(perkArmaraCh1)
                .connect(perkArmaraCh2)
                .connect(perkArmaraCh3);

        AttributeModifierPerk perkVicioCh1 = new AttributeModifierPerk("threshold_vicio", -6, 30);
        perkVicioCh1.addModifier(more_ch, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        AttributeModifierPerk perkVicioCh2 = new AttributeModifierPerk("threshold_vicio_1", -4, 33).setNameOverride(perkVicioCh1);
        perkVicioCh2.addModifier(more_ch, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        AttributeModifierPerk perkVicioCh3 = new AttributeModifierPerk("threshold_vicio_2", -8, 33).setNameOverride(perkVicioCh1);
        perkVicioCh3.addModifier(more_ch, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP);
        PerkTreeConnector thresholdVicio = new PerkTreeConnector("epi_vicio", -6, 32);

        PERK_TREE.registerPerk(perkVicioCh1)
                .connect(PERK_TREE.getAstralSorceryPerk("outer_s_inc_trv_3"));
        PERK_TREE.registerPerk(perkVicioCh2)
                .connect(perkVicioCh1);
        PERK_TREE.registerPerk(perkVicioCh3)
                .connect(perkVicioCh2)
                .connect(perkVicioCh1);
        PERK_TREE.registerPerk(thresholdVicio)
                .connect(perkVicioCh1)
                .connect(perkVicioCh2)
                .connect(perkVicioCh3);
    }

    private static void initializePerkExteriorTravelWheel() {
        float inc_t4 = 0.03F;

        // Evorsio -> Discidia
        AttributeModifierPerk perkEff1 = new AttributeModifierPerk("base_inc_perkeffect_t4", -10, -18);
        perkEff1.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff2 = new AttributeModifierPerk("base_inc_perkeffect_t4_1", -3, -20).setNameOverride(perkEff1);
        perkEff2.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff3 = new AttributeModifierPerk("base_inc_perkeffect_t4_2", 5, -21).setNameOverride(perkEff1);
        perkEff3.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff4 = new AttributeModifierPerk("base_inc_perkeffect_t4_3", 9, -18).setNameOverride(perkEff1);
        perkEff4.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        PERK_TREE.registerPerk(perkEff1)
                .connect(PERK_TREE.getAstralSorceryPerk("med_added_hrv_speed"))
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3"));
        PERK_TREE.registerPerk(perkEff2)
                .connect(perkEff1);
        PERK_TREE.registerPerk(perkEff3)
                .connect(perkEff2);
        PERK_TREE.registerPerk(perkEff4)
                .connect(perkEff3)
                .connect(PERK_TREE.getAstralSorceryPerk("med_reach_arrowspeed"))
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_2"));

        // Discidia -> Armara
        AttributeModifierPerk perkEff5 = new AttributeModifierPerk("base_inc_perkeffect_t4_4", 17, -12).setNameOverride(perkEff1);
        perkEff5.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff6 = new AttributeModifierPerk("base_inc_perkeffect_t4_5", 19, -7).setNameOverride(perkEff1);
        perkEff6.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff7 = new AttributeModifierPerk("base_inc_perkeffect_t4_6", 18, -1).setNameOverride(perkEff1);
        perkEff7.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff8 = new AttributeModifierPerk("base_inc_perkeffect_t4_7", 19, 6).setNameOverride(perkEff1);
        perkEff8.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        PERK_TREE.registerPerk(perkEff5)
                .connect(PERK_TREE.getAstralSorceryPerk("med_reach_arrowspeed"))
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_3"));
        PERK_TREE.registerPerk(perkEff6)
                .connect(perkEff5);
        PERK_TREE.registerPerk(perkEff7)
                .connect(perkEff6);
        PERK_TREE.registerPerk(perkEff8)
                .connect(perkEff7)
                .connect(PERK_TREE.getAstralSorceryPerk("med_more_res"))
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_5"));

        // Armara -> Vicio
        AttributeModifierPerk perkEff9 = new AttributeModifierPerk("base_inc_perkeffect_t4_9", 15, 13).setNameOverride(perkEff1);
        perkEff9.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff10 = new AttributeModifierPerk("base_inc_perkeffect_t4_10", 14, 17).setNameOverride(perkEff1);
        perkEff10.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff11 = new AttributeModifierPerk("base_inc_perkeffect_t4_11", 9, 18).setNameOverride(perkEff1);
        perkEff11.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff12 = new AttributeModifierPerk("base_inc_perkeffect_t4_12", 5, 19).setNameOverride(perkEff1);
        perkEff12.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        PERK_TREE.registerPerk(perkEff9)
                .connect(PERK_TREE.getAstralSorceryPerk("med_more_res"))
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_6"));
        PERK_TREE.registerPerk(perkEff10)
                .connect(perkEff9);
        PERK_TREE.registerPerk(perkEff11)
                .connect(perkEff10);
        PERK_TREE.registerPerk(perkEff12)
                .connect(perkEff11)
                .connect(PERK_TREE.getAstralSorceryPerk("med_add_ats_dodge"))
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_8"));

        // Vicio -> Aevitas
        AttributeModifierPerk perkEff13 = new AttributeModifierPerk("base_inc_perkeffect_t4_13", -5, 20).setNameOverride(perkEff1);
        perkEff13.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff14 = new AttributeModifierPerk("base_inc_perkeffect_t4_14", -9, 19).setNameOverride(perkEff1);
        perkEff14.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff15 = new AttributeModifierPerk("base_inc_perkeffect_t4_15", -14, 17).setNameOverride(perkEff1);
        perkEff15.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff16 = new AttributeModifierPerk("base_inc_perkeffect_t4_16", -16, 12).setNameOverride(perkEff1);
        perkEff16.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        PERK_TREE.registerPerk(perkEff13)
                .connect(PERK_TREE.getAstralSorceryPerk("med_add_ats_dodge"))
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_9"));
        PERK_TREE.registerPerk(perkEff14)
                .connect(perkEff13);
        PERK_TREE.registerPerk(perkEff15)
                .connect(perkEff14);
        PERK_TREE.registerPerk(perkEff16)
                .connect(perkEff15)
                .connect(PERK_TREE.getAstralSorceryPerk("med_add_life"))
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_11"));

        // Aevitas -> Evorsio
        AttributeModifierPerk perkEff17 = new AttributeModifierPerk("base_inc_perkeffect_t4_17", -19, 4).setNameOverride(perkEff1);
        perkEff17.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff18 = new AttributeModifierPerk("base_inc_perkeffect_t4_18", -20, -1).setNameOverride(perkEff1);
        perkEff18.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff19 = new AttributeModifierPerk("base_inc_perkeffect_t4_19", -18, -6).setNameOverride(perkEff1);
        perkEff19.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff20 = new AttributeModifierPerk("base_inc_perkeffect_t4_20", -16, -10).setNameOverride(perkEff1);
        perkEff20.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        PERK_TREE.registerPerk(perkEff17)
                .connect(PERK_TREE.getAstralSorceryPerk("med_add_life"))
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_12"));
        PERK_TREE.registerPerk(perkEff18)
                .connect(perkEff17);
        PERK_TREE.registerPerk(perkEff19)
                .connect(perkEff18);
        PERK_TREE.registerPerk(perkEff20)
                .connect(perkEff19)
                .connect(PERK_TREE.getAstralSorceryPerk("med_added_hrv_speed"))
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_14"));
    }

    private static void initializeEvorsioBranch() {
        AttributeModifierPerk perkM1 = new AttributeModifierPerk("med_inc_hrv_speed", -11, -11);
        perkM1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkM2 = new AttributeModifierPerk("med_inc_hrv_speed_1", -13, -11).setNameOverride(perkM1);
        perkM2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);

        AttributeModifierPerk perkHrvAts = new MajorPerk("not_evo_hrv_ats", -12, -10);
        perkHrvAts.addModifier(1.1F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        perkHrvAts.addModifier(1.1F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_ATTACK_SPEED);

        AttributeModifierPerk perkS1 = new AttributeModifierPerk("med_inc_hrv_speed_2", -10, -13).setNameOverride(perkM1);
        perkS1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk perkS2 = new AttributeModifierPerk("med_inc_hrv_speed_3", -12, -13).setNameOverride(perkM1);
        perkS2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);

        AttributeModifierPerk perkHrvReach = new MajorPerk("not_evo_hrv_reach", -11, -14);
        perkHrvReach.addModifier(0.08F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        perkHrvReach.addModifier(0.15F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_REACH);

        AttributeModifierPerk perkAddedHrvSpeed = new MajorPerk("med_added_hrv_speed", -14, -14);
        perkAddedHrvSpeed.addModifier(3F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_HARVEST_SPEED);
        perkAddedHrvSpeed.addModifier(0.15F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_REACH);

        PERK_TREE.registerPerk(perkM1)
                .connect(PERK_TREE.getAstralSorceryPerk("major_inc_harvest"));
        PERK_TREE.registerPerk(perkHrvAts)
                .connect(perkM1);
        PERK_TREE.registerPerk(perkM2)
                .connect(perkHrvAts);

        PERK_TREE.registerPerk(perkS1)
                .connect(PERK_TREE.getAstralSorceryPerk("major_inc_harvest"));
        PERK_TREE.registerPerk(perkHrvReach)
                .connect(perkS1);
        PERK_TREE.registerPerk(perkS2)
                .connect(perkHrvReach);

        PERK_TREE.registerPerk(perkAddedHrvSpeed)
                .connect(perkS2)
                .connect(perkM2);
    }

    private static void initializeDiscidiaBranch() {
        AttributeModifierPerk perkP1 = new AttributeModifierPerk("med_inc_proj_damage", 11, -12);
        perkP1.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        AttributeModifierPerk perkP2 = new AttributeModifierPerk("med_inc_proj_damage_1", 13, -12).setNameOverride(perkP1);
        perkP2.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);

        AttributeModifierPerk perkProjCrit = new MajorPerk("not_dsc_proj_crit", 12, -11);
        perkProjCrit.addModifier(1.05F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        perkProjCrit.addModifier(2F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE);

        AttributeModifierPerk perkM1 = new AttributeModifierPerk("med_inc_melee_damage", 10, -14);
        perkM1.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        AttributeModifierPerk perkM2 = new AttributeModifierPerk("med_inc_melee_damage_1", 12, -14).setNameOverride(perkM1);
        perkM2.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);

        AttributeModifierPerk perkMeleeMulti = new MajorPerk("not_dsc_melee_multi", 11, -15);
        perkMeleeMulti.addModifier(1.05F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkMeleeMulti.addModifier(0.3F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_MULTIPLIER);

        AttributeModifierPerk perkReachProjSpeed = new MajorPerk("med_reach_arrowspeed", 14, -16);
        perkReachProjSpeed.addModifier(1F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_REACH);
        perkReachProjSpeed.addModifier(1.15F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_PROJ_SPEED);

        PERK_TREE.registerPerk(perkP1)
                .connect(PERK_TREE.getAstralSorceryPerk("major_inc_damage"));
        PERK_TREE.registerPerk(perkProjCrit)
                .connect(perkP1);
        PERK_TREE.registerPerk(perkP2)
                .connect(perkProjCrit);

        PERK_TREE.registerPerk(perkM1)
                .connect(PERK_TREE.getAstralSorceryPerk("major_inc_damage"));
        PERK_TREE.registerPerk(perkMeleeMulti)
                .connect(perkM1);
        PERK_TREE.registerPerk(perkM2)
                .connect(perkMeleeMulti);

        PERK_TREE.registerPerk(perkReachProjSpeed)
                .connect(perkP2)
                .connect(perkM2);
    }

    private static void initializeArmaraBranch() {
        AttributeModifierPerk perkAr1 = new AttributeModifierPerk("med_inc_armor", 14, 8);
        perkAr1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        AttributeModifierPerk perkAr2 = new AttributeModifierPerk("med_inc_armor_1", 14, 10).setNameOverride(perkAr1);
        perkAr2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);

        AttributeModifierPerk perkArmorDodge = new MajorPerk("not_arm_armor_dodge", 13, 9);
        perkArmorDodge.addModifier(1.05F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_ARMOR);
        perkArmorDodge.addModifier(3F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_DODGE);

        AttributeModifierPerk perkRes1 = new AttributeModifierPerk("med_inc_resist", 16, 7);
        perkRes1.addModifier(0.06F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        AttributeModifierPerk perkRes2 = new AttributeModifierPerk("med_inc_resist_1", 16, 9).setNameOverride(perkRes1);
        perkRes2.addModifier(0.06F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);

        AttributeModifierPerk perkResistLife = new MajorPerk("not_arm_res_life", 17, 8);
        perkResistLife.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        perkResistLife.addModifier(1F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_HEALTH);

        AttributeModifierPerk perkResArmor = new MajorPerk("med_more_res", 18, 11);
        perkResArmor.addModifier(1.06F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        perkResArmor.addModifier(1.06F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_ARMOR);

        PERK_TREE.registerPerk(perkAr1)
                .connect(PERK_TREE.getAstralSorceryPerk("major_inc_armor"));
        PERK_TREE.registerPerk(perkArmorDodge)
                .connect(perkAr1);
        PERK_TREE.registerPerk(perkAr2)
                .connect(perkArmorDodge);

        PERK_TREE.registerPerk(perkRes1)
                .connect(PERK_TREE.getAstralSorceryPerk("major_inc_armor"));
        PERK_TREE.registerPerk(perkResistLife)
                .connect(perkRes1);
        PERK_TREE.registerPerk(perkRes2)
                .connect(perkResistLife);

        PERK_TREE.registerPerk(perkResArmor)
                .connect(perkAr2)
                .connect(perkRes2);
    }

    private static void initializeVicioBranch() {
        AttributeModifierPerk perkM1 = new AttributeModifierPerk("med_inc_ms", 1, 16);
        perkM1.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        AttributeModifierPerk perkM2 = new AttributeModifierPerk("med_inc_ms_1", 1, 18).setNameOverride(perkM1);
        perkM2.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);

        AttributeModifierPerk perkDodgeMs = new MajorPerk("not_vic_dodge_ms", 2, 17);
        perkDodgeMs.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        perkDodgeMs.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE);

        AttributeModifierPerk perkM3 = new AttributeModifierPerk("mec_inc_ms_2", -1, 17).setNameOverride(perkM1);
        perkM3.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        AttributeModifierPerk perkM4 = new AttributeModifierPerk("mec_inc_ms_3", -1, 19).setNameOverride(perkM1);
        perkM4.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);

        AttributeModifierPerk perkAtsMs = new MajorPerk("not_vic_ats", -2, 18);
        perkAtsMs.addModifier(0.15F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED);

        AttributeModifierPerk perkAddAts = new MajorPerk("med_add_ats_dodge", 0, 21);
        perkAddAts.addModifier(1F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_ATTACK_SPEED);
        perkAddAts.addModifier(5F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_DODGE);

        PERK_TREE.registerPerk(perkM1)
                .connect(PERK_TREE.getAstralSorceryPerk("major_inc_ms_fs"));
        PERK_TREE.registerPerk(perkDodgeMs)
                .connect(perkM1);
        PERK_TREE.registerPerk(perkM2)
                .connect(perkDodgeMs);

        PERK_TREE.registerPerk(perkM3)
                .connect(PERK_TREE.getAstralSorceryPerk("major_inc_ms_fs"));
        PERK_TREE.registerPerk(perkAtsMs)
                .connect(perkM3);
        PERK_TREE.registerPerk(perkM4)
                .connect(perkAtsMs);

        PERK_TREE.registerPerk(perkAddAts)
                .connect(perkM2)
                .connect(perkM4);
    }

    private static void initializeAevitasBranch() {
        AttributeModifierPerk perkL1 = new AttributeModifierPerk("med_inc_life", -15, 6);
        perkL1.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        AttributeModifierPerk perkL2 = new AttributeModifierPerk("med_inc_life_1", -17, 6).setNameOverride(perkL1);
        perkL2.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);

        AttributeModifierPerk perkArmorLife = new MajorPerk("not_aev_armor_life", -16, 5);
        perkArmorLife.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        perkArmorLife.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);

        AttributeModifierPerk perkL3 = new AttributeModifierPerk("med_inc_life_2", -14, 8).setNameOverride(perkL1);
        perkL3.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        AttributeModifierPerk perkL4 = new AttributeModifierPerk("med_inc_life_3", -16, 8).setNameOverride(perkL1);
        perkL4.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);

        AttributeModifierPerk perkAllResLife = new MajorPerk("not_aev_res_life", -15, 9);
        perkAllResLife.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        perkAllResLife.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);

        AttributeModifierPerk perkAddLife = new MajorPerk("med_add_life", -18, 9);
        perkAddLife.addModifier(2F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_HEALTH);

        PERK_TREE.registerPerk(perkL1)
                .connect(PERK_TREE.getAstralSorceryPerk("major_inc_life"));
        PERK_TREE.registerPerk(perkArmorLife)
                .connect(perkL1);
        PERK_TREE.registerPerk(perkL2)
                .connect(perkArmorLife);

        PERK_TREE.registerPerk(perkL3)
                .connect(PERK_TREE.getAstralSorceryPerk("major_inc_life"));
        PERK_TREE.registerPerk(perkAllResLife)
                .connect(perkL3);
        PERK_TREE.registerPerk(perkL4)
                .connect(perkAllResLife);

        PERK_TREE.registerPerk(perkAddLife)
                .connect(perkL2)
                .connect(perkL4);
    }

    //Registers T1 perk-effect perks
    private static void initializePerkCore() {
        float inc_t1 = 0.07F;

        AttributeModifierPerk perkEff1 = new AttributeModifierPerk("base_inc_perkeffect_t1", 1, -2);
        perkEff1.addModifier(inc_t1, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff2 = new AttributeModifierPerk("base_inc_perkeffect_t1_1", 2, 1).setNameOverride(perkEff1);
        perkEff2.addModifier(inc_t1, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff3 = new AttributeModifierPerk("base_inc_perkeffect_t1_2", -1, 2).setNameOverride(perkEff1);
        perkEff3.addModifier(inc_t1, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff4 = new AttributeModifierPerk("base_inc_perkeffect_t1_3", -2, -1).setNameOverride(perkEff1);
        perkEff4.addModifier(inc_t1, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        CoreRootPerk core = new CoreRootPerk();

        PERK_TREE.registerPerk(perkEff1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t2_2"));
        PERK_TREE.registerPerk(perkEff2)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t2_5"))
                .connect(perkEff1);
        PERK_TREE.registerPerk(perkEff3)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t2_8"))
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t2_11"))
                .connect(perkEff2);
        PERK_TREE.registerPerk(perkEff4)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t2_14"))
                .connect(perkEff3)
                .connect(perkEff1);
        PERK_TREE.registerPerk(core)
                .connect(perkEff1)
                .connect(perkEff3);
    }

    //Registers T2 perk-effect perks
    private static void initializePerkInteriorTravelWheel() {
        float inc_t2 = 0.05F;

        AttributeModifierPerk perkEffectEvDis1 = new AttributeModifierPerk("base_inc_perkeffect_t2", -1, -10);
        perkEffectEvDis1.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectEvDis2 = new AttributeModifierPerk("base_inc_perkeffect_t2_1", 1, -8).setNameOverride(perkEffectEvDis1);
        perkEffectEvDis2.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectEvDis3 = new AttributeModifierPerk("base_inc_perkeffect_t2_2", 0, -5).setNameOverride(perkEffectEvDis1);
        perkEffectEvDis3.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectDisArm1 = new AttributeModifierPerk("base_inc_perkeffect_t2_3", 10, -2).setNameOverride(perkEffectEvDis1);
        perkEffectDisArm1.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectDisArm2 = new AttributeModifierPerk("base_inc_perkeffect_t2_4", 7, -3).setNameOverride(perkEffectEvDis1);
        perkEffectDisArm2.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectDisArm3 = new AttributeModifierPerk("base_inc_perkeffect_t2_5", 4, -1).setNameOverride(perkEffectEvDis1);
        perkEffectDisArm3.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectArmVic1 = new AttributeModifierPerk("base_inc_perkeffect_t2_6", 6, 9).setNameOverride(perkEffectEvDis1);
        perkEffectArmVic1.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectArmVic2 = new AttributeModifierPerk("base_inc_perkeffect_t2_7", 4, 6).setNameOverride(perkEffectEvDis1);
        perkEffectArmVic2.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectArmVic3 = new AttributeModifierPerk("base_inc_perkeffect_t2_8", 1, 4).setNameOverride(perkEffectEvDis1);
        perkEffectArmVic3.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectVicAev1 = new AttributeModifierPerk("base_inc_perkeffect_t2_9", -6, 9).setNameOverride(perkEffectEvDis1);
        perkEffectVicAev1.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectVicAev2 = new AttributeModifierPerk("base_inc_perkeffect_t2_10", -5, 5).setNameOverride(perkEffectEvDis1);
        perkEffectVicAev2.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectVicAev3 = new AttributeModifierPerk("base_inc_perkeffect_t2_11", -2, 4).setNameOverride(perkEffectEvDis1);
        perkEffectVicAev3.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectAevEv1 = new AttributeModifierPerk("base_inc_perkeffect_t2_12", -10, -2).setNameOverride(perkEffectEvDis1);
        perkEffectAevEv1.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectAevEv2 = new AttributeModifierPerk("base_inc_perkeffect_t2_13", -7, -1).setNameOverride(perkEffectEvDis1);
        perkEffectAevEv2.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectAevEv3 = new AttributeModifierPerk("base_inc_perkeffect_t2_14", -5, -2).setNameOverride(perkEffectEvDis1);
        perkEffectAevEv3.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        PERK_TREE.registerPerk(perkEffectEvDis1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_1"));
        PERK_TREE.registerPerk(perkEffectEvDis2)
                .connect(perkEffectEvDis1);
        PERK_TREE.registerPerk(perkEffectEvDis3)
                .connect(perkEffectEvDis2);

        PERK_TREE.registerPerk(perkEffectDisArm1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_4"));
        PERK_TREE.registerPerk(perkEffectDisArm2)
                .connect(perkEffectDisArm1);
        PERK_TREE.registerPerk(perkEffectDisArm3)
                .connect(perkEffectDisArm2);

        PERK_TREE.registerPerk(perkEffectArmVic1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_7"));
        PERK_TREE.registerPerk(perkEffectArmVic2)
                .connect(perkEffectArmVic1);
        PERK_TREE.registerPerk(perkEffectArmVic3)
                .connect(perkEffectArmVic2);

        PERK_TREE.registerPerk(perkEffectVicAev1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_10"));
        PERK_TREE.registerPerk(perkEffectVicAev2)
                .connect(perkEffectVicAev1);
        PERK_TREE.registerPerk(perkEffectVicAev3)
                .connect(perkEffectVicAev2);

        PERK_TREE.registerPerk(perkEffectAevEv1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_13"));
        PERK_TREE.registerPerk(perkEffectAevEv2)
                .connect(perkEffectAevEv1);
        PERK_TREE.registerPerk(perkEffectAevEv3)
                .connect(perkEffectAevEv2);
    }

    //Registers T3 perk-effect perks
    private static void initializeRootPerkWheel() {
        float inc_t3 = 0.04F;

        AttributeModifierPerk perkEffectEvDis1 = new AttributeModifierPerk("base_inc_perkeffect_t3", -5, -12);
        perkEffectEvDis1.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectEvDis2 = new AttributeModifierPerk("base_inc_perkeffect_t3_1", 0, -13).setNameOverride(perkEffectEvDis1);
        perkEffectEvDis2.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectEvDis3 = new AttributeModifierPerk("base_inc_perkeffect_t3_2", 5, -12).setNameOverride(perkEffectEvDis1);
        perkEffectEvDis3.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectDisArm1 = new AttributeModifierPerk("base_inc_perkeffect_t3_3", 11, -7).setNameOverride(perkEffectEvDis1);
        perkEffectDisArm1.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectDisArm2 = new AttributeModifierPerk("base_inc_perkeffect_t3_4", 13, -3).setNameOverride(perkEffectEvDis1);
        perkEffectDisArm2.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectDisArm3 = new AttributeModifierPerk("base_inc_perkeffect_t3_5", 14, 2).setNameOverride(perkEffectEvDis1);
        perkEffectDisArm3.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectArmVic1 = new AttributeModifierPerk("base_inc_perkeffect_t3_6", 11, 9).setNameOverride(perkEffectEvDis1);
        perkEffectArmVic1.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectArmVic2 = new AttributeModifierPerk("base_inc_perkeffect_t3_7", 8, 12).setNameOverride(perkEffectEvDis1);
        perkEffectArmVic2.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectArmVic3 = new AttributeModifierPerk("base_inc_perkeffect_t3_8", 4, 13).setNameOverride(perkEffectEvDis1);
        perkEffectArmVic3.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectVicAev1 = new AttributeModifierPerk("base_inc_perkeffect_t3_9", -3, 13).setNameOverride(perkEffectEvDis1);
        perkEffectVicAev1.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectVicAev2 = new AttributeModifierPerk("base_inc_perkeffect_t3_10", -8, 12).setNameOverride(perkEffectEvDis1);
        perkEffectVicAev2.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectVicAev3 = new AttributeModifierPerk("base_inc_perkeffect_t3_11", -11, 9).setNameOverride(perkEffectEvDis1);
        perkEffectVicAev3.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectAevEv1 = new AttributeModifierPerk("base_inc_perkeffect_t3_12", -14, 2).setNameOverride(perkEffectEvDis1);
        perkEffectAevEv1.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectAevEv2 = new AttributeModifierPerk("base_inc_perkeffect_t3_13", -13, -3).setNameOverride(perkEffectEvDis1);
        perkEffectAevEv2.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectAevEv3 = new AttributeModifierPerk("base_inc_perkeffect_t3_14", -11, -7).setNameOverride(perkEffectEvDis1);
        perkEffectAevEv3.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        PERK_TREE.registerPerk(perkEffectEvDis1).connect(perkRootMajorHarvest);
        PERK_TREE.registerPerk(perkEffectEvDis2).connect(perkEffectEvDis1);
        PERK_TREE.registerPerk(perkEffectEvDis3).connect(perkEffectEvDis2).connect(perkRootMajorDamage);

        PERK_TREE.registerPerk(perkEffectDisArm1).connect(perkRootMajorDamage);
        PERK_TREE.registerPerk(perkEffectDisArm2).connect(perkEffectDisArm1);
        PERK_TREE.registerPerk(perkEffectDisArm3).connect(perkEffectDisArm2).connect(perkRootMajorArmor);

        PERK_TREE.registerPerk(perkEffectArmVic1).connect(perkRootMajorArmor);
        PERK_TREE.registerPerk(perkEffectArmVic2).connect(perkEffectArmVic1);
        PERK_TREE.registerPerk(perkEffectArmVic3).connect(perkEffectArmVic2).connect(perkRootMajorMovespeed);

        PERK_TREE.registerPerk(perkEffectVicAev1).connect(perkRootMajorMovespeed);
        PERK_TREE.registerPerk(perkEffectVicAev2).connect(perkEffectVicAev1);
        PERK_TREE.registerPerk(perkEffectVicAev3).connect(perkEffectVicAev2).connect(perkRootMajorHealth);

        PERK_TREE.registerPerk(perkEffectAevEv1).connect(perkRootMajorHealth);
        PERK_TREE.registerPerk(perkEffectAevEv2).connect(perkEffectAevEv1);
        PERK_TREE.registerPerk(perkEffectAevEv3).connect(perkEffectAevEv2).connect(perkRootMajorHarvest);
    }

    private static void initializeEvorsioRoot() {
        AttributeModifierPerk breakRoot1 = new AttributeModifierPerk("base_inc_harvest", -7, -7);
        breakRoot1.addModifier(0.10F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        AttributeModifierPerk breakRoot2 = new AttributeModifierPerk("base_inc_harvest_1", -6, -9).setNameOverride(breakRoot1);
        breakRoot2.addModifier(0.10F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        perkRootMajorHarvest = new MajorPerk("major_inc_harvest", -9, -10);
        perkRootMajorHarvest.addModifier(1.05F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        perkRootMajorHarvest.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);

        PointConnector ctHarvest1 = PERK_TREE.registerPerk(breakRoot1);
        PointConnector ctHarvest2 = PERK_TREE.registerPerk(breakRoot2);
        PointConnector ctMajorHarvest = PERK_TREE.registerPerk(perkRootMajorHarvest);

        ctHarvest1.connect(PERK_TREE.getRootPerk(Constellations.evorsio));
        ctHarvest2.connect(ctHarvest1);
        ctMajorHarvest.connect(ctHarvest2);
    }

    private static void initializeDiscidiaRoot() {
        AttributeModifierPerk dmgRoot1 = new AttributeModifierPerk("base_inc__melee_damage", 7, -7);
        dmgRoot1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        AttributeModifierPerk dmgRoot2 = new AttributeModifierPerk("base_inc_proj_damage", 6, -9);
        dmgRoot2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        perkRootMajorDamage = new MajorPerk("major_inc_damage", 9, -10);
        perkRootMajorDamage.addModifier(1.05F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkRootMajorDamage.addModifier(1.05F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        perkRootMajorDamage.addModifier(2F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE);

        PointConnector ctDamage1 = PERK_TREE.registerPerk(dmgRoot1);
        PointConnector ctDamage2 = PERK_TREE.registerPerk(dmgRoot2);
        PointConnector ctMajorDamage = PERK_TREE.registerPerk(perkRootMajorDamage);

        ctDamage1.connect(PERK_TREE.getRootPerk(Constellations.discidia));
        ctDamage2.connect(ctDamage1);
        ctMajorDamage.connect(ctDamage2);
    }

    private static void initializeArmaraRoot() {
        AttributeModifierPerk armorRoot1 = new AttributeModifierPerk("base_inc_armor", 9, 3);
        armorRoot1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        AttributeModifierPerk armorRoot2 = new AttributeModifierPerk("base_inc_armor_1", 8, 5).setNameOverride(armorRoot1);
        armorRoot2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        perkRootMajorArmor = new MajorPerk("major_inc_armor", 12, 6);
        perkRootMajorArmor.addModifier(1.20F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_ARMOR);

        PointConnector ctArmor1 = PERK_TREE.registerPerk(armorRoot1);
        PointConnector ctArmor2 = PERK_TREE.registerPerk(armorRoot2);
        PointConnector ctMajorArmor = PERK_TREE.registerPerk(perkRootMajorArmor);

        ctArmor1.connect(PERK_TREE.getRootPerk(Constellations.armara));
        ctArmor2.connect(ctArmor1);
        ctMajorArmor.connect(ctArmor2);
    }

    private static void initializeVicioRoot() {
        AttributeModifierPerk moveRoot1 = new AttributeModifierPerk("base_inc_ms", 1, 10);
        moveRoot1.addModifier(0.03F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        AttributeModifierPerk moveRoot2 = new AttributeModifierPerk("base_inc_ms_1", -1, 11).setNameOverride(moveRoot1);
        moveRoot2.addModifier(0.03F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        perkRootMajorMovespeed = new MajorPerk("major_inc_ms_fs", 0, 14);
        perkRootMajorMovespeed.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        perkRootMajorMovespeed.addModifier(5F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_DODGE);

        PointConnector ctMove1 = PERK_TREE.registerPerk(moveRoot1);
        PointConnector ctMove2 = PERK_TREE.registerPerk(moveRoot2);
        PointConnector ctMajorMobility = PERK_TREE.registerPerk(perkRootMajorMovespeed);

        ctMove1.connect(PERK_TREE.getRootPerk(Constellations.vicio));
        ctMove2.connect(ctMove1);
        ctMajorMobility.connect(ctMove2);
    }

    private static void initializeAevitasRoot() {
        AttributeModifierPerk lifeRoot1 = new AttributeModifierPerk("base_inc_life", -9, 3);
        lifeRoot1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        AttributeModifierPerk lifeRoot2 = new AttributeModifierPerk("base_inc_life_1", -8, 5).setNameOverride(lifeRoot1);
        lifeRoot2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        perkRootMajorHealth = new MajorPerk("major_inc_life", -12, 6);
        perkRootMajorHealth.addModifier(1.15F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_HEALTH);

        PointConnector ctLife1 = PERK_TREE.registerPerk(lifeRoot1);
        PointConnector ctLife2 = PERK_TREE.registerPerk(lifeRoot2);
        PointConnector ctMajorLife = PERK_TREE.registerPerk(perkRootMajorHealth);

        ctLife1.connect(PERK_TREE.getRootPerk(Constellations.aevitas));
        ctLife2.connect(ctLife1);
        ctMajorLife.connect(ctLife2);
    }

    private static void initializeRoot() {
        RootPerk rootAevitas = new AevitasRootPerk(-6,  2);
        rootAevitas.addModifier(2F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_HEALTH);

        RootPerk rootVicio = new VicioRootPerk(0,  7);
        rootVicio.addModifier(1F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_REACH);

        RootPerk rootArmara = new ArmaraRootPerk(6,  2);
        rootArmara.addModifier(1.15F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);

        RootPerk rootDiscidia = new DiscidiaRootPerk(4, -5);
        rootDiscidia.addModifier(10F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE);

        RootPerk rootEvorsio = new EvorsioRootPerk(-4, -5);
        rootEvorsio.addModifier(1.2F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);

        PERK_TREE.registerRootPerk(rootAevitas);
        PERK_TREE.registerRootPerk(rootVicio);
        PERK_TREE.registerRootPerk(rootArmara);
        PERK_TREE.registerRootPerk(rootDiscidia);
        PERK_TREE.registerRootPerk(rootEvorsio);
    }

    private static void initializeAttributeTypes() {
        registerPerkType(new AttributeTypePerkEffect());
        registerPerkType(new AttributeBreakSpeed());
        registerPerkType(new AttributeCritChance());
        registerPerkType(new AttributeCritMultiplier());
        registerPerkType(new AttributeAllElementalResist());
        registerPerkType(new AttributeDodge());
        registerPerkType(new AttributeProjectileAttackDamage());
        registerPerkType(new AttributeArrowSpeed());
        registerPerkType(new PerkAttributeType(ATTR_TYPE_INC_PERK_EXP));
        registerPerkType(new AttributeLifeRecovery());
        registerPerkType(new AttributePotionDuration());
        registerPerkType(new PerkAttributeType(ATTR_TYPE_ARC_CHAINS));
        registerPerkType(new PerkAttributeType(ATTR_TYPE_BLEED_DURATION));
        registerPerkType(new PerkAttributeType(ATTR_TYPE_BLEED_STACKS));
        registerPerkType(new PerkAttributeType(ATTR_TYPE_MINING_CHAIN_LENGTH));
        registerPerkType(new PerkAttributeType(ATTR_TYPE_MINING_CHAIN_CHANCE));
        registerPerkType(new PerkAttributeType(ATTR_TYPE_MINING_CHAIN_SUCCESSIVECHAIN));
        registerPerkType(new AttributeLifeLeech());
        registerPerkType(new AttributeThorns());
        registerPerkType(new PerkAttributeType(ATTR_TYPE_INC_THORNS_RANGED));
        registerPerkType(new AttributeEnchantmentEffectiveness());

        registerPerkType(new AttributeTypeMeleeAttackDamage());
        registerPerkType(new AttributeTypeMaxHealth());
        registerPerkType(new AttributeTypeMovementSpeed());
        registerPerkType(new AttributeTypeSwimSpeed());
        registerPerkType(new AttributeTypeArmor());
        registerPerkType(new AttributeTypeArmorToughness());
        registerPerkType(new AttributeTypeAttackSpeed());
        registerPerkType(new AttributeTypeMaxReach());

        limitPerkType(ATTR_TYPE_INC_DODGE, 0F, 0.75F);
        limitPerkType(ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST, 0F, 0.75F);
        limitPerkType(ATTR_TYPE_ATTACK_LIFE_LEECH, 0F, 0.25F);

        MinecraftForge.EVENT_BUS.post(new APIRegistryEvent.PerkAttributeTypeRegister());
    }

}
