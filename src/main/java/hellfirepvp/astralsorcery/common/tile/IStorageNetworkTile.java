/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IStorageNetworkTile
 * Created by HellFirePvP
 * Date: 13.12.2017 / 20:23
 */
//TileEntity interface!
public interface IStorageNetworkTile {

    //Should return the actual network core this tileentity is associated with.
    //May chain onto other cores that then resolve their owner with this.
    public <T extends TileStorageCore & IStorageNetworkTile> T getAssociatedCore();

}
