package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
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
 * Class: CEffectOrion
 * Created by HellFirePvP
 * Date: 07.11.2016 / 22:30
 */
public class CEffectOrion extends ConstellationEffect {

    public static boolean enabled = true;
    public static double range = 16.0D;
    public static float damage = 4.0F;

    private static AxisAlignedBB baseBoundingBox = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    public CEffectOrion() {
        super(Constellations.orion, "orion");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility, boolean extendedEffects) {
        if(!Minecraft.isFancyGraphicsEnabled() && rand.nextInt(5) != 0) return;
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5);
        p.motion((rand.nextFloat() * 0.1F) * (rand.nextBoolean() ? 1 : -1),
                 (rand.nextFloat() * 0.1F) * (rand.nextBoolean() ? 1 : -1),
                 (rand.nextFloat() * 0.1F) * (rand.nextBoolean() ? 1 : -1));
        p.scale(0.25F).setColor(Color.RED);
    }

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable Constellation possibleTraitEffect) {
        if(!enabled) return false;
        if(percStrength < 1) {
            if(world.rand.nextFloat() > percStrength) return false;
        }
        float actDamageDealt = percStrength * damage;
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, baseBoundingBox.offset(pos), (entity) -> !entity.isDead && !(entity instanceof EntityPlayer));
        EntityPlayer owner = getOwningPlayerInWorld(world, pos);
        DamageSource dmgSource = owner == null ? CommonProxy.dmgSourceStellar : DamageSource.causePlayerDamage(owner);
        for (EntityLivingBase entity : entities) {
            if(entity.attackEntityFrom(dmgSource, actDamageDealt)) {
                PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_DMG_ENTITY, entity.posX, entity.posY + entity.height / 2, entity.posZ);
                PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, pos, 16));
            }
        }
        return false;
    }

    @Override
    public boolean playTraitEffect(World world, BlockPos pos, Constellation traitType, float traitStrength) {
        return false;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        enabled = cfg.getBoolean(getKey() + "Enabled", getConfigurationSection(), true, "Set to false to disable this ConstellationEffect.");
        range = cfg.getFloat(getKey() + "DamageRange", getConfigurationSection(), 16, 2, 64, "Defines the range in which the ritual will try to find and damage mobs");
        damage = cfg.getFloat(getKey() + "DamageDealt", getConfigurationSection(), 4.0F, 0.1F, 100F, "Defines the max. possible damage dealt per damage tick.");

        baseBoundingBox = new AxisAlignedBB(0, 0, 0, 1, 1, 1).expand(range, range, range);
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
            p.motion((rand.nextFloat() * 0.1F) * (rand.nextBoolean() ? 1 : -1),
                     (rand.nextFloat() * 0.1F) * (rand.nextBoolean() ? 1 : -1),
                     (rand.nextFloat() * 0.1F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.25F).setColor(Color.RED);
        }
    }
}
