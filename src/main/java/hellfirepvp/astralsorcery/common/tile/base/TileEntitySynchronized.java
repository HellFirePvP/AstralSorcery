/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.SyncedAuxiliaryLightManager;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import hellfirepvp.observerlib.common.util.CodecUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileEntitySynchronized
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class TileEntitySynchronized<T extends TileEntitySynchronized.Data> extends BlockEntity {

    protected final RandomSource rand = RandomSource.create();

    private T data;

    protected TileEntitySynchronized(TileRegistryObject<?> type, BlockPos pos, BlockState blockState) {
        this(type.type(), pos, blockState);
    }

    protected TileEntitySynchronized(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.setTileData(this.newTileData());
    }

    protected T newTileData() {
        return this.dataCodec().parse(NbtOps.INSTANCE, new CompoundTag()).getOrThrow(str -> {
            throw new IllegalStateException(String.format("Failed to create new data for tile entity! (%s)", str));
        });
    }

    protected final void setTileData(T data) {
        this.data = data;
        this.data.tile = this;
    }

    public final T getTileData() {
        return this.data;
    }

    public abstract Codec<T> dataCodec();

    public Codec<T> netDataCodec() {
        return this.dataCodec();
    }

    public void markForUpdate() {
        if (this.getLevel() != null) {
            BlockState thisState = this.getBlockState();
            this.getLevel().sendBlockUpdated(getBlockPos(), thisState, thisState, Block.UPDATE_ALL);
        }
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.put("saveData", this.dataCodec().encodeStart(this.ops(registries), this.getTileData()).getOrThrow());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        this.dataCodec().parse(this.ops(registries), tag.get("saveData")).ifSuccess(this::setTileData);
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.put("syncData", this.netDataCodec().encodeStart(this.ops(registries), this.getTileData()).getOrThrow());
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);

        T dataPre = this.getTileData();
        CompoundTag tag = pkt.getTag();
        this.netDataCodec().parse(this.ops(lookupProvider), tag.get("syncData")).ifSuccess(newData -> {
            this.setTileData(newData);
            this.onClientDataUpdated(dataPre);
        });
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);

        T dataPre = this.getTileData();
        this.netDataCodec().parse(this.ops(lookupProvider), tag.get("syncData")).ifSuccess(newData -> {
            this.setTileData(newData);
            this.onClientDataUpdated(dataPre);
        });
    }

    private DynamicOps<Tag> ops(HolderLookup.Provider registries) {
        return registries.createSerializationContext(NbtOps.INSTANCE);
    }

    //Called after receiving tile data from server, either chunk sync or explicit tile sync
    protected void onClientDataUpdated(T previousData) {}

    //Actual removal from the chunk, not including 'just' unloading
    public void onTileEntityRemove(Level level, BlockPos pos) {}

    public void setLight(ServerLevel sLevel, int light) {
        AuxiliaryLightManager lightMgr = SyncedAuxiliaryLightManager.get(sLevel);
        if (light > 0) {
            lightMgr.setLightAt(this.getBlockPos(), light);
        } else {
            lightMgr.removeLightAt(this.getBlockPos());
        }
    }

    public static class Data {

        TileEntitySynchronized<?> tile;

        @Nullable
        protected <T extends TileEntitySynchronized<?>> T getTile() {
            return MiscUtil.cast(this.tile);
        }

        @Nullable
        protected <T extends TileEntitySynchronized<?>> T getTile(Class<T> clazzHint) {
            return CodecUtil.informedCast(this.tile, clazzHint);
        }

        @Nonnull
        protected <T extends TileEntitySynchronized<?>> Optional<T> getOptionalTile(Class<T> clazzHint) {
            if (clazzHint.isInstance(this.tile)) {
                return Optional.of(clazzHint.cast(this.tile));
            }
            return Optional.empty();
        }

        public void markDirty() {
            if (this.getTile() != null) {
                this.getTile().setChanged();
            }
        }

        public void markForUpdate() {
            if (this.getTile() != null) {
                this.getTile().markForUpdate();
            }
        }
    }
}
