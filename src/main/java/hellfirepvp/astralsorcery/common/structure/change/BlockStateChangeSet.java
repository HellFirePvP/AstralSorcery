/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure.change;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStateChangeSet
 * Created by HellFirePvP
 * Date: 02.12.2018 / 01:09
 */
public final class BlockStateChangeSet {

    private Map<BlockPos, StateChange> changes = Maps.newHashMap();

    public final void reset() {
        this.changes.clear();
    }

    public void addChange(BlockPos pos, IBlockState oldState, IBlockState newState) {
        StateChange oldChangeSet = this.changes.get(pos);
        if (oldChangeSet != null) { // Chain changes so absolute old one is still consistent!
            this.changes.put(pos, new StateChange(pos, oldChangeSet.oldState, newState));
        } else {
            this.changes.put(pos, new StateChange(pos, oldState, newState));
        }
    }

    public boolean hasChange(BlockPos pos) {
        return this.changes.containsKey(pos);
    }

    public boolean isEmpty() {
        return this.changes.isEmpty();
    }

    public Iterable<StateChange> getChanges() {
        return Collections.unmodifiableCollection(this.changes.values());
    }

    public void readFromNBT(NBTTagCompound cmp) {
        this.changes.clear();

        NBTTagList changeList = cmp.getTagList("changeList", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < changeList.tagCount(); i++) {
            NBTTagCompound changeTag = changeList.getCompoundTagAt(i);

            BlockPos pos = NBTHelper.readBlockPosFromNBT(changeTag);
            IBlockState oldState = NBTHelper.getBlockStateFromTag(changeTag.getCompoundTag("oldState"));
            IBlockState newState = NBTHelper.getBlockStateFromTag(changeTag.getCompoundTag("newState"));
            this.changes.put(pos, new StateChange(pos, oldState, newState));
        }
    }

    public void writeToNBT(NBTTagCompound cmp) {
        NBTTagList changes = new NBTTagList();
        for (StateChange change : this.changes.values()) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTHelper.writeBlockPosToNBT(change.pos, tag);
            tag.setTag("oldState", NBTHelper.getBlockStateNBTTag(change.oldState));
            tag.setTag("newState", NBTHelper.getBlockStateNBTTag(change.newState));
            changes.appendTag(tag);
        }

        cmp.setTag("changeList", changes);
    }

    public static final class StateChange {

        public final BlockPos pos;
        public final IBlockState oldState, newState;

        private StateChange(BlockPos pos, IBlockState oldState, IBlockState newState) {
            this.pos = pos;
            this.oldState = oldState;
            this.newState = newState;
        }

    }

}
