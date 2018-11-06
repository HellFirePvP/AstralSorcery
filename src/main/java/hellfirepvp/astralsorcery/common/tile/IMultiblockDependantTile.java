/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.util.ILocatable;
import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IMultiblockDependantTile
 * Created by HellFirePvP
 * Date: 31.10.2017 / 15:51
 */
public interface IMultiblockDependantTile extends ILocatable {

    //'this' tile needs to be centered on that structure
    @Nullable
    public PatternBlockArray getRequiredStructure();

}
