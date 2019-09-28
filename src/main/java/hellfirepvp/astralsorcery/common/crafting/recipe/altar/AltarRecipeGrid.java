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
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

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

    private AltarRecipeGrid(Map<Integer, Ingredient> gridParts) {
        this.gridParts = gridParts;
    }

    public boolean containsInputs(IItemHandlerModifiable itemHandler, boolean testMirrored) {
        if (matches(itemHandler, Function.identity())) {
            return true;
        }
        if (testMirrored && matches(itemHandler, (slot) -> mirrorRowIndex(GRID_SIZE, slot))) {
            return true;
        }
        return false;
    }

    public Ingredient getIngredient(int index) {
        return this.gridParts.getOrDefault(index, Ingredient.EMPTY);
    }

    private boolean matches(IItemHandlerModifiable itemHandler, Function<Integer, Integer> slotConverter) {
        for (Integer slotId : this.gridParts.keySet()) {
            Ingredient expected = this.gridParts.get(slotId);

            ItemStack contained = itemHandler.getStackInSlot(slotConverter.apply(slotId));
            if (!expected.test(contained)) {
                return false;
            }
        }
        return true;
    }

    private int mirrorRowIndex(int gridSize, int in) {
        return (in / gridSize) * gridSize + (gridSize - ((in % gridSize) + 1));
    }

    public void write(PacketBuffer buffer) {
        buffer.writeInt(this.gridParts.size());
        this.gridParts.forEach((key, value) -> {
            buffer.writeInt(key);
            value.write(buffer);
        });
    }

    public static AltarRecipeGrid read(PacketBuffer buffer) {
        Map<Integer, Ingredient> ingredientMap = new HashMap<>();
        for (int i = 0; i < buffer.readInt(); i++) {
            int slot = buffer.readInt();
            Ingredient ingredient = Ingredient.read(buffer);
            ingredientMap.put(slot, ingredient);
        }
        return new AltarRecipeGrid(ingredientMap);
    }

    public static AltarRecipeGrid deserialize(AltarType type, JsonArray pattern, JsonObject objKeyElements) throws JsonSyntaxException {
        Map<Integer, Character> patternMap = new HashMap<>();
        Set<Character> usedChars = new HashSet<>();
        for (int i = 0; i < MAX_INVENTORY_SIZE; i++) {
            patternMap.put(i, '_');
        }
        usedChars.add('_');

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
                patternMap.put(i % GRID_SIZE + j, c);
            }
        }

        Map<Integer, Ingredient> mappedIngredients = new HashMap<>();
        for (Map.Entry<String, JsonElement> jEntry : objKeyElements.entrySet()) {
            String key = jEntry.getKey();
            if (key.length() != 1) {
                throw new JsonSyntaxException("Invalid Key: '" + key + "'! Keys must only be a single character!");
            }

            char c = key.charAt(0);
            if (c == ' ') {
                continue;
            }
            if (!usedChars.contains(c)) {
                throw new JsonSyntaxException("Invalid Key: '" + key + "'! Not used in the pattern map!");
            }

            Ingredient i;
            if (c == '_') {
                i = Ingredient.EMPTY;
            } else {
                i = Ingredient.deserialize(jEntry.getValue());
            }

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

        for (Integer slot : mappedIngredients.keySet()) {
            if (!type.hasSlot(slot)) {
                throw new JsonSyntaxException("Slot " + slot + " has an ingredient but cannot be used in altar type " + type.name());
            }
        }

        return new AltarRecipeGrid(mappedIngredients);
    }

}
