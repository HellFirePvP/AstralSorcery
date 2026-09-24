/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.helper;

import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ClientBlockDestroyProgressHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ClientBlockDestroyProgressHelper {

    private static final Map<BreakOriginReference, List<BlockDestructionProgress>> adjacentProgressMap = new HashMap<>();
    private static final Map<Integer, BreakOriginReference> knownBreakProgress = new HashMap<>();

    public static void addBreakProgress(int entityId, BlockPos originPos, BlockPos breakPos, int progress) {
        if (progress < 0) return;
        LevelRenderer renderer = Minecraft.getInstance().levelRenderer;
        BreakOriginReference ref = new BreakOriginReference(entityId, originPos);

        // If we get new progress about a position that previously didn't have progress, reset all adjacent progress.
        BreakOriginReference knownRef = knownBreakProgress.get(entityId);
        if (knownRef != null && !knownRef.equals(ref)) {
            List<BlockDestructionProgress> adjacentProgress = adjacentProgressMap.get(knownRef);
            if (adjacentProgress != null) {
                adjacentProgress.forEach(prog -> removeProgress(renderer, prog));
            }
            adjacentProgressMap.remove(knownRef);
        }
        knownBreakProgress.put(entityId, ref);

        //If the level renderer has different progress for the entity than what we get progress for, reset all adjacent progress.
        BlockDestructionProgress existingProgress = renderer.destroyingBlocks.get(entityId);
        if (existingProgress != null && !existingProgress.getPos().equals(originPos)) {
            List<BlockDestructionProgress> adjacentProgress = adjacentProgressMap.get(ref);
            if (adjacentProgress != null) {
                adjacentProgress.forEach(prog -> removeProgress(renderer, prog));
            }
            adjacentProgressMap.remove(ref);
        }

        BlockDestructionProgress newProgress = new BlockDestructionProgress(entityId, breakPos);
        newProgress.updateTick(renderer.getTicks());
        newProgress.setProgress(progress);

        List<BlockDestructionProgress> progresses = adjacentProgressMap.computeIfAbsent(ref, k -> new ArrayList<>());
        progresses.add(newProgress);
        updateProgress(renderer, newProgress);
    }

    private static void updateProgress(LevelRenderer renderer, BlockDestructionProgress progress) {
        SortedSet<BlockDestructionProgress> progresses = renderer.destructionProgress
                .computeIfAbsent(progress.getPos().asLong(), packedPos -> Sets.newTreeSet());

        progresses.remove(progress);
        progresses.add(progress);
    }

    private static void removeProgress(LevelRenderer renderer, BlockDestructionProgress progress) {
        long packedPos = progress.getPos().asLong();
        Set<BlockDestructionProgress> set = renderer.destructionProgress.get(packedPos);
        if (set != null) {
            set.remove(progress);
            if (set.isEmpty()) {
                renderer.destructionProgress.remove(packedPos);
            }
        }
    }

    public static void removeBreakProgress(int entityId, BlockPos originPos) {
        LevelRenderer renderer = Minecraft.getInstance().levelRenderer;
        BreakOriginReference ref = new BreakOriginReference(entityId, originPos);
        List<BlockDestructionProgress> adjacentProgress = adjacentProgressMap.get(ref);
        if (adjacentProgress != null) {
            adjacentProgress.forEach(prog -> removeProgress(renderer, prog));
        }
        adjacentProgressMap.remove(ref);
        knownBreakProgress.remove(entityId);
    }

    private record BreakOriginReference(int entityId, BlockPos originPos) {

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            BreakOriginReference that = (BreakOriginReference) o;
            return entityId == that.entityId && Objects.equals(originPos, that.originPos);
        }

        @Override
        public int hashCode() {
            return Objects.hash(entityId, originPos);
        }
    }
}
