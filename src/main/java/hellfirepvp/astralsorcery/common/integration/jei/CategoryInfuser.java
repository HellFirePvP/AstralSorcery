/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.jei;

import com.google.common.collect.ImmutableList;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInfusion;
import hellfirepvp.astralsorcery.common.integration.IntegrationJEI;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CategoryInfuser
 * Created by HellFirePvP
 * Date: 05.09.2020 / 12:37
 */
public class CategoryInfuser extends JEICategory<LiquidInfusion> {

    private final IDrawable background, icon;

    public CategoryInfuser(IGuiHelper guiHelper) {
        super(IntegrationJEI.CATEGORY_INFUSER);
        this.background = guiHelper.createDrawable(AstralSorcery.key("textures/gui/jei/infuser.png"), 0, 0, 116, 162);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(BlocksAS.INFUSER));
    }

    @Override
    public Class<? extends LiquidInfusion> getRecipeClass() {
        return LiquidInfusion.class;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public List<LiquidInfusion> getRecipes() {
        return RecipeTypesAS.TYPE_INFUSION.getAllRecipes();
    }

    @Override
    public void setIngredients(LiquidInfusion liquidInfusion, IIngredients ingredients) {
        ImmutableList.Builder<List<FluidStack>> fluidInputs = ImmutableList.builder();
        ImmutableList.Builder<List<ItemStack>> itemInputs = ImmutableList.builder();
        ImmutableList.Builder<List<ItemStack>> itemOutputs = ImmutableList.builder();

        itemInputs.add(Arrays.asList(liquidInfusion.getItemInput().getMatchingStacks()));
        itemOutputs.add(Collections.singletonList(liquidInfusion.getOutputForRender(Collections.emptyList())));

        FluidStack fInput = new FluidStack(liquidInfusion.getLiquidInput(), FluidAttributes.BUCKET_VOLUME);
        for (int i = 0; i < 12; i++) {
            fluidInputs.add(Collections.singletonList(fInput.copy()));
        }

        ingredients.setInputLists(VanillaTypes.FLUID, fluidInputs.build());
        ingredients.setInputLists(VanillaTypes.ITEM, itemInputs.build());
        ingredients.setOutputLists(VanillaTypes.ITEM, itemOutputs.build());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, LiquidInfusion liquidInfusion, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();

        itemStacks.init(0, true, 49, 95);

        initFluidInput(fluidStacks, 1, 30, 57);
        initFluidInput(fluidStacks, 2, 49, 57);
        initFluidInput(fluidStacks, 3, 68, 57);
        initFluidInput(fluidStacks, 4, 11, 76);
        initFluidInput(fluidStacks, 5, 87, 76);
        initFluidInput(fluidStacks, 6, 11, 95);
        initFluidInput(fluidStacks, 7, 87, 95);
        initFluidInput(fluidStacks, 8, 11, 114);
        initFluidInput(fluidStacks, 9, 87, 114);
        initFluidInput(fluidStacks, 10, 30, 133);
        initFluidInput(fluidStacks, 11, 49, 133);
        initFluidInput(fluidStacks, 12, 68, 133);

        itemStacks.init(13, false, 48, 18);

        itemStacks.set(ingredients);
        fluidStacks.set(ingredients);
    }
}
