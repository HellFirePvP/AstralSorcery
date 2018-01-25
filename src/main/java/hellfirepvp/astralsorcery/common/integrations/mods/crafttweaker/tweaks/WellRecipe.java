/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks;

import crafttweaker.CraftTweakerAPI;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationCrafttweaker;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.BaseTweaker;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network.WellRecipeAdd;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network.WellRecipeRemove;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WellRecipe
 * Created by HellFirePvP
 * Date: 28.02.2017 / 00:03
 */
@ZenClass("mods.astralsorcery.Lightwell")
public class WellRecipe extends BaseTweaker {

    protected static final String name = "AstralSorcery Lightwell";

    @ZenMethod
    public static void removeLiquefaction(IItemStack input, ILiquidStack output) {
        FluidStack fs = convertToFluidStack(output, false);

        ItemStack in = convertToItemStack(input);
        if (in.isEmpty()) {
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe-removal due to invalid input.");
            return;
        }

        ModIntegrationCrafttweaker.recipeModifications.add(new WellRecipeRemove(in, fs == null ? null : fs.getFluid()));
    }

    @ZenMethod
    public static void addLiquefaction(IItemStack input, ILiquidStack output, float productionMultiplier, float shatterMultiplier, int colorhex) {
        ItemStack in = convertToItemStack(input);
        if (in.isEmpty()) {
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe-add due to invalid input itemstack.");
            return;
        }

        FluidStack fs = convertToFluidStack(output, false);
        if (fs == null) {
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe-add due to invalid output fluid.");
            return;
        }

        ModIntegrationCrafttweaker.recipeModifications.add(new WellRecipeAdd(in, fs.getFluid(), productionMultiplier, shatterMultiplier, colorhex));
    }

}
