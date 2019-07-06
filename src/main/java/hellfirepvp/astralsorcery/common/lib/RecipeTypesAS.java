/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.common.crafting.helper.ResolvingRecipeType;
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefaction;
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefactionContext;
import hellfirepvp.astralsorcery.common.util.tile.TileInventory;
import net.minecraftforge.items.IItemHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RecipeTypesAS
 * Created by HellFirePvP
 * Date: 30.06.2019 / 23:50
 */
public class RecipeTypesAS {

    private RecipeTypesAS() {}

    public static ResolvingRecipeType<IItemHandler, WellLiquefaction, WellLiquefactionContext> TYPE_WELL =
            new ResolvingRecipeType<>("well", WellLiquefaction.class,
                    (recipe, context) -> recipe.matches(context.getTileWell()));

}
