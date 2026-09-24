/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IBlockExtension;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BlockDynamicColor
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
//Mirror for BlockColor
public interface BlockDynamicColor extends IBlockExtension {

    int getColor(BlockState state, long tick, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex);

}
