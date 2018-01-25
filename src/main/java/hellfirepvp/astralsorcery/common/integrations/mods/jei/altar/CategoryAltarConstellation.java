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
 * Class: CategoryAltarConstellation
 * Created by HellFirePvP
 * Date: 15.02.2017 / 19:17
 */
public class CategoryAltarConstellation extends JEIBaseCategory<AltarConstellationRecipeWrapper> {

    private final IDrawable background;

    public CategoryAltarConstellation(IGuiHelper guiHelper) {
        super("jei.category.altar.constellation", ModIntegrationJEI.idAltarConstellation);
        ResourceLocation location = new ResourceLocation("astralsorcery", "textures/gui/jei/recipeTemplateAltarConstellation.png");
        background = guiHelper.createDrawable(location, 0, 0, 116, 162);
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {}

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AltarConstellationRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        group.init(0, false, 48, 18);

        group.init(1, true, 30, 76);
        group.init(2, true, 49, 76);
        group.init(3, true, 68, 76);
        group.init(4, true, 30, 95);
        group.init(5, true, 49, 95);
        group.init(6, true, 68, 95);
        group.init(7, true, 30, 114);
        group.init(8, true, 49, 114);
        group.init(9, true, 68, 114);

        group.init(10, true, 11, 57);
        group.init(11, true, 87, 57);
        group.init(12, true, 11, 133);
        group.init(13, true, 87, 133);

        group.init(14, true, 30, 57);
        group.init(15, true, 68, 57);
        group.init(16, true, 11, 76);
        group.init(17, true, 87, 76);
        group.init(18, true, 11, 114);
        group.init(19, true, 87, 114);
        group.init(20, true, 30, 133);
        group.init(21, true, 68, 133);

        group.set(ingredients);
    }

}
