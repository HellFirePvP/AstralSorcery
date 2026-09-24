/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact.condition.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.artifact.ArtifactCondition;
import hellfirepvp.astralsorcery.common.artifact.SelectableArtifactCondition;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.MapStream;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactConditionLoader
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactConditionLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final ArtifactConditionLoader INSTANCE = new ArtifactConditionLoader();

    private final Set<SelectableArtifactCondition> conditions = new HashSet<>();
    private final Map<ArtifactCondition.Type<?>, Set<SelectableArtifactCondition>> byTypeConditions = new HashMap<>();

    private ArtifactConditionLoader() {
        super(GSON, "artifact_conditions");
    }

    public static ArtifactConditionLoader getInstance() {
        return INSTANCE;
    }

    public List<SelectableArtifactCondition> getLoadedConditions() {
        return List.copyOf(this.conditions);
    }

    public Map<ArtifactCondition.Type<?>, Set<SelectableArtifactCondition>> getTypedLoadedConditions() {
        return Collections.unmodifiableMap(this.byTypeConditions);
    }

    public static Optional<ArtifactCondition> pickNextCondition(RandomSource rand, List<ArtifactCondition> existingConditions) {
        List<ResourceLocation> existingIds = existingConditions.stream().map(ArtifactCondition::getId).toList();
        List<SelectableArtifactCondition> availableConditions = getInstance().getLoadedConditions().stream()
                .filter(select -> !existingIds.contains(select.condition().getId()))
                .filter(select -> !select.selector().conflictsWith(existingIds))
                .toList();
        return MiscUtil.getWeightedRandomEntry(availableConditions, rand, select -> select.selector().getWeight())
                .map(SelectableArtifactCondition::condition);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> dataMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, JsonObject> conditionObjects = MapStream.of(dataMap)
                .filterValue(JsonElement::isJsonObject)
                .mapValue(JsonElement::getAsJsonObject)
                .toMap();
        AstralSorcery.LOG.info("Loading artifact conditions with {} conditions.", conditionObjects.size());

        this.conditions.clear();
        RegistryOps<JsonElement> ops = this.getRegistryLookup().createSerializationContext(JsonOps.INSTANCE);
        conditionObjects.forEach((key, obj) -> {
            SelectableArtifactCondition.CODEC.parse(ops, obj)
                    .ifError(error -> AstralSorcery.LOG.warn("Failed to load artifact condition {}: {}", key, error.message()))
                    .ifSuccess(condition -> {
                        this.conditions.add(condition);
                        this.byTypeConditions
                                .computeIfAbsent(condition.condition().unwrapType(), t -> new HashSet<>())
                                .add(condition);
                    });
        });
    }
}
