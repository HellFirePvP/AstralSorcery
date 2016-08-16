package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.effect.text.OverlayText;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.util.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.AssetLoader;
import hellfirepvp.astralsorcery.client.util.BindableResource;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.Tier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalConstellationCluster
 * Created by HellFirePvP
 * Date: 15.08.2016 / 12:53
 */
public class GuiJournalConstellationCluster extends GuiScreenJournal {

    private static OverlayText.OverlayFontRenderer fRend = new OverlayText.OverlayFontRenderer();

    private static final BindableResource texArrowLeft = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "arrow_left");
    private static final BindableResource texArrowRight = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "arrow_right");

    private static final int width = 80, height = 110;
    private static final Map<Integer, Point> offsetMap = new HashMap<>(); //we put 6 on "1" page/screen

    private final boolean isDiscovered;
    private List<Constellation> constellations;
    private String unlocTitle;
    private int pageId = 0;

    private Map<Rectangle, Constellation> rectCRenderMap = new HashMap<>();

    private Rectangle rectBack, rectPrev, rectNext;

    public GuiJournalConstellationCluster(int bookmark, int pageId, boolean discoveredTier, String unlocTitle, List<Constellation> constellations) {
        this(bookmark, discoveredTier, unlocTitle, constellations);
        this.pageId = pageId;
    }

    public GuiJournalConstellationCluster(int bookmark, boolean discoveredTier, String unlocTitle, List<Constellation> constellations) {
        super(bookmark);
        this.unlocTitle = unlocTitle;
        this.isDiscovered = discoveredTier;
        this.constellations = constellations;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        drawDefault(textureResBlank);
        zLevel += 250;
        drawNavArrows();
        drawTitle();
        rectCRenderMap.clear();
        drawConstellations();
        zLevel -= 250;
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void drawConstellations() {
        Point mouse = getCurrentMousePoint();
        List<Constellation> cs = constellations.subList(pageId * 6, Math.min((pageId + 1) * 6, constellations.size()));
        for (int i = 0; i < cs.size(); i++) {
            Constellation c = cs.get(i);
            Point p = offsetMap.get(i);
            rectCRenderMap.put(drawConstellationRect(c, guiLeft + p.x, guiTop + p.y, zLevel, mouse, null), c);
        }
    }

    private void drawTitle() {
        if(unlocTitle != null) {
            Color c = Color.DARK_GRAY;
            float r = c.getRed()   / 255F;
            float g = c.getGreen() / 255F;
            float b = c.getBlue()  / 255F;

            GL11.glColor4f(r, g, b, 1F);
            String translated = I18n.translateToLocal(unlocTitle).toUpperCase();
            fRend.zLevel = zLevel;
            fRend.font_size_multiplicator = 0.07F;
            fRend.drawString(translated, guiLeft + 225, guiTop + 14, Color.DARK_GRAY, 0.7F, 0);
            GL11.glColor4f(1F, 1F, 1F, 1F);
        }
    }

    private void drawNavArrows() {
        Point mouse = getCurrentMousePoint();
        int cIndex = pageId * 6;
        rectBack = null;
        rectNext = null;
        rectPrev = null;
        if(cIndex > 0) {
            int width = 30;
            int height = 15;
            rectPrev = new Rectangle(guiLeft + 15, guiTop + 127, width, height);
            GL11.glPushMatrix();
            GL11.glTranslated(rectPrev.getX() + (width / 2), rectPrev.getY() + (height / 2), 0);
            if(rectPrev.contains(mouse)) {
                GL11.glScaled(1.1, 1.1, 1.1);
            }
            GL11.glColor4f(1F, 1F, 1F, 0.8F);
            GL11.glTranslated(-(width / 2), -(height / 2), 0);
            texArrowLeft.bind();
            drawTexturedRectAtCurrentPos(width, height);
            GL11.glPopMatrix();
        }
        int nextIndex = cIndex + 6;
        if(constellations.size() >= (nextIndex + 1)) {
            int width = 30;
            int height = 15;
            rectNext = new Rectangle(guiLeft + 367, guiTop + 125, width, height);
            GL11.glPushMatrix();
            GL11.glTranslated(rectNext.getX() + (width / 2), rectNext.getY() + (height / 2), 0);
            if(rectNext.contains(mouse)) {
                GL11.glScaled(1.1, 1.1, 1.1);
            }
            GL11.glColor4f(1F, 1F, 1F, 0.8F);
            GL11.glTranslated(-(width / 2), -(height / 2), 0);
            texArrowRight.bind();
            drawTexturedRectAtCurrentPos(width, height);
            GL11.glPopMatrix();
        }

        if(bookmarkIndex != 1) {
            int width = 30;
            int height = 15;
            rectBack = new Rectangle(guiLeft + 197, guiTop + 242, width, height);
            GL11.glPushMatrix();
            GL11.glTranslated(rectBack.getX() + (width / 2), rectBack.getY() + (height / 2), 0);
            if(rectBack.contains(mouse)) {
                GL11.glScaled(1.1, 1.1, 1.1);
            }
            GL11.glColor4f(1F, 1F, 1F, 0.8F);
            GL11.glTranslated(-(width / 2), -(height / 2), 0);
            texArrowLeft.bind();
            drawTexturedRectAtCurrentPos(width, height);
            GL11.glPopMatrix();
        }
    }

    protected static Rectangle drawConstellationRect(Constellation display, double offsetX, double offsetY, float zLevel, Point mouse, @Nullable String specTitle) {
        Rectangle rect = new Rectangle(MathHelper.floor_double(offsetX), MathHelper.floor_double(offsetY), width, height);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glTranslated(offsetX + (width / 2), offsetY + (width / 2), zLevel);

        if(rect.contains(mouse)) {
            GL11.glScaled(1.1, 1.1, 1.1);
        }

        GL11.glTranslated(-(width / 2), -(width / 2), zLevel);

        Color c = Color.DARK_GRAY;
        float r = c.getRed()   / 255F;
        float g = c.getGreen() / 255F;
        float b = c.getBlue()  / 255F;

        GL11.glColor4f(r, g, b, 1F);

        RenderConstellation.renderConstellationIntoGUI(c, display,
                0, 0, 0,
                80, 80, 2F, new RenderConstellation.BrightnessFunction() {
                    @Override
                    public float getBrightness() {
                        return 0.15F;
                    }
                }, true, false);

        GL11.glColor4f(r, g, b, 0.7F);

        String trName = specTitle == null ? I18n.translateToLocal(display.getName()).toUpperCase() : I18n.translateToLocal(specTitle).toUpperCase();
        OverlayText.OverlayFontRenderer fontRenderer = new OverlayText.OverlayFontRenderer();
        fontRenderer.font_size_multiplicator = 0.04F;
        float fullLength = (width / 2) - (((float) fontRenderer.getStringWidth(trName)) / 2F);

        fontRenderer.zLevel = zLevel;
        fontRenderer.drawString(trName, fullLength, 75F, null, 1F, 0);

        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glPopMatrix();
        return rect;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);
        if(rectResearchBookmark != null && rectResearchBookmark.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalProgression.currentInstance == null ? new GuiJournalProgression() : GuiJournalProgression.currentInstance);
            return;
        }
        if(bookmarkIndex == -1 && rectConstellationBookmark != null && rectConstellationBookmark.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalConstellations.getConstellationScreen());
            return;
        }
        for (Rectangle r : rectCRenderMap.keySet()) {
            if(r.contains(p)) {
                Constellation c = rectCRenderMap.get(r);
                Minecraft.getMinecraft().displayGuiScreen(new GuiJournalConstellationDetails(this, c, isDiscovered));
            }
        }
        if(rectBack != null && rectBack.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalConstellations.getConstellationScreen());
            return;
        }
        if(rectPrev != null && rectPrev.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiJournalConstellationCluster(bookmarkIndex, pageId - 6, isDiscovered, unlocTitle, constellations));
            return;
        }
        if(rectNext != null && rectNext.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiJournalConstellationCluster(bookmarkIndex, pageId + 6, isDiscovered, unlocTitle, constellations));
        }
    }

    static {
        offsetMap.put(0, new Point( 30,  20));
        offsetMap.put(1, new Point( 20, 150));
        offsetMap.put(2, new Point(120, 100));
        offsetMap.put(3, new Point(310,  20));
        offsetMap.put(4, new Point(220, 100));
        offsetMap.put(5, new Point(310, 140));
    }

}
