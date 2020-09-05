package hellfirepvp.astralsorcery.common.integration.jei;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingConstellationUtils;
import hellfirepvp.astralsorcery.common.block.tile.BlockAltar;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.helper.WrappedIngredient;
import hellfirepvp.astralsorcery.common.crafting.helper.ingredient.FluidIngredient;
import hellfirepvp.astralsorcery.common.crafting.recipe.BlockTransmutation;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.integration.IntegrationJEI;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CategoryAltarDiscovery
 * Created by HellFirePvP
 * Date: 05.09.2020 / 14:16
 */
public class CategoryAltar extends JEICategory<SimpleAltarRecipe> {

    private final IDrawable background, icon;
    private final AltarType altarType;
    private final Predicate<Integer> gridFilter;

    public CategoryAltar(ResourceLocation id, String textureRef, BlockAltar altarRef, IGuiHelper guiHelper) {
        super(id);
        this.background = guiHelper.createDrawable(AstralSorcery.key(String.format("textures/gui/jei/%s.png", textureRef)), 0, 0, 116, 162);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(altarRef));
        this.altarType = altarRef.getAltarType();
        this.gridFilter = this.altarType::hasSlot;
    }

    @Override
    public Class<? extends SimpleAltarRecipe> getRecipeClass() {
        return SimpleAltarRecipe.class;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    public AltarType getAltarType() {
        return altarType;
    }

    @Override
    public void draw(SimpleAltarRecipe recipe, double mouseX, double mouseY) {
        if (recipe.getFocusConstellation() != null) {
            RenderSystem.enableBlend();
            Blending.DEFAULT.apply();
            IConstellation cst = recipe.getFocusConstellation();
            RenderingConstellationUtils.renderConstellationIntoGUI(Color.BLACK, cst,
                    0, 0, 0,
                    50, 50, 1.2F,
                    () -> 0.9F, true, false);
            RenderSystem.disableBlend();
        }
    }

    @Override
    public void setIngredients(SimpleAltarRecipe altarRecipe, IIngredients ingredients) {
        ImmutableList.Builder<List<ItemStack>> itemInputs = ImmutableList.builder();
        ImmutableList.Builder<List<ItemStack>> itemOutputs = ImmutableList.builder();

        itemOutputs.add(Collections.singletonList(altarRecipe.getOutputForRender(Collections.emptyList())));

        AltarRecipeGrid grid = altarRecipe.getInputs();
        for (int slot = 0; slot < AltarRecipeGrid.MAX_INVENTORY_SIZE; slot++) {
            itemInputs.add(ingredientStacks(grid.getIngredient(slot)));
        }

        for (WrappedIngredient relayInput : altarRecipe.getRelayInputs()) {
            itemInputs.add(ingredientStacks(relayInput.getIngredient()));
        }

        ingredients.setInputLists(VanillaTypes.ITEM, itemInputs.build());
        ingredients.setOutputLists(VanillaTypes.ITEM, itemOutputs.build());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, SimpleAltarRecipe altarRecipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();

        itemStacks.init(0, false, 48, 18);

        int step = 19;
        int xOffset = 11;
        int yOffset = 57;
        for (int yy = 0; yy < AltarRecipeGrid.GRID_SIZE; yy++) {
            for (int xx = 0; xx < AltarRecipeGrid.GRID_SIZE; xx++) {
                int slot = xx + yy * AltarRecipeGrid.GRID_SIZE;
                itemStacks.init(slot + 1, true, xOffset + step * xx, yOffset + step * yy);
            }
        }

        int centerX = 49;
        int centerY = 95;
        int additional = altarRecipe.getRelayInputs().size();
        for (int i = 0; i < additional; i++) {
            double part = ((double) i) / ((double) additional) * 2.0 * Math.PI; //Shift by half a period
            part = MathHelper.clamp(part, 0, 2.0 * Math.PI);
            part += Math.PI;
            double xAdd = Math.sin(part) * 60.0;
            double yAdd = Math.cos(part) * 60.0;
            itemStacks.init(26 + i, true, MathHelper.floor(centerX + xAdd), MathHelper.floor(centerY + yAdd));
        }

        itemStacks.set(ingredients);
    }
}
