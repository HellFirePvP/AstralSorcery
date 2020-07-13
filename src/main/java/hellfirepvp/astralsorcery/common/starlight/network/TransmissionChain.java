/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.network;

import hellfirepvp.astralsorcery.common.block.base.BlockStarlightRecipient;
import hellfirepvp.astralsorcery.common.crystal.CrystalCalculations;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.sync.server.DataLightBlockEndpoints;
import hellfirepvp.astralsorcery.common.data.sync.server.DataLightConnections;
import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.NodeConnection;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TransmissionChain
 * Created by HellFirePvP
 * Date: 05.08.2016 / 11:18
 */
public class TransmissionChain {

    private List<ChunkPos> involvedChunks = new LinkedList<>();
    private List<LightConnection> foundConnections = new LinkedList<>();
    private Map<BlockPos, Float> remainMultiplierMap = new HashMap<>();

    private Set<BlockPos> uncheckedEndpointsBlock = new HashSet<>(); //Might be IBlockSLRecipient or just a normal block.
    private Set<BlockPos> resolvedNormalBlockPositions = new HashSet<>();
    private Set<ITransmissionReceiver> endpointsNodes = new HashSet<>(); //Safe to assume those are endpoints
    private Set<IPrismTransmissionNode> transmissionUpdateList = new HashSet<>();

    private final WorldNetworkHandler handler;
    private final IPrismTransmissionNode sourceNode;

    private TransmissionChain(WorldNetworkHandler netHandler, IPrismTransmissionNode sourceNode) {
        this.handler = netHandler;
        this.sourceNode = sourceNode;
    }

    public static void threadedBuildTransmissionChain(World world, TransmissionWorldHandler handle, IIndependentStarlightSource source, WorldNetworkHandler netHandler, BlockPos sourcePos) {
        Thread tr = new Thread(() -> {
            TransmissionChain chain = buildFromSource(netHandler, sourcePos);
            handle.threadTransmissionChainCallback(world, chain, source, netHandler, sourcePos);
            SyncDataHolder.executeServer(SyncDataHolder.DATA_LIGHT_CONNECTIONS, DataLightConnections.class, data -> {
                data.updateNewConnectionsThreaded(netHandler.getWorld().getDimension().getType(), chain.getFoundConnections());
            });
            SyncDataHolder.executeServer(SyncDataHolder.DATA_LIGHT_BLOCK_ENDPOINTS, DataLightBlockEndpoints.class, data -> {
                data.updateNewEndpoints(netHandler.getWorld().getDimension().getType(), chain.getResolvedNormalBlockPositions());
            });
        });
        tr.setName("TrChainCalculationThread");
        tr.start();
    }

    private static TransmissionChain buildFromSource(WorldNetworkHandler netHandler, BlockPos at) {
        TransmissionChain chain = new TransmissionChain(netHandler, null);

        IPrismTransmissionNode node = netHandler.getTransmissionNode(at);
        if (node != null) { //Well otherwise we don't need to do anything huh...
            chain = new TransmissionChain(netHandler, node);
            chain.recBuildChain(node, 1F, new LinkedList<>());
        }

        chain.calculateInvolvedChunks();
        chain.resolveLoadedEndpoints(netHandler.getWorld());
        return chain;
    }

    private void resolveLoadedEndpoints(World world) {
        for (BlockPos pos : uncheckedEndpointsBlock) {
            MiscUtils.executeWithChunk(world, pos, () -> {
                BlockState state = world.getBlockState(pos);
                Block b = state.getBlock();
                if (b instanceof BlockStarlightRecipient) {
                    return;
                }
                resolvedNormalBlockPositions.add(pos);
            });
        }
    }

    protected void updatePosAsResolved(World world, BlockPos pos) {
        if (uncheckedEndpointsBlock.contains(pos) && !resolvedNormalBlockPositions.contains(pos)) {
            resolvedNormalBlockPositions.add(pos);
            SyncDataHolder.executeServer(SyncDataHolder.DATA_LIGHT_BLOCK_ENDPOINTS, DataLightBlockEndpoints.class, data -> {
                data.updateNewEndpoint(world.getDimension().getType(), pos);
            });
        }
    }

