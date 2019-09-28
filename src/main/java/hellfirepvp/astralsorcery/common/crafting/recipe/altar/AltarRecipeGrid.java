/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.altar;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.Tuple;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarRecipeGrid
 * Created by HellFirePvP
 * Date: 12.08.2019 / 19:53
 */
public class AltarRecipeGrid {

    public static final int GRID_SIZE = 5;
    public static final int MAX_INVENTORY_SIZE = GRID_SIZE * GRID_SIZE;

    private static final Pattern SKIP_CHARS = Pattern.compile("^\\s|_|#$");

    private final Map<Integer, Ingredient> gridParts;
    private final int width, height;

    private AltarRecipeGrid(Map<Integer, Ingredient> gridParts, int width, int height) {
        this.gridParts = gridParts;
        this.width = width;
        this.height = height;
    }

    public boolean containsInputs(IItemHandlerModifiable itemHandler, boolean testMirrored) {
        for (int xx = 0; xx <= (GRID_SIZE - this.width); xx++) {
            for (int zz = 0; zz <= (GRID_SIZE - this.height); zz++) {
                if (matches(itemHandler, xx, zz, false)) {
                    return true;
                }
                if (testMirrored && matches(itemHandler, xx, zz, true)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Ingredient getIngredient(int index) {
        return this.gridParts.getOrDefault(index, Ingredient.EMPTY);
    }

    private boolean matches(IItemHandlerModifiable itemHandler, int xOffset, int zOffset, boolean mirrored) {
        Set<Integer> matchedItems = new HashSet<>();
        int totalOffset = zOffset * GRID_SIZE + xOffset;
        for (int x = 0; x < this.width; x++) {
            for (int z = 0; z < this.height; z++) {
                int index = x + z * GRID_SIZE;
                if (mirrored) {
                    index = (this.width - x - 1) + z * GRID_SIZE;
                }

                Ingredient expected = this.getIngredient(index);
                int slot = index + totalOffset;

                ItemStack contained = itemHandler.getStackInSlot(slot);
                if (!expected.test(contained)) {
                    return false;
                }
                matchedItems.add(slot);
            }
        }
        return isGridEmpty(itemHandler, matchedItems);
    }

    private boolean isGridEmpty(IItemHandlerModifiable inventory, Collection<Integer> skipSlots) {
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int z = 0; z < GRID_SIZE; z++) {
                int slot = x + z * GRID_SIZE;
                if (!skipSlots.contains(slot) && !inventory.getStackInSlot(slot).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void write(PacketBuffer buffer) {
        buffer.writeInt(this.width);
        buffer.writeInt(this.height);
        buffer.writeInt(this.gridParts.size());
        this.gridParts.forEach((key, value) -> {
            buffer.writeInt(key);
            value.write(buffer);
        });
    }

    public static AltarRecipeGrid read(PacketBuffer buffer) {
        int width = buffer.readInt();
        int height = buffer.readInt();
        Map<Integer, Ingredient> ingredientMap = new HashMap<>();
        for (int i = 0; i < buffer.readInt(); i++) {
            int slot = buffer.readInt();
            Ingredient ingredient = Ingredient.read(buffer);
            ingredientMap.put(slot, ingredient);
        }
        return new AltarRecipeGrid(ingredientMap, width, height);
    }

    public static AltarRecipeGrid deserialize(AltarType type, JsonArray pattern, JsonObject objKeyElements) throws JsonSyntaxException {
        Map<Integer, Character> patternMap = new HashMap<>();
        Set<Character> usedChars = new HashSet<>();
        for (int i = 0; i < MAX_INVENTORY_SIZE; i++) {
            patternMap.put(i, '_');
        }

        for (int i = 0; i < Math.min(pattern.size(), GRID_SIZE); i++) {
            String str = JSONUtils.getString(pattern.get(i), String.format("pattern[%s]", i));
            if (str.length() > GRID_SIZE) {
                throw new JsonSyntaxException("Invalid pattern: too many columns, " + GRID_SIZE + " is maximum");
            }

            char[] charArray = str.toCharArray();
            for (int j = 0; j < charArray.length; j++) {
                char c = charArray[j];
                String strChar = String.valueOf(c);
                if (SKIP_CHARS.matcher(strChar).matches()) {
                    continue;
                }
                usedChars.add(c);
                patternMap.put(i * GRID_SIZE + j, c);
            }
        }

        Map<Integer, Ingredient> mappedIngredients = new HashMap<>();
        for (Map.Entry<String, JsonElement> jEntry : objKeyElements.entrySet()) {
            String key = jEntry.getKey();
            if (key.length() != 1) {
                throw new JsonSyntaxException("Invalid Key: '" + key + "'! Keys must only be a single character!");
            }

            char c = key.charAt(0);
            if (SKIP_CHARS.matcher(String.valueOf(c)).matches()) {
                continue;
            }
            if (!usedChars.contains(c)) {
                throw new JsonSyntaxException("Invalid Key: '" + key + "'! Not used in the pattern map!");
            }

            Ingredient i = Ingredient.deserialize(jEntry.getValue());
            for (int index = 0; index < MAX_INVENTORY_SIZE; index++) {
                if (patternMap.get(index) == c) {
                    mappedIngredients.put(index, i);
                }
            }

            usedChars.remove(c);
        }

        if (!usedChars.isEmpty()) {
            throw new JsonSyntaxException("The following keys are used in the pattern but don't have a key associated with them: " + usedChars);
        }
        if (mappedIngredients.isEmpty()) {
            throw new JsonSyntaxException("Empty recipe found. At least one input must be specified!");
        }
        for (Integer slot : mappedIngredients.keySet()) {
            if (!type.hasSlot(slot)) {
                throw new JsonSyntaxException("Slot " + slot + " has an ingredient but cannot be used in altar type " + type.name());
            }
        }

        int firstColumn = GRID_SIZE - 1,
            firstRow = GRID_SIZE - 1,
            lastColumn = 0,
            lastRow = 0;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int column = 0; column < GRID_SIZE; column++) {
                int slotIndex = row * GRID_SIZE + column;
                if (mappedIngredients.containsKey(slotIndex)) {
                    if (row < firstRow) {
                        firstRow = row;
                    }
                    if (row > lastRow) {
                        lastRow = row;
                    }
                    if (column < firstColumn) {
                        firstColumn = column;
                    }
                    if (column > lastColumn) {
                        lastColumn = column;
                    }
                }
            }
        }
        if (firstColumn > 0) {
            int xShift = firstColumn;
            mappedIngredients = mappedIngredients.entrySet().stream()
                    .map(e -> new Tuple<>(e.getKey() - xShift, e.getValue()))
                    .collect(Collectors.toMap(Tuple::getA, Tuple::getB));
        }
        if (firstRow > 0) {
            int yShift = firstRow * GRID_SIZE;
            mappedIngredients = mappedIngredients.entrySet().stream()
                    .map(e -> new Tuple<>(e.getKey() - yShift, e.getValue()))
                    .collect(Collectors.toMap(Tuple::getA, Tuple::getB));
        }

        return new AltarRecipeGrid(mappedIngredients, (lastColumn - firstColumn) + 1, (lastRow - firstRow) + 1);
    }

}
