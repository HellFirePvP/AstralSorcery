/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.entities.EntityFlare;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkCreationFlares
 * Created by HellFirePvP
 * Date: 06.04.2017 / 23:56
 */
public class PerkCreationFlares extends ConstellationPerk {

    public static int defensiveFlareChance = 7;
    public static int spawnCooldown = 150;

    public PerkCreationFlares() {
        super("CRE_FLARES", Target.ENTITY_HURT);
    }

    @Override
    public float onEntityHurt(EntityPlayer hurt, DamageSource source, float dmgIn) {
        if(!isCooldownActiveForPlayer(hurt)) {
            Entity cause = source.getImmediateSource();
            if(cause != null && cause instanceof EntityLivingBase && !cause.isDead && cause != hurt) {
                Vector3 pos = Vector3.atEntityCenter(hurt);
                EntityFlare flare = new EntityFlare(hurt.getEntityWorld(), pos.getX(), pos.getY(), pos.getZ());
                flare.setAttackTarget((EntityLivingBase) cause);
                hurt.getEntityWorld().spawnEntity(flare);
                setCooldownActiveForPlayer(hurt, spawnCooldown);
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
        spawnCooldown = cfg.getInt(getKey() + "SpawnCooldown", getConfigurationSection(), spawnCooldown, 1, Integer.MAX_VALUE, "Defines the minimum cooldown/time between 2 flare spawns caused by this perk.");
        defensiveFlareChance = cfg.getInt(getKey() + "FlareChance", getConfigurationSection(), defensiveFlareChance, 1, 6000, "Defines the chance (random.nextInt(chance) == 0) that a flare spawns to attack the attacking entity");

        spawnCooldown = Math.max(1, spawnCooldown);
        defensiveFlareChance = Math.max(1, defensiveFlareChance);
    }

}
