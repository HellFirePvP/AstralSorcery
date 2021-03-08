/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.altar;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundNBT;

import java.util.Deque;
import java.util.LinkedList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DeferredStarlightStorage
 * Created by HellFirePvP
 * Date: 10.01.2021 / 11:45
 */
public class DeferredStarlightStorage {

    private final Deque<Integer> starlightStorage = new LinkedList<>();
    private final int ticksDeferred;

    public DeferredStarlightStorage(int ticksDeferred) {
        Preconditions.checkArgument(ticksDeferred > 0, "DeferredStarlightStorage must be set to defer by at least 1 tick.");
        this.ticksDeferred = ticksDeferred;
    }

    public void setStoredStarlight(int starlight) {
        while (this.starlightStorage.size() >= this.ticksDeferred) {
            this.starlightStorage.removeLast();
        }
        this.starlightStorage.addFirst(starlight);
    }

    public int getStoredStarlight() {
        if (this.starlightStorage.isEmpty()) {
            return 0;
        }
        return starlightStorage.getLast();
    }

    public void readNBT(CompoundNBT compound) {
        this.starlightStorage.clear();
        this.starlightStorage.addLast(compound.getInt("starlightStorage"));
    }

    public void writeNBT(CompoundNBT compound) {
        compound.putInt("starlightStorage", this.getStoredStarlight());
    }
}
