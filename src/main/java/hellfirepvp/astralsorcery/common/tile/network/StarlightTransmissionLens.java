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
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.CrystalTransmissionNode;
import hellfirepvp.astralsorcery.common.tile.TileLens;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarlightTransmissionLens
 * Created by HellFirePvP
 * Date: 24.08.2019 / 21:20
 */
public class StarlightTransmissionLens extends CrystalTransmissionNode {

    public StarlightTransmissionLens(BlockPos thisPos, CrystalAttributes attributes) {
        super(thisPos, attributes);
    }

    public StarlightTransmissionLens(BlockPos thisPos) {
        super(thisPos);
    }

    @Override
    public <T extends TileEntity> boolean updateFromTileEntity(T tile) {
        if (!(tile instanceof TileLens)) {
            return super.updateFromTileEntity(tile);
        }

        LensColorType colorType = ((TileLens) tile).getColorType();
        this.updateAdditionalLoss(colorType == null ? 1 : colorType.getFlowMultiplier());
        this.updateIgnoreBlockCollisionState(tile.getWorld(), colorType != null && colorType.doesIgnoreBlockCollision());
        return true;
    }
}
