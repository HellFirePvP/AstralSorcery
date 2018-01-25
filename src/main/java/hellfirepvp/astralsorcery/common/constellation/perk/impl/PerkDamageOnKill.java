/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkDamageOnKill
 * Created by HellFirePvP
 * Date: 02.12.2016 / 21:46
 */
public class PerkDamageOnKill extends ConstellationPerk {

    private static float dmgMultiplier = 2.4F;
    private static int ticksDuration = 60;

    public PerkDamageOnKill() {
        super("DMG_KILL", Target.ENTITY_ATTACK, Target.ENTITY_KILL);
    }

    @Override
    public float onEntityAttack(EntityPlayer attacker, EntityLivingBase attacked, float dmgIn) {
        if(isCooldownActiveForPlayer(attacker)) {
            addAlignmentCharge(attacker, 2);
            dmgIn *= dmgMultiplier;
        }
        return dmgIn;
    }

    @Override
    public void onEntityKilled(EntityPlayer attacker, EntityLivingBase killed) {
        setCooldownActiveForPlayer(attacker, ticksDuration);
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        dmgMultiplier = cfg.getFloat(getKey() + "DamageIncrease", getConfigurationSection(), dmgMultiplier, 1F, 2F, "Sets the damage multiplier that is applied to entity damaged after knocked back if the player has this perk.");
        ticksDuration = cfg.getInt(getKey() + "KillDuration", getConfigurationSection(), ticksDuration, 1, 1000, "Sets the duration on how long the player gets additional damage on the mob when it was knocked back.");
    }

}
