/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.config.server;

import hellfirepvp.astralsorcery.common.config.ConfigEntry;
import hellfirepvp.astralsorcery.common.util.data.IntRange;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactConfig
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactConfig extends ConfigEntry {

    public static final ArtifactConfig CONFIG = new ArtifactConfig();

    public ModConfigSpec.IntValue triggerDelay;
    public ModConfigSpec.IntValue triggerChance;
    public ModConfigSpec.IntValue pulseDelay;

    public ModConfigSpec.IntValue artifactConditionCountMin;
    public ModConfigSpec.IntValue artifactConditionCountMax;

    public ModConfigSpec.DoubleValue negativeEffectGainChance;
    public ModConfigSpec.DoubleValue positiveEffectChanceMin;
    public ModConfigSpec.DoubleValue positiveEffectChanceMax;
    public ModConfigSpec.DoubleValue negativeEffectChanceMin;
    public ModConfigSpec.DoubleValue negativeEffectChanceMax;

    private ArtifactConfig() {
        super("artifact");
    }

    @Override
    public void createEntries(ModConfigSpec.Builder cfgBuilder) {
        this.triggerDelay = cfgBuilder
                .comment("Sets the minimum delay between potential artifact triggers.")
                .translation(translationKey("triggerDelay"))
                .defineInRange("triggerDelay", 12 * 20, 20, 10000);
        this.triggerChance = cfgBuilder
                .comment("Define the chance (1 in X) per second for the artifact to start a trigger after the delay is over.")
                .translation(translationKey("triggerChance"))
                .defineInRange("triggerChance", 20, 1, 10000);
        this.pulseDelay = cfgBuilder
                .comment("Sets the default delay between artifact pulses, before stability.")
                .translation(translationKey("pulseDelay"))
                .defineInRange("pulseDelay", 40 * 20, 20, 10000);

        this.artifactConditionCountMin = cfgBuilder
                .comment("The minimum amount of conditions a artifact may need to stabilize.")
                .translation(translationKey("artifactConditionCountMin"))
                .defineInRange("artifactConditionCountMin", 4, 1, 100);
        this.artifactConditionCountMax = cfgBuilder
                .comment("The maximum amount of conditions a artifact may need to stabilize.")
                .translation(translationKey("artifactConditionCountMax"))
                .defineInRange("artifactConditionCountMax", 6, 1, 100);

        this.negativeEffectGainChance = cfgBuilder
                .comment("When a new condition is added to an artifact, this defines the chance that a negative effect is added to the artifact.")
                .translation(translationKey("negativeEffectGainChance"))
                .defineInRange("negativeEffectGainChance", 0.4F, 0F, 1F);
        this.positiveEffectChanceMin = cfgBuilder
                .comment("When adding a new condition, this defines the lower bound chance that fulfilling the chain of conditions will trigger that condition's positive effect.")
                .translation(translationKey("positiveEffectChanceMin"))
                .defineInRange("positiveEffectChanceMin", 0.2F, 0F, 1F);
        this.positiveEffectChanceMax = cfgBuilder
                .comment("When adding a new condition, this defines the upper bound chance that fulfilling the chain of conditions will trigger that condition's positive effect.")
                .translation(translationKey("positiveEffectChanceMax"))
                .defineInRange("positiveEffectChanceMax", 0.55F, 0F, 1F);
        this.negativeEffectChanceMin = cfgBuilder
                .comment("When adding a new negative effct, this defines the lower bound chance that a pulse triggers this effect.")
                .translation(translationKey("negativeEffectChanceMin"))
                .defineInRange("negativeEffectChanceMin", 0.3F, 0F, 1F);
        this.negativeEffectChanceMax = cfgBuilder
                .comment("When adding a new negative effct, this defines the uppre bound chance that a pulse triggers this effect.")
                .translation(translationKey("negativeEffectChanceMax"))
                .defineInRange("negativeEffectChanceMax", 0.7F, 0F, 1F);
    }

    public IntRange getArtifactConditionCountRange() {
        return IntRange.of(this.artifactConditionCountMin.get(), this.artifactConditionCountMax.get());
    }

    public float getPositiveEffectChance(RandomSource rand) {
        float minChance = Math.min(this.positiveEffectChanceMin.get().floatValue(), this.positiveEffectChanceMax.get().floatValue());
        float maxChance = Math.max(this.positiveEffectChanceMin.get().floatValue(), this.positiveEffectChanceMax.get().floatValue());
        return minChance + rand.nextFloat() * (maxChance - minChance);
    }

    public float getNegativeEffectChance(RandomSource rand) {
        float minChance = Math.min(this.negativeEffectChanceMin.get().floatValue(), this.negativeEffectChanceMax.get().floatValue());
        float maxChance = Math.max(this.negativeEffectChanceMin.get().floatValue(), this.negativeEffectChanceMax.get().floatValue());
        return minChance + rand.nextFloat() * (maxChance - minChance);
    }
}
