package hellfirepvp.astralsorcery.common.base;

import net.minecraft.block.state.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStateCheck
 * Created by HellFirePvP
 * Date: 17.10.2016 / 00:30
 */
public interface BlockStateCheck {

    public boolean isStateValid(IBlockState state);

    public static class Meta implements BlockStateCheck {

        private final int toCheck;

        public Meta(int toCheck) {
            this.toCheck = toCheck;
        }

        @Override
        public boolean isStateValid(IBlockState state) {
            return state.getBlock().getMetaFromState(state) == toCheck;
        }
    }
    
}
