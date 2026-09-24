/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lumen.transfer.LumenNetworkHelper;
import hellfirepvp.astralsorcery.common.util.ChunkUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.ObserverRegistryObject;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import hellfirepvp.observerlib.api.ChangeSubscriber;
import hellfirepvp.observerlib.api.ObserverHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileEntityTick
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class TileEntityTick<T extends TileEntityTick.Data> extends TileEntitySynchronized<T> {

    protected static final Tuple<BlockPos, BlockPos> SKY_CHECK_AREA =
            new Tuple<>(new BlockPos(0, 1, 0), new BlockPos(0, 1, 0));

    private ChangeSubscriber<?> structureObserver = null;
    private long lastSkyUpdateTick = -20;

    protected TileEntityTick(TileRegistryObject<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    protected void tick(Level level) {
        //By itself doesn't trigger a sync/update as server and client should tick the same amount anyway
        this.getTileData().incrementTicks();
    }

    public void serverTick(ServerLevel level) {
        this.tick(level);
    }

    @OnlyIn(Dist.CLIENT)
    public void clientTick(Level level) {
        this.tick(level);
    }

    public boolean removeBlock() {
        return this.removeBlock(this.getLevel());
    }

    public boolean removeBlock(Level level) {
        return level.setBlock(this.getBlockPos(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
    }

    @Nullable
    public ObserverRegistryObject getRequiredObserver() {
        return null;
    }

    public final Optional<ChangeSubscriber<?>> getStructureObserver() {
        return Optional.ofNullable(this.structureObserver);
    }

    protected Tuple<BlockPos, BlockPos> getSkyCheckArea() {
        return SKY_CHECK_AREA;
    }

    public boolean doesSeeSky() {
        Level level = this.getLevel();
        if (level == null) return false;

        if (level.isClientSide()) {
            return this.getTileData().hasSky();
        }

        if (this.getTileData().getTicksExisted() - this.lastSkyUpdateTick >= 20) {
            this.lastSkyUpdateTick = this.getTileData().getTicksExisted();
            boolean prevSky = this.getTileData().hasSky();

            Tuple<BlockPos, BlockPos> area = this.getSkyCheckArea();
            BlockPos.betweenClosed(area.getA(), area.getB()).forEach(pos -> {
                BlockPos actualPos = pos.offset(this.getBlockPos());
                boolean canSee = MiscUtil.canSeeSky(level, actualPos, false, this.seesSkyInNoSkyWorlds(), false);
                boolean loaded = ChunkUtil.isChunkLoaded(level, actualPos);

                boolean writeCanSee = loaded ? canSee : this.getTileData().getSkyObstructions().getOrDefault(pos, false);
                this.getTileData().setSkyObstruction(pos.immutable(), writeCanSee);
            });
            this.getTileData().markForUpdate();

            if (prevSky != this.getTileData().hasSky()) {
                this.onSkyStateChange();
            }
        }
        return this.getTileData().hasSky();
    }

    public boolean hasStructure() {
        Level level = this.getLevel();
        if (level == null) return false;

        if (level.isClientSide()) {
            return this.getTileData().hasStructure();
        }

        ObserverRegistryObject structure = this.getRequiredObserver();
        if (structure == null) {
            this.updateObserver(level);
            if (this.getTileData().hasStructure()) {
                this.getTileData().setHasStructure(false);
                this.getTileData().markForUpdate();
                this.onStructureStateChange();
            }
            return false;
        }

        this.updateObserver(level);
        if (this.structureObserver == null) {
            this.structureObserver = structure.createSubscriber(level, this.getBlockPos());
        }
        boolean matches = this.structureObserver.isValid(level);
        if (this.getTileData().hasStructure() != matches) {
            this.getTileData().setHasStructure(matches);
            this.getTileData().markForUpdate();
            this.onStructureStateChange();
        }
        return this.getTileData().hasStructure();
    }

    private void updateObserver(Level level) {
        ObserverRegistryObject observer = this.getRequiredObserver();
        if (this.structureObserver != null) {
            if (observer == null || !observer.isProviderFor(this.structureObserver)) {
                this.removeObserver(level);
            }
        }

        if (observer == null && ObserverHelper.getHelper().getSubscriber(level, this.getBlockPos()) != null) {
            this.removeObserver(level);
        }
    }

    public void removeObserver(Level level) {
        ObserverHelper.getHelper().removeObserver(level, this.getBlockPos());
        this.structureObserver = null;
    }

    //Since no-sky worlds count always as "can't see sky" even if it's exposed to the sky
    //Set to true to always count as seeing the sky in no-sky worlds.
    public boolean seesSkyInNoSkyWorlds() {
        return false;
    }

    protected void onSkyStateChange() {}

    protected void onStructureStateChange() {}

    public boolean shouldRemoveStructureObserver(BlockState currentState, BlockState newState) {
        return currentState != newState;
    }

    @Override
    public void onTileEntityRemove(Level level, BlockPos pos) {
        super.onTileEntityRemove(level, pos);

        if (!level.isClientSide()) {
            this.removeObserver(level);
            LumenNetworkHelper.getNode(level, pos).ifPresent(lumenNode -> {
                LumenNetworkHelper.removeNode(level, lumenNode);
            });
        }
    }

    public static class Data extends TileEntitySynchronized.Data {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> tickFields(inst).apply(inst, TileEntityTick.Data::new));

        protected static <T extends Data> Products.P3<RecordCodecBuilder.Mu<T>, Long, Boolean, Map<BlockPos, Boolean>> tickFields(RecordCodecBuilder.Instance<T> instance) {
            return instance.group(
                    Codec.LONG.optionalFieldOf("ticksExisted", 0L).forGetter(Data::getTicksExisted),
                    Codec.BOOL.optionalFieldOf("hasStructure", false).forGetter(Data::hasStructure),
                    CodecUtil.defaulted(Codec.unboundedMap(CodecUtil.stringBlockPos(), Codec.BOOL), "skyObstructions", HashMap::new, Data::getSkyObstructions));
        }

        private boolean hasStructure;
        private final Map<BlockPos, Boolean> skyObstructions = new HashMap<>();

        private long ticksExisted;

        protected Data(long ticksExisted, boolean hasStructure, Map<BlockPos, Boolean> skyObstructions) {
            this.ticksExisted = ticksExisted;
            this.hasStructure = hasStructure;
            this.skyObstructions.putAll(skyObstructions);
        }

        protected void incrementTicks() {
            this.ticksExisted++;
        }

        protected void setHasStructure(boolean hasStructure) {
            this.hasStructure = hasStructure;
        }

        protected void setSkyObstruction(BlockPos pos, boolean obstructed) {
            this.skyObstructions.put(pos, obstructed);
        }

        protected Map<BlockPos, Boolean> getSkyObstructions() {
            return Collections.unmodifiableMap(this.skyObstructions);
        }

        public boolean hasStructure() {
            return this.hasStructure;
        }

        public boolean hasSky() {
            return this.getSkyObstructions().values().stream().allMatch(b -> b);
        }

        public long getTicksExisted() {
            return this.ticksExisted;
        }
    }
}
