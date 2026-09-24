/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.page;

import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.recipe.infusion.InfusionRecipe;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.tome.TomePage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderPageStarlightInfusion
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderPageStarlightInfusion extends RenderPageRecipe<InfusionRecipe> {

    public RenderPageStarlightInfusion(@Nullable ResearchNode node, int nodePage, ResourceLocation recipeId) {
        super(node, nodePage, RecipeTypesAS.INFUSION_TYPE, recipeId);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float pTicks, float mouseX, float mouseY) {
        this.clearFrame();

        this.resolveRecipeOrWriteError(guiGraphics, x, y).map(RecipeHolder::value).ifPresent(recipe -> {
            this.renderPageOverlay(guiGraphics, x, y, TexturesAS.SCREEN_TOME_PAGE_GRID_STARLIGHT_INFUSION);
            this.renderRecipeHeader(guiGraphics, x, y, false);

            int midX = x + TomePage.DEFAULT_WIDTH / 2;
            int resultY = y + 18 + 60 / 2;
            int yOffset = 136;
            this.renderOutput(guiGraphics, midX - 8, resultY - 8, 16, 16, recipe.getOutput());

            this.renderInput(guiGraphics, midX - 8, y + yOffset, recipe.getItemInput());
            this.renderScaledItem(guiGraphics, midX - 8, y + yOffset + 12, ItemsAS.BLOCK_INFUSER.toStack(), 1.5F);

            FluidStack input = new FluidStack(recipe.getFluidInput(), FluidType.BUCKET_VOLUME);
            this.renderLiquidInput(guiGraphics, x + 61, y + 100, input);
            this.renderLiquidInput(guiGraphics, x + 79, y + 100, input);
            this.renderLiquidInput(guiGraphics, x + 97, y + 100, input);
            this.renderLiquidInput(guiGraphics, x + 61, y + 172, input);
            this.renderLiquidInput(guiGraphics, x + 79, y + 172, input);
            this.renderLiquidInput(guiGraphics, x + 97, y + 172, input);

            this.renderLiquidInput(guiGraphics, x + 43, y + 118, input);
            this.renderLiquidInput(guiGraphics, x + 43, y + 136, input);
            this.renderLiquidInput(guiGraphics, x + 43, y + 154, input);
            this.renderLiquidInput(guiGraphics, x + 115, y + 118, input);
            this.renderLiquidInput(guiGraphics, x + 115, y + 136, input);
            this.renderLiquidInput(guiGraphics, x + 115, y + 154, input);

            this.renderHoverTooltips(guiGraphics, mouseX, mouseY);
        });
    }
}
