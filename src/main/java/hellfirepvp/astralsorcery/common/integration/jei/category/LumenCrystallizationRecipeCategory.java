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
import hellfirepvp.astralsorcery.common.integration.jei.ingredient.LumenIngredientType;
import hellfirepvp.astralsorcery.common.item.block.LumenCrystalClusterBlockItem;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenCrystallizationRecipe;
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
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenCrystallizationRecipeCategory
 * Created by HellFirePvP
 * Date: 25.09.2026 / 22:46
 */
public class LumenCrystallizationRecipeCategory extends ASRecipeCategory<LumenCrystallizationRecipe> {

    public static final RecipeType<LumenCrystallizationRecipe> RECIPE_TYPE = makeType("lumen_crystallization", LumenCrystallizationRecipe.class);
    private static final ResourceLocation BACKGROUND = AstralSorcery.key("textures/screen/jei/lumen_crystallization.png");

    private final IDrawable background;

    public LumenCrystallizationRecipeCategory(IGuiHelper helper) {
        super(116, 54, helper, ItemsAS.BLOCK_LUMEN_CRYSTALLIZER);
        this.background = createBackground(helper, BACKGROUND, this.getWidth(), this.getHeight());
    }

    @SuppressWarnings("removal")
    @Nullable
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public RecipeType<LumenCrystallizationRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LumenCrystallizationRecipe recipe, IFocusGroup focuses) {
        Lumen toCrystallize = recipe.getLumenToCrystallize();
        toCrystallize.getHolder().ifPresent(lumenHolder -> {
            builder.addSlot(RecipeIngredientRole.INPUT, 13, 18)
                    .addIngredient(LumenIngredientType.INSTANCE, LumenStack.of(toCrystallize, LumenStack.FLASK_VALUE));

            builder.addSlot(RecipeIngredientRole.INPUT, this.getWidth() / 2 - 8, this.getHeight() / 2 - 16)
                    .addIngredients(recipe.getInput());

            ItemStack lumenCrystalCluster = LumenCrystalClusterBlockItem.getCluster(lumenHolder, 4);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 19)
                    .addItemStack(lumenCrystalCluster);
        });
    }

    @Override
    public void draw(LumenCrystallizationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.categoryIcon.draw(guiGraphics, this.getWidth() / 2 - 8, this.getHeight() / 2);
    }

    @Override
    public List<ItemStack> provideCatalyst() {
        return List.of(ItemsAS.BLOCK_LUMEN_CRYSTALLIZER.toStack());
    }

    @Override
    public List<LumenCrystallizationRecipe> provideRecipes(RecipeManager recipeManager, IRecipeRegistration register) {
        return provideRawRecipes(recipeManager, RecipeTypesAS.LUMEN_CRYSTALLIZATION_TYPE);
    }
}
