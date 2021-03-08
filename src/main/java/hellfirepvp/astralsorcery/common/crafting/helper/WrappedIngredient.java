/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import hellfirepvp.astralsorcery.common.util.IngredientHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.crafting.CraftingHelper;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WrappedIngredient
 * Created by HellFirePvP
 * Date: 25.09.2019 / 18:19
 */
public class WrappedIngredient {

    private final Ingredient ingredient;

    public WrappedIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public ItemStack getRandomMatchingStack(long tick) {
        return IngredientHelper.getRandomVisibleStack(this.getIngredient(), tick);
    }

    @Nullable
    public static WrappedIngredient deserialize(CompoundNBT tag) {
        if (!tag.contains("ingredient")) {
            return null;
        }
        JsonElement jsonElement = new JsonParser().parse(tag.getString("ingredient"));
        return new WrappedIngredient(CraftingHelper.getIngredient(jsonElement));
    }

    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("ingredient", this.ingredient.serialize().toString());
        return tag;
    }
}
