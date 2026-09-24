/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.transmission;

import hellfirepvp.astralsorcery.common.data.sync.SyncDataManager;
import hellfirepvp.astralsorcery.common.lib.types.SyncDataTypesAS;
import hellfirepvp.astralsorcery.common.recipe.focal.place.ActiveTransmutationHandler;
import hellfirepvp.astralsorcery.common.starlight.StarlightNetworkLevelHelper;
import hellfirepvp.astralsorcery.common.starlight.api.TransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.api.TransmissionSourceNode;
import hellfirepvp.astralsorcery.common.util.ChunkUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StarlightTransmissionLevelHandler
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StarlightTransmissionLevelHandler {

    protected static final RandomSource rand = RandomSource.create();

    //A map to track if a chunk position has source nodes that have a transmission chain going through this specific chunk
    private final Map<ChunkPos, Set<TransmissionSourceNode>> involvedSourceMap = new HashMap<>();
    //The inverse, a mapping of a source node to its !CURRENTLY LOADED! chunk positions involved in the chain
    private final Map<TransmissionSourceNode, Set<ChunkPos>> activeSourceChunkMap = new HashMap<>();
    //Just a map to keep already calculated chains around
    private final Map<TransmissionSourceNode, StarlightTransmissionChain> sourceChains = new HashMap<>();
    //A map to keep track of which positions are involved for which source node chains, to easily resolve breaks
    private final Map<BlockPos, Set<TransmissionSourceNode>> posToSourceMap = new HashMap<>();

    private final ResourceKey<Level> dimKey;

    protected StarlightTransmissionLevelHandler(ResourceKey<Level> dimKey) {
        this.dimKey = dimKey;
    }

    protected void tick(ServerLevel sLevel) {
        StarlightNetworkLevelHelper.get(sLevel).getSourceNodes().forEach(sourceNode -> {
            if (!this.sourceChains.containsKey(sourceNode)) {
                this.createTransmissionChain(sLevel, sourceNode);
            }

            StarlightTransmissionChain sourceChain = this.sourceChains.get(sourceNode);
            if (sourceChain == null) return;

            if (this.activeSourceChunkMap.getOrDefault(sourceNode, Collections.emptySet()).isEmpty()) {
                return;
            }

            StarlightTransmissionPacket packet = sourceNode.produceStarlight(sLevel).orElse(null);
            if (packet == null) return;

            sourceChain.getReceiverEndpoints().forEach(receiverNode -> {
                Float lossMultiplier = sourceChain.getInvolvedStepLossMap().get(receiverNode.getNodePos());
                if (lossMultiplier != null) {
                    receiverNode.receiveStarlight(sLevel, packet.withMultiplier(lossMultiplier));
                }
            });

            if (packet.amount() > 1E-3F) {
                sourceChain.getTransmissionNotifierMap().forEach((notifierNode, lossMultiplier) -> {
                    if (lossMultiplier > 1E-3F) {
                        notifierNode.updateTransmission(sLevel, packet.withMultiplier(lossMultiplier));
                    }
                });
            }

            sourceChain.getBlockEndpoints().forEach((endpointPos, lossMultiplier) -> {
                if (lossMultiplier > 1E-3F) {
                    ActiveTransmutationHandler.receiveStarlight(sLevel, endpointPos, packet.withMultiplier(lossMultiplier));
                }
            });
        });
    }

    protected void createTransmissionChain(ServerLevel sLevel, TransmissionSourceNode sourceNode) {
        StarlightTransmissionChain chain = StarlightTransmissionChain.makeChain(sLevel, sourceNode);
        this.sourceChains.put(sourceNode, chain);

        Set<ChunkPos> activeChunks = new HashSet<>();
        chain.getInvolvedChunks().forEach(chPos -> {
            this.involvedSourceMap.computeIfAbsent(chPos, p -> new HashSet<>()).add(sourceNode);
            ChunkUtil.executeWithChunk(sLevel, chPos, () -> activeChunks.add(chPos));
        });
        if (!activeChunks.isEmpty()) {
            this.activeSourceChunkMap.put(sourceNode, activeChunks);
        }

        chain.getInvolvedStepLossMap().keySet().forEach(pos -> {
            this.posToSourceMap.computeIfAbsent(pos, p -> new HashSet<>()).add(sourceNode);
        });
        this.posToSourceMap.computeIfAbsent(sourceNode.getNodePos(), p -> new HashSet<>()).add(sourceNode);
        
        SyncDataManager.getInstance().getData(SyncDataTypesAS.LIGHT_CONNECTION)
                .addConnections(sLevel.dimension(), chain.getLightConnections());
    }

    public void notifyNodeChange(TransmissionNode node) {
        BlockPos pos = node.getNodePos();
        Set<TransmissionSourceNode> sources = this.posToSourceMap.get(pos);
        if (sources != null) {
            new ArrayList<>(sources).forEach(this::breakSourceChain);
        }
    }

    public void breakSourceChain(TransmissionSourceNode node) {
        StarlightTransmissionChain chain = this.sourceChains.get(node);
        if (chain != null) {
            chain.getInvolvedChunks().forEach(chPos -> {
                Set<TransmissionSourceNode> sources = this.involvedSourceMap.get(chPos);
                if (sources != null) {
                    sources.remove(node);
                    if (sources.isEmpty()) {
                        this.involvedSourceMap.remove(chPos);
                    }
                }
            });
            chain.getInvolvedStepLossMap().keySet().forEach(pos -> {
                Set<TransmissionSourceNode> sources = this.posToSourceMap.get(pos);
                if (sources != null) {
                    sources.remove(node);
                    if (sources.isEmpty()) {
                        this.posToSourceMap.remove(pos);
                    }
                }
            });
            SyncDataManager.getInstance().getData(SyncDataTypesAS.LIGHT_CONNECTION)
                    .removeConnections(this.dimKey, chain.getLightConnections());
        }
        this.activeSourceChunkMap.remove(node);
        this.sourceChains.remove(node);
    }

    protected void onChunkUnload(ChunkPos pos) {
        Set<TransmissionSourceNode> sources = this.involvedSourceMap.get(pos);
        if (sources == null) return;
        for (TransmissionSourceNode sourceNode : sources) {
            Set<ChunkPos> activeChunks = this.activeSourceChunkMap.get(sourceNode);
            if (activeChunks != null) {
                activeChunks.remove(pos);
                if (activeChunks.isEmpty()) {
                    this.activeSourceChunkMap.remove(sourceNode);
                }
            }
        }
    }

    protected void onChunkLoad(ChunkPos pos) {
        Set<TransmissionSourceNode> sources = this.involvedSourceMap.get(pos);
        if (sources == null) return;
        for (TransmissionSourceNode sourceNode : sources) {
            StarlightTransmissionChain existingChain = this.sourceChains.get(sourceNode);
            if (existingChain == null) return;
            if (existingChain.getInvolvedChunks().contains(pos)) {
                this.activeSourceChunkMap.computeIfAbsent(sourceNode, node -> new HashSet<>()).add(pos);
            }
        }
    }

    protected void clear() {
        this.involvedSourceMap.clear();
        this.activeSourceChunkMap.clear();
        this.sourceChains.clear();
        this.posToSourceMap.clear();
    }
}
