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
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInfusion;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidInfusionSerializer
 * Created by HellFirePvP
 * Date: 26.07.2019 / 21:30
 */
public class LiquidInfusionSerializer extends CustomRecipeSerializer<LiquidInfusion> {

    public LiquidInfusionSerializer() {
        super(RecipeSerializersAS.LIQUID_INFUSION);
    }

    @Override
    public LiquidInfusion read(ResourceLocation recipeId, JsonObject json) {
        return null;
    }

    @Override
    public LiquidInfusion read(ResourceLocation recipeId, PacketBuffer buffer) {
        return null;
    }

    @Override
    public void write(PacketBuffer buffer, LiquidInfusion recipe) {

    }
}
