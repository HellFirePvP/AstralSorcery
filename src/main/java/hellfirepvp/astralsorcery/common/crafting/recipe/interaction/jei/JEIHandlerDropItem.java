package hellfirepvp.astralsorcery.common.crafting.recipe.interaction.jei;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.util.LightmapUtil;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInteraction;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.InteractionResult;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.ResultDropItem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collections;
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
