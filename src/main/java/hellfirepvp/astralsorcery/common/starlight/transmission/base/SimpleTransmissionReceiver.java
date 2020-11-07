/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.transmission.base;

import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.tile.base.network.TileReceiverBase;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleTransmissionReceiver
 * Created by HellFirePvP
 * Date: 05.08.2016 / 13:59
 */
public abstract class SimpleTransmissionReceiver<T extends TileReceiverBase<?>> implements ITransmissionReceiver {

    private BlockPos thisPos;
    private final Set<BlockPos> sourcesToThis = new HashSet<>();

    private boolean needsTileSync = false;

    public SimpleTransmissionReceiver(BlockPos thisPos) {
        this.thisPos = thisPos;
    }

    @Override
    public void update(World world) {
        if (this.needsTileSync) {
            T tile = getTileAtPos(world);
            if (tile != null && this.syncTileData(world, tile)) {
                this.needsTileSync = false;
            }
        }
    }

    public final void markForTileSync() {
        this.needsTileSync = true;
    }

    public abstract boolean syncTileData(World world, T tile);

    public abstract Class<T> getTileClass();

    @Override
    public BlockPos getLocationPos() {
        return thisPos;
    }

    @Override
    public void notifySourceLink(World world, BlockPos source) {
        sourcesToThis.add(source);
    }

    @Override
    public void notifySourceUnlink(World world, BlockPos source) {
        sourcesToThis.remove(source);
    }

    @Override
    public boolean notifyBlockChange(World world, BlockPos changed) {
        return false;
    }

    @Override
    public List<BlockPos> getSources() {
        return new LinkedList<>(sourcesToThis);
    }

    @Nullable
    public T getTileAtPos(World world) {
        return MiscUtils.getTileAt(world, getLocationPos(), this.getTileClass(), false);
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        this.sourcesToThis.clear();

        this.thisPos = NBTHelper.readBlockPosFromNBT(compound);
        this.needsTileSync = compound.getBoolean("needsTileSync");

        ListNBT list = compound.getList("sources", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            sourcesToThis.add(NBTHelper.readBlockPosFromNBT(list.getCompound(i)));
        }
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        NBTHelper.writeBlockPosToNBT(thisPos, compound);
        compound.putBoolean("needsTileSync", this.needsTileSync);

        ListNBT sources = new ListNBT();
        for (BlockPos source : sourcesToThis) {
            CompoundNBT comp = new CompoundNBT();
            NBTHelper.writeBlockPosToNBT(source, comp);
            sources.add(comp);
        }
        compound.put("sources", sources);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleTransmissionReceiver<?> that = (SimpleTransmissionReceiver<?>) o;
        return Objects.equals(thisPos, that.thisPos);
    }

}
