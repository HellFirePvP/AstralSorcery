/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerServer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkDamageKnockedback
 * Created by HellFirePvP
 * Date: 02.12.2016 / 21:04
 */
public class PerkDamageKnockedback extends ConstellationPerk {

    private static float dmgMultiplier = 1.05F;
    private static int ticksDuration = 60;

    public PerkDamageKnockedback() {
        super("DMG_KNOCK", Target.ENTITY_KNOCKBACK, Target.ENTITY_ATTACK);
    }

    @Override
    public float onEntityAttack(EntityPlayer attacker, EntityLivingBase attacked, float dmgIn) {
        if(isCooldownActiveForPlayer(attacker)) {
            dmgIn *= dmgMultiplier;
        }
        return dmgIn;
    }

    @Override
    public void onEntityKnockback(EntityPlayer attacker, EntityLivingBase attacked) {
        setCooldownActiveForPlayer(attacker, ticksDuration);
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        dmgMultiplier = cfg.getFloat(getKey() + "DamageIncrease", getConfigurationSection(), 1.05F, 1F, 2F, "Sets the damage multiplier that is applied to entity damaged after knocked back if the player has this perk.");
        ticksDuration = cfg.getInt(getKey() + "KnockbackDuration", getConfigurationSection(), 60, 1, 1000, "Sets the duration on how long the player gets additional damage on the mob when it was knocked back.");
    }

}
