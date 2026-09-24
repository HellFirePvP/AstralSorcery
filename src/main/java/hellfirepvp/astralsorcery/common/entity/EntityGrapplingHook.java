/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.vfx.VFXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.RenderVectorUtil;
import hellfirepvp.astralsorcery.common.event.helper.DamageCancellingHelper;
import hellfirepvp.astralsorcery.common.lib.EntitiesAS;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.util.ClientObject;
import hellfirepvp.astralsorcery.common.util.ServerSoundHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EntityGrapplingHook
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EntityGrapplingHook extends ThrowableProjectile {

    private static final EntityDataAccessor<Integer> PULLING_ENTITY = SynchedEntityData.defineId(EntityGrapplingHook.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> PULLING = SynchedEntityData.defineId(EntityGrapplingHook.class, EntityDataSerializers.BOOLEAN);

    private int timeout = 0;
    private double previousDist = 0;
    private boolean launchedThrower = false;

    private int despawning = -1;
    private float pullFactor = 0.0F;

    public EntityGrapplingHook(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public EntityGrapplingHook(LivingEntity shooter, Level level) {
        this(EntitiesAS.GRAPPLING_HOOK.get(), shooter, level);
    }

    protected EntityGrapplingHook(EntityType<? extends ThrowableProjectile> entityType, LivingEntity shooter, Level level) {
        super(entityType, shooter, level);
        this.shoot(Vector3.directionFromYawPitch(shooter.getYRot(), shooter.getXRot()), 1.5F);
        this.setOwner(shooter);
        ServerSoundHelper.playSoundAround(SoundsAS.GRAPPLING_WAND_CAST, level, this.position().add(this.getDeltaMovement()), 0.5F,
                0.85F + random.nextFloat() * 0.3F);
    }

    public static EntityType.EntityFactory<EntityGrapplingHook> factory() {
        return EntityGrapplingHook::new;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(PULLING, false);
        builder.define(PULLING_ENTITY, -1);
    }

    public void setPulling(boolean pull, @Nullable LivingEntity hit) {
        this.entityData.set(PULLING, pull);
        this.entityData.set(PULLING_ENTITY, hit == null ? -1 : hit.getId());
    }

    public boolean isPulling() {
        return this.entityData.get(PULLING);
    }

    @Nullable
    public LivingEntity getPulling() {
        int idPull = this.entityData.get(PULLING_ENTITY);
        if (idPull > 0) {
            try {
                return (LivingEntity) this.level().getEntity(idPull);
            } catch (Exception ignored) {}
        }
        return null;
    }

    //0 = none, 1=basically gone
    public float despawnPercentage(float partial) {
        float p = this.despawning - (1 - partial);
        p /= 10;
        return Mth.clamp(p, 0, 1);
    }

    public boolean isDespawning() {
        return this.despawning != -1;
    }

    private void setDespawning() {
        if (this.despawning == -1) {
            this.despawning = 0;
        }
    }

    private void despawnTick() {
        this.despawning++;
        if (this.despawning > 10) {
            this.remove(RemovalReason.DISCARDED);
        }
        if (this.despawning == 6) {
            if (!this.level().isClientSide() && !this.isPulling()) {
                ServerSoundHelper.playSoundAround(SoundsAS.GRAPPLING_WAND_FAIL, this.level(), this.position(), 0.5F,
                        0.85F + random.nextFloat() * 0.3F);
                if (this.getOwner() instanceof Player) {
                    ServerSoundHelper.playSoundAround(SoundsAS.GRAPPLING_WAND_FAIL, this.level(),
                            this.getOwner().position().add(this.getDeltaMovement()), 0.5F,
                            0.85F + random.nextFloat() * 0.3F);
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        Entity owner = this.getOwner();
        if (owner == null || !owner.isAlive()) {
            this.setDespawning();
        }
        if (!this.isPulling() && this.tickCount >= 30) {
            this.setDespawning();
        }

        Level level = this.level();
        if (level.isClientSide()) {
            if (this.isPulling()) {
                this.pullFactor *= 0.7F;
            } else {
                this.pullFactor += 0.02F;
            }
        }

        if (this.isDespawning()) {
            this.despawnTick();

            if (level.isClientSide() && this.despawning == 6) {
                this.playDespawnParticles();
            }
            return;
        }

        if (!this.isAlive() || !this.isPulling() || owner == null) return;

        double dist = Math.max(0.01, owner.distanceTo(this));
        LivingEntity pulling = this.getPulling();
        if (pulling != null) {
            this.setPos(pulling.getX(), pulling.getY(), pulling.getZ());
        }
        if (((pulling != null && this.tickCount > 60 && dist < 2) ||
                (pulling == null && this.tickCount > 15 && dist < 2)) ||
                this.timeout > 15) {
            this.setDespawning();
            return;
        }

        owner.fallDistance = -2F;
        owner.setOnGround(false);
        double mx = this.getX() - owner.getX();
        double my = this.getY() - owner.getY();
        double mz = this.getZ() - owner.getZ();
        mx /= dist * 5.0D;
        my /= dist * 5.0D;
        mz /= dist * 5.0D;
        Vec3 v2 = new Vec3(mx, my, mz);
        if (v2.length() > 0.25D) {
            v2 = v2.normalize();
            mx = v2.x / 4.0D;
            my = v2.y / 4.0D;
            mz = v2.z / 4.0D;
        }

        Vec3 motion = owner.getDeltaMovement();
        motion = motion.add(mx, my + 0.04F, mz);
        if (!this.launchedThrower) {
            motion = motion.add(0, 0.4F, 0);
            this.launchedThrower = true;
        }
        owner.setDeltaMovement(motion);

        if (owner instanceof ServerPlayer sPlayer) {
            DamageCancellingHelper.preventNextDamage(sPlayer, DamageTypes.FALL);
        }

        int roughDst = (int) (dist / 2.5D);
        if (roughDst >= this.previousDist) {
            this.timeout += 1;
        } else {
            this.timeout = 0;
        }
        this.previousDist = roughDst;
    }

    @OnlyIn(Dist.CLIENT)
    private void playDespawnParticles() {
        if (this.isPulling()) return;

        Vector3 iPos = RenderVectorUtil.interpolatePosition(this, 1F);
        this.buildLine(1F).forEach(pos -> {
            VFXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos.add(Vector3.random(this.random).multiply(0.1F)).add(iPos))
                    .setScale(0.3F + this.random.nextFloat() * 0.2F)
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .color(FXColorFunction.constant(ColorsAS.ENTITY_GRAPPLING_HOOK))
                    .setMotion(Vector3.random(this.random).multiply(0.005F))
                    .setMaxAge(25 + this.random.nextInt(20));
            if (this.random.nextBoolean()) {
                p.color(FXColorFunction.WHITE);
            }
        });
    }

    @Override
    protected double getDefaultGravity() {
        return this.isPulling() ? 0 : super.getDefaultGravity();
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double dst = this.getBoundingBox().getSize() * 64D;
        if (Double.isNaN(dst)) {
            dst = 64D;
        }
        dst = dst * 64.0D;
        return distance < dst * dst;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return AABB.INFINITE;
    }

    @OnlyIn(Dist.CLIENT)
    public List<Vector3> buildLine(float partial) {
        return this.buildLine(partial, 0);
    }

    @OnlyIn(Dist.CLIENT)
    public List<Vector3> buildLine(float partial, float offset) {
        Entity thrower = this.getOwner();
        if (thrower == null) {
            return Collections.emptyList();
        }

        List<Vector3> list = Lists.newLinkedList();
        Vector3 interpThrower = RenderVectorUtil.interpolatePosition(thrower, partial);
        Vector3 interpHook = RenderVectorUtil.interpolatePosition(this, partial);
        Vector3 to = interpThrower.copy().subtract(interpHook).addY(thrower.getBbHeight() / 4);
        float lineLength = (float) (to.length() * 5);
        int iter = (int) lineLength;
        for (int xx = 1; xx < iter - 1; xx++) {
            float dist = xx * (lineLength / iter);
            double dx = (interpThrower.getX() - interpHook.getX())                              / iter * xx + offset + Mth.sin(dist / 10.0F) * this.pullFactor;
            double dy = (interpThrower.getY() - interpHook.getY() + thrower.getBbHeight() / 2F) / iter * xx + offset + Mth.sin(dist / 7.0F)  * this.pullFactor;
            double dz = (interpThrower.getZ() - interpHook.getZ())                              / iter * xx + offset + Mth.sin(dist / 2.0F)  * this.pullFactor;
            list.add(new Vector3(dx, dy, dz));
        }

        return list;
    }

    public void shoot(Vector3 dir, float velocity) {
        this.shoot(dir.getX(), dir.getY(), dir.getZ(), velocity, 0);
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, 0F);
    }

    @Override
    protected void onHit(HitResult result) {
        if (this.despawning >= 2) return;

        Vec3 loc = result.getLocation();
        switch (result.getType()) {
            case BLOCK -> {
                this.setPulling(true, null);

                if (!this.level().isClientSide()) {
                    ServerSoundHelper.playSoundAround(SoundsAS.GRAPPLING_WAND_ATTACH, this.level(), this.position(), 0.5F,
                            0.85F + random.nextFloat() * 0.3F);
                    if (this.getOwner() instanceof Player) {
                        ServerSoundHelper.playSoundAround(SoundsAS.GRAPPLING_WAND_ATTACH, this.level(),
                                this.getOwner().position().add(this.getDeltaMovement()), 0.5F,
                                0.85F + random.nextFloat() * 0.3F);
                    }
                }
            }
            case ENTITY -> {
                if (result instanceof EntityHitResult ehr) {
                    Entity hit = ehr.getEntity();
                    if (hit instanceof LivingEntity hitEntity && !hitEntity.equals(this.getOwner())) {
                        this.setPulling(true, hitEntity);
                        loc = loc.add(0, hitEntity.getBbHeight() * 3 / 4, 0);

                        if (!this.level().isClientSide()) {
                            ServerSoundHelper.playSoundAround(SoundsAS.GRAPPLING_WAND_ATTACH, this.level(), this.position(), 0.5F,
                                    0.85F + random.nextFloat() * 0.3F);
                            if (this.getOwner() instanceof Player) {
                                ServerSoundHelper.playSoundAround(SoundsAS.GRAPPLING_WAND_ATTACH, this.level(),
                                        this.getOwner().position().add(this.getDeltaMovement()), 0.5F,
                                        0.85F + random.nextFloat() * 0.3F);
                            }
                        }
                    }
                }
            }
        }
        this.setDeltaMovement(0, 0, 0);
        this.setPos(loc.x, loc.y, loc.z);
    }
}
