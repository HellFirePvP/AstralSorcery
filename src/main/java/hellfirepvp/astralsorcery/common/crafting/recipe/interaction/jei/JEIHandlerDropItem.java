/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.interaction.jei;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInteraction;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.InteractionResult;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.ResultDropItem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JEIHandlerDropItem
 * Created by HellFirePvP
 * Date: 31.10.2020 / 14:50
 */
public class JEIHandlerDropItem extends JEIInteractionResultHandler {
    @Override
    @OnlyIn(Dist.CLIENT)
    public void addToRecipeLayout(IRecipeLayout recipeLayout, LiquidInteraction recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();

        itemStacks.init(2, false, 47, 18);

        itemStacks.set(ingredients);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addToRecipeIngredients(LiquidInteraction recipe, IIngredients ingredients) {
        ImmutableList.Builder<List<ItemStack>> itemOutputs = ImmutableList.builder();

        InteractionResult result = recipe.getResult();
        if (result instanceof ResultDropItem) {
            itemOutputs.add(Lists.newArrayList(((ResultDropItem) result).getOutput()));
        }

        ingredients.setOutputLists(VanillaTypes.ITEM, itemOutputs.build());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawRecipe(LiquidInteraction recipe, double mouseX, double mouseY) {
    }
}
