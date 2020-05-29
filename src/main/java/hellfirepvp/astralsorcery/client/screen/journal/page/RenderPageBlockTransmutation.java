/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.page;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.draw.TextureHelper;
import hellfirepvp.astralsorcery.common.crafting.recipe.BlockTransmutation;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.util.block.BlockMatchInformation;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
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

    private BlockTransmutation recipe;
    private List<ItemStack> inputOptions;

    public RenderPageBlockTransmutation(@Nullable ResearchNode node, int nodePage, BlockTransmutation blockTransmutation) {
        super(node, nodePage);
        this.recipe = blockTransmutation;
        this.inputOptions = blockTransmutation.getInputOptions().stream()
                .map(BlockMatchInformation::getDisplayStack)
                .filter(stack -> !stack.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
        this.clearFrameRectangles();

        this.renderRecipeGrid(offsetX, offsetY, zLevel, TexturesAS.TEX_GUI_BOOK_GRID_TRANSMUTATION);
        this.renderExpectedItemStackOutput(offsetX + 78, offsetY + 25, zLevel, 1.4F, this.recipe.getOutputDisplay());
        if (this.recipe.getRequiredConstellation() != null) {
            this.renderInfoStar(offsetX, offsetY, zLevel, pTicks);
            this.renderRequiredConstellation(offsetX, offsetY, zLevel, this.recipe.getRequiredConstellation());
        }

        float renderX = offsetX + 80;
        float renderY = offsetY + 73;
        this.renderExpectedIngredientInput(renderX, renderY + 80, zLevel, 1.2F, 0, this.inputOptions);

        SpritesAS.SPR_LIGHTBEAM.bindTexture();
        Blending.ADDITIVE_ALPHA.applyStateManager();

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buf = tes.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        RenderingGuiUtils.rect(renderX - 15, renderY + 10, zLevel, 50, 120)
                .tex(SpritesAS.SPR_LIGHTBEAM)
                .draw();
        tes.draw();

        Blending.DEFAULT.applyStateManager();
        TextureHelper.bindBlockAtlas();

        GlStateManager.disableDepthTest();
        GlStateManager.pushMatrix();
        GlStateManager.translated(renderX + 11, renderY + 11, 0);
        GlStateManager.scaled(40, 40, 40);
        RenderingDrawUtils.renderLightRayFan(0, 0, 0, ColorsAS.ROCK_CRYSTAL, getNodePage(), 9, 9, 20);
        GlStateManager.popMatrix();

        this.renderItemStack(renderX - 4, renderY - 4, zLevel, 1.75F, new ItemStack(BlocksAS.ROCK_COLLECTOR_CRYSTAL));
        GlStateManager.enableDepthTest();
    }

    @Override
    public boolean propagateMouseClick(double mouseX, double mouseZ) {
        return this.handleBookLookupClick(mouseX, mouseZ);
    }

    @Override
    public void postRender(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
        this.renderHoverTooltips(mouseX, mouseY, this.recipe.getId());
        this.renderInfoStarTooltips(offsetX, offsetY, mouseX, mouseY, (toolTip) -> {
            this.addConstellationInfoTooltip(this.recipe.getRequiredConstellation(), toolTip);
        });
    }
}
