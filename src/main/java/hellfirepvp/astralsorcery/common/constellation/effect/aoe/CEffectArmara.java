/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.controller.OrbitalEffectController;
import hellfirepvp.astralsorcery.client.effect.controller.OrbitalPropertiesRitualArmara;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.entities.EntityGrapplingHook;
import hellfirepvp.astralsorcery.common.entities.EntityTechnicalAmbient;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerEntity;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerServer;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.ILocatable;
import hellfirepvp.astralsorcery.common.util.data.TickTokenizedMap;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.data.WorldBlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectArmara
 * Created by HellFirePvP
 * Date: 03.01.2017 / 14:49
 */
public class CEffectArmara extends ConstellationEffect {

    public static boolean enabled = true;
    public static double potencyMultiplier = 1;
    public static int protectionRange = 32;

    private int rememberedTimeout = 0;
    public static int potionAmplifier = 0;

    public CEffectArmara(@Nullable ILocatable origin) {
        super(origin, Constellations.armara, "armara");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility, boolean extendedEffects) {
        if(pedestal.getTicksExisted() % 20 == 0) {
            OrbitalPropertiesRitualArmara luc = new OrbitalPropertiesRitualArmara();
            OrbitalEffectController ctrl = EffectHandler.getInstance().orbital(luc, luc, luc);
            ctrl.setOffset(new Vector3(pos).add(0.5, 0.5, 0.5));
            ctrl.setOrbitRadius(0.8 + rand.nextFloat() * 0.7);
            ctrl.setOrbitAxis(Vector3.RotAxis.Y_AXIS);
            ctrl.setTicksPerRotation(20 + rand.nextInt(20));
        }
        List<Entity> projectiles = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(pos).grow(protectionRange));
        if(!projectiles.isEmpty()) {
            for (Entity e : projectiles) {
                if(!e.isDead && !(e instanceof EntityTechnicalAmbient)) {
                    if(e instanceof IProjectile) {
                        double xRatio = (pos.getX() + 0.5) - e.posX;
                        double zRatio = (pos.getZ() + 0.5) - e.posZ;
                        float f = MathHelper.sqrt(xRatio * xRatio + zRatio * zRatio);
                        e.motionX /= 2.0D;
                        e.motionZ /= 2.0D;
                        e.motionX -= xRatio / f * 0.4;
                        e.motionZ -= zRatio / f * 0.4;
                        ((IProjectile) e).setThrowableHeading(e.motionX, e.motionY, e.motionZ, 1F, 0F);
                    }
                }
            }
        }
    }

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable IMinorConstellation possibleTraitEffect) {
        if(!enabled) return false;
        percStrength *= potencyMultiplier;
        if(percStrength < 1) {
            if(world.rand.nextFloat() > percStrength) return false;
        }

        int toAdd = 1 + rand.nextInt(3);
        WorldBlockPos at = new WorldBlockPos(world, pos);
        TickTokenizedMap.SimpleTickToken<Double> token = EventHandlerEntity.spawnDenyRegions.get(at);
        if(token != null) {
            int next = token.getRemainingTimeout() + toAdd;
            if(next > 400) next = 400;
            token.setTimeout(next);
            rememberedTimeout = next;
        } else {
            rememberedTimeout = Math.min(400, rememberedTimeout + toAdd);
            EventHandlerEntity.spawnDenyRegions.put(at, new TickTokenizedMap.SimpleTickToken<>((double) protectionRange, rememberedTimeout));
        }

        EntityPlayer owner = getOwningPlayerInWorld(world, pos);

        List<Entity> projectiles = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(pos).grow(protectionRange));
        if(!projectiles.isEmpty()) {
            for (Entity e : projectiles) {
                if(!e.isDead && !(e instanceof EntityTechnicalAmbient)) {
                    if(e instanceof IProjectile) {
                        double xRatio = (pos.getX() + 0.5) - e.posX;
                        double zRatio = (pos.getZ() + 0.5) - e.posZ;
                        float f = MathHelper.sqrt(xRatio * xRatio + zRatio * zRatio);
                        e.motionX /= 2.0D;
                        e.motionZ /= 2.0D;
                        e.motionX -= xRatio / f * 0.4;
                        e.motionZ -= zRatio / f * 0.4;
                        ((IProjectile) e).setThrowableHeading(e.motionX, e.motionY, e.motionZ, 1F, 0F);
                    } else if(e instanceof EntityLivingBase && !(e instanceof EntityPlayer)) {
                        ((EntityLivingBase) e).knockBack(owner == null ? e : owner, 0.4F, (pos.getX() + 0.5) - e.posX, (pos.getZ() + 0.5) - e.posZ);
                    }
                }
            }
        }
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(pos).grow(protectionRange));
        for (EntityLivingBase entity : entities) {
            if(!entity.isDead) {
                entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 30, potionAmplifier));
                if (entity instanceof EntityPlayer) {
                    entity.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 30, potionAmplifier));
                }
            }
        }

        return true;
    }

    @Override
    public boolean playTraitEffect(World world, BlockPos pos, IMinorConstellation traitType, float traitStrength) {
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound cmp) {
        super.readFromNBT(cmp);

        this.rememberedTimeout = cmp.getInteger("rememberedTimeout");
    }

    @Override
    public void writeToNBT(NBTTagCompound cmp) {
        super.writeToNBT(cmp);

        cmp.setInteger("rememberedTimeout", rememberedTimeout);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        protectionRange = cfg.getInt(getKey() + "Range", getConfigurationSection(), 32, 1, 128, "Defines the radius (in blocks) in which the ritual will stop mob spawning and projectiles.");
        enabled = cfg.getBoolean(getKey() + "Enabled", getConfigurationSection(), true, "Set to false to disable this ConstellationEffect.");
        potionAmplifier = cfg.getInt(getKey() + "ResistanceAmplifier", getConfigurationSection(), 0, 0, Short.MAX_VALUE, "Set the amplifier for the resistance potion effect.");
        potencyMultiplier = cfg.getFloat(getKey() + "PotencyMultiplier", getConfigurationSection(), 1.0F, 0.01F, 100F, "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
    }

}
