/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderQuadUtil;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ScreenContainerMenu
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ScreenContainerMenu<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    private final int screenWidth;
    private final int screenHeight;

    public ScreenContainerMenu(T menu, Inventory playerInventory, Component title, int screenWidth, int screenHeight) {
        super(menu, playerInventory, title);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public abstract AbstractRenderTexture getBackgroundTexture();

    protected int getScreenWidth() {
        return this.screenWidth;
    }

    protected int getScreenHeight() {
        return this.screenHeight;
    }

    @Override
    protected void init() {
        this.imageWidth = this.getScreenWidth();
        this.imageHeight = this.getScreenHeight();
        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        AbstractRenderTexture tex = this.getBackgroundTexture();
        tex.bindTexture();

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();

        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
            RenderQuadUtil.rect(buf, guiGraphics.pose(), this.leftPos, this.topPos, this.getScreenWidth(), this.getScreenHeight())
                    .tex(tex.getUV())
                    .draw();
        });
    }
}
