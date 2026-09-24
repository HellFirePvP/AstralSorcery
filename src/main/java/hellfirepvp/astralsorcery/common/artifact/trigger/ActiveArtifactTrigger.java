/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact.trigger;

import hellfirepvp.astralsorcery.common.artifact.ArtifactStability;
import hellfirepvp.astralsorcery.common.artifact.ArtifactStats;
import hellfirepvp.astralsorcery.common.artifact.TestableArtifactCondition;
import hellfirepvp.astralsorcery.common.artifact.condition.OnHitArtifactCondition;
import hellfirepvp.astralsorcery.common.component.ArtifactComponent;
import hellfirepvp.astralsorcery.common.visual.type.ArtifactConditionTriggerEffect;
import hellfirepvp.astralsorcery.common.entity.item.ItemEntityArtifact;
import hellfirepvp.astralsorcery.common.util.ServerSoundHelper;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ActiveArtifactTrigger
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ActiveArtifactTrigger {

    public static final int TRIGGER_TIMEOUT = 10 * 20;

    private final RandomSource rand = RandomSource.create();

    private final Deque<ActiveCondition> pendingConditions = new LinkedList<>();
    private int triggerTimer = 0;
    private int conditionTimeout = 20;

    private ActiveArtifactTrigger() {}

    public static Optional<ActiveArtifactTrigger> makeTrigger(ArtifactComponent artifactComponent) {
        ActiveArtifactTrigger trigger = new ActiveArtifactTrigger();
        artifactComponent.conditions().forEach(condition -> {
            trigger.pendingConditions.addLast(new ActiveCondition(condition));
        });
        artifactComponent.activeCondition().ifPresent(condition -> {
            trigger.pendingConditions.addLast(new ActiveCondition(condition));
        });
        trigger.triggerTimer = TRIGGER_TIMEOUT;

        if (trigger.pendingConditions.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(trigger);
    }

    public void recordHurt(ItemEntityArtifact artifact, DamageSource source, float amount) {
        ActiveCondition currentCondition = this.pendingConditions.peekFirst();
        if (currentCondition == null) return;
        if (currentCondition.condition.condition() instanceof OnHitArtifactCondition onHitCondition) {
            if (onHitCondition.isFulfilled(artifact, currentCondition.condition.getRandom(), source, amount)) {
                if (source.getDirectEntity() != null) {
                    Vector3 at = new Vector3(source.getDirectEntity()).addY(source.getDirectEntity().getEyeHeight() * 0.5F);
                    currentCondition.succeedingPositions.add(at);
                } else {
                    RandomSource rand = RandomSource.create();
                    for (int i = 0; i < 3; i++) {
                        currentCondition.succeedingPositions.add(Vector3.random(rand).multiply(1F + rand.nextFloat() * 1.5F).add(artifact.position()));
                    }
                }
            }
        }
    }

    public void tick(ItemEntityArtifact artifact, ServerLevel sLevel) {
        ActiveCondition currentCondition = this.pendingConditions.peekFirst();
        if (currentCondition == null) return; //huh.
        artifact.setColor(currentCondition.condition.getColor());

        if (this.conditionTimeout-- > 0) return;

        this.triggerTimer--;
        if (this.triggerTimer <= 0) {
            this.failTrigger(artifact);
            return;
        }

        currentCondition.test(artifact);

        if (!currentCondition.succeedingPositions.isEmpty()) {
            currentCondition.condition.fulfillCondition(artifact);
            this.spawnEffects(artifact, sLevel, currentCondition);
            this.completeTrigger(artifact, sLevel);
        }
    }

    private void failTrigger(ItemEntityArtifact artifact) {
        artifact.setNoColor();

        artifact.getArtifactComponent().ifPresent(component -> {
            ArtifactStability stability = component.stability();
            ArtifactStats stats = component.stats();
            ArtifactComponent newComponent = component;

            if (stats.eligibleForStabilityDecrease() && this.rand.nextBoolean()) {
                ArtifactStability nextStability = stability.getWorseStability().orElse(null);
                if (nextStability != null) {
                    newComponent = newComponent.changeStability(nextStability)
                            .updateStats(ArtifactStats.EMPTY);
                }
            } else {
                if (component.getActiveConditionCount() == this.pendingConditions.size()) {
                    newComponent = newComponent.updateStats(stats.incrementEmptyTrigger());
                } else {
                    newComponent = newComponent.updateStats(stats.incrementPartialTrigger());
                }
            }

            artifact.setArtifactComponent(newComponent);

            if (stability.mayCauseBreak() && stats.eligibleForBreak() && this.rand.nextInt(16) == 0) {
                artifact.remove(Entity.RemovalReason.KILLED);
                // todo more fancy stuff
            }
        });

        artifact.finishTriggerFailure();
    }

    private void completeTrigger(ItemEntityArtifact artifact, ServerLevel sLevel) {
        if (this.pendingConditions.isEmpty()) return;
        this.pendingConditions.removeFirst();
        this.triggerTimer = TRIGGER_TIMEOUT;
        this.conditionTimeout = 20;

        artifact.getArtifactComponent().ifPresent(component -> {
            ArtifactStability nextStability = component.stability().getBetterStability().orElse(component.stability());
            artifact.setArtifactComponent(component
                    .changeStability(nextStability)
                    .updateStats(ArtifactStats.EMPTY));
        });

        if (this.pendingConditions.isEmpty()) {
            artifact.finishTriggerSuccess(sLevel);
        } else {
            this.playConditionSound(artifact);
        }
    }

    private void spawnEffects(ItemEntityArtifact artifact, ServerLevel sLevel, ActiveCondition condition) {
        Vector3 from = new Vector3(artifact).addY(artifact.getEyeHeight());
        ColorWrapper color = condition.condition.getColor();
        condition.succeedingPositions.forEach(pos -> {
            ArtifactConditionTriggerEffect.of(from, pos, color).sendToNearby(sLevel, from.toBlockPos());
        });
    }

    public void playConditionSound(ItemEntityArtifact artifact) {
        ActiveCondition condition = this.pendingConditions.peekFirst();
        if (condition == null) return;
        RandomSource rand = condition.condition.getRandom();

        SoundEvent sound = NoteBlockInstrument.BELL.getSoundEvent().value();
        float pitch = 0.1F + rand.nextFloat() * 0.2F;
        ServerSoundHelper.playSoundAround(sound, SoundSource.MASTER, artifact.level(), artifact.position(), 1F, pitch);
    }

    private static class ActiveCondition {

        private final TestableArtifactCondition condition;
        private final List<Vector3> succeedingPositions = new ArrayList<>();

        private ActiveCondition(TestableArtifactCondition condition) {
            this.condition = condition;
        }

        private void test(ItemEntityArtifact artifact) {
            if (this.succeedingPositions.isEmpty()) {
                this.succeedingPositions.addAll(this.condition.isFulfilled(artifact));
            }
        }
    }
}
