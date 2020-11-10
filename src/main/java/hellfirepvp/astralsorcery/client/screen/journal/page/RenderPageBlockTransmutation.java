/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.page;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.crafting.recipe.BlockTransmutation;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.util.block.BlockMatchInformation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderPageBlockTransmutation
 * Created by HellFirePvP
 * Date: 28.05.2020 / 22:41
 */
public class RenderPageBlockTransmutation extends RenderPageRecipeTemplate {

    private final BlockTransmutation recipe;
    private final List<ItemStack> inputOptions;

    public RenderPageBlockTransmutation(@Nullable ResearchNode node, int nodePage, BlockTransmutation blockTransmutation) {
        super(node, nodePage);
        this.recipe = blockTransmutation;
        this.inputOptions = blockTransmutation.getInputOptions().stream()
                .map(BlockMatchInformation::getDisplayStack)
                .filter(stack -> !stack.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public void render(MatrixStack renderStack, float x, float y, float z, float pTicks, float mouseX, float mouseY) {
        this.clearFrameRectangles();

        RenderSystem.depthMask(false);
        this.renderRecipeGrid(renderStack, x, y, z, TexturesAS.TEX_GUI_BOOK_GRID_TRANSMUTATION);
        RenderSystem.depthMask(true);

        this.renderExpectedItemStackOutput(renderStack, x + 78, y + 25, z, 1.4F, this.recipe.getOutputDisplay());
        if (this.recipe.getRequiredConstellation() != null) {
            this.renderInfoStar(renderStack, x, y, z, pTicks);
            this.renderRequiredConstellation(renderStack, x, y, z, this.recipe.getRequiredConstellation());
        }

        float renderX = x + 80;
        float renderY = y + 73;
        this.renderExpectedIngredientInput(renderStack, renderX, renderY + 80, z, 1.2F, 0, this.inputOptions);

        SpritesAS.SPR_LIGHTBEAM.bindTexture();

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        Blending.ADDITIVE_ALPHA.apply();

        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            RenderingGuiUtils.rect(buf, renderStack, renderX - 15, renderY + 10, z, 50, 120)
                    .tex(SpritesAS.SPR_LIGHTBEAM)
                    .draw();
        });

        Blending.DEFAULT.apply();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);

        RenderSystem.disableDepthTest();

        renderStack.push();
        renderStack.translate(renderX + 11, renderY + 11, z);
        renderStack.scale(40, 40, 0);
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR, buf -> {
            RenderingDrawUtils.renderLightRayFan(renderStack, (renderType) -> buf, ColorsAS.ROCK_CRYSTAL, getNodePage(), 9, 9, 20);
        });
        renderStack.pop();

        this.renderItemStack(renderStack, renderX - 4, renderY - 4, z, 1.75F, new ItemStack(BlocksAS.ROCK_COLLECTOR_CRYSTAL));

        RenderSystem.enableDepthTest();
    }

    @Override
    public boolean propagateMouseClick(double mouseX, double mouseZ) {
        return this.handleBookLookupClick(mouseX, mouseZ);
    }

    @Override
    public void postRender(MatrixStack renderStack, float x, float y, float z, float pTicks, float mouseX, float mouseY) {
        this.renderHoverTooltips(renderStack, mouseX, mouseY, z, this.recipe.getId());
        this.renderInfoStarTooltips(renderStack, x, y, z, mouseX, mouseY, (toolTip) -> {
            this.addConstellationInfoTooltip(this.recipe.getRequiredConstellation(), toolTip);
        });
    }
}
