/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.status.ChunkStatus;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ChunkUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ChunkUtil {

    public static List<ServerPlayer> getPlayersTrackingChunk(ServerLevel sLevel, BlockPos pos) {
        return getPlayersTrackingChunk(sLevel, new ChunkPos(pos));
    }

    public static List<ServerPlayer> getPlayersTrackingChunk(ServerLevel sLevel, ChunkPos pos) {
        ServerChunkCache provider = sLevel.getChunkSource();
        return provider.chunkMap.getPlayers(pos, false);
    }

    public static boolean isChunkLoaded(LevelReader level, BlockPos pos) {
        return executeWithChunk(level, pos, () -> true, false);
    }

    public static void executeWithChunk(LevelReader world, ChunkPos pos, Runnable run) {
        executeWithChunk(world, pos.getWorldPosition(), MiscUtil.nullSupplier(run));
    }

    public static void executeWithChunk(LevelReader world, BlockPos pos, Runnable run) {
        executeWithChunk(world, pos, MiscUtil.nullSupplier(run));
    }

    public static <T> T executeWithChunk(LevelReader world, BlockPos pos, Supplier<T> run) {
        return executeWithChunk(world, pos, run, (T) null);
    }

    public static <T> T executeWithChunk(LevelReader world, BlockPos pos, Supplier<T> run, T defaultValue) {
        if (world instanceof ServerLevel serverWorld) {
            ServerChunkCache provider = serverWorld.getChunkSource();
            int prev = provider.getLoadedChunksCount();
            try {
                if (serverWorld.isPositionEntityTicking(pos)) {
                    return run.get();
                }
            } finally {
                int current = provider.getLoadedChunksCount();
                if (current > prev) {
                    AstralSorcery.LOG.error("Unintended chunk loading!", new Exception());
                }
            }
        } else if (world instanceof LevelAccessor accessor) {
            ChunkSource provider = accessor.getChunkSource();
            int prev = provider.getLoadedChunksCount();
            try {
                if (provider instanceof ServerChunkCache serverChunkProvider) {
                    if (serverChunkProvider.isPositionTicking(ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4))) {
                        return run.get();
                    }
                }
                if (provider.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
                    return run.get();
                }
            } finally {
                int current = provider.getLoadedChunksCount();
                if (current > prev) {
                    AstralSorcery.LOG.error("Unintended chunk loading!", new Exception());
                }
            }
        } else {
            if (world.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, false) != null) {
                return run.get();
            }
        }
        return defaultValue;
    }

    public static <T> void executeWithChunk(LevelReader world, BlockPos pos, T obj, Consumer<T> run) {
        executeWithChunk(world, pos, MiscUtil.nullSupplier(MiscUtil.apply(run, () -> obj)));
    }

    public static <T, U> void executeWithChunk(LevelReader world, BlockPos pos, T obj, U obj1, BiConsumer<T, U> run) {
        executeWithChunk(world, pos, obj, MiscUtil.apply(run, () -> obj1));
    }

    public static <T, R> R executeWithChunk(LevelReader world, BlockPos pos, T obj, Function<T, R> run) {
        return executeWithChunk(world, pos, MiscUtil.apply(run, () -> obj));
    }

    public static <T, R> R executeWithChunk(LevelReader world, BlockPos pos, T obj, Function<T, R> run, R _default) {
        return executeWithChunk(world, pos, MiscUtil.apply(run, () -> obj), _default);
    }

}
