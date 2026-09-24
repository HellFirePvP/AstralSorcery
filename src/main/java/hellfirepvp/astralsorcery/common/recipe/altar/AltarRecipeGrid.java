/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.ingredient.IngredientBridge;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.CharacterCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarRecipeGrid
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarRecipeGrid {

    public static final Codec<AltarRecipeGrid> CODEC = GridData.MAP_CODEC.codec().flatXmap(
            AltarRecipeGrid::createGrid,
            grid -> Optional.ofNullable(grid.data)
                    .map(DataResult::success)
                    .orElseGet(() -> DataResult.error(() -> "Grid is unpacked and cannot be serialized"))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AltarRecipeGrid> STREAM_CODEC =
            StreamCodec.of(AltarRecipeGrid::write, AltarRecipeGrid::read);

    private final NonNullList<IngredientBridge> inputs = NonNullList.withSize(9, IngredientBridge.EMPTY);
    private final NonNullList<IngredientBridge> relayInputs = NonNullList.withSize(25, IngredientBridge.EMPTY);
    @Nullable
    private final GridData data;

    private AltarRecipeGrid(@Nullable GridData data) {
        this.data = data;
    }

    public List<IngredientBridge> getInputs() {
        return Collections.unmodifiableList(this.inputs);
    }

    public List<IngredientBridge> getRelayInputs() {
        return Collections.unmodifiableList(this.relayInputs);
    }

    public static AltarRecipeGrid create(Map<Character, IngredientBridge> ingredientKeys, List<String> pattern, List<String> relayPattern) {
        AltarRecipeGrid.GridData data = new AltarRecipeGrid.GridData(ingredientKeys, pattern, relayPattern);
        return createGrid(data).getOrThrow();
    }

    private static DataResult<AltarRecipeGrid> createGrid(GridData data) {
        AltarRecipeGrid grid = new AltarRecipeGrid(data);

        for (int line = 0; line < data.pattern.size(); line++) {
            String patternLine = data.pattern.get(line);
            for (int column = 0; column < patternLine.length(); column++) {
                char patternChar = patternLine.charAt(column);

                IngredientBridge input = Character.isWhitespace(patternChar) ? IngredientBridge.EMPTY : data.ingredientKeys.get(patternChar);
                if (input == null) {
                    return DataResult.error(() -> "Pattern references character '" + patternChar + "' but it's not defined in the key");
                }

                grid.inputs.set(line * 3 + column, input);
            }
        }
        for (int line = 0; line < data.relayPattern.size(); line++) {
            String relayLine = data.relayPattern.get(line);
            for (int column = 0; column < relayLine.length(); column++) {
                char patternChar = relayLine.charAt(column);

                if (line == 2 && column == 2 && !Character.isWhitespace(patternChar)) {
                    return DataResult.error(() -> "Relay pattern cannot have a input at the center.");
                }

                IngredientBridge input = Character.isWhitespace(patternChar) ? IngredientBridge.EMPTY : data.ingredientKeys.get(patternChar);
                if (input == null) {
                    return DataResult.error(() -> "Relay pattern references character '" + patternChar + "' but it's not defined in the key");
                }

                grid.relayInputs.set(line * 5 + column, input);
            }
        }
        return DataResult.success(grid);
    }

    private static void write(RegistryFriendlyByteBuf buf, AltarRecipeGrid recipe) {
        for (IngredientBridge ingredient : recipe.inputs) {
            IngredientBridge.STREAM_CODEC.encode(buf, ingredient);
        }
        for (IngredientBridge ingredient : recipe.relayInputs) {
            IngredientBridge.STREAM_CODEC.encode(buf, ingredient);
        }
    }

    private static AltarRecipeGrid read(RegistryFriendlyByteBuf buf) {
        AltarRecipeGrid grid = new AltarRecipeGrid(null);
        grid.inputs.replaceAll(ingredient -> IngredientBridge.STREAM_CODEC.decode(buf));
        grid.relayInputs.replaceAll(ingredient -> IngredientBridge.STREAM_CODEC.decode(buf));
        return grid;
    }

    public boolean matches(List<ItemStack> gridInputs, List<ItemStack> relayInputs) {
        if (gridInputs.size() != this.inputs.size() || relayInputs.size() != this.relayInputs.size()) {
            return false;
        }
        for (int slot = 0; slot < this.inputs.size(); slot++) {
            IngredientBridge ingredient = this.inputs.get(slot);
            ItemStack gridStack = gridInputs.get(slot);
            if (!ingredient.isEmpty() && !ingredient.test(gridStack)) {
                return false;
            }
            if (ingredient.isEmpty() && !gridStack.isEmpty()) {
                return false;
            }
        }
        for (int relay = 0; relay < this.relayInputs.size(); relay++) {
            IngredientBridge ingredient = this.relayInputs.get(relay);
            ItemStack relayStack = relayInputs.get(relay);
            if (!ingredient.isEmpty() && !ingredient.test(relayStack)) {
                return false;
            }
            if (ingredient.isEmpty() && !relayStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public record GridData(Map<Character, IngredientBridge> ingredientKeys, List<String> pattern, List<String> relayPattern) {
        public static final MapCodec<GridData> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.unboundedMap(CharacterCodec.CODEC_NON_BLANK, IngredientBridge.CODEC.codec()).fieldOf("key").forGetter(GridData::ingredientKeys),
                CodecUtil.stringSized(3, 3).listOf(3, 3).fieldOf("pattern").forGetter(GridData::pattern),
                CodecUtil.stringSized(5, 5).listOf(5, 5).fieldOf("relayPattern").forGetter(GridData::relayPattern)
        ).apply(inst, GridData::new));
    }
}
