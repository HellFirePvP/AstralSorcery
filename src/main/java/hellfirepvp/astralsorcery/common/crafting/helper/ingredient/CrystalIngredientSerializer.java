/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper.ingredient;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.common.crafting.IIngredientSerializer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalIngredientSerializer
 * Created by HellFirePvP
 * Date: 28.09.2019 / 10:06
 */
public class CrystalIngredientSerializer implements IIngredientSerializer<CrystalIngredient> {

    @Override
    public CrystalIngredient parse(JsonObject json) {
        boolean hasToBeAttuned = JSONUtils.getBoolean(json, "hasToBeAttuned", false);
        boolean hasToBeCelestial = JSONUtils.getBoolean(json, "hasToBeCelestial", false);
        boolean canBeAttuned = JSONUtils.getBoolean(json, "canBeAttuned", true);
        boolean canBeCelestialCrystal = JSONUtils.getBoolean(json, "canBeCelestialCrystal", true);
        return new CrystalIngredient(hasToBeAttuned, hasToBeCelestial, canBeAttuned, canBeCelestialCrystal);
    }

    @Override
    public CrystalIngredient parse(PacketBuffer buffer) {
        return new CrystalIngredient(buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
    }

    @Override
    public void write(PacketBuffer buffer, CrystalIngredient ingredient) {
        buffer.writeBoolean(ingredient.hasToBeAttuned());
        buffer.writeBoolean(ingredient.hasToBeCelestial());
        buffer.writeBoolean(ingredient.canBeAttuned());
        buffer.writeBoolean(ingredient.canBeCelestialCrystal());
    }
}
