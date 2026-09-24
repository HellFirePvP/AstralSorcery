/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.component.DynamicModifiersComponent;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.event.helper.DamageCancellingHelper;
import hellfirepvp.astralsorcery.common.event.helper.SwordParryHelper;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.PerksAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.perk.modifier.DynamicAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.type.base.ModifierType;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.*;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.level.DayTimeHelper;
import hellfirepvp.astralsorcery.common.visual.type.CelestialStrikeBeamEffect;
import hellfirepvp.astralsorcery.common.visual.type.SwordCrescentWaveEffect;
import hellfirepvp.astralsorcery.common.visual.type.SwordShockwaveEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: IridescentCrystalPickaxeItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class IridescentCrystalSwordItem extends CrystalSwordItem {

    public static final int PARRY_WINDOW_TICKS = 5;

    private static final int CHARGE_TICKS_WAVE = 12;
    private static final int CHARGE_TICKS_FULL = 30;
    private static final int COOLDOWN_STRIKE = 240;
    private static final int COOLDOWN_WAVE = 60;
    private static final float WAVE_RANGE = 5F;
    private static final float STRIKE_RANGE = 20F;
    private static final int SHOCKWAVE_RANGE = 6;

    private static final UUID MODIFIER_ID = UUID.fromString("dd448b76-5ad5-4b68-82db-330ec7bdac02");
    private static final DynamicAttributeModifier BASE_CRIT_MODIFIER =
            new DynamicAttributeModifier(MODIFIER_ID.toString(), PerksAS.AttributeTypes.CRITICAL_HIT_CHANCE, ModifierType.ADDITION, 0.1F);

    private static final RandomSource rand = RandomSource.create();

    public IridescentCrystalSwordItem() {
        super(ItemsAS.CRYSTAL_TOOL_TIER, new Properties()
                .setNoRepair()
                .component(DataComponentsAS.DYNAMIC_MODIFIERS, new DynamicModifiersComponent(List.of(BASE_CRIT_MODIFIER)))
                .attributes(swordAttributes()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (!player.getCooldowns().isOnCooldown(this)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(held);
        }
        return InteractionResultHolder.pass(held);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        int chargeTicks = this.getUseDuration(stack, livingEntity) - remainingUseDuration;
        if (level.isClientSide()) {
            this.playChargeParticles(livingEntity, Mth.clamp((float) chargeTicks / CHARGE_TICKS_FULL, 0F, 1F));
        } else if (chargeTicks == 1) {
            ServerSoundHelper.playSoundAround(SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, level, new Vector3(livingEntity), 0.5F, 1.6F);
        } else if (chargeTicks == CHARGE_TICKS_FULL) {
            ServerSoundHelper.playSoundAround(SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, level, new Vector3(livingEntity), 0.7F, 2F);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (!(livingEntity instanceof ServerPlayer sPlayer) || !(level instanceof ServerLevel sLevel)) {
            return;
        }
        boolean empowered = SwordParryHelper.consumeEmpoweredCharge(sPlayer);
        if (MiscUtil.isPlayerFake(sPlayer) || sPlayer.getCooldowns().isOnCooldown(this)) {
            return;
        }
        int chargeTicks = this.getUseDuration(stack, livingEntity) - timeCharged;
        if (empowered || chargeTicks >= CHARGE_TICKS_FULL) {
            PlayerProgress progress = ResearchManager.getProgress(sPlayer, SidedHelper.getSide(sPlayer));
            this.playFullStrike(sPlayer, sLevel, progress.getAttunedConstellation());
        } else if (chargeTicks >= CHARGE_TICKS_WAVE) {
            this.playCrescentWave(sPlayer, sLevel);
        }
    }

    private void playFullStrike(ServerPlayer sPlayer, ServerLevel sLevel, Optional<BaseConstellation> attuned) {
        Vector3 origin = Vector3.atEntityEyes(sPlayer);
        BlockHitResult result = RayTraceUtil.clip(sLevel, RayTraceUtil.getEntityViewContext(sPlayer, STRIKE_RANGE,
                1F, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE));
        Vector3 at = result.getType() == HitResult.Type.MISS
                ? origin.copy().add(lookVec(sPlayer).multiply(STRIKE_RANGE))
                : new Vector3(result.getLocation());

        float multiplier = getSkyStrengthMultiplier(sLevel, at.toBlockPos());
        float allyHealing = 0F;

        if (isAttunedTo(attuned, ConstellationsAS.DISCIDIA)) {
            multiplier *= 1.5F;
        } else if (isAttunedTo(attuned, ConstellationsAS.AEVITAS)) {
            allyHealing = 8F;
            sPlayer.heal(4F);
        } else if (isAttunedTo(attuned, ConstellationsAS.ARMARA)) {
            sPlayer.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 300, 1));
            ServerSoundHelper.playSoundAround(SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, sLevel, origin, 0.8F, 0.6F);
        } else if (isAttunedTo(attuned, ConstellationsAS.VICIO)) {
            Vector3 dash = at.copy().subtract(origin).normalize().multiply(1.6F);
            sPlayer.setDeltaMovement(dash.toVector3d());
            sPlayer.fallDistance = 0F;
            sPlayer.hurtMarked = true;
            DamageCancellingHelper.preventNextDamage(sPlayer, DamageTypes.FALL);
        } else if (isAttunedTo(attuned, ConstellationsAS.EVORSIO)) {
            this.playShockwave(sPlayer, sLevel);
        }

        CelestialStrikeBeamEffect.of(origin.copy().subtract(0, sPlayer.getEyeHeight() / 3F, 0), at).sendToNearby(sLevel);
        CelestialStrike.play(sPlayer, sLevel, at, at, multiplier, allyHealing);
        ServerSoundHelper.playSoundAround(SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, sLevel, at, 0.7F, 1.4F);
        this.applyAbilityCooldown(sPlayer, COOLDOWN_STRIKE);
    }

    private void playCrescentWave(ServerPlayer sPlayer, ServerLevel sLevel) {
        Vector3 origin = Vector3.atEntityEyes(sPlayer);
        Vector3 look = lookVec(sPlayer);
        DamageSource source = sLevel.damageSources().playerAttack(sPlayer);
        float dmg = (float) sPlayer.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2F
                * getSkyStrengthMultiplier(sLevel, sPlayer.blockPosition());

        for (LivingEntity target : getTargetsAround(sPlayer, sLevel, WAVE_RANGE)) {
            Vector3 dir = Vector3.atCenter(target).subtract(origin);
            if (dir.length() > WAVE_RANGE || dir.normalize().dot(look) < 0.5D) {
                continue;
            }
            if (DamageUtil.attackEntity(target, source, dmg)) {
                knockbackFrom(target, sPlayer, 0.5F);
            }
        }

        ServerSoundHelper.playSoundAround(SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, sLevel, origin, 1F, 0.8F);
        SwordCrescentWaveEffect.at(origin, look).sendToNearby(sLevel, sPlayer.blockPosition());
        this.applyAbilityCooldown(sPlayer, COOLDOWN_WAVE);
    }

    private void playShockwave(ServerPlayer sPlayer, ServerLevel sLevel) {
        for (LivingEntity target : getTargetsAround(sPlayer, sLevel, SHOCKWAVE_RANGE)) {
            knockbackFrom(target, sPlayer, 1F);
        }

        Vector3 center = new Vector3(sPlayer);
        ServerSoundHelper.playSoundAround(SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, sLevel, center, 0.6F, 0.7F);
        SwordShockwaveEffect.at(center, SHOCKWAVE_RANGE).sendToNearby(sLevel, sPlayer.blockPosition());
    }

    private void applyAbilityCooldown(ServerPlayer sPlayer, int baseTicks) {
        sPlayer.getCooldowns().addCooldown(this, DayTimeHelper.isNight(sPlayer.level()) ? baseTicks / 2 : baseTicks);
    }

    private static Vector3 lookVec(LivingEntity entity) {
        return new Vector3(entity.getViewVector(1F)).normalize();
    }

    private static boolean isAttunedTo(Optional<BaseConstellation> attuned, Supplier<BaseConstellation> constellation) {
        return attuned.map(constellation.get()::equals).orElse(false);
    }

    private static List<LivingEntity> getTargetsAround(ServerPlayer sPlayer, ServerLevel sLevel, float range) {
        return sLevel.getEntitiesOfClass(LivingEntity.class, sPlayer.getBoundingBox().inflate(range),
                target -> target.isAlive() && target != sPlayer && CelestialStrike.isValidTarget(target, sPlayer));
    }

    private static void knockbackFrom(LivingEntity target, ServerPlayer sPlayer, float strength) {
        target.knockback(strength, sPlayer.getX() - target.getX(), sPlayer.getZ() - target.getZ());
        target.hurtMarked = true;
    }

    private static float getSkyStrengthMultiplier(Level level, BlockPos pos) {
        float multiplier = 0.4F + 0.6F * DayTimeHelper.getCurrentDaytimeDistribution(level);
        if (!MiscUtil.canSeeSky(level, pos, true, false)) {
            multiplier *= 0.6F;
        }
        return multiplier;
    }

    @OnlyIn(Dist.CLIENT)
    private void playChargeParticles(LivingEntity entity, float progress) {
        Vector3 center = new Vector3(entity).addY(entity.getBbHeight() * 0.6F);
        int amount = 1 + Math.round(progress * 4F);
        for (int i = 0; i < amount; i++) {
            Vector3 pos = center.copy().add(Vector3.random(rand).normalize().multiply(1.2F + rand.nextFloat() * 1.5F));
            Vector3 motion = center.copy().subtract(pos).normalize().multiply(0.08F + progress * 0.12F);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .color(rand.nextBoolean() ? FXColorFunction.WHITE : FXColorFunction.constant(ColorsAS.CELESTIAL_STRIKE_LIGHT))
                    .setScale(0.2F + rand.nextFloat() * 0.3F)
                    .setAlpha(0.4F + 0.6F * progress)
                    .setMotion(motion)
                    .setMaxAge(10 + rand.nextInt(10));
        }

        if (progress >= 1F && rand.nextInt(3) == 0) {
            EffectHelper.of(EffectTemplatesAS.LIGHT_BEAM)
                    .spawn(center.copy().addY(0.4F))
                    .setup(center.copy().addY(2.6F), 0.8F, 0.8F)
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .color(FXColorFunction.WHITE)
                    .setAlpha(0.6F)
                    .setMaxAge(15);
        }
    }
}
