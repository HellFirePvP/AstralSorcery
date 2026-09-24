/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.page;

import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.effect.ticket.StaticIdentifierTicket;
import hellfirepvp.astralsorcery.common.item.block.LumenCrystalClusterBlockItem;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenCrystallizationRecipe;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.tome.TomePage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderPageLumenCrystallization
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderPageLumenCrystallization extends RenderPageRecipe<LumenCrystallizationRecipe> {

    public RenderPageLumenCrystallization(@Nullable ResearchNode node, int nodePage, ResourceLocation recipeId) {
        super(node, nodePage, RecipeTypesAS.LUMEN_CRYSTALLIZATION_TYPE, recipeId);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float pTicks, float mouseX, float mouseY) {
        this.clearFrame();

        this.resolveRecipeOrWriteError(guiGraphics, x, y).map(RecipeHolder::value).ifPresent(recipe -> {
            this.renderPageOverlay(guiGraphics, x, y, TexturesAS.SCREEN_TOME_PAGE_GRID_LUMEN_CRYSTALLIZATION);
            this.renderRecipeHeader(guiGraphics, x, y, false);

            Lumen lumen = recipe.getLumenToCrystallize();
            int yOffset = 113;

            ItemStack cluster = lumen.getHolder()
                    .map(lumenRef -> LumenCrystalClusterBlockItem.getCluster(lumenRef, 4))
                    .orElse(ItemsAS.BLOCK_LUMEN_CRYSTAL_CLUSTER.toStack());

            int midX = x + TomePage.DEFAULT_WIDTH / 2;
            int resultY = y + 18 + 60 / 2;
            this.renderOutput(guiGraphics, midX - 8, resultY - 8, 16, 16, cluster);

            this.renderInput(guiGraphics, midX - 8, y + yOffset, recipe.getInput());
            this.renderScaledItem(guiGraphics, midX - 8, y + yOffset + 16, ItemsAS.BLOCK_LUMEN_CRYSTALLIZER.toStack(), 1.5F);

            int lumenOffsetY = y + yOffset + 60;
            this.renderLumen(midX, lumenOffsetY, lumen);
            this.renderScaledItem(guiGraphics, midX - 8, lumenOffsetY + 16 - 8, ItemsAS.BLOCK_LUMEN_ARRAY.toStack(), 1.5F);

            StaticIdentifierTicket.Container container = this.getEffectContainer();
            if (container.canAddEffects()) {
                this.playLumenEffect(container, midX, lumenOffsetY, lumen);
                this.playLumenConnectionEffect(container, midX, lumenOffsetY, midX, y + yOffset + 8, lumen, ConnectionShape.BEZIER);
            }

            this.renderEffects(guiGraphics, pTicks);
            this.renderHoverTooltips(guiGraphics, mouseX, mouseY);
        });
    }
}
