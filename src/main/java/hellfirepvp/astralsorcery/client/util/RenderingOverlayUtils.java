/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
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

    public static void renderDefaultItemDisplay(List<Tuple<ItemStack, Integer>> itemStacks) {
        int heightNormal  =  26;
        int heightSplit = 13;
        int width   =  26;
        int offsetX =  30;
        int offsetY =  15;

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buf = tes.getBuffer();
        ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();

        //Draw background frame
        int tempY = offsetY;
        for (int i = 0; i < itemStacks.size(); i++) {
            boolean first = i == 0;
            boolean last = i + 1 == itemStacks.size();

            if (first) {
                //Draw upper half of the 1st slot
                buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                TexturesAS.TEX_OVERLAY_ITEM_FRAME.bindTexture();
                buf.pos(offsetX,            tempY + heightSplit, 10).tex(0, 0.5F).endVertex();
                buf.pos(offsetX + width, tempY + heightSplit, 10).tex(1, 0.5F).endVertex();
                buf.pos(offsetX + width,    tempY,               10).tex(1, 0)  .endVertex();
                buf.pos(offsetX,               tempY,               10).tex(0, 0)  .endVertex();
                tes.draw();
                tempY += heightSplit;
            } else {
                //Draw lower half and upper next half of the sequence
                buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                TexturesAS.TEX_OVERLAY_ITEM_FRAME_EXTENSION.bindTexture();
                buf.pos(offsetX,            tempY + heightNormal, 10).tex(0, 1).endVertex();
                buf.pos(offsetX + width, tempY + heightNormal, 10).tex(1, 1).endVertex();
                buf.pos(offsetX + width,    tempY,                10).tex(1, 0).endVertex();
                buf.pos(offsetX,               tempY,                10).tex(0, 0).endVertex();
                tes.draw();
                tempY += heightNormal;
            }
            if (last) {
                //Draw lower half of the slot
                buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                TexturesAS.TEX_OVERLAY_ITEM_FRAME.bindTexture();
                buf.pos(offsetX,            tempY + heightSplit, 10).tex(0, 1)  .endVertex();
                buf.pos(offsetX + width, tempY + heightSplit, 10).tex(1, 1)  .endVertex();
                buf.pos(offsetX + width,    tempY,               10).tex(1, 0.5F).endVertex();
                buf.pos(offsetX,               tempY,               10).tex(0, 0.5F).endVertex();
                tes.draw();
                tempY += heightSplit;
            }
        }

        RenderSystem.disableBlend();
        BlockAtlasTexture.getInstance().bindTexture();

        //Draw itemstacks on frame
        tempY = offsetY;
        for (Tuple<ItemStack, Integer> stackTpl : itemStacks) {
            itemRender.renderItemAndEffectIntoGUI(Minecraft.getInstance().player, stackTpl.getA(), offsetX + 5, tempY + 5);
            tempY += heightNormal;
        }

        //Draw itemstack counts
        RenderSystem.pushMatrix();
        RenderSystem.translated(offsetX + 14, offsetY + 16, 0);
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

            RenderSystem.pushMatrix();
            RenderSystem.translated(-fontRenderer.getStringWidth(amountStr) / 3, 0, 0);
            RenderSystem.scaled(0.7, 0.7, 0.7);
            if (amountStr.length() > 3) {
                RenderSystem.scaled(0.9, 0.9, 0.9);
            }
            RenderingDrawUtils.renderStringAtCurrentPos(fr, amountStr, txtColor);
            RenderSystem.popMatrix();

            RenderSystem.translated(0, heightNormal, 0);
        }

        RenderSystem.popMatrix();
    }

}
