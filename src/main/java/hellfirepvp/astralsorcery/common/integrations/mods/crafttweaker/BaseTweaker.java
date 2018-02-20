/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker;

import crafttweaker.api.minecraft.CraftTweakerMC;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BaseTweaker
 * Created by HellFirePvP
 * Date: 27.02.2017 / 00:57
 */
public abstract class BaseTweaker {

    @Nonnull
    public static ItemStack convertToItemStack(IItemStack stack) {
        return CraftTweakerMC.getItemStack(stack);
    }

    @Nullable
    public static FluidStack convertToFluidStack(ILiquidStack stack, boolean capAndLimitToBuckets) {
        FluidStack fs = CraftTweakerMC.getLiquidStack(stack);
        if(fs != null && capAndLimitToBuckets) {
            fs.amount = Fluid.BUCKET_VOLUME; //Only full buckets please...
        }
        return fs;
    }

    @Nullable
    public static ItemHandle convertToHandle(IIngredient obj) {
        if(obj == null) {
            return null;
        }
        if(obj instanceof IItemStack) {
            ItemStack ret = convertToItemStack((IItemStack) obj);
            if(ret.isEmpty()) return null;
            return new ItemHandle(ret);
        } else if(obj instanceof ILiquidStack) {
            FluidStack ret = convertToFluidStack((ILiquidStack) obj, true);
            if (ret == null) return null;
            return new ItemHandle(ret);
        } else if(obj instanceof IOreDictEntry) {
            return new ItemHandle(((IOreDictEntry) obj).getName());
        }
        return null;
    }

}
