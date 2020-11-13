/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.page;

import com.mojang.blaze3d.matrix.MatrixStack;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;

import javax.annotation.Nullable;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderPageAltarRecipe
 * Created by HellFirePvP
 * Date: 12.10.2019 / 10:49
 */
public class RenderPageAltarRecipe extends RenderPageRecipeTemplate {

    private final SimpleAltarRecipe recipe;
    private final AbstractRenderableTexture gridTexture;

    public RenderPageAltarRecipe(@Nullable ResearchNode node, int nodePage, SimpleAltarRecipe recipe) {
        super(node, nodePage);
        this.recipe = recipe;
        this.gridTexture = this.getGridTexture(recipe);
    }

    private AbstractRenderableTexture getGridTexture(SimpleAltarRecipe recipe) {
        switch (recipe.getAltarType()) {
            case DISCOVERY:
                return TexturesAS.TEX_GUI_BOOK_GRID_T1;
            case ATTUNEMENT:
                return TexturesAS.TEX_GUI_BOOK_GRID_T2;
            case CONSTELLATION:
                return TexturesAS.TEX_GUI_BOOK_GRID_T3;
            case RADIANCE:
                return TexturesAS.TEX_GUI_BOOK_GRID_T4;
        }
        return TexturesAS.TEX_GUI_BOOK_GRID_T4;
    }

    @Override
    public void render(MatrixStack renderStack, float x, float y, float z, float pTicks, float mouseX, float mouseY) {
        this.clearFrameRectangles();

        this.renderRecipeGrid(renderStack, x, y, z, this.gridTexture);
        this.renderExpectedItemStackOutput(renderStack, x + 78, y + 25, z, 1.4F,
                this.recipe.getOutputForRender(Collections.emptyList()));
        this.renderInfoStar(renderStack, x, y, z, pTicks);
        this.renderRequiredConstellation(renderStack, x, y, z, this.recipe.getFocusConstellation());

        int widthShift  = (AltarRecipeGrid.GRID_SIZE - recipe.getInputs().getWidth())  / 2;
        int heightShift = (AltarRecipeGrid.GRID_SIZE - recipe.getInputs().getHeight()) / 2;

        AltarType type = this.recipe.getAltarType();
        float recipeX = x + 30;
        float recipeY = y + 78;
        for (int xx = 0; xx < AltarRecipeGrid.GRID_SIZE; xx++) {
            for (int yy = 0; yy < AltarRecipeGrid.GRID_SIZE; yy++) {
                int slot = xx + yy * AltarRecipeGrid.GRID_SIZE;

                if (!type.hasSlot(slot)) {
                    continue;
                }

                int recipeIndex = (xx - widthShift) + (yy - heightShift) * AltarRecipeGrid.GRID_SIZE;
                if (recipeIndex >= 0 && recipeIndex < AltarRecipeGrid.MAX_INVENTORY_SIZE) {
                    float renderX = recipeX + 25 * xx;
                    float renderY = recipeY + 25 * yy;

                    this.renderExpectedIngredientInput(renderStack, renderX, renderY, z, 1.1F, recipeIndex * 20, this.recipe.getInputs().getIngredient(recipeIndex));
                }
            }
        }
        this.renderExpectedRelayInputs(renderStack, x, y, z + 150, this.recipe);
    }

    @Override
    public boolean propagateMouseClick(double mouseX, double mouseZ) {
        return this.handleRecipeNameCopyClick(mouseX, mouseZ, this.recipe) || this.handleBookLookupClick(mouseX, mouseZ);
    }

    @Override
    public void postRender(MatrixStack renderStack, float x, float y, float z, float pTicks, float mouseX, float mouseY) {
        this.renderHoverTooltips(renderStack, mouseX, mouseY, z, this.recipe.getId());
        this.renderInfoStarTooltips(renderStack, x, y, z, mouseX, mouseY, (toolTip) -> {
            this.addAltarRecipeTooltip(this.recipe, toolTip);
            this.addConstellationInfoTooltip(this.recipe.getFocusConstellation(), toolTip);
        });
        super.postRender(renderStack, x, y, z, pTicks, mouseX, mouseY);
    }
}
