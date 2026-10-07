/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.world.structure;

import com.mojang.datafixers.util.Pair;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.worldgen.structure.TemplateStructure;
import hellfirepvp.astralsorcery.common.worldgen.structure.marker.StructureMarkerReplacement;
import hellfirepvp.astralsorcery.common.worldgen.structure.processor.StructureMarkerProcessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.*;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StructureDefinition
 * Created by HellFirePvP
 * Date: 06.10.2026 / 22:51
 */
public class StructureDefinition {

    private final SortedMap<ResourceKey<Structure>, Integer> structureKeys;
    private final ResourceKey<StructureSet> structureSetKey;
    private final ResourceKey<StructureTemplatePool> templatePoolKey;
    private final ResourceKey<StructureProcessorList> processorListKey;

    private final TagKey<Biome> biomes;
    private final GenerationStep.Decoration generationStep;
    private final TerrainAdjustment terrainAdjustment;
    private final List<TemplateEntry> templates;
    private final StructureTemplatePool.Projection projection;

    private final int spacing;
    private final int separation;
    private final int salt;
    private final RandomSpreadType spreadType;

    private final Heightmap.Types heightmap;
    private final int verticalOffset;
    private final int requiredClearance;
    private final int boundingBoxExpansion;
    private final boolean extendBoundsToBuildHeight;
    private final boolean randomChunkOffset;
    private final Optional<Rotation> rotation;
    private final LiquidSettings liquidSettings;

    private final StructureMarkerProcessor markerProcessor;
    private final Function<BootstrapContext<StructureProcessorList>, List<StructureProcessor>> additionalProcessors;

