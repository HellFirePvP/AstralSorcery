/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact.condition.data;

import hellfirepvp.astralsorcery.common.artifact.ArtifactCondition;
import hellfirepvp.astralsorcery.common.artifact.ArtifactPartIdSelector;
import hellfirepvp.astralsorcery.common.artifact.SelectableArtifactCondition;
import hellfirepvp.astralsorcery.common.artifact.condition.*;
import hellfirepvp.astralsorcery.common.util.data.DescribedEntityPredicate;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactConditionProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ArtifactConditionProvider implements DataProvider {

    private final String modId;
    protected final PackOutput.PathProvider pathProvider;
    protected final CompletableFuture<HolderLookup.Provider> registries;

    private final List<BuiltCondition> registeredConditions = new ArrayList<>();

    protected ArtifactConditionProvider(String modId, PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.modId = modId;
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "artifact_conditions");
        this.registries = registries;
    }

    public abstract void registerConditions();

    protected ArtifactConditionBuilder newCondition(String id) {
        return this.newCondition(ResourceLocation.fromNamespaceAndPath(this.modId, id));
    }

    protected ArtifactConditionBuilder newCondition(ResourceLocation id) {
        return new ArtifactConditionBuilder(this, id);
    }

    BuiltCondition build(ArtifactConditionBuilder builder, ArtifactCondition condition) {
        return this.build(builder, id -> condition);
    }

    BuiltCondition build(ArtifactConditionBuilder builder, Function<ResourceLocation, ? extends ArtifactCondition> condition) {
        ResourceLocation id = builder.getId();
        ArtifactPartIdSelector selector = builder.buildSelector();
        BuiltCondition builtCondition = new BuiltCondition(id, selector, condition.apply(id));
        this.registeredConditions.add(builtCondition);
        return builtCondition;
    }

    protected ArtifactConditionNearBlock.Builder blocksNear(BlockPredicate.Builder builder) {
        return ArtifactConditionNearBlock.of(builder.build());
    }

    protected ArtifactConditionNearEntity.Builder entitiesNear(DescribedEntityPredicate.Builder builder, String key) {
        return ArtifactConditionNearEntity.of(builder.build());
    }

    protected ArtifactCondition.Provider inDimension(ResourceKey<DimensionType> dimType) {
        String langKey = "artifact.astralsorcery.condition.value.in_dimension.%s.%s";
        String hidden = langKey.formatted(dimType.location().getPath(), "hidden");
        String clear = langKey.formatted(dimType.location().getPath(), "clear");
        return ArtifactConditionInDimension.of(dimType, Component.translatable(hidden), Component.translatable(clear));
    }

    protected ArtifactCondition.Provider inAnyStructure(ResourceKey<Structure>... structures) {
        if (structures.length == 0) throw new IllegalArgumentException("At least one structure must be provided");
        String langKey = "artifact.astralsorcery.condition.value.in_structure.%s.%s";
        String hidden = langKey.formatted(structures[0].location().getPath(), "hidden");
        String clear = langKey.formatted(structures[0].location().getPath(), "clear");
        return ArtifactConditionInStructure.of(List.of(structures), Component.translatable(hidden), Component.translatable(clear));
    }

    protected ArtifactConditionDamageType.Builder hitByDamageType(String key) {
        String langKey = "artifact.astralsorcery.condition.value.damage_type.%s.%s";
        String hidden = langKey.formatted(key, "hidden");
        String clear = langKey.formatted(key, "clear");
        return ArtifactConditionDamageType.of(Component.translatable(hidden), Component.translatable(clear));
    }

    protected ArtifactConditionDamageItem.Builder hitByItem(String key) {
        String langKey = "artifact.astralsorcery.condition.value.damage_item.%s.%s";
        String hidden = langKey.formatted(key, "hidden");
        String clear = langKey.formatted(key, "clear");
        return ArtifactConditionDamageItem.of(Component.translatable(hidden), Component.translatable(clear));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return this.registries.thenCompose(provider -> {
            List<CompletableFuture<?>> conditionRegisters = new ArrayList<>();
            List<BuiltCondition> builtConditions = new ArrayList<>();

            this.registeredConditions.clear();
            this.registerConditions();

            this.registeredConditions.forEach(condition -> {
                ResourceLocation conditionName = condition.getId();
                if (builtConditions.contains(condition)) {
                    throw new IllegalArgumentException("Duplicate artifact condition: " + conditionName);
                }
                builtConditions.add(condition);

                Path path = this.pathProvider.json(conditionName);
                conditionRegisters.add(DataProvider.saveStable(output, provider, SelectableArtifactCondition.CODEC, condition.toSelectable(), path));
            });

            return CompletableFuture.allOf(conditionRegisters.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "ArtifactConditions";
    }

    public static final class BuiltCondition {

        private final ResourceLocation id;
        private final ArtifactPartIdSelector selector;
        private final ArtifactCondition condition;

        private BuiltCondition(ResourceLocation id, ArtifactPartIdSelector selector, ArtifactCondition condition) {
            this.condition = condition;
            this.selector = selector;
            this.id = id;
        }

        public ResourceLocation getId() {
            return this.id;
        }

        public ArtifactPartIdSelector getSelector() {
            return this.selector;
        }

        public ArtifactCondition getCondition() {
            return this.condition;
        }

        public SelectableArtifactCondition toSelectable() {
            return new SelectableArtifactCondition(this.getSelector(), this.getCondition());
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            BuiltCondition that = (BuiltCondition) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
