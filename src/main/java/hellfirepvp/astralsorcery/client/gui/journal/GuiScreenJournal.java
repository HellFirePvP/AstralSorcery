/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal;

import hellfirepvp.astralsorcery.client.gui.GuiWHScreen;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerkMap;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiScreenJournal
 * Created by HellFirePvP
 * Date: 15.08.2016 / 12:40
 */
public abstract class GuiScreenJournal extends GuiWHScreen {

    public static final BindableResource textureResBlank    = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijblankbook");
    public static final BindableResource textureResShell    = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijspacebook");
    public static final BindableResource textureResShellCst = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijspaceconstellation");
    public static final BindableResource textureBookmark    = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijbookmark");
    public static final BindableResource textureBookmarkStr = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijbookmarkstretched");

    protected final int bookmarkIndex;

    protected Rectangle rectResearchBookmark, rectConstellationBookmark, rectPerkMapBookmark;

    public GuiScreenJournal(int bookmarkIndex) {
        super(270, 420);
        this.bookmarkIndex = bookmarkIndex;
    }

    public void drawDefault(BindableResource background) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        Point mouse = getCurrentMousePoint();

        zLevel += 100; //To ensure that it over-renders items conflicting with the shell.
        drawWHRect(background);
        drawBookmarks(zLevel, mouse);
        zLevel -= 100;

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void drawBookmarks(float zLevel, Point mousePoint) {
        GL11.glPushMatrix();
        GL11.glColor4f(1F, 1F, 1F, 1F);

        double bookmarkWidth =  67;
        double bookmarkHeight = 15;

        double offsetX = guiLeft + guiWidth - 17.25;
        double offsetY = guiTop  + 20;

        rectResearchBookmark = drawBookmark(offsetX, offsetY, bookmarkWidth, bookmarkHeight, bookmarkWidth + (bookmarkIndex == 0 ? 0 : 5), zLevel, "gui.journal.bm.knowledge.name", 0xDDDDDDDD, mousePoint);

        if(!ResearchManager.clientProgress.getSeenConstellations().isEmpty()) {
            offsetY = guiTop + 40;
            rectConstellationBookmark = drawBookmark(offsetX, offsetY, bookmarkWidth, bookmarkHeight, bookmarkWidth + (bookmarkIndex == 1 ? 0 : 5), zLevel, "gui.journal.bm.constellations.name", 0xDDDDDDDD, mousePoint);
        }

        IMajorConstellation attuned = ResearchManager.clientProgress.getAttunedConstellation();
        //attuned = Constellations.discidia;
        if(attuned != null) {
            ConstellationPerkMap map = attuned.getPerkMap();
            if(map != null) {
                offsetY = guiTop + 60;
                rectPerkMapBookmark = drawBookmark(offsetX, offsetY, bookmarkWidth, bookmarkHeight, bookmarkWidth + (bookmarkIndex == 2 ? 0 : 5), zLevel, "gui.journal.bm.perks.name", 0xDDDDDDDD, mousePoint);
            }
        }

        GL11.glPopMatrix();
    }

    private Rectangle drawBookmark(double offsetX, double offsetY, double width, double height, double mouseOverWidth, float zLevel, String title, int titleRGBColor, Point mousePoint) {
        TextureHelper.setActiveTextureToAtlasSprite();
        //Reset styles, because MC fontrenderer is STUPID A F
        if(titleRGBColor == Color.WHITE.getRGB()) {
            fontRenderer.drawString("", 0, 0, Color.BLACK.getRGB());
        } else {
            fontRenderer.drawString("", 0, 0, Color.WHITE.getRGB());
        }
        GL11.glPushMatrix();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GlStateManager.color(1F, 1F, 1F, 1F);
        textureBookmark.bind();

        Rectangle r = new Rectangle(MathHelper.floor(offsetX), MathHelper.floor(offsetY), MathHelper.floor(width), MathHelper.floor(height));
        if(r.contains(mousePoint)) {
            if(mouseOverWidth > width) {
                textureBookmarkStr.bind();
            }
            width = mouseOverWidth;
            r = new Rectangle(MathHelper.floor(offsetX), MathHelper.floor(offsetY), MathHelper.floor(width), MathHelper.floor(height));
        }

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, zLevel).tex(0, 1).endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel).tex(1, 1).endVertex();
        vb.pos(offsetX + width, offsetY,          zLevel).tex(1, 0).endVertex();
        vb.pos(offsetX,         offsetY,          zLevel).tex(0, 0).endVertex();
        tes.draw();

        GL11.glPushMatrix();
        GL11.glTranslated(offsetX + 2, offsetY + 4, zLevel + 50);
        GL11.glScaled(0.7, 0.7, 0.7);
        fontRenderer.drawString(I18n.format(title), 0, 0, titleRGBColor);
        GL11.glPopMatrix();

        GlStateManager.color(1F, 1F, 1F, 1F);

        GL11.glPopMatrix();

        return r;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
