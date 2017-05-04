/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RecipeHelper
 * Created by HellFirePvP
 * Date: 22.09.2016 / 15:56
 */
public class RecipeHelper {

    public static IRecipe getShapelessOreDictRecipe(ItemStack stack, Object... recipeComponents) {
        return new ShapelessHandleOreRecipe(stack, recipeComponents);
    }

    public static IRecipe getShapedOredictRecipe(ItemStack stack, Object... recipeComponents) {
        return new ShapedHandleOreRecipe(stack, recipeComponents);
    }

    public static class ShapelessHandleOreRecipe extends ShapelessOreRecipe {
        protected ItemStack output = null;
        protected ArrayList<Object> input = new ArrayList<>();

        public ShapelessHandleOreRecipe(Block result, Object... recipe) {
            this(new ItemStack(result), recipe);
        }

        public ShapelessHandleOreRecipe(Item result, Object... recipe) {
            this(new ItemStack(result), recipe);
        }

        public ShapelessHandleOreRecipe(ItemStack result, Object... recipe) {
            super(result.copy());
            output = result.copy();
            for (Object in : recipe) {
                if (in instanceof ItemStack) {
                    input.add(((ItemStack) in).copy());
                } else if (in instanceof Item) {
                    input.add(new ItemStack((Item) in));
                } else if (in instanceof Block) {
                    input.add(new ItemStack((Block) in));
                } else if (in instanceof String) {
                    input.add(OreDictionary.getOres((String) in));
                /*
                 * ADDED CLAUSE TO ALLOW FOR MULTIPLE ITEMSTACK DEFINITIONS
                 */
                } else if (in instanceof List) {
                    input.add(in);
                } else {
                    String ret = "Invalid shapeless ore recipe: ";
                    for (Object tmp : recipe) {
                        ret += tmp + ", ";
                    }
                    ret += output;
                    throw new RuntimeException(ret);
                }
            }
        }

        ShapelessHandleOreRecipe(ShapelessRecipes recipe, Map<ItemStack, String> replacements) {
            super(recipe.getRecipeOutput());
            output = recipe.getRecipeOutput();

            for (ItemStack ingredient : recipe.recipeItems) {
                Object finalObj = ingredient;
                for (Map.Entry<ItemStack, String> replace : replacements.entrySet()) {
                    if (OreDictionary.itemMatches(replace.getKey(), ingredient, false)) {
                        finalObj = OreDictionary.getOres(replace.getValue());
                        break;
                    }
                }
                input.add(finalObj);
            }
        }

        /**
         * Returns the size of the recipe area
         */
        @Override
        public int getRecipeSize() {
            return input.size();
        }

        @Override
        public ItemStack getRecipeOutput() {
            return output;
        }

        /**
         * Returns an Item that is the result of this recipe
         */
        @Override
        public ItemStack getCraftingResult(InventoryCrafting var1) {
            return output.copy();
        }

