/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.network;

import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.CrystalPrismTransmissionNode;
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
}
