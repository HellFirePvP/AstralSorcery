/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.spell.entity;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.constellation.spell.ISpellComponent;
import hellfirepvp.astralsorcery.common.constellation.spell.ISpellEffect;
import hellfirepvp.astralsorcery.common.constellation.spell.SpellControllerEffect;
import hellfirepvp.astralsorcery.common.util.ASDataSerializers;
import hellfirepvp.astralsorcery.common.util.EntityUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpellProjectile
 * Created by HellFirePvP
 * Date: 07.07.2017 / 10:48
 */
public class SpellProjectile extends EntityThrowable implements IProjectile, ISpellComponent {

    private static final AxisAlignedBB affectBox = new AxisAlignedBB(-5, -5, -5, 5, 5, 5);

    private static final DataParameter<Vector3> TARGET =      EntityDataManager.createKey(SpellProjectile.class, ASDataSerializers.VECTOR);
    private static final DataParameter<Integer> COLOR_TRAIL = EntityDataManager.createKey(SpellProjectile.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> COLOR_SPARK = EntityDataManager.createKey(SpellProjectile.class, DataSerializers.VARINT);

    private int ticksNextSpellPulse = -1, spellPulseInterval = -1;

    private float maxVelocity;
    private double lastTickDst = Double.MAX_VALUE;
    private int vDstTick = 0;

    private SpellControllerEffect spellController;
    private SpellTarget target;

    public SpellProjectile(World worldIn) {
        super(worldIn);
    }

    public SpellProjectile(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public SpellProjectile(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public SpellProjectile(World worldIn, SpellControllerEffect controllerEffect, float inaccuracy) {
        super(worldIn, controllerEffect.getCaster());
        setSpellController(controllerEffect);
        float x = -MathHelper.sin(controllerEffect.getCaster().rotationYaw     * 0.017453292F)
                * MathHelper.cos(controllerEffect.getCaster().rotationPitch    * 0.017453292F);
        float y = -MathHelper.sin((controllerEffect.getCaster().rotationPitch) * 0.017453292F);
        float z =  MathHelper.cos(controllerEffect.getCaster().rotationYaw     * 0.017453292F)
                * MathHelper.cos(controllerEffect.getCaster().rotationPitch    * 0.017453292F);
        this.setThrowableHeading((double) x, (double) y, (double) z, 0.9F + rand.nextFloat() * 1.4F, inaccuracy);
    }

    public SpellProjectile(World worldIn, SpellControllerEffect controllerEffect, Vector3 motion, float speed) {
        super(worldIn, controllerEffect.getCaster());
        setSpellController(controllerEffect);
        this.setThrowableHeading(motion.getX(), motion.getY(), motion.getZ(), speed, 0);
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        this.dataManager.register(TARGET, new Vector3());
        this.dataManager.register(COLOR_TRAIL, 0xFFFFFFFF);
        this.dataManager.register(COLOR_SPARK, 0xFFFFFFFF);
    }

    @Override
    public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy) {
        super.setThrowableHeading(x, y, z, velocity, inaccuracy);

        this.maxVelocity = velocity;
    }

    public void setTarget(SpellTarget target) {
        this.target = target;
        this.dataManager.set(TARGET, target.getTargetVector());
    }

    public SpellTarget getTarget() {
        return target;
    }

    public void setSpellController(SpellControllerEffect spellController) {
        this.spellController = spellController;
    }

    public void scheduleNextSpellPulse(int ticks) {
        this.ticksNextSpellPulse = ticks;
        this.spellPulseInterval = ticks;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if(!world.isRemote) {
            if(this.spellController == null || this.target == null) {
                setDead();
                return;
            }

            Vector3 motion = new Vector3(this.motionX, this.motionY, this.motionZ);
            double vel = motion.length();
            motion.normalize().multiply(MathHelper.clamp(vel, 0.1, maxVelocity));
            this.motionX = motion.getX();
            this.motionY = motion.getY();
            this.motionZ = motion.getZ();

            Vector3 target = getTarget().getTargetVector();
            EntityUtils.applyVortexMotion((v) -> Vector3.atEntityCorner(this).addY(this.getEyeHeight()),
                    (v) -> {
                        this.motionX += v.getX();
                        this.motionY += v.getY();
                        this.motionZ += v.getZ();
                        return null;
                    },
                    target, 512, 2);

            double flatLength = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
            this.rotationPitch = (float) (MathHelper.atan2(this.motionY, flatLength) * (180D / Math.PI));

            double dst = Vector3.atEntityCorner(this).addY(this.getEyeHeight()).distance(target);

            if(dst < 0.2) {
                setDead();
                return;
            }
            if(dst < lastTickDst) {
                lastTickDst = dst;
            }
            if(dst > lastTickDst && ticksExisted > 30) {
                vDstTick++;

                if(vDstTick > 10) {
                    setDead();
                    return;
                }
            } else {
                vDstTick = 0;
            }

            if(ticksNextSpellPulse > 0) {
                ticksNextSpellPulse--;

                if(ticksNextSpellPulse <= 0) {
                    spellEffect();
                    ticksNextSpellPulse = spellPulseInterval;
                }
            }
        } else {
            spawnEffects();
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnEffects() {
        EntityFXFacingParticle particle;

        Vector3 interp = RenderingUtils.interpolatePosition(this, 0);
        Color main = getColorTrail();
        Color spark = getColorSparks();

        particle = EffectHelper.genericFlareParticle(interp.getX(), interp.getY(), interp.getZ());
        particle.scale(1F + rand.nextFloat() * 0.3F).setMaxAge(12 + rand.nextInt(6));
        particle.setColor(main);
        if(!this.firstUpdate) {
            interp = RenderingUtils.interpolatePosition(this, 0.25F);
            particle = EffectHelper.genericFlareParticle(interp.getX(), interp.getY(), interp.getZ());
            particle.scale(0.75F + rand.nextFloat() * 0.3F).setMaxAge(12 + rand.nextInt(6));
            particle.setColor(main);
            interp = RenderingUtils.interpolatePosition(this, 0.5F);
            particle = EffectHelper.genericFlareParticle(interp.getX(), interp.getY(), interp.getZ());
            particle.scale(0.75F + rand.nextFloat() * 0.3F).setMaxAge(12 + rand.nextInt(6));
            particle.setColor(main);
            interp = RenderingUtils.interpolatePosition(this, 0.75F);
            particle = EffectHelper.genericFlareParticle(interp.getX(), interp.getY(), interp.getZ());
            particle.scale(0.75F + rand.nextFloat() * 0.3F).setMaxAge(12 + rand.nextInt(6));
            particle.setColor(main);
        }
        for (int i = 0; i < 3; i++) {
            interp = RenderingUtils.interpolatePosition(this, rand.nextFloat());
            particle = EffectHelper.genericFlareParticle(interp.getX(), interp.getY(), interp.getZ());
            particle.motion(
                    0.02F - rand.nextFloat() * 0.04F,
                    0.02F - rand.nextFloat() * 0.04F,
                    0.02F - rand.nextFloat() * 0.04F).scale(0.45F + rand.nextFloat() * 0.2F).setMaxAge(15 + rand.nextInt(10));
            particle.setColor(spark);
        }
    }

    private void spellEffect() {
        for (ISpellEffect effect : this.spellController) {
            effect.affectProjectileMajor(this);
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if(!world.isRemote) {
            if(this.spellController == null) {
                setDead();
                return;
            }
            Entity entityHit = null;
            switch (result.typeOfHit) {
                case ENTITY:
                    if(this.spellController.getCaster().equals(result.entityHit)) {
                        return;
                    }
                    entityHit = result.entityHit;
                    break;
            }
            if(entityHit != null) {
                entityHit = EntityUtils.selectClosest(
                        world.getEntitiesInAABBexcluding(this,
                                affectBox.offset(this.getPosition()),
                                EntityUtils.selectEntities(EntityLivingBase.class)),
                        entity1 -> this.getPosition().getDistance((int) entity1.posX, (int) entity1.posY, (int) entity1.posZ));
            }
            spellController.projectileImpact(this, result, entityHit instanceof EntityLivingBase ? (EntityLivingBase) entityHit : null);
            setDead();
        }
    }

    @Override
    public void onUpdateController() {
        if(this.spellController == null) {
            setDead();
            return;
        }
        spellController.forEach(c -> c.affectProjectile(this));
    }

    public void setColors(int colorTrail, int colorSparks) {
        this.dataManager.set(COLOR_TRAIL, colorTrail);
        this.dataManager.set(COLOR_SPARK, colorSparks);
    }

    public Color getColorTrail() {
        return new Color(this.dataManager.get(COLOR_TRAIL));
    }

    public Color getColorSparks() {
        return new Color(this.dataManager.get(COLOR_SPARK));
    }

    @Override
    public boolean isValid() {
        return !isDead && !onGround;
    }

    @Override
    protected float getGravityVelocity() {
        return 0;
    }

    @Nullable
    @Override
    public SpellControllerEffect getSpellController() {
        return this.spellController;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 16.0D;
        if (Double.isNaN(d0)) {
            d0 = 16.0D;
        }
        d0 = d0 * 64.0D;
        return distance < d0 * d0;
    }

    @Override
    public ISpellComponent copy() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        tag.removeTag("UUIDMost");
        tag.removeTag("UUIDLeast");
        Entity e = EntityList.newEntity(getClass(), world);
        if(e == null) {
            throw new IllegalStateException("Unknown or unregistered entity with class: " + getClass().getName());
        }
        if(!(e instanceof ISpellComponent)) {
            throw new IllegalStateException("Entity is not a ISpellComponent: " + getClass().getName());
        }
        e.readFromNBT(tag);
        return (ISpellComponent) e;
    }

    public static SpellTarget getTarget(EntityLivingBase entity) {
        RayTraceResult rtr = MiscUtils.rayTraceLook(entity, 150);
        if(rtr != null) {
            switch (rtr.typeOfHit) {
                case ENTITY:
                    if(rtr.entityHit instanceof EntityLivingBase) {
                        return new SpellTarget((EntityLivingBase) rtr.entityHit);
                    }
                case BLOCK:
                    Entity closest = EntityUtils.selectClosest(
                            entity.world.getEntitiesInAABBexcluding(entity,
                                    affectBox.offset(rtr.hitVec),
                                    EntityUtils.selectEntities(EntityLivingBase.class)),
                            entity1 -> rtr.hitVec.distanceTo(new Vec3d(entity1.posX, entity1.posY, entity1.posZ)));
                    if(closest != null && closest instanceof EntityLivingBase) {
                        return new SpellTarget((EntityLivingBase) closest);
                    }

            }
            return new SpellTarget(new Vector3(rtr.hitVec));
        }
        Vec3d look = entity.getLook(1F).scale(150);
        return new SpellTarget(Vector3.atEntityCorner(entity).add(look.x, look.y, look.z));
    }

    private static class SpellTarget {

        private Vector3 fixed;
        private EntityLivingBase entity;

        private SpellTarget(Vector3 fixed) {
            this.fixed = fixed;
        }

        private SpellTarget(EntityLivingBase entity) {
            this.entity = entity;
        }

        private boolean isFix() {
            return fixed != null;
        }

        private Vector3 getTargetVector() {
            if(entity != null) {
                return Vector3.atEntityCorner(entity).addY(entity.getEyeHeight());
            }
            return fixed;
        }

    }

}
