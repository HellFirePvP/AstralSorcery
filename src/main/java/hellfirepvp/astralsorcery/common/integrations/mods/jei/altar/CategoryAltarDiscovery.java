/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.jei.altar;

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
 * Class: CategoryAltarDiscovery
 * Created by HellFirePvP
 * Date: 15.02.2017 / 16:54
 */
public class CategoryAltarDiscovery extends JEIBaseCategory<AltarDiscoveryRecipeWrapper> {

    private final IDrawable background;

    public CategoryAltarDiscovery(IGuiHelper guiHelper) {
        super("jei.category.altar.discovery", ModIntegrationJEI.idAltarDiscovery);
        ResourceLocation location = new ResourceLocation("astralsorcery", "textures/gui/jei/recipeTemplateAltarDiscovery.png");
        background = guiHelper.createDrawable(location, 0, 0, 116, 162);
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {}

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AltarDiscoveryRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        group.init(0, false, 48, 18);
        group.init(1, true, 22, 70);
        group.init(2, true, 49, 70);
        group.init(3, true, 76, 70);
        group.init(4, true, 22, 97);
        group.init(5, true, 49, 97);
        group.init(6, true, 76, 97);
        group.init(7, true, 22, 124);
        group.init(8, true, 49, 124);
        group.init(9, true, 76, 124);

        group.set(ingredients);
    }

}
