/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CustomMatcherRecipe
 * Created by HellFirePvP
 * Date: 01.07.2019 / 00:23
 */
public abstract class CustomMatcherRecipe extends BaseHandlerRecipe<IItemHandler> {

    protected CustomMatcherRecipe(ResourceLocation recipeId) {
        super(recipeId);
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public boolean matches(IItemHandler handler, World world) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return getRecipeOutput();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public abstract CustomRecipeSerializer<?> getSerializer();
}
