/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.artifact.*;
import hellfirepvp.astralsorcery.common.config.server.ArtifactConfig;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.IntRange;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record ArtifactComponent(ArtifactType artifactType,
                                ArtifactStability stability,
                                ArtifactStats stats,
                                long seed,
                                long lastPulseGameTick,
                                int conditionCount,
                                Optional<TestableArtifactCondition> activeCondition,
                                List<TestableArtifactCondition> conditions,
                                List<ChancedArtifactEffect> failureEffects) implements DynamicTooltipComponent {

    public static final Codec<ArtifactComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RegistriesAS.REGISTRY_ARTIFACT_TYPES.byNameCodec().fieldOf("type").forGetter(ArtifactComponent::artifactType),
            CodecUtil.enumCodec(ArtifactStability.class).fieldOf("stability").forGetter(ArtifactComponent::stability),
            ArtifactStats.CODEC.fieldOf("stats").forGetter(ArtifactComponent::stats),
            Codec.LONG.fieldOf("seed").forGetter(ArtifactComponent::seed),
            Codec.LONG.fieldOf("lastPulseGameTick").forGetter(ArtifactComponent::lastPulseGameTick),
            Codec.INT.fieldOf("conditionCount").forGetter(ArtifactComponent::conditionCount),
            TestableArtifactCondition.CODEC.optionalFieldOf("activeCondition").forGetter(ArtifactComponent::activeCondition),
            TestableArtifactCondition.CODEC.listOf().fieldOf("conditions").forGetter(ArtifactComponent::conditions),
            ChancedArtifactEffect.CODEC.listOf().fieldOf("failureEffects").forGetter(ArtifactComponent::failureEffects)
    ).apply(inst, ArtifactComponent::new));

    public static ArtifactComponent initialize(ArtifactType type) {
        long seed = RandomSource.create().nextLong();
        RandomSource seededRand = RandomSource.create(seed);
        IntRange requiredConditions = ArtifactConfig.CONFIG.getArtifactConditionCountRange();
        return new ArtifactComponent(type, ArtifactStability.INERT, ArtifactStats.EMPTY, seed,
                -1, requiredConditions.getRandom(seededRand), Optional.empty(), List.of(), List.of());
    }

    public static ArtifactComponent initializeBlank(ArtifactType type) {
        return new ArtifactComponent(type, ArtifactStability.INERT, ArtifactStats.EMPTY, -1,
                -1, -1, Optional.empty(), List.of(), List.of());
    }

    public ArtifactComponent updatePulseTick(ServerLevel level) {
        return new ArtifactComponent(this.artifactType, this.stability, this.stats, this.seed, level.getGameTime(),
                this.conditionCount, this.activeCondition, this.conditions, this.failureEffects);
    }

    public ArtifactComponent commitActiveCondition() {
        if (this.activeCondition.isEmpty()) {
            return this;
        }
        List<TestableArtifactCondition> newConditions = new ArrayList<>(this.conditions);
        newConditions.add(this.activeCondition.get());
        long nextSeed = this.getRandom().nextLong();
        return new ArtifactComponent(this.artifactType, this.stability, ArtifactStats.EMPTY, nextSeed, this.lastPulseGameTick,
                this.conditionCount, Optional.empty(), List.copyOf(newConditions), this.failureEffects);
    }

    public ArtifactComponent createActiveCondition(ArtifactCondition newCondition, ArtifactEffect newEffect) {
        RandomSource rand = this.getRandom();
        ChancedArtifactEffect newPositiveEffect = new ChancedArtifactEffect(newEffect, ArtifactConfig.CONFIG.getPositiveEffectChance(rand));
        TestableArtifactCondition testableCondition = new TestableArtifactCondition(newCondition, rand.nextLong(), newPositiveEffect);
        return new ArtifactComponent(this.artifactType, this.stability, ArtifactStats.EMPTY, this.seed, this.lastPulseGameTick,
                this.conditionCount, Optional.of(testableCondition), this.conditions, this.failureEffects);
    }

    public ArtifactComponent changeStability(ArtifactStability newStability) {
        return new ArtifactComponent(this.artifactType, newStability, this.stats, this.seed, this.lastPulseGameTick,
                this.conditionCount, this.activeCondition, this.conditions, this.failureEffects);
    }

    public ArtifactComponent updateStats(ArtifactStats newStats) {
        return new ArtifactComponent(this.artifactType, this.stability, newStats, this.seed, this.lastPulseGameTick,
                this.conditionCount, this.activeCondition, this.conditions, this.failureEffects);
    }

    public ArtifactComponent addFailureEffect(ArtifactEffect effect) {
        List<ChancedArtifactEffect> newFailureEffects = new ArrayList<>(this.failureEffects);
        RandomSource rand = this.getRandom();
        newFailureEffects.add(new ChancedArtifactEffect(effect, ArtifactConfig.CONFIG.getNegativeEffectChance(rand)));
        return new ArtifactComponent(this.artifactType, this.stability, this.stats, this.seed, this.lastPulseGameTick,
                this.conditionCount, this.activeCondition, this.conditions, List.copyOf(newFailureEffects));
    }

    public RandomSource getRandom() {
        return RandomSource.create(this.seed);
    }

    public boolean canBecomeStable() {
        return this.conditions.size() >= this.conditionCount;
    }

    public boolean shouldGenerateNewCondition() {
        return this.activeCondition.isEmpty() && this.conditions.size() < this.conditionCount;
    }

    public long getNextPulseTick() {
        if (this.lastPulseGameTick == -1) return -1;
        if (!this.stability().mayTriggerPulse()) return -1;
        int adjustedDelay = Mth.floor(this.stability().getPulseDelayMultiplier() * ArtifactConfig.CONFIG.pulseDelay.getAsInt());
        return this.lastPulseGameTick + adjustedDelay;
    }

    public List<ArtifactCondition> getRawConditions() {
        return this.conditions().stream()
                .map(TestableArtifactCondition::condition)
                .toList();
    }

    public int getActiveConditionCount() {
        return this.conditions.size() + (this.activeCondition.isPresent() ? 1 : 0);
    }

    public List<ArtifactEffect> getRawPositiveEffects() {
        List<ArtifactEffect> effects = new ArrayList<>();
        this.conditions().forEach(condition -> effects.add(condition.successEffect().effect()));
        this.activeCondition().ifPresent(condition -> effects.add(condition.successEffect().effect()));
        return effects;
    }

    public List<ArtifactEffect> getRawNegativeEffects() {
        return this.failureEffects().stream()
                .map(ChancedArtifactEffect::effect)
                .toList();
    }

    @Override
    public void addTooltips(ItemStack stack, Consumer<Component> tooltipAdder, AttributeTooltipContext context) {
        tooltipAdder.accept(this.stability().getDisplay());

        this.conditions().forEach(finished -> tooltipAdder.accept(finished.getDisplay(true)));
        this.activeCondition().ifPresent(active -> tooltipAdder.accept(active.getDisplay(false)));

        if (this.stability().mayTriggerPulse() && context.player() != null) {
            Level level = context.player().level();
            long gameTime = level.getGameTime();
            if (this.lastPulseGameTick() != -1 && gameTime >= this.getNextPulseTick()) {
                tooltipAdder.accept(Component.empty());
                tooltipAdder.accept(Component.translatable("artifact.astralsorcery.pulse.imminent")
                        .withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC));
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ArtifactComponent that = (ArtifactComponent) o;
        return seed == that.seed &&
                conditionCount == that.conditionCount &&
                lastPulseGameTick == that.lastPulseGameTick &&
                Objects.equals(stats, that.stats) &&
                Objects.equals(artifactType, that.artifactType) &&
                stability == that.stability &&
                Objects.equals(conditions, that.conditions) &&
                Objects.equals(failureEffects, that.failureEffects) &&
                Objects.equals(activeCondition, that.activeCondition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artifactType, stability, stats, seed, lastPulseGameTick, conditionCount, activeCondition, conditions, failureEffects);
    }
}
