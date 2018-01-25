/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.jei.altar;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.base.JEIBaseWrapper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarConstellationRecipeWrapper
 * Created by HellFirePvP
 * Date: 15.02.2017 / 19:17
 */
public class AltarConstellationRecipeWrapper extends JEIBaseWrapper {

    private ConstellationRecipe recipe;

    public AltarConstellationRecipeWrapper(ConstellationRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        AccessibleRecipe underlyingRecipe = recipe.getNativeRecipe();

        boolean r = ItemHandle.ignoreGatingRequirement;
        ItemHandle.ignoreGatingRequirement = true;

        List<List<ItemStack>> stackList = Lists.newArrayList();
        for (ShapedRecipeSlot srs : ShapedRecipeSlot.values()) {
            List<ItemStack> stacks = underlyingRecipe.getExpectedStackForRender(srs);
            stackList.add(stacks == null ? Lists.newArrayList() : stacks);
        }
        for (AttunementRecipe.AttunementAltarSlot as : AttunementRecipe.AttunementAltarSlot.values()) {
            stackList.add(recipe.getAttItems(as));
        }
        for (ConstellationRecipe.ConstellationAtlarSlot as : ConstellationRecipe.ConstellationAtlarSlot.values()) {
            stackList.add(recipe.getCstItems(as));
        }

        ItemHandle.ignoreGatingRequirement = r;

        ingredients.setInputLists(ItemStack.class, stackList);

        ingredients.setOutput(ItemStack.class, recipe.getOutputForRender());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {}


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
