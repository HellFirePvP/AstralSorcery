package hellfirepvp.astralsorcery.common.lib;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkNamesAS
 * Created by HellFirePvP
 * Date: 25.07.2020 / 22:13
 */
public class PerkNamesAS {

    private PerkNamesAS() {}

    public static final String NAME_INC_ALL_RES             = name("generic.inc.allres");
    public static final String NAME_INC_DODGE               = name("generic.inc.dodge");
    public static final String NAME_INC_REACH               = name("generic.inc.reach");
    public static final String NAME_INC_ARMOR               = name("generic.inc.armor");
    public static final String NAME_INC_ARMOR_TOUGHNESS     = name("generic.inc.armor_toughness");
    public static final String NAME_INC_LIFE                = name("generic.inc.life");
    public static final String NAME_INC_LIFE_RECOVERY       = name("generic.inc.life_recovery");
    public static final String NAME_INC_LIFE_LEECH          = name("generic.inc.life_leech");
    public static final String NAME_INC_PROJ_SPEED          = name("generic.inc.proj_speed");
    public static final String NAME_INC_PROJ_DAMAGE         = name("generic.inc.damage_proj");
    public static final String NAME_INC_ATTACK_SPEED        = name("generic.inc.attack_speed");
    public static final String NAME_INC_MELEE_DAMAGE        = name("generic.inc.damage_melee");
    public static final String NAME_INC_MINING_SPEED        = name("generic.inc.mining_speed");
    public static final String NAME_INC_MINING_SIZE         = name("generic.inc.mining_size");
    public static final String NAME_INC_CRIT_CHANCE         = name("generic.inc.crit_chance");
    public static final String NAME_INC_CRIT_MULTIPLIER     = name("generic.inc.crit_multiplier");
    public static final String NAME_INC_MOVESPEED           = name("generic.inc.move_speed");
    public static final String NAME_INC_SWIMSPEED           = name("generic.inc.swim_speed");
    public static final String NAME_INC_PERK_EFFECT         = name("generic.inc.perk_effect");
    public static final String NAME_INC_PERK_EXP            = name("generic.inc.perk_exp");
    public static final String NAME_INC_POTION_DURATION     = name("generic.inc.potion_duration");
    public static final String NAME_INC_CHARGE_MAX          = name("generic.inc.starlight_charge_max");
    public static final String NAME_INC_CHARGE_REGEN        = name("generic.inc.starlight_charge_regen");
    public static final String NAME_INC_THORNS              = name("generic.inc.thorns");
    public static final String NAME_INC_BLEED_DURATION      = name("generic.inc.bleed_duration");
    public static final String NAME_INC_BLEED_CHANCE        = name("generic.inc.bleed_chance");
    public static final String NAME_INC_BLEED_STACKS        = name("generic.inc.bleed_stacks");
    public static final String NAME_INC_RAMPAGE_DURATION    = name("generic.inc.rampage_duration");
    public static final String NAME_INC_ENCH_EFFECT         = name("generic.inc.ench_effect");
    public static final String NAME_INC_COOLDOWN_RECOVERY   = name("generic.inc.cooldown_reduction");
    public static final String NAME_INC_LUCK                = name("generic.inc.luck");

    public static final String NAME_ADD_ALL_RES             = name("generic.add.allres");
    public static final String NAME_ADD_DODGE               = name("generic.add.dodge");
    public static final String NAME_ADD_REACH               = name("generic.add.reach");
    public static final String NAME_ADD_ARMOR               = name("generic.add.armor");
    public static final String NAME_ADD_ARMOR_TOUGHNESS     = name("generic.add.armor_toughness");
    public static final String NAME_ADD_LIFE                = name("generic.add.life");
    public static final String NAME_ADD_LIFE_RECOVERY       = name("generic.add.life_recovery");
    public static final String NAME_ADD_LIFE_LEECH          = name("generic.add.life_leech");
    public static final String NAME_ADD_PROJ_SPEED          = name("generic.add.proj_speed");
    public static final String NAME_ADD_PROJ_DAMAGE         = name("generic.add.damage_proj");
    public static final String NAME_ADD_ATTACK_SPEED        = name("generic.add.attack_speed");
    public static final String NAME_ADD_MELEE_DAMAGE        = name("generic.add.damage_melee");
    public static final String NAME_ADD_MINING_SPEED        = name("generic.add.mining_speed");
    public static final String NAME_ADD_MINING_SIZE         = name("generic.add.mining_size");
    public static final String NAME_ADD_CRIT_CHANCE         = name("generic.add.crit_chance");
    public static final String NAME_ADD_CRIT_MULTIPLIER     = name("generic.add.crit_multiplier");
    public static final String NAME_ADD_MOVESPEED           = name("generic.add.move_speed");
    public static final String NAME_ADD_SWIMSPEED           = name("generic.add.swim_speed");
    public static final String NAME_ADD_PERK_EFFECT         = name("generic.add.perk_effect");
    public static final String NAME_ADD_PERK_EXP            = name("generic.add.perk_exp");
    public static final String NAME_ADD_POTION_DURATION     = name("generic.add.potion_duration");
    public static final String NAME_ADD_CHARGE_MAX          = name("generic.add.starlight_charge_max");
    public static final String NAME_ADD_CHARGE_REGEN        = name("generic.add.starlight_charge_regen");
    public static final String NAME_ADD_THORNS              = name("generic.add.thorns");
    public static final String NAME_ADD_BLEED_DURATION      = name("generic.add.bleed_duration");
    public static final String NAME_ADD_BLEED_CHANCE        = name("generic.add.bleed_chance");
    public static final String NAME_ADD_BLEED_STACKS        = name("generic.add.bleed_stacks");
    public static final String NAME_ADD_RAMPAGE_DURATION    = name("generic.add.rampage_duration");
    public static final String NAME_ADD_LUCK                = name("generic.add.luck");

    public static String name(String namespacedName) {
        return String.format("perk.astralsorcery.%s", namespacedName);
    }

}
