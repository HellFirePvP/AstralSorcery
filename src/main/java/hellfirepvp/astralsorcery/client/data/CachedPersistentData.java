/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CachedPersistentData
 * Created by HellFirePvP
 * Date: 22.09.2018 / 14:19
 */
@SideOnly(Side.CLIENT)
public abstract class CachedPersistentData {

    private final PersistentDataManager.PersistentKey key;
    protected boolean creative = false;

    public CachedPersistentData(PersistentDataManager.PersistentKey key) {
        this.key = key;
    }

    protected final boolean save() {
        return PersistentDataManager.INSTANCE.savePersistentData(this);
    }

    //Return true to indicate 'this' has changed and added data from 'that' and might need saving
    protected abstract boolean mergeFrom(CachedPersistentData that);

    public final PersistentDataManager.PersistentKey getKey() {
        return key;
    }

    public abstract void readFromNBT(NBTTagCompound cmp);

    public abstract void writeToNBT(NBTTagCompound cmp);

    public void clearCreativeCaches() {}

    public final void setCreativeFlag() {
        creative = true;
    }

    public final void clearCreativeFlag() {
        creative = false;
        clearCreativeCaches();
    }

}
