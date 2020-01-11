/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.storage;

import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IStorageNetworkTile
 * Created by HellFirePvP
 * Date: 30.05.2019 / 14:44
 */
//TileEntity interface!
public interface IStorageNetworkTile<T extends IStorageNetworkTile<T>> extends ILocatable {

    //Should return the actual network core this tileentity is associated with.
    //May chain onto other cores that then resolve their owner with this.
    public T getAssociatedCore();

    //The world the network is in. Usually the tile's world
    public World getNetworkWorld();

    //This tile's notification of mapping or network changes
    public void receiveMappingChange(StorageNetworkHandler.MappingChange newMapping);

    @Nullable
    default public StorageNetwork getNetwork() {
        return StorageNetworkHandler.getHandler(getNetworkWorld())
                .getNetwork(getAssociatedCore().getLocationPos());

    }

    //change/redo and get data from network instead.
    //Can be adjusted to do a different lookup logic.
    default public T resolveMasterCore() {
        T assoc = getAssociatedCore();
        T next;
        while (assoc != (next = getAssociatedCore())) {
            assoc = next;
        }
        return assoc;
    }

}