package hellfirepvp.astralsorcery.common.crafting.helper;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.ShapedLightProximityRecipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ShapedRecipe
 * Created by HellFirePvP
 * Date: 10.08.2016 / 15:21
 */
public class ShapedRecipe extends AbstractCacheableRecipe {

    protected ShapeMap crafingShape = new ShapeMap();

    //If set to true, forces the exact places specified in the craftingShape map.
    //If set to false, the pattern the recipe is made of can be placed wherever in the 3x3 main grid.
    private boolean forceEmptySpaces = false;

    public ShapedRecipe(Block output) {
        this(new ItemStack(output));
    }

    public ShapedRecipe(Item output) {
        this(new ItemStack(output));
    }

    public ShapedRecipe(ItemStack output) {
        super(output);
    }

    public ShapedRecipe addPart(Block block, ShapedRecipeSlot... slots) {
        return addPart(new ItemStack(block), slots);
    }

    public ShapedRecipe addPart(Item stack, ShapedRecipeSlot... slots) {
        return addPart(new ItemStack(stack), slots);
    }

    public ShapedRecipe addPart(ItemStack stack, ShapedRecipeSlot... slots) {
        ItemHandle handle = new ItemHandle(stack);
        for(ShapedRecipeSlot slot : slots) {
            crafingShape.put(slot, handle);
        }
        return this;
    }

    public ShapedRecipe addPart(FluidStack fluidStack, ShapedRecipeSlot... slots) {
        ItemHandle handle = new ItemHandle(fluidStack);
        for(ShapedRecipeSlot slot : slots) {
            crafingShape.put(slot, handle);
        }
        return this;
    }

    public ShapedRecipe addPart(Fluid fluid, int mbAmount, ShapedRecipeSlot... slots) {
        return addPart(new FluidStack(fluid, mbAmount), slots);
    }

    public ShapedRecipe addPart(Fluid fluid, ShapedRecipeSlot... slots) {
        return addPart(fluid, 1000, slots);
    }

    public ShapedRecipe addPart(String oreDictName, ShapedRecipeSlot... slots) {
        ItemHandle handle = new ItemHandle(oreDictName);
        for(ShapedRecipeSlot slot : slots) {
            crafingShape.put(slot, handle);
        }
        return this;
    }

    public ShapedRecipe forceEmptySpaces() {
        this.forceEmptySpaces = true;
        return this;
    }

    @Override
    public void register() {
        CraftingManager.getInstance().addRecipe(make());
    }

    @Override
    public AccessibleRecipeAdapater make() {
        return new AccessibleRecipeAdapater(RecipeHelper.getShapedOredictRecipe(getOutput(), getNativeObjOutArray()), this);
    }

    public ShapedLightProximityRecipe makeLightProximityRecipe() {
        return new ShapedLightProximityRecipe(getOutput(), getNativeObjOutArray());
    }

    private Object[] getNativeObjOutArray() {
        Counter c = new Counter();
        c.count = 0;
        Map<ItemHandle, Character> shapeCharacters = new HashMap<>();
        String upperRow = refactorRow(ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.UPPER_RIGHT, shapeCharacters, c);
        String middleRow = refactorRow(ShapedRecipeSlot.LEFT, ShapedRecipeSlot.CENTER, ShapedRecipeSlot.RIGHT, shapeCharacters, c);
        String lowerRow = refactorRow(ShapedRecipeSlot.LOWER_LEFT, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.LOWER_RIGHT, shapeCharacters, c);
        if(forceEmptySpaces) {
            int arrayLength = 3 + (shapeCharacters.size() * 2); //ForEach 1 ItemStack, 1 Character.
            Object[] recipeObjArray = new Object[arrayLength];
            recipeObjArray[0] = upperRow;
            recipeObjArray[1] = middleRow;
            recipeObjArray[2] = lowerRow;
            int arrayPointer = 3;
            addToArray(shapeCharacters, recipeObjArray, arrayPointer);
            return recipeObjArray;
        } else {
            String[] recipeTrimmed = trimRecipeStrings(upperRow, middleRow, lowerRow);
            int point = 0;
            if(!recipeTrimmed[0].trim().isEmpty()) point++;
            if(!recipeTrimmed[1].trim().isEmpty()) point++;
            if(!recipeTrimmed[2].trim().isEmpty()) point++;
            int arrayLength = point + (shapeCharacters.size() * 2); //ForEach 1 ItemStack, 1 Character.
            Object[] recipeObjArray = new Object[arrayLength];
            int pointer = 0;
            if(!recipeTrimmed[0].trim().isEmpty()) {
                recipeObjArray[pointer] = recipeTrimmed[0];
                pointer++;
            }
            if(!recipeTrimmed[1].trim().isEmpty()) {
                recipeObjArray[pointer] = recipeTrimmed[1];
                pointer++;
            }
            if(!recipeTrimmed[2].trim().isEmpty()) {
                recipeObjArray[pointer] = recipeTrimmed[2];
            }
            addToArray(shapeCharacters, recipeObjArray, point);
            return recipeObjArray;
        }
    }