    private StructureDefinition(Builder builder) {
        this.structureKeys = builder.structureKeys;
        this.structureSetKey = ResourceKey.create(Registries.STRUCTURE_SET, AstralSorcery.key(builder.name + "_structures"));
        this.templatePoolKey = ResourceKey.create(Registries.TEMPLATE_POOL, AstralSorcery.key(builder.name + "_pool"));
        this.processorListKey = ResourceKey.create(Registries.PROCESSOR_LIST, AstralSorcery.key(builder.name + "_processors"));

        this.biomes = builder.biomes;
        this.generationStep = builder.generationStep;
        this.terrainAdjustment = builder.terrainAdjustment;
        this.templates = List.copyOf(builder.templates);
        this.projection = builder.projection;

        this.spacing = builder.spacing;
        this.separation = builder.separation;
        this.salt = builder.salt;
        this.spreadType = builder.spreadType;

        this.heightmap = builder.heightmap;
        this.verticalOffset = builder.verticalOffset;
        this.requiredClearance = builder.requiredClearance;
        this.boundingBoxExpansion = builder.boundingBoxExpansion;
        this.extendBoundsToBuildHeight = builder.extendBoundsToBuildHeight;
        this.randomChunkOffset = builder.randomChunkOffset;
        this.rotation = builder.rotation;
        this.liquidSettings = builder.liquidSettings;

        this.markerProcessor = builder.markers.build();
        this.additionalProcessors = builder.additionalProcessors;
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public SortedMap<ResourceKey<Structure>, Integer> getStructureKeys() {
        return this.structureKeys;
    }

    public ResourceKey<StructureSet> getStructureSetKey() {
        return this.structureSetKey;
    }

    public ResourceKey<StructureTemplatePool> getTemplatePoolKey() {
        return this.templatePoolKey;
    }

    public ResourceKey<StructureProcessorList> getProcessorListKey() {
        return this.processorListKey;
    }

    public void registerStructure(BootstrapContext<Structure> context) {
        HolderSet<Biome> biomeSet = context.lookup(Registries.BIOME).getOrThrow(this.biomes);

        this.getStructureKeys().forEach((structure, weight) -> {
            context.register(structure, new TemplateStructure(
                    new Structure.StructureSettings.Builder(biomeSet)
                            .generationStep(this.generationStep)
                            .terrainAdapation(this.terrainAdjustment)
                            .build(),
                    context.lookup(Registries.TEMPLATE_POOL).getOrThrow(this.getTemplatePoolKey()),
                    this.heightmap,
                    this.verticalOffset,
                    this.requiredClearance,
                    this.boundingBoxExpansion,
                    this.extendBoundsToBuildHeight,
                    this.randomChunkOffset,
                    this.rotation,
                    this.liquidSettings
            ));
        });
    }

    public void registerStructureSet(BootstrapContext<StructureSet> context) {
        List<StructureSet.StructureSelectionEntry> entries = new ArrayList<>();

        this.getStructureKeys().forEach((structure, weight) -> {
            entries.add(new StructureSet.StructureSelectionEntry(
                    context.lookup(Registries.STRUCTURE).getOrThrow(structure),
                    weight
            ));
        });

        context.register(this.getStructureSetKey(), new StructureSet(entries,
                new RandomSpreadStructurePlacement(this.spacing, this.separation, this.spreadType, this.salt)));
    }

    public void registerTemplatePool(BootstrapContext<StructureTemplatePool> context) {
        Holder<StructureProcessorList> processors = context.lookup(Registries.PROCESSOR_LIST).getOrThrow(this.getProcessorListKey());

        List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> elements = new ArrayList<>();
        for (TemplateEntry template : this.templates) {
            elements.add(new Pair<>(StructurePoolElement.single(template.template().toString(), processors), template.weight()));
        }

        context.register(this.getTemplatePoolKey(), new StructureTemplatePool(
                context.lookup(Registries.TEMPLATE_POOL).getOrThrow(Pools.EMPTY),
                elements,
                this.projection
        ));
    }

    public void registerProcessorList(BootstrapContext<StructureProcessorList> context) {
        List<StructureProcessor> processors = new ArrayList<>();
        processors.add(this.markerProcessor);
        processors.addAll(this.additionalProcessors.apply(context));

        context.register(this.getProcessorListKey(), new StructureProcessorList(processors));
    }

    private record TemplateEntry(ResourceLocation template, int weight) {}

    public static class Builder {

        private final String name;
        private final SortedMap<ResourceKey<Structure>, Integer> structureKeys = new TreeMap<>();
        private final List<TemplateEntry> templates = new ArrayList<>();
        private final StructureMarkerProcessor.Builder markers = StructureMarkerProcessor.builder();

        private TagKey<Biome> biomes;
        private GenerationStep.Decoration generationStep = GenerationStep.Decoration.SURFACE_STRUCTURES;
        private TerrainAdjustment terrainAdjustment = TerrainAdjustment.NONE;
        private StructureTemplatePool.Projection projection = StructureTemplatePool.Projection.RIGID;

        private int spacing = 32;
        private int separation = 8;
        private int salt;
        private RandomSpreadType spreadType = RandomSpreadType.LINEAR;

        private Heightmap.Types heightmap = Heightmap.Types.WORLD_SURFACE_WG;
        private int verticalOffset = 0;
        private int requiredClearance = 0;
        private int boundingBoxExpansion = 0;
        private boolean extendBoundsToBuildHeight = false;
        private boolean randomChunkOffset = true;
        private Optional<Rotation> rotation = Optional.empty();
        private LiquidSettings liquidSettings = LiquidSettings.APPLY_WATERLOGGING;

        private Function<BootstrapContext<StructureProcessorList>, List<StructureProcessor>> additionalProcessors = context -> List.of();

        private Builder(String name) {
            this.name = name;
            this.salt = Math.abs(name.hashCode());
        }

        public Builder structure(String name) {
            return this.structure(name, 1);
        }

        public Builder structure(ResourceKey<Structure> structureKey) {
            return this.structure(structureKey, 1);
        }

        public Builder structure(String name, int weight) {
            return this.structure(ResourceKey.create(Registries.STRUCTURE, AstralSorcery.key(name)), weight);
        }

        public Builder structure(ResourceKey<Structure> structureKey, int weight) {
            this.structureKeys.put(structureKey, weight);
            return this;
        }

        public Builder template(String template) {
            return this.template(template, 1);
        }

        public Builder template(String template, int weight) {
            this.templates.add(new TemplateEntry(AstralSorcery.key(template), weight));
            return this;
        }

        public Builder biomes(TagKey<Biome> biomes) {
            this.biomes = biomes;
            return this;
        }

        public Builder generationStep(GenerationStep.Decoration generationStep) {
            this.generationStep = generationStep;
            return this;
        }

        public Builder terrainAdjustment(TerrainAdjustment terrainAdjustment) {
            this.terrainAdjustment = terrainAdjustment;
            return this;
        }

        public Builder projection(StructureTemplatePool.Projection projection) {
            this.projection = projection;
            return this;
        }

        public Builder spread(int spacing, int separation, int salt) {
            this.spacing = spacing;
            this.separation = separation;
            this.salt = Math.abs(salt);
            return this;
        }

        public Builder spreadType(RandomSpreadType spreadType) {
            this.spreadType = spreadType;
            return this;
        }

        public Builder heightmap(Heightmap.Types heightmap) {
            this.heightmap = heightmap;
            return this;
        }

        public Builder verticalOffset(int verticalOffset) {
            this.verticalOffset = verticalOffset;
            return this;
        }

        public Builder requiredClearance(int requiredClearance) {
            this.requiredClearance = requiredClearance;
            return this;
        }

        public Builder boundingBoxExpansion(int boundingBoxExpansion) {
            this.boundingBoxExpansion = boundingBoxExpansion;
            return this;
        }

        public Builder extendBoundsToBuildHeight() {
            this.extendBoundsToBuildHeight = true;
            return this;
        }

        public Builder fixedChunkOffset() {
            this.randomChunkOffset = false;
            return this;
        }

        public Builder rotation(Rotation rotation) {
            this.rotation = Optional.of(rotation);
            return this;
        }

        public Builder liquidSettings(LiquidSettings liquidSettings) {
            this.liquidSettings = liquidSettings;
            return this;
        }

        public Builder marker(String markerId, StructureMarkerReplacement replacement) {
            this.markers.marker(markerId, replacement);
            return this;
        }

        public Builder markerFallback(StructureMarkerReplacement fallback) {
            this.markers.fallback(fallback);
            return this;
        }

        public Builder processors(Function<BootstrapContext<StructureProcessorList>, List<StructureProcessor>> additionalProcessors) {
            this.additionalProcessors = additionalProcessors;
            return this;
        }

        public StructureDefinition build() {
            if (this.structureKeys.isEmpty()) {
                this.structureKeys.put(ResourceKey.create(Registries.STRUCTURE, AstralSorcery.key(this.name)), 1);
            }
            if (this.templates.isEmpty()) {
                this.templates.add(new TemplateEntry(AstralSorcery.key(this.name), 1));
            }
            if (this.biomes == null) {
                throw new IllegalStateException("No biomes specified for " + this.name);
            }
            return new StructureDefinition(this);
        }
    }
}
