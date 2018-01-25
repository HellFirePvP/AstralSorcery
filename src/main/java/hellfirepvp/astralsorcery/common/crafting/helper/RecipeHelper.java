/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Iterator;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RecipeHelper
 * Created by HellFirePvP
 * Date: 22.09.2016 / 15:56
 */
public class RecipeHelper {

    public static BasePlainRecipe getShapelessOreDictRecipe(ResourceLocation name, ItemStack output, NonNullList<Ingredient> craftingComponents) {
        return new ShapelessIngredientRecipe(name, output, craftingComponents);
    }

    public static BasePlainRecipe getShapedOredictRecipe(ResourceLocation name, ItemStack output, ShapeMap.Baked craftingComponents) {
        return new ShapedIngredientRecipe(name, output, craftingComponents);
    }

    public static class ShapelessIngredientRecipe extends BasePlainRecipe {

        private final ItemStack out;
        private final NonNullList<Ingredient> inputs;

        public ShapelessIngredientRecipe(ResourceLocation registryName, ItemStack out, NonNullList<Ingredient> inputs) {
            super(registryName);
            this.out = out;
            this.inputs = inputs;
        }

        @Override
        public boolean matches(InventoryCrafting inv, World worldIn) {
            NonNullList<Ingredient> required = NonNullList.create();
            required.addAll(inputs);
            for (int x = 0; x < inv.getSizeInventory(); x++) {
                ItemStack slot = inv.getStackInSlot(x);
                if (!slot.isEmpty()) {
                    boolean inRecipe = false;
                    Iterator<Ingredient> req = required.iterator();
                    while (req.hasNext()) {
                        if (req.next().apply(slot)) {
                            inRecipe = true;
                            req.remove();
                            break;
                        }
                    }
                    if (!inRecipe) {
                        return false;
                    }
                }
            }

            return required.isEmpty();
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            return out.copy();
        }

        @Override
        public boolean canFit(int width, int height) {
            return width * height >= inputs.size();
        }

        @Override
        public ItemStack getRecipeOutput() {
            return out.copy();
        }

        @Override
        public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
            return ForgeHooks.defaultRecipeGetRemainingItems(inv);
        }

        @Override
        public NonNullList<Ingredient> getIngredients() {
            return inputs;
        }

    }

    public static class ShapedIngredientRecipe extends BasePlainRecipe {

        private final ItemStack out;
        private final ShapeMap.Baked grid;

        private ShapedIngredientRecipe(ResourceLocation name, ItemStack out, ShapeMap.Baked grid) {
            super(name);
            this.out = out;
            this.grid = grid;
        }

        @Override
        public boolean matches(InventoryCrafting inv, World worldIn) {
            for (int x = 0; x <= ShapedOreRecipe.MAX_CRAFT_GRID_WIDTH - grid.getWidth(); x++) {
                for (int y = 0; y <= ShapedOreRecipe.MAX_CRAFT_GRID_HEIGHT - grid.getHeight(); ++y) {
                    if (checkMatch(inv, x, y)) {
                        return true;
                    }
                }
            }

            return false;
        }

        protected boolean checkMatch(InventoryCrafting inv, int startX, int startY) {
            for (int x = 0; x < ShapedOreRecipe.MAX_CRAFT_GRID_WIDTH; x++) {
                for (int y = 0; y < ShapedOreRecipe.MAX_CRAFT_GRID_HEIGHT; y++) {
                    int subX = x - startX;
                    int subY = y - startY;
                    Ingredient target = Ingredient.EMPTY;

                    if (subX >= 0 && subY >= 0 && subX < grid.getWidth() && subY < grid.getHeight()) {
                        ShapedRecipeSlot srs = ShapedRecipeSlot.getByRowColumnIndex(subY, subX);
                        target = grid.get(srs);
                    }

                    if (!target.apply(inv.getStackInRowAndColumn(x, y))) {
                        return false;
                    }
                }
            }

            return true;
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            return out.copy();
        }

        @Override
        public boolean canFit(int width, int height) {
            return width >= grid.getWidth() && height >= grid.getHeight();
        }

        @Override
        public ItemStack getRecipeOutput() {
            return out.copy();
        }

        @Override
        public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
            return ForgeHooks.defaultRecipeGetRemainingItems(inv);
        }

        @Override
        public NonNullList<Ingredient> getIngredients() {
            return grid.getRawIngredientList();
        }

    }

}
