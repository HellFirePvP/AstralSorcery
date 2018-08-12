/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute.type;

import com.google.common.collect.ImmutableList;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeRegistry
 * Created by HellFirePvP
 * Date: 08.07.2018 / 12:27
 */
public class AttributeTypeRegistry {

    public static final String ATTR_TYPE_MELEE_DAMAGE = AstralSorcery.MODID + ".meleeattackdamage";
    public static final String ATTR_TYPE_PROJ_DAMAGE = AstralSorcery.MODID + ".projectileattackdamage";
    public static final String ATTR_TYPE_PROJ_SPEED = AstralSorcery.MODID + ".projectilespeed";
    public static final String ATTR_TYPE_HEALTH = AstralSorcery.MODID + ".maxhealth";
    public static final String ATTR_TYPE_MOVESPEED = AstralSorcery.MODID + ".movespeed";
    public static final String ATTR_TYPE_SWIMSPEED = AstralSorcery.MODID + ".swimspeed";
    public static final String ATTR_TYPE_ARMOR = AstralSorcery.MODID + ".armor";
    public static final String ATTR_TYPE_ARMOR_TOUGHNESS = AstralSorcery.MODID + ".armortoughness";
    public static final String ATTR_TYPE_ATTACK_SPEED = AstralSorcery.MODID + ".attackspeed";
    public static final String ATTR_TYPE_REACH = AstralSorcery.MODID + ".reach";

    public static final String ATTR_TYPE_LIFE_RECOVERY = AstralSorcery.MODID + ".liferecovery";
    public static final String ATTR_TYPE_BLEED_DURATION = AstralSorcery.MODID + ".bleedduration";
    public static final String ATTR_TYPE_BLEED_STACKS = AstralSorcery.MODID + ".bleedamount";
    public static final String ATTR_TYPE_BLEED_CHANCE = AstralSorcery.MODID + ".bleedchance";
    public static final String ATTR_TYPE_RAMPAGE_DURATION = AstralSorcery.MODID + ".rampageduration";
    public static final String ATTR_TYPE_MINING_CHAIN_CHANCE = AstralSorcery.MODID + ".chainchance";
    public static final String ATTR_TYPE_MINING_CHAIN_LENGTH = AstralSorcery.MODID + ".chainlength";
    public static final String ATTR_TYPE_MINING_CHAIN_SUCCESSIVECHAIN = AstralSorcery.MODID + ".chainchancing";
    public static final String ATTR_TYPE_ATTACK_LIFE_LEECH = AstralSorcery.MODID + ".lifeleech";
    public static final String ATTR_TYPE_ARC_CHAINS = AstralSorcery.MODID + ".archops";
    public static final String ATTR_TYPE_INC_PERK_EFFECT = AstralSorcery.MODID + ".perkeffect";
    public static final String ATTR_TYPE_INC_HARVEST_SPEED = AstralSorcery.MODID + ".harvestspeed";
    public static final String ATTR_TYPE_INC_CRIT_CHANCE = AstralSorcery.MODID + ".critchance";
    public static final String ATTR_TYPE_INC_CRIT_MULTIPLIER = AstralSorcery.MODID + ".critmulti";
    public static final String ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST = AstralSorcery.MODID + ".allres";
    public static final String ATTR_TYPE_INC_PERK_EXP = AstralSorcery.MODID + ".expgain";
    public static final String ATTR_TYPE_INC_DODGE = AstralSorcery.MODID + ".dodge";
    public static final String ATTR_TYPE_INC_THORNS = AstralSorcery.MODID + ".thorns";
    public static final String ATTR_TYPE_INC_THORNS_RANGED = AstralSorcery.MODID + ".rangedthorns";

    private static Map<String, PerkAttributeType> typeMap = new HashMap<>();

    AttributeTypeRegistry() {}

    public static void registerPerkType(PerkAttributeType type) {
        if(typeMap.putIfAbsent(type.getTypeString(), type) == null) {
            type.init();
            MinecraftForge.EVENT_BUS.register(type);
        }
    }

    public static Collection<PerkAttributeType> getTypes() {
        return ImmutableList.copyOf(typeMap.values());
    }

    @Nullable
    public static PerkAttributeType getType(String typeStr) {
        return typeMap.get(typeStr);
    }

}
