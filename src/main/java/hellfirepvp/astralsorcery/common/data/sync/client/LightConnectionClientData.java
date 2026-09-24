/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync.client;

import com.mojang.datafixers.util.Pair;
import hellfirepvp.astralsorcery.common.data.sync.ClientData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LightConnectionSyncData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LightConnectionClientData extends ClientData {

    private final Map<ResourceKey<Level>, Map<BlockPos, Set<BlockPos>>> levelLinks = new HashMap<>();

    public void receiveServerData(Map<ResourceKey<Level>, Map<BlockPos, Set<BlockPos>>> serverLinks) {
        this.levelLinks.clear();
        this.levelLinks.putAll(serverLinks);
    }

    public void receiveDiffData(Map<ResourceKey<Level>, Set<Pair<BlockPos, BlockPos>>> addedConnections,
                                Map<ResourceKey<Level>, Set<Pair<BlockPos, BlockPos>>> removedConnections,
                                Set<ResourceKey<Level>> clearedDimensions) {
        for (ResourceKey<Level> dimKey : clearedDimensions) {
            this.levelLinks.remove(dimKey);
        }
        for (Map.Entry<ResourceKey<Level>, Set<Pair<BlockPos, BlockPos>>> entry : removedConnections.entrySet()) {
            ResourceKey<Level> dimKey = entry.getKey();
            Map<BlockPos, Set<BlockPos>> dimLinks = this.levelLinks.get(dimKey);
            if (dimLinks == null) continue;

            for (Pair<BlockPos, BlockPos> conn : entry.getValue()) {
                Set<BlockPos> endpoints = dimLinks.get(conn.getFirst());
                if (endpoints != null) {
                    endpoints.remove(conn.getSecond());
                    if (endpoints.isEmpty()) {
                        dimLinks.remove(conn.getFirst());
                    }
                }
            }
        }
        for (Map.Entry<ResourceKey<Level>, Set<Pair<BlockPos, BlockPos>>> entry : addedConnections.entrySet()) {
            ResourceKey<Level> dimKey = entry.getKey();
            Map<BlockPos, Set<BlockPos>> dimLinks = this.levelLinks.computeIfAbsent(dimKey, k -> new HashMap<>());

            for (Pair<BlockPos, BlockPos> conn : entry.getValue()) {
                dimLinks.computeIfAbsent(conn.getFirst(), k -> new HashSet<>()).add(conn.getSecond());
            }
        }
    }

    @Nonnull
    public Map<BlockPos, Set<BlockPos>> getConnections(ResourceKey<Level> dim) {
        return Collections.unmodifiableMap(this.levelLinks.getOrDefault(dim, Collections.emptyMap()));
    }

    @Override
    public void clear() {
        this.levelLinks.clear();
    }
}
