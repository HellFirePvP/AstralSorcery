/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.focal;

import hellfirepvp.astralsorcery.common.util.TriFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocusCrystalVisualSortFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface FocusCrystalVisualSortFunction extends TriFunction<Level, BlockPos, List<BlockPos>, List<BlockPos>> {

}
