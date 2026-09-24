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
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EntityNocturnalSpark
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EntityNocturnalSpark extends ThrowableProjectile {

    private static final AABB NO_DUPE_BOX = new AABB(0, 0, 0, 1, 1, 1).inflate(6);

    private static final EntityDataAccessor<Boolean> SPAWNING = SynchedEntityData.defineId(EntityNocturnalSpark.class, EntityDataSerializers.BOOLEAN);
    private int ticksSpawning = 0;

    public EntityNocturnalSpark(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public EntityNocturnalSpark(LivingEntity shooter, Level level) {
        this(EntitiesAS.NOCTURNAL_SPARK.get(), shooter, level);
    }

    public EntityNocturnalSpark(double x, double y, double z, Level level) {
        this(EntitiesAS.NOCTURNAL_SPARK.get(), x, y, z, level);
    }

    protected EntityNocturnalSpark(EntityType<? extends ThrowableProjectile> entityType, LivingEntity shooter, Level level) {
        super(entityType, shooter, level);
        this.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0F, 0.7F, 0F);
    }

    protected EntityNocturnalSpark(EntityType<? extends ThrowableProjectile> entityType, double x, double y, double z, Level level) {
        super(entityType, x, y, z, level);
    }

    public static EntityType.EntityFactory<EntityNocturnalSpark> factory() {
        return EntityNocturnalSpark::new;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SPAWNING, false);
    }

    @Override
    public boolean canUsePortal(boolean allowPassengers) {
        return false;
    }

    public void setSpawning() {
        this.getEntityData().set(SPAWNING, true);
    }

    public boolean isSpawning() {
        return this.getEntityData().get(SPAWNING);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isSpawning()) {
            this.setDeltaMovement(Vec3.ZERO);
        }

        Level level = this.level();
        if (level.isClientSide()) {
            this.clientTick();
        } else if (level instanceof ServerLevel sLevel) {
            this.breakLights(sLevel);
            if (this.isSpawning()) {
                this.ticksSpawning++;
                this.removeDuplicates(sLevel);
                this.spawnCycle(sLevel);

                if (this.ticksSpawning > 200) {
                    this.remove(RemovalReason.KILLED);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void clientTick() {
        if (this.isSpawning()) {
            this.spawnSpawningParticles();
        } else {
            this.spawnFlyingParticles();
        }
    }

    private void spawnSpawningParticles() {
        for (int i = 0; i < 15; i++) {
            Vector3 pos = VectorUtil.withRandomOffset(new Vector3(this).addY(1), this.random, 1.5F + this.random.nextFloat() * 4F);
            VFXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .setScale(4F)
                    .setAlpha(0.8F)
                    .color(FXColorFunction.constant(ColorWrapper.BLACK));
            if (this.random.nextInt(4) == 0) {
                p.color(FXColorFunction.constant(this.randomColor()));
            }
        }

        Vector3 from = VectorUtil.withRandomOffset(new Vector3(this).addY(1), this.random, 3F);
        Vector3 to = VectorUtil.withRandomOffset(new Vector3(this).addY(1), this.random, 3F);

        EffectHelper.of(EffectTemplatesAS.LIGHTNING)
                .spawn(from)
                .makeDefault(to)
                .setAlpha(0.7F)
                .color(FXColorFunction.constant(this.randomColor()));
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
            case 0 -> ColorsAS.NOCTURNAL_POWDER_1;
            case 1 -> ColorsAS.NOCTURNAL_POWDER_2;
            case 2 -> ColorsAS.NOCTURNAL_POWDER_3;
            default -> ColorWrapper.BLACK;
        };
    }

    private void breakLights(ServerLevel sLevel) {
        if (this.tickCount % 4 != 0) return;

        BlockFinder.findNearbyBlocks(sLevel, this.blockPosition(), 8, (lvl, pos, state) -> {
            return !(state.getBlock() instanceof AirBlock) &&
                    !BlockUtil.isLiquidBlock(state) &&
                    state.getDestroySpeed(sLevel, pos) != -1F &&
                    state.getLightEmission(sLevel, pos) > 2;
        }).forEach(lightPos -> {
            BlockState state = sLevel.getBlockState(lightPos);
            BlockBreakUtil.Result result = BlockBreakUtil.breakBlockWithoutPlayer(sLevel, lightPos, ItemStack.EMPTY, true);
            if (result.isSuccess()) {
                result.dropResultsInWorld(sLevel, state, lightPos, ItemStack.EMPTY);
            }
        });

        if (this.isSpawning()) {
            AABB box = new AABB(this.blockPosition()).inflate(4);
            sLevel.getNearbyPlayers(TargetingConditions.forNonCombat(), null, box).forEach(player -> {
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0, true, false, true));
            });
        }
    }

    private void removeDuplicates(ServerLevel sLevel) {
        sLevel.getEntitiesOfClass(EntityNocturnalSpark.class, NO_DUPE_BOX.move(this.position())).forEach(spark -> {
            if (this.equals(spark)) {
                return;
            }
            if (!spark.isAlive() || !spark.isSpawning()) {
                return;
            }
            spark.remove(RemovalReason.KILLED);
        });
    }

    private void spawnCycle(ServerLevel sLevel) {
        if (this.ticksSpawning % 10 != 0) return;
        if (sLevel.getDifficulty() == Difficulty.PEACEFUL) return;

        BlockPos pos = this.blockPosition().offset(
                this.random.nextInt(3) - 1,
                this.random.nextInt(2) - 1,
                this.random.nextInt(3) - 1);

        Optional<BlockPos> posOpt = MiscUtil.iterateDown(sLevel, pos, p -> {
            BlockState state = sLevel.getBlockState(p);
            return state.blocksMotion() || !state.getFluidState().isEmpty();
        });
        if (posOpt.isEmpty()) return;
        pos = posOpt.get().above();
        if (pos.distSqr(this.blockPosition()) > 18) return;

        EntitySpawnUtil.performNaturalSpawnAt(sLevel, pos, true,
                EntitySpawnUtil.SpawnConditionFlags.C_IGNORE_SPAWN_RULES | EntitySpawnUtil.SpawnConditionFlags.IGNORE_ENTITY_SPAWN_COLLISION);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.startSpawn(result.getBlockPos().relative(result.getDirection()).getCenter());
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        this.startSpawn(result.getLocation());
    }

    public void startSpawn(Vec3 pos) {
        this.setSpawning();
        this.setPos(pos.x, pos.y, pos.z);
        this.setDeltaMovement(Vec3.ZERO);
    }
}
