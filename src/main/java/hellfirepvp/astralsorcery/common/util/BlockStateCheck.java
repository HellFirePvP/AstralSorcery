/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStateCheck
 * Created by HellFirePvP
 * Date: 17.10.2016 / 00:30
 */
public interface BlockStateCheck {

    public boolean isStateValid(World world, BlockPos pos, IBlockState state);

    public static class Block implements BlockStateCheck {

        private final net.minecraft.block.Block toCheck;

        public Block(net.minecraft.block.Block toCheck) {
            this.toCheck = toCheck;
        }

        @Override
        public boolean isStateValid(World world, BlockPos pos, IBlockState state) {
            return state.getBlock().equals(toCheck);
        }
    }

    public static class Meta implements BlockStateCheck {

        private final int toCheck;

        public Meta(int toCheck) {
            this.toCheck = toCheck;
        }

        @Override
        public boolean isStateValid(World world, BlockPos pos, IBlockState state) {
            return state.getBlock().getMetaFromState(state) == toCheck;
        }
    }
    
}
