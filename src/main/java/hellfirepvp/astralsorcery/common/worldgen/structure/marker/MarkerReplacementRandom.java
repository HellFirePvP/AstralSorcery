/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.worldgen.structure.marker;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.types.StructureMarkerReplacementTypesAS;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MarkerReplacementRandom
 * Created by HellFirePvP
 * Date: 06.10.2026 / 19:58
 */
public class MarkerReplacementRandom extends StructureMarkerReplacement {

    public static final Type<MarkerReplacementRandom> TYPE = new Type<>(RecordCodecBuilder.mapCodec(inst -> inst.group(
            SimpleWeightedRandomList.wrappedCodec(StructureMarkerReplacement.CODEC).fieldOf("entries").forGetter(MarkerReplacementRandom::getEntries)
    ).apply(inst, MarkerReplacementRandom::new)));

    private final SimpleWeightedRandomList<StructureMarkerReplacement> entries;

    public MarkerReplacementRandom(SimpleWeightedRandomList<StructureMarkerReplacement> entries) {
        this.entries = entries;
    }

    public static Builder builder() {
        return new Builder();
    }

    public SimpleWeightedRandomList<StructureMarkerReplacement> getEntries() {
        return this.entries;
    }

    @Override
    public Type<?> getType() {
        return StructureMarkerReplacementTypesAS.RANDOM.get();
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo replace(LevelReader level, StructureTemplate.StructureBlockInfo marker, RandomSource rand) {
        return this.getEntries().getRandomValue(rand)
                .map(replacement -> replacement.replace(level, marker, rand))
                .orElse(null);
    }

    public static class Builder {

        private final SimpleWeightedRandomList.Builder<StructureMarkerReplacement> entries = SimpleWeightedRandomList.builder();

        private Builder() {}

        public Builder entry(int weight, StructureMarkerReplacement replacement) {
            this.entries.add(replacement, weight);
            return this;
        }

        public MarkerReplacementRandom build() {
            return new MarkerReplacementRandom(this.entries.build());
        }
    }
}
