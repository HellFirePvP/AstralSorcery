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
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.EntitiesAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.util.BlockUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EntityIlluminationSpark
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EntityIlluminationSpark extends ThrowableProjectile {

    public EntityIlluminationSpark(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public EntityIlluminationSpark(LivingEntity shooter, Level level) {
        this(EntitiesAS.ILLUMINATION_SPARK.get(), shooter, level);
    }

    public EntityIlluminationSpark(double x, double y, double z, Level level) {
        this(EntitiesAS.ILLUMINATION_SPARK.get(), x, y, z, level);
    }

    protected EntityIlluminationSpark(EntityType<? extends ThrowableProjectile> entityType, LivingEntity shooter, Level level) {
        super(entityType, shooter, level);
        this.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0F, 0.7F, 0F);
    }

    protected EntityIlluminationSpark(EntityType<? extends ThrowableProjectile> entityType, double x, double y, double z, Level level) {
        super(entityType, x, y, z, level);
    }

    public static EntityType.EntityFactory<EntityIlluminationSpark> factory() {
        return EntityIlluminationSpark::new;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public boolean canUsePortal(boolean allowPassengers) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.clientTick();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void clientTick() {
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
            case 0 -> ColorsAS.ILLUMINATION_POWDER_1;
            case 1 -> ColorsAS.ILLUMINATION_POWDER_2;
            case 2 -> ColorsAS.ILLUMINATION_POWDER_3;
            default -> ColorWrapper.WHITE;
        };
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!(this.level() instanceof ServerLevel sLevel)) return;

        BlockPos placePos = result.getBlockPos();
        if (!BlockUtil.isReplaceable(sLevel, placePos)) {
            placePos = placePos.relative(result.getDirection());
        }
        if (!BlockUtil.isReplaceable(sLevel, placePos)) {
            this.remove(RemovalReason.KILLED);
            return;
        }

        if (sLevel.isInWorldBounds(placePos) && sLevel.getWorldBorder().isWithinBounds(placePos)) {
            if (this.getOwner() instanceof ServerPlayer sPlayer) {
                if (!sPlayer.mayInteract(sLevel, placePos) || EventHooks.onBlockPlace(sPlayer, BlockSnapshot.create(sLevel.dimension(), sLevel, placePos), result.getDirection())) {
                    this.remove(RemovalReason.KILLED);
                    return;
                }
            }
            sLevel.setBlockAndUpdate(placePos, BlocksAS.FLARE_LIGHT.get().defaultBlockState());
        }
        this.remove(RemovalReason.KILLED);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        this.remove(RemovalReason.KILLED);
    }
}
