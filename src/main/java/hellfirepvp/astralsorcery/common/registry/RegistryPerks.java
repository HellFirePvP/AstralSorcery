/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.type.*;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreeMajor;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.CoreRootPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.PerkTreeConnector;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.root.*;
import hellfirepvp.astralsorcery.common.event.APIRegistryEvent;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import net.minecraftforge.common.MinecraftForge;

import static hellfirepvp.astralsorcery.common.constellation.perk.attribute.type.AttributeTypeRegistry.*;
import static hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree.*;

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
        initializeTreeConnectorPerks();

        MinecraftForge.EVENT_BUS.post(new APIRegistryEvent.PerkRegister());

        initializeAttributeTypes();
    }

    private static void initializeTreeConnectorPerks() {
        float more_ch = 1.02F;

        AttributeModifierPerk perkEvorsioCh1 = new AttributeModifierPerk("threshold_evorsio", -11, -21);
        perkEvorsioCh1.addModifier(more_ch, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEvorsioCh2 = new AttributeModifierPerk("threshold_evorsio_1", -13, -24).setNameOverride(perkEvorsioCh1.getUnlocalizedName());
        perkEvorsioCh2.addModifier(more_ch, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEvorsioCh3 = new AttributeModifierPerk("threshold_evorsio_2", -9, -24).setNameOverride(perkEvorsioCh1.getUnlocalizedName());
        perkEvorsioCh3.addModifier(more_ch, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        PerkTreeConnector thresholdEvorsio = new PerkTreeConnector("epi_evorsio", -11, -23);

        PERK_TREE.registerPerk(perkEvorsioCh1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4"));
        PERK_TREE.registerPerk(perkEvorsioCh2)
                .connect(perkEvorsioCh1);
        PERK_TREE.registerPerk(perkEvorsioCh3)
                .connect(perkEvorsioCh2)
                .connect(perkEvorsioCh1);
        PERK_TREE.registerPerk(thresholdEvorsio)
                .connect(perkEvorsioCh1)
                .connect(perkEvorsioCh2)
                .connect(perkEvorsioCh3);

        AttributeModifierPerk perkArmaraCh1 = new AttributeModifierPerk("threshold_armara", 21, 0);
        perkArmaraCh1.addModifier(more_ch, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkArmaraCh2 = new AttributeModifierPerk("threshold_armara_1", 24, 2).setNameOverride(perkArmaraCh1.getUnlocalizedName());
        perkArmaraCh2.addModifier(more_ch, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkArmaraCh3 = new AttributeModifierPerk("threshold_armara_2", 24, -2).setNameOverride(perkArmaraCh1.getUnlocalizedName());
        perkArmaraCh3.addModifier(more_ch, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        PerkTreeConnector thresholdArmara = new PerkTreeConnector("epi_armara", 23, 0);

        PERK_TREE.registerPerk(perkArmaraCh1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_6"));
        PERK_TREE.registerPerk(perkArmaraCh2)
                .connect(perkArmaraCh1);
        PERK_TREE.registerPerk(perkArmaraCh3)
                .connect(perkArmaraCh2)
                .connect(perkArmaraCh1);
        PERK_TREE.registerPerk(thresholdArmara)
                .connect(perkArmaraCh1)
                .connect(perkArmaraCh2)
                .connect(perkArmaraCh3);

        AttributeModifierPerk perkVicioCh1 = new AttributeModifierPerk("threshold_vicio", -10, 22);
        perkVicioCh1.addModifier(more_ch, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkVicioCh2 = new AttributeModifierPerk("threshold_vicio_1", -8, 25).setNameOverride(perkVicioCh1.getUnlocalizedName());
        perkVicioCh2.addModifier(more_ch, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkVicioCh3 = new AttributeModifierPerk("threshold_vicio_2", -12, 25).setNameOverride(perkVicioCh1.getUnlocalizedName());
        perkVicioCh3.addModifier(more_ch, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        PerkTreeConnector thresholdVicio = new PerkTreeConnector("epi_vicio", -10, 24);

        PERK_TREE.registerPerk(perkVicioCh1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t4_14"));
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
        String unloc;

        // Evorsio -> Discidia
        AttributeModifierPerk perkEff1 = new AttributeModifierPerk("base_inc_perkeffect_t4", -10, -18);
        perkEff1.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        unloc = perkEff1.getUnlocalizedName();
        AttributeModifierPerk perkEff2 = new AttributeModifierPerk("base_inc_perkeffect_t4_1", -3, -20).setNameOverride(unloc);
        perkEff2.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff3 = new AttributeModifierPerk("base_inc_perkeffect_t4_2", 5, -21).setNameOverride(unloc);
        perkEff3.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff4 = new AttributeModifierPerk("base_inc_perkeffect_t4_3", 9, -18).setNameOverride(unloc);
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
        AttributeModifierPerk perkEff5 = new AttributeModifierPerk("base_inc_perkeffect_t4_4", 17, -12).setNameOverride(unloc);
        perkEff5.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff6 = new AttributeModifierPerk("base_inc_perkeffect_t4_5", 19, -7).setNameOverride(unloc);
        perkEff6.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff7 = new AttributeModifierPerk("base_inc_perkeffect_t4_6", 18, -1).setNameOverride(unloc);
        perkEff7.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff8 = new AttributeModifierPerk("base_inc_perkeffect_t4_7", 19, 6).setNameOverride(unloc);
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
        AttributeModifierPerk perkEff9 = new AttributeModifierPerk("base_inc_perkeffect_t4_9", 15, 13).setNameOverride(unloc);
        perkEff9.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff10 = new AttributeModifierPerk("base_inc_perkeffect_t4_10", 14, 17).setNameOverride(unloc);
        perkEff10.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff11 = new AttributeModifierPerk("base_inc_perkeffect_t4_11", 9, 18).setNameOverride(unloc);
        perkEff11.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff12 = new AttributeModifierPerk("base_inc_perkeffect_t4_12", 5, 19).setNameOverride(unloc);
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
        AttributeModifierPerk perkEff13 = new AttributeModifierPerk("base_inc_perkeffect_t4_13", -5, 20).setNameOverride(unloc);
        perkEff13.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff14 = new AttributeModifierPerk("base_inc_perkeffect_t4_14", -9, 19).setNameOverride(unloc);
        perkEff14.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff15 = new AttributeModifierPerk("base_inc_perkeffect_t4_15", -14, 17).setNameOverride(unloc);
        perkEff15.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff16 = new AttributeModifierPerk("base_inc_perkeffect_t4_16", -15, 12).setNameOverride(unloc);
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
        AttributeModifierPerk perkEff17 = new AttributeModifierPerk("base_inc_perkeffect_t4_17", -19, 4).setNameOverride(unloc);
        perkEff17.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff18 = new AttributeModifierPerk("base_inc_perkeffect_t4_18", -20, -1).setNameOverride(unloc);
        perkEff18.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff19 = new AttributeModifierPerk("base_inc_perkeffect_t4_19", -18, -6).setNameOverride(unloc);
        perkEff19.addModifier(inc_t4, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff20 = new AttributeModifierPerk("base_inc_perkeffect_t4_20", -16, -10).setNameOverride(unloc);
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
        AttributeModifierPerk perkM2 = new AttributeModifierPerk("med_inc_hrv_speed_1", -13, -11).setNameOverride(perkM1.getUnlocalizedName());
        perkM2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);

        AttributeModifierPerk perkHrvAts = new AttributeModifierPerk("not_evo_hrv_ats", -12, -10) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkHrvAts.addModifier(1.1F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        perkHrvAts.addModifier(1.1F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_ATTACK_SPEED);

        AttributeModifierPerk perkS1 = new AttributeModifierPerk("med_inc_hrv_speed_2", -10, -13).setNameOverride(perkM1.getUnlocalizedName());
        perkS1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        AttributeModifierPerk perkS2 = new AttributeModifierPerk("med_inc_hrv_speed_3", -12, -13).setNameOverride(perkM1.getUnlocalizedName());
        perkS2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);

        AttributeModifierPerk perkHrvReach = new AttributeModifierPerk("not_evo_hrv_reach", -11, -14) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkHrvReach.addModifier(0.08F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        perkHrvReach.addModifier(0.15F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_REACH);

        AttributeModifierPerk perkAddedHrvSpeed = new AttributeModifierPerk("med_added_hrv_speed", -14, -14) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkAddedHrvSpeed.addModifier(1.5F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_HARVEST_SPEED);
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
        AttributeModifierPerk perkP2 = new AttributeModifierPerk("med_inc_proj_damage_1", 13, -12).setNameOverride(perkP1.getUnlocalizedName());
        perkP2.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);

        AttributeModifierPerk perkProjCrit = new AttributeModifierPerk("not_dsc_proj_crit", 12, -11) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkProjCrit.addModifier(1.05F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        perkProjCrit.addModifier(0.02F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE);

        AttributeModifierPerk perkM1 = new AttributeModifierPerk("med_inc_melee_damage", 10, -14);
        perkM1.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        AttributeModifierPerk perkM2 = new AttributeModifierPerk("med_inc_melee_damage_1", 12, -14).setNameOverride(perkM1.getUnlocalizedName());
        perkM2.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);

        AttributeModifierPerk perkMeleeMulti = new AttributeModifierPerk("not_dsc_melee_multi", 11, -15) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkMeleeMulti.addModifier(1.05F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkMeleeMulti.addModifier(0.3F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_MULTIPLIER);

        AttributeModifierPerk perkReachProjSpeed = new AttributeModifierPerk("med_reach_arrowspeed", 15, -16) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
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
        AttributeModifierPerk perkAr2 = new AttributeModifierPerk("med_inc_armor_1", 14, 10).setNameOverride(perkAr1.getUnlocalizedName());
        perkAr2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);

        AttributeModifierPerk perkArmorDodge = new AttributeModifierPerk("not_arm_armor_dodge", 13, 9) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkArmorDodge.addModifier(1.05F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_ARMOR);
        perkArmorDodge.addModifier(0.03F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_DODGE);

        AttributeModifierPerk perkRes1 = new AttributeModifierPerk("med_inc_resist", 16, 7);
        perkRes1.addModifier(-0.06F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        AttributeModifierPerk perkRes2 = new AttributeModifierPerk("med_inc_resist_1", 16, 9).setNameOverride(perkRes1.getUnlocalizedName());
        perkRes2.addModifier(-0.06F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);

        AttributeModifierPerk perkResistLife = new AttributeModifierPerk("not_arm_res_life", 17, 8) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkResistLife.addModifier(-0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        perkResistLife.addModifier(1F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_HEALTH);

        AttributeModifierPerk perkResArmor = new AttributeModifierPerk("med_more_res", 18, 11) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkResArmor.addModifier(0.94F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);
        perkResArmor.addModifier(0.06F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_ARMOR);

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
        String unloc;

        AttributeModifierPerk perkM1 = new AttributeModifierPerk("med_inc_ms", 1, 16);
        perkM1.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        unloc = perkM1.getUnlocalizedName();
        AttributeModifierPerk perkM2 = new AttributeModifierPerk("med_inc_ms_1", 1, 18).setNameOverride(unloc);
        perkM2.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);

        AttributeModifierPerk perkDodgeMs = new AttributeModifierPerk("not_vic_dodge_ms", 2, 17) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkDodgeMs.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        perkDodgeMs.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE);

        AttributeModifierPerk perkM3 = new AttributeModifierPerk("mec_inc_ms_2", -1, 17).setNameOverride(unloc);
        perkM3.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        AttributeModifierPerk perkM4 = new AttributeModifierPerk("mec_inc_ms_3", -1, 19).setNameOverride(unloc);
        perkM4.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);

        AttributeModifierPerk perkAtsMs = new AttributeModifierPerk("not_vic_ats", -2, 18) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkAtsMs.addModifier(0.15F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED);

        AttributeModifierPerk perkAddAts = new AttributeModifierPerk("med_add_ats_dodge", 0, 21) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkAddAts.addModifier(0.5F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_ATTACK_SPEED);
        perkAddAts.addModifier(0.05F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_DODGE);

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
        String unloc;

        AttributeModifierPerk perkL1 = new AttributeModifierPerk("med_inc_life", -15, 6);
        perkL1.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        unloc = perkL1.getUnlocalizedName();
        AttributeModifierPerk perkL2 = new AttributeModifierPerk("med_inc_life_1", -17, 6).setNameOverride(unloc);
        perkL2.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);

        AttributeModifierPerk perkArmorLife = new AttributeModifierPerk("not_aev_armor_life", -16, 5) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkArmorLife.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        perkArmorLife.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);

        AttributeModifierPerk perkL3 = new AttributeModifierPerk("med_inc_life_2", -14, 8).setNameOverride(unloc);
        perkL3.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        AttributeModifierPerk perkL4 = new AttributeModifierPerk("med_inc_life_3", -16, 8).setNameOverride(unloc);
        perkL4.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);

        AttributeModifierPerk perkAllResLife = new AttributeModifierPerk("not_aev_res_life", -15, 9) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkAllResLife.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        perkAllResLife.addModifier(-0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);

        AttributeModifierPerk perkAddLife = new AttributeModifierPerk("med_add_life", -18, 9) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
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
        float inc_t1 = 0.7F;
        String unloc;

        AttributeModifierPerk perkEff1 = new AttributeModifierPerk("base_inc_perkeffect_t1", 1, -2);
        perkEff1.addModifier(inc_t1, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        unloc = perkEff1.getUnlocalizedName();
        AttributeModifierPerk perkEff2 = new AttributeModifierPerk("base_inc_perkeffect_t1_1", 2, 1).setNameOverride(unloc);
        perkEff2.addModifier(inc_t1, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff3 = new AttributeModifierPerk("base_inc_perkeffect_t1_2", -1, 2).setNameOverride(unloc);
        perkEff3.addModifier(inc_t1, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEff4 = new AttributeModifierPerk("base_inc_perkeffect_t1_3", -2, -1).setNameOverride(unloc);
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
        String unloc;

        AttributeModifierPerk perkEffectEvDis1 = new AttributeModifierPerk("base_inc_perkeffect_t2", -1, -10);
        perkEffectEvDis1.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        unloc = perkEffectEvDis1.getUnlocalizedName();
        AttributeModifierPerk perkEffectEvDis2 = new AttributeModifierPerk("base_inc_perkeffect_t2_1", 1, -8).setNameOverride(unloc);
        perkEffectEvDis2.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectEvDis3 = new AttributeModifierPerk("base_inc_perkeffect_t2_2", 0, -5).setNameOverride(unloc);
        perkEffectEvDis3.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectDisArm1 = new AttributeModifierPerk("base_inc_perkeffect_t2_3", 10, -2).setNameOverride(unloc);
        perkEffectDisArm1.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectDisArm2 = new AttributeModifierPerk("base_inc_perkeffect_t2_4", 7, -3).setNameOverride(unloc);
        perkEffectDisArm2.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectDisArm3 = new AttributeModifierPerk("base_inc_perkeffect_t2_5", 4, -1).setNameOverride(unloc);
        perkEffectDisArm3.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectArmVic1 = new AttributeModifierPerk("base_inc_perkeffect_t2_6", 6, 9).setNameOverride(unloc);
        perkEffectArmVic1.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectArmVic2 = new AttributeModifierPerk("base_inc_perkeffect_t2_7", 4, 6).setNameOverride(unloc);
        perkEffectArmVic2.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectArmVic3 = new AttributeModifierPerk("base_inc_perkeffect_t2_8", 1, 4).setNameOverride(unloc);
        perkEffectArmVic3.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectVicAev1 = new AttributeModifierPerk("base_inc_perkeffect_t2_9", -6, 9).setNameOverride(unloc);
        perkEffectVicAev1.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectVicAev2 = new AttributeModifierPerk("base_inc_perkeffect_t2_10", -5, 5).setNameOverride(unloc);
        perkEffectVicAev2.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectVicAev3 = new AttributeModifierPerk("base_inc_perkeffect_t2_11", -2, 4).setNameOverride(unloc);
        perkEffectVicAev3.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectAevEv1 = new AttributeModifierPerk("base_inc_perkeffect_t2_12", -10, -2).setNameOverride(unloc);
        perkEffectAevEv1.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectAevEv2 = new AttributeModifierPerk("base_inc_perkeffect_t2_13", -7, -1).setNameOverride(unloc);
        perkEffectAevEv2.addModifier(inc_t2, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectAevEv3 = new AttributeModifierPerk("base_inc_perkeffect_t2_14", -5, -2).setNameOverride(unloc);
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
        String unloc;

        AttributeModifierPerk perkEffectEvDis1 = new AttributeModifierPerk("base_inc_perkeffect_t3", -5, -12);
        perkEffectEvDis1.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        unloc = perkEffectEvDis1.getUnlocalizedName();
        AttributeModifierPerk perkEffectEvDis2 = new AttributeModifierPerk("base_inc_perkeffect_t3_1", 0, -13).setNameOverride(unloc);
        perkEffectEvDis2.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectEvDis3 = new AttributeModifierPerk("base_inc_perkeffect_t3_2", 5, -12).setNameOverride(unloc);
        perkEffectEvDis3.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectDisArm1 = new AttributeModifierPerk("base_inc_perkeffect_t3_3", 11, -7).setNameOverride(unloc);
        perkEffectDisArm1.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectDisArm2 = new AttributeModifierPerk("base_inc_perkeffect_t3_4", 13, -3).setNameOverride(unloc);
        perkEffectDisArm2.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectDisArm3 = new AttributeModifierPerk("base_inc_perkeffect_t3_5", 14, 2).setNameOverride(unloc);
        perkEffectDisArm3.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectArmVic1 = new AttributeModifierPerk("base_inc_perkeffect_t3_6", 11, 9).setNameOverride(unloc);
        perkEffectArmVic1.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectArmVic2 = new AttributeModifierPerk("base_inc_perkeffect_t3_7", 8, 12).setNameOverride(unloc);
        perkEffectArmVic2.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectArmVic3 = new AttributeModifierPerk("base_inc_perkeffect_t3_8", 4, 13).setNameOverride(unloc);
        perkEffectArmVic3.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectVicAev1 = new AttributeModifierPerk("base_inc_perkeffect_t3_9", -3, 13).setNameOverride(unloc);
        perkEffectVicAev1.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectVicAev2 = new AttributeModifierPerk("base_inc_perkeffect_t3_10", -8, 12).setNameOverride(unloc);
        perkEffectVicAev2.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectVicAev3 = new AttributeModifierPerk("base_inc_perkeffect_t3_11", -11, 9).setNameOverride(unloc);
        perkEffectVicAev3.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectAevEv1 = new AttributeModifierPerk("base_inc_perkeffect_t3_12", -14, 2).setNameOverride(unloc);
        perkEffectAevEv1.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectAevEv2 = new AttributeModifierPerk("base_inc_perkeffect_t3_13", -13, -3).setNameOverride(unloc);
        perkEffectAevEv2.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectAevEv3 = new AttributeModifierPerk("base_inc_perkeffect_t3_14", -11, -7).setNameOverride(unloc);
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
        String unlocHrv = breakRoot1.getUnlocalizedName();
        AttributeModifierPerk breakRoot2 = new AttributeModifierPerk("base_inc_harvest_1", -6, -9).setNameOverride(unlocHrv);
        breakRoot2.addModifier(0.10F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        perkRootMajorHarvest = new AttributeModifierPerk("major_inc_harvest", -9, -10) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkRootMajorHarvest.addModifier(1.05F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        perkRootMajorHarvest.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);

        PerkTree.PointConnector ctHarvest1 = PERK_TREE.registerPerk(breakRoot1);
        PerkTree.PointConnector ctHarvest2 = PERK_TREE.registerPerk(breakRoot2);
        PerkTree.PointConnector ctMajorHarvest = PERK_TREE.registerPerk(perkRootMajorHarvest);

        ctHarvest1.connect(PERK_TREE.getRootPerk(Constellations.evorsio));
        ctHarvest2.connect(ctHarvest1);
        ctMajorHarvest.connect(ctHarvest2);
    }

    private static void initializeDiscidiaRoot() {
        AttributeModifierPerk dmgRoot1 = new AttributeModifierPerk("base_inc__melee_damage", 7, -7);
        dmgRoot1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        AttributeModifierPerk dmgRoot2 = new AttributeModifierPerk("base_inc_proj_damage", 6, -9);
        dmgRoot2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkRootMajorDamage = new AttributeModifierPerk("major_inc_damage", 9, -10) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkRootMajorDamage.addModifier(1.05F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE);
        perkRootMajorDamage.addModifier(1.05F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE);
        perkRootMajorDamage.addModifier(0.02F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE);

        PerkTree.PointConnector ctDamage1 = PERK_TREE.registerPerk(dmgRoot1);
        PerkTree.PointConnector ctDamage2 = PERK_TREE.registerPerk(dmgRoot2);
        PerkTree.PointConnector ctMajorDamage = PERK_TREE.registerPerk(perkRootMajorDamage);

        ctDamage1.connect(PERK_TREE.getRootPerk(Constellations.discidia));
        ctDamage2.connect(ctDamage1);
        ctMajorDamage.connect(ctDamage2);
    }

    private static void initializeArmaraRoot() {
        AttributeModifierPerk armorRoot1 = new AttributeModifierPerk("base_inc_armor", 9, 3);
        armorRoot1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        String unlocArmor = armorRoot1.getUnlocalizedName();
        AttributeModifierPerk armorRoot2 = new AttributeModifierPerk("base_inc_armor_1", 8, 5).setNameOverride(unlocArmor);
        armorRoot2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_ARMOR);
        perkRootMajorArmor = new AttributeModifierPerk("major_inc_armor", 12, 6) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkRootMajorArmor.addModifier(1.20F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_ARMOR);

        PerkTree.PointConnector ctArmor1 = PERK_TREE.registerPerk(armorRoot1);
        PerkTree.PointConnector ctArmor2 = PERK_TREE.registerPerk(armorRoot2);
        PerkTree.PointConnector ctMajorArmor = PERK_TREE.registerPerk(perkRootMajorArmor);

        ctArmor1.connect(PERK_TREE.getRootPerk(Constellations.armara));
        ctArmor2.connect(ctArmor1);
        ctMajorArmor.connect(ctArmor2);
    }

    private static void initializeVicioRoot() {
        AttributeModifierPerk moveRoot1 = new AttributeModifierPerk("base_inc_ms", 1, 10);
        moveRoot1.addModifier(0.03F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        String unlocMoveSpeed = moveRoot1.getUnlocalizedName();
        AttributeModifierPerk moveRoot2 = new AttributeModifierPerk("base_inc_ms_1", -1, 11).setNameOverride(unlocMoveSpeed);
        moveRoot2.addModifier(0.03F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        perkRootMajorMovespeed = new AttributeModifierPerk("major_inc_ms_fs", 0, 14) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkRootMajorMovespeed.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        perkRootMajorMovespeed.addModifier(0.05F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_DODGE);

        PerkTree.PointConnector ctMove1 = PERK_TREE.registerPerk(moveRoot1);
        PerkTree.PointConnector ctMove2 = PERK_TREE.registerPerk(moveRoot2);
        PerkTree.PointConnector ctMajorMobility = PERK_TREE.registerPerk(perkRootMajorMovespeed);

        ctMove1.connect(PERK_TREE.getRootPerk(Constellations.vicio));
        ctMove2.connect(ctMove1);
        ctMajorMobility.connect(ctMove2);
    }

    private static void initializeAevitasRoot() {
        AttributeModifierPerk lifeRoot1 = new AttributeModifierPerk("base_inc_life", -9, 3);
        lifeRoot1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        String unlocLife = lifeRoot1.getUnlocalizedName();
        AttributeModifierPerk lifeRoot2 = new AttributeModifierPerk("base_inc_life_1", -8, 5).setNameOverride(unlocLife);
        lifeRoot2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_HEALTH);
        perkRootMajorHealth = new AttributeModifierPerk("major_inc_life", -12, 6) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkRootMajorHealth.addModifier(1.15F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_HEALTH);

        PerkTree.PointConnector ctLife1 = PERK_TREE.registerPerk(lifeRoot1);
        PerkTree.PointConnector ctLife2 = PERK_TREE.registerPerk(lifeRoot2);
        PerkTree.PointConnector ctMajorLife = PERK_TREE.registerPerk(perkRootMajorHealth);

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
        rootArmara.addModifier(0.9F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);

        RootPerk rootDiscidia = new DiscidiaRootPerk(4, -5);
        rootDiscidia.addModifier(3F, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE);

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

        registerPerkType(new AttributeTypeMeleeAttackDamage());
        registerPerkType(new AttributeTypeMaxHealth());
        registerPerkType(new AttributeTypeMovementSpeed());
        registerPerkType(new AttributeTypeArmor());
        registerPerkType(new AttributeTypeAttackSpeed());
        registerPerkType(new AttributeTypeMaxReach());

        MinecraftForge.EVENT_BUS.post(new APIRegistryEvent.PerkAttributeTypeRegister());
    }

    /*public static void init() {
        ConstellationPerkMap map = new ConstellationPerkMap();
        map.addPerk(ConstellationPerks.DMG_INCREASE,  ConstellationPerkMap.PerkOrder.DEFAULT,  1, 13);

        map.addPerk(ConstellationPerks.DMG_DISTANCE,  ConstellationPerkMap.PerkOrder.DEFAULT,  5, 12,
                ConstellationPerks.DMG_INCREASE);

        map.addPerk(ConstellationPerks.DMG_KNOCKBACK, ConstellationPerkMap.PerkOrder.DEFAULT,  2,  9,
                ConstellationPerks.DMG_INCREASE);

        map.addPerk(ConstellationPerks.DMG_AFTERKILL, ConstellationPerkMap.PerkOrder.DEFAULT,  8,  6,
                ConstellationPerks.DMG_KNOCKBACK,
                ConstellationPerks.DMG_DISTANCE);

        map.addPerk(ConstellationPerks.DMG_BLEED,     ConstellationPerkMap.PerkOrder.DEFAULT,  13,  1,
                ConstellationPerks.DMG_AFTERKILL);

        map.addPerk(ConstellationPerks.DMG_REFLECT,   ConstellationPerkMap.PerkOrder.DEFAULT,  10, 2,
                ConstellationPerks.DMG_AFTERKILL);

        ConstellationPerkMapRegistry.registerPerkMap(Constellations.discidia, map);

        map = new ConstellationPerkMap();

        map.addPerk(ConstellationPerks.TRV_MOVESPEED,   ConstellationPerkMap.PerkOrder.DEFAULT,  1, 13);

        map.addPerk(ConstellationPerks.TRV_SWIMSPEED,   ConstellationPerkMap.PerkOrder.DEFAULT,  5,  9,
                ConstellationPerks.TRV_MOVESPEED);

        map.addPerk(ConstellationPerks.TRV_PLACELIGHTS, ConstellationPerkMap.PerkOrder.DEFAULT,  13,  6,
                ConstellationPerks.TRV_SWIMSPEED);

        map.addPerk(ConstellationPerks.TRV_REDFOODNEED, ConstellationPerkMap.PerkOrder.DEFAULT,  4,  5,
                ConstellationPerks.TRV_SWIMSPEED);

        map.addPerk(ConstellationPerks.TRV_LAVAPROTECT, ConstellationPerkMap.PerkOrder.DEFAULT,  13,  1,
                ConstellationPerks.TRV_REDFOODNEED);

        map.addPerk(ConstellationPerks.TRV_STEPASSIST,  ConstellationPerkMap.PerkOrder.DEFAULT,  10, 10,
                ConstellationPerks.TRV_SWIMSPEED);

        ConstellationPerkMapRegistry.registerPerkMap(Constellations.vicio, map);

        map = new ConstellationPerkMap();

        map.addPerk(ConstellationPerks.DEF_DMGREDUCTION,  ConstellationPerkMap.PerkOrder.DEFAULT,  7,  13);

        map.addPerk(ConstellationPerks.DEF_FALLREDUCTION, ConstellationPerkMap.PerkOrder.DEFAULT,  2,  9,
                ConstellationPerks.DEF_DMGREDUCTION);

        map.addPerk(ConstellationPerks.DEF_ELEMENTAL,     ConstellationPerkMap.PerkOrder.DEFAULT,  3,  1,
                ConstellationPerks.DEF_FALLREDUCTION);

        map.addPerk(ConstellationPerks.DEF_DODGE,         ConstellationPerkMap.PerkOrder.DEFAULT,  11,  10,
                ConstellationPerks.DEF_DMGREDUCTION);

        map.addPerk(ConstellationPerks.DEF_NOARMOR,       ConstellationPerkMap.PerkOrder.DEFAULT,  10, 2,
                ConstellationPerks.DEF_DODGE);

        map.addPerk(ConstellationPerks.DEF_CHEATDEATH,    ConstellationPerkMap.PerkOrder.DEFAULT,  8, 6,
                ConstellationPerks.DEF_DODGE);

        ConstellationPerkMapRegistry.registerPerkMap(Constellations.armara, map);

        map = new ConstellationPerkMap();

        map.addPerk(ConstellationPerks.CRE_GROWTH, ConstellationPerkMap.PerkOrder.DEFAULT, 7, 7);

        map.addPerk(ConstellationPerks.CRE_BREEDING, ConstellationPerkMap.PerkOrder.DEFAULT, 14, 6,
                ConstellationPerks.CRE_GROWTH);

        map.addPerk(ConstellationPerks.CRE_REACH, ConstellationPerkMap.PerkOrder.DEFAULT, 4, 2,
                ConstellationPerks.CRE_BREEDING);

        map.addPerk(ConstellationPerks.CRE_MEND, ConstellationPerkMap.PerkOrder.DEFAULT, 0, 8,
                ConstellationPerks.CRE_GROWTH);

        map.addPerk(ConstellationPerks.CRE_OREGEN, ConstellationPerkMap.PerkOrder.DEFAULT, 12, 13,
                ConstellationPerks.CRE_MEND);

        map.addPerk(ConstellationPerks.CRE_FLARES, ConstellationPerkMap.PerkOrder.DEFAULT,  12, 2,
                ConstellationPerks.CRE_BREEDING);

        ConstellationPerkMapRegistry.registerPerkMap(Constellations.aevitas, map);

        map = new ConstellationPerkMap();

        map.addPerk(ConstellationPerks.DTR_DIG_SPEED, ConstellationPerkMap.PerkOrder.DEFAULT, 7, 7);

        map.addPerk(ConstellationPerks.DTR_DAMAGE_ARC, ConstellationPerkMap.PerkOrder.DEFAULT, 2, 6,
                ConstellationPerks.DTR_DIG_SPEED);

        map.addPerk(ConstellationPerks.DTR_DIG_TYPES, ConstellationPerkMap.PerkOrder.DEFAULT, 7, 1,
                ConstellationPerks.DTR_DIG_SPEED);

        map.addPerk(ConstellationPerks.DTR_LOW_HEALTH, ConstellationPerkMap.PerkOrder.DEFAULT, 4, 13,
                ConstellationPerks.DTR_DAMAGE_ARC);

        map.addPerk(ConstellationPerks.DTR_ARMOR_BREAK, ConstellationPerkMap.PerkOrder.DEFAULT, 12, 4,
                ConstellationPerks.DTR_DIG_SPEED);

        map.addPerk(ConstellationPerks.DTR_STACK, ConstellationPerkMap.PerkOrder.DEFAULT, 12, 10,
                ConstellationPerks.DTR_LOW_HEALTH);


        ConstellationPerkMapRegistry.registerPerkMap(Constellations.evorsio, map);
    }*/

}
