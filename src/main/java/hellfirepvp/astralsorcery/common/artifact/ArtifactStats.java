/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactStats
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record ArtifactStats(int pulses, int emptyTriggers, int partialTriggers) {

    public static ArtifactStats EMPTY = new ArtifactStats(0, 0, 0);
    public static final Codec<ArtifactStats> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("pulses").forGetter(ArtifactStats::pulses),
            Codec.INT.fieldOf("emptyTriggers").forGetter(ArtifactStats::emptyTriggers),
            Codec.INT.fieldOf("partialTriggers").forGetter(ArtifactStats::partialTriggers)
    ).apply(inst, ArtifactStats::new));

    public ArtifactStats incrementPulse() {
        return new ArtifactStats(this.pulses + 1, this.emptyTriggers, this.partialTriggers);
    }

    public ArtifactStats incrementEmptyTrigger() {
        return new ArtifactStats(this.pulses, this.emptyTriggers + 1, this.partialTriggers);
    }

    public ArtifactStats incrementPartialTrigger() {
        return new ArtifactStats(this.pulses, this.emptyTriggers, this.partialTriggers + 1);
    }

    public boolean eligibleForStabilityDecrease() {
        return this.pulses >= 4 || this.emptyTriggers >= 2 || this.partialTriggers >= 2;
    }

    public boolean eligibleForBreak() {
        return this.emptyTriggers + this.partialTriggers >= 20;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ArtifactStats that = (ArtifactStats) o;
        return pulses == that.pulses && emptyTriggers == that.emptyTriggers && partialTriggers == that.partialTriggers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pulses, emptyTriggers, partialTriggers);
    }
}
