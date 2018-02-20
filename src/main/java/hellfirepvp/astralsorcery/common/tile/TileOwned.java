/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileOwned
 * Created by HellFirePvP
 * Date: 31.07.2016 / 10:47
 */
public class TileOwned extends TileEntitySynchronized {

    public static UUID UUID_OWNER_WORLD = UUID.fromString("7f6971c5-fb58-4519-a975-b1b5766e92d2"); //LUL

    protected UUID ownerUUID;

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        if(compound.hasKey("owner")) {
            this.ownerUUID = compound.getUniqueId("owner");
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        if(ownerUUID != null) {
            compound.setUniqueId("owner", ownerUUID);
        }
    }

}
