/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.network;

import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.item.lens.LensColorType;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightTransmissionHandler;
import hellfirepvp.astralsorcery.common.starlight.network.TransmissionWorldHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.CrystalPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionProvider;
import hellfirepvp.astralsorcery.common.tile.TilePrism;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarlightTransmissionPrism
 * Created by HellFirePvP
 * Date: 24.08.2019 / 23:16
 */
public class StarlightTransmissionPrism extends CrystalPrismTransmissionNode {

    public StarlightTransmissionPrism(BlockPos thisPos, CrystalAttributes attributes) {
        super(thisPos, attributes);
    }

    public StarlightTransmissionPrism(BlockPos thisPos) {
        super(thisPos);
    }

    @Override
    public <T extends TileEntity> boolean updateFromTileEntity(T tile) {
        if (!(tile instanceof TilePrism)) {
            return super.updateFromTileEntity(tile);
        }

        LensColorType colorType = ((TilePrism) tile).getColorType();
        if (this.updateAdditionalLoss(colorType == null ? 0 : colorType.getFlowMultiplier())) {
            TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance().getWorldHandler(tile.getWorld());
            if (handle != null) {
                handle.notifyTransmissionNodeChange(this);
            }
        }
        this.updateIgnoreBlockCollisionState(tile.getWorld(), colorType != null && colorType.doesIgnoreBlockCollision());
        return true;
    }

    @Override
    public TransmissionProvider getProvider() {
        return new Provider();
    }

    public static class Provider extends TransmissionProvider {

        @Override
        public IPrismTransmissionNode get() {
            return new StarlightTransmissionPrism(null);
        }

    }
}
