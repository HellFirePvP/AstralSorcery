/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.world.World;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RecipeHelper
 * Created by HellFirePvP
 * Date: 22.09.2019 / 09:40
 */
public class RecipeHelper {

    private RecipeHelper() {}

    public static ItemStack findSmeltingResult(World world, ItemStack input) {
        RecipeManager mgr = world.getRecipeManager();
        IInventory inv = new Inventory(input);
        Optional<IRecipe<IInventory>> optRecipe = (Optional<IRecipe<IInventory>>) ObjectUtils.firstNonNull(
                mgr.getRecipe(IRecipeType.SMELTING, inv, world),
                mgr.getRecipe(IRecipeType.CAMPFIRE_COOKING, inv, world),
                mgr.getRecipe(IRecipeType.SMOKING, inv, world),
                Optional.empty());
        return optRecipe.map(recipe -> recipe.getCraftingResult(inv)).orElse(ItemStack.EMPTY);
    }

}
