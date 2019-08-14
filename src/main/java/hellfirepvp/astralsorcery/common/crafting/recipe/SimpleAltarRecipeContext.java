/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe;

import hellfirepvp.astralsorcery.common.crafting.helper.RecipeCraftingContext;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.tile.TileInventory;
import net.minecraftforge.items.IItemHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleAltarRecipeContext
 * Created by HellFirePvP
 * Date: 12.08.2019 / 19:32
 */
public class SimpleAltarRecipeContext extends RecipeCraftingContext<SimpleAltarRecipe, IItemHandler> {

    private final TileAltar altar;

    public SimpleAltarRecipeContext(TileAltar altar) {
        this.altar = altar;
    }

    public TileAltar getAltar() {
        return altar;
    }

}
