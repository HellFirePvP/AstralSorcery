package hellfirepvp.astralsorcery.common.integration.crt;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefaction;
import hellfirepvp.astralsorcery.common.integration.crt.actions.ActionRemoveWellRecipe;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

import java.awt.*;

@ZenRegister
@ZenCodeType.Name("mods.astralsorcery.WellManager")
public class WellManager implements IRecipeManager {

    @ZenCodeType.Method
    public void addRecipe(String name, Fluid output, IIngredient input, float productionMultiplier, float shatterMultiplier, @ZenCodeType.OptionalInt(0x00FF55FF) int color) {
        name = fixRecipeName(name);
        ResourceLocation recipeId = new ResourceLocation(name);
        WellLiquefaction recipe = new WellLiquefaction(recipeId, input.asVanillaIngredient(), output, new Color(color, true), productionMultiplier, shatterMultiplier);
        CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe, ""));
    }

    @Override
    public void removeRecipe(IItemStack output) {

        throw new UnsupportedOperationException("Cannot remove Astral Sorcery Well Liquefaction recipes by IItemStacks, use the Fluid method instead!");
    }

    @ZenCodeType.Method
    public void removeRecipe(Fluid output) {

        CraftTweakerAPI.apply(new ActionRemoveWellRecipe(this, output));
    }

    @Override
    public IRecipeType<WellLiquefaction> getRecipeType() {

        return RecipeTypesAS.TYPE_WELL.getType();
    }

}
