/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.*;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.visual.type.ShootingStarExplosion;
import hellfirepvp.astralsorcery.common.lib.EntitiesAS;
import hellfirepvp.astralsorcery.common.lib.LootTablesAS;
import hellfirepvp.astralsorcery.common.util.ChunkUtil;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.level.DayTimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.EventHooks;
import org.joml.Vector3f;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EntityShootingStar
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EntityShootingStar extends ThrowableProjectile {

    private static final EntityDataAccessor<Vector3f> SHOOT_VEC = SynchedEntityData.defineId(EntityShootingStar.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Long> SEED = SynchedEntityData.defineId(EntityShootingStar.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Long> LAST_UPDATE = SynchedEntityData.defineId(EntityShootingStar.class, EntityDataSerializers.LONG);

    // Unserialized, will just delete the entity if captured
    private boolean pendingRemoval = true;

    protected EntityShootingStar(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    protected EntityShootingStar(EntityType<? extends ThrowableProjectile> entityType, double x, double y, double z, Level level) {
        super(entityType, x, y, z, level);
    }

    protected EntityShootingStar(EntityType<? extends ThrowableProjectile> entityType, LivingEntity shooter, Level level) {
        super(entityType, shooter, level);
    }

    public static EntityType.EntityFactory<EntityShootingStar> factory() {
        return EntityShootingStar::new;
    }

    public static EntityShootingStar create(Level level, Vector3 pos, Vector3 shot) {
        EntityShootingStar shootingStar = EntitiesAS.SHOOTING_STAR.get().create(level);
        shootingStar.setPos(pos.toVector3d());
        shootingStar.pendingRemoval = false;
        shootingStar.entityData.set(SHOOT_VEC, shot.toVector3d().toVector3f());
        shootingStar.entityData.set(SEED, level.random.nextLong());
        shootingStar.entityData.set(LAST_UPDATE, level.getGameTime());
        shootingStar.setDeltaMovement(shootingStar.getAdjustedMovement().toVector3d());
        return shootingStar;
    }

    public static float getYLevelCutoff(LevelReader level) {
        return level.getMaxBuildHeight() + 300;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SHOOT_VEC, new Vector3f());
        builder.define(SEED, 0L);
        builder.define(LAST_UPDATE, 0L);
    }

    protected Vector3 getAdjustedMovement() {
        Vector3 move = new Vector3(this.entityData.get(SHOOT_VEC));
        if (this.getY() >= getYLevelCutoff(this.level())) {
            move.setY(-0.14F);
        } else {
            move.setY(-0.9F * (1F - (this.getY() / (getYLevelCutoff(this.level()) + 200))));
        }
        return move;
    }

    @Override
    public void tick() {
        super.tick();

        Level level = this.level();
        long lastUpdate = this.entityData.get(LAST_UPDATE);

        if (!level.isClientSide()) {
            if (this.pendingRemoval || !DayTimeHelper.isNight(level) || level.getGameTime() - lastUpdate >= 20) {
                this.discard();
                return;
            }

            this.entityData.set(LAST_UPDATE, level.getGameTime());

            if (this.isInFluidType()) {
                BlockPos hit = this.blockPosition();
                while (level.isInWorldBounds(hit) && !level.getFluidState(hit).isEmpty()) {
                    hit = hit.above();
                }
                HitResult result = new BlockHitResult(new Vec3(this.getX(), hit.getY() + 0.1F, this.getZ()), Direction.UP, hit, false);
                if (!EventHooks.onProjectileImpact(this, result)) {
                    this.onHit(result);
                }
            }
        }

        this.setDeltaMovement(this.getAdjustedMovement().toVector3d());

        if (level.isClientSide()) {
            if (!DayTimeHelper.isNight(level) || level.getGameTime() - lastUpdate >= 20) {
                this.discard();
                return;
            }

            this.spawnEffects();
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void spawnEffects() {
        Vector3 dir = new Vector3(this.entityData.get(SHOOT_VEC));
        Vector3 adjustedDir = this.getAdjustedMovement();
        float maxPositionDist = 128F;
        float maxPositionDistSq = maxPositionDist * maxPositionDist;
        ColorWrapper color = this.getSeededColor();
        FXColorFunction<?> colorFn = FXColorFunction.constant(color);

        //Move render closer to the player
        FXRenderOffsetFunction<?> offsetFn = (fx, renderPosition, pTicks) -> {
            Entity view = Minecraft.getInstance().cameraEntity;
            if (view == null) return renderPosition;
            float eyeHeight = view.getDimensions(Pose.STANDING).eyeHeight();
            Vector3 viewPos = new Vector3(view).addY(eyeHeight);

            Vector3 toView = renderPosition.copy().subtract(viewPos);
            if (toView.lengthSquared() <= maxPositionDistSq) {
                return renderPosition;
            }
            return viewPos.copy().add(toView.copy().normalize().multiply(maxPositionDist));
        };
        FXScaleFunction<?> scaleFn = FXScaleFunction.SHRINK.andThen((fx, scale, pTicks) -> {
            Entity view = Minecraft.getInstance().cameraEntity;
            if (view == null) return scale;
            float eyeHeight = view.getDimensions(Pose.STANDING).eyeHeight();
            Vector3 viewPos = new Vector3(view).addY(eyeHeight);

            Vector3 toView = fx.getPos().subtract(viewPos);
            if (toView.lengthSquared() <= maxPositionDistSq) {
                return scale;
            }
            float dstMult = maxPositionDist / (float) toView.length();
            float scalePart = scale * 0.25F;
            return scalePart + ((dstMult * scale) - scalePart);
        });

        for (int i = 0; i < 6; i++) {
            if (this.random.nextFloat() >= 0.75F) continue;

            Vector3 motion = adjustedDir.copy().multiply(-0.1F - random.nextFloat() * 0.5F)
                    .addX(random.nextFloat() * 0.006 * (random.nextBoolean() ? 1 : -1))
                    .addZ(random.nextFloat() * 0.006 * (random.nextBoolean() ? 1 : -1))
                    .multiply(1.5F);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(VectorUtil.withRandomOffset(new Vector3(this), random, 0.5F))
                    .color(FXColorFunction.WHITE)
                    .renderOffset(offsetFn)
                    .setAlpha(0.8F)
                    .alpha(FXAlphaFunction.fadeIn(10).andThen(FXAlphaFunction.FADE_OUT))
                    .setScale(1F + random.nextFloat() * 0.4F)
                    .scale(scaleFn)
                    .setMotion(motion)
                    .persistence(FXPersistenceFunction.ALWAYS_PERSIST)
                    .setMaxAge(80 + random.nextInt(40));
        }

        if (!this.level().isOutsideBuildHeight(this.blockPosition())) {
            Vector3 offset = adjustedDir.copy().perpendicular().normalize();
            for (int i = 0; i < 2; i++) {
                Vector3 motion = offset.copy().rotate(random.nextFloat() * Mth.PI * 2, adjustedDir).normalize()
                                .multiply(0.8F + random.nextFloat() * 0.4F);
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(new Vector3(this))
                        .color(colorFn)
                        .alpha(FXAlphaFunction.fadeIn(10).andThen(FXAlphaFunction.FADE_OUT))
                        .setAlpha(0.4F)
                        .setScale(0.6F + random.nextFloat() * 0.4F)
                        .setMotion(motion)
                        .motion(FXMotionFunction.decelerate(0.14F))
                        .setGravity(adjustedDir.copy().normalize().multiply(-1).multiply(0.06F))
                        .setMaxAge(20 + random.nextInt(10));
            }
        }

        float scale = 6F + random.nextFloat() * 4F;
        int age = 4 + random.nextInt(2);

        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(new Vector3(this))
                .setRoll(random.nextFloat() * 2 * Mth.PI)
                .alpha(FXAlphaFunction.FADE_OUT)
                .setScale(scale)
                .scale(scaleFn)
                .color(colorFn)
                .renderOffset(offsetFn)
                .persistence(FXPersistenceFunction.ALWAYS_PERSIST)
                .setMaxAge(age);
        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(new Vector3(this))
                .alpha(FXAlphaFunction.FADE_OUT)
                .setScale(scale * 0.6F)
                .scale(scaleFn)
                .renderOffset(offsetFn)
                .persistence(FXPersistenceFunction.ALWAYS_PERSIST)
                .setMaxAge(Math.round(age * 1.5F));
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (result.getType() != HitResult.Type.MISS) {
            this.impact(result.getLocation());
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (result.getType() != HitResult.Type.MISS) {
            this.impact(result.getLocation());
        }
    }

    protected void impact(Vec3 vec) {
        if (this.pendingRemoval) return;
        this.discard();

        Level level = this.level();
        BlockPos pos = BlockPos.containing(vec);
        if (level.isClientSide() || !ChunkUtil.isChunkLoaded(level, pos)) return;
        if (!(level instanceof ServerLevel sLevel)) return;

        Vector3 at = new Vector3(vec);
        ShootingStarExplosion.at(at, this.getSeededColor()).sendToNearby(sLevel);

        LootTable table = sLevel.getServer().reloadableRegistries().getLootTable(LootTablesAS.SHOOTING_STAR);
        LootParams ctx = new LootParams.Builder(sLevel)
                .withParameter(LootContextParams.THIS_ENTITY, this)
                .withParameter(LootContextParams.ORIGIN, this.position())
                .create(LootContextParamSets.EMPTY);
        table.getRandomItems(ctx).forEach(stack -> {
            ItemUtil.dropItemNaturally(sLevel, at.getX(), at.getY(), at.getZ(), stack);
        });
    }

    public long getSeed() {
        return this.entityData.get(SEED);
    }

    public ColorWrapper getSeededColor() {
        RandomSource effectRand = RandomSource.create(this.getSeed());
        return ColorWrapper.ofHSB(effectRand.nextFloat(), 1F, 1F);
    }

    @Override
    protected double getDefaultGravity() {
        return 0;
    }
}
