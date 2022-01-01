/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2021
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.crt;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Arrays;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarManager
 * Created by Jaredlll08
 * Date: 03.17.2021 / 15:36
 */
@ZenRegister
@ZenCodeType.Name("mods.astralsorcery.AltarManager")
public class AltarManager implements IRecipeManager {

    @ZenCodeType.Method
    public void addRecipe(String name, String altarType, IItemStack output, IIngredient[][] ingredients, int duration, int starlightRequired) {
        name = fixRecipeName(name);
        if (Arrays.stream(AltarType.values()).map(Enum::name).noneMatch(s -> s.equalsIgnoreCase(altarType))) {
            throw new IllegalArgumentException("Unknown Astral Sorcery Altar Type: " + altarType);
        }
        if (ingredients.length != 5) {
            throw new IllegalArgumentException("Astral Sorcery Altar ingredients needs to be a 5x5 array with all values filled. Use <item:minecraft:air> to pad it out!");
        }
        for (IIngredient[] ingredient : ingredients) {
            if (ingredient.length != 5) {
                throw new IllegalArgumentException("Astral Sorcery Altar ingredients needs to be a 5x5 array with all values filled. Use <item:minecraft:air> to pad it out!");
            }
        }

        AltarRecipeGrid.Builder builder = AltarRecipeGrid.builder();
        builder.patternLine("ABCDE").patternLine("FGHIJ").patternLine("KLMNO").patternLine("PQRST").patternLine("UVWXY");
        int index = 'A';
        for (IIngredient[] ingredient : ingredients) {
            for (IIngredient iIngredient : ingredient) {
                builder.key((char) (index), iIngredient.asVanillaIngredient());
                index++;
            }
        }

        SimpleAltarRecipe recipe = new SimpleAltarRecipe(new ResourceLocation(name), AltarType.valueOf(altarType.toUpperCase()), duration, starlightRequired, builder.build());
        recipe.addOutput(output.getInternal());
        CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
    }

    @ZenCodeType.Method
    public void addRecipe(String name, String altarType, IItemStack output, String[] pattern, Map<String, IIngredient> ingredients, int duration, int starlightRequired) {
        name = fixRecipeName(name);
        if (Arrays.stream(AltarType.values()).map(Enum::name).noneMatch(s -> s.equalsIgnoreCase(altarType))) {
            throw new IllegalArgumentException("Unknown Astral Sorcery Altar Type: " + altarType);
        }
        if (pattern.length != 5) {
            throw new IllegalArgumentException("Astral Sorcery Altar ingredients needs to be a 5x5 array with all values filled.");
        }
        if (ingredients.keySet().stream().anyMatch(s -> s.length() != 1)) {
            throw new IllegalArgumentException("Cannot have multiple characters as pattern key!");
        }

        AltarRecipeGrid.Builder builder = AltarRecipeGrid.builder();
        for (String strings : pattern) {
            builder.patternLine(strings);
        }
        for (String character : ingredients.keySet()) {
            builder.key(character.charAt(0), ingredients.get(character).asVanillaIngredient());
        }

        SimpleAltarRecipe recipe = new SimpleAltarRecipe(new ResourceLocation(name), AltarType.valueOf(altarType.toUpperCase()), duration, starlightRequired, builder.build());
        recipe.addOutput(output.getInternal());
        CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
    }

    @Override
    public void removeRecipe(IItemStack output) {
        // Not all recipes return an output, and they all require a TileAltar, which we don't have.
        throw new UnsupportedOperationException("Cannot remove Altar recipes by their output, remove them by their name instead!");
    }

    @Override
    public IRecipeType<SimpleAltarRecipe> getRecipeType() {
        return RecipeTypesAS.TYPE_ALTAR.getType();
    }
}
