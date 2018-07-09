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

    public static void initPerkTree() {
        initializeRoot();

        initializeAevitasRoot();
        initializeVicioRoot();

        initializeInnerPerkWheel();

        MinecraftForge.EVENT_BUS.post(new PerkTreeEvent.PerkRegister());

        initializeAttributeTypes();
    }

    private static void initializeInnerPerkWheel() {

    }

    private static void initializeVicioRoot() {
        AttributeModifierPerk moveRoot1 = new AttributeModifierPerk("base_inc_ms", 1, 10);
        moveRoot1.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        String unlocMoveSpeed = moveRoot1.getUnlocalizedName();
        AttributeModifierPerk moveRoot2 = new AttributeModifierPerk("base_inc_ms_1", -1, 11).setNameOverride(unlocMoveSpeed);
        moveRoot2.addModifier(0.05F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        AttributeModifierPerk perkMajorMovement = new AttributeModifierPerk("major_inc_ms_fs", 0, 14) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkMajorMovement.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED);
        perkMajorMovement.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_FLYSPEED);


        PerkTree.PointConnector ctMove1 = PERK_TREE.registerPerk(moveRoot1);
        PerkTree.PointConnector ctMove2 = PERK_TREE.registerPerk(moveRoot2);
        PerkTree.PointConnector ctMajorMobility = PERK_TREE.registerPerk(perkMajorMovement);

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
        AttributeModifierPerk perkMajorLife = new AttributeModifierPerk("major_inc_life", -12, 6) {
            @Override
            public PerkTreePoint getPoint() {
                return new PerkTreeMajor(this, this.getOffset());
            }
        };
        perkMajorLife.addModifier(1.15F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_HEALTH);

        PerkTree.PointConnector ctLife1 = PERK_TREE.registerPerk(lifeRoot1);
        PerkTree.PointConnector ctLife2 = PERK_TREE.registerPerk(lifeRoot2);
        PerkTree.PointConnector ctMajorLife = PERK_TREE.registerPerk(perkMajorLife);

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
        registerPerkType(new PerkAttributeType(ATTR_TYPE_INC_PERK_EFFECT));

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
