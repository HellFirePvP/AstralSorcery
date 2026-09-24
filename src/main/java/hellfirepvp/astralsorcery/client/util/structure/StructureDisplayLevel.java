/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.structure;

import com.mojang.serialization.Lifecycle;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.ScheduledTick;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StructureDisplayLevel
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StructureDisplayLevel extends Level implements WorldGenLevel {

    private final StructureDisplayStorage storage = new StructureDisplayStorage(this);
    private final TickRateManager tickRateManager = new TickRateManager();
    private final Scoreboard scoreboard = new Scoreboard();
    private final Holder<Biome> plainsBiome;
    private final BoundingBox structureBox;
    private final ChunkSource chunkSource;

    protected StructureDisplayLevel(RegistryAccess registryAccess, BoundingBox structureBox) {
        super(LevelData.EMPTY, Level.OVERWORLD, registryAccess, resolveOverworld(registryAccess), Minecraft.getInstance()::getProfiler, true, false, 0, 0);
        this.plainsBiome = registryAccess.registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.PLAINS);
        this.structureBox = structureBox;
        this.chunkSource = new StructureDisplayLevelComponents.Chunks(this);
    }

    private static Holder<DimensionType> resolveOverworld(RegistryAccess registryAccess) {
        return registryAccess.registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow(BuiltinDimensionTypes.OVERWORLD);
    }

    public static StructureDisplayLevel createLevel(RegistryAccess registries, BoundingBox structureBox) {
        return new StructureDisplayLevel(registries, structureBox);
    }

    public static StructureDisplayLevel empty(RegistryAccess registries) {
        return createLevel(registries, new BoundingBox(BlockPos.ZERO));
    }

    public StructureDisplayStorage getStorage() {
        return this.storage;
    }

    public Holder<Biome> getDefaultBiome() {
        return this.plainsBiome;
    }

    @Override
    public boolean setBlockAndUpdate(BlockPos pos, BlockState state) {
        return this.setBlock(pos, state, Block.UPDATE_ALL);
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState newState, int flags) {
        return this.setBlock(pos, newState, flags, 512);
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft) {
        if (!this.isInWorldBounds(pos)) return false;
        this.getStorage().setBlockState(pos, state);
        return true;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        if (!this.isInWorldBounds(pos)) return Blocks.VOID_AIR.defaultBlockState();
        return this.getStorage().getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        if (!this.isInWorldBounds(pos)) return Fluids.EMPTY.defaultFluidState();
        return this.getStorage().getFluidState(pos);
    }

    @Override
    public void setBlockEntity(BlockEntity blockEntity) {
        if (!this.isInWorldBounds(blockEntity.getBlockPos())) return;
        this.getStorage().setBlockEntity(blockEntity);
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        if (!this.isInWorldBounds(pos)) return null;
        return this.getStorage().getBlockEntity(pos);
    }

    @Override
    public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {}

    @Override
    public void playSeededSound(@Nullable Player player, double x, double y, double z, Holder<SoundEvent> sound, SoundSource category, float volume, float pitch, long seed) {}

    @Override
    public void playSeededSound(@Nullable Player player, Entity entity, Holder<SoundEvent> sound, SoundSource category, float volume, float pitch, long seed) {}

    @Override
    public String gatherChunkSourceStats() {
        return "Structure Mock Level";
    }

    @Nullable
    @Override
    public Entity getEntity(int id) {
        return this.getEntities().get(id);
    }

    @Override
    public TickRateManager tickRateManager() {
        return this.tickRateManager;
    }

    @Nullable
    @Override
    public MapItemSavedData getMapData(MapId mapId) {
        return null;
    }

    @Override
    public void setMapData(MapId mapId, MapItemSavedData mapData) {}

    @Override
    public MapId getFreeMapId() {
        return new MapId(0);
    }

    @Override
    public void destroyBlockProgress(int breakerId, BlockPos pos, int progress) {}

    @Override
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    @Override
    public RecipeManager getRecipeManager() {
        return null;
    }

    @Override
    protected LevelEntityGetter<Entity> getEntities() {
        return StructureDisplayLevelComponents.Entities.EMPTY;
    }

    @Override
    public PotionBrewing potionBrewing() {
        return PotionBrewing.EMPTY;
    }

    @Override
    public void setDayTimeFraction(float dayTimeFraction) {}

    @Override
    public float getDayTimeFraction() {
        return 0;
    }

    @Override
    public float getDayTimePerTick() {
        return 0;
    }

    @Override
    public void setDayTimePerTick(float dayTimePerTick) {}

    @Override
    public long getSeed() {
        return 0;
    }

    @Override
    public ServerLevel getLevel() {
        return null;
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return NoTickList.emptyList();
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return NoTickList.emptyList();
    }

    @Override
    public ChunkSource getChunkSource() {
        return this.chunkSource;
    }

    @Override
    public void levelEvent(@Nullable Player player, int type, BlockPos pos, int data) {}

    @Override
    public void gameEvent(Holder<GameEvent> gameEvent, Vec3 pos, GameEvent.Context context) {}

    @Override
    public float getShade(Direction direction, boolean shade) {
        return 1F;
    }

    @Override
    public List<? extends Player> players() {
        return List.of();
    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int x, int y, int z) {
        return this.plainsBiome;
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return FeatureFlags.REGISTRY.allFlags();
    }

    @Override
    public int getMinBuildHeight() {
        return this.structureBox.minY();
    }

    @Override
    public int getMaxBuildHeight() {
        return this.structureBox.maxY() + 1;
    }

    @Override
    public boolean isInWorldBounds(BlockPos pos) {
        return super.isInWorldBounds(pos) && this.structureBox.isInside(pos);
    }

    private static class LevelData extends PrimaryLevelData {

        public static final LevelData EMPTY = new LevelData();

        private LevelData() {
            super(new LevelSettings(
                            AstralSorcery.key("structure_level").toString(),
                            GameType.SPECTATOR,
                            false,
                            Difficulty.PEACEFUL,
                            false,
                            new GameRules(),
                            WorldDataConfiguration.DEFAULT
                    ),
                    WorldOptions.defaultWithRandomSeed(),
                    SpecialWorldProperty.NONE,
                    Lifecycle.stable());
        }
    }

    private static class NoTickList<T> implements LevelTickAccess<T> {

        private static final NoTickList<?> INSTANCE = new NoTickList<>();

        private static <T> NoTickList<T> emptyList() {
            return MiscUtil.cast(INSTANCE);
        }

        @Override
        public boolean willTickThisTick(BlockPos pos, T type) {
            return false;
        }

        @Override
        public void schedule(ScheduledTick<T> tick) {}

        @Override
        public boolean hasScheduledTick(BlockPos pos, T type) {
            return false;
        }

        @Override
        public int count() {
            return 0;
        }
    }
}
