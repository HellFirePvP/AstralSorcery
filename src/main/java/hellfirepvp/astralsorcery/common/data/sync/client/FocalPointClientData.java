/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync.client;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.data.sync.ClientData;
import hellfirepvp.astralsorcery.common.focal.node.FocalPointNode;
import hellfirepvp.astralsorcery.common.util.data.DiffEntry;
import hellfirepvp.astralsorcery.common.util.data.MapStream;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalPointClientData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocalPointClientData extends ClientData {

    private final Map<ResourceKey<Level>, Map<ChunkPos, List<FocalPointNode>>> focalPoints = new HashMap<>();

    public void receiveServerData(Map<ResourceKey<Level>, Map<ChunkPos, List<FocalPointNode>>> points) {
        this.focalPoints.clear();
        this.focalPoints.putAll(points);
    }

    public void receiveDiffData(Set<ResourceKey<Level>> levelClearBuffer,
                                Map<ResourceKey<Level>, List<DiffEntry<FocalPointNode>>> nodeChanges) {
        //Remove all levels that are unloaded & delete added and removed ones for the same level
        levelClearBuffer.forEach(levelKey -> {
            this.focalPoints.remove(levelKey);
            nodeChanges.remove(levelKey);
        });

        nodeChanges.forEach((levelKey, changes) -> {
            Map<ChunkPos, List<FocalPointNode>> nodes = this.focalPoints.computeIfAbsent(levelKey, key -> new HashMap<>());
            changes.forEach(change -> {
                FocalPointNode node = change.value();
                ChunkPos chunkPos = node.getPos().toChunkPos();
                switch (change.type()) {
                    case ADDITION -> {
                        nodes.computeIfAbsent(chunkPos, pos -> new ArrayList<>()).add(node);
                    }
                    case REMOVAL -> {
                        List<FocalPointNode> chunkNodes = nodes.get(chunkPos);
                        if (chunkNodes != null) {
                            chunkNodes.removeIf(otherNode -> otherNode.getPos().equals(node.getPos()));
                        }
                    }
                    case UPDATE -> {
                        List<FocalPointNode> chunkNodes = nodes.get(chunkPos);
                        if (chunkNodes == null) {
                            nodes.put(chunkPos, Lists.newArrayList(node));
                        } else {
                            chunkNodes.removeIf(otherNode -> otherNode.getPos().equals(node.getPos()));
                            chunkNodes.add(node);
                        }
                    }
                }
            });
        });
    }

    public List<FocalPointNode> getNodes(Level level, Vector3 pos, double distance) {
        double chunkDistance = Mth.square((distance + 16) / 16);
        ChunkPos chunkComparison = pos.toChunkPos();
        return MapStream.of(this.focalPoints.getOrDefault(level.dimension(), Collections.emptyMap()))
                .filterKey(chPos -> chPos.distanceSquared(chunkComparison) <= chunkDistance)
                .valueStream()
                .flatMap(Collection::stream)
                .toList();
    }

    public List<FocalPointNode> getNearbyFocusedNodes(Level level, Vector3 pos, double distance) {
        double distanceSq = distance * distance;
        return this.getNodes(level, pos, distance).stream()
                .filter(node -> node.getFocalPosition().isPresent())
                .filter(node -> pos.distanceSquared(node.getFocalPosition().get()) <= distanceSq)
                .toList();
    }

    @Override
    public void clear() {
        this.focalPoints.clear();
    }
}
