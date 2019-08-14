/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.serializer;

import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeSerializer;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleAltarRecipeSerializer
 * Created by HellFirePvP
 * Date: 12.08.2019 / 19:45
 */
public class SimpleAltarRecipeSerializer extends CustomRecipeSerializer<SimpleAltarRecipe> {

    public SimpleAltarRecipeSerializer() {
        super(RecipeSerializersAS.SIMPLE_ALTAR_CRAFTING);
    }

    @Override
    public SimpleAltarRecipe read(ResourceLocation recipeId, JsonObject json) {
        return null;
    }

    @Override
    public SimpleAltarRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        return null;
    }

    @Override
    public void write(PacketBuffer buffer, SimpleAltarRecipe recipe) {

    }
}
