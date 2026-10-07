/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.worldgen.structure.marker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.types.StructureMarkerReplacementTypesAS;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MarkerReplacementBiome
 * Created by HellFirePvP
 * Date: 07.10.2026 / 13:56
 */
public class MarkerReplacementBiome extends StructureMarkerReplacement {

    public static final Type<MarkerReplacementBiome> TYPE = new Type<>(RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.unboundedMap(TagKey.codec(Registries.BIOME), StructureMarkerReplacement.CODEC).fieldOf("replacements").forGetter(MarkerReplacementBiome::getReplacements),
            StructureMarkerReplacement.CODEC.fieldOf("fallback").forGetter(MarkerReplacementBiome::getFallback)
    ).apply(inst, MarkerReplacementBiome::new)));

    private final Map<TagKey<Biome>, StructureMarkerReplacement> replacements = new HashMap<>();
    private final StructureMarkerReplacement fallback;

    public MarkerReplacementBiome(Map<TagKey<Biome>, StructureMarkerReplacement> replacements, StructureMarkerReplacement fallback) {
        this.replacements.putAll(replacements);
        this.fallback = fallback;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<TagKey<Biome>, StructureMarkerReplacement> getReplacements() {
        return Collections.unmodifiableMap(this.replacements);
    }

    public StructureMarkerReplacement getFallback() {
        return this.fallback;
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo replace(LevelReader level, StructureTemplate.StructureBlockInfo marker, RandomSource rand) {
        Holder<Biome> biome = level.getBiome(marker.pos());
        for (Map.Entry<TagKey<Biome>, StructureMarkerReplacement> entry : this.replacements.entrySet()) {
            if (biome.is(entry.getKey())) {
                return entry.getValue().replace(level, marker, rand);
            }
        }

        return this.fallback.replace(level, marker, rand);
    }

    @Override
    public Type<?> getType() {
        return StructureMarkerReplacementTypesAS.BIOME.get();
    }

    public static class Builder {

        private final Map<TagKey<Biome>, StructureMarkerReplacement> replacements = new HashMap<>();
        private StructureMarkerReplacement fallback = MarkerReplacementNothing.INSTANCE;

        public Builder add(TagKey<Biome> biomeTag, StructureMarkerReplacement replacement) {
            this.replacements.put(biomeTag, replacement);
            return this;
        }

        public Builder fallback(StructureMarkerReplacement fallback) {
            this.fallback = fallback;
            return this;
        }

        public MarkerReplacementBiome build() {
            return new MarkerReplacementBiome(this.replacements, this.fallback);
        }
    }
}
