/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectEntityCollect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.ILocatable;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
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
 * Class: CEffectOrion
 * Created by HellFirePvP
 * Date: 07.11.2016 / 22:30
 */
public class CEffectDiscidia extends CEffectEntityCollect<EntityLivingBase> {

    public static double potencyMultiplier = 1;
    public static float damage = 6.5F;

    public CEffectDiscidia(@Nullable ILocatable origin) {
        super(origin, Constellations.discidia, "discidia", 16D, EntityLivingBase.class, (entity) -> !entity.isDead && !(entity instanceof EntityPlayer));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility, boolean extendedEffects) {
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5);
        p.motion((rand.nextFloat() * 0.1F) * (rand.nextBoolean() ? 1 : -1),
                 (rand.nextFloat() * 0.1F) * (rand.nextBoolean() ? 1 : -1),
                 (rand.nextFloat() * 0.1F) * (rand.nextBoolean() ? 1 : -1));
        p.scale(0.25F).setColor(Color.DARK_GRAY);
    }

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable IMinorConstellation possibleTraitEffect) {
        if(world.getTotalWorldTime() % 20 != 0) return false;

        percStrength *= potencyMultiplier;
        if(percStrength < 1 ) {
            if(world.rand.nextFloat() > percStrength) return false;
        }
        float actDamageDealt = percStrength * damage;
        List<EntityLivingBase> entities = collectEntities(world, pos);
        if(!entities.isEmpty()) {
            EntityPlayer owner = getOwningPlayerInWorld(world, pos);
            DamageSource dmgSource = owner == null ? CommonProxy.dmgSourceStellar : DamageSource.causePlayerDamage(owner);
            for (EntityLivingBase entity : entities) {
                int hrTime = entity.hurtResistantTime;
                entity.hurtResistantTime = 0;
                try {
                    if(entity.attackEntityFrom(dmgSource, actDamageDealt)) {
                        PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_DMG_ENTITY, entity.posX, entity.posY + entity.height / 2, entity.posZ);
                        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, pos, 16));
                    }
                } finally {
                    entity.hurtResistantTime = hrTime;
                }
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
        super.loadFromConfig(cfg);

        damage = cfg.getFloat(getKey() + "DamageDealt", getConfigurationSection(), damage, 0.1F, 400F, "Defines the max. possible damage dealt per damage tick.");
        potencyMultiplier = cfg.getFloat(getKey() + "PotencyMultiplier", getConfigurationSection(), 1.0F, 0.01F, 100F, "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
    }

    @SideOnly(Side.CLIENT)
    public static void playParticles(PktParticleEvent event) {
        if(!Minecraft.isFancyGraphicsEnabled()) return;
        Vector3 pos = event.getVec();
        EntityFXFacingParticle p;
        for (int i = 0; i < 6; i++) {
            p = EffectHelper.genericFlareParticle(
                    pos.getX(),
                    pos.getY(),
                    pos.getZ());
            p.motion((rand.nextFloat() * 0.05F) * (rand.nextBoolean() ? 1 : -1),
                     (rand.nextFloat() * 0.05F) * (rand.nextBoolean() ? 1 : -1),
                     (rand.nextFloat() * 0.05F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.25F).setColor(Color.RED);
        }
    }
}
