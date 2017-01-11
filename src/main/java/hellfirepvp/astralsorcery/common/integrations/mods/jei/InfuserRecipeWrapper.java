/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.jei;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import hellfirepvp.astralsorcery.common.integrations.mods.ModIntegrationJEI;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InfuserRecipeWrapper
 * Created by HellFirePvP
 * Date: 11.01.2017 / 00:11
 */
public class InfuserRecipeWrapper implements IRecipeWrapper {

    private final AbstractInfusionRecipe recipe;

    public InfuserRecipeWrapper(AbstractInfusionRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        IStackHelper stackHelper = ModIntegrationJEI.stackHelper;

        ItemHandle inputHandle = recipe.getInput();
        switch (inputHandle.handleType) {
            case OREDICT:
                List<ItemStack> stacks = stackHelper.toItemStackList(inputHandle.getOreDictName());
                ingredients.setInputs(ItemStack.class, stacks);
                break;
            case STACK:
                ingredients.setInput(ItemStack.class, inputHandle.getApplicableItems().get(0));
                break;
            case FLUID:
                ingredients.setInput(FluidStack.class, inputHandle.getFluidTypeAndAmount());
                break;
        }

        ingredients.setOutput(ItemStack.class, recipe.getOutput(null));

    }

    @Override
    @Deprecated
    public List getInputs() {
        return Lists.newArrayList();
    }

    @Override
    @Deprecated
    public List getOutputs() {
        return Lists.newArrayList();
    }

    @Override
    @Deprecated
    public List<FluidStack> getFluidInputs() {
        return Lists.newArrayList();
    }

    @Override
    @Deprecated
    public List<FluidStack> getFluidOutputs() {
        return Lists.newArrayList();
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {}

    @Override
    public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight) {}

    @Nullable
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Lists.newArrayList();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}
