/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationCrafttweaker;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.BaseTweaker;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network.LiquidInteractionAdd;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network.LiquidInteractionRemove;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidInteraction
 * Created by HellFirePvP
 * Date: 20.04.2018 / 09:53
 */
@ZenClass("mods.astralsorcery.LiquidInteraction")
public class LiquidInteraction extends BaseTweaker {

    protected static final String name = "AstralSorcery LiquidInteraction";

    @ZenMethod
    public static void removeInteraction(ILiquidStack liquid1, ILiquidStack liquid2) {
        removeInteraction(liquid1, liquid2, null);
    }

    @ZenMethod
    public static void removeInteraction(ILiquidStack liquid1, ILiquidStack liquid2, IItemStack output) {
        FluidStack comp1 = convertToFluidStack(liquid1, true);
        FluidStack comp2 = convertToFluidStack(liquid2, true);
        ItemStack out = convertToItemStack(output);

        ModIntegrationCrafttweaker.recipeModifications.add(new LiquidInteractionRemove(comp1, comp2, out));
    }

    @ZenMethod
    public static void addInteraction(ILiquidStack liquidIn1, float chanceConsumption1, ILiquidStack liquidIn2, float chanceConsumption2, int weight, IItemStack output) {
        ItemStack out = convertToItemStack(output);
        if (out.isEmpty()) {
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe-removal due to invalid/empty item output.");
            return;
        }
        FluidStack in1 = convertToFluidStack(liquidIn1, false);
        if (in1 == null || in1.getFluid() == null) {
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe-removal due to invalid/empty fluid input.");
            return;
        }
        FluidStack in2 = convertToFluidStack(liquidIn2, false);
        if (in2 == null || in2.getFluid() == null) {
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe-removal due to invalid/empty fluid input.");
            return;
        }

        weight = Math.max(0, weight);
        chanceConsumption1 = Math.max(0, chanceConsumption1);
        chanceConsumption2 = Math.max(0, chanceConsumption2);

        ModIntegrationCrafttweaker.recipeModifications.add(new LiquidInteractionAdd(in1, in2, chanceConsumption1, chanceConsumption2, out, weight));
    }

}
