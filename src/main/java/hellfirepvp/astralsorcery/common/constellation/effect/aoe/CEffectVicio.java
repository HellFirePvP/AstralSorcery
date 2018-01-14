/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.ILocatable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectVicio
 * Created by HellFirePvP
 * Date: 10.01.2017 / 21:52
 */
public class CEffectVicio extends ConstellationEffect {

    public static boolean enabled = true;
    public static double potencyMultiplier = 1;
    public static int effectRange = 16;

    public static int potionAmplifierSpeed = 1;
    public static int potionAmplifierJump = 1;

    public CEffectVicio(@Nullable ILocatable origin) {
        super(origin, Constellations.vicio, "vicio");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility, boolean extendedEffects) {
        if(rand.nextInt(3) == 0) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    pos.getX() + rand.nextFloat() * 3 * (rand.nextBoolean() ? 1 : -1) + 0.5,
                    pos.getY() + rand.nextFloat() * 2 + 0.5,
                    pos.getZ() + rand.nextFloat() * 3 * (rand.nextBoolean() ? 1 : -1) + 0.5);
            p.motion((rand.nextFloat() * 0.07F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.07F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.07F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.45F).setColor(new Color(200, 200, 255)).gravity(0.008).setMaxAge(25);
        }
    }

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable IMinorConstellation possibleTraitEffect) {
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(pos).grow(effectRange));
        for (EntityLivingBase entity : entities) {
            if(!entity.isDead) {
                entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 30, potionAmplifierSpeed));
                entity.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 30, potionAmplifierJump));
            }
        }
        return false;
    }

    @Override
    public boolean playTraitEffect(World world, BlockPos pos, IMinorConstellation traitType, float traitStrength) {
        return false;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        effectRange = cfg.getInt(getKey() + "Range", getConfigurationSection(), 16, 1, 32, "Defines the radius (in blocks) in which the ritual will stop mob spawning and projectiles.");
        enabled = cfg.getBoolean(getKey() + "Enabled", getConfigurationSection(), true, "Set to false to disable this ConstellationEffect.");
        potionAmplifierSpeed = cfg.getInt(getKey() + "SpeedAmplifier", getConfigurationSection(), 1, 0, Short.MAX_VALUE, "Set the amplifier for the speed potion effect.");
        potionAmplifierJump = cfg.getInt(getKey() + "JumpAmplifier", getConfigurationSection(), 1, 0, Short.MAX_VALUE, "Set the amplifier for the jump potion effect.");
        potencyMultiplier = cfg.getFloat(getKey() + "PotencyMultiplier", getConfigurationSection(), 1.0F, 0.01F, 100F, "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");

    }

}
