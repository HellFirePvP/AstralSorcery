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
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenGenerationRecipe;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.tome.TomePage;
import hellfirepvp.astralsorcery.common.util.data.IntPoint;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderPageLumenGeneration
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderPageLumenGeneration extends RenderPageRecipe<LumenGenerationRecipe> {

    public RenderPageLumenGeneration(@Nullable ResearchNode node, int nodePage, ResourceLocation recipeId) {
        super(node, nodePage, RecipeTypesAS.LUMEN_GENERATION_TYPE, recipeId);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float pTicks, float mouseX, float mouseY) {
        this.clearFrame();

        this.resolveRecipeOrWriteError(guiGraphics, x, y).map(RecipeHolder::value).ifPresent(recipe -> {
            List<Lumen> requiredLumen = new ArrayList<>(recipe.getLumenCombinationInputs().keySet());
            requiredLumen.sort(Comparator.comparing(lumen -> lumen.getRegistryKey().orElse(LumenAS.NONE.getKey())));
            Lumen producedLumen = recipe.getProducedLumen();
            int yOffset = 113;

            ItemStack display = requiredLumen.isEmpty() ? ItemsAS.BLOCK_LUMEN_ARRAY.toStack() : ItemsAS.BLOCK_LUMEN_ALCHEMY_ARRAY.toStack();

            this.renderPageOverlay(guiGraphics, x, y, TexturesAS.SCREEN_TOME_PAGE_GRID_LUMEN_GENERATION);
            this.renderRecipeHeader(guiGraphics, x, y, false);

            int midX = x + TomePage.DEFAULT_WIDTH / 2;
            int resultY = y + 18 + 60 / 2;
            this.renderLumen(midX, resultY, producedLumen);

            this.renderInput(guiGraphics, midX - 8, y + yOffset, recipe.getInput());
            this.renderScaledItem(guiGraphics, midX - 8, y + yOffset + 16, display, 1.5F);

            Map<IntPoint, Lumen> lumenInputEffects = new HashMap<>();
            float angleDegPerInput = requiredLumen.size() < 5 ? 50F : 270F / requiredLumen.size();
            float angleOffset = -angleDegPerInput * (requiredLumen.size() - 1) / 2F;
            for (int i = 0; i < requiredLumen.size(); i++) {
                Lumen lumen = requiredLumen.get(i);
                double angle = Math.toRadians(angleOffset + i * angleDegPerInput);
                Vector3 offset = new Vector3(0, 1, 0).rotate(angle, Vector3.RotAxis.Z_AXIS).normalize().multiply(60F);

                int offsetX = midX + Mth.floor(offset.getX());
                int offsetY = y + yOffset + Mth.floor(offset.getY());
                this.renderLumen(offsetX, offsetY, lumen);
                lumenInputEffects.put(new IntPoint(offsetX, offsetY), lumen);
                this.renderScaledItem(guiGraphics, offsetX - 8, offsetY + 12 - 8, ItemsAS.BLOCK_LUMEN_ARRAY.toStack(), 1F);
            }

            StaticIdentifierTicket.Container container = this.getEffectContainer();
            if (container.canAddEffects()) {
                this.playLumenEffect(container, midX, resultY, producedLumen);
                lumenInputEffects.forEach((pos, lumen) -> {
                    this.playLumenEffect(container, pos.x(), pos.y(), lumen);
                    this.playLumenConnectionEffect(container, pos.x(), pos.y(), midX, y + yOffset + 8, lumen, ConnectionShape.STRAIGHT);
                });
                this.playLumenConnectionEffect(container, midX, y + yOffset + 8, midX, resultY, producedLumen, ConnectionShape.BEZIER);
            }

            this.renderEffects(guiGraphics, pTicks);
            this.renderHoverTooltips(guiGraphics, mouseX, mouseY);
        });
    }
}
