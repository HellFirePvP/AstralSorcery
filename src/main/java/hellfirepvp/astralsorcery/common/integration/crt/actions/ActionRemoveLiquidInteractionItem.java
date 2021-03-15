package hellfirepvp.astralsorcery.common.integration.crt.actions;

import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionRecipeBase;
import com.blamejared.crafttweaker.impl.item.MCItemStackMutable;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInteraction;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.InteractionResult;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.ResultDropItem;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.Iterator;
import java.util.Map;

public class ActionRemoveLiquidInteractionItem extends ActionRecipeBase {
    private final IItemStack output;

    public ActionRemoveLiquidInteractionItem(IRecipeManager manager, IItemStack output) {
        super(manager);
        this.output = output;
    }

    @Override
    public void apply() {
        Map<ResourceLocation, IRecipe<?>> recipes = this.getManager().getRecipes();
        Iterator<ResourceLocation> iterator = recipes.keySet().iterator();
        while (iterator.hasNext()) {
            ResourceLocation recipeId = iterator.next();
            LiquidInteraction recipe = (LiquidInteraction) recipes.get(recipeId);
            InteractionResult result = recipe.getResult();
            if (result instanceof ResultDropItem) {
                ResultDropItem resultDropItem = (ResultDropItem) result;
                if (output.matches(new MCItemStackMutable(resultDropItem.getOutput()))) {
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public String describe() {
        return "Removing \"" + Registry.RECIPE_TYPE.getKey(this.getManager().getRecipeType()) + "\" recipes with entity output: " + output.getCommandString() + "\"";
    }
}
