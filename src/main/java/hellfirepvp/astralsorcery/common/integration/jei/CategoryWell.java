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
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefaction;
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
public class CategoryWell extends JEICategory<WellLiquefaction> {

    private final IDrawable background, icon;

    public CategoryWell(IGuiHelper guiHelper) {
        super(IntegrationJEI.CATEGORY_WELL);
        this.background = guiHelper.createDrawable(AstralSorcery.key("textures/gui/jei/lightwell.png"), 0, 0, 116, 54);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(BlocksAS.WELL));
    }

    @Override
    public Class<? extends WellLiquefaction> getRecipeClass() {
        return WellLiquefaction.class;
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
    public void draw(WellLiquefaction recipe, double mouseX, double mouseY) {
        this.icon.draw(46, 20);
    }

    @Override
    public List<WellLiquefaction> getRecipes() {
        return RecipeTypesAS.TYPE_WELL.getAllRecipes();
    }

    @Override
    public void setIngredients(WellLiquefaction wellLiquefaction, IIngredients ingredients) {
        ImmutableList.Builder<List<FluidStack>> fluidOutputs = ImmutableList.builder();
        ImmutableList.Builder<List<ItemStack>> itemInputs = ImmutableList.builder();

        itemInputs.add(ingredientStacks(wellLiquefaction.getInput()));
        fluidOutputs.add(Collections.singletonList(new FluidStack(wellLiquefaction.getFluidOutput(), FluidAttributes.BUCKET_VOLUME)));

        ingredients.setInputLists(VanillaTypes.ITEM, itemInputs.build());
        ingredients.setOutputLists(VanillaTypes.FLUID, fluidOutputs.build());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, WellLiquefaction wellLiquefaction, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();

        itemStacks.init(0, true, 2, 18);
        initFluidOutput(fluidStacks, 1, 94, 18);

        itemStacks.set(ingredients);
        fluidStacks.set(ingredients);
    }
}
