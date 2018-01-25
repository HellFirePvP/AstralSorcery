/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingSprite;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectBootes;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.item.wearable.ItemCape;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityFlare
 * Created by HellFirePvP
 * Date: 07.02.2017 / 12:15
 */
public class EntityFlare extends EntityFlying {

    private static final int strollRange = 31;

    public Object texSprite = null;
    private BlockPos moveTarget = null;
    private boolean isAmbient = false;
    private int entityAge = 0;

    private int followingEntityId = -1;

    public EntityFlare(World worldIn) {
        super(worldIn);
        setSize(0.7F, 0.7F);
    }

    public EntityFlare(World worldIn, double x, double y, double z) {
        super(worldIn);
        setSize(0.7F, 0.7F);
        this.setPositionAndRotation(x, y, z, 0, 0);
    }

    public EntityFlare setAmbient(boolean ambient) {
        this.isAmbient = ambient;
        return this;
    }

    public EntityFlare setFollowingTarget(EntityPlayer player) {
        this.followingEntityId = player.getEntityId();
        return this;
    }

    public static void spawnAmbient(World world, Vector3 at) {
        if(world.isRemote) return;
        if(Config.ambientFlareChance <= 0) return;
        float nightPerc = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(world);
        if(world.rand.nextInt(Config.ambientFlareChance) == 0 && world.isAirBlock(at.toBlockPos()) && world.rand.nextFloat() < nightPerc) {
            world.spawnEntity(new EntityFlare(world, at.getX(), at.getY(), at.getZ()).setAmbient(true));
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        entityAge++;

        if(world.isRemote) {
            if(texSprite == null) {
                setupSprite();
            }
            clientUpdate();
        } else {
            if(followingEntityId != -1) {
                if(getFollowingEntity() == null) {
                    damageEntity(DamageSource.MAGIC, 20F);
                }
            } else if(entityAge > 300 && rand.nextInt(700) == 0) {
                damageEntity(DamageSource.MAGIC, 20F);
            }

            if(!isDead) {

                if(Config.flareKillsBats && entityAge % 70 == 0 && rand.nextBoolean()) {
                    Entity closest = world.findNearestEntityWithinAABB(EntityBat.class, getEntityBoundingBox().grow(10), this);
                    if(closest != null && closest instanceof EntityBat && ((EntityBat) closest).getHealth() > 0 && !closest.isDead) {
                        closest.attackEntityFrom(CommonProxy.dmgSourceStellar, 40F);
                        PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.FLARE_PROC, new Vector3(posX, posY + this.height / 2, posZ));
                        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, getPosition(), 16));
                        AstralSorcery.proxy.fireLightning(world, Vector3.atEntityCenter(this), Vector3.atEntityCenter(closest), new Color(0, 0, 216));
                    }
                }

                if(isAmbient) {
                    if ((moveTarget == null || getDistanceSq(moveTarget) < 5D) && rand.nextInt(260) == 0) {
                        moveTarget = getPosition().add(-strollRange / 2, -strollRange / 2, -strollRange / 2).add(rand.nextInt(strollRange), rand.nextInt(strollRange), rand.nextInt(strollRange));
                    }

                    if (moveTarget != null && (moveTarget.getY() <= 1 || !world.isAirBlock(moveTarget) || getDistanceSq(moveTarget) < 5D)) {
                        moveTarget = null;
                    }
                } else if(followingEntityId != -1) {
                    if(getFollowingEntity() != null) {
                        EntityPlayer e = getFollowingEntity();
                        CapeEffectBootes cb = ItemCape.getCapeEffect(e, Constellations.bootes);
                        if(cb == null) {
                            followingEntityId = -1;
                            return;
                        }
                        if(getAttackTarget() != null && getAttackTarget().isDead) {
                            setAttackTarget(null);
                        }
                        if(getAttackTarget() == null) {
                            moveTarget = Vector3.atEntityCenter(getFollowingEntity()).addY(-1 + rand.nextFloat() * 2.5).toBlockPos();
                        }
                    }
                } else if(getAttackTarget() != null) {
                    if(getAttackTarget().isDead) {
                        if(rand.nextInt(30) == 0) {
                            damageEntity(DamageSource.MAGIC, 20F);
                        }
                    } else {
                        moveTarget = Vector3.atEntityCenter(getAttackTarget()).toBlockPos();
                    }

                    if(moveTarget != null && (moveTarget.getY() <= 1 || getDistanceSq(moveTarget) < 3D)) {
                        moveTarget = null;
                    }
                }

                if(getAttackTarget() != null && !getAttackTarget().isDead && getAttackTarget().getDistanceToEntity(this) < 10 && rand.nextInt(40) == 0) {
                    getAttackTarget().attackEntityFrom(CommonProxy.dmgSourceStellar, 4F);
                    PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.FLARE_PROC, new Vector3(posX, posY + this.height / 2, posZ));
                    PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, getPosition(), 16));
                    AstralSorcery.proxy.fireLightning(world, Vector3.atEntityCenter(this), Vector3.atEntityCenter(getAttackTarget()), new Color(0, 0, 216));
                }

                if(moveTarget != null) {
                    this.motionX += (Math.signum(moveTarget.getX() + 0.5D - this.posX) * 0.5D - this.motionX) * (isAmbient ? 0.01D : 0.02D);
                    this.motionY += (Math.signum(moveTarget.getY() + 0.5D - this.posY) * 0.7D - this.motionY) * (isAmbient ? 0.01D : 0.02D);
                    this.motionZ += (Math.signum(moveTarget.getZ() + 0.5D - this.posZ) * 0.5D - this.motionZ) * (isAmbient ? 0.01D : 0.02D);
                    this.moveForward = 0.2F;
                }
            }
        }
    }

    @Nullable
    public EntityPlayer getFollowingEntity() {
        Entity e = world.getEntityByID(this.followingEntityId);
        if(e == null || e.isDead || !(e instanceof EntityPlayer)) {
            return null;
        }
        return (EntityPlayer) e;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public boolean getCanSpawnHere() {
        return false;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox();
    }

    @Override
    protected void damageEntity(DamageSource damageSrc, float damageAmount) {
        super.damageEntity(damageSrc, damageAmount);
        setHealth(0F);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    protected void onDeathUpdate() {
        setDead();

        if(world.isRemote) {
            deathEffectsEnd();
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("as_entityAge", this.entityAge);
        compound.setBoolean("isSpawnedAmbient", this.isAmbient);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.entityAge = compound.getInteger("as_entityAge");
        this.isAmbient = compound.getBoolean("isSpawnedAmbient");
    }

    @SideOnly(Side.CLIENT)
    private void deathEffectsOngoing() {
        for (int i = 0; i < 3; i++) {
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(posX, posY, posZ);
            particle.motion(-0.05 + rand.nextFloat() * 0.1, -0.05 + rand.nextFloat() * 0.1, -0.05 + rand.nextFloat() * 0.1);
            particle.scale(0.1F + rand.nextFloat() * 0.2F).gravity(-0.02);
            if(rand.nextBoolean()) {
                particle.setColor(Color.WHITE);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void deathEffectsEnd() {
        EntityFXFacingSprite p = (EntityFXFacingSprite) texSprite;
        if(p != null) {
            p.requestRemoval();
        }
        for (int i = 0; i < 29; i++) {
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(posX, posY + this.height / 2, posZ);
            particle.motion(-0.1 + rand.nextFloat() * 0.2, -0.1 + rand.nextFloat() * 0.2, -0.1 + rand.nextFloat() * 0.2);
            particle.scale(0.1F + rand.nextFloat() * 0.2F).gravity(-0.02);
            if(rand.nextBoolean()) {
                particle.setColor(Color.WHITE);
            }
        }
        for (int i = 0; i < 35; i++) {
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(posX, posY, posZ);
            particle.offset(-0.2 + rand.nextFloat() * 0.4, (this.height / 2) - 0.2 + rand.nextFloat() * 0.4, -0.2 + rand.nextFloat() * 0.4);
            particle.scale(0.1F + rand.nextFloat() * 0.2F).gravity(0.004);
            if(rand.nextBoolean()) {
                particle.setColor(Color.WHITE);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void clientUpdate() {
        EntityFXFacingSprite p = (EntityFXFacingSprite) texSprite;
        if(p.isRemoved()) {
            EffectHandler.getInstance().registerFX(p);
        }

        if(rand.nextBoolean()) {
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(posX, posY, posZ);
            particle.offset(-0.3 + rand.nextFloat() * 0.6, (this.height / 2) - 0.3 + rand.nextFloat() * 0.6, -0.3 + rand.nextFloat() * 0.6);
            particle.scale(0.1F + rand.nextFloat() * 0.2F).gravity(-0.02);
            if(rand.nextBoolean()) {
                particle.setColor(Color.WHITE);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void setupSprite() {
        EntityFXFacingSprite p = EntityFXFacingSprite.fromSpriteSheet(SpriteLibrary.spriteFlare1, posX, posY, posZ, 0.8F, 2);
        p.setPositionUpdateFunction((fx, v, m) -> Vector3.atEntityCorner(this).addY(this.height / 2));
        p.setRefreshFunc(() -> !isDead);
        EffectHandler.getInstance().registerFX(p);
        this.texSprite = p;
    }

    @SideOnly(Side.CLIENT)
    public static void playParticles(PktParticleEvent pktParticleEvent) {
        Random rand = new Random();
        Vector3 at = pktParticleEvent.getVec();
        for (int i = 0; i < 17; i++) {
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
            particle.motion(-0.05 + rand.nextFloat() * 0.1, -0.05 + rand.nextFloat() * 0.1, -0.05 + rand.nextFloat() * 0.1);
            particle.scale(0.1F + rand.nextFloat() * 0.2F).gravity(-0.02);
            if(rand.nextBoolean()) {
                particle.setColor(Color.WHITE);
            }
        }
    }

}
