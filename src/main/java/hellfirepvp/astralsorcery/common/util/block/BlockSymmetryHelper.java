/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block;

import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockSymmetryHelper
 * Created by HellFirePvP
 * Date: 21.12.2020 / 17:59
 */
public class BlockSymmetryHelper {

    public SymmetryResult getDotSymmetry(BlockPos center, int radiusLayer) {
        List<BlockPos> layerPositions = BlockGeometry.getHollowSphere(radiusLayer + 1, radiusLayer);

        float degree = 1F;



        return null;
    }

    public static class SymmetryResult {

        private final float degree;
        private final float density;

        public SymmetryResult(float degree, float density) {
            this.degree = degree;
            this.density = density;
        }

        public float getDegree() {
            return degree;
        }

        public float getDensity() {
            return density;
        }
    }
}