    private void recBuildChain(IPrismTransmissionNode node, float lossMultiplier, LinkedList<BlockPos> prevPath) {
        if (lossMultiplier <= 0.001F) return; //No. we don't transfer a part less than 0.1% of the starlight.

        float lossPerc = CrystalCalculations.getThroughputMultiplier(node.getTransmissionProperties());
        lossPerc *= node.getAdditionalTransmissionLossMultiplier();
        List<NodeConnection<IPrismTransmissionNode>> next = node.queryNext(handler);
        float nextLoss = (lossMultiplier * lossPerc) / ((float) next.size());
        prevPath.push(node.getLocationPos());

        if (node.needsTransmissionUpdate()) {
            transmissionUpdateList.add(node);
        }

        for (NodeConnection<IPrismTransmissionNode> nextNode : next) {
            IPrismTransmissionNode trNode = nextNode.getNode();
            if (nextNode.canConnect()) {
                BlockPos nextPos = nextNode.getTo();
                addIfNonExistentConnection(node.getLocationPos(), nextPos);
                if (!prevPath.contains(nextPos)) { //Saves us from cycles. cyclic starlight transmission to a cyclic node means 100% loss.

                    //This never exceeds 1F
                    remainMultiplierMap.merge(nextPos, nextLoss, (current, newNext) -> current + newNext);

                    if (trNode != null) {
                        if (trNode instanceof ITransmissionReceiver) { //Tile endpoint
                            if (!this.endpointsNodes.contains(trNode)) {
                                this.endpointsNodes.add((ITransmissionReceiver) trNode);
                            }
                        } else {
                            recBuildChain(trNode, nextLoss, prevPath);
                        }
                    } else { //BlockPos endpoint - Check for BlockStarlightRecipient is missing here, bc chunk is/might be unloaded.
                        this.uncheckedEndpointsBlock.add(nextPos);
                    }
                }
            }
        }

        prevPath.pop();
    }

    //After calculating everything...
    private void calculateInvolvedChunks() {
        for (BlockPos nodePos : remainMultiplierMap.keySet()) {
            ChunkPos ch = new ChunkPos(nodePos);
            if (!involvedChunks.contains(ch)) involvedChunks.add(ch);
        }
    }

    public Set<BlockPos> getResolvedNormalBlockPositions() {
        return resolvedNormalBlockPositions;
    }

    public IPrismTransmissionNode getSourceNode() {
        return sourceNode;
    }

    //For rendering purposes.
    private void addIfNonExistentConnection(BlockPos start, BlockPos end) {
        LightConnection newCon = new LightConnection(start, end);
        if (!foundConnections.contains(newCon)) foundConnections.add(newCon);
    }

    public Set<IPrismTransmissionNode> getTransmissionUpdateList() {
        return transmissionUpdateList;
    }

    public List<ChunkPos> getInvolvedChunks() {
        return involvedChunks;
    }

    public Map<BlockPos, Float> getLossMultipliers() {
        return remainMultiplierMap;
    }

    public List<LightConnection> getFoundConnections() {
        return foundConnections;
    }

    public Set<ITransmissionReceiver> getEndpointsNodes() {
        return endpointsNodes;
    }

    public Set<BlockPos> getUncheckedEndpointsBlock() {
        return uncheckedEndpointsBlock;
    }

    public static class LightConnection {

        private final BlockPos start, end;

        public LightConnection(BlockPos start, BlockPos end) {
            this.start = start;
            this.end = end;
        }

        public BlockPos getStart() {
            return start;
        }

        public BlockPos getEnd() {
            return end;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LightConnection that = (LightConnection) o;
            return Objects.equals(end, that.end) && Objects.equals(start, that.start);

        }

        @Override
        public int hashCode() {
            int result = start != null ? start.hashCode() : 0;
            result = 31 * result + (end != null ? end.hashCode() : 0);
            return result;
        }
    }

}
