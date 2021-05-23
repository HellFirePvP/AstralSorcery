/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2021
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.crt;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.*;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.*;
import com.blamejared.crafttweaker.impl.item.MCItemStackMutable;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInfusion;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InfusionManager
 * Created by Jaredlll08
 * Date: 03.17.2021 / 15:36
 */
@ZenRegister
@ZenCodeType.Name("mods.astralsorcery.InfusionManager")
public class InfusionManager implements IRecipeManager {
    
    @ZenCodeType.Method
    public void addRecipe(String name, IItemStack itemOutput, IIngredient itemInput, Fluid liquidInput, int craftingTickTime, float consumptionChance, boolean consumeMultipleFluids, boolean acceptChaliceInput, boolean copyNBTToOutputs) {
        name = fixRecipeName(name);
        ResourceLocation recipeId = new ResourceLocation(name);
        LiquidInfusion recipe = new LiquidInfusion(recipeId, craftingTickTime, liquidInput, itemInput.asVanillaIngredient(), itemOutput.getInternal(), consumptionChance, consumeMultipleFluids, acceptChaliceInput, copyNBTToOutputs);
        CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
    }
    
    @Override
    public void removeRecipe(IItemStack output) {
        CraftTweakerAPI.apply(new ActionRemoveRecipe(this, iRecipe -> {
            
            if(iRecipe instanceof LiquidInfusion) {
                LiquidInfusion recipe = (LiquidInfusion) iRecipe;
                return output.matches(new MCItemStackMutable(recipe.getOutput(ItemStack.EMPTY)));
            }
            return false;
        }, action -> "Removing \"" + action.getRecipeTypeName() + "\" recipes with output: " + output + "\""));
    }
    
    @Override
    public IRecipeType<LiquidInfusion> getRecipeType() {
        return RecipeTypesAS.TYPE_INFUSION.getType();
    }
}
