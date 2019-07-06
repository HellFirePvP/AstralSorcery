/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.serializer;

import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.crafting.helper.ingredient.FluidIngredient;
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefaction;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WellRecipeSerializer
 * Created by HellFirePvP
 * Date: 30.06.2019 / 23:29
 */
public class WellRecipeSerializer implements IRecipeSerializer<WellLiquefaction> {

    @Override
    public WellLiquefaction read(ResourceLocation recipeId, JsonObject json) {
        return null;
    }

    @Override
    public WellLiquefaction read(ResourceLocation recipeId, PacketBuffer buffer) {
        return null;
    }

    @Override
    public void write(PacketBuffer buffer, WellLiquefaction recipe) {
        recipe.getInput().write(buffer);
        Ingredient.read(buffer);
    }

    @Override
    public ResourceLocation getName() {
        return RecipeSerializersAS.WELL_LIQUEFACTION;
    }

    @Override
    public IRecipeSerializer<?> setRegistryName(ResourceLocation name) {
        return null;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return null;
    }

    @Override
    public Class<IRecipeSerializer<?>> getRegistryType() {
        return null;
    }
}
