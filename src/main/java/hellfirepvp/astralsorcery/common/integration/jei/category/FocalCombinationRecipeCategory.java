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
import hellfirepvp.astralsorcery.common.recipe.focal.drop.FocalCombineRecipe;
import hellfirepvp.astralsorcery.common.recipe.focal.place.FocalTransmutationRecipe;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalCombinationRecipeCategory
 * Created by HellFirePvP
 * Date: 25.09.2026 / 17:28
 */
public class FocalCombinationRecipeCategory extends ASRecipeCategory<FocalCombineRecipe> {

    public static final RecipeType<FocalCombineRecipe> RECIPE_TYPE = makeType("focal_combination", FocalCombineRecipe.class);
    private static final ResourceLocation BACKGROUND = AstralSorcery.key("textures/screen/jei/focal_combination.png");
    private static final IntRectangle INFO_ICON = new IntRectangle(62, 5, 12, 12);

    private final IDrawable background, infoIcon;

    public FocalCombinationRecipeCategory(IGuiHelper helper) {
        super(116, 54, helper, ItemsAS.BLOCK_STARLIGHT_FOCUS_ROCK_CRYSTAL);
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
    public RecipeType<FocalCombineRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FocalCombineRecipe recipe, IFocusGroup focuses) {
        int offsetX = 3;
        int offsetY = 1;
        List<Ingredient> inputs = recipe.getInputs();
        if (inputs.isEmpty()) {
            return; //Invaild recipe
        }

        if (inputs.size() == 1) {
            builder.addSlot(RecipeIngredientRole.INPUT, offsetX + 18, offsetY + 18)
                    .setStandardSlotBackground()
                    .addIngredients(inputs.getFirst());
        } else if (inputs.size() <= 4) {
            offsetX += 9;
            offsetY += 9;

            for (int i = 0; i < inputs.size(); i++) {
                int x = offsetX + (i % 2) * 18;
                int y = offsetY + (i / 2) * 18;
                builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                        .setStandardSlotBackground()
                        .addIngredients(inputs.get(i));
            }
        } else {
            for (int i = 0; i < inputs.size(); i++) {
                int x = offsetX + (i % 3) * 18;
                int y = offsetY + (i / 3) * 18;
                builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                        .setStandardSlotBackground()
                        .addIngredients(inputs.get(i));
            }
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 19)
                .addItemStacks(recipe.getOutputs());

    }

    @Override
    public void draw(FocalCombineRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if (recipe.getRequiredConstellation().isPresent()) {
            this.infoIcon.draw(guiGraphics, INFO_ICON.x(), INFO_ICON.y());
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, FocalCombineRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (recipe.getRequiredConstellation().isPresent()) {
            if (INFO_ICON.contains(mouseX, mouseY)) {
                List<Component> tTip = new ArrayList<>();
                recipe.getRequiredConstellation().ifPresent(cst -> {
                    tTip.add(Component.translatable("jei.astralsorcery.info.focal_transmutation.specific_constellation", cst.getColoredName()));
                });
                tooltip.addAll(tTip);
            }
        }
    }

    @Override
    public List<ItemStack> provideCatalyst() {
        return List.of(ItemsAS.BLOCK_STARLIGHT_FOCUS_ROCK_CRYSTAL.toStack());
    }

    @Override
    public List<FocalCombineRecipe> provideRecipes(RecipeManager recipeManager, IRecipeRegistration register) {
        return provideRawRecipes(recipeManager, RecipeTypesAS.FOCAL_COMBINE_TYPE);
    }
}
