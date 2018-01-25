/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.jei;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.crafting.grindstone.DustGrindstoneRecipe;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipe;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.base.JEIBaseWrapper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GrindstoneRecipeWrapper
 * Created by HellFirePvP
 * Date: 23.11.2017 / 20:00
 */
public class GrindstoneRecipeWrapper extends JEIBaseWrapper {

    private final GrindstoneRecipe recipe;

    public GrindstoneRecipeWrapper(GrindstoneRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, recipe.getInputForRender().getApplicableItemsForRender());
        ingredients.setOutput(ItemStack.class, recipe.getOutputForRender().getApplicableItemsForRender());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        if(minecraft.fontRenderer != null && recipe instanceof DustGrindstoneRecipe) {
            int displChance = Math.round(((DustGrindstoneRecipe) recipe).getDoubleChance() * 100);
            String out = I18n.format("misc.grindstone.double", displChance + "%");
            int length = minecraft.fontRenderer.getStringWidth(out);
            minecraft.fontRenderer.drawString(out, recipeWidth / 2 - length / 2, recipeHeight - 12, 0xFF454545, false);
        }
    }

    @Nullable
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Lists.newArrayList();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }

}
