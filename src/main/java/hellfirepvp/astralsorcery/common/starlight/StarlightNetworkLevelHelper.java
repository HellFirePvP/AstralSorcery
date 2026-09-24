/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight;

import hellfirepvp.astralsorcery.common.data.level.StarlightNetworkData;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.linking.SimpleLineOfSightLinkable;
import hellfirepvp.astralsorcery.common.starlight.api.ITransmissionTickable;
import hellfirepvp.astralsorcery.common.starlight.api.TransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.api.TransmissionSourceNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.StarlightTransmissionLevelHelper;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StarlightNetworkLevelHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StarlightNetworkLevelHelper {

    private final StarlightNetworkData networkData;
    private final Level level;

    private StarlightNetworkLevelHelper(Level level) {
        this.networkData = DataAS.DOMAIN_AS.getData(level, DataAS.KEY_STARLIGHT_NETWORK_DATA);
        this.level = level;
    }

    public static StarlightNetworkLevelHelper get(Level level) {
        return new StarlightNetworkLevelHelper(level);
    }

    public Level getLevel() {
        return this.level;
    }

    public boolean hasNode(BlockPos pos) {
        return this.getNode(pos).isPresent();
    }

    public Optional<TransmissionNode> getNode(BlockPos pos) {
        StarlightNetworkData.ChunkNetworkData section = this.networkData.getSection(pos);
        if (section == null) return Optional.empty();
        return section.getNode(pos);
    }

    public Collection<TransmissionSourceNode> getSourceNodes() {
        return this.networkData.getSourceNodes();
    }

    public <N extends TransmissionNode> void createNetworkNode(TileEntityNetwork<N, ?> tile) {
        tile.getNodeProvider().asOptional().ifPresent(provider -> {
            N newNode = provider.provideNewNode(tile.getBlockPos());
            this.networkData.addTransmissionNode(newNode);

            if (newNode instanceof ITransmissionTickable tickableNode) {
                StarlightNetworkTickHelper.getInstance().addNodeUpdate(this.level, tickableNode);
            }
            tile.onNodeCreate(newNode);
        });
    }

    public void removeNetworkNode(TileEntityNetwork<?, ?> tile) {
        this.getNode(tile.getBlockPos()).ifPresent(node -> {
            this.networkData.removeTransmissionNode(tile.getBlockPos());
            if (node instanceof ITransmissionTickable tickableNode) {
                StarlightNetworkTickHelper.getInstance().removeNodeUpdate(this.level, tickableNode);
            }
            if (this.level instanceof ServerLevel sLevel) {
                StarlightTransmissionLevelHelper.getInstance().getHandler(sLevel)
                        .ifPresent(handler -> handler.notifyNodeChange(node));
            }
        });
    }

    public void notifyBlockChange(BlockPos pos) {
        int rangeSq = 16 * 16;
        this.collectAffectedNodes(pos).forEach(node -> {
            if (node instanceof SimpleLineOfSightLinkable linkableNode) {
                if (linkableNode.ignoreBlockCollisionForLinks()) return;
                double changeDistSq = node.getNodePos().distSqr(pos);
                if (changeDistSq > rangeSq) return; //Too far away from node
                linkableNode.getLinkedPositions().forEach(connection -> {
                    if (changeDistSq > connection.getDistanceSq()) return; //Too far away for this connection to matter
                    boolean linked = linkableNode.hasLineOfSight(this.level, connection.getTo());
                    if (linked != connection.canConnect()) {
                        connection.setCanConnect(linked);
                        if (this.level instanceof ServerLevel sLevel) {
                            StarlightTransmissionLevelHelper.getInstance().getHandler(sLevel)
                                    .ifPresent(handler -> handler.notifyNodeChange(node));
                        }
                        node.markDirty(this.level);
                    }
                });
            }
        });
    }

    private List<TransmissionNode> collectAffectedNodes(BlockPos pos) {
        List<TransmissionNode> nodes = new ArrayList<>();
        int y = pos.getY();
        this.networkData.getSections(pos.offset(-16, 0, -16), pos.offset(16, 0, 16)).forEach(section -> {
            section.getNodes().forEach(node -> {
                BlockPos nodePos = node.getNodePos();
                if (node instanceof SimpleLineOfSightLinkable && Math.abs(nodePos.getY() - y) <= 16) {
                    nodes.add(node);
                }
            });
        });
        return nodes;
    }
}
