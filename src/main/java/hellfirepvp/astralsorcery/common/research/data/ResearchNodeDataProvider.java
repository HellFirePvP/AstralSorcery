/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research.data;

import hellfirepvp.astralsorcery.common.research.ResearchNode;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResearchNodeDataProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ResearchNodeDataProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;
    protected final CompletableFuture<HolderLookup.Provider> registries;

    public ResearchNodeDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "research");
        this.registries = registries;
    }

    public abstract void registerResearchNodes(HolderLookup.Provider registryLookup, Consumer<ResearchNode> registrar);

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return this.registries.thenCompose(provider -> {
            Set<ResourceLocation> seenNodeNames = new HashSet<>();
            List<CompletableFuture<?>> nodesRegisters = new ArrayList<>();

            List<ResearchNode> collected = new ArrayList<>();
            this.registerResearchNodes(provider, collected::add);

            collected.forEach(node -> {
                if (!seenNodeNames.add(node.getKey())) {
                    throw new IllegalStateException("Duplicate research node: " + node.getKey());
                }

                Path path = this.pathProvider.json(node.getKey());
                nodesRegisters.add(DataProvider.saveStable(output, provider, ResearchNode.CODEC, node, path));
            });

            return CompletableFuture.allOf(nodesRegisters.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "ResearchNodes";
    }
}
