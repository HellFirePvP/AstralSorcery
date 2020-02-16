/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import hellfirepvp.astralsorcery.common.structure.types.StructureType;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileRequiresMultiblock
 * Created by HellFirePvP
 * Date: 14.02.2020 / 18:50
 */
//Tileentity interface
public interface TileRequiresMultiblock {

    @Nullable
    public StructureType getRequiredStructureType();

}
