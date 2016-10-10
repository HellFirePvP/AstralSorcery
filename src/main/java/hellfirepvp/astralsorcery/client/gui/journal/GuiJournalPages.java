package hellfirepvp.astralsorcery.client.gui.journal;

import hellfirepvp.astralsorcery.client.effect.text.OverlayText;
import hellfirepvp.astralsorcery.client.gui.GuiJournalConstellations;
import hellfirepvp.astralsorcery.client.gui.GuiJournalProgression;
import hellfirepvp.astralsorcery.client.gui.journal.page.IGuiRenderablePage;
import hellfirepvp.astralsorcery.client.gui.journal.page.IJournalPage;
import hellfirepvp.astralsorcery.client.util.SpecialTextureLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalPages
 * Created by HellFirePvP
 * Date: 29.08.2016 / 18:01
 */
public class GuiJournalPages extends GuiScreenJournal {

    private static OverlayText.OverlayFontRenderer titleFontRenderer = new OverlayText.OverlayFontRenderer();

    private static final BindableResource texArrowLeft = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "arrow_left");
    private static final BindableResource texArrowRight = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "arrow_right");
    private static final BindableResource texUnderline = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "underline");

    private GuiJournalProgression origin;
    private List<IGuiRenderablePage> pages;
    private String unlocTitle;

    private int currentPageOffset = 0; //* 2 = left page.
    private Rectangle rectBack, rectNext, rectPrev;

    public GuiJournalPages(GuiJournalProgression origin, ResearchNode node) {
        super(-1);
        this.origin = origin;
        this.pages = new ArrayList<>(node.getPages().size());
        pages.addAll(node.getPages().stream().map(IJournalPage::buildRenderPage).collect(Collectors.toList()));
        this.unlocTitle = node.getUnLocalizedName();
    }

    @Override
    public void initGui() {
        super.initGui();

        origin.rescaleAndRefresh = false;
        origin.setGuiSize(width, height);
        origin.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        drawDefault(textureResBlank);

        zLevel += 100;
        drawBackArrow();
        drawNavArrows();
        int pageOffsetY = 15;
        if(currentPageOffset == 0) {
            titleFontRenderer.zLevel = zLevel;
            titleFontRenderer.font_size_multiplicator = 0.04F;
            String name = I18n.translateToLocal(unlocTitle).toUpperCase();
            double width = titleFontRenderer.getStringWidth(name);
            double offsetX = 105 - (width / 2);
            titleFontRenderer.drawString(name, guiLeft + offsetX, guiTop + 15, zLevel, null, 0.7F, 0);

            texUnderline.bind();
            GL11.glPushMatrix();
            GL11.glTranslated(guiLeft + 15, guiTop + pageOffsetY + 20, zLevel);
            drawTexturedRectAtCurrentPos(190, 6);
            GL11.glPopMatrix();
            pageOffsetY = 50;
            SpecialTextureLibrary.setActiveTextureToAtlasSprite();
        }

        int index = currentPageOffset * 2;
        if(pages.size() > index) {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            IGuiRenderablePage page = pages.get(index);
            page.render(guiLeft + 20, guiTop + pageOffsetY, partialTicks, zLevel);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
            SpecialTextureLibrary.setActiveTextureToAtlasSprite();
        }
        index = currentPageOffset + 1;
        if(pages.size() > index) {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            IGuiRenderablePage page = pages.get(index);
            page.render(guiLeft + 220, guiTop + 20, partialTicks, zLevel);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
            SpecialTextureLibrary.setActiveTextureToAtlasSprite();
        }

        zLevel -= 100;

        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void drawBackArrow() {
        Point mouse = getCurrentMousePoint();
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

    private void drawNavArrows() {
        Point mouse = getCurrentMousePoint();
        int cIndex = currentPageOffset * 2;
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
        int nextIndex = cIndex + 2;
        if(pages.size() >= (nextIndex + 1)) {
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
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);
        if(rectResearchBookmark != null && rectResearchBookmark.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalProgression.currentInstance == null ? new GuiJournalProgression() : GuiJournalProgression.currentInstance);
            return;
        }
        if(rectConstellationBookmark != null && rectConstellationBookmark.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalConstellations.getConstellationScreen());
            return;
        }
        if(rectBack != null && rectBack.contains(p)) {
            origin.expectReinit = true;
            Minecraft.getMinecraft().displayGuiScreen(origin);
        }
        if(rectPrev != null && rectPrev.contains(p)) {
            this.currentPageOffset -= 1;
            return;
        }
        if(rectNext != null && rectNext.contains(p)) {
            this.currentPageOffset += 1;
        }
    }

}
