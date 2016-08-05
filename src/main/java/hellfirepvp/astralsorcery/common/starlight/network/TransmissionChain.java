package hellfirepvp.astralsorcery.common.starlight.network;

import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.NodeConnection;
import hellfirepvp.astralsorcery.common.util.CrystalCalculations;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    private Map<BlockPos, Float> lossMultipliers = new HashMap<>();

    private List<BlockPos> uncheckedEndpointsBlock = new LinkedList<>(); //Might be IBlockSLRecipient or just a normal block.
    private List<ITransmissionReceiver> endpointsNodes = new LinkedList<>(); //Safe to assume those are endpoints

    private final WorldNetworkHandler handler;

    private TransmissionChain(WorldNetworkHandler netHandler) {
        this.handler = netHandler;
    }

    public static TransmissionChain buildFromSource(WorldNetworkHandler netHandler, BlockPos at) {
        TransmissionChain chain = new TransmissionChain(netHandler);

        IPrismTransmissionNode node = netHandler.getTransmissionNode(at);
        if(node != null) { //Well otherwise we don't need to do anything huh...
            chain.recBuildChain(node, 1F, new LinkedList<>());
        }

        chain.calculateInvolvedChunks();
        return chain;
    }

    private void recBuildChain(IPrismTransmissionNode node, float lossMultiplier, LinkedList<BlockPos> prevPath) {
        if(lossMultiplier <= 0.001F) return; //No. we don't transfer a part less than 0.1% of the starlight.

        CrystalProperties properties = node.getTransmissionProperties();
        lossMultiplier *= CrystalCalculations.getThroughputMultiplier(properties, lossMultiplier);
        List<NodeConnection<IPrismTransmissionNode>> next = node.queryNext(handler);
        float nextLoss = lossMultiplier / ((float) next.size());
        prevPath.push(node.getPos());

        for (NodeConnection<IPrismTransmissionNode> nextNode : next) {
            IPrismTransmissionNode trNode = nextNode.getNode();
            if(nextNode.canConnect()) {
                BlockPos nextPos = nextNode.getTo();
                addIfNonExistentConnection(node.getPos(), nextPos);
                if(!prevPath.contains(nextPos)) { //Saves us from cycles. cyclic starlight transmission to a cyclic node means 100% loss.

                    Float currentLoss = lossMultipliers.get(nextPos);
                    if (currentLoss != null) {
                        lossMultipliers.put(nextPos, currentLoss + nextLoss); //This never exceeds 1F
                    } else {
                        lossMultipliers.put(nextPos, nextLoss);
                    }

                    if(trNode != null) {
                        if(trNode instanceof ITransmissionReceiver) { //Tile endpoint
                            if(!this.endpointsNodes.contains(trNode))
                                this.endpointsNodes.add((ITransmissionReceiver) trNode);
                        } else {
                            recBuildChain(trNode, nextLoss, prevPath);
                        }
                    } else { //BlockPos endpoint - Check for IBlockStarlightRecipient is missing here, bc chunk is/might be unloaded.
                        if(!this.uncheckedEndpointsBlock.contains(nextPos))
                            this.uncheckedEndpointsBlock.add(nextPos);
                    }
                }
            }
        }

        prevPath.pop();
    }

    //After calculating everything...
    private void calculateInvolvedChunks() {
        for (BlockPos nodePos : lossMultipliers.keySet()) {
            ChunkPos ch = new ChunkPos(nodePos);
            if(!involvedChunks.contains(ch)) involvedChunks.add(ch);
        }
    }

    //For rendering purposes.
    private void addIfNonExistentConnection(BlockPos start, BlockPos end) {
        for (LightConnection con : foundConnections) {
            if(con.isSimilar(start, end)) return;
        }
        foundConnections.add(new LightConnection(start, end));
    }

    public List<ChunkPos> getInvolvedChunks() {
        return involvedChunks;
    }

    public Map<BlockPos, Float> getLossMultipliers() {
        return lossMultipliers;
    }

    public List<LightConnection> getFoundConnections() {
        return foundConnections;
    }

    public List<ITransmissionReceiver> getEndpointsNodes() {
        return endpointsNodes;
    }

    public List<BlockPos> getUncheckedEndpointsBlock() {
        return uncheckedEndpointsBlock;
    }

    public static class LightConnection {

        private final BlockPos pos1, pos2;

        public LightConnection(BlockPos pos1, BlockPos pos2) {
            this.pos1 = pos1;
            this.pos2 = pos2;
        }

        public boolean isSimilar(BlockPos pos, BlockPos otherPos) {
            return (pos.equals(pos1) && otherPos.equals(pos2)) || (pos.equals(pos2) && otherPos.equals(pos1));
        }

    }

}
