package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
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

    //private static OverlayText.OverlayFontRenderer fRend = new OverlayText.OverlayFontRenderer();

    //private static final BindableResource texArrowLeft = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "arrow_left");
    //private static final BindableResource texArrowRight = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "arrow_right");

    private static final BindableResource texArrow = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiJArrow");

    private static final int width = 80, height = 110;
    private static final Map<Integer, Point> offsetMap = new HashMap<>(); //we put 6 on "1" page/screen

    private final boolean isDiscovered;
    private List<IConstellation> constellations;
    private String unlocTitle;
    private int pageId = 0;

    private Map<Rectangle, IConstellation> rectCRenderMap = new HashMap<>();

    private Rectangle rectBack, rectPrev, rectNext;

    public GuiJournalConstellationCluster(int bookmark, int pageId, boolean discoveredTier, String unlocTitle, List<IConstellation> constellations) {
        this(bookmark, discoveredTier, unlocTitle, constellations);
        this.pageId = pageId;
    }

    public GuiJournalConstellationCluster(int bookmark, boolean discoveredTier, String unlocTitle, List<IConstellation> constellations) {
        super(bookmark);
        this.unlocTitle = unlocTitle;
        this.isDiscovered = discoveredTier;
        this.constellations = constellations;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        drawDefault(textureResBlank);

        zLevel += 250;
        drawNavArrows(partialTicks);
        //fRend.zLevel = zLevel;
        //fRend.font_size_multiplicator = 0.07F;
        drawTitle();
        rectCRenderMap.clear();
        drawConstellations();
        zLevel -= 250;

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void drawConstellations() {
        Point mouse = getCurrentMousePoint();
        List<IConstellation> cs = constellations.subList(pageId * 6, Math.min((pageId + 1) * 6, constellations.size()));
        for (int i = 0; i < cs.size(); i++) {
            IConstellation c = cs.get(i);
            Point p = offsetMap.get(i);
            rectCRenderMap.put(drawConstellationRect(c, guiLeft + p.x, guiTop + p.y, zLevel, mouse, null), c);
        }
    }

    private void drawTitle() {
        //no title atm
        if(unlocTitle != null && false) {
            float r = 0xDD / 255F;
            float g = 0xDD / 255F;
            float b = 0xDD / 255F;

            TextureHelper.refreshTextureBindState();
            GL11.glColor4f(r, g, b, 1F);
            String translated = I18n.format(unlocTitle).toUpperCase();
            FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
            fr.drawString(translated, guiLeft + 225, guiTop + 14, Color.DARK_GRAY.getRGB(), true);
            //fRend.drawString(translated, guiLeft + 225, guiTop + 14, zLevel, Color.DARK_GRAY, 0.7F, 0);
            GlStateManager.color(1F, 1F, 1F, 1F);
            GL11.glColor4f(1F, 1F, 1F, 1F);
        }
    }

    private void drawNavArrows(float partialTicks) {
        Point mouse = getCurrentMousePoint();
        int cIndex = pageId * 6;
        rectBack = null;
        rectNext = null;
        rectPrev = null;
        GL11.glColor4f(1F, 1F, 1F, 1F);
        if(cIndex > 0) {
            int width = 30;
            int height = 15;
            rectPrev = new Rectangle(guiLeft + 15, guiTop + 127, width, height);
            GL11.glPushMatrix();
            GL11.glTranslated(rectPrev.getX() + (width / 2), rectPrev.getY() + (height / 2), 0);
            float uFrom = 0F, vFrom = 0.5F;
            if(rectPrev.contains(mouse)) {
                uFrom = 0.5F;
                GL11.glScaled(1.1, 1.1, 1.1);
            } else {
                float t = ClientScheduler.getClientTick() + partialTicks;
                float sin = MathHelper.sin(t / 4F) / 32F + 1F;
                GL11.glScaled(sin, sin, sin);
            }
            GL11.glTranslated(-(width / 2), -(height / 2), 0);
            texArrow.bind();
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
            GL11.glPopMatrix();
            TextureHelper.refreshTextureBindState();
        }
        int nextIndex = cIndex + 6;
        if(constellations.size() >= (nextIndex + 1)) {
            int width = 30;
            int height = 15;
            rectNext = new Rectangle(guiLeft + 367, guiTop + 125, width, height);
            GL11.glPushMatrix();
            GL11.glTranslated(rectNext.getX() + (width / 2), rectNext.getY() + (height / 2), 0);
            float uFrom = 0F, vFrom = 0F;
            if(rectNext.contains(mouse)) {
                uFrom = 0.5F;
                GL11.glScaled(1.1, 1.1, 1.1);
            } else {
                float t = ClientScheduler.getClientTick() + partialTicks;
                float sin = MathHelper.sin(t / 4F) / 32F + 1F;
                GL11.glScaled(sin, sin, sin);
            }
            GL11.glTranslated(-(width / 2), -(height / 2), 0);
            texArrow.bind();
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
            GL11.glPopMatrix();
            TextureHelper.refreshTextureBindState();
        }

        if(bookmarkIndex != 1) {
            int width = 30;
            int height = 15;
            rectBack = new Rectangle(guiLeft + 197, guiTop + 230, width, height);
            GL11.glPushMatrix();
            GL11.glTranslated(rectBack.getX() + (width / 2), rectBack.getY() + (height / 2), 0);
            float uFrom = 0F, vFrom = 0.5F;
            if(rectBack.contains(mouse)) {
                uFrom = 0.5F;
                GL11.glScaled(1.1, 1.1, 1.1);
            } else {
                float t = ClientScheduler.getClientTick() + partialTicks;
                float sin = MathHelper.sin(t / 4F) / 32F + 1F;
                GL11.glScaled(sin, sin, sin);
            }
            GL11.glTranslated(-(width / 2), -(height / 2), 0);
            texArrow.bind();
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
            GL11.glPopMatrix();
            TextureHelper.refreshTextureBindState();
        }
    }

    protected static Rectangle drawConstellationRect(IConstellation display, double offsetX, double offsetY, float zLevel, Point mouse, @Nullable String specTitle) {
        Rectangle rect = new Rectangle(MathHelper.floor(offsetX), MathHelper.floor(offsetY), width, height);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glTranslated(offsetX + (width / 2), offsetY + (width / 2), zLevel);

        if(rect.contains(mouse)) {
            GL11.glScaled(1.1, 1.1, 1.1);
        }

        GL11.glTranslated(-(width / 2), -(width / 2), zLevel);

        Color c = new Color(0x00DDDDDD);
        float r = 0xDD / 255F;
        float g = 0xDD / 255F;
        float b = 0xDD / 255F;

        GL11.glColor4f(r, g, b, 1F);

        RenderConstellation.renderConstellationIntoGUI(c, display,
                0, 0, 0,
                80, 80, 2F, new RenderConstellation.BrightnessFunction() {
                    @Override
                    public float getBrightness() {
                        return 0.15F;
                    }
                }, true, false);

        GL11.glColor4f(r, g, b, 1F);

        TextureHelper.refreshTextureBindState();
        String trName = specTitle == null ? I18n.format(display.getUnlocalizedName()).toUpperCase() : I18n.format(specTitle).toUpperCase();
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        //OverlayText.OverlayFontRenderer fontRenderer = new OverlayText.OverlayFontRenderer();
        //fontRenderer.font_size_multiplicator = 0.04F;
        float fullLength = (width / 2) - (((float) fr.getStringWidth(trName)) / 2F);

        fr.drawString(trName, fullLength, 75F, 0xBBDDDDDD, true);
        //fontRenderer.drawString(trName, fullLength, 75F, zLevel, null, 1F, 0);

        GL11.glColor4f(1F, 1F, 1F, 1F);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GL11.glPopMatrix();
        return rect;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);
        if(rectResearchBookmark != null && rectResearchBookmark.contains(p)) {
            GuiJournalProgression.resetJournal();
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalProgression.getJournalInstance());
            return;
        }
        if(rectPerkMapBookmark != null && rectPerkMapBookmark.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiJournalPerkMap());
            return;
        }
        if(bookmarkIndex == -1 && rectConstellationBookmark != null && rectConstellationBookmark.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalConstellations.getConstellationScreen());
            return;
        }
        for (Rectangle r : rectCRenderMap.keySet()) {
            if(r.contains(p)) {
                IConstellation c = rectCRenderMap.get(r);
                Minecraft.getMinecraft().displayGuiScreen(new GuiJournalConstellationDetails(this, c, isDiscovered));
            }
        }
        if(rectBack != null && rectBack.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalConstellations.getConstellationScreen());
            return;
        }
        if(rectPrev != null && rectPrev.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiJournalConstellationCluster(bookmarkIndex, pageId - 1, isDiscovered, unlocTitle, constellations));
            return;
        }
        if(rectNext != null && rectNext.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiJournalConstellationCluster(bookmarkIndex, pageId + 1, isDiscovered, unlocTitle, constellations));
        }
    }

    static {
        offsetMap.put(0, new Point( 30,  20));
        offsetMap.put(1, new Point( 30, 140));
        offsetMap.put(2, new Point(120,  80));

        offsetMap.put(3, new Point(310,  20));
        offsetMap.put(4, new Point(220, 100));
        offsetMap.put(5, new Point(310, 140));
    }

}
