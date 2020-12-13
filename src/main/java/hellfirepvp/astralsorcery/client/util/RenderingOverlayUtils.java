/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingOverlayUtils
 * Created by HellFirePvP
 * Date: 28.02.2020 / 21:41
 */
public class RenderingOverlayUtils {

    public static void renderDefaultItemDisplay(MatrixStack renderStack, List<Tuple<ItemStack, Integer>> itemStacks) {
        int heightNormal  =  26;
        int heightSplit = 13;
        int width   =  26;
        int offsetX =  30;
        int offsetY =  15;

        ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        //Draw background frame
        int tempY = offsetY;
        for (int i = 0; i < itemStacks.size(); i++) {
            boolean first = i == 0;
            boolean last = i + 1 == itemStacks.size();
            float currentY = tempY;

            if (first) {
                //Draw upper half of the 1st slot
                TexturesAS.TEX_OVERLAY_ITEM_FRAME.bindTexture();
                RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX, buf -> {
                    Matrix4f offset = renderStack.getLast().getMatrix();
                    buf.pos(offset, offsetX,            currentY + heightSplit, 10).tex(0, 0.5F).endVertex();
                    buf.pos(offset, offsetX + width, currentY + heightSplit, 10).tex(1, 0.5F).endVertex();
                    buf.pos(offset, offsetX + width,    currentY,               10).tex(1, 0)  .endVertex();
                    buf.pos(offset, offsetX,               currentY,               10).tex(0, 0)  .endVertex();
                });
                tempY += heightSplit;
            } else {
                //Draw lower half and upper next half of the sequence
                TexturesAS.TEX_OVERLAY_ITEM_FRAME_EXTENSION.bindTexture();
                RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX, buf -> {
                    Matrix4f offset = renderStack.getLast().getMatrix();
                    buf.pos(offset, offsetX,            currentY + heightNormal, 10).tex(0, 1).endVertex();
                    buf.pos(offset, offsetX + width, currentY + heightNormal, 10).tex(1, 1).endVertex();
                    buf.pos(offset, offsetX + width,    currentY,                10).tex(1, 0).endVertex();
                    buf.pos(offset, offsetX,               currentY,                10).tex(0, 0).endVertex();
                });
                tempY += heightNormal;
            }
            if (last) {
                float drawY = tempY;
                //Draw lower half of the slot
                TexturesAS.TEX_OVERLAY_ITEM_FRAME.bindTexture();
                RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX, buf -> {
                    Matrix4f offset = renderStack.getLast().getMatrix();
                    buf.pos(offset, offsetX,            drawY + heightSplit, 10).tex(0, 1)  .endVertex();
                    buf.pos(offset, offsetX + width, drawY + heightSplit, 10).tex(1, 1)  .endVertex();
                    buf.pos(offset, offsetX + width,    drawY,               10).tex(1, 0.5F).endVertex();
                    buf.pos(offset, offsetX,               drawY,               10).tex(0, 0.5F).endVertex();
                });
                tempY += heightSplit;
            }
        }

        RenderSystem.disableBlend();
        BlockAtlasTexture.getInstance().bindTexture();

        //Draw itemstacks on frame
        tempY = offsetY;
        for (Tuple<ItemStack, Integer> stackTpl : itemStacks) {
            renderStack.push();
            renderStack.translate(offsetX + 5, tempY + 5, 0);
            RenderingUtils.renderItemStackGUI(renderStack, stackTpl.getA(), null);
            renderStack.pop();

            tempY += heightNormal;
        }

        //Draw itemstack counts
        renderStack.push();
        renderStack.translate(offsetX + 14, offsetY + 16, 0);
        int txtColor = 0x00DDDDDD;
        for (Tuple<ItemStack, Integer> stackTpl : itemStacks) {
            ItemStack stack = stackTpl.getA();
            FontRenderer fr;
            if ((fr = stack.getItem().getFontRenderer(stack)) == null) {
                fr = fontRenderer;
            }
            String amountStr = String.valueOf(stackTpl.getB());
            if (stackTpl.getB() == -1) {
                amountStr = "\u221E"; //+Inf
            }
            ITextProperties prop = new StringTextComponent(amountStr);
            int length = fontRenderer.getStringPropertyWidth(prop);

            renderStack.push();
            renderStack.translate(-length / 3F, 0, 500);
            renderStack.scale(0.7F, 0.7F, 1F);
            if (amountStr.length() > 3) {
                renderStack.scale(0.9F, 0.9F, 1F);
            }
            RenderingDrawUtils.renderStringAt(fr, renderStack, prop, txtColor);
            renderStack.pop();

            renderStack.translate(0, heightNormal, 0);
        }
        renderStack.pop();
    }

}
