/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.transmission;

import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.IStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import net.minecraft.tileentity.TileEntity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ITransmissionSource
 * Created by HellFirePvP
 * Date: 03.08.2016 / 11:06
 */
public interface ITransmissionSource extends IPrismTransmissionNode {

    public IIndependentStarlightSource provideNewIndependentSource(IStarlightSource source);

    @Override
    default <T extends TileEntity> boolean updateFromTileEntity(T tile) {
        WorldNetworkHandler handle = WorldNetworkHandler.getNetworkHandler(tile.getWorld());
        IIndependentStarlightSource src = handle.getSourceAt(getLocationPos());
        if (src != null) {
            return src.updateFromTileEntity(tile);
        }
        return true;
    }
}
