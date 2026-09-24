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
import hellfirepvp.astralsorcery.common.starlight.StarlightNetworkLevelHelper;
import hellfirepvp.astralsorcery.common.starlight.api.TransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.api.provider.TransmissionNodeProvider;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileEntityNetwork
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class TileEntityNetwork<N extends TransmissionNode, T extends TileEntityNetwork.Data> extends TileEntityTick<T> {

    @Nullable
    private N networkNode = null;
    private boolean networkNodeCreated = false;

    protected TileEntityNetwork(TileRegistryObject<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public Optional<N> getNetworkNode() {
        if (this.networkNode != null) {
            if (!this.networkNode.getNodePos().equals(getBlockPos())) {
                this.networkNode = null;
            }
        }
        if (this.networkNode == null) {
            StarlightNetworkLevelHelper.get(this.getLevel()).getNode(this.getBlockPos()).ifPresent(node -> {
                this.networkNode = MiscUtil.cast(node);
            });
        }
        return Optional.ofNullable(this.networkNode);
    }

    @Override
    public void serverTick(ServerLevel level) {
        if (!this.networkNodeCreated) {
            StarlightNetworkLevelHelper slHelper = StarlightNetworkLevelHelper.get(level);
            if (!slHelper.hasNode(this.getBlockPos())) {
                slHelper.createNetworkNode(this);
            }
            this.networkNodeCreated = true;
        }
        if (this.getTileData().needsNetworkSync()) {
            this.getNetworkNode()
                    .filter(node -> node.updateFromTileEntity(this))
                    .ifPresent(node -> {
                        this.getTileData().markForUpdate();
                        this.getTileData().setNeedsNetworkSync(false);
                    });
        }
        super.serverTick(level);
    }

    @Override
    public void setChanged() {
        this.setChanged(true);
    }

    public void setChanged(boolean updateNetworkNode) {
        this.getTileData().setNeedsNetworkSync(updateNetworkNode);
        super.setChanged();
    }

    @Override
    public void markForUpdate() {
        this.markForUpdate(true);
    }

    public void markForUpdate(boolean updateNetworkNode) {
        if (this.getLevel() != null) {
            BlockState thisState = this.getBlockState();
            this.getLevel().sendBlockUpdated(getBlockPos(), thisState, thisState, Block.UPDATE_ALL);
        }
        this.setChanged(updateNetworkNode);
    }

    @Override
    public void onTileEntityRemove(Level level, BlockPos pos) {
        super.onTileEntityRemove(level, pos);

        if (!level.isClientSide()) {
            StarlightNetworkLevelHelper.get(level).removeNetworkNode(this);
            this.networkNode = null;
            this.networkNodeCreated = false;
        }
    }

    public abstract DeferredHolder<TransmissionNodeProvider<?>, ? extends TransmissionNodeProvider<N>> getNodeProvider();

    public void onNodeCreate(N newNode) {}

    public static class Data extends TileEntityTick.Data {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> netFields(inst).apply(inst, Data::new));

        protected static <T extends Data> Products.P4<RecordCodecBuilder.Mu<T>, Long, Boolean, Map<BlockPos, Boolean>, Boolean> netFields(RecordCodecBuilder.Instance<T> instance) {
            return TileEntityTick.Data.tickFields(instance).and(
                    Codec.BOOL.optionalFieldOf("needsNetworkSync", false).forGetter(Data::needsNetworkSync)
            );
        }

        private boolean needsNetworkSync;

        protected Data(long ticksExisted, boolean hasStructure, Map<BlockPos, Boolean> skyObstructions, boolean needsNetworkSync) {
            super(ticksExisted, hasStructure, skyObstructions);
            this.needsNetworkSync = needsNetworkSync;
        }

        public boolean needsNetworkSync() {
            return this.needsNetworkSync;
        }

        protected void setNeedsNetworkSync(boolean needsNetworkSync) {
            this.needsNetworkSync = needsNetworkSync;
        }

        @Override
        public void markDirty() {
            this.markDirty(true);
        }

        public void markDirty(boolean updateNetworkNode) {
            if (this.getTile() instanceof TileEntityNetwork<?, ?> networkTile) {
                networkTile.setChanged(updateNetworkNode);
            } else {
                super.markDirty();
            }
        }

        @Override
        public void markForUpdate() {
            this.markForUpdate(true);
        }

        public void markForUpdate(boolean updateNetworkNode) {
            if (this.getTile() instanceof TileEntityNetwork<?, ?> networkTile) {
                networkTile.markForUpdate(updateNetworkNode);
            } else {
                super.markForUpdate();
            }
        }
    }
}
