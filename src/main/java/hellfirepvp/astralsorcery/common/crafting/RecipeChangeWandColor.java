/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting;

import hellfirepvp.astralsorcery.common.crafting.helper.BasePlainRecipe;
import hellfirepvp.astralsorcery.common.item.wand.ItemIlluminationWand;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.OreDictAlias;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RecipeChangeWandColor
 * Created by HellFirePvP
 * Date: 02.05.2017 / 22:05
 */
public class RecipeChangeWandColor extends BasePlainRecipe {

    public static final RecipeChangeWandColor INSTANCE = new RecipeChangeWandColor();

    private RecipeChangeWandColor() {
        super("recipechangewandcolor");
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        return tryFindValidRecipeAndDye(inv) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        EnumDyeColor color = tryFindValidRecipeAndDye(inv);
        if(color == null) {
            return ItemStack.EMPTY; //Uhh.. wtf went wrong here. no valid check?
        }
        ItemStack wand = new ItemStack(ItemsAS.illuminationWand);
        ItemIlluminationWand.setConfiguredColor(wand, color);
        return wand;
    }

    @Nullable
    private EnumDyeColor tryFindValidRecipeAndDye(InventoryCrafting inv) {
        boolean foundWand = false;
        EnumDyeColor dyeColorFound = null;
        int nonEmptyItemsFound = 0;

        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                ItemStack in = inv.getStackInRowAndColumn(x, z);
                if(!in.isEmpty()) {
                    nonEmptyItemsFound++;

                    if(in.getItem() instanceof ItemIlluminationWand) {
                        foundWand = true;
                    } else {
                        for (EnumDyeColor color : EnumDyeColor.values()) {
                            if(ItemUtils.hasOreName(in, OreDictAlias.getDyeOreDict(color))) {
                                dyeColorFound = color;
                                break;
                            }
                        }
                    }
                }
            }
        }

        if(!foundWand || dyeColorFound == null || nonEmptyItemsFound != 2) {
            return null;
        } else {
            return dyeColorFound;
        }
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(ItemsAS.illuminationWand);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }
}
