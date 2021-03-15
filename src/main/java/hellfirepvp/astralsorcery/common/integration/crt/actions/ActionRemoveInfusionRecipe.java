package hellfirepvp.astralsorcery.common.integration.crt.actions;

import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionRecipeBase;
import com.blamejared.crafttweaker.impl.item.MCItemStackMutable;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInfusion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.Iterator;
import java.util.Map;

public class ActionRemoveInfusionRecipe extends ActionRecipeBase {

    private final IItemStack output;

    public ActionRemoveInfusionRecipe(IRecipeManager manager, IItemStack output) {
        super(manager);
        this.output = output;
    }

    @Override
    public void apply() {
        Map<ResourceLocation, IRecipe<?>> recipes = this.getManager().getRecipes();
        Iterator<ResourceLocation> iterator = recipes.keySet().iterator();
        while (iterator.hasNext()) {
            ResourceLocation recipeId = iterator.next();
            LiquidInfusion recipe = (LiquidInfusion) recipes.get(recipeId);
            if (output.matches(new MCItemStackMutable(recipe.getOutput(ItemStack.EMPTY)))) {
                iterator.remove();
            }
        }
    }

    public String describe() {
        return "Removing \"" + Registry.RECIPE_TYPE.getKey(this.getManager().getRecipeType()) + "\" recipes with output: " + this.output + "\"";
    }
}
