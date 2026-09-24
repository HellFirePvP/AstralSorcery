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
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactEffectBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactEffectBuilder {

    private final ArtifactEffectProvider provider;
    private final ResourceLocation id;
    private int weight = 1;
    private final Set<ResourceLocation> conflicts = new LinkedHashSet<>();

    public ArtifactEffectBuilder(ArtifactEffectProvider provider, ResourceLocation id) {
        this.provider = provider;
        this.id = id;
    }

    public ArtifactEffectBuilder setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    public ArtifactEffectBuilder addConflict(ArtifactEffectProvider.BuiltEffect conflict) {
        return this.addConflict(conflict.getId());
    }

    public ArtifactEffectBuilder addConflict(ResourceLocation conflict) {
        this.conflicts.add(conflict);
        return this;
    }

    protected ArtifactEffectProvider getProvider() {
        return this.provider;
    }

    ResourceLocation getId() {
        return this.id;
    }

    ArtifactPartIdSelector buildSelector() {
        return new ArtifactPartIdSelector(this.weight, this.conflicts);
    }

    public ArtifactEffectProvider.BuiltEffect build(ArtifactEffect effect) {
        return this.getProvider().build(this, effect);
    }

    public ArtifactEffectProvider.BuiltEffect build(Function<ResourceLocation, ? extends ArtifactEffect> effect) {
        return this.getProvider().build(this, effect);
    }
}
