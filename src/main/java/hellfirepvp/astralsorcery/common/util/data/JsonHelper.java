/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JsonHelper
 * Created by HellFirePvP
 * Date: 19.07.2019 / 21:02
 */
public class JsonHelper {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    @Nonnull
    public static FluidStack getFluidStack(JsonElement fluidElement, String infoKey) {
        FluidStack fluidStack;
        if (fluidElement.isJsonPrimitive() && ((JsonPrimitive) fluidElement).isString()) {
            String strKey = fluidElement.getAsString();
            ResourceLocation fluidKey = new ResourceLocation(strKey);
            fluidStack = new FluidStack(Registry.FLUID.getValue(fluidKey).orElseThrow(
                    () -> new IllegalStateException("Fluid: " + strKey + " does not exist")), FluidAttributes.BUCKET_VOLUME);
        } else if (fluidElement.isJsonObject()) {
            fluidStack = getFluidStack(fluidElement.getAsJsonObject(), true);
        } else {
            throw new JsonSyntaxException("Missing " + infoKey + ", expected to find a string or object");
        }
        return fluidStack;
    }

    @Nonnull
    public static FluidStack getFluidStack(JsonObject json, boolean readNBT) {
        String fluidName = JSONUtils.getString(json, "fluid");
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));
        if (fluid == null || fluid == Fluids.EMPTY) {
            return FluidStack.EMPTY;
        }
        if (readNBT && json.has("nbt")) {
            //Copied from CraftingHelper.getItemStack's NBT deserialization.
            try {
                JsonElement element = json.get("nbt");
                CompoundNBT nbt;
                if (element.isJsonObject()) {
                    nbt = JsonToNBT.getTagFromJson(GSON.toJson(element));
                } else {
                    nbt = JsonToNBT.getTagFromJson(JSONUtils.getString(element, "nbt"));
                }

                CompoundNBT tempRead = new CompoundNBT();
                tempRead.put("Tag", nbt);
                tempRead.putString("FluidName", fluidName);
                tempRead.putInt("Amount", JSONUtils.getInt(json, "amount", FluidAttributes.BUCKET_VOLUME));

                return FluidStack.loadFluidStackFromNBT(tempRead);
            }
            catch (CommandSyntaxException e)
            {
                throw new JsonSyntaxException("Invalid NBT Entry: " + e.toString());
            }
        }
        return new FluidStack(fluid, JSONUtils.getInt(json, "amount", FluidAttributes.BUCKET_VOLUME));
    }

    @Nonnull
    public static ItemStack getItemStack(JsonElement itemElement, String infoKey) {
        ItemStack itemstack;
        if (itemElement.isJsonPrimitive() && ((JsonPrimitive) itemElement).isString()) {
            String strKey = itemElement.getAsString();
            ResourceLocation itemKey = new ResourceLocation(strKey);
            itemstack = new ItemStack(Registry.ITEM.getValue(itemKey).orElseThrow(
                    () -> new IllegalStateException("Item: " + strKey + " does not exist")));
        } else if (itemElement.isJsonObject()) {
            itemstack = CraftingHelper.getItemStack(itemElement.getAsJsonObject(), true);
        } else {
            throw new JsonSyntaxException("Missing " + infoKey + ", expected to find a string or object");
        }
        return itemstack;
    }

    @Nonnull
    public static ItemStack getItemStack(JsonObject root, String key) {
        if (!JSONUtils.hasField(root, key)) {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a string or object");
        }
        ItemStack itemstack;
        if (root.get(key).isJsonObject()) {
            itemstack = CraftingHelper.getItemStack(JSONUtils.getJsonObject(root, key), true);
        } else {
            String strKey = JSONUtils.getString(root, key);
            ResourceLocation itemKey = new ResourceLocation(strKey);
            itemstack = new ItemStack(Registry.ITEM.getValue(itemKey).orElseThrow(
                    () -> new IllegalStateException("Item: " + strKey + " does not exist")));
        }
        return itemstack;
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
