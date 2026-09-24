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
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactConditionBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactConditionBuilder {

    private final ArtifactConditionProvider provider;
    private final ResourceLocation id;
    private int weight = 1;
    private final Set<ResourceLocation> conflicts = new LinkedHashSet<>();

    public ArtifactConditionBuilder(ArtifactConditionProvider provider, ResourceLocation id) {
        this.provider = provider;
        this.id = id;
    }

    public ArtifactConditionBuilder setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    public ArtifactConditionBuilder addConflict(ArtifactConditionProvider.BuiltCondition conflict) {
        return this.addConflict(conflict.getId());
    }

    public ArtifactConditionBuilder addConflict(ResourceLocation conflict) {
        this.conflicts.add(conflict);
        return this;
    }

    protected ArtifactConditionProvider getProvider() {
        return this.provider;
    }

    ResourceLocation getId() {
        return this.id;
    }

    ArtifactPartIdSelector buildSelector() {
        return new ArtifactPartIdSelector(this.weight, this.conflicts);
    }

    public ArtifactConditionProvider.BuiltCondition build(ArtifactCondition condition) {
        return this.getProvider().build(this, condition);
    }

    public ArtifactConditionProvider.BuiltCondition build(Function<ResourceLocation, ? extends ArtifactCondition> condition) {
        return this.getProvider().build(this, condition);
    }
}
