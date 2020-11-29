/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.serializer;

import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeSerializer;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInteraction;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidInteractionSerializer
 * Created by HellFirePvP
 * Date: 29.10.2020 / 20:45
 */
public class LiquidInteractionSerializer extends CustomRecipeSerializer<LiquidInteraction> {

    public LiquidInteractionSerializer() {
        super(RecipeSerializersAS.LIQUID_INTERACTION);
    }

    @Override
    public LiquidInteraction read(ResourceLocation recipeId, JsonObject json) {
        return LiquidInteraction.read(recipeId, json);
    }

    @Override
    public void write(JsonObject object, LiquidInteraction recipe) {
        recipe.write(object);
    }

    @Nullable
    @Override
    public LiquidInteraction read(ResourceLocation recipeId, PacketBuffer buffer) {
        return LiquidInteraction.read(recipeId, buffer);
    }

    @Override
    public void write(PacketBuffer buffer, LiquidInteraction recipe) {
        recipe.write(buffer);
    }
}
