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
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.BiPredicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenNetworkHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenNetworkHelper {

    public static void removeNode(Level level, LumenNode node) {
        LumenNetworkData data = DataAS.DOMAIN_AS.getData(level, DataAS.KEY_LUMEN_NETWORK_DATA);
        node.getMutableProvidedLumenTypes().clear();
        removeProvidedTypesFromConnectedNodes(level, node);

        Map<Lumen, Set<LumenNode.RelayDistance>> relayTypes = node.getRelayedLumenTypes();

        //remove nodes with known relay distances in the direction of higher distances
        visitNodeTree(level, node.getPos(), (current, visitedNodes) -> {
            if (current.pos().equals(node.getPos())) return true; // skip current node

            return data.getLumenNode(current.pos()).map(currentNode -> {
                boolean hasAscendingDistances = false;
                for (Lumen lumen : relayTypes.keySet()) {
                    Set<LumenNode.RelayDistance> relayDistances = relayTypes.get(lumen);
                    if (relayDistances.isEmpty()) continue;
                    for (LumenNode.RelayDistance distanceNode : relayDistances) {
                        int currentRelayDistance = currentNode.getRelayDistance(lumen, distanceNode.srcPos());
                        if (currentRelayDistance == -1) continue; // Doesn't relay this lumen type

                        int nodeDistance = distanceNode.hopDistance();
                        //AstralSorcery.LOG.warn("Test Remove From {} | currentRelayDistance: {} vs nodeDistance: {}", current.pos(), currentRelayDistance, nodeDistance);
                        if (currentRelayDistance > nodeDistance) {
                            //AstralSorcery.LOG.warn(" - Removing relay {}", current.pos());
                            currentNode.removeRelay(distanceNode.srcPos());
                            data.markDirty(currentNode.getPos());
                            hasAscendingDistances = true;
                        }
                    }
                }
                return hasAscendingDistances;
            }).orElse(false);
        });

        node.getLinkedPositions().forEach(linkedPos -> {
            data.getLumenNode(linkedPos).ifPresent(linkedNode -> {
                //AstralSorcery.LOG.warn("Unlinking {} from {}", node.getPos(), linkedNode.getPos());
                linkedNode.getLinkedPositions().remove(node.getPos());
                data.markDirty(linkedNode.getPos());
            });
        });
        data.removeLumenNode(node.getPos());

        //rebuild source provider trees without this node
        node.getLinkedPositions().forEach(linkedPos -> {
            data.getLumenNode(linkedPos).ifPresent(linkedNode -> {
                List<BlockPos> foundProviderNodes = new ArrayList<>();
                visitNodeTree(level, linkedPos, (current, visitedNodes) -> {
                    data.getLumenNode(current.pos).ifPresent(currentNode -> {
                        if (!currentNode.getMutableProvidedLumenTypes().isEmpty()) {
                            foundProviderNodes.add(current.pos);
                        }
                    });
                    return true;
                });

                foundProviderNodes.forEach(providerPos -> {
                    data.getLumenNode(providerPos).ifPresent(providerNode -> {
                        visitNodeTree(level, providerPos, (current, visitedNodes) -> {
                            data.getLumenNode(current.pos()).ifPresent(currentNode -> {
                                if (currentNode.getPos().equals(providerPos)) return;

                                providerNode.getMutableProvidedLumenTypes().forEach(providedType -> {
                                    int existingHopDistance = currentNode.getRelayDistance(providedType, providerPos);
                                    int newDistance = current.distance;

                                    if (existingHopDistance > newDistance || existingHopDistance == -1) {
                                        currentNode.updateRelayType(providedType, providerPos, newDistance);
                                        data.markDirty(currentNode.getPos());
                                    }
                                });
                            });
                            return true;
                        });
                    });
                });
            });
        });

        node.getLinkedPositions().clear();
        data.markDirty(node.getPos());
    }

    /**
     * Creates a new LumenNode at the given position.
     *
     * @param level the level in which the node should be created
     * @param pos the position where the node should be created
     * @param nodePos the position of the node exactly, used for line of sight checks
     * @param type the type of the node to create
     * @return the created LumenNode, or empty if a node already exists at the position
     */
    public static Optional<LumenNode> createNode(Level level, BlockPos pos, Vec3 nodePos, LumenNode.ConnectionType type) {
        LumenNetworkData data = DataAS.DOMAIN_AS.getData(level, DataAS.KEY_LUMEN_NETWORK_DATA);
        LumenNode existing = data.getLumenNode(pos).orElse(null);
        if (existing != null) return Optional.empty();

        List<LumenNode> nearbyNodes = data.collectNearbyNodes(pos, 16);

        LumenNode newNode = new LumenNode(pos, nodePos, type);
        data.addLumenNode(pos, newNode);

        nearbyNodes.forEach(node -> {
            if (node.getPos().equals(newNode.getPos())) return;

            newNode.getLinkedPositions().add(node.getPos());
            node.getLinkedPositions().add(newNode.getPos());

            data.markDirty(node.getPos());
            data.markDirty(newNode.getPos());
        });
        cascadeNewNonProviderNode(level, newNode);
        return Optional.of(newNode);
    }

    public static Optional<LumenNode> getNode(Level level, BlockPos pos) {
        return DataAS.DOMAIN_AS.getData(level, DataAS.KEY_LUMEN_NETWORK_DATA).getLumenNode(pos);
    }

    //Updates distances of all visible nodes and updates new transient distances
    private static void cascadeNewNonProviderNode(Level level, LumenNode newNode) {
        LumenNetworkData data = DataAS.DOMAIN_AS.getData(level, DataAS.KEY_LUMEN_NETWORK_DATA);

        //Re-cascade from providers
        List<BlockPos> foundProviderNodes = new ArrayList<>();
        visitNodeTree(level, newNode.getPos(), (current, visitedNodes) -> {
            data.getLumenNode(current.pos).ifPresent(currentNode -> {
                if (!currentNode.getMutableProvidedLumenTypes().isEmpty()) {
                    foundProviderNodes.add(current.pos);
                }
            });
            return true;
        });
        //AstralSorcery.LOG.warn("Found {} provider nodes", foundProviderNodes.size());
        foundProviderNodes.forEach(providerPos -> {
            data.getLumenNode(providerPos).ifPresent(providerNode -> {
                //AstralSorcery.LOG.warn("Re-cascading from provider node {}", providerPos);
                visitNodeTree(level, providerPos, (current, visitedNodes) -> {
                    data.getLumenNode(current.pos).ifPresent(currentNode -> {
                        if (currentNode.getPos().equals(providerPos)) return; // Skip current node

                        providerNode.getMutableProvidedLumenTypes().forEach(providedType -> {
                            int existingHopDistance = currentNode.getRelayDistance(providedType, providerPos);
                            int newDistance = current.distance;

                            //No update if the node has a shorter distance already
                            if (existingHopDistance > newDistance) {
                                //AstralSorcery.LOG.warn("- Add relay {} | type: {} | newDistance: {} (was {})",
                                //        currentNode.getPos(), RegistriesAS.REGISTRY_LUMEN.getKey(providedType), newDistance, existingHopDistance);
                                currentNode.updateRelayType(providedType, providerPos, newDistance);
                                data.markDirty(currentNode.getPos());
                            } else if (existingHopDistance == -1) {
                                //AstralSorcery.LOG.warn("- Add relay {} | type: {} | newDistance: {}",
                                //        currentNode.getPos(), RegistriesAS.REGISTRY_LUMEN.getKey(providedType), newDistance);
                                currentNode.addRelayedLumenType(providedType, providerPos, newDistance);
                                data.markDirty(currentNode.getPos());
                            }
                        });
                    });
                    return true;
                });
            });
        });
    }

    public static void setNodeProvidedLumenTypes(Level level, LumenNode node, Set<Lumen> providedTypes) {
        if (node.getConnectionType() != LumenNode.ConnectionType.SOURCE) {
            return;
        }

        LumenNetworkData data = DataAS.DOMAIN_AS.getData(level, DataAS.KEY_LUMEN_NETWORK_DATA);

        node.getMutableProvidedLumenTypes().clear();
        removeProvidedTypesFromConnectedNodes(level, node);
        node.getMutableProvidedLumenTypes().addAll(providedTypes);

        data.markDirty(node.getPos());

        visitNodeTree(level, node.getPos(), (current, visitedNodes) -> {
            data.getLumenNode(current.pos()).ifPresent(currentNode -> {
                providedTypes.forEach(lumen -> {
                    //AstralSorcery.LOG.warn("Add relay {} | type: {} | distance: {}",
                    //        current.pos(), RegistriesAS.REGISTRY_LUMEN.getKey(lumen), current.distance);
                    currentNode.addRelayedLumenType(lumen, node.getPos(), current.distance);
                });
                data.markDirty(current.pos());
            });
            return true;
        });
    }

    private static void removeProvidedTypesFromConnectedNodes(Level level, LumenNode node) {
        LumenNetworkData data = DataAS.DOMAIN_AS.getData(level, DataAS.KEY_LUMEN_NETWORK_DATA);

        visitNodeTree(level, node.getPos(), (current, visitedNodes) -> {
            data.getLumenNode(current.pos()).ifPresent(currentNode -> {
                currentNode.removeRelay(node.getPos());
                data.markDirty(current.pos());
            });
            return true;
        });
    }

    private static void visitNodeTree(Level level, BlockPos start, BiPredicate<StepPos, Map<Integer, Set<BlockPos>>> visitor) {
        LumenNetworkData data = DataAS.DOMAIN_AS.getData(level, DataAS.KEY_LUMEN_NETWORK_DATA);

        Set<BlockPos> visited = new HashSet<>();
        Map<Integer, Set<BlockPos>> visitedDistance = new HashMap<>();

        Deque<StepPos> queue = new LinkedList<>();
        queue.addLast(new StepPos(start, 0));

        while (!queue.isEmpty()) {
            StepPos current = queue.removeFirst();
            if (visited.contains(current.pos)) continue;
            visited.add(current.pos);
            //AstralSorcery.LOG.warn("Visiting node {} (distance {})", current.pos, current.distance);

            LumenNode currentNode = data.getLumenNode(current.pos).orElse(null);
            if (currentNode == null) continue;
            visitedDistance.computeIfAbsent(current.distance, dst -> new HashSet<>()).add(current.pos);

            if (!visitor.test(current, visitedDistance)) {
                continue; // stop visiting further
            }
            if (!currentNode.getPos().equals(start) && currentNode.getConnectionType() != LumenNode.ConnectionType.TRANSMISSION) {
                continue; // stop visiting further, as this is a endpoint
            }
            for (BlockPos linkedPos : currentNode.getLinkedPositions()) {
                if (!visited.contains(linkedPos)) {
                    //AstralSorcery.LOG.warn("Queueing node {} from {} (distance {})", linkedPos, current.pos, current.distance + 1);
                    queue.addLast(new StepPos(linkedPos, current.distance + 1));
                }
            }
        }
    }

    private record StepPos(BlockPos pos, int distance) {}
}
