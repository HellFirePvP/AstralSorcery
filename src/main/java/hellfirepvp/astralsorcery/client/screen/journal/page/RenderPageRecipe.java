/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.page;

import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderPageRecipe
 * Created by HellFirePvP
 * Date: 11.10.2019 / 21:14
 */
public class RenderPageRecipe extends RenderPageRecipeTemplate {

    private final Map<Integer, Ingredient> inputs;
    private final ItemStack output;
    private final ResourceLocation recipeId;

    private RenderPageRecipe(@Nullable ResearchNode node, int nodePage, Map<Integer, Ingredient> inputs, ItemStack output, ResourceLocation recipeId) {
        super(node, nodePage);
        this.inputs = inputs;
        this.output = output;
        this.recipeId = recipeId;
    }

    public static RenderPageRecipe fromRecipe(@Nullable ResearchNode node, int nodePage, IRecipe<?> recipe) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        Map<Integer, Ingredient> inputs = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            inputs.put(i, Ingredient.EMPTY);
        }
        //Centering inputs on render
        boolean offsetDiagonal = ingredients.size() == 1;

        for (int xx = 0; xx < 3; xx++) {
            for (int zz = 0; zz < 3; zz++) {
                int slot = xx + zz * 3;

                int indexSlot = slot;
                if (offsetDiagonal) {
                    indexSlot -= 4;
                }
                if (indexSlot >= 0 && indexSlot < ingredients.size()) {
                    inputs.put(slot, ingredients.get(indexSlot));
                }
            }
        }
        return new RenderPageRecipe(node, nodePage, inputs, recipe.getRecipeOutput(), recipe.getId());
    }

    @Override
    public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
        this.clearFrameRectangles();

        this.renderRecipeGrid(offsetX, offsetY, zLevel, TexturesAS.TEX_GUI_BOOK_GRID_T1);
        this.renderExpectedItemStackOutput(offsetX + 78, offsetY + 25, zLevel, 1.4F, this.output);

        float recipeX = offsetX + 55;
        float recipeY = offsetY + 103;
        for (int xx = 0; xx < 3; xx++) {
            for (int yy = 0; yy < 3; yy++) {
                int slot = xx + yy * 3;

                float renderX = recipeX + 25 * xx;
                float renderY = recipeY + 25 * yy;
                this.renderExpectedIngredientInput(renderX, renderY, zLevel, 1.1F, slot * 20, this.inputs.get(slot));
            }
        }
    }

    @Override
    public boolean propagateMouseClick(double mouseX, double mouseZ) {
        return this.handleBookLookupClick(mouseX, mouseZ);
    }

    @Override
    public void postRender(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
        this.renderHoverTooltips(mouseX, mouseY, zLevel, this.recipeId);
    }
}
