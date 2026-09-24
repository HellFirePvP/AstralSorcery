/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.page;

import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.effect.ticket.StaticIdentifierTicket;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.recipe.focal.place.FocalTransmutationRecipe;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.tome.TomePage;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.joml.Vector2f;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderPageFocalTransmutation
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderPageFocalTransmutation extends RenderPageRecipe<FocalTransmutationRecipe> {

    public RenderPageFocalTransmutation(@Nullable ResearchNode node, int nodePage, ResourceLocation recipeId) {
        super(node, nodePage, RecipeTypesAS.FOCAL_TRANSMUTATION_TYPE, recipeId);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float pTicks, float mouseX, float mouseY) {
        this.clearFrame();

        this.resolveRecipeOrWriteError(guiGraphics, x, y).map(RecipeHolder::value).ifPresent(recipe -> {
            this.renderPageOverlay(guiGraphics, x, y, TexturesAS.SCREEN_TOME_PAGE_GRID_FOCAL_TRANSMUTATION);
            List<Component> tip = recipe.requiresFocusedStarlight() ?
                    List.of(Component.translatable("tome.research.info.transmutation_focused_starlight").withStyle(ChatFormatting.GRAY)) :
                    List.of();
            this.renderRecipeHeader(guiGraphics, x, y, false, tip);

            recipe.getRequiredConstellation().ifPresent(cst -> {
                this.renderConstellation(guiGraphics,
                        x + TomePage.DEFAULT_WIDTH / 2 - 70, y + TomePage.DEFAULT_HEIGHT / 2 - 70,
                        140, 140, cst);
            });
            long tick = ClientProxy.getClientTick();

            int midX = x + TomePage.DEFAULT_WIDTH / 2;
            int resultY = y + 18 + 60 / 2;
            this.renderOutput(guiGraphics, midX - 8, resultY - 8, 16, 16, recipe.getOutputForDisplay(tick));

            this.renderInput(guiGraphics, midX - 8, y + 113, recipe.getInputDisplay());

            StaticIdentifierTicket.Container container = this.getEffectContainer();
            if (container.canAddEffects()) {
                RandomSource rand = RandomSource.create();
                int offsetY = y + TomePage.DEFAULT_HEIGHT - 20;

                if (ClientProxy.getClientTick() % 40 == 0) {
                    ColorWrapper color = ColorWrapper.WHITE;
                    if (recipe.getRequiredConstellation().isPresent() && rand.nextInt(3) == 0) {
                        color = recipe.getRequiredConstellation().get().getConstellationColor();
                    }
                    int rX = rand.nextInt(8) - 4;
                    int rY = rand.nextInt(8) - 4;
                    container.createParticle(EffectTemplatesAS.SCREEN_LIGHT_BEAM, midX + rX, offsetY + rY)
                            .setup(new Vector2f(midX + rX, y - 10 + rY), 100F, 100F)
                            .color(FXColorFunction.constant(color));
                }
                for (int i = 0; i < 2; i++) {
                    Vector3 offset = Vector3.random(rand).setZ(0);
                    Vector3 motion = new Vector3(offset.getX(), offset.getY() * 0.4F, 0);
                    container.createParticle(EffectTemplatesAS.SCREEN_PLANE_PARTICLE, midX + offset.getX() * 10F, offsetY - 6 + offset.getY() * 4F)
                            .color(rand.nextBoolean() ? FXColorFunction.WHITE : FXColorFunction.constant(ColorsAS.ROCK_CRYSTAL))
                            .alpha(FXAlphaFunction.fadeIn(10).andThen(FXAlphaFunction.FADE_OUT))
                            .setScale(11F + rand.nextFloat() * 8F)
                            .setMotion(motion.multiply(0.2F));
                }
            }
            this.renderEffects(guiGraphics, pTicks);
            this.renderHoverTooltips(guiGraphics, mouseX, mouseY);
        });
    }
}
