/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactStability
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public enum ArtifactStability {

    INERT(ChatFormatting.GRAY),    // Just idiling, inactive
    ACTIVE(1F, 1F, ChatFormatting.GREEN),   // Active, not unstable
    UNSTABLE(0.8F, 1F, ChatFormatting.YELLOW), // Slightly unstable, more common pulses
    VOLATILE(0.3F, 1.5F, ChatFormatting.RED), // Highly unstable, may be recovered
    CRITICAL(0.05F, 2.5F, ChatFormatting.DARK_RED), // Pulses a lot, will break, unrecoverable

    STABLE(ChatFormatting.BLUE);   // Stable result, can be crafted into a result

    private final float pulseMultiplier;
    private final float pulseEffectChanceMultiplier;
    private final ChatFormatting chatColor;

    ArtifactStability(ChatFormatting chatColor) {
        this(-1, -1, chatColor);
    }

    ArtifactStability(float pulseMultiplier, float pulseEffectChanceMultiplier, ChatFormatting chatColor) {
        this.pulseMultiplier = pulseMultiplier;
        this.pulseEffectChanceMultiplier = pulseEffectChanceMultiplier;
        this.chatColor = chatColor;
    }

    public boolean mayTriggerPulse() {
        return this.pulseMultiplier > 0 && this.pulseEffectChanceMultiplier > 0;
    }

    public float getPulseDelayMultiplier() {
        return this.pulseMultiplier;
    }

    public float getPulseEffectChanceMultiplier() {
        return this.pulseEffectChanceMultiplier;
    }

    public Optional<ArtifactStability> getWorseStability() {
        return switch (this) {
            case ACTIVE -> Optional.of(UNSTABLE);
            case UNSTABLE -> Optional.of(VOLATILE);
            case VOLATILE -> Optional.of(CRITICAL);
            default -> Optional.empty();
        };
    }

    public Optional<ArtifactStability> getBetterStability() {
        return switch (this) {
            case UNSTABLE -> Optional.of(ACTIVE);
            case VOLATILE -> Optional.of(UNSTABLE);
            case CRITICAL -> Optional.of(VOLATILE);
            default -> Optional.empty();
        };
    }

    public boolean mayCauseBreak() {
        return this == CRITICAL;
    }

    public ChatFormatting getChatColor() {
        return this.chatColor;
    }

    public Component getDisplay() {
        return Component.translatable("artifact.astralsorcery.stability.%s".formatted(this.name().toLowerCase()))
                .withStyle(this.getChatColor()).withStyle(ChatFormatting.ITALIC);
    }
}
