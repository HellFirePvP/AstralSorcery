/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity.item;

import hellfirepvp.astralsorcery.common.artifact.*;
import hellfirepvp.astralsorcery.common.artifact.condition.data.ArtifactConditionLoader;
import hellfirepvp.astralsorcery.common.artifact.effect.data.ArtifactEffectLoader;
import hellfirepvp.astralsorcery.common.artifact.trigger.ActiveArtifactTrigger;
import hellfirepvp.astralsorcery.common.component.ArtifactComponent;
import hellfirepvp.astralsorcery.common.config.server.ArtifactConfig;
import hellfirepvp.astralsorcery.common.entity.ItemEntityChiselAttackable;
import hellfirepvp.astralsorcery.common.item.ArtifactItem;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import hellfirepvp.astralsorcery.common.util.ServerSoundHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ItemEntityArtifact
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ItemEntityArtifact extends ItemEntityChiselAttackable {

    protected static final EntityDataAccessor<BlockPos> FORCED_MOVE_POS = SynchedEntityData.defineId(ItemEntityArtifact.class, EntityDataSerializers.BLOCK_POS);
    protected static final EntityDataAccessor<Float> FORCED_MOVE_SPEED = SynchedEntityData.defineId(ItemEntityArtifact.class, EntityDataSerializers.FLOAT);

    @Nullable
    private ActiveArtifactTrigger activeTrigger = null;
    private int triggerTimeout = 0;
    private int gracePulseTimeout = 1 * 20;

    public ItemEntityArtifact(EntityType<? extends ItemEntityArtifact> entityType, Level level) {
        super(entityType, level);
        this.init();
    }

    public ItemEntityArtifact(EntityType<? extends ItemEntityArtifact> entityType, Level level, double posX, double posY, double posZ, ItemStack itemStack) {
        super(entityType, level, posX, posY, posZ, itemStack);
        this.init();
    }

    public ItemEntityArtifact(EntityType<? extends ItemEntityArtifact> entityType, Level level, double posX, double posY, double posZ, ItemStack itemStack, double deltaX, double deltaY, double deltaZ) {
        super(entityType, level, posX, posY, posZ, itemStack, deltaX, deltaY, deltaZ);
        this.init();
    }

    protected void init() {
        this.triggerTimeout = ArtifactConfig.CONFIG.triggerDelay.getAsInt();
        this.setInvulnerable(true);
        this.setUnlimitedLifetime();
    }

    public static EntityType.EntityFactory<ItemEntityArtifact> factory() {
        return ItemEntityArtifact::new;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FORCED_MOVE_POS, BlockPos.ZERO);
        builder.define(FORCED_MOVE_SPEED, 0F);
    }

    public void resetForcedMovePos() {
        this.setForcedMovePos(BlockPos.ZERO, 0F);
    }

    public void setForcedMovePos(@Nullable BlockPos pos, float speed) {
        if (pos == null) {
            pos = BlockPos.ZERO;
            speed = 0;
        }
        this.getEntityData().set(FORCED_MOVE_POS, pos);
        this.getEntityData().set(FORCED_MOVE_SPEED, speed);
        this.setDeltaMovement(Vector3.atCenter(this.getForcedMovePos()).subtract(this).normalize().multiply(this.getForcedMoveSpeed()).toVector3d());
    }

    protected boolean hasForcedMovePos() {
        return this.getEntityData().get(FORCED_MOVE_SPEED) > 1E-4;
    }

    protected BlockPos getForcedMovePos() {
        return this.getEntityData().get(FORCED_MOVE_POS);
    }

    protected float getForcedMoveSpeed() {
        return this.getEntityData().get(FORCED_MOVE_SPEED);
    }

    public Optional<ArtifactComponent> getArtifactComponent() {
        return Optional.ofNullable(this.getItem().get(DataComponentsAS.ARTIFACT));
    }

    public void setArtifactComponent(ArtifactComponent component) {
        ItemStack stack = this.getItem().copy();
        stack.set(DataComponentsAS.ARTIFACT, component);
        this.setItem(stack);
    }

    public void finishTriggerSuccess(ServerLevel sLevel) {
        this.resetTrigger();
        this.getArtifactComponent().ifPresent(artifactComponent -> {
            ArtifactComponent nextComponent = artifactComponent.commitActiveCondition();
            if (nextComponent.canBecomeStable()) {
                nextComponent = nextComponent.changeStability(ArtifactStability.STABLE);
            }
            RandomSource negativeSrc = nextComponent.getRandom();
            if (negativeSrc.nextFloat() < ArtifactConfig.CONFIG.negativeEffectGainChance.getAsDouble()) {
                ArtifactEffect newEffect = ArtifactEffectLoader.getNegativeInstance().pickNewEffect(negativeSrc, nextComponent.getRawNegativeEffects()).orElse(null);
                if (newEffect != null) {
                    nextComponent = nextComponent.addFailureEffect(newEffect);
                }
            }
            this.setArtifactComponent(nextComponent);

            int serverTick = sLevel.getServer().getTickCount();
            RandomSource effectRand = RandomSource.create();
            List<TestableArtifactCondition> conditions = nextComponent.conditions();
            for (int count = 0; count < conditions.size(); count++) {
                TestableArtifactCondition condition = conditions.get(count);
                ChancedArtifactEffect effect = condition.successEffect();
                if (effectRand.nextFloat() < effect.chance()) {
                    sLevel.getServer().tell(new TickTask(serverTick + count * 30, () -> {
                        effect.effect().applyEffect(effectRand, sLevel, this);
                    }));
                }
            }
        });
    }

    public void finishTriggerFailure() {
        this.resetTrigger();
    }

    private void resetTrigger() {
        this.activeTrigger = null;
        this.triggerTimeout = ArtifactConfig.CONFIG.triggerDelay.getAsInt();
        this.setNoColor();
    }

    @Override
    public void tick() {
        super.tick();
        this.setUnlimitedLifetime();

        if (this.isOnFire() && this.getRemainingFireTicks() > 20) {
            this.setRemainingFireTicks(20);
        }

        if (this.getArtifactComponent().map(ArtifactComponent::stability).orElse(ArtifactStability.INERT) == ArtifactStability.STABLE) {
            return;
        }

        if (this.hasForcedMovePos() &&
                this.blockPosition().equals(this.getForcedMovePos()) &&
                Vector3.atCenter(this.getForcedMovePos()).distance(this) < 0.8F &&
                this.level().noBlockCollision(this, this.getBoundingBox().deflate(1.0E-7))) {
            this.resetForcedMovePos();
        }

        if (this.level() instanceof ServerLevel sLevel) {
            this.getArtifactComponent().ifPresent(component -> {
                if (!component.stability().mayTriggerPulse()) return;
                if (component.lastPulseGameTick() == -1) {
                    this.setArtifactComponent(component.updatePulseTick(sLevel));
                }
            });

            if (this.getOwner() instanceof ServerPlayer) {
                this.triggerTimeout--;

                this.getArtifactComponent().ifPresent(component -> {
                    if (component.stability() == ArtifactStability.INERT) {
                        component = component.changeStability(ArtifactStability.ACTIVE);
                        this.setArtifactComponent(component);
                    }

                    if (this.triggerTimeout <= 0 &&
                            this.activeTrigger == null &&
                            this.tickCount % 20 == 0 &&
                            this.random.nextInt(ArtifactConfig.CONFIG.triggerChance.getAsInt()) == 0) {

                        ActiveArtifactTrigger.makeTrigger(component).ifPresent(trigger -> {
                            this.activeTrigger = trigger;
                            this.activeTrigger.playConditionSound(this);
                        });
                    }

                    if (component.shouldGenerateNewCondition()) {
                        ArtifactCondition nextCondition = ArtifactConditionLoader.pickNextCondition(component.getRandom(), component.getRawConditions()).orElse(null);
                        ArtifactEffect nextPositiveEffect = ArtifactEffectLoader.getPositiveInstance().pickNewEffect(component.getRandom(), component.getRawPositiveEffects()).orElse(null);
                        if (nextCondition != null && nextPositiveEffect != null) {
                            component = component.createActiveCondition(nextCondition, nextPositiveEffect);
                            this.setArtifactComponent(component);
                        }
                    }
                });

                if (this.activeTrigger != null) {
                    this.activeTrigger.tick(this, sLevel);
                }

                this.gracePulseTimeout--;
                if (this.gracePulseTimeout <= 0) {
                    this.getArtifactComponent().ifPresent(component -> {
                        if (!component.stability().mayTriggerPulse()) return;

                        long nextPulseTick = component.getNextPulseTick();
                        if (nextPulseTick <  sLevel.getGameTime() - (ArtifactConfig.CONFIG.pulseDelay.getAsInt() * 2L)) {
                            component = component.updatePulseTick(sLevel);
                            this.setArtifactComponent(component);
                        }

                        if (nextPulseTick != -1 &&
                                sLevel.getGameTime() >= nextPulseTick &&
                                this.tickCount % 20 == 0 &&
                                this.random.nextInt(5) == 0) {

                            if (this.activeTrigger == null) {
                                if (component.failureEffects().isEmpty() ||
                                        sLevel.getGameTime() + ArtifactConfig.CONFIG.pulseDelay.getAsInt() * 2L >= nextPulseTick) {
                                    RandomSource rand = RandomSource.create();
                                    ArtifactEffect newEffect = ArtifactEffectLoader.getNegativeInstance().pickNewEffect(rand, component.getRawNegativeEffects()).orElse(null);
                                    if (newEffect != null) {
                                        component = component.addFailureEffect(newEffect);
                                    }
                                }

                                int serverTick = sLevel.getServer().getTickCount();
                                RandomSource effectRand = RandomSource.create();
                                List<ChancedArtifactEffect> failureEffects = component.failureEffects();
                                for (int i = 0; i < failureEffects.size(); i++) {
                                    ChancedArtifactEffect effect = failureEffects.get(i);
                                    if (effectRand.nextFloat() < effect.chance() * component.stability().getPulseEffectChanceMultiplier()) {
                                        sLevel.getServer().tell(new TickTask(serverTick + i * 30, () -> {
                                            effect.effect().applyEffect(effectRand, sLevel, this);
                                        }));
                                    }
                                }

                                this.setArtifactComponent(component.updatePulseTick(sLevel).updateStats(component.stats().incrementPulse()));
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void moveTowardsClosestSpace(double x, double y, double z) {
        //Ignore physics if we have a forced move position
        if (!this.hasForcedMovePos()) {
            super.moveTowardsClosestSpace(x, y, z);
        }
    }

    @Override
    public void move(MoverType type, Vec3 pos) {
        if (!this.hasForcedMovePos()) {
            super.move(type, pos);
            return;
        }
        // Other movement adjusting things are just ignored
        if (type == MoverType.SELF) {
            Vector3 dir = Vector3.atCenter(this.getForcedMovePos()).subtract(this).normalize().multiply(this.getForcedMoveSpeed());
            this.setPos(this.getX() + dir.getX(), this.getY() + dir.getY(), this.getZ() + dir.getZ());
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.GENERIC_KILL)) { //At least make it removeable somehow
            this.getItem().onDestroyed(this, source);
            this.discard();
            return false;
        }
        if (this.activeTrigger != null) {
            this.activeTrigger.recordHurt(this, source, amount);
        }
        return false;
    }

    @Override
    public void onAttack(ServerPlayer sPlayer, ItemStack chisel) {
        if (chisel.is(ItemsAS.CHISEL)) {
            if (random.nextFloat() < 0.4F) {
                if (this.getItem().getItem() instanceof ArtifactItem artifact) {
                    artifact.createShard(this.getItem()).ifPresent(shard -> {
                        ItemUtil.dropItemNaturally(this.level(), this.getX(), this.getY(), this.getZ(), shard);
                    });
                }

                Holder<Enchantment> fortuneEnch = sPlayer.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.FORTUNE);
                int fortune = chisel.getEnchantmentLevel(fortuneEnch);
                float breakChance = 0.4F;
                breakChance -= Mth.clamp(fortune, 0, 10) * 0.03F;
                if (random.nextFloat() < breakChance) {
                    ItemStack thisStack = this.getItem();
                    thisStack.shrink(1);
                    this.setItem(thisStack);
                }
            }
            if (random.nextFloat() < 0.75F) {
                chisel.hurtAndBreak(1 + random.nextInt(2), sPlayer.serverLevel(), sPlayer, (item) -> {
                    sPlayer.onEquippedItemBroken(item, EquipmentSlot.MAINHAND);
                    EventHooks.onPlayerDestroyItem(sPlayer, chisel, InteractionHand.MAIN_HAND);
                });
            }
        }
    }

    @Override
    public boolean skipAttackInteraction(Entity entity) {
        super.skipAttackInteraction(entity);
        return false;
    }

    @Override
    public void playerTouch(Player entity) {
        if (this.activeTrigger != null) return;
        if (this.hasForcedMovePos()) return;
        super.playerTouch(entity);
    }

    @Override
    protected boolean isValidAttackableStack(ItemStack stack) {
        return !stack.isEmpty();
    }
}
