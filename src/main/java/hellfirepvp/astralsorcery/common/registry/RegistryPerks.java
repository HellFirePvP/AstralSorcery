/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.constellation.perk.attribute.type.*;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.root.RootPerk;
import hellfirepvp.astralsorcery.common.event.PerkTreeEvent;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import net.minecraftforge.common.MinecraftForge;

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

        MinecraftForge.EVENT_BUS.post(new PerkTreeEvent.PerkRegister());

        initializeAttributeTypes();
    }

    private static void initializeRoot() {
        PerkTree.INSTANCE.registerRootPerk(new RootPerk("aevitas",   Constellations.aevitas, -6,  2));
        PerkTree.INSTANCE.registerRootPerk(new RootPerk("vicio",     Constellations.vicio,    0,  7));
        PerkTree.INSTANCE.registerRootPerk(new RootPerk("armara",    Constellations.armara,   6,  2));
        PerkTree.INSTANCE.registerRootPerk(new RootPerk("discidia",  Constellations.discidia, 4, -5));
        PerkTree.INSTANCE.registerRootPerk(new RootPerk("evorsio",   Constellations.evorsio, -4, -5));
    }

    private static void initializeAttributeTypes() {
        AttributeTypeRegistry.registerType(new AttributeTypeAttackDamage());
        AttributeTypeRegistry.registerType(new AttributeTypeMaxHealth());
        AttributeTypeRegistry.registerType(new AttributeTypeMovementSpeed());
        AttributeTypeRegistry.registerType(new AttributeTypeArmor());

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
