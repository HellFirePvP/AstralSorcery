/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.transfer;

import hellfirepvp.astralsorcery.common.data.level.LumenNetworkData;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.lumen.ILumenHandler;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import hellfirepvp.astralsorcery.common.util.RayTraceUtil;
import hellfirepvp.astralsorcery.common.util.data.BiDiPair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.*;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenRequestHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenRequestHelper {

    public static Optional<LumenRequestChain> requestDirect(Level level, LumenNode requestor, LumenStack lumen) {
        return requestDirect(level, requestor, lumen, node -> true);
    }

    public static Optional<LumenRequestChain> requestDirect(Level level, LumenNode requestor, LumenStack lumen, Predicate<LumenNode> nodeFilter) {
        if (lumen.isEmpty()) return Optional.empty();

        Lumen type = lumen.getLumen();
        LumenNetworkData data = DataAS.DOMAIN_AS.getData(level, DataAS.KEY_LUMEN_NETWORK_DATA);

        TraversalContext ctx = new TraversalContext();
        for (BlockPos linkedPos : requestor.getLinkedPositions()) {
            LumenNode linkedNode = data.getLumenNode(linkedPos)
                    .filter(node -> node.getConnectionType() == LumenNode.ConnectionType.SOURCE)
                    .filter(node -> node.getProvidedLumenTypes().contains(type))
                    .orElse(null);
            if (linkedNode != null && canProvideLumen(level, linkedNode, lumen) && nodeFilter.test(linkedNode) && ctx.canConnect(level, requestor, linkedNode)) {
                LumenRequestChain chain = new LumenRequestChain(requestor.getPos(), linkedNode);
                chain.appendNode(requestor);
                chain.appendNode(linkedNode);
                return Optional.of(chain);
            }
        }
        return Optional.empty();
    }

    public static Optional<LumenRequestChain> requestRelayed(Level level, LumenNode requestor, LumenStack lumen) {
        return requestRelayed(level, requestor, lumen, node -> true);
    }

    public static Optional<LumenRequestChain> requestRelayed(Level level, LumenNode requestor, LumenStack lumen, Predicate<LumenNode> nodeFilter) {
        if (lumen.isEmpty()) return Optional.empty();

        Lumen type = lumen.getLumen();
        LumenNetworkData data = DataAS.DOMAIN_AS.getData(level, DataAS.KEY_LUMEN_NETWORK_DATA);
        List<LumenNode> nodes = requestor.getLinkedPositions().stream()
                .flatMap(linkedPos -> data.getLumenNode(linkedPos).stream())
                .filter(node -> node.doesRelayLumenType(type))
                .sorted(Comparator.comparing(node -> node.getMinRelayDistance(type).orElse(Integer.MAX_VALUE)))
                .toList();
        if (nodes.isEmpty()) return Optional.empty();

        return requestListedRelayed(level, requestor.getPos(), nodes, lumen, nodeFilter);
    }

    public static Optional<LumenRequestChain> requestRelayed(Level level, BlockPos requestPos, LumenStack lumen) {
        return requestRelayed(level, requestPos, lumen, node -> true);
    }

    public static Optional<LumenRequestChain> requestRelayed(Level level, BlockPos requestPos, LumenStack lumen, Predicate<LumenNode> nodeFilter) {
        if (lumen.isEmpty()) return Optional.empty();

        Lumen type = lumen.getLumen();
        LumenNetworkData data = DataAS.DOMAIN_AS.getData(level, DataAS.KEY_LUMEN_NETWORK_DATA);
        List<LumenNode> nodes = data.collectNearbyNodes(requestPos, 16).stream()
                .filter(node -> node.doesRelayLumenType(type))
                .sorted(Comparator.comparing(node -> node.getMinRelayDistance(type).orElse(Integer.MAX_VALUE)))
                .toList();
        if (nodes.isEmpty()) return Optional.empty();

        return requestListedRelayed(level, requestPos, nodes, lumen, nodeFilter);
    }

    private static Optional<LumenRequestChain> requestListedRelayed(Level level, BlockPos requestPos, List<LumenNode> nodes, LumenStack lumen, Predicate<LumenNode> nodeFilter) {
        if (lumen.isEmpty()) return Optional.empty();

        Lumen type = lumen.getLumen();
        TraversalContext ctx = new TraversalContext();
        for (LumenNode startNode : nodes) {
            if (!ctx.canConnect(level, requestPos, startNode)) continue;

            StepChain chain = searchTree(level, startNode, ctx, type, nodeFilter);
            if (chain != null) {
                LumenRequestChain requestChain = new LumenRequestChain(requestPos, chain.node());
                for (LumenNode node : chain.chain()) {
                    requestChain.appendNode(node);
                }
                return Optional.of(requestChain);
            }
        }
        return Optional.empty();
    }

    private static StepChain searchTree(Level level, LumenNode start, TraversalContext ctx, Lumen lumen, Predicate<LumenNode> nodeFilter) {
        LumenNetworkData data = DataAS.DOMAIN_AS.getData(level, DataAS.KEY_LUMEN_NETWORK_DATA);

        Set<BlockPos> visited = new HashSet<>();

        Deque<StepChain> queue = new ArrayDeque<>();
        queue.push(new StepChain(start, List.of(start)));

        while (!queue.isEmpty()) {
            StepChain stepChain = queue.removeFirst();
            visited.add(stepChain.node().getPos());

            LumenNode currentNode = stepChain.node();
            if (canProvideLumen(level, currentNode, lumen) && nodeFilter.test(currentNode)) {
                return stepChain;
            }
            if (currentNode.getConnectionType() == LumenNode.ConnectionType.SOURCE) {
                continue; // stop searching, don't visit further from a source node
            }

            List<LumenNode> linkedNodes = currentNode.getLinkedPositions().stream()
                    .map(data::getLumenNode)
                    .flatMap(Optional::stream)
                    .filter(lumenNode -> lumenNode.getConnectionType() != LumenNode.ConnectionType.RECEIVER)
                    .filter(linkedNode -> linkedNode.doesRelayLumenType(lumen))
                    .sorted(Comparator.comparing(node -> node.getMinRelayDistance(lumen).orElse(Integer.MAX_VALUE)))
                    .toList();

            for (LumenNode linkedNode : linkedNodes) {
                if (!visited.contains(linkedNode.getPos()) && ctx.canConnect(level, currentNode, linkedNode)) {
                    List<LumenNode> chain = new ArrayList<>(stepChain.chain());
                    chain.add(linkedNode);
                    queue.addLast(new StepChain(linkedNode, chain));
                }
            }
        }

        return null;
    }

    private static boolean canProvideLumen(Level level, LumenNode node, Lumen lumen) {
        return canProvideLumen(level, node, lumen.stack(100));
    }

    private static boolean canProvideLumen(Level level, LumenNode node, LumenStack lumenStack) {
        if (!node.getProvidedLumenTypes().contains(lumenStack.getLumen())) return false;
        ILumenHandler handler = level.getCapability(ILumenHandler.BLOCK, node.getPos(), null);
        return handler != null && !handler.drain(lumenStack, ILumenHandler.Action.SIMULATE).isEmpty();
    }

    private record StepChain(LumenNode node, List<LumenNode> chain) {}

    private static class TraversalContext {

        private final Map<BiDiPair<BlockPos, BlockPos>, Boolean> connectState = new HashMap<>();

        public boolean canConnect(Level level, BlockPos from, LumenNode to) {
            BiDiPair<BlockPos, BlockPos> pair = new BiDiPair<>(from, to.getPos());
            return this.connectState.computeIfAbsent(pair, p -> {
                ClipContext ctx = new ClipContext(from.getCenter(), to.getLineOfSightPos(),
                        ClipContext.Block.VISUAL, ClipContext.Fluid.WATER,
                        CollisionContext.empty());
                return RayTraceUtil.clip(level, ctx, Set.of(from, to.getPos())).getType() == HitResult.Type.MISS;
            });
        }

        public boolean canConnect(Level level, LumenNode from, LumenNode to) {
            BiDiPair<BlockPos, BlockPos> pair = new BiDiPair<>(from.getPos(), to.getPos());
            return this.connectState.computeIfAbsent(pair, p -> from.canConnect(level, to));
        }
    }
}
