/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityFX;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.vfx.VFXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.visual.type.LightningEffect;
import hellfirepvp.astralsorcery.common.lib.DamageTypesAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.util.*;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EntityFlare
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EntityFlare extends FlyingMob {

    private static final int RANDOM_WANDER_RANGE = 31;

    private int maxEntityAge = 0;
    private Vector3 currentMoveTarget = null;

    private final ClientObject<VFXFacingParticle> flareSprite = new ClientObject<>();

    protected EntityFlare(EntityType<? extends FlyingMob> entityType, Level level) {
        super(entityType, level);
    }

    public static EntityType.EntityFactory<EntityFlare> factory() {
        return EntityFlare::new;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return FlyingMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1)
                .add(Attributes.GRAVITY, 0);
    }

    public int getMaxEntityAge() {
        return this.maxEntityAge;
    }

    public void setMaxEntityAge(int maxEntityAge) {
        this.maxEntityAge = maxEntityAge;
    }

    public void extendMaxAge(int by) {
        this.setMaxEntityAge(this.tickCount + by);
    }

    @Override
    public void tick() {
        super.tick();

        Level level = this.level();
        if (level.isClientSide()) {
            this.tickClient();
            return;
        }

        if (this.tickCount > this.maxEntityAge && this.tickCount > 600 && random.nextInt(600) == 0) {
            DamageUtil.attackEntity(this, level.damageSources().genericKill(), 1F);
            return;
        }

        if (this.isAlive()) {
            if (random.nextInt(30) == 0) {
                Bat closestBat = level.getNearestEntity(Bat.class, TargetingConditions.forCombat(), null,
                        this.getX(), this.getY(), this.getZ(), this.getBoundingBox().inflate(16));
                if (closestBat != null && closestBat.isAlive()) {
                    DamageUtil.shotgunAttack(closestBat, e -> this.doLightningAttack(e, 100F));
                }
            }
            if (random.nextInt(30) == 0) {
                Phantom closestPhantom = level.getNearestEntity(Phantom.class, TargetingConditions.forCombat(), null,
                        this.getX(), this.getY(), this.getZ(), this.getBoundingBox().inflate(16));
                if (closestPhantom != null && closestPhantom.isAlive()) {
                    DamageUtil.shotgunAttack(closestPhantom, e -> this.doLightningAttack(e, 100F));
                }
            }

            if (this.currentMoveTarget == null || this.currentMoveTarget.distanceSquared(this) < 25) {
                this.currentMoveTarget = null;
            }

            if (this.currentMoveTarget == null && random.nextInt(90) == 0) {
                BlockPos target = this.blockPosition()
                        .offset(random.nextInt(RANDOM_WANDER_RANGE) * (random.nextBoolean() ? 1 : -1),
                                random.nextInt(RANDOM_WANDER_RANGE) * (random.nextBoolean() ? 1 : -1),
                                random.nextInt(RANDOM_WANDER_RANGE) * (random.nextBoolean() ? 1 : -1));

                if (ChunkUtil.isChunkLoaded(level, target) &&
                        level.isInWorldBounds(target) &&
                        Vector3.atCenter(target).distanceSquared(this) >= 25) {
                    ClipContext ctx = new ClipContext(Vec3.atCenterOf(this.blockPosition()), Vec3.atCenterOf(target),
                            ClipContext.Block.VISUAL, ClipContext.Fluid.ANY, CollisionContext.empty());
                    if (RayTraceUtil.clip(level, ctx).getType() == HitResult.Type.MISS) {
                        ChunkUtil.executeWithChunk(level, target, () -> {
                            this.currentMoveTarget = Vector3.atCenter(target);
                        });
                    }
                }
            }

            this.doMovement();
        }
    }

    private void doMovement() {
        if (this.currentMoveTarget == null) return;
        Vec3 motion = this.getDeltaMovement();
        double motionX = (Math.signum(this.currentMoveTarget.getX() - this.getX()) * 0.5D - motion.x()) * 0.01D;
        double motionY = (Math.signum(this.currentMoveTarget.getY() - this.getY()) * 0.7D - motion.y()) * 0.01D;
        double motionZ = (Math.signum(this.currentMoveTarget.getZ() - this.getZ()) * 0.5D - motion.z()) * 0.01D;
        this.setDeltaMovement(motion.add(motionX, motionY, motionZ));
    }

    private void doLightningAttack(LivingEntity target, float damage) {
        if (!target.isAlive()) return;

        if (target.level() instanceof ServerLevel sLevel) {
            DamageUtil.attackEntityFrom(target, DamageTypesAS.STELLAR, damage, this);
            LightningEffect.make(new Vector3(this).addY(this.getBbHeight() / 2F), new Vector3(target).addY(this.getBbHeight() / 2F))
                    .sendToNearby(sLevel, this.blockPosition());
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tickClient() {
        if (this.flareSprite.isNull()) {
            this.flareSprite.set(EffectHelper.of(EffectTemplatesAS.ENTITY_FLARE)
                    .spawn(new Vector3(this).addY(this.getBbHeight() / 2F))
                    .setScale(0.9F)
                    .scale((fx, scaleIn, pTicks) -> this.isAlive() ? scaleIn : 0)
                    .position((fx, positionIn, motion) -> new Vector3(this).addY(this.getBbHeight() / 2F))
                    .refresh(fx -> this.isAlive()));
        } else {
            EffectHelper.refresh(EffectTemplatesAS.ENTITY_FLARE, this.flareSprite.get());
        }

        Vector3 pos = new Vector3(this).addY(this.getBbHeight() / 2F);
        pos = VectorUtil.withRandomOffset(pos, random, 0.2F);

        VFXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(pos)
                .alpha(FXAlphaFunction.FADE_OUT)
                .color(FXColorFunction.constant(ColorsAS.ENTITY_FLARE_EFFECT_COLOR))
                .setScale(0.2F + random.nextFloat() * 0.1F);
        if (random.nextBoolean()) {
            p.color(FXColorFunction.constant(ColorWrapper.WHITE));
        }
    }

    @Override
    protected void tickDeath() {
        this.remove(Entity.RemovalReason.KILLED);

        if (this.level().isClientSide()) {
            this.tickClientDeath();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tickClientDeath() {
        this.flareSprite.ifPresent(EntityFX::requestRemoval);

        List<Vector3> offsets = new ArrayList<>();
        Vector3 pos = new Vector3(this).addY(this.getBbHeight() / 2F);
        for (int i = 0; i < 30; i++) {
            offsets.add(VectorUtil.withRandomOffset(pos, random, 0.8F));
        }
        offsets.forEach(vec -> {
            VFXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(vec)
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .color(FXColorFunction.constant(ColorsAS.ENTITY_FLARE_EFFECT_COLOR))
                    .setScale(0.1F + random.nextFloat() * 0.2F)
                    .setMaxAge(30 + random.nextInt(40));
            if (random.nextBoolean()) {
                p.color(FXColorFunction.constant(ColorWrapper.WHITE));
            }
        });

        for (int i = 0; i < 10; i++) {
            Vector3 at = VectorUtil.withRandomOffset(pos, random, 0.15F);

            VFXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at)
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .color(FXColorFunction.constant(ColorsAS.ENTITY_FLARE_EFFECT_COLOR))
                    .setScale(0.25F + random.nextFloat() * 0.25F)
                    .setMotion(Vector3.random(random).multiply(0.05F))
                    .setMaxAge(40 + random.nextInt(40));
            if (random.nextBoolean()) {
                p.color(FXColorFunction.constant(ColorWrapper.WHITE));
            }
        }
    }

    @Override
    public void push(Entity entity) {
        if (entity instanceof Player) return;
        super.push(entity);
    }

    @Override
    protected void doPush(Entity entity) {
        if (entity instanceof Player) return;
        super.doPush(entity);
    }

    @Override
    protected void actuallyHurt(DamageSource damageSource, float damageAmount) {
        super.actuallyHurt(damageSource, damageAmount);
        this.setHealth(0F);
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType reason) {
        return false;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.EMPTY;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.EMPTY;
    }

    @Override
    protected SoundEvent getSwimHighSpeedSplashSound() {
        return SoundEvents.EMPTY;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (source.is(DamageTypeTags.IS_FIRE)) return true;
        return super.isInvulnerableTo(source);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        compound.putInt("AS_MaxAge", this.maxEntityAge);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        this.maxEntityAge = compound.getInt("AS_MaxAge");
    }
}
