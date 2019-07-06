/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe;

import hellfirepvp.astralsorcery.common.crafting.helper.RecipeCraftingContext;
import hellfirepvp.astralsorcery.common.tile.TileWell;
import hellfirepvp.astralsorcery.common.util.tile.TileInventory;
import net.minecraftforge.items.IItemHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WellLiquefactionContext
 * Created by HellFirePvP
 * Date: 01.07.2019 / 00:04
 */
public class WellLiquefactionContext extends RecipeCraftingContext<WellLiquefaction, IItemHandler> {

    private final TileWell tileWell;

    public WellLiquefactionContext(TileWell tileWell) {
        this.tileWell = tileWell;
    }

    public TileWell getTileWell() {
        return tileWell;
    }
}
