/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.screen.tome.BookmarkProvider;
import hellfirepvp.astralsorcery.client.screen.tome.TomeScreen;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderQuadUtil;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.sounds.SoundManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomeBookmarkElement
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TomeBookmarkElement extends AbstractWidget {

    private final boolean selected;
    private final BookmarkProvider provider;

    public TomeBookmarkElement(int x, int y, boolean selected, BookmarkProvider provider) {
        super(x, y, bookmarkWidth(provider) + 30, 14, provider.getName());
        this.selected = selected;
        this.provider = provider;
    }

    private static int bookmarkWidth(BookmarkProvider provider) {
        Font font = Minecraft.getInstance().font;
        return Math.max(font.width(provider.getName()) + 4, 40);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        PoseStack pose = guiGraphics.pose();
        Font font = Minecraft.getInstance().font;
        float v = this.selected || this.isHovered() ? 0.5F : 0F;

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        this.provider.getBookmarkTexture().bindTexture();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexShader, buf -> {
            int neededWidth = bookmarkWidth(this.provider);
            int offsetX = this.getX();

            RenderQuadUtil.rect(buf, pose, offsetX, this.getY(), 20, 14)
                    .tex(0F, v, 20F / 57F, 0.5F)
                    .draw();

            offsetX += 20;
            neededWidth -= 20;
            while (neededWidth > 0) {
                RenderQuadUtil.rect(buf, pose, offsetX, this.getY(), 5, 14)
                        .tex(21F / 57F, v, 5F / 57F, 0.5F)
                        .draw();
                offsetX += 5;
                neededWidth -= 5;
            }

            RenderQuadUtil.rect(buf, pose, offsetX, this.getY(), 30, 14)
                    .tex(27F / 57F, v, 30F / 57F, 0.5F)
                    .draw();
        });
        RenderSystem.disableBlend();

        guiGraphics.drawString(font, this.provider.getName(), this.getX() + 2, this.getY() + 2, 0xDDDDDDDD);
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        if (Minecraft.getInstance().screen instanceof TomeScreen tomeScreen) {
            if (tomeScreen.getBookmarkIndex() != this.provider.getBookmarkIndex()) {
                tomeScreen.doBookmarkClick(this.provider);
            }
        }
    }

    @Override
    public void playDownSound(SoundManager handler) {
        // No sound ?
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}
