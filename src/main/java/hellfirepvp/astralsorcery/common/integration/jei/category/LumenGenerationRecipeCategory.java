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
import hellfirepvp.astralsorcery.common.integration.jei.ingredient.LumenIngredientRenderer;
import hellfirepvp.astralsorcery.common.integration.jei.ingredient.LumenIngredientType;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenGenerationRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenGenerationRecipeCategory
 * Created by HellFirePvP
 * Date: 25.09.2026 / 21:54
 */
public class LumenGenerationRecipeCategory extends ASRecipeCategory<LumenGenerationRecipe> {

    public static final RecipeType<LumenGenerationRecipe> RECIPE_TYPE = makeType("lumen_generation", LumenGenerationRecipe.class);
    private static final ResourceLocation BACKGROUND = AstralSorcery.key("textures/screen/jei/lumen_generation.png");

    private final IDrawable background;
    private final IDrawable lumenAlchemyArrayIcon;

    public LumenGenerationRecipeCategory(IGuiHelper helper) {
        super(116, 54, helper, ItemsAS.BLOCK_LUMEN_ARRAY);
        this.background = createBackground(helper, BACKGROUND, this.getWidth(), this.getHeight());
        this.lumenAlchemyArrayIcon = helper.createDrawableItemStack(ItemsAS.BLOCK_LUMEN_ALCHEMY_ARRAY.toStack());
    }

    @SuppressWarnings("removal")
    @Nullable
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public RecipeType<LumenGenerationRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LumenGenerationRecipe recipe, IFocusGroup focuses) {
        int offsetX = 4;
        int offsetY = 9;
        List<Lumen> inputs = recipe.getLumenCombinationInputs().keySet().stream().toList();

        if (!inputs.isEmpty()) {
            if (inputs.size() == 1) {
                builder.addSlot(RecipeIngredientRole.INPUT, offsetX + 18, offsetY + 18)
                        .addIngredient(LumenIngredientType.INSTANCE, LumenStack.of(inputs.getFirst(), LumenStack.FLASK_VALUE));
            } else {
                for (int i = 0; i < inputs.size(); i++) {
                    int x = offsetX + (i % 2) * 18;
                    int y = offsetY + (i / 2) * 18;
                    builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                            .addIngredient(LumenIngredientType.INSTANCE, LumenStack.of(inputs.get(i), LumenStack.FLASK_VALUE));
                }
            }
        }

        builder.addSlot(RecipeIngredientRole.INPUT, this.getWidth() / 2 - 8, this.getHeight() / 2 - 16)
                .addIngredients(recipe.getInput());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 19)
                .addIngredient(LumenIngredientType.INSTANCE, LumenStack.of(recipe.getProducedLumen(), LumenStack.FLASK_VALUE));
    }

    @Override
    public void draw(LumenGenerationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if (recipe.getLumenCombinationInputs().isEmpty()) {
            this.categoryIcon.draw(guiGraphics, this.getWidth() / 2 - 8, this.getHeight() / 2);
        } else {
            this.lumenAlchemyArrayIcon.draw(guiGraphics, this.getWidth() / 2 - 8, this.getHeight() / 2);
        }
    }

    @Override
    public List<ItemStack> provideCatalyst() {
        return List.of(ItemsAS.BLOCK_LUMEN_ARRAY.toStack(), ItemsAS.BLOCK_LUMEN_ALCHEMY_ARRAY.toStack());
    }

    @Override
    public List<LumenGenerationRecipe> provideRecipes(RecipeManager recipeManager, IRecipeRegistration register) {
        return provideRawRecipes(recipeManager, RecipeTypesAS.LUMEN_GENERATION_TYPE);
    }
}
