/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block;

import net.minecraft.util.math.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ILocatable
 * Created by HellFirePvP
 * Date: 26.07.2017 / 21:44
 */
public interface ILocatable {

    public BlockPos getLocationPos();

    public static ILocatable fromPos(BlockPos pos) {
        return new PosLocatable(pos);
    }

    class PosLocatable implements ILocatable {

        private final BlockPos pos;

        private PosLocatable(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public BlockPos getLocationPos() {
            return pos;
        }

    }

}
