/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkDamageReflect
 * Created by HellFirePvP
 * Date: 06.04.2017 / 23:51
 */
public class PerkDamageReflect extends ConstellationPerk {

    public static int reflectChance = 3;
    public static float reflectPerc = 1F;

    public PerkDamageReflect() {
        super("DMG_REFLECT", Target.ENTITY_HURT);
    }

    @Override
    public float onEntityHurt(EntityPlayer hurt, DamageSource source, float dmgIn) {
        if(rand.nextInt(reflectChance) == 0) {
            Entity cause = source.getImmediateSource();
            if(cause == null || !(cause instanceof EntityLivingBase)) {
                cause = source.getTrueSource();
            }
            if(cause != null && cause instanceof EntityLivingBase) {
                cause.attackEntityFrom(DamageSource.causePlayerDamage(hurt), dmgIn * reflectPerc);
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
        reflectChance = cfg.getInt(getKey() + "ReflectChance", getConfigurationSection(), reflectChance, 1, 5000, "Defines the chance that a incoming attack (if it can be resolved to an attacker) can be reflected (random.nextInt(chance) == 0 check)");
        reflectPerc = cfg.getFloat(getKey() + "ReflectPercentage", getConfigurationSection(), reflectPerc, 0.001F, 1F, "Defines the percentage of damage that the attacker will also take upon attacking the player.");

        reflectChance = Math.max(1, reflectChance);
        reflectPerc = MathHelper.clamp(reflectPerc, 0.001F, 1F);
    }

}
