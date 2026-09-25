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
import hellfirepvp.astralsorcery.common.recipe.focal.place.FocalTransmutationRecipe;
import hellfirepvp.astralsorcery.common.recipe.lightwell.LightwellRecipe;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalTransmutationRecipeCategory
 * Created by HellFirePvP
 * Date: 25.09.2026 / 16:53
 */
public class FocalTransmutationRecipeCategory extends ASRecipeCategory<FocalTransmutationRecipe> {

    public static final RecipeType<FocalTransmutationRecipe> RECIPE_TYPE = makeType("focal_transmutation", FocalTransmutationRecipe.class);
    private static final ResourceLocation BACKGROUND = AstralSorcery.key("textures/screen/jei/focal_transmutation.png");
    private static final IntRectangle INFO_ICON = new IntRectangle(62, 5, 12, 12);

    private final IDrawable background, infoIcon;

    public FocalTransmutationRecipeCategory(IGuiHelper helper) {
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
    public RecipeType<FocalTransmutationRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FocalTransmutationRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 22, 18)
                .addIngredients(recipe.getInputDisplay());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 19)
                .addItemStacks(recipe.getOutputForDisplay());

    }

    @Override
    public void draw(FocalTransmutationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if (recipe.requiresFocusedStarlight() || recipe.getRequiredConstellation().isPresent()) {
            this.infoIcon.draw(guiGraphics, INFO_ICON.x(), INFO_ICON.y());
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, FocalTransmutationRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (recipe.requiresFocusedStarlight() || recipe.getRequiredConstellation().isPresent()) {
            if (INFO_ICON.contains(mouseX, mouseY)) {
                List<Component> tTip = new ArrayList<>();
                if (recipe.requiresFocusedStarlight()) {
                    tTip.add(Component.translatable("jei.astralsorcery.info.focal_transmutation.focused_starlight"));
                }
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
    public List<FocalTransmutationRecipe> provideRecipes(RecipeManager recipeManager, IRecipeRegistration register) {
        return provideRawRecipes(recipeManager, RecipeTypesAS.FOCAL_TRANSMUTATION_TYPE);
    }
}
