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
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.*;
import com.blamejared.crafttweaker.impl.entity.MCEntityType;
import com.blamejared.crafttweaker.impl.item.MCItemStackMutable;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInteraction;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.*;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidInteractionManager
 * Created by Jaredlll08
 * Date: 03.17.2021 / 15:36
 */
@ZenRegister
@ZenCodeType.Name("mods.astralsorcery,LiquidInteractionManager")
public class LiquidInteractionManager implements IRecipeManager {
    
    @ZenCodeType.Method
    public void addRecipe(String name, IItemStack output, IFluidStack reactant1, float chanceConsumeReactant1, IFluidStack reactant2, float chanceConsumeReactant2, int weight) {
        name = fixRecipeName(name);
        ResourceLocation recipeId = new ResourceLocation(name);
        LiquidInteraction recipe = new LiquidInteraction(recipeId, reactant1.getInternal(), chanceConsumeReactant1, reactant2.getInternal(), chanceConsumeReactant2, weight, ResultDropItem.dropItem(output.getInternal()));
        CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe, "Drop Item"));
    }
    
    @ZenCodeType.Method
    public void addRecipe(String name, MCEntityType output, IFluidStack reactant1, float chanceConsumeReactant1, IFluidStack reactant2, float chanceConsumeReactant2, int weight) {
        name = fixRecipeName(name);
        ResourceLocation recipeId = new ResourceLocation(name);
        LiquidInteraction recipe = new LiquidInteraction(recipeId, reactant1.getInternal(), chanceConsumeReactant1, reactant2.getInternal(), chanceConsumeReactant2, weight, ResultSpawnEntity.spawnEntity(output.getInternal()));
        CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe, "Spawn Entity"));
    }
    
    @Override
    public void removeRecipe(IItemStack output) {
        CraftTweakerAPI.apply(new ActionRemoveRecipe(this, iRecipe -> {
            LiquidInteraction recipe = (LiquidInteraction) iRecipe;
            InteractionResult result = recipe.getResult();
            if(result instanceof ResultDropItem) {
                ResultDropItem resultDropItem = (ResultDropItem) result;
                return output.matches(new MCItemStackMutable(resultDropItem.getOutput()));
            }
            return false;
        }).describeDefaultRemoval(output));
    }
    
    @ZenCodeType.Method
    public void removeRecipe(MCEntityType entityType) {
        CraftTweakerAPI.apply(new ActionRemoveRecipe(this, iRecipe -> {
            LiquidInteraction recipe = (LiquidInteraction) iRecipe;
            InteractionResult result = recipe.getResult();
            if(result instanceof ResultSpawnEntity) {
                ResultSpawnEntity resultSpawnEntity = (ResultSpawnEntity) result;
                return entityType.getInternal() == resultSpawnEntity.getEntityType();
            }
            return false;
        }).describeDefaultRemoval(entityType));
    }
    
    @Override
    public IRecipeType<LiquidInteraction> getRecipeType() {
        return RecipeTypesAS.TYPE_LIQUID_INTERACTION.getType();
    }
}
