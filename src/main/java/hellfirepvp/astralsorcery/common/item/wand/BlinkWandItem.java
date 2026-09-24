/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.vfx.VFXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.component.IdentifierComponent;
import hellfirepvp.astralsorcery.common.component.IntegerModeComponent;
import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.event.helper.DamageCancellingHelper;
import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.lumen.ILumenHandler;
import hellfirepvp.astralsorcery.common.util.*;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BlinkWandItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class BlinkWandItem extends ItemCustom {

    public BlinkWandItem() {
        super(new Properties()
                .component(DataComponentsAS.MODE, IntegerModeComponent.ZERO)
                .component(DataComponentsAS.STORED_LUMEN, StoredLumenComponent.EMPTY
                        .updateProperties(properties -> properties.allowFill().allowAccept(LumenAS.VICIO)))
                .stacksTo(1));
    }

    @Override
    public void fillCreativeTab(Consumer<ItemStack> tabItems) {
        tabItems.accept(new ItemStack(this));

        ItemStack lumenFilled = new ItemStack(this);
        LumenUtil.Storage.setStoredLumen(lumenFilled, LumenAS.VICIO.stack(StoredLumenComponent.DEFAULT_CAPACITY));
        IdentifierComponent.createIdentifierIfNotExists(lumenFilled);
        tabItems.accept(lumenFilled);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(getBlinkMode(stack).getDisplay().copy().withStyle(ChatFormatting.GOLD));
    }

    public static boolean hasLumenBoost(ItemStack stack, int amount) {
        return LumenUtil.drainItem(stack, LumenAS.VICIO.stack(amount), ILumenHandler.Action.SIMULATE).getAmount() >= amount;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            BlinkMode current = getBlinkMode(stack);
            BlinkMode next = current.next();
            setBlinkMode(stack, next);
            player.displayClientMessage(next.getDisplay(), true);
            return InteractionResultHolder.success(stack);
        } else if (!player.getCooldowns().isOnCooldown(this)) {
            player.startUsingItem(hand);
        }
        return InteractionResultHolder.consume(stack);
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
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        getBlinkMode(stack).onRelease(stack, livingEntity, timeCharged);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide()) {
            float perc = 0.2F + Math.min(1F, Math.min(50, stack.getUseDuration(livingEntity) - remainingUseDuration) / 50F) * 0.8F;
            this.playParticles(stack, livingEntity, perc);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playParticles(ItemStack stack, LivingEntity entity, float perc) {
        if (!(entity instanceof Player player)) return;
        if (player.getCooldowns().isOnCooldown(this)) return;

        RandomSource rand = RandomSource.create();
        switch (getBlinkMode(stack)) {
            case LAUNCH -> this.playLaunchParticles(entity, perc, rand);
            case TELEPORT -> this.playTeleportParticles(stack, entity, perc);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playLaunchParticles(LivingEntity entity, float usagePercent, RandomSource rand) {
        Vector3 look = new Vector3(entity.getViewVector(1F)).normalize().multiply(20);
        Vector3 pos = new Vector3(entity).addY(entity.getEyeHeight());
        Vector3 motion = look.copy().normalize().multiply(-0.8F + rand.nextFloat() * -0.5F);
        Vector3 perp = look.copy().perpendicular().normalize();

        for (int i = 0; i < Math.round(usagePercent * 6); i++) {
            float dst = i == 0 ? rand.nextFloat() * 0.4F : 0.2F + rand.nextFloat() * 0.4F;
            float speed = i == 0 ? 0.005F : 0.5F + rand.nextFloat() * 0.5F;
            float angleDeg = rand.nextFloat() * 360F;

            Vector3 angle = perp.copy().rotate(angleDeg, look).normalize();
            Vector3 at = pos.copy()
                    .add(look.copy().multiply(0.7F + rand.nextFloat() * 0.3F))
                    .add(angle.copy().multiply(dst));
            Vector3 mot = motion.copy().add(angle.copy().multiply(0.1F + rand.nextFloat() * 0.15F)).multiply(speed);

            VFXFacingParticle particle = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at)
                    .color(FXColorFunction.constant(ColorsAS.BLINK_WAND_LAUNCH))
                    .setScale(0.3F + rand.nextFloat() * 0.4F)
                    .setAlpha(usagePercent)
                    .setMotion(mot)
                    .setMaxAge(20 + rand.nextInt(15));
            if (rand.nextBoolean()) {
                particle.color(FXColorFunction.WHITE);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playTeleportParticles(ItemStack stack, LivingEntity entity, float usagePercent) {
        float dist = 30F;
        if (hasLumenBoost(stack, 40)) dist *= 2F;

        Vector3 origin = new Vector3(entity).addY(entity.getEyeHeight() * 0.8F);
        BlockHitResult result = RayTraceUtil.clip(entity.level(), RayTraceUtil.getEntityViewContext(entity, dist));

        Vec3 target = result.getLocation();
        if (origin.distance(target) < 5) return;

        List<Vector3> line = VectorUtil.iteratePoints(origin, new Vector3(target), 0.2F);
        if (line.isEmpty()) return;
        Vector3 last = line.getLast();

        line.forEach(point -> {
            if (point == last || rand.nextInt(5) == 0) {
                FXColorFunction<?> color = FXColorFunction.constant(ColorsAS.BLINK_WAND_TELEPORT_LINE);

                float scale = 0.15F + rand.nextFloat() * 0.1F;
                float motion = rand.nextFloat() * 0.01F;
                int age = 15 + rand.nextInt(10);

                if (point == last) {
                    scale *= 5F;
                    motion *= 2F;
                    age = Math.round(age * 1.6F);

                    if (result.getType() == HitResult.Type.MISS) {
                        color = FXColorFunction.constant(ColorsAS.BLINK_WAND_TELEPORT_MISS);
                    } else {
                        color = FXColorFunction.constant(ColorsAS.BLINK_WAND_TELEPORT_HIT);
                    }
                }

                if (rand.nextInt(3) == 0) color = FXColorFunction.WHITE;

                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(point)
                        .color(color)
                        .alpha(FXAlphaFunction.FADE_OUT)
                        .setAlpha(usagePercent)
                        .setScale(scale)
                        .setMotion(Vector3.random(rand).normalize().multiply(motion))
                        .setMaxAge(age);
            }
        });
    }

    public static BlinkMode getBlinkMode(ItemStack stack) {
        return stack.getOrDefault(DataComponentsAS.MODE, IntegerModeComponent.ZERO).getMode(BlinkMode.class);
    }

    public static void setBlinkMode(ItemStack stack, BlinkMode mode) {
        stack.set(DataComponentsAS.MODE, new IntegerModeComponent(mode.ordinal()));
    }

    public enum BlinkMode {

        LAUNCH {
            @Override
            public void onRelease(ItemStack stack, LivingEntity entity, int usageTimeLeft) {
                boolean usesLumen = hasLumenBoost(stack, 20);

                float multiplier = 0.6F;
                if (entity.isFallFlying()) multiplier *= 2F;
                if (usesLumen) multiplier *= 1.5F;

                float strength = 0.2F + Math.min(1F, Math.min(50, stack.getUseDuration(entity) - usageTimeLeft) / 50F) * multiplier;
                if (strength < 0.3F) return;

                Vector3 motion = new Vector3(entity.getViewVector(1F)).normalize().multiply(strength * 3F);
                if (motion.getY() > 0) {
                    motion.setY(Mth.clamp(motion.getY() + (0.2F * strength), 0.2F * strength, Float.MAX_VALUE));
                }

                entity.setDeltaMovement(motion.toVector3d());
                entity.fallDistance = 0F;

                if (!entity.isFallFlying() && entity instanceof ServerPlayer sPlayer) {
                    DamageCancellingHelper.preventNextDamage(sPlayer, DamageTypes.FALL);
                }
                if (entity instanceof Player player) {
                    player.getCooldowns().addCooldown(stack.getItem(), 80);

                    if (usesLumen && !player.isCreative()) {
                        LumenUtil.drainItem(stack, LumenAS.VICIO.stack(20), ILumenHandler.Action.EXECUTE);
                    }
                }
            }
        },
        TELEPORT {
            @Override
            public void onRelease(ItemStack stack, LivingEntity entity, int usageTimeLeft) {
                boolean usesLumen = hasLumenBoost(stack, 40);

                float maxDistance = 30F;
                if (usesLumen) maxDistance *= 2F;

                Vector3 origin = new Vector3(entity).addY(0.5F);
                BlockHitResult result = RayTraceUtil.clip(entity.level(), RayTraceUtil.getEntityViewContext(entity, maxDistance));

                Vec3 target = result.getLocation();
                if (origin.distance(target) < 5) return;

                if (result.getType() == HitResult.Type.BLOCK) {
                    target = target.add(Vec3.atLowerCornerOf(result.getDirection().getNormal()));
                }
                EntityUtil.transferEntity(entity, new Vector3(target));
                if (entity instanceof Player player) {
                    player.getCooldowns().addCooldown(stack.getItem(), 80);

                    if (usesLumen && !player.isCreative()) {
                        LumenUtil.drainItem(stack, LumenAS.VICIO.stack(40), ILumenHandler.Action.EXECUTE);
                    }
                }
            }
        };

        public MutableComponent getName() {
            return Component.translatable("astralsorcery.misc.blink.mode." + this.name().toLowerCase(Locale.ROOT));
        }

        public MutableComponent getDisplay() {
            return Component.translatable("astralsorcery.misc.blink.mode", this.getName());
        }

        @Nonnull
        private BlinkMode next() {
            int next = (this.ordinal() + 1) % values().length;
            return MiscUtil.getEnumEntry(BlinkMode.class, next);
        }

        public abstract void onRelease(ItemStack stack, LivingEntity entity, int usageTimeLeft);
    }
}
