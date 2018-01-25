/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.jei.altar;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationJEI;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.base.JEIBaseCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CategoryAltarTrait
 * Created by HellFirePvP
 * Date: 19.10.2017 / 22:58
 */
public class CategoryAltarTrait extends JEIBaseCategory<AltarTraitRecipeWrapper> {

    private final IDrawable background;

    public CategoryAltarTrait(IGuiHelper guiHelper) {
        super("jei.category.altar.trait", ModIntegrationJEI.idAltarTrait);
        ResourceLocation location = new ResourceLocation("astralsorcery", "textures/gui/jei/recipeTemplateAltarTrait.png");
        background = guiHelper.createDrawable(location, 0, 0, 116, 162);
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {}

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AltarTraitRecipeWrapper recipeWrapper, IIngredients ingredients) {
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

        group.init(22, true, 49, 57);
        group.init(23, true, 11, 95);
        group.init(24, true, 87, 95);
        group.init(25, true, 49, 133);

        int centerX = 49;
        int centerY = 95;
        TraitRecipe recipe = recipeWrapper.getUnderlyingRecipe();
        int additional = recipe.getTraitItemHandles().size();
        for (int i = 0; i < additional; i++) {
            double part = ((double) i) / ((double) additional) * 2.0 * Math.PI; //Shift by half a period
            part = MathHelper.clamp(part, 0, 2.0 * Math.PI);
            part += Math.PI;
            double xAdd = Math.sin(part) * 60.0;
            double yAdd = Math.cos(part) * 60.0;
            group.init(26 + i, true, MathHelper.floor(centerX + xAdd), MathHelper.floor(centerY + yAdd));
        }

        group.set(ingredients);
    }
}
