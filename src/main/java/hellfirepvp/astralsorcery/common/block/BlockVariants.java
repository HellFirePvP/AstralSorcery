/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import net.minecraft.block.state.IBlockState;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockVariants
 * Created by HellFirePvP
 * Date: 31.07.2016 / 09:30
 */
public interface BlockVariants {

    public List<IBlockState> getValidStates();

    public String getStateName(IBlockState state);

    default public String getBlockName(IBlockState state) {
        return state.getBlock().getClass().getSimpleName();
    }

}
