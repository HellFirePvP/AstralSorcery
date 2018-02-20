/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.jei.altar;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.base.JEIBaseWrapper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarTraitRecipeWrapper
 * Created by HellFirePvP
 * Date: 19.10.2017 / 22:59
 */
public class AltarTraitRecipeWrapper extends JEIBaseWrapper {

    private TraitRecipe recipe;

    public AltarTraitRecipeWrapper(TraitRecipe recipe) {
        this.recipe = recipe;
    }

    public final TraitRecipe getUnderlyingRecipe() {
        return recipe;
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
        for (TraitRecipe.TraitRecipeSlot as : TraitRecipe.TraitRecipeSlot.values()) {
            stackList.add(recipe.getInnerTraitItems(as));
        }
        for (NonNullList<ItemStack> outerTrait : recipe.getTraitItems()) {
            stackList.add(outerTrait);
        }

        ItemHandle.ignoreGatingRequirement = r;

        ingredients.setInputLists(ItemStack.class, stackList);

        ingredients.setOutput(ItemStack.class, recipe.getOutputForRender());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        if(recipe.getRequiredConstellation() != null) {
            GlStateManager.disableAlpha();
            RenderConstellation.renderConstellationIntoGUI(recipe.getRequiredConstellation().getConstellationColor(), recipe.getRequiredConstellation(),
                    0, 40, 0,
                    recipeWidth, recipeHeight - 40, 2F, new RenderConstellation.BrightnessFunction() {
                        @Override
                        public float getBrightness() {
                            return 0.5F;
                        }
                    }, true, false);
            GlStateManager.enableAlpha();
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
