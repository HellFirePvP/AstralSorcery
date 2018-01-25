/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CraftingRecipeHelper
 * Created by HellFirePvP
 * Date: 29.10.2017 / 14:24
 */
public class CraftingRecipeHelper {

    private CraftingRecipeHelper() {}

    public static List<IRecipe> findRecipesWithOutput(ItemStack stack) {
        List<IRecipe> ir = new LinkedList<>();
        for (IRecipe rec : ForgeRegistries.RECIPES) {
            if(ItemUtils.matchStackLoosely(stack, rec.getRecipeOutput())) {
                ir.add(rec);
            }
        }
        return ir;
    }

}
