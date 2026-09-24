/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.transmission;

import com.mojang.datafixers.util.Pair;
import hellfirepvp.astralsorcery.common.linking.BlockLinkConnection;
import hellfirepvp.astralsorcery.common.linking.Linkable;
import hellfirepvp.astralsorcery.common.starlight.StarlightNetworkLevelHelper;
import hellfirepvp.astralsorcery.common.starlight.api.ITransmissionNotifiable;
import hellfirepvp.astralsorcery.common.starlight.api.TransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.api.TransmissionReceiverNode;
import hellfirepvp.astralsorcery.common.starlight.api.TransmissionSourceNode;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StarlightTransmissionChain
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
//A chain going from 1 source node to its endpoints and receivers, along with all chain-relevant data
public class StarlightTransmissionChain {

    //Set of all chunks involved in this transmission chain
    private final Set<ChunkPos> involvedChunks = new HashSet<>();
    //All parts involved in this chain, nodes and targets + their associated transmission loss
    private final Map<BlockPos, Float> involvedStepLossMap = new HashMap<>();

    //Resolved endpoints that are receivers
    private final Set<TransmissionReceiverNode> receiverEndpoints = new HashSet<>();
    //Endpoints that are not nodes, loaded or not
    private final Map<BlockPos, Float> blockEndpoints = new HashMap<>();

    //Nodes that need transmission updates, with their associated transmission loss
    private final Map<ITransmissionNotifiable, Float> transmissionNotifierMap = new HashMap<>();
    //Light connections for rendering and effects
    private final Set<Pair<BlockPos, BlockPos>> lightConnections = new HashSet<>();

    private StarlightTransmissionChain() {}

    public static StarlightTransmissionChain makeChain(ServerLevel level, TransmissionSourceNode sourceNode) {
        StarlightTransmissionChain chain = new StarlightTransmissionChain();
        StarlightNetworkLevelHelper helper = StarlightNetworkLevelHelper.get(level);
        chain.buildChain(helper, sourceNode, 1F, new LinkedList<>());
        chain.resolveInvolvedChunks();
        return chain;
    }

    private void resolveInvolvedChunks() {
        this.involvedChunks.clear();
        this.involvedStepLossMap.keySet().forEach(pos -> {
            this.involvedChunks.add(new ChunkPos(pos));
        });
    }

    private void buildChain(StarlightNetworkLevelHelper helper, TransmissionNode node, float lossMultiplier, LinkedList<BlockPos> prevPath) {
        if (lossMultiplier <= 1E-3F) return;

        float thisNodeLoss = lossMultiplier * node.getTransmissionLossMultiplier();

        if (node instanceof ITransmissionNotifiable notifiable) {
            this.transmissionNotifierMap.put(notifiable, thisNodeLoss);
        }

        prevPath.push(node.getNodePos());
        if (node instanceof Linkable linkableNode) {
            Collection<BlockLinkConnection> linked = linkableNode.getLinkedPositions();
            float nextLoss = thisNodeLoss / linked.size();

            linked.forEach(connection -> {
                if (connection.canConnect()) {
                    BlockPos posTo = connection.getTo();
                    Pair<BlockPos, BlockPos> pair = new Pair<>(node.getNodePos(), posTo);
                    this.lightConnections.add(pair);
                    if (prevPath.contains(posTo)) return;

                    this.involvedStepLossMap.merge(posTo, nextLoss, Float::sum);

                    TransmissionNode nextNode = helper.getNode(posTo).orElse(null);
                    if (nextNode != null) {
                        if (nextNode instanceof TransmissionReceiverNode receiverNode) {
                            this.receiverEndpoints.add(receiverNode);
                        }
                        this.buildChain(helper, nextNode, nextLoss, prevPath);
                    } else {
                        this.blockEndpoints.put(posTo, nextLoss);
                    }
                }
            });
        }
        prevPath.pop();
    }

    public Set<ChunkPos> getInvolvedChunks() {
        return Collections.unmodifiableSet(this.involvedChunks);
    }

    public Map<BlockPos, Float> getInvolvedStepLossMap() {
        return Collections.unmodifiableMap(this.involvedStepLossMap);
    }

    public Set<TransmissionReceiverNode> getReceiverEndpoints() {
        return Collections.unmodifiableSet(this.receiverEndpoints);
    }

    public Map<BlockPos, Float> getBlockEndpoints() {
        return Collections.unmodifiableMap(this.blockEndpoints);
    }

    public Map<ITransmissionNotifiable, Float> getTransmissionNotifierMap() {
        return Collections.unmodifiableMap(this.transmissionNotifierMap);
    }

    public Set<Pair<BlockPos, BlockPos>> getLightConnections() {
        return Collections.unmodifiableSet(this.lightConnections);
    }
}
