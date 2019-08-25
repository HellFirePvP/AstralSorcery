/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkAttributeTypesAS
 * Created by HellFirePvP
 * Date: 08.08.2019 / 16:57
 */
public class PerkAttributeTypesAS {

    private PerkAttributeTypesAS() {}

    public static final ResourceLocation KEY_ATTR_TYPE_MELEE_DAMAGE =                 name("meleeattackdamage");
    public static final ResourceLocation KEY_ATTR_TYPE_PROJ_DAMAGE =                  name("projectileattackdamage");
    public static final ResourceLocation KEY_ATTR_TYPE_PROJ_SPEED =                   name("projectilespeed");
    public static final ResourceLocation KEY_ATTR_TYPE_HEALTH =                       name("maxhealth");
    public static final ResourceLocation KEY_ATTR_TYPE_MOVESPEED =                    name("movespeed");
    public static final ResourceLocation KEY_ATTR_TYPE_SWIMSPEED =                    name("swimspeed");
    public static final ResourceLocation KEY_ATTR_TYPE_ARMOR =                        name("armor");
    public static final ResourceLocation KEY_ATTR_TYPE_ARMOR_TOUGHNESS =              name("armortoughness");
    public static final ResourceLocation KEY_ATTR_TYPE_ATTACK_SPEED =                 name("attackspeed");
    public static final ResourceLocation KEY_ATTR_TYPE_REACH =                        name("reach");

    public static final ResourceLocation KEY_ATTR_TYPE_LIFE_RECOVERY =                name("liferecovery");
    public static final ResourceLocation KEY_ATTR_TYPE_POTION_DURATION =              name("potionduration");
    public static final ResourceLocation KEY_ATTR_TYPE_BLEED_DURATION =               name("bleedduration");
    public static final ResourceLocation KEY_ATTR_TYPE_BLEED_STACKS =                 name("bleedamount");
    public static final ResourceLocation KEY_ATTR_TYPE_BLEED_CHANCE =                 name("bleedchance");
    public static final ResourceLocation KEY_ATTR_TYPE_RAMPAGE_DURATION =             name("rampageduration");
    public static final ResourceLocation KEY_ATTR_TYPE_MINING_CHAIN_CHANCE =          name("chainchance");
    public static final ResourceLocation KEY_ATTR_TYPE_MINING_CHAIN_LENGTH =          name("chainlength");
    public static final ResourceLocation KEY_ATTR_TYPE_MINING_CHAIN_SUCCESSIVECHAIN = name("chainchancing");
    public static final ResourceLocation KEY_ATTR_TYPE_ATTACK_LIFE_LEECH =            name("lifeleech");
    public static final ResourceLocation KEY_ATTR_TYPE_ARC_CHAINS =                   name("archops");
    public static final ResourceLocation KEY_ATTR_TYPE_INC_PERK_EFFECT =              name("perkeffect");
    public static final ResourceLocation KEY_ATTR_TYPE_INC_HARVEST_SPEED =            name("harvestspeed");
    public static final ResourceLocation KEY_ATTR_TYPE_INC_CRIT_CHANCE =              name("critchance");
    public static final ResourceLocation KEY_ATTR_TYPE_INC_CRIT_MULTIPLIER =          name("critmulti");
    public static final ResourceLocation KEY_ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST =     name("allres");
    public static final ResourceLocation KEY_ATTR_TYPE_INC_PERK_EXP =                 name("expgain");
    public static final ResourceLocation KEY_ATTR_TYPE_INC_DODGE =                    name("dodge");
    public static final ResourceLocation KEY_ATTR_TYPE_INC_THORNS =                   name("thorns");
    public static final ResourceLocation KEY_ATTR_TYPE_INC_THORNS_RANGED =            name("rangedthorns");
    public static final ResourceLocation KEY_ATTR_TYPE_INC_ENCH_EFFECT =              name("dynenchantmenteffect");

    public static PerkAttributeType ATTR_TYPE_MELEE_DAMAGE;
    public static PerkAttributeType ATTR_TYPE_PROJ_DAMAGE;
    public static PerkAttributeType ATTR_TYPE_PROJ_SPEED;
    public static PerkAttributeType ATTR_TYPE_HEALTH;
    public static PerkAttributeType ATTR_TYPE_MOVESPEED;
    public static PerkAttributeType ATTR_TYPE_SWIMSPEED;
    public static PerkAttributeType ATTR_TYPE_ARMOR;
    public static PerkAttributeType ATTR_TYPE_ARMOR_TOUGHNESS;
    public static PerkAttributeType ATTR_TYPE_ATTACK_SPEED;
    public static PerkAttributeType ATTR_TYPE_REACH;

    public static PerkAttributeType ATTR_TYPE_LIFE_RECOVERY;
    public static PerkAttributeType ATTR_TYPE_POTION_DURATION;
    public static PerkAttributeType ATTR_TYPE_BLEED_DURATION;
    public static PerkAttributeType ATTR_TYPE_BLEED_STACKS;
    public static PerkAttributeType ATTR_TYPE_BLEED_CHANCE;
    public static PerkAttributeType ATTR_TYPE_RAMPAGE_DURATION;
    public static PerkAttributeType ATTR_TYPE_MINING_CHAIN_CHANCE;
    public static PerkAttributeType ATTR_TYPE_MINING_CHAIN_LENGTH;
    public static PerkAttributeType ATTR_TYPE_MINING_CHAIN_SUCCESSIVECHAIN;
    public static PerkAttributeType ATTR_TYPE_ATTACK_LIFE_LEECH;
    public static PerkAttributeType ATTR_TYPE_ARC_CHAINS;
    public static PerkAttributeType ATTR_TYPE_INC_PERK_EFFECT;
    public static PerkAttributeType ATTR_TYPE_INC_HARVEST_SPEED;
    public static PerkAttributeType ATTR_TYPE_INC_CRIT_CHANCE;
    public static PerkAttributeType ATTR_TYPE_INC_CRIT_MULTIPLIER;
    public static PerkAttributeType ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST;
    public static PerkAttributeType ATTR_TYPE_INC_PERK_EXP;
    public static PerkAttributeType ATTR_TYPE_INC_DODGE;
    public static PerkAttributeType ATTR_TYPE_INC_THORNS;
    public static PerkAttributeType ATTR_TYPE_INC_THORNS_RANGED;
    public static PerkAttributeType ATTR_TYPE_INC_ENCH_EFFECT;

    private static ResourceLocation name(String path) {
        return new ResourceLocation(AstralSorcery.MODID, path);
    }

}
