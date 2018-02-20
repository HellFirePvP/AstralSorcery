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
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CategoryWell
 * Created by HellFirePvP
 * Date: 27.02.2017 / 23:37
 */
public class CategoryWell extends JEIBaseCategory<WellRecipeWrapper> {

    private final IDrawable background;

    public CategoryWell(IGuiHelper guiHelper) {
        super("jei.category.well", ModIntegrationJEI.idWell);
        ResourceLocation location = new ResourceLocation("astralsorcery", "textures/gui/jei/recipeTemplateLightwell.png");
        background = guiHelper.createDrawable(location, 0, 0, 116, 54);
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        RenderHelper.enableGUIStandardItemLighting();
        minecraft.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(BlocksAS.blockWell), 46, 20);
        RenderHelper.disableStandardItemLighting();
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, WellRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fGroup = recipeLayout.getFluidStacks();
        group.init(0, true, 2, 18);
        fGroup.init(1, false, 94, 18, 18, 18, 1000, false, null);

        group.set(ingredients);
        fGroup.set(ingredients);
    }
}
