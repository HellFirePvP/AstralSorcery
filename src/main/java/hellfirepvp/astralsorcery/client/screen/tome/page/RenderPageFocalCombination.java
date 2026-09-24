/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.page;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.effect.ticket.StaticIdentifierTicket;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderQuadUtil;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.recipe.focal.drop.FocalCombineRecipe;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.tome.TomePage;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.joml.Vector2f;
import org.joml.Vector2i;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderPageFocalCombination
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderPageFocalCombination extends RenderPageRecipe<FocalCombineRecipe> {

    public RenderPageFocalCombination(@Nullable ResearchNode node, int nodePage, ResourceLocation recipeId) {
        super(node, nodePage, RecipeTypesAS.FOCAL_COMBINE_TYPE, recipeId);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float pTicks, float mouseX, float mouseY) {
        this.clearFrame();

        this.resolveRecipeOrWriteError(guiGraphics, x, y).map(RecipeHolder::value).ifPresent(recipe -> {
            this.renderPageOverlay(guiGraphics, x, y, TexturesAS.SCREEN_TOME_PAGE_GRID_EMPTY_RESULT);
            this.renderRecipeHeader(guiGraphics, x, y, false);

            recipe.getRequiredConstellation().ifPresent(cst -> {
                this.renderConstellation(guiGraphics,
                        x + TomePage.DEFAULT_WIDTH / 2 - 70, y + TomePage.DEFAULT_HEIGHT / 2 - 70,
                        140, 140, cst);
            });

            int midX = x + TomePage.DEFAULT_WIDTH / 2;
            int resultY = y + 18 + 60 / 2;
            List<ItemStack> results = recipe.getOutputs();
            ItemStack mainResult = results.isEmpty() ? ItemStack.EMPTY : results.getFirst();
            this.renderOutput(guiGraphics, midX - 8, resultY - 8, 16, 16, mainResult);

            List<ItemStack> remainingResults = results.size() <= 1 ? List.of() : results.subList(1, results.size());
            int additionalX = midX + 60 / 2 + 4;
            int additionalY = y + 8;
            for (ItemStack stack : remainingResults) {
                this.renderOutput(guiGraphics, additionalX, additionalY, 16, 16, stack);
                additionalY += 20;
            }

            List<Ingredient> ingredients = recipe.getInputs();
            int ingredientCount = ingredients.size();
            int rowSize = ingredientCount > 9 ? 3 : 2;

            List<Vector2i> slots = new ArrayList<>();
            for (int i = 0; i < ingredientCount; i++) {
                int row = i / rowSize;
                int col = i % rowSize;
                int itemsInThisRow = Math.min(rowSize, ingredientCount - row * rowSize);
                int rowWidth = itemsInThisRow * 18;
                int slotX = midX - rowWidth / 2 + col * 18;
                int slotY = y + 100 + row * 18;
                slots.add(new Vector2i(slotX, slotY));
            }

            RenderSystem.enableBlend();
            Blending.DEFAULT.apply();
            TexturesAS.SCREEN_TOME_PAGE_SLOT.bindTexture();
            RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
                slots.forEach(vec -> {
                    RenderQuadUtil.rect(buf, guiGraphics.pose(), vec.x(), vec.y(), 18, 18)
                            .draw();
                });
            });
            RenderSystem.disableBlend();

            for (int i = 0; i < slots.size(); i++) {
                Vector2i slot = slots.get(i);
                this.renderInput(guiGraphics, slot.x() + 1, slot.y() + 1, ingredients.get(i));
            }

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
                            .setScale(9F + rand.nextFloat() * 6F)
                            .setMotion(motion.multiply(0.2F));
                }
            }
            this.renderEffects(guiGraphics, pTicks);
            this.renderHoverTooltips(guiGraphics, mouseX, mouseY);
        });
    }
}
