/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.jei;

import hellfirepvp.astralsorcery.common.integrations.ModIntegrationJEI;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.base.JEIBaseCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CategoryTransmutation
 * Created by HellFirePvP
 * Date: 15.02.2017 / 15:54
 */
public class CategoryTransmutation extends JEIBaseCategory<TransmutationRecipeWrapper> {

    private final IDrawable background;

    public CategoryTransmutation(IGuiHelper guiHelper) {
        super("jei.category.transmutation", ModIntegrationJEI.idTransmutation);
        ResourceLocation location = new ResourceLocation("astralsorcery", "textures/gui/jei/recipeTemplateTransmutation.png");
        background = guiHelper.createDrawable(location, 0, 0, 116, 54);
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {}

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, TransmutationRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        group.init(0, true, 22, 17);
        group.init(1, false, 94, 18);

        group.set(ingredients);
    }

}
