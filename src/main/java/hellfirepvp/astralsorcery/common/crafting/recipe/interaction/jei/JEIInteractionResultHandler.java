/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.interaction.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInteraction;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JEIInteractionResultHandler
 * Created by HellFirePvP
 * Date: 31.10.2020 / 14:47
 */
public abstract class JEIInteractionResultHandler {

    @OnlyIn(Dist.CLIENT)
    public abstract void addToRecipeLayout(IRecipeLayout recipeLayout, LiquidInteraction recipe, IIngredients ingredients);

    @OnlyIn(Dist.CLIENT)
    public abstract void addToRecipeIngredients(LiquidInteraction recipe, IIngredients ingredients);

    @OnlyIn(Dist.CLIENT)
    public abstract void drawRecipe(LiquidInteraction recipe, MatrixStack renderStack, double mouseX, double mouseY);

}