        /**
         * Used to check if a recipe matches current crafting inventory
         */
        @SuppressWarnings("unchecked")
        @Override
        public boolean matches(InventoryCrafting var1, World world) {
            ArrayList<Object> required = new ArrayList<>(input);

            for (int x = 0; x < var1.getSizeInventory(); x++) {
                ItemStack slot = var1.getStackInSlot(x);

                if (slot != null) {
                    boolean inRecipe = false;
                    Iterator<Object> req = required.iterator();

                    while (req.hasNext()) {
                        boolean match = false;

                        Object next = req.next();

                        if (next instanceof ItemStack) {
                            match = OreDictionary.itemMatches((ItemStack) next, slot, false);
                        } else if (next instanceof List) {
                            Iterator<ItemStack> itr = ((List<ItemStack>) next).iterator();
                            while (itr.hasNext() && !match) {
                                match = OreDictionary.itemMatches(itr.next(), slot, false);
                            }
                        }

                        if (match) {
                            inRecipe = true;
                            required.remove(next);
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

        /**
         * Returns the input for this recipe, any mod accessing this value should never
         * manipulate the values in this array as it will effect the recipe itself.
         *
         * @return The recipes input vales.
         */
        public ArrayList<Object> getInput() {
            return this.input;
        }

        @Override
        public ItemStack[] getRemainingItems(InventoryCrafting inv) //getRecipeLeftovers
        {
            return ForgeHooks.defaultRecipeGetRemainingItems(inv);
        }
    }

    public static class ShapedHandleOreRecipe extends ShapedOreRecipe {

        public static final int MAX_CRAFT_GRID_WIDTH = 3;
        public static final int MAX_CRAFT_GRID_HEIGHT = 3;

        public ShapedHandleOreRecipe(Block result, Object... recipe) {
            this(new ItemStack(result), recipe);
        }

        public ShapedHandleOreRecipe(Item result, Object... recipe) {
            this(new ItemStack(result), recipe);
        }

        public ShapedHandleOreRecipe(ItemStack result, Object... recipe) {
            super(result.copy(), "R", 'R', new ItemStack(Blocks.STONE)); //Placeholder
            output = null;
            input = null;
            height = 0;
            width = 0;
            mirrored = true;
            //Resetting done.

            output = result.copy();

            String shape = "";
            int idx = 0;

            if (recipe[idx] instanceof Boolean) {
                mirrored = (Boolean) recipe[idx];
                if (recipe[idx + 1] instanceof Object[]) {
                    recipe = (Object[]) recipe[idx + 1];
                } else {
                    idx = 1;
                }
            }

            if (recipe[idx] instanceof String[]) {
                String[] parts = ((String[]) recipe[idx++]);

                for (String s : parts) {
                    width = s.length();
                    shape += s;
                }

                height = parts.length;
            } else {
                while (recipe[idx] instanceof String) {
                    String s = (String) recipe[idx++];
                    shape += s;
                    width = s.length();
                    height++;
                }
            }

            if (width * height != shape.length()) {
                String ret = "Invalid shaped ore recipe: ";
                for (Object tmp : recipe) {
                    ret += tmp + ", ";
                }
                ret += output;
                throw new RuntimeException(ret);
            }

            HashMap<Character, Object> itemMap = new HashMap<>();

            for (; idx < recipe.length; idx += 2) {
                Character chr = (Character) recipe[idx];
                Object in = recipe[idx + 1];

                if (in instanceof ItemStack) {
                    itemMap.put(chr, ((ItemStack) in).copy());
                } else if (in instanceof Item) {
                    itemMap.put(chr, new ItemStack((Item) in));
                } else if (in instanceof Block) {
                    itemMap.put(chr, new ItemStack((Block) in, 1, OreDictionary.WILDCARD_VALUE));
                } else if (in instanceof String) {
                    itemMap.put(chr, OreDictionary.getOres((String) in));
                /*
                 * ADDED CLAUSE TO ALLOW FOR MULTIPLE ITEMSTACK DEFINITIONS
                 */
                } else if (in instanceof List) {
                    itemMap.put(chr, in);
                } else {
                    String ret = "Invalid shaped ore recipe: ";
                    for (Object tmp : recipe) {
                        ret += tmp + ", ";
                    }
                    ret += output;
                    throw new RuntimeException(ret);
                }
            }

            input = new Object[width * height];
            int x = 0;
            for (char chr : shape.toCharArray()) {
                input[x++] = itemMap.get(chr);
            }
        }

        ShapedHandleOreRecipe(ShapedRecipes recipe, Map<ItemStack, String> replacements) {
            super(recipe.getRecipeOutput());
            output = recipe.getRecipeOutput();
            width = recipe.recipeWidth;
            height = recipe.recipeHeight;

            input = new Object[recipe.recipeItems.length];

            for (int i = 0; i < input.length; i++) {
                ItemStack ingredient = recipe.recipeItems[i];

                if (ingredient == null) continue;

                input[i] = recipe.recipeItems[i];

                for (Map.Entry<ItemStack, String> replace : replacements.entrySet()) {
                    if (OreDictionary.itemMatches(replace.getKey(), ingredient, true)) {
                        input[i] = OreDictionary.getOres(replace.getValue());
                        break;
                    }
                }
            }
        }

        /**
         * Returns an Item that is the result of this recipe
         */
        @Override
        public ItemStack getCraftingResult(InventoryCrafting var1) {
            return output.copy();
        }

        /**
         * Returns the size of the recipe area
         */
        @Override
        public int getRecipeSize() {
            return input.length;
        }

        @Override
        public ItemStack getRecipeOutput() {
            return output;
        }

        /**
         * Used to check if a recipe matches current crafting inventory
         */
        @Override
        public boolean matches(InventoryCrafting inv, World world) {
            for (int x = 0; x <= MAX_CRAFT_GRID_WIDTH - width; x++) {
                for (int y = 0; y <= MAX_CRAFT_GRID_HEIGHT - height; ++y) {
                    if (checkMatch(inv, x, y, false)) {
                        return true;
                    }

                    if (mirrored && checkMatch(inv, x, y, true)) {
                        return true;
                    }
                }
            }

            return false;
        }

        @SuppressWarnings("unchecked")
        protected boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
            for (int x = 0; x < MAX_CRAFT_GRID_WIDTH; x++) {
                for (int y = 0; y < MAX_CRAFT_GRID_HEIGHT; y++) {
                    int subX = x - startX;
                    int subY = y - startY;
                    Object target = null;

                    if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
                        if (mirror) {
                            target = input[width - subX - 1 + subY * width];
                        } else {
                            target = input[subX + subY * width];
                        }
                    }

                    ItemStack slot = inv.getStackInRowAndColumn(x, y);

                    if (target instanceof ItemStack) {
                        if (!OreDictionary.itemMatches((ItemStack) target, slot, false)) {
                            return false;
                        }
                    } else if (target instanceof List) {
                        boolean matched = false;

                        Iterator<ItemStack> itr = ((List<ItemStack>) target).iterator();
                        while (itr.hasNext() && !matched) {
                            matched = OreDictionary.itemMatches(itr.next(), slot, false);
                        }

                        if (!matched) {
                            return false;
                        }
                    } else if (target == null && slot != null) {
                        return false;
                    }
                }
            }

            return true;
        }

        public ShapedHandleOreRecipe setMirrored(boolean mirror) {
            mirrored = mirror;
            return this;
        }

        /**
         * Returns the input for this recipe, any mod accessing this value should never
         * manipulate the values in this array as it will effect the recipe itself.
         *
         * @return The recipes input vales.
         */
        public Object[] getInput() {
            return this.input;
        }

        @Override
        public ItemStack[] getRemainingItems(InventoryCrafting inv) {
            return ForgeHooks.defaultRecipeGetRemainingItems(inv);
        }

    }

}
