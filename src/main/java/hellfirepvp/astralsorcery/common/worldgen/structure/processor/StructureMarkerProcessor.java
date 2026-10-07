/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.worldgen.structure.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.WorldGenAS;
import hellfirepvp.astralsorcery.common.tile.TileStructureMarker;
import hellfirepvp.astralsorcery.common.worldgen.structure.marker.MarkerReplacementNothing;
import hellfirepvp.astralsorcery.common.worldgen.structure.marker.StructureMarkerReplacement;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StructureMarkerProcessor
 * Created by HellFirePvP
 * Date: 06.10.2026 / 20:46
 */
public class StructureMarkerProcessor extends StructureProcessor {

    public static final MapCodec<StructureMarkerProcessor> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.unboundedMap(ResourceLocation.CODEC, StructureMarkerReplacement.CODEC).fieldOf("markers").forGetter(StructureMarkerProcessor::getMarkers),
            StructureMarkerReplacement.CODEC.optionalFieldOf("fallback", MarkerReplacementNothing.INSTANCE).forGetter(StructureMarkerProcessor::getFallback)
    ).apply(inst, StructureMarkerProcessor::new));

    private final Map<ResourceLocation, StructureMarkerReplacement> markers = new HashMap<>();
    private final StructureMarkerReplacement fallback;

    public StructureMarkerProcessor(Map<ResourceLocation, StructureMarkerReplacement> markers, StructureMarkerReplacement fallback) {
        this.markers.putAll(markers);
        this.fallback = fallback;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<ResourceLocation, StructureMarkerReplacement> getMarkers() {
        return Collections.unmodifiableMap(this.markers);
    }

    public StructureMarkerReplacement getFallback() {
        return this.fallback;
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader level,
                                                        BlockPos offset,
                                                        BlockPos pos,
                                                        StructureTemplate.StructureBlockInfo originalBlockInfo,
                                                        StructureTemplate.StructureBlockInfo currentBlockInfo,
                                                        StructurePlaceSettings settings,
                                                        @Nullable StructureTemplate template) {
        if (!currentBlockInfo.state().is(BlocksAS.STRUCTURE_MARKER.get())) {
            return currentBlockInfo;
        }

        ResourceLocation markerId = TileStructureMarker.readMarkerId(currentBlockInfo.nbt());
        StructureMarkerReplacement replacement = this.getMarkers().getOrDefault(markerId, this.getFallback());
        return replacement.replace(level, currentBlockInfo, settings.getRandom(currentBlockInfo.pos()));
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return WorldGenAS.STRUCTURE_MARKER_PROCESSOR.get();
    }

    public static class Builder {

        private final Map<ResourceLocation, StructureMarkerReplacement> markers = new LinkedHashMap<>();
        private StructureMarkerReplacement fallback = MarkerReplacementNothing.INSTANCE;

        private Builder() {}

        public Builder marker(String markerId, StructureMarkerReplacement replacement) {
            return this.marker(AstralSorcery.key(markerId), replacement);
        }

        public Builder marker(ResourceLocation markerId, StructureMarkerReplacement replacement) {
            this.markers.put(markerId, replacement);
            return this;
        }

        public Builder fallback(StructureMarkerReplacement fallback) {
            this.fallback = fallback;
            return this;
        }

        public boolean isEmpty() {
            return this.markers.isEmpty();
        }

        public StructureMarkerProcessor build() {
            return new StructureMarkerProcessor(this.markers, this.fallback);
        }
    }
}
