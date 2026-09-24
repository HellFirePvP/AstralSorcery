/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.page;

import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.tome.TomePage;
import hellfirepvp.astralsorcery.common.util.IngredientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderPageCraftingRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderPageCraftingRecipe extends RenderPageRecipe<CraftingRecipe> {

    public RenderPageCraftingRecipe(@Nullable ResearchNode node, int nodePage, ResourceLocation recipeId) {
        super(node, nodePage, () -> RecipeType.CRAFTING, recipeId);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float pTicks, float mouseX, float mouseY) {
        this.clearFrame();

        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) return;
        HolderLookup.Provider lookupProvider = connection.registryAccess();

        this.resolveRecipeOrWriteError(guiGraphics, x, y).map(RecipeHolder::value).ifPresent(recipe -> {
            if (recipe instanceof ShapedRecipe shapedRecipe) {
                this.renderShapedRecipe(guiGraphics, x, y, lookupProvider, shapedRecipe);
            } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                this.renderShapelessRecipe(guiGraphics, x, y, lookupProvider, shapelessRecipe);
            } else {
                ResourceLocation id = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
                id = id == null ? ResourceLocation.withDefaultNamespace("unregistered_sadface") : id;
                Component errorDisplay = Component.translatable("tome.research.info.recipe_cannot_display", id.toString());

                Font font = Minecraft.getInstance().font;
                List<FormattedCharSequence> lines = new ArrayList<>(font.split(errorDisplay, TomePage.DEFAULT_WIDTH));

                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(x, y, 0);
                for (FormattedCharSequence text : lines) {
                    guiGraphics.drawString(font, text, 0, 0, ColorsAS.TOME_TEXT_COLOR.getColor(), true);
                    guiGraphics.pose().translate(0, 10, 0);
                }
                guiGraphics.pose().popPose();
            }

            this.renderEffects(guiGraphics, pTicks);
            this.renderHoverTooltips(guiGraphics, mouseX, mouseY);
        });
    }

    private void renderShapedRecipe(GuiGraphics guiGraphics, int x, int y, HolderLookup.Provider lookupProvider, ShapedRecipe shapedRecipe) {
        this.renderPageOverlay(guiGraphics, x, y, TexturesAS.SCREEN_TOME_PAGE_GRID_CRAFTING);

        NonNullList<ItemStack> inputPattern = NonNullList.withSize(9, ItemStack.EMPTY);
        for (int xx = 0; xx < shapedRecipe.getWidth(); xx++) {
            for (int yy = 0; yy < shapedRecipe.getHeight(); yy++) {
                int index = xx + yy * shapedRecipe.getWidth();
                inputPattern.set(xx + yy * 3, IngredientUtil.getRandomDisplayStack(shapedRecipe.getIngredients().get(index), 0));
            }
        }
        CraftingInput input = CraftingInput.of(shapedRecipe.getWidth(), shapedRecipe.getWidth(), inputPattern);
        ItemStack resultStack = shapedRecipe.assemble(input, lookupProvider);

        int midX = x + TomePage.DEFAULT_WIDTH / 2;
        int resultY = y + 18 + 60 / 2;
        this.renderOutput(guiGraphics, midX - 8, resultY - 8, 16, 16, resultStack);

        this.renderInputGrid(guiGraphics, x, y, shapedRecipe.getWidth(), shapedRecipe.getHeight(), shapedRecipe.getIngredients());
    }

    private void renderShapelessRecipe(GuiGraphics guiGraphics, int x, int y, HolderLookup.Provider lookupProvider, ShapelessRecipe shapelessRecipe) {
        this.renderPageOverlay(guiGraphics, x, y, TexturesAS.SCREEN_TOME_PAGE_GRID_CRAFTING);

        NonNullList<ItemStack> inputPattern = NonNullList.withSize(9, ItemStack.EMPTY);
        for (int i = 0; i < shapelessRecipe.getIngredients().size(); i++) {
            inputPattern.set(i, IngredientUtil.getRandomDisplayStack(shapelessRecipe.getIngredients().get(i), 0));
        }
        CraftingInput input = CraftingInput.of(3, 3, inputPattern);
        ItemStack resultStack = shapelessRecipe.assemble(input, lookupProvider);

        int midX = x + TomePage.DEFAULT_WIDTH / 2;
        int resultY = y + 18 + 60 / 2;
        this.renderOutput(guiGraphics, midX - 8, resultY - 8, 16, 16, resultStack);

        this.renderInputGrid(guiGraphics, x, y, 3, 3, shapelessRecipe.getIngredients());
    }

    private void renderInputGrid(GuiGraphics guiGraphics, int x, int y, int width, int height, NonNullList<Ingredient> inputPattern) {
        int centerOffsetX = x + 61;
        int centerOffsetY = y + 118;

        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int index = xx + yy * width;
                if (inputPattern.size() <= index) continue;

                Ingredient input = inputPattern.get(index);
                if (!input.isEmpty()) {
                    int slotX = centerOffsetX + xx * 18;
                    int slotY = centerOffsetY + yy * 18;
                    this.renderInput(guiGraphics, slotX, slotY, input);
                }
            }
        }
    }
}
