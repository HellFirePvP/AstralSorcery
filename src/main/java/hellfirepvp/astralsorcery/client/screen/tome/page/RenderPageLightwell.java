/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.page;

import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.effect.ticket.StaticIdentifierTicket;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.recipe.lightwell.LightwellRecipe;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.tome.TomePage;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderPageLightwell
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderPageLightwell extends RenderPageRecipe<LightwellRecipe> {

    public RenderPageLightwell(@Nullable ResearchNode node, int nodePage, ResourceLocation recipeId) {
        super(node, nodePage, RecipeTypesAS.LIGHTWELL_TYPE, recipeId);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float pTicks, float mouseX, float mouseY) {
        this.clearFrame();

        this.resolveRecipeOrWriteError(guiGraphics, x, y).map(RecipeHolder::value).ifPresent(recipe -> {
            this.renderPageOverlay(guiGraphics, x, y, TexturesAS.SCREEN_TOME_PAGE_GRID_LIGHTWELL);
            this.renderRecipeHeader(guiGraphics, x, y, false);

            int midX = x + TomePage.DEFAULT_WIDTH / 2;
            int resultY = y + 18 + 60 / 2;
            int yOffset = 113;

            this.renderLiquidOutput(guiGraphics, midX - 8, resultY - 8, new FluidStack(recipe.getGeneratedFluid(), FluidType.BUCKET_VOLUME));

            this.renderInput(guiGraphics, midX - 8, y + yOffset, recipe.getInput());
            this.renderScaledItem(guiGraphics, midX - 8, y + yOffset + 16, ItemsAS.BLOCK_LIGHTWELL.toStack(), 1.5F);

            StaticIdentifierTicket.Container container = this.getEffectContainer();
            if (container.canAddEffects()) {
                RandomSource rand = RandomSource.create();
                ColorWrapper color = rand.nextBoolean() ? ColorWrapper.WHITE : recipe.getCatalystColor();

                if (rand.nextBoolean()) {
                    int rX = rand.nextInt(60) - 30;
                    int rY = rand.nextInt(60) - 30;
                    container.createParticle(EffectTemplatesAS.SCREEN_PLANE_PARTICLE, midX + rX, y + yOffset + rY)
                            .color(FXColorFunction.constant(color))
                            .setScale(5F + rand.nextFloat() * 5F)
                            .setGravity(Vector3.y(-0.002F + rand.nextFloat() * -0.002F));
                }
            }

            this.renderEffects(guiGraphics, pTicks);
            this.renderHoverTooltips(guiGraphics, mouseX, mouseY);
        });
    }
}
