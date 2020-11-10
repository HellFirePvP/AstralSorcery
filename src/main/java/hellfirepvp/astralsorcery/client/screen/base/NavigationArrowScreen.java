/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.base;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: NavigationArrowScreen
 * Created by HellFirePvP
 * Date: 15.06.2020 / 17:25
 */
public interface NavigationArrowScreen {

    default public Rectangle drawArrow(MatrixStack renderStack, int offsetLeft, int offsetTop, int guiZLevel, Type direction, int mouseX, int mouseY, float pTicks) {
        float width = 30F;
        float height = 15F;

        Rectangle rectArrow = new Rectangle(offsetLeft, offsetTop, (int) width, (int) height);
        renderStack.push();
        renderStack.translate(rectArrow.getX() + (width / 2), rectArrow.getY() + (height / 2), 0);
        float uFrom, vFrom = direction == Type.LEFT ? 0.5F : 0F;
        if (rectArrow.contains(mouseX, mouseY)) {
            uFrom = 0.5F;
            renderStack.scale(1.1F, 1.1F, 1.1F);
        } else {
            uFrom = 0F;
            double t = ClientScheduler.getClientTick() + pTicks;
            float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
            renderStack.scale(sin, sin, sin);
        }
        renderStack.translate(-(width / 2), -(height / 2), 0);

        TexturesAS.TEX_GUI_BOOK_ARROWS.bindTexture();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            RenderingGuiUtils.rect(buf, renderStack, 0, 0, guiZLevel, width, height)
                    .tex(uFrom, vFrom, 0.5F, 0.5F)
                    .color(1F, 1F, 1F, 0.8F)
                    .draw();
        });

        renderStack.pop();

        return rectArrow;
    }

    public static enum Type {

        LEFT,
        RIGHT

    }
}
