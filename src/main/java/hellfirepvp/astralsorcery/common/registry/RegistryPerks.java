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
import hellfirepvp.astralsorcery.common.constellation.perk.tree.root.RootPerk;
import hellfirepvp.astralsorcery.common.event.PerkTreeEvent;
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

        MinecraftForge.EVENT_BUS.post(new PerkTreeEvent.PerkRegister());

        initializeAttributeTypes();
    }

    //Registers T1 perk-effect perks
    private static void initializePerkCore() {
        float inc_t1 = 0.12F;
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
    }

    //Registers T2 perk-effect perks
    private static void initializePerkInteriorTravelWheel() {
        float inc_t2 = 0.07F;
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
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3"))
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_1"));
        PERK_TREE.registerPerk(perkEffectEvDis2)
                .connect(perkEffectEvDis1);
        PERK_TREE.registerPerk(perkEffectEvDis3)
                .connect(perkEffectEvDis2);

        PERK_TREE.registerPerk(perkEffectDisArm1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_2"))
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_3"));
        PERK_TREE.registerPerk(perkEffectDisArm2)
                .connect(perkEffectDisArm1);
        PERK_TREE.registerPerk(perkEffectDisArm3)
                .connect(perkEffectDisArm2);

        PERK_TREE.registerPerk(perkEffectArmVic1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_4"))
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_5"));
        PERK_TREE.registerPerk(perkEffectArmVic2)
                .connect(perkEffectArmVic1);
        PERK_TREE.registerPerk(perkEffectArmVic3)
                .connect(perkEffectArmVic2);

        PERK_TREE.registerPerk(perkEffectVicAev1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_6"))
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_7"));
        PERK_TREE.registerPerk(perkEffectVicAev2)
                .connect(perkEffectVicAev1);
        PERK_TREE.registerPerk(perkEffectVicAev3)
                .connect(perkEffectVicAev2);

        PERK_TREE.registerPerk(perkEffectAevEv1)
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_8"))
                .connect(PERK_TREE.getAstralSorceryPerk("base_inc_perkeffect_t3_9"));
        PERK_TREE.registerPerk(perkEffectAevEv2)
                .connect(perkEffectAevEv1);
        PERK_TREE.registerPerk(perkEffectAevEv3)
                .connect(perkEffectAevEv2);
    }

    //Registers T3 perk-effect perks
    private static void initializeRootPerkWheel() {
        float inc_t3 = 0.04F;
        String unloc;

        AttributeModifierPerk perkEffectEvDis1 = new AttributeModifierPerk("base_inc_perkeffect_t3", -4, -13);
        perkEffectEvDis1.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        unloc = perkEffectEvDis1.getUnlocalizedName();
        AttributeModifierPerk perkEffectEvDis2 = new AttributeModifierPerk("base_inc_perkeffect_t3_1", 4, -13).setNameOverride(unloc);
        perkEffectEvDis2.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectDisArm1 = new AttributeModifierPerk("base_inc_perkeffect_t3_2", 11, -5).setNameOverride(unloc);
        perkEffectDisArm1.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectDisArm2 = new AttributeModifierPerk("base_inc_perkeffect_t3_3", 13, 1).setNameOverride(unloc);
        perkEffectDisArm2.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectArmVic1 = new AttributeModifierPerk("base_inc_perkeffect_t3_4", 10, 10).setNameOverride(unloc);
        perkEffectArmVic1.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectArmVic2 = new AttributeModifierPerk("base_inc_perkeffect_t3_5", 5, 13).setNameOverride(unloc);
        perkEffectArmVic2.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectVicAev1 = new AttributeModifierPerk("base_inc_perkeffect_t3_6", -5, 13).setNameOverride(unloc);
        perkEffectVicAev1.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectVicAev2 = new AttributeModifierPerk("base_inc_perkeffect_t3_7", -10, 10).setNameOverride(unloc);
        perkEffectVicAev2.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        AttributeModifierPerk perkEffectAevEv1 = new AttributeModifierPerk("base_inc_perkeffect_t3_8", -13, 1).setNameOverride(unloc);
        perkEffectAevEv1.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);
        AttributeModifierPerk perkEffectAevEv2 = new AttributeModifierPerk("base_inc_perkeffect_t3_9", -11, -5).setNameOverride(unloc);
        perkEffectAevEv2.addModifier(inc_t3, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT);

        PERK_TREE.registerPerk(perkEffectEvDis1).connect(perkRootMajorHarvest);
        PERK_TREE.registerPerk(perkEffectEvDis2).connect(perkEffectEvDis1).connect(perkRootMajorDamage);

        PERK_TREE.registerPerk(perkEffectDisArm1).connect(perkRootMajorDamage);
        PERK_TREE.registerPerk(perkEffectDisArm2).connect(perkEffectDisArm1).connect(perkRootMajorArmor);

        PERK_TREE.registerPerk(perkEffectArmVic1).connect(perkRootMajorArmor);
        PERK_TREE.registerPerk(perkEffectArmVic2).connect(perkEffectArmVic1).connect(perkRootMajorMovespeed);

        PERK_TREE.registerPerk(perkEffectVicAev1).connect(perkRootMajorMovespeed);
        PERK_TREE.registerPerk(perkEffectVicAev2).connect(perkEffectVicAev1).connect(perkRootMajorHealth);

        PERK_TREE.registerPerk(perkEffectAevEv1).connect(perkRootMajorHealth);
        PERK_TREE.registerPerk(perkEffectAevEv2).connect(perkEffectAevEv1).connect(perkRootMajorHarvest);
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
        perkRootMajorHarvest.addModifier(1.10F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED);
        perkRootMajorHarvest.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_DAMAGE);

        PerkTree.PointConnector ctHarvest1 = PERK_TREE.registerPerk(breakRoot1);
        PerkTree.PointConnector ctHarvest2 = PERK_TREE.registerPerk(breakRoot2);
        PerkTree.PointConnector ctMajorHarvest = PERK_TREE.registerPerk(perkRootMajorHarvest);

        ctHarvest1.connect(PERK_TREE.getRootPerk(Constellations.evorsio));
        ctHarvest2.connect(ctHarvest1);
        ctMajorHarvest.connect(ctHarvest2);
    }

    private static void initializeDiscidiaRoot() {
        AttributeModifierPerk dmgRoot1 = new AttributeModifierPerk("base_inc_damage", 7, -7);
        dmgRoot1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_DAMAGE);
        String unlocDmg = dmgRoot1.getUnlocalizedName();
        AttributeModifierPerk dmgRoot2 = new AttributeModifierPerk("base_inc_damage_1", 6, -9).setNameOverride(unlocDmg);
        dmgRoot2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_DAMAGE);
        perkRootMajorDamage = new AttributeModifierPerk("major_inc_damage", 9, -10) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkRootMajorDamage.addModifier(1.05F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_DAMAGE);
        perkRootMajorDamage.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE);

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
        moveRoot1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        String unlocMoveSpeed = moveRoot1.getUnlocalizedName();
        AttributeModifierPerk moveRoot2 = new AttributeModifierPerk("base_inc_ms_1", -1, 11).setNameOverride(unlocMoveSpeed);
        moveRoot2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        perkRootMajorMovespeed = new AttributeModifierPerk("major_inc_ms_fs", 0, 14) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkRootMajorMovespeed.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        perkRootMajorMovespeed.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_FLYSPEED);

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
        PERK_TREE.registerRootPerk(new RootPerk("aevitas",   Constellations.aevitas, -6,  2));
        PERK_TREE.registerRootPerk(new RootPerk("vicio",     Constellations.vicio,    0,  7));
        PERK_TREE.registerRootPerk(new RootPerk("armara",    Constellations.armara,   6,  2));
        PERK_TREE.registerRootPerk(new RootPerk("discidia",  Constellations.discidia, 4, -5));
        PERK_TREE.registerRootPerk(new RootPerk("evorsio",   Constellations.evorsio, -4, -5));
    }

    private static void initializeAttributeTypes() {
        registerPerkType(new AttributeTypePerkEffect());
        registerPerkType(new PerkAttributeType(ATTR_TYPE_INC_HARVEST_SPEED));
        registerPerkType(new PerkAttributeType(ATTR_TYPE_INC_CRIT_CHANCE));

        registerPerkType(new AttributeTypeAttackDamage());
        registerPerkType(new AttributeTypeMaxHealth());
        registerPerkType(new AttributeTypeMovementSpeed());
        registerPerkType(new AttributeTypeFlyingSpeed());
        registerPerkType(new AttributeTypeArmor());

        MinecraftForge.EVENT_BUS.post(new PerkTreeEvent.PerkAttributeTypeRegister());
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
