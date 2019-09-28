/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JsonHelper
 * Created by HellFirePvP
 * Date: 19.07.2019 / 21:02
 */
public class JsonHelper {

    public static ItemStack getItemStack(JsonObject object, String key) {
        Item i = JSONUtils.getItem(object, key);
        ItemStack out = new ItemStack(i);

        String nbtKey = key + "Nbt";
        if (JSONUtils.hasField(object, nbtKey)) {
            try {
                CompoundNBT nbt = JsonToNBT.getTagFromJson(JSONUtils.getString(object.get(nbtKey), nbtKey));
                out.setTag(nbt);
            } catch (CommandSyntaxException e) {
                throw new JsonSyntaxException("Invalid nbt tag: " + e.getMessage(), e);
            }
        }
        String countKey = key + "Count";
        if (JSONUtils.hasField(object, countKey)) {
            int count = JSONUtils.getInt(object, countKey);
            out.setCount(MathHelper.clamp(count, 1, out.getMaxStackSize()));
        }
        return out;
    }

    public static Color getColor(JsonObject object, String key) {
        String value = JSONUtils.getString(object, key);
        if (value.startsWith("0x")) { //Assume hex color.
            String hexNbr = value.substring(2);
            try {
                return new Color(Integer.parseInt(hexNbr, 16), true);
            } catch (NumberFormatException exc) {
                throw new JsonParseException("Expected " + hexNbr + " to be a hexadecimal string!", exc);
            }
        } else {
            try {
                return new Color(Integer.parseInt(value), true);
            } catch (NumberFormatException exc) {
                try {
                    return new Color(Integer.parseInt(value, 16), true);
                } catch (NumberFormatException e) {
                    throw new JsonParseException("Expected " + value + " to be a int or hexadecimal-number!", e);
                }
            }
        }
    }

}
