/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.common.crafting.helper.ResolvingRecipeType;
import hellfirepvp.astralsorcery.common.crafting.recipe.*;
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

    public static ResolvingRecipeType<IItemHandler, WellLiquefaction, WellLiquefactionContext> TYPE_WELL;
    public static ResolvingRecipeType<IItemHandler, LiquidInfusion, LiquidInfusionContext> TYPE_INFUSION;
    public static ResolvingRecipeType<IItemHandler, BlockTransmutation, BlockTransmutationContext> TYPE_BLOCK_TRANSMUTATION;
    public static ResolvingRecipeType<IItemHandler, SimpleAltarRecipe, SimpleAltarRecipeContext> TYPE_ALTAR;

}
