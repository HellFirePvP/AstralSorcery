/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.jei;

import hellfirepvp.astralsorcery.common.integrations.mods.ModIntegrationJEI;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CategoryInfuser
 * Created by HellFirePvP
 * Date: 11.01.2017 / 00:10
 */
public class CategoryInfuser implements IRecipeCategory<InfuserRecipeWrapper> {

    private final IDrawable background;
    private final String locInfuser;

    public CategoryInfuser(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation("astralsorcery", "textures/gui/jei/recipeTemplateInfusion.png");
        background = guiHelper.createDrawable(location, 0, 0, 116, 54);
        locInfuser = I18n.format("jei.category.infuser");
    }

    @Override
    public String getUid() {
        return ModIntegrationJEI.idInfuser;
    }

    @Override
    public String getTitle() {
        return locInfuser;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {}

    @Override
    public void drawAnimations(Minecraft minecraft) {}

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, InfuserRecipeWrapper recipeWrapper) {}

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, InfuserRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        group.init(0, true, 18, 18);
        group.init(1, false, 94, 18);

        group.set(ingredients);
    }
}
