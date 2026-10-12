/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.time;

import hellfirepvp.astralsorcery.common.config.json.data.TileTickManipulationRegistry;
import hellfirepvp.astralsorcery.common.util.ChunkUtil;
import hellfirepvp.astralsorcery.common.util.EntityMatchPredicate;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TimeStopZone
 * Created by HellFirePvP
 * Date: 08.10.2026 / 15:01
 */
public class TimeStopZone {

    private final UUID id;
    private final BlockPos offset;
    private final float range;
    private final EntityMatchPredicate entityTarget;

    private boolean active = true;
    private boolean dirty = false;
    private int ticksToLive;

    private final Map<BlockPos, TickingBlockEntity> cachedTiles = new HashMap<>();
    private final Set<Integer> capturedEntities = new HashSet<>();

    public TimeStopZone(UUID id, BlockPos offset, float range, EntityMatchPredicate entityTarget, int ticksToLive) {
        this.id = id;
        this.offset = offset;
        this.range = range;
        this.entityTarget = entityTarget;
        this.ticksToLive = ticksToLive;
    }

    public Snapshot snapshot() {
        return new Snapshot(this.id, this.offset, this.range, this.cachedTiles.keySet(), this.capturedEntities);
    }

    public UUID getId() {
        return this.id;
    }

    public void setTicksToLive(int ticksToLive) {
        this.ticksToLive = ticksToLive;
    }

    public void markDirty() {
        this.dirty = true;
    }

    void clearDirty() {
        this.dirty = false;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public boolean isActive() {
        return this.active && this.ticksToLive > 0;
    }

    void levelTick(ServerLevel sLevel) {
        if (!this.isActive()) return;

        this.ticksToLive--;
        this.cachedTiles.values().removeIf(TickingBlockEntity::isRemoved);

        if (sLevel.getGameTime() % 5 == 0) {
            this.captureComponents(sLevel);
        }
    }

    private void captureComponents(ServerLevel sLevel) {
        int minChX = Mth.floor((this.offset.getX() - this.range) / 16F);
        int maxChX = Mth.ceil((this.offset.getX() + this.range) / 16F);
        int minChZ = Mth.floor((this.offset.getZ() - this.range) / 16F);
        int maxChZ = Mth.ceil((this.offset.getZ() + this.range) / 16F);

        for (int chX = minChX; chX <= maxChX; chX++) {
            for (int chZ = minChZ; chZ < maxChZ; chZ++) {
                if (ChunkUtil.isChunkLoaded(sLevel, new ChunkPos(chX, chZ))) {
                    LevelChunk chunk = sLevel.getChunk(chX, chZ);
                    if (chunk.isEmpty()) continue;

                    chunk.getBlockEntities().forEach((pos, tile) -> {
                        if (pos.closerThan(this.offset, this.range) && TileTickManipulationRegistry.getInstance().canBeAffected(tile)) {
                            this.cacheTicker(sLevel, tile).ifPresent(sLevel.blockEntityTickers::remove);
                        }
                    });
                }
            }
        }

        Set<Integer> existing = new HashSet<>(this.capturedEntities);
        Set<Integer> newEntities = new HashSet<>();
        this.capturedEntities.clear();
        Vec3 center = Vec3.atCenterOf(this.offset);
        AABB dot = new AABB(center.x, center.y, center.z, center.x, center.y, center.z).inflate(this.range * 1.05F);
        for (LivingEntity livingEntity : sLevel.getEntities(EntityTypeTest.forClass(LivingEntity.class), dot, e -> this.shouldInterceptEntityTick(sLevel, e))) {
            int entityId = livingEntity.getId();
            this.capturedEntities.add(entityId);
            TimeStopController.freeze(livingEntity);
            if (!existing.remove(entityId)) {
                newEntities.add(entityId);
            }
        }
        if (!existing.isEmpty() || !newEntities.isEmpty()) {
            this.markDirty();
        }
    }

    private Optional<TickingBlockEntity> cacheTicker(ServerLevel sLevel, BlockEntity tile) {
        return sLevel.blockEntityTickers.stream()
                .filter(entry -> entry.getPos().equals(tile.getBlockPos()))
                .findFirst()
                .map(entry -> {
                    this.cachedTiles.put(tile.getBlockPos(), entry);
                    this.markDirty();
                    return entry;
                });
    }

    public void stopEffect(ServerLevel sLevel) {
        this.cachedTiles.forEach((pos, tile) -> {
            BlockState state = sLevel.getBlockState(pos);
            if (state.getBlock() instanceof EntityBlock entityBlock && state.hasBlockEntity()) {
                BlockEntity be = entityBlock.newBlockEntity(pos, state);
                if (be != null && be.isValidBlockState(state)) {
                    BlockEntityTicker<?> ticker = state.getTicker(sLevel, be.getType());
                    if (ticker != null) {
                        sLevel.addBlockEntityTicker(tile);
                    }
                }
            }
        });

        this.cachedTiles.clear();
        this.active = false;
    }

    public boolean shouldInterceptEntityTick(ServerLevel sLevel, LivingEntity entity) {
        return this.active && this.shouldFreezeEntity(sLevel, entity);
    }

    private boolean shouldFreezeEntity(ServerLevel sLevel, LivingEntity entity) {
        if (!entity.isAlive()) return false;
        if (entity instanceof EnderDragon dragon && dragon.getPhaseManager().getCurrentPhase().getPhase() == EnderDragonPhase.DYING) {
            return false;
        }

        if (this.entityTarget.getOwnerId().map(id -> entity.getUUID().equals(id)).orElse(false)) {
            return false;
        }
        if (!this.entityTarget.doesTargetPlayers() && entity instanceof Player) {
            return false;
        }
        return this.entityTarget.getEntityPredicate().matches(sLevel, Vec3.atCenterOf(this.offset), entity);
    }

    public record Snapshot(UUID id, BlockPos offset, float range, Set<BlockPos> capturedBlockEntities, Set<Integer> capturedEntities) {

        public static final StreamCodec<RegistryFriendlyByteBuf, Snapshot> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, Snapshot::id,
                BlockPos.STREAM_CODEC, Snapshot::offset,
                ByteBufCodecs.FLOAT, Snapshot::range,
                BlockPos.STREAM_CODEC.apply(SetCodec.streamOp()), Snapshot::capturedBlockEntities,
                ByteBufCodecs.INT.apply(SetCodec.streamOp()), Snapshot::capturedEntities,
                Snapshot::new);

        public Snapshot(UUID id, BlockPos offset, float range, Set<BlockPos> capturedBlockEntities, Set<Integer> capturedEntities) {
            this.id = id;
            this.offset = offset;
            this.range = range;
            this.capturedBlockEntities = Set.copyOf(capturedBlockEntities);
            this.capturedEntities = Set.copyOf(capturedEntities);
        }

        @Override
        public Set<BlockPos> capturedBlockEntities() {
            return Collections.unmodifiableSet(this.capturedBlockEntities);
        }

        @Override
        public Set<Integer> capturedEntities() {
            return Collections.unmodifiableSet(this.capturedEntities);
        }
    }
}
