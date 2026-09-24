/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.util.data.ChunkSectionPos;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DiminishingMultiplier
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class DiminishingMultiplier {

    private final float multiplierReGainRate;
    private final float multiplierReGainTime;
    private final float minMultiplier;
    private final float multiplierLossRate;
    private final ChunkTracker chunkTracker;
    private final RandomSource rand = RandomSource.create();

    private float computedMultiplier = 1F;
    private int lastGainServerTick = 0;

    private DiminishingMultiplier(float multiplierReGainRate,
                                  float multiplierReGainTime,
                                  float minMultiplier,
                                  float multiplierLossRate,
                                  ChunkTracker chunkTracker) {
        this.multiplierReGainRate = multiplierReGainRate;
        this.multiplierReGainTime = multiplierReGainTime;
        this.minMultiplier = minMultiplier;
        this.multiplierLossRate = multiplierLossRate;
        this.chunkTracker = chunkTracker;
    }

    public static Builder of() {
        return new Builder();
    }

    public float getMultiplier(ServerPlayer sPlayer) {
        this.compute(sPlayer);
        return this.computedMultiplier * this.chunkTracker.getChunkMultiplier(sPlayer.blockPosition());
    }

    private void compute(ServerPlayer sPlayer) {
        MinecraftServer srv = sPlayer.getServer();
        if (srv == null) return;
        int gameTick = srv.getTickCount();
        int gameTickDiff = gameTick - this.lastGainServerTick;
        this.lastGainServerTick = gameTick;

        if (gameTickDiff < 0) { //Time reset?
            this.computedMultiplier = 1F;
            return;
        }

        int diffCount = MiscUtil.roundChanced(gameTickDiff / this.multiplierReGainTime, this.rand);
        if (diffCount > 0) {
            this.computedMultiplier = Mth.clamp(this.computedMultiplier + (diffCount * this.multiplierReGainRate), this.minMultiplier, 1F);
        } else {
            this.computedMultiplier = Math.max(this.computedMultiplier - this.multiplierLossRate, this.minMultiplier);
        }
    }

    public static class Builder {

        private float multiplierReGainRate = 0.01F;
        private float multiplierReGainTime = 5;
        private float minMultiplier = 0.01F;
        private float multiplierLossRate = 0.01F;
        private ChunkTracker chunkTracker = new ChunkTracker();

        private Builder() {}

        public Builder multiplierReGainRate(float multiplierReGainRate) {
            this.multiplierReGainRate = multiplierReGainRate;
            return this;
        }

        public Builder multiplierReGainTime(float multiplierReGainTime) {
            this.multiplierReGainTime = multiplierReGainTime;
            return this;
        }

        public Builder minMultiplier(float minMultiplier) {
            this.minMultiplier = minMultiplier;
            return this;
        }

        public Builder multiplierLossRate(float multiplierLossRate) {
            this.multiplierLossRate = multiplierLossRate;
            return this;
        }

        public Builder trackChunks(int perChunkCap, int chunkCap) {
            this.chunkTracker = new TrailingChunkTracker(perChunkCap, chunkCap);
            return this;
        }

        public DiminishingMultiplier build() {
            return new DiminishingMultiplier(this.multiplierReGainRate,
                    this.multiplierReGainTime,
                    this.minMultiplier,
                    this.multiplierLossRate,
                    this.chunkTracker);
        }
    }

    private static class ChunkTracker {

        protected float getChunkMultiplier(BlockPos pos) {
            return 1F;
        }
    }

    private static class TrailingChunkTracker extends ChunkTracker {

        private final float perChunkCap;
        private final int chunkCap;

        private final Map<ChunkSectionPos, Integer> countMap = new HashMap<>();
        private final Deque<ChunkSectionPos> chunkQueue = new LinkedList<>();

        private TrailingChunkTracker(int perChunkCap, int chunkCap) {
            this.perChunkCap = perChunkCap;
            this.chunkCap = chunkCap;
        }

        @Override
        protected float getChunkMultiplier(BlockPos pos) {
            ChunkSectionPos chPos = ChunkSectionPos.of(pos);
            this.chunkQueue.remove(chPos);
            this.chunkQueue.addLast(chPos);

            if (this.chunkQueue.size() > this.chunkCap) {
                ChunkSectionPos removed = this.chunkQueue.removeFirst();
                this.countMap.remove(removed);
            }

            int count = this.countMap.getOrDefault(chPos, 0) + 1;
            this.countMap.put(chPos, count);

            return Mth.clamp(1F - (this.countMap.get(chPos) / this.perChunkCap), 0.01F, 1F);
        }
    }
}