    private String[] trimRecipeStrings(String upperRow, String middleRow, String lowerRow) {
        String[] out = new String[] { upperRow, middleRow, lowerRow };
        List<Integer> cutIndices = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            boolean mayRemove = true;
            for (int j = 0; j < 3; j++) {
                String str = out[j];
                if(str.charAt(i) != ' ') mayRemove = false;
            }
            if(mayRemove) {
                cutIndices.add(i);
            }
        }
        for (int j = 0; j < 3; j++) {
            out[j] = new String(cut(out[j].toCharArray(), cutIndices));
        }
        return out;
    }

    private char[] cut(char[] in, List<Integer> toRemove) {
        char[] out = new char[in.length - toRemove.size()];
        int outPointer = 0;
        for (int i = 0; i < in.length; i++) {
            if(!toRemove.contains(i)) {
                out[outPointer] = in[i];
                outPointer++;
            }
        }
        return out;
    }

    @Nullable
    @Override
    public ItemHandle getExpectedStack(int row, int column) {
        ShapedRecipeSlot slot = ShapedRecipeSlot.getByRowColumnIndex(row, column);
        return slot == null ? null : crafingShape.get(slot);
    }

    @Nullable
    @Override
    public ItemHandle getExpectedStack(ShapedRecipeSlot slot) {
        return crafingShape.get(slot);
    }

    private void addToArray(Map<ItemHandle, Character> shapeCharacters, Object[] recipeObjArray, int arrayPointer) {
        for(ItemHandle key : shapeCharacters.keySet()) {
            Character value = shapeCharacters.get(key);
            recipeObjArray[arrayPointer] = value;
            arrayPointer++;
            recipeObjArray[arrayPointer] = key.getObjectForRecipe();
            arrayPointer++;
        }
    }

    private String refactorRow(ShapedRecipeSlot first, ShapedRecipeSlot second, ShapedRecipeSlot third, Map<ItemHandle, Character> characterMap, Counter craftingPointer) {
        StringBuilder builder = new StringBuilder();
        if(append(first, builder, craftingPointer, characterMap)) craftingPointer.count++;
        if(append(second, builder, craftingPointer, characterMap)) craftingPointer.count++;
        if(append(third, builder, craftingPointer, characterMap)) craftingPointer.count++;
        return builder.toString();
    }

    private boolean append(ShapedRecipeSlot slot, StringBuilder builder, Counter craftingPointer, Map<ItemHandle, Character> characterMap) {
        boolean increment = false;
        if(crafingShape.get(slot) != null) {
            ItemHandle firstStack = crafingShape.get(slot);
            Character toAdd;
            if(characterMap.containsKey(firstStack)) {
                toAdd = characterMap.get(firstStack);
            } else {
                toAdd = refactorCraftingPointer(craftingPointer.count);
                characterMap.put(firstStack, toAdd);
                increment = true;
            }
            builder.append(toAdd);
        } else {
            builder.append(" "); //Nothing.
        }
        return increment;
    }

    private Character refactorCraftingPointer(int pointer) {
        switch (pointer) {
            case 0: return 'A';
            case 1: return 'B';
            case 2: return 'C';
            case 3: return 'D';
            case 4: return 'E';
            case 5: return 'F';
            case 6: return 'G';
            case 7: return 'H';
            case 8: return 'I';
        }
        return null; //Only 9 crafting slots.
    }

    private class Counter {

        int count;

    }

}
