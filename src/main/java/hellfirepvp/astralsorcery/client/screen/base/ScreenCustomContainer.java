/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.base;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenCustomContainer
 * Created by HellFirePvP
 * Date: 15.08.2019 / 14:56
 */
public abstract class ScreenCustomContainer<T extends Container> extends ContainerScreen<T> implements IHasContainer<T> {

    private final int sWidth, sHeight;

    public ScreenCustomContainer(T screenContainer, PlayerInventory inv, ITextComponent name, int width, int height) {
        super(screenContainer, inv, name);
        this.sWidth = width;
        this.sHeight = height;
    }

    public abstract AbstractRenderableTexture getBackgroundTexture();

    @Override
    protected void init() {
        this.xSize = sWidth;
        this.ySize = sHeight;
        super.init();
    }

    @Override
    public T getContainer() {
        return this.container;
    }

    @Override
    public void render(int mouseX, int mouseY, float pTicks) {
        this.renderBackground();
        GlStateManager.enableBlend();
        super.render(mouseX, mouseY, pTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.getBackgroundTexture().bindTexture();
        this.drawRect(this.guiLeft, this.guiTop, this.sWidth, this.sHeight);
    }

    protected void drawRect(int offsetX, int offsetY, int width, int height, double u, double v, double uLength, double vLength) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,            offsetY + height, this.blitOffset).tex(u,               v + vLength).endVertex();
        vb.pos(offsetX + width, offsetY + height, this.blitOffset).tex(u + uLength, v + vLength).endVertex();
        vb.pos(offsetX + width,    offsetY,          this.blitOffset).tex(u + uLength,    v          ).endVertex();
        vb.pos(offsetX,               offsetY,          this.blitOffset).tex(u,                  v          ).endVertex();
        tes.draw();
    }

    protected void drawRect(int offsetX, int offsetY, int width, int height) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,            offsetY + height, this.blitOffset).tex(0, 1).endVertex();
        vb.pos(offsetX + width, offsetY + height, this.blitOffset).tex(1, 1).endVertex();
        vb.pos(offsetX + width, offsetY,             this.blitOffset).tex(1, 0).endVertex();
        vb.pos(offsetX,            offsetY,             this.blitOffset).tex(0, 0).endVertex();
        tes.draw();
    }
}
