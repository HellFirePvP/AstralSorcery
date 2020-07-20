/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.base;

import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
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
        super.render(mouseX, mouseY, pTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.getBackgroundTexture().bindTexture();

        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            RenderingGuiUtils.rect(buf, this.guiLeft, this.guiTop, this.getBlitOffset(), this.sWidth, this.sHeight).draw();
        });
    }
}
