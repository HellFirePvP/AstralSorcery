/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCelestialOrrery
 * Created by HellFirePvP
 * Date: 15.02.2017 / 22:46
 */
public class TileCelestialOrrery extends TileEntityTick {

    public static final double size = 2;
    public static final Color c = new Color(0x22, 0, 0x77);

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().expand(3, 5, 3);
    }

    @Override
    protected void onFirstTick() {}

}
