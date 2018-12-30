/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStructureObserver
 * Created by HellFirePvP
 * Date: 29.12.2018 / 15:52
 */
//Add interface to blocks to notify structure match buffer of block removals
public interface BlockStructureObserver {

    // oldState's block will be *this* block's instance!
    default boolean removeWithNewState(IBlockAccess world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return true;
    }

}
