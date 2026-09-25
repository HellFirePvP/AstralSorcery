/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.jei.category;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.integration.jei.base.ASRecipeCategory;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.recipe.infusion.InfusionRecipe;
import hellfirepvp.astralsorcery.common.util.data.IntRectangle;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: InfusionRecipeCategory
 * Created by HellFirePvP
 * Date: 25.09.2026 / 17:54
 */
public class InfusionRecipeCategory extends ASRecipeCategory<InfusionRecipe> {

    public static final RecipeType<InfusionRecipe> RECIPE_TYPE = makeType("infusion", InfusionRecipe.class);
    private static final ResourceLocation BACKGROUND = AstralSorcery.key("textures/screen/jei/infusion.png");
    private static final DecimalFormat FORMAT_CHANCE = new DecimalFormat("0.#");
    private static final IntRectangle INFO_ICON = new IntRectangle(88, 3, 12, 12);

    private final IDrawable background, infoIcon;

    public InfusionRecipeCategory(IGuiHelper helper) {
        super(116, 162, helper, ItemsAS.BLOCK_INFUSER);
        this.background = createBackground(helper, BACKGROUND, this.getWidth(), this.getHeight());
        this.infoIcon = createInfoIcon(helper);
    }

    @SuppressWarnings("removal")
    @Nullable
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public RecipeType<InfusionRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, InfusionRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 50, 96)
                .addIngredients(recipe.getItemInput());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 49, 18)
                .addItemStack(recipe.getOutput());

        for (int xx = 0; xx < 3; xx++) {
            builder.addSlot(RecipeIngredientRole.INPUT, 31 + xx * 19, 58)
                    .setFluidRenderer(1000, false, 16, 16)
                    .addFluidStack(recipe.getFluidInput());
            builder.addSlot(RecipeIngredientRole.INPUT, 31 + xx * 19, 134)
                    .setFluidRenderer(1000, false, 16, 16)
                    .addFluidStack(recipe.getFluidInput());
        }
        for (int yy = 0; yy < 3; yy++) {
            builder.addSlot(RecipeIngredientRole.INPUT, 12, 77 + yy * 19)
                    .setFluidRenderer(1000, false, 16, 16)
                    .addFluidStack(recipe.getFluidInput());
            builder.addSlot(RecipeIngredientRole.INPUT, 88, 77 + yy * 19)
                    .setFluidRenderer(1000, false, 16, 16)
                    .addFluidStack(recipe.getFluidInput());
        }
    }

    @Override
    public void draw(InfusionRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.infoIcon.draw(guiGraphics, INFO_ICON.x(), INFO_ICON.y());
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, InfusionRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (INFO_ICON.contains(mouseX, mouseY)) {
            List<Component> tTip = new ArrayList<>();
            if (!recipe.acceptChaliceInput()) {
                tTip.add(Component.translatable("jei.astralsorcery.info.infusion.no_chalice"));
            }
            if (recipe.getFluidConsumptionChance() >= 1F) {
                if (recipe.consumeMultipleFluids()) {
                    tTip.add(Component.translatable("jei.astralsorcery.info.infusion.consumption_chance.all.multiples"));
                } else {
                    tTip.add(Component.translatable("jei.astralsorcery.info.infusion.consumption_chance.all.one"));
                }
            } else {
                tTip.add(Component.translatable("jei.astralsorcery.info.infusion.consumption_chance",
                        FORMAT_CHANCE.format(recipe.getFluidConsumptionChance() * 100F)));
                if (recipe.consumeMultipleFluids()) {
                    tTip.add(Component.translatable("jei.astralsorcery.info.infusion.consume_multiples"));
                }
            }
            tooltip.addAll(tTip);
        }
    }

    @Override
    public List<ItemStack> provideCatalyst() {
        return List.of(ItemsAS.BLOCK_INFUSER.toStack());
    }

    @Override
    public List<InfusionRecipe> provideRecipes(RecipeManager recipeManager, IRecipeRegistration register) {
        return provideRawRecipes(recipeManager, RecipeTypesAS.INFUSION_TYPE);
    }
}
