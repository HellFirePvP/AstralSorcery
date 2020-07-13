/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.altar;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.crafting.helper.ingredient.FluidIngredient;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
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

    //TODO re-add dynamic size shifting, respecting typed slot filters

    public static final int GRID_SIZE = 5;
    public static final int MAX_INVENTORY_SIZE = GRID_SIZE * GRID_SIZE;
    public static final AltarRecipeGrid EMPTY = new AltarRecipeGrid(new HashMap<>(), 0, 0);

    private static final Pattern SKIP_CHARS = Pattern.compile("^\\s|_|#$");

    private final Map<Integer, Ingredient> gridParts;
    private final int width, height;

    private AltarRecipeGrid(Map<Integer, Ingredient> gridParts) {
        this(new AltarRecipeGrid(gridParts, GRID_SIZE, GRID_SIZE)/*.minimizeGrid()*/);
    }

    private AltarRecipeGrid(AltarRecipeGrid other) {
        this(other.gridParts, other.width, other.height);
    }

    private AltarRecipeGrid(Map<Integer, Ingredient> gridParts, int width, int height) {
        this.gridParts = gridParts;
        this.width = width;
        this.height = height;
    }

    public static Builder builder() {
        return new Builder();
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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

    public void validate(AltarType type) {
        //AltarRecipeGrid centralized = this.centralizeGrid();
        if (this.gridParts.isEmpty()) {
            throw new IllegalArgumentException("Altar recipe grid cannot be empty!");
        }

        for (Integer index : this.gridParts.keySet()) {
            if (!type.hasSlot(index)) {
                throw new IllegalArgumentException("Altar type " + type.name() + " has no slot at " + index);
            }

            Ingredient input = this.gridParts.get(index);
            if (input.hasNoMatchingItems()){
                throw new IllegalArgumentException("Input at " + index + " has no matching items!");
            }
        }
    }

    /*
    private AltarRecipeGrid centralizeGrid() {
        int shiftX = (GRID_SIZE - this.getWidth())  / 2;
        int shiftZ = (GRID_SIZE - this.getHeight()) / 2;

        Map<Integer, Ingredient> copy = Maps.newHashMap();
        for (Integer slotIndex : this.gridParts.keySet()) {
            int index = slotIndex + shiftX + (shiftZ * GRID_SIZE);
            copy.put(index, this.gridParts.get(slotIndex));
        }
        return new AltarRecipeGrid(copy, GRID_SIZE, GRID_SIZE);
    }

    private AltarRecipeGrid minimizeGrid() {
        Map<Integer, Ingredient> mappedIngredients = new HashMap<>(this.gridParts);

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
            mappedIngredients = MapStream.of(mappedIngredients)
                    .mapKey(slot -> slot - xShift)
                    .toMap();
        }
        if (firstRow > 0) {
            int yShift = firstRow * GRID_SIZE;
            mappedIngredients = MapStream.of(mappedIngredients)
                    .mapKey(slot -> slot - yShift)
                    .toMap();
        }
        return new AltarRecipeGrid(mappedIngredients, width, height);
    }
    */

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
        int gridParts = buffer.readInt();
        Map<Integer, Ingredient> ingredientMap = new HashMap<>();
        for (int i = 0; i < gridParts; i++) {
            int slot = buffer.readInt();
            Ingredient ingredient = Ingredient.read(buffer);
            ingredientMap.put(slot, ingredient);
        }
        return new AltarRecipeGrid(ingredientMap, width, height);
    }

    public void serialize(JsonObject object) {
        JsonArray pattern = new JsonArray();
        JsonObject keys = new JsonObject();

        Map<JsonElement, String> revMap = new HashMap<>();
        Map<String, JsonElement> ingredientMap = new HashMap<>();
        Map<Integer, String> patternMap = new HashMap<>();
        char c = 'A';

        for (Map.Entry<Integer, Ingredient> entry : this/*.centralizeGrid()*/.gridParts.entrySet()) {
            Integer slotIndex = entry.getKey();
            Ingredient value = entry.getValue();
            JsonElement jsonIngredient = value.serialize();
            if (!revMap.containsKey(jsonIngredient)) {
                String strKey = String.valueOf(c);
                revMap.put(jsonIngredient, strKey);
                patternMap.put(slotIndex, strKey);
                ingredientMap.put(strKey, jsonIngredient);
                c++;
            } else {
                patternMap.put(slotIndex, revMap.get(jsonIngredient));
            }
        }

        for (int xx = 0; xx < GRID_SIZE; xx++) {
            StringBuilder line = new StringBuilder();
            for (int zz = 0; zz < GRID_SIZE; zz++) {
                int slotIndex = xx * GRID_SIZE + zz;
                line.append(patternMap.getOrDefault(slotIndex, "_"));
            }
            pattern.add(line.toString());
        }
        object.add("pattern", pattern);

        ingredientMap.forEach((key, ingredient) -> keys.add(String.valueOf(key), ingredient));
        object.add("key", keys);
    }

    public static AltarRecipeGrid deserialize(AltarType type, JsonObject json) throws JsonSyntaxException {
        JsonArray pattern = JSONUtils.getJsonArray(json, "pattern");
        JsonObject keys = JSONUtils.getJsonObject(json, "key");

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
        for (Map.Entry<String, JsonElement> jEntry : keys.entrySet()) {
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
        return new AltarRecipeGrid(mappedIngredients);
    }

    public static class Builder {

        private LinkedList<String> pattern = Lists.newLinkedList();
        private Map<Character, Ingredient> inputMapping = Maps.newHashMap();

        public Builder patternLine(String line) {
            if (line.length() > GRID_SIZE) {
                throw new IllegalArgumentException("Altar recipe pattern line must not be more than 5 characters long! Passed line '" + line + "'");
            }
            if (this.pattern.size() >= GRID_SIZE) {
                throw new IllegalArgumentException("Altar recipe pattern must not have more than 5 lines total!");
            }
            this.pattern.add(line);
            return this;
        }

        public Builder key(Character key, Tag<Item> tagIn) {
            return this.key(key, Ingredient.fromTag(tagIn));
        }

        public Builder key(Character key, IItemProvider itemIn) {
            return this.key(key, Ingredient.fromItems(itemIn));
        }

        public Builder key(Character key, Fluid fluid) {
            return this.key(key, new FluidIngredient(new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME)));
        }

        public Builder key(Character key, Ingredient input) {
            if (this.inputMapping.containsKey(key)) {
                throw new IllegalArgumentException("Character '" + key + "' is already defined!");
            }
            if (key.equals(' ') || key.equals('_')) {
                throw new IllegalArgumentException("Character ' ' (whitespace) or '_' (underscore) is reserved and cannot be defined!");
            }
            this.inputMapping.put(key, input);
            return this;
        }

        public AltarRecipeGrid build() {
            int mostWidth = this.pattern.stream()
                    .map(String::length)
                    .max(Integer::compareTo)
                    .orElseThrow(() -> new IllegalArgumentException("No pattern is defined for altar recipe!"));
            int mostHeight = (int) this.pattern.stream()
                    .filter(s -> !s.isEmpty())
                    .count();
            if (mostHeight == 0 || mostWidth == 0) {
                throw new IllegalArgumentException("Altar recipe grid pattern is empty!");
            }
            int shiftZ = (GRID_SIZE - mostHeight) / 2;
            for (int i = 0; i < shiftZ; i++) {
                this.pattern.addFirst(StringUtils.repeat('_', GRID_SIZE));
            }
            for (int i = 0; i < (GRID_SIZE - mostHeight - shiftZ); i++) {
                this.pattern.add(StringUtils.repeat('_', GRID_SIZE));
            }

            List<String> patternLines = new LinkedList<>();
            int shiftX = (GRID_SIZE - mostWidth) / 2;
            for (String line : this.pattern) {
                String newLine = StringUtils.repeat("_", shiftX) + line + StringUtils.repeat("_", GRID_SIZE - mostWidth - shiftX);
                patternLines.add(newLine);
            }

            HashSet<Character> foundCharacters = new HashSet<>();
            for (String line : patternLines) {
                for (char c : line.toCharArray()) {
                    if (!SKIP_CHARS.matcher(String.valueOf(c)).matches()) {
                        foundCharacters.add(c);
                    }
                }
            }
            if (!this.inputMapping.keySet().containsAll(foundCharacters)) {
                String missingCharacters = foundCharacters.stream()
                        .filter(key -> !this.inputMapping.containsKey(key))
                        .map(String::valueOf)
                        .collect(Collectors.joining(", "));
                throw new IllegalArgumentException("No matching input found for characters " + missingCharacters);
            }

            Map<Integer, Ingredient> ingredientMap = new HashMap<>();
            for (int lineIndex = 0; lineIndex < patternLines.size(); lineIndex++) {
                char[] charArray = patternLines.get(lineIndex).toCharArray();
                for (int cIndex = 0; cIndex < charArray.length; cIndex++) {
                    Character c = charArray[cIndex];
                    if (!SKIP_CHARS.matcher(String.valueOf(c)).matches()) {
                        int slotIndex = lineIndex * GRID_SIZE + cIndex;
                        ingredientMap.put(slotIndex, this.inputMapping.get(c));
                    }
                }
            }
            return new AltarRecipeGrid(ingredientMap);
        }
    }

}
