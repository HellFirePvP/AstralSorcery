package hellfirepvp.astralsorcery.common.crafting.helper;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ShapedRecipe
 * Created by HellFirePvP
 * Date: 10.08.2016 / 15:21
 */
public class ShapedRecipe extends AbstractRecipe {

    private ShapeMap crafingShape = new ShapeMap();

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
        for(ShapedRecipeSlot slot : slots) {
            crafingShape.put(slot, stack);
        }
        return this;
    }

    @Override
    public void register() {
        Counter c = new Counter();
        c.count = 0;
        Map<ItemStack, Character> shapeCharacters = new HashMap<>();
        String upperRow = refactorRow(ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.UPPER_RIGHT, shapeCharacters, c);
        String middleRow = refactorRow(ShapedRecipeSlot.LEFT, ShapedRecipeSlot.CENTER, ShapedRecipeSlot.RIGHT, shapeCharacters, c);
        String lowerRow = refactorRow(ShapedRecipeSlot.LOWER_LEFT, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.LOWER_RIGHT, shapeCharacters, c);
        int arrayLength = 3 + (shapeCharacters.size() * 2); //ForEach 1 ItemStack, 1 Character.
        Object[] recipeObjArray = new Object[arrayLength];
        recipeObjArray[0] = upperRow;
        recipeObjArray[1] = middleRow;
        recipeObjArray[2] = lowerRow;
        int arrayPointer = 3;
        addToArray(shapeCharacters, recipeObjArray, arrayPointer);
        CraftingManager.getInstance().addRecipe(getOutput(), recipeObjArray);
    }

    private void addToArray(Map<ItemStack, Character> shapeCharacters, Object[] recipeObjArray, int arrayPointer) {
        for(ItemStack key : shapeCharacters.keySet()) {
            Character value = shapeCharacters.get(key);
            recipeObjArray[arrayPointer] = value;
            arrayPointer++;
            recipeObjArray[arrayPointer] = key;
            arrayPointer++;
        }
    }

    private String refactorRow(ShapedRecipeSlot first, ShapedRecipeSlot sencond, ShapedRecipeSlot third, Map<ItemStack, Character> characterMap, Counter craftingPointer) {
        StringBuilder builder = new StringBuilder();
        if(append(first, builder, craftingPointer, characterMap)) craftingPointer.count++;
        if(append(sencond, builder, craftingPointer, characterMap)) craftingPointer.count++;
        append(third, builder, craftingPointer, characterMap);
        return builder.toString();
    }

    private boolean append(ShapedRecipeSlot slot, StringBuilder builder, Counter craftingPointer, Map<ItemStack, Character> characterMap) {
        boolean increment = false;
        if(crafingShape.get(slot) != null) {
            ItemStack firstStack = crafingShape.get(slot);
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
