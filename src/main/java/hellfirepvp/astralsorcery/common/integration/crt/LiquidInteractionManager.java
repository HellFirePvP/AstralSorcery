package hellfirepvp.astralsorcery.common.integration.crt;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl.entity.MCEntityType;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInteraction;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.ResultDropItem;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.ResultSpawnEntity;
import hellfirepvp.astralsorcery.common.integration.crt.actions.ActionRemoveLiquidInteractionEntity;
import hellfirepvp.astralsorcery.common.integration.crt.actions.ActionRemoveLiquidInteractionItem;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

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
        CraftTweakerAPI.apply(new ActionRemoveLiquidInteractionItem(this, output));
    }

    @ZenCodeType.Method
    public void removeRecipe(MCEntityType entityType) {
        CraftTweakerAPI.apply(new ActionRemoveLiquidInteractionEntity(this, entityType));
    }

    @Override
    public IRecipeType<LiquidInteraction> getRecipeType() {
        return RecipeTypesAS.TYPE_LIQUID_INTERACTION.getType();
    }
}
