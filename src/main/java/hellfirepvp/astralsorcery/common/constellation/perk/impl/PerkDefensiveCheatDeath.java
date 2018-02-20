/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.registry.RegistryPotions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkDefensiveCheatDeath
 * Created by HellFirePvP
 * Date: 06.04.2017 / 23:15
 */
public class PerkDefensiveCheatDeath extends ConstellationPerk {

    public static float thresholdApplyPerkHealth = 4F;
    public static float thresholdApplyPerkDamage = 6F;
    public static int cooldownPotionApplication = 1000;

    public static int potionDuration = 600;
    public static int potionAmplifier = 0;

    public PerkDefensiveCheatDeath() {
        super("DEF_CHEATDEATH", Target.ENTITY_HURT);
    }

    @Override
    public float onEntityHurt(EntityPlayer hurt, DamageSource source, float dmgIn) {
        if(hurt.getHealth() <= thresholdApplyPerkHealth ||
                dmgIn >= thresholdApplyPerkDamage) {
            if(!isCooldownActiveForPlayer(hurt)) {
                setCooldownActiveForPlayer(hurt, cooldownPotionApplication);
                hurt.addPotionEffect(new PotionEffect(RegistryPotions.potionCheatDeath, potionDuration, potionAmplifier, true, false));
            }
        }
        return dmgIn;
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        thresholdApplyPerkHealth = cfg.getFloat(getKey() + "ThresholdHealth", getConfigurationSection(), thresholdApplyPerkHealth, 0F, 20F, "If the player drops below this value of health, the potion effect will apply in case it isn't on cooldown.");
        thresholdApplyPerkDamage = cfg.getFloat(getKey() + "ThresholdDamage", getConfigurationSection(), thresholdApplyPerkDamage, 1F, 100F, "If the player takes damage equals/higher to the amount of damage configured here, the potion effect will apply in case it isn't on cooldown.");
        cooldownPotionApplication = cfg.getInt(getKey() + "CooldownPotion", getConfigurationSection(), cooldownPotionApplication, 1, Integer.MAX_VALUE, "Once the potion effect gets applied, it'll take at least this amount of ticks or a server restart until it can be re-applied by this perk");

        potionDuration = cfg.getInt(getKey() + "PotionDuration", getConfigurationSection(), potionDuration, 1, Integer.MAX_VALUE, "Once the potion effect gets applied by any of the triggers, this will be used as tick-duration of the potion effect.");
        potionAmplifier = cfg.getInt(getKey() + "PotionAmplifier", getConfigurationSection(), potionAmplifier, 0, 32, "Once the potion effect gets applied by any of the triggers, this will be used as amplifier of the potion effect.");
    }

}
