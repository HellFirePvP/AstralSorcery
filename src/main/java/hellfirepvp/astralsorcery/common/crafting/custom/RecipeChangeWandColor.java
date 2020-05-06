/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.custom;

import hellfirepvp.astralsorcery.common.item.wand.ItemIlluminationWand;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RecipeChangeWandColor
 * Created by HellFirePvP
 * Date: 29.11.2019 / 13:24
 */
public class RecipeChangeWandColor extends SpecialRecipe {

    public RecipeChangeWandColor(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        return tryFindValidRecipeAndDye(inv) != null;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        DyeColor color = tryFindValidRecipeAndDye(inv);
        if (color == null) {
            return ItemStack.EMPTY;
        }
        ItemStack wand = new ItemStack(ItemsAS.ILLUMINATION_WAND);
        ItemIlluminationWand.setConfiguredColor(wand, color);
        return wand;
    }

    @Nullable
    private DyeColor tryFindValidRecipeAndDye(CraftingInventory inv) {
        boolean foundWand = false;
        DyeColor dyeColorFound = null;
        int nonEmptyItemsFound = 0;

        for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
            ItemStack in = inv.getStackInSlot(slot);
            if (!in.isEmpty()) {
                nonEmptyItemsFound++;

                if (in.getItem() instanceof ItemIlluminationWand) {
                    foundWand = true;
                } else {
                    DyeColor color = DyeColor.getColor(in);
                    if (color != null) {
                        dyeColorFound = color;
                    }
                }
            }
        }

        if (!foundWand || dyeColorFound == null || nonEmptyItemsFound != 2) {
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
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializersAS.CUSTOM_CHANGE_WAND_COLOR_SERIALIZER;
    }

    public static class Serializer extends SpecialRecipeSerializer<RecipeChangeWandColor> {

        public Serializer() {
            super(RecipeChangeWandColor::new);
            this.setRegistryName(RecipeSerializersAS.CUSTOM_CHANGE_WAND_COLOR);
        }
    }
}
