/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.structure;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StructureDisplayLevelComponents
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StructureDisplayLevelComponents {

    private StructureDisplayLevelComponents() {}

    static class Entities implements LevelEntityGetter<Entity> {

        static final Entities EMPTY = new Entities();

        @Nullable
        @Override
        public Entity get(int id) {
            return null;
        }

        @Nullable
        @Override
        public Entity get(UUID uuid) {
            return null;
        }

        @Override
        public Iterable<Entity> getAll() {
            return Collections.emptyList();
        }

        @Override
        public <U extends Entity> void get(EntityTypeTest<Entity, U> test, AbortableIterationConsumer<U> consumer) {}

        @Override
        public void get(AABB boundingBox, Consumer<Entity> consumer) {}

        @Override
        public <U extends Entity> void get(EntityTypeTest<Entity, U> test, AABB bounds, AbortableIterationConsumer<U> consumer) {}
    }

    static class Chunks extends ChunkSource {

        private final StructureDisplayLevel level;
        private final LevelLightEngine lightEngine;
        private final Long2ObjectMap<LevelChunk> storedChunks = new Long2ObjectOpenHashMap<>();

        Chunks(StructureDisplayLevel level) {
            this.level = level;
            this.lightEngine = new FullBrightLightEngine(level);
        }

        @Nullable
        @Override
        public ChunkAccess getChunk(int x, int z, ChunkStatus chunkStatus, boolean requireChunk) {
            return this.getChunk(x, z);
        }

        @Nullable
        @Override
        public LevelChunk getChunk(int chunkX, int chunkZ, boolean load) {
            return this.getChunk(chunkX, chunkZ);
        }

        private LevelChunk getChunk(int chunkX, int chunkZ) {
            long pos = ChunkPos.asLong(chunkX, chunkZ);
            return this.storedChunks.computeIfAbsent(pos, packedPos -> new StructureDisplayStorage.Chunk(this.level, chunkX, chunkZ));
        }

        @Override
        public void tick(BooleanSupplier hasTimeLeft, boolean tickChunks) {}

        @Override
        public String gatherStats() {
            return "";
        }

        @Override
        public int getLoadedChunksCount() {
            return this.storedChunks.size();
        }

        @Override
        public LevelLightEngine getLightEngine() {
            return this.lightEngine;
        }

        @Override
        public BlockGetter getLevel() {
            return this.level;
        }
    }

    static class FullBrightLightEngine extends LevelLightEngine {

        public FullBrightLightEngine(BlockGetter level) {
            super(new LightChunkGetter() {
                @Nullable
                @Override
                public LightChunk getChunkForLighting(int chunkX, int chunkZ) {
                    return null;
                }

                @Override
                public BlockGetter getLevel() {
                    return level;
                }
            }, true, true);
        }
    }
}
