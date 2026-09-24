/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact.effect.data;

import hellfirepvp.astralsorcery.common.artifact.ArtifactEffect;
import hellfirepvp.astralsorcery.common.artifact.ArtifactPartIdSelector;
import hellfirepvp.astralsorcery.common.artifact.SelectableArtifactEffect;
import hellfirepvp.astralsorcery.common.artifact.effect.*;
import hellfirepvp.astralsorcery.common.util.data.FloatRange;
import hellfirepvp.astralsorcery.common.util.data.IntRange;
import hellfirepvp.astralsorcery.common.util.data.RandomWeightedList;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactEffectProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ArtifactEffectProvider implements DataProvider {

    private final String modId;
    private final String name;
    protected final PackOutput.PathProvider pathProvider;
    protected final CompletableFuture<HolderLookup.Provider> registries;

    private final List<BuiltEffect> registeredEffects = new ArrayList<>();

    protected ArtifactEffectProvider(String modId, boolean positive, PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.modId = modId;
        this.name = "ArtifactEffects([)" + (positive ? "positive" : "negative") + ")";
        String path = positive ? "artifact_effects" : "artifact_penalties";
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, path);
        this.registries = registries;
    }

    public abstract void registerEffects();

    protected ArtifactEffectBuilder newCondition(String id) {
        return this.newCondition(ResourceLocation.fromNamespaceAndPath(this.modId, id));
    }

    protected ArtifactEffectBuilder newCondition(ResourceLocation id) {
        return new ArtifactEffectBuilder(this, id);
    }

    BuiltEffect build(ArtifactEffectBuilder builder, ArtifactEffect effect) {
        return this.build(builder, id -> effect);
    }

    BuiltEffect build(ArtifactEffectBuilder builder, Function<ResourceLocation, ? extends ArtifactEffect> effect) {
        ResourceLocation id = builder.getId();
        ArtifactPartIdSelector selector = builder.buildSelector();
        BuiltEffect builtEffect = new BuiltEffect(id, selector, effect.apply(id));
        this.registeredEffects.add(builtEffect);
        return builtEffect;
    }

    protected ArtifactEffectEffects.Builder applyEffects() {
        return ArtifactEffectEffects.of();
    }

    protected ArtifactEffect.Provider explode(int range, Level.ExplosionInteraction interaction) {
        return ArtifactEffectExplode.of(range, interaction);
    }

    protected ArtifactEffect.Provider placeRandomBlocks(int range, float placeChance, RandomWeightedList.Builder<BlockState> palette) {
        return ArtifactEffectPlaceBlocks.of(range, placeChance, palette);
    }

    protected ArtifactEffect.Provider spawnRandomMobs(int range, IntRange spawnCount, RandomWeightedList.Builder<EntityType<?>> spawnableTypes) {
        return ArtifactEffectSpawnMobs.of(range, spawnCount, spawnableTypes);
    }

    protected ArtifactEffect.Provider spawnNaturalMobs(int range, IntRange spawnCount, MobCategory category) {
        return ArtifactEffectSpawnNaturalMobs.of(range, spawnCount, category);
    }

    protected ArtifactEffect.Provider randomMove(FloatRange distance, float moveSpeed) {
        return ArtifactEffectRandomMove.of(distance, moveSpeed);
    }

    protected ArtifactEffect.Provider randomTeleport(FloatRange distance) {
        return ArtifactEffectRandomTeleport.of(distance);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return this.registries.thenCompose(provider -> {
            List<CompletableFuture<?>> effectRegisters = new ArrayList<>();
            List<BuiltEffect> builtEffects = new ArrayList<>();

            this.registeredEffects.clear();
            this.registerEffects();

            this.registeredEffects.forEach(condition -> {
                ResourceLocation effectName = condition.getId();
                if (builtEffects.contains(condition)) {
                    throw new IllegalArgumentException("Duplicate artifact effect: " + effectName);
                }
                builtEffects.add(condition);

                Path path = this.pathProvider.json(effectName);
                effectRegisters.add(DataProvider.saveStable(output, provider, SelectableArtifactEffect.CODEC, condition.toSelectable(), path));
            });

            return CompletableFuture.allOf(effectRegisters.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return this.name;
    }

    public static final class BuiltEffect {

        private final ResourceLocation id;
        private final ArtifactPartIdSelector selector;
        private final ArtifactEffect effect;

        private BuiltEffect(ResourceLocation id, ArtifactPartIdSelector selector, ArtifactEffect effect) {
            this.effect = effect;
            this.selector = selector;
            this.id = id;
        }

        public ResourceLocation getId() {
            return this.id;
        }

        public ArtifactPartIdSelector getSelector() {
            return this.selector;
        }

        public ArtifactEffect getEffect() {
            return this.effect;
        }

        public SelectableArtifactEffect toSelectable() {
            return new SelectableArtifactEffect(this.getSelector(), this.getEffect());
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            BuiltEffect that = (BuiltEffect) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
