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
import hellfirepvp.astralsorcery.common.crafting.recipe.BlockTransmutation;
import hellfirepvp.astralsorcery.common.integration.IntegrationJEI;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;

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
public class CategoryTransmutation extends JEICategory<BlockTransmutation> {

    private final IDrawable background, icon;

    public CategoryTransmutation(IGuiHelper guiHelper) {
        super(IntegrationJEI.CATEGORY_TRANSMUTATION);
        this.background = guiHelper.createDrawable(AstralSorcery.key("textures/gui/jei/transmutation.png"), 0, 0, 116, 54);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(BlocksAS.LENS));
    }

    @Override
    public Class<? extends BlockTransmutation> getRecipeClass() {
        return BlockTransmutation.class;
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
    public Collection<BlockTransmutation> getRecipes() {
        return RecipeTypesAS.TYPE_BLOCK_TRANSMUTATION.getAllRecipes();
    }

    @Override
    public void setIngredients(BlockTransmutation transmutation, IIngredients ingredients) {
        ImmutableList.Builder<List<ItemStack>> itemInputs = ImmutableList.builder();
        ImmutableList.Builder<List<ItemStack>> itemOutputs = ImmutableList.builder();

        itemInputs.add(transmutation.getInputDisplay());
        itemOutputs.add(Collections.singletonList(transmutation.getOutputDisplay()));

        ingredients.setInputLists(VanillaTypes.ITEM, itemInputs.build());
        ingredients.setOutputLists(VanillaTypes.ITEM, itemOutputs.build());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, BlockTransmutation transmutation, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();

        itemStacks.init(0, true, 22, 17);
        itemStacks.init(1, false, 94, 18);

        itemStacks.set(ingredients);
    }
}
