/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.vfx.VFXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.lib.EntitiesAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.util.*;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EntityVividSpark
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EntityVividSpark extends ThrowableProjectile {

    private static final AABB NO_DUPE_BOX = new AABB(0, 0, 0, 1, 1, 1).inflate(4);

    private static final EntityDataAccessor<Boolean> GROWING = SynchedEntityData.defineId(EntityVividSpark.class, EntityDataSerializers.BOOLEAN);
    private int ticksGrowing = 0;

    public EntityVividSpark(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public EntityVividSpark(LivingEntity shooter, Level level) {
        this(EntitiesAS.VIVID_SPARK.get(), shooter, level);
    }

    public EntityVividSpark(double x, double y, double z, Level level) {
        this(EntitiesAS.VIVID_SPARK.get(), x, y, z, level);
    }

    protected EntityVividSpark(EntityType<? extends ThrowableProjectile> entityType, LivingEntity shooter, Level level) {
        super(entityType, shooter, level);
        this.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0F, 0.7F, 0F);
    }

    protected EntityVividSpark(EntityType<? extends ThrowableProjectile> entityType, double x, double y, double z, Level level) {
        super(entityType, x, y, z, level);
    }

    public static EntityType.EntityFactory<EntityVividSpark> factory() {
        return EntityVividSpark::new;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(GROWING, false);
    }

    @Override
    public boolean canUsePortal(boolean allowPassengers) {
        return false;
    }

    public void setGrowing() {
        this.getEntityData().set(GROWING, true);
    }

    public boolean isGrowing() {
        return this.getEntityData().get(GROWING);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isGrowing()) {
            this.setDeltaMovement(Vec3.ZERO);
        }

        Level level = this.level();
        if (level.isClientSide()) {
            this.clientTick();
        } else if (level instanceof ServerLevel sLevel) {
            this.provideEffects(sLevel);
            if (this.isGrowing()) {
                this.ticksGrowing++;
                this.removeDuplicates(sLevel);
                this.growthCycle(sLevel);

                if (this.ticksGrowing > 200) {
                    this.remove(RemovalReason.KILLED);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void clientTick() {
        if (this.isGrowing()) {
            this.spawnSpawningParticles();
        } else {
            this.spawnFlyingParticles();
        }
    }

    private void spawnSpawningParticles() {
        for (int i = 0; i < 3; i++) {
            Vector3 pos = VectorUtil.withRandomOffset(new Vector3(this), this.random, 1F + this.random.nextFloat() * 5F);
            pos.setY(this.getBlockY());
            float scale = 0.4F + random.nextFloat() * 0.5F;
            float gravity = 0.0008F + this.random.nextFloat() * 0.0003F;
            int age = 30 + this.random.nextInt(20);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .setScale(scale)
                    .setAlpha(0.8F)
                    .color(FXColorFunction.constant(this.randomColor()))
                    .setGravity(Vector3.y(gravity))
                    .setMaxAge(age);
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .setScale(scale * 0.3F)
                    .setAlpha(0.8F)
                    .color(FXColorFunction.WHITE)
                    .setGravity(Vector3.y(gravity))
                    .setMaxAge(age);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnFlyingParticles() {
        for (int i = 0; i < 5; i++) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(new Vector3(this))
                    .setScale(0.25F)
                    .color(FXColorFunction.constant(this.randomColor()))
                    .setGravity(Vector3.y(0.0003F))
                    .setMotion(VectorUtil.withRandomOffset(new Vector3(), this.random, 0.04F));
        }

        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(new Vector3(this))
                .setScale(0.5F)
                .alpha(FXAlphaFunction.FADE_OUT.andThen(FXAlphaFunction.fadeIn(10)))
                .color(FXColorFunction.constant(this.randomColor()))
                .setGravity(Vector3.y(0.0003F));

        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(new Vector3(this).add(this.getDeltaMovement().multiply(0.5F, 0.5F, 0.5F)))
                .setScale(0.5F)
                .alpha(FXAlphaFunction.FADE_OUT.andThen(FXAlphaFunction.fadeIn(10)))
                .color(FXColorFunction.constant(this.randomColor()))
                .setGravity(Vector3.y(0.0003F));
    }

    private ColorWrapper randomColor() {
        return switch (this.random.nextInt(3)) {
            case 0 -> ColorsAS.VIVID_POWDER_1;
            case 1 -> ColorsAS.VIVID_POWDER_2;
            case 2 -> ColorsAS.VIVID_POWDER_3;
            default -> ColorWrapper.WHITE;
        };
    }

    private void provideEffects(ServerLevel sLevel) {
        if (this.tickCount % 4 != 0) return;

        if (this.isGrowing()) {
            AABB box = new AABB(this.blockPosition()).inflate(4);
            sLevel.getNearbyPlayers(TargetingConditions.forNonCombat(), null, box).forEach(player -> {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0, true, false, true));
            });
        }
    }

    private void removeDuplicates(ServerLevel sLevel) {
        sLevel.getEntitiesOfClass(EntityVividSpark.class, NO_DUPE_BOX.move(this.position())).forEach(spark -> {
            if (this.equals(spark)) {
                return;
            }
            if (!spark.isAlive() || !spark.isGrowing()) {
                return;
            }
            spark.remove(RemovalReason.KILLED);
        });
    }

    private void growthCycle(ServerLevel sLevel) {
        if (this.tickCount % 8 != 0) return;
        Vec3i offset = new Vec3i(5, 1, 5);
        BlockPos.betweenClosed(this.blockPosition().subtract(offset), this.blockPosition().offset(offset)).forEach(at -> {
            if (this.random.nextInt(20) != 0) return;
            CropUtil.wrapPlant(sLevel, at).ifPresent(plant -> {
                plant.tryGrow(sLevel);
                if (plant.canHarvest(sLevel)) {
                    plant.getDrops(sLevel, 0).forEach(stack -> {
                        Block.popResource(sLevel, at, stack);
                    });
                    plant.replant(sLevel);
                }
            });
        });
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.startGrowing(result.getBlockPos().relative(result.getDirection()).getCenter());
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        this.startGrowing(result.getLocation());
    }

    public void startGrowing(Vec3 pos) {
        this.setGrowing();
        this.setPos(pos.x, pos.y, pos.z);
        this.setDeltaMovement(Vec3.ZERO);
    }
}
