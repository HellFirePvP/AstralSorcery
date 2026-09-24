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
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactPartIdSelector
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactPartIdSelector {

    public static final Codec<ArtifactPartIdSelector> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("weight").forGetter(ArtifactPartIdSelector::getWeight),
            Codec.list(ResourceLocation.CODEC).fieldOf("conflicts").forGetter(selector -> List.copyOf(selector.conflicts))
    ).apply(inst, ArtifactPartIdSelector::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArtifactPartIdSelector> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ArtifactPartIdSelector::getWeight,
            ResourceLocation.STREAM_CODEC.apply(SetCodec.streamOp()),
            ArtifactPartIdSelector::getConflicts,
            ArtifactPartIdSelector::new);

    private final int weight;
    private final Set<ResourceLocation> conflicts = new LinkedHashSet<>();

    public ArtifactPartIdSelector(int weight, Collection<ResourceLocation> conflicts) {
        this.weight = weight;
        this.conflicts.addAll(conflicts);
    }

    public int getWeight() {
        return this.weight;
    }

    public Set<ResourceLocation> getConflicts() {
        return Set.copyOf(this.conflicts);
    }

    public boolean conflictsWith(List<ResourceLocation> otherConditions) {
        if (this.conflicts.isEmpty()) return false;
        for (ResourceLocation cond : otherConditions) {
            if (this.conflicts.contains(cond)) {
                return true;
            }
        }
        return false;
    }
}
