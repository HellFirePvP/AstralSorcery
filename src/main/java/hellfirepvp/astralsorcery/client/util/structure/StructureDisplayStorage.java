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
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Stack;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StructureDisplayStorage
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StructureDisplayStorage {

    private final Long2ObjectMap<BlockState> states = new Long2ObjectOpenHashMap<>();
    private final Long2ObjectMap<BlockEntity> tiles = new Long2ObjectOpenHashMap<>();
    private final Level level;

    private final Stack<Predicate<BlockPos>> levelFilters = new Stack<>();

    protected StructureDisplayStorage(Level level) {
        this.level = level;
        this.states.defaultReturnValue(Blocks.AIR.defaultBlockState());
    }

    public void pushFilter(Predicate<BlockPos> filter) {
        this.levelFilters.push(filter);
    }

    public void popFilter() {
        this.levelFilters.pop();
    }

    private boolean canAccess(BlockPos pos) {
        if (this.levelFilters.isEmpty()) return true;
        for (Predicate<BlockPos> filter : this.levelFilters) {
            if (!filter.test(pos)) {
                return false;
            }
        }
        return true;
    }

    @Nonnull
    public BlockState getBlockState(BlockPos pos) {
        if (!this.canAccess(pos)) return Blocks.VOID_AIR.defaultBlockState();
        return this.states.get(pos.asLong());
    }

    @Nonnull
    public FluidState getFluidState(BlockPos pos) {
        return this.getBlockState(pos).getFluidState();
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        if (!this.canAccess(pos)) return null;
        return this.tiles.get(pos.asLong());
    }

    public BlockState setBlockState(BlockPos pos, BlockState state) {
        BlockState existing = this.getBlockState(pos);
        if (existing.equals(state)) return existing;

        existing.onRemove(this.level, pos, state, false);
        if (state.equals(Blocks.AIR.defaultBlockState())) {
            this.states.remove(pos.asLong());
        } else {
            this.states.put(pos.asLong(), state);
        }

        state.onPlace(this.level, pos, existing, false);
        this.removeBlockEntity(pos);
        if (state.hasBlockEntity() && state.getBlock() instanceof EntityBlock entityBlock) {
            BlockEntity created = entityBlock.newBlockEntity(pos, state);
            if (created != null) {
                this.setBlockEntity(created);
            }
        }
        return existing;
    }

    public void setBlockEntity(BlockEntity tile) {
        this.removeBlockEntity(tile.getBlockPos());

        if (tile.getBlockState().hasBlockEntity()) {
            tile.setLevel(this.level);
            tile.clearRemoved();
            this.tiles.put(tile.getBlockPos().asLong(), tile);
        }
    }

    public void removeBlockEntity(BlockPos pos) {
        BlockEntity removed = this.tiles.remove(pos.asLong());
        if (removed != null) {
            removed.setRemoved();
        }
    }

    static class Chunk extends EmptyLevelChunk {

        private final StructureDisplayLevel level;
        private final ChunkSection[] sections;
        private boolean needsLightUpdate = true;

        public Chunk(StructureDisplayLevel level, int chunkX, int chunkZ) {
            super(level, new ChunkPos(chunkX, chunkZ), level.getDefaultBiome());
            this.level = level;

            int sections = level.getSectionsCount();
            this.sections = new ChunkSection[sections];
            for (int y = 0; y < sections; y++) {
                this.sections[y] = new ChunkSection(level, this, y * 16);
            }
        }

        @Nullable
        @Override
        public BlockState setBlockState(BlockPos pos, BlockState state, boolean isMoving) {
            return this.level.getStorage().setBlockState(pos, state);
        }

        @Override
        public void setBlockEntity(BlockEntity blockEntity) {
            this.level.getStorage().setBlockEntity(blockEntity);
        }

        @Override
        public void removeBlockEntity(BlockPos pos) {
            this.level.getStorage().removeBlockEntity(pos);
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            return this.level.getStorage().getBlockState(pos);
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            return this.level.getStorage().getFluidState(pos);
        }

        @Override
        public FluidState getFluidState(int x, int y, int z) {
            return super.getFluidState(new BlockPos(x, y, z));
        }

        @Nullable
        @Override
        public BlockEntity getBlockEntity(BlockPos pos) {
            return this.level.getStorage().getBlockEntity(pos);
        }

        @Nullable
        @Override
        public BlockEntity getBlockEntity(BlockPos pos, EntityCreationType creationType) {
            return this.getBlockEntity(pos);
        }

        @Override
        public ChunkSection[] getSections() {
            return this.sections;
        }

        @Nullable
        @Override
        public CompoundTag getBlockEntityNbt(BlockPos pos) {
            return null;
        }

        @Nullable
        @Override
        public CompoundTag getBlockEntityNbtForSaving(BlockPos pos, HolderLookup.Provider registries) {
            return null;
        }

        @Override
        public int getLightEmission(BlockPos pos) {
            return 15;
        }

        @Override
        public boolean isLightCorrect() {
            return this.needsLightUpdate;
        }

        @Override
        public void setLightCorrect(boolean lightCorrect) {
            this.needsLightUpdate = lightCorrect;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean isUnsaved() {
            return false;
        }
    }

    static class ChunkSection extends LevelChunkSection {

        private final StructureDisplayLevel level;
        private final int x, y, z;

        public ChunkSection(StructureDisplayLevel level, Chunk parent, int y) {
            super(level.registryAccess().registryOrThrow(Registries.BIOME));
            this.level = level;
            this.x = parent.getPos().getMinBlockX();
            this.y = y;
            this.z = parent.getPos().getMinBlockZ();
        }

        @Override
        public BlockState getBlockState(int x, int y, int z) {
            return this.level.getStorage().getBlockState(new BlockPos(this.x + x, this.y + y, this.z + z));
        }

        @Override
        public FluidState getFluidState(int x, int y, int z) {
            return this.level.getStorage().getFluidState(new BlockPos(this.x + x, this.y + y, this.z + z));
        }

        @Override
        public BlockState setBlockState(int x, int y, int z, BlockState state, boolean useLocks) {
            return this.level.getStorage().setBlockState(new BlockPos(this.x + x, this.y + y, this.z + z), state);
        }
    }
}
