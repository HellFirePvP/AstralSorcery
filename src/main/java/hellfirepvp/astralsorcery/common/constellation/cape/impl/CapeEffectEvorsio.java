/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape.impl;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectEvorsio
 * Created by HellFirePvP
 * Date: 14.10.2017 / 17:07
 */
public class CapeEffectEvorsio extends CapeArmorEffect {

    private static float percDamageAppliedNearby = 0.5F;
    private static float rangeDeathAOE = 4F;

    public CapeEffectEvorsio(NBTTagCompound cmp) {
        super(cmp, "evorsio");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.evorsio;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        playConstellationCapeSparkles(pl, 0.15F);
    }

    @SideOnly(Side.CLIENT)
    public static void playBlockBreakParticles(PktParticleEvent event) {
        Vector3 at = event.getVec();
        if(!Minecraft.getMinecraft().player.isCreative() && event.getAdditionalData() != 0) {
            int stateId = (int) Math.round(event.getAdditionalData());
            IBlockState state = Block.getStateById(stateId);
            if(state != Blocks.AIR.getDefaultState()) {
                ParticleManager pm = Minecraft.getMinecraft().effectRenderer;
                World world = Minecraft.getMinecraft().world;
                if(!state.getBlock().addDestroyEffects(world, at.toBlockPos(), pm)) {
                    RenderingUtils.playBlockBreakParticles(at.toBlockPos(), state);
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            double x = at.getX() + rand.nextFloat();
            double y = at.getY() + rand.nextFloat();
            double z = at.getZ() + rand.nextFloat();
            float scale = rand.nextFloat() * 0.2F + 0.3F;
            float mX = rand.nextFloat() * 0.01F * (rand.nextBoolean() ? 1 : -1);
            float mY = rand.nextFloat() * 0.01F * (rand.nextBoolean() ? 1 : -1);
            float mZ = rand.nextFloat() * 0.01F * (rand.nextBoolean() ? 1 : -1);

            Color c = Constellations.evorsio.getConstellationColor();
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(x, y, z);
            p.setColor(c).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.scale(scale);
            if(rand.nextInt(6) == 0) {
                p.setColor(IConstellation.weak);
            }
            p.setMaxAge(30 + rand.nextInt(10));
            p.motion(mX, mY, mZ);

            if(rand.nextFloat() < 0.4F) {
                p = EffectHelper.genericFlareParticle(x, y, z);
                p.setColor(Color.WHITE).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(scale * 0.35F);
                p.setMaxAge(20 + rand.nextInt(10));
                p.motion(mX, mY, mZ);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void playAreaDamageParticles(PktParticleEvent event) {
        Vector3 offset = event.getVec();
        Color c = Constellations.evorsio.getConstellationColor();
        EntityFXFacingParticle p;
        for (int i = 0; i < 45; i++) {
            Vector3 dir = Vector3.random();
            dir.setY(dir.getY() * 0.2F).normalize().multiply(rangeDeathAOE / 2F);
            Vector3 off = dir.clone().multiply(0.1F + rand.nextFloat() * 0.4F);
            Vector3 mov = dir.clone().multiply(0.01F);
            float scale = rand.nextFloat() * 0.2F + 0.2F;

            p = EffectHelper.genericFlareParticle(offset.getX() + off.getX(), offset.getY() + off.getY(), offset.getZ() + off.getZ());
            p.setColor(c).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.scale(scale);
            p.setMaxAge(30 + rand.nextInt(10));
            p.motion(mov.getX(), mov.getY(), mov.getZ());

            if(rand.nextFloat() < 0.4F) {
                p = EffectHelper.genericFlareParticle(offset.getX() + off.getX(), offset.getY() + off.getY(), offset.getZ() + off.getZ());
                p.setColor(Color.WHITE).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(scale * 0.35F);
                p.setMaxAge(20 + rand.nextInt(10));
                p.motion(mov.getX(), mov.getY(), mov.getZ());
            }
        }
        for (int i = 0; i < 65; i++) {
            Vector3 dir = Vector3.random();
            dir.setY(dir.getY() * 0.3F).normalize().multiply(rangeDeathAOE / 2F);
            Vector3 off = dir.clone().multiply(0.5F + rand.nextFloat() * 0.4F);
            Vector3 mov = dir.clone().multiply(0.015F);
            float scale = rand.nextFloat() * 0.2F + 0.2F;

            p = EffectHelper.genericFlareParticle(offset.getX() + off.getX(), offset.getY() + off.getY(), offset.getZ() + off.getZ());
            p.setColor(IConstellation.weak).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.scale(scale);
            p.setMaxAge(30 + rand.nextInt(10));
            p.motion(mov.getX(), mov.getY(), mov.getZ());

            if(rand.nextFloat() < 0.4F) {
                p = EffectHelper.genericFlareParticle(offset.getX() + off.getX(), offset.getY() + off.getY(), offset.getZ() + off.getZ());
                p.setColor(Color.WHITE).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(scale * 0.35F);
                p.setMaxAge(20 + rand.nextInt(10));
                p.motion(mov.getX(), mov.getY(), mov.getZ());
            }
        }
    }

    public void deathAreaDamage(DamageSource ds, EntityLivingBase entityLiving) {
        if(percDamageAppliedNearby > 0) {
            float damage = entityLiving.getMaxHealth() * percDamageAppliedNearby;

            float r = rangeDeathAOE;
            java.util.List<EntityLivingBase> eList = entityLiving.world.getEntitiesWithinAABB(
                    EntityLivingBase.class,
                    new AxisAlignedBB(-r, -r, -r, r, r, r).offset(entityLiving.getPosition()),
                    e -> e != null && !e.isDead && e.getHealth() > 0 && e.isCreatureType(EnumCreatureType.MONSTER, false));
            for (EntityLivingBase el : eList) {
                int preTime = el.hurtResistantTime;
                el.attackEntityFrom(ds, damage);
                el.hurtResistantTime = Math.max(preTime, el.hurtResistantTime);
            }

            PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CAPE_EVORSIO_AOE, Vector3.atEntityCenter(entityLiving));
            PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(entityLiving.world, entityLiving.getPosition(), 16));
        }
    }

    public void breakBlocksPlane(EntityPlayerMP player, EnumFacing sideBroken, World world, BlockPos at) {
        for (int xx = -2; xx <= 2; xx++) {
            if(sideBroken.getDirectionVec().getX() != 0 && xx != 0) continue;
            for (int yy = -1; yy <= 3; yy++) {
                if(sideBroken.getDirectionVec().getY() != 0 && yy != 0) continue;
                for (int zz = -2; zz <= 2; zz++) {
                    if(sideBroken.getDirectionVec().getZ() != 0 && zz != 0) continue;
                    BlockPos other = at.add(xx, yy, zz);
                    if(world.getTileEntity(other) == null && world.getBlockState(other).getBlockHardness(world, other) != -1) {
                        IBlockState present = world.getBlockState(other);
                        if(MiscUtils.breakBlockWithPlayer(other, player)) {
                            PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CAPE_EVORSIO_BREAK, other);
                            ev.setAdditionalData(Block.getStateId(present));
                            PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, other, 16));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        percDamageAppliedNearby = cfg.getFloat(getKey() + "PercentLifeDamage", getConfigurationSection(), percDamageAppliedNearby, 0, 10, "Defines the multiplier how much of the dead entity's max-life should be dealt as AOE damage to mobs nearby.");
        rangeDeathAOE = cfg.getFloat(getKey() + "DeathAOERange", getConfigurationSection(), rangeDeathAOE, 0.5F,50, "Defines the Range of the death-AOE effect of when a mob gets killed by a player with this cape on.");
    }

}
