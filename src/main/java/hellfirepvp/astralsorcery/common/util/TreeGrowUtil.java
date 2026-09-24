/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TreeGrowUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TreeGrowUtil {

    private static final Map<ResourceKey<Level>, Set<Entry>> handlers = new HashMap<>();
    private static final Table<ResourceKey<Level>, ChunkPos, Set<Entry>> chunkHandlers = HashBasedTable.create();

    public static void addHandler(ServerLevel level, BlockPos pos, double range, BiPredicate<BlockGrowFeatureEvent, TreeUtil.Tree> handler) {
        Entry newEntry = new Entry(pos, range, handler);
        handlers.computeIfAbsent(level.dimension(), k -> new HashSet<>()).add(newEntry);

        int chunkRange = (int) Math.ceil(range / 16);
        ChunkPos centerChunk = new ChunkPos(pos);

        for (int dx = -chunkRange; dx <= chunkRange; dx++) {
            for (int dz = -chunkRange; dz <= chunkRange; dz++) {
                ChunkPos chunkPos = new ChunkPos(centerChunk.x + dx, centerChunk.z + dz);
                Map<ChunkPos, Set<Entry>> chunkPosMap = chunkHandlers.row(level.dimension());
                if (!chunkPosMap.containsKey(chunkPos)) chunkPosMap.put(chunkPos, new HashSet<>());
                chunkPosMap.computeIfAbsent(chunkPos, k -> new HashSet<>()).add(newEntry);
            }
        }
    }

    public static void removeHandler(ServerLevel level, BlockPos pos) {
        Entry foundEntry = null;
        Set<Entry> levelEntries = handlers.get(level.dimension());
        if (levelEntries != null) {
            for (Entry entry : levelEntries) {
                if (entry.pos.equals(pos)) {
                    foundEntry = entry;
                    break;
                }
            }
            if (foundEntry != null) {
                levelEntries.remove(foundEntry);

                int chunkRange = (int) Math.ceil(foundEntry.range / 16);
                ChunkPos centerChunk = new ChunkPos(pos);

                for (int dx = -chunkRange; dx <= chunkRange; dx++) {
                    for (int dz = -chunkRange; dz <= chunkRange; dz++) {
                        ChunkPos chunkPos = new ChunkPos(centerChunk.x + dx, centerChunk.z + dz);
                        Map<ChunkPos, Set<Entry>> chunkPosMap = chunkHandlers.row(level.dimension());
                        if (chunkPosMap.isEmpty()) continue;

                        Set<Entry> entriesInChunk = chunkPosMap.get(chunkPos);
                        if (entriesInChunk != null) {
                            entriesInChunk.remove(foundEntry);
                            if (entriesInChunk.isEmpty()) {
                                chunkPosMap.remove(chunkPos);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void attachEventListeners(IEventBus bus) {
        bus.addListener(TreeGrowUtil::onGrow);
        bus.addListener(TreeGrowUtil::onUnload);
    }

    private static void onGrow(BlockGrowFeatureEvent event) {
        if (!(event.getLevel() instanceof ServerLevel sLevel)) return;
        FlagExecutor.run(FlagExecutor.Flag.TREE_GROWTH, () -> {
            TreeUtil.wrapTree(event.getLevel(), event.getPos()).ifPresent(tree -> {
                ResourceKey<Level> dim = sLevel.dimension();
                ChunkPos chunkPos = new ChunkPos(event.getPos());

                Set<Entry> relevantEntries = chunkHandlers.get(dim, chunkPos);
                if (relevantEntries != null) {
                    for (Entry entry : relevantEntries) {
                        double distanceSq = entry.pos.distSqr(event.getPos());
                        if (distanceSq <= entry.range * entry.range) {
                            if (entry.handler.test(event, tree)) {
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            });
        });
    }

    private static void onUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level) {
            handlers.remove(level.dimension());
        }
    }

    private record Entry(BlockPos pos, double range, BiPredicate<BlockGrowFeatureEvent, TreeUtil.Tree> handler) {

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Entry entry = (Entry) o;
            return Objects.equals(pos, entry.pos);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(pos);
        }
    }
}
