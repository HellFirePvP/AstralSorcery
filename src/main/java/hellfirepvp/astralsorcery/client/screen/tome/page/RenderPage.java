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
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.screen.tome.TomePagesScreen;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderQuadUtil;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.tome.TomePage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderPage
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@OnlyIn(Dist.CLIENT)
public abstract class RenderPage {

    @Nullable
    private final ResearchNode node;
    private final int nodePage;

    protected int pageX, pageY;

    public RenderPage(@Nullable ResearchNode node, int nodePage) {
        this.node = node;
        this.nodePage = nodePage;
    }

    @Nullable
    protected final ResearchNode getResearchNode() {
        return this.node;
    }

    protected final int getNodePage() {
        return this.nodePage;
    }

    public void init(TomePagesScreen parent, int pageX, int pageY) {
        this.pageX = pageX;
        this.pageY = pageY;
    }

    public void renderPageOverlay(GuiGraphics guiGraphics, int x, int y, AbstractRenderTexture overlay) {
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        overlay.bindTexture();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
            RenderQuadUtil.rect(buf, guiGraphics.pose(), x, y, TomePage.DEFAULT_WIDTH, TomePage.DEFAULT_HEIGHT)
                    .draw();
        });
        RenderSystem.disableBlend();
    }

    public void preRender(GuiGraphics guiGraphics, int x, int y, float pTicks, float mouseX, float mouseY) {}

    public abstract void render(GuiGraphics guiGraphics, int x, int y, float pTicks, float mouseX, float mouseY);

    public void postRender(GuiGraphics guiGraphics, int x, int y, float pTicks, float mouseX, float mouseY) {}

    public void tick() {}

    public boolean propagateMouseClick(double mouseX, double mouseZ) {
        return false;
    }

    public boolean propagateMouseDrag(double mouseX, double mouseY, double mouseDX, double mouseDZ) {
        return false;
    }

    public boolean propagateMouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
        return false;
    }

    public static Font getFontRenderer() {
        return Minecraft.getInstance().font;
    }
}
