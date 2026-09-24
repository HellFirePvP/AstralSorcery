/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.ResearchTier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.*;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResearchNodeLoader
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ResearchNodeLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final ResearchNodeLoader INSTANCE = new ResearchNodeLoader();

    private final Map<ResourceLocation, ResearchNode> nodeMap = new HashMap<>();
    private final List<ResearchNode> nodes = new ArrayList<>();
    private final Map<Item, ResearchNode> indexedLookupMap = new HashMap<>();

    private ResearchNodeLoader() {
        super(GSON, "research");
    }

    public static ResearchNodeLoader getInstance() {
        return INSTANCE;
    }

    @OnlyIn(Dist.CLIENT)
    public void updateServerNodes(List<ResearchNode> nodes) {
        this.indexedLookupMap.clear();
        this.nodeMap.clear();
        nodes.forEach(node -> this.nodeMap.put(node.getKey(), node));

        this.nodes.clear();
        this.nodes.addAll(nodes);
        nodes.forEach(node -> node.getLookupIndexItems().forEach(item -> this.indexedLookupMap.put(item, node)));
    }

    public Optional<ResearchNode> getNode(ResourceLocation key) {
        return Optional.ofNullable(this.nodeMap.get(key));
    }

    public List<ResearchNode> getNodes() {
        return Collections.unmodifiableList(this.nodes);
    }

    public Optional<ResearchNode> lookupNode(Item item) {
        return Optional.ofNullable(this.indexedLookupMap.get(item));
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> dataMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        this.nodeMap.clear();
        this.nodes.clear();
        this.indexedLookupMap.clear();

        dataMap.values().stream()
                .filter(JsonElement::isJsonObject)
                .map(JsonElement::getAsJsonObject)
                .map(obj -> ResearchNode.CODEC.parse(JsonOps.INSTANCE, obj))
                .peek(result -> {
                    if (result.error().isPresent()) {
                        AstralSorcery.LOG.error("Failed to load research node: {}", result.error().get());
                    }
                })
                .filter(DataResult::isSuccess)
                .map(DataResult::getOrThrow)
                .forEach(newNode -> {
                    this.nodeMap.put(newNode.getKey(), newNode);
                    this.nodes.add(newNode);

                    newNode.getLookupIndexItems().forEach(item -> this.indexedLookupMap.put(item, newNode));
                });
        AstralSorcery.LOG.info("Loading tome research with {} research nodes.", this.nodes.size());
    }
}
