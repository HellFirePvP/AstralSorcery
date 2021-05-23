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
import com.blamejared.crafttweaker.impl_native.fluid.ExpandFluid;
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefaction;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WellManager
 * Created by Jaredlll08
 * Date: 03.17.2021 / 15:36
 */
@ZenRegister
@ZenCodeType.Name("mods.astralsorcery.WellManager")
public class WellManager implements IRecipeManager {
    
    @ZenCodeType.Method
    public void addRecipe(String name, Fluid output, IIngredient input, float productionMultiplier, float shatterMultiplier, @ZenCodeType.OptionalInt(0x00FF55FF) int color) {
        name = fixRecipeName(name);
        ResourceLocation recipeId = new ResourceLocation(name);
        WellLiquefaction recipe = new WellLiquefaction(recipeId, input.asVanillaIngredient(), output, new Color(color, true), productionMultiplier, shatterMultiplier);
        CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
    }
    
    @Override
    public void removeRecipe(IItemStack output) {
        
        throw new UnsupportedOperationException("Cannot remove Astral Sorcery Well Liquefaction recipes by IItemStacks, use the Fluid method instead!");
    }
    
    @ZenCodeType.Method
    public void removeRecipe(Fluid output) {
        
        CraftTweakerAPI.apply(new ActionRemoveRecipe(this, iRecipe -> {
            
            if(iRecipe instanceof WellLiquefaction) {
                WellLiquefaction recipe = (WellLiquefaction) iRecipe;
                
                return output == recipe.getFluidOutput();
            }
            return false;
        }, action -> "Removing Well Liquefaction recipes that output " + ExpandFluid.getCommandString(output)));
    }
    
    @Override
    public IRecipeType<WellLiquefaction> getRecipeType() {
        
        return RecipeTypesAS.TYPE_WELL.getType();
    }
    
}
