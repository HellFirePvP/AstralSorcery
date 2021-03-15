package hellfirepvp.astralsorcery.common.integration.crt.actions;

import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionRecipeBase;
import com.blamejared.crafttweaker.impl_native.fluid.ExpandFluid;
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefaction;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import java.util.Iterator;
import java.util.Map;

public class ActionRemoveWellRecipe extends ActionRecipeBase {
    private final Fluid output;

    public ActionRemoveWellRecipe(IRecipeManager manager, Fluid output) {
        super(manager);
        this.output = output;
    }

    @Override
    public void apply() {

        Map<ResourceLocation, IRecipe<?>> recipes = this.getManager().getRecipes();
        Iterator<ResourceLocation> iterator = recipes.keySet().iterator();
        while (iterator.hasNext()) {
            ResourceLocation recipeId = iterator.next();
            WellLiquefaction recipe = (WellLiquefaction) recipes.get(recipeId);

            if (output == recipe.getFluidOutput()) {
                iterator.remove();
            }
        }
    }

    @Override
    public String describe() {

        return "Removing Well Liquefaction recipes that output " + ExpandFluid.getCommandString(output);
    }
}
