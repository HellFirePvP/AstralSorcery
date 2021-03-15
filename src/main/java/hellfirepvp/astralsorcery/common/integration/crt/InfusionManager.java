package hellfirepvp.astralsorcery.common.integration.crt;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInfusion;
import hellfirepvp.astralsorcery.common.integration.crt.actions.ActionRemoveInfusionRecipe;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.astralsorcery.InfusionManager")
public class InfusionManager implements IRecipeManager {

    @ZenCodeType.Method
    public void addRecipe(String name, IItemStack itemOutput, IIngredient itemInput, Fluid liquidInput, int craftingTickTime, float consumptionChance, boolean consumeMultipleFluids, boolean acceptChaliceInput, boolean copyNBTToOutputs) {
        name = fixRecipeName(name);
        ResourceLocation recipeId = new ResourceLocation(name);
        LiquidInfusion recipe = new LiquidInfusion(recipeId, craftingTickTime, liquidInput, itemInput.asVanillaIngredient(), itemOutput.getInternal(), consumptionChance, consumeMultipleFluids, acceptChaliceInput, copyNBTToOutputs);
        CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe, ""));
    }

    @Override
    public void removeRecipe(IItemStack output) {
        CraftTweakerAPI.apply(new ActionRemoveInfusionRecipe(this, output));
    }

    @Override
    public IRecipeType<LiquidInfusion> getRecipeType() {
        return RecipeTypesAS.TYPE_INFUSION.getType();
    }
}
