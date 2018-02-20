/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.gui.GuiJournalConstellationCluster;
import hellfirepvp.astralsorcery.client.gui.GuiJournalPerkMap;
import hellfirepvp.astralsorcery.client.gui.GuiJournalProgression;
import hellfirepvp.astralsorcery.client.gui.journal.page.IGuiRenderablePage;
import hellfirepvp.astralsorcery.client.gui.journal.page.IJournalPage;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.util.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
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

    private static GuiJournalPages openGuiInstance;
    private static boolean saveSite = true;
    //private static OverlayText.OverlayFontRenderer titleFontRenderer = new OverlayText.OverlayFontRenderer();

    private static final BindableResource texArrow = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijarrow");
    private static final BindableResource texUnderline = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "underline");

    @Nullable
    private final GuiJournalProgression origin;
    @Nullable
    private final GuiScreen previous;
    private final ResearchNode researchNode;
    private List<IGuiRenderablePage> pages;
    private String unlocTitle;

    private boolean informPreviousClose = true;
    private int currentPageOffset = 0; //* 2 = left page.
    private Rectangle rectBack, rectNext, rectPrev;

    GuiJournalPages(@Nullable GuiJournalProgression origin, ResearchNode node) {
        super(-1);
        this.researchNode = node;
        this.origin = origin;
        this.previous = null;
        this.pages = new ArrayList<>(node.getPages().size());
        pages.addAll(node.getPages().stream().map(IJournalPage::buildRenderPage).collect(Collectors.toList()));
        this.unlocTitle = node.getUnLocalizedName();
    }

    //Use this to use this screen independently of the actual journal.
    public GuiJournalPages(@Nullable GuiScreen previous, ResearchNode detailedInformation, int exactPage) {
        super(-1);
        this.researchNode = detailedInformation;
        this.origin = null;
        this.previous = previous;
        this.pages = new ArrayList<>(detailedInformation.getPages().size());
        pages.addAll(detailedInformation.getPages().stream().map(IJournalPage::buildRenderPage).collect(Collectors.toList()));
        this.unlocTitle = detailedInformation.getUnLocalizedName();
        this.currentPageOffset = exactPage / 2;
    }

    public int getCurrentPageOffset() {
        return currentPageOffset;
    }

    public ResearchNode getResearchNode() {
        return researchNode;
    }

    @Override
    public void initGui() {
        super.initGui();

        if(origin != null) {
            origin.rescaleAndRefresh = false;
            origin.setGuiSize(width, height);
            origin.initGui();
        }
    }

    public static GuiJournalPages getClearOpenGuiInstance() {
        GuiJournalPages gui = openGuiInstance;
        openGuiInstance = null;
        return gui;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        if(origin != null) {
            if(saveSite) {
                openGuiInstance = this;
                GuiJournalProgression.getJournalInstance().rescaleAndRefresh = false;
            } else {
                saveSite = true;
                openGuiInstance = null;
            }
        }
        if(previous != null) {
            if(informPreviousClose) {
                previous.onGuiClosed();
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        if(origin != null) {
            drawDefault(textureResBlank);
        } else {
            drawWHRect(textureResBlank);
        }
        TextureHelper.refreshTextureBindState();

        zLevel += 100;
        int pageOffsetY = 20;
        if(currentPageOffset == 0) {
            /*texUnderline.bind();
            GL11.glPushMatrix();
            GL11.glTranslated(guiLeft + 20, guiTop + 15, zLevel);
            drawTexturedRectAtCurrentPos(175, 6);
            GL11.glPopMatrix();*/

            TextureHelper.refreshTextureBindState();
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            String name = I18n.format(unlocTitle);
            double width = fontRenderer.getStringWidth(name);
            GL11.glTranslated(guiLeft + 117, guiTop + 22, 0);
            GL11.glScaled(1.3, 1.3, 1.3);
            GL11.glTranslated(-(width / 2), 0, 0);
            fontRenderer.drawString(name, 0, 0, 0x00DDDDDD);//Color.LIGHT_GRAY.getRGB());
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glPopMatrix();

            texUnderline.bind();
            GL11.glPushMatrix();
            GL11.glTranslated(guiLeft + 30, guiTop + 35, zLevel);
            drawTexturedRectAtCurrentPos(175, 6);
            GL11.glPopMatrix();
            pageOffsetY = 50;
            TextureHelper.refreshTextureBindState();
        }

        int index = currentPageOffset * 2;
        if(pages.size() > index) {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            IGuiRenderablePage page = pages.get(index);
            page.render    (guiLeft + 30, guiTop + pageOffsetY, partialTicks, zLevel, mouseX, mouseY);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
            TextureHelper.refreshTextureBindState();
        }
        index = index + 1;
        if(pages.size() > index) {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            IGuiRenderablePage page = pages.get(index);
            page.render    (guiLeft + 220, guiTop + 20, partialTicks, zLevel, mouseX, mouseY);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
            TextureHelper.refreshTextureBindState();
        }

        drawBackArrow(partialTicks);
        drawNavArrows(partialTicks);
        TextureHelper.refreshTextureBindState();

        index = currentPageOffset * 2;
        if(pages.size() > index) {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            IGuiRenderablePage page = pages.get(index);
            page.postRender(guiLeft + 30, guiTop + pageOffsetY, partialTicks, zLevel, mouseX, mouseY);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
            TextureHelper.refreshTextureBindState();
        }
        index = index + 1;
        if(pages.size() > index) {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            IGuiRenderablePage page = pages.get(index);
            page.postRender(guiLeft + 220, guiTop + 20, partialTicks, zLevel, mouseX, mouseY);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
            TextureHelper.refreshTextureBindState();
        }
        zLevel -= 100;

        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void drawBackArrow(float partialTicks) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        Point mouse = getCurrentMousePoint();
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
            double t = ClientScheduler.getClientTick() + partialTicks;
            float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
            GL11.glScaled(sin, sin, sin);
        }
        GL11.glColor4f(1F, 1F, 1F, 0.8F);
        GL11.glTranslated(-(width / 2), -(height / 2), 0);
        texArrow.bind();
        drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private void drawNavArrows(float partialTicks) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        Point mouse = getCurrentMousePoint();
        int cIndex = currentPageOffset * 2;
        rectNext = null;
        rectPrev = null;
        if(cIndex > 0) {
            int width = 30;
            int height = 15;
            rectPrev = new Rectangle(guiLeft + 25, guiTop + 220, width, height);
            GL11.glPushMatrix();
            GL11.glTranslated(rectPrev.getX() + (width / 2), rectPrev.getY() + (height / 2), 0);
            float uFrom = 0F, vFrom = 0.5F;
            if(rectPrev.contains(mouse)) {
                uFrom = 0.5F;
                GL11.glScaled(1.1, 1.1, 1.1);
            } else {
                double t = ClientScheduler.getClientTick() + partialTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
                GL11.glScaled(sin, sin, sin);
            }
            GL11.glColor4f(1F, 1F, 1F, 0.8F);
            GL11.glTranslated(-(width / 2), -(height / 2), 0);
            texArrow.bind();
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
            GL11.glPopMatrix();
        }
        int nextIndex = cIndex + 2;
        if(pages.size() >= (nextIndex + 1)) {
            int width = 30;
            int height = 15;
            rectNext = new Rectangle(guiLeft + 367, guiTop + 220, width, height);
            GL11.glPushMatrix();
            GL11.glTranslated(rectNext.getX() + (width / 2), rectNext.getY() + (height / 2), 0);
            float uFrom = 0F, vFrom = 0F;
            if(rectNext.contains(mouse)) {
                uFrom = 0.5F;
                GL11.glScaled(1.1, 1.1, 1.1);
            } else {
                double t = ClientScheduler.getClientTick() + partialTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
                GL11.glScaled(sin, sin, sin);
            }
            GL11.glColor4f(1F, 1F, 1F, 0.8F);
            GL11.glTranslated(-(width / 2), -(height / 2), 0);
            texArrow.bind();
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
            GL11.glPopMatrix();
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    @Override
    protected boolean handleRightClickClose(int mouseX, int mouseY) {
        if(origin != null) {
            origin.expectReinit = true;
            saveSite = false;
            Minecraft.getMinecraft().displayGuiScreen(origin);
        } else {
            informPreviousClose = false;
            Minecraft.getMinecraft().displayGuiScreen(previous);
        }
        return true;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if(mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);
        if(origin != null) {
            if(rectResearchBookmark != null && rectResearchBookmark.contains(p)) {
                saveSite = false;
                Minecraft.getMinecraft().displayGuiScreen(origin);
                return;
            }
            if(rectConstellationBookmark != null && rectConstellationBookmark.contains(p)) {
                saveSite = false;
                Minecraft.getMinecraft().displayGuiScreen(GuiJournalConstellationCluster.getConstellationScreen());
                return;
            }
            if(rectPerkMapBookmark != null && rectPerkMapBookmark.contains(p)) {
                saveSite = false;
                Minecraft.getMinecraft().displayGuiScreen(new GuiJournalPerkMap());
                return;
            }
        }
        if(rectBack != null && rectBack.contains(p)) {
            if(origin != null) {
                origin.expectReinit = true;
                saveSite = false;
                Minecraft.getMinecraft().displayGuiScreen(origin);
                return;
            } else {
                informPreviousClose = false;
                Minecraft.getMinecraft().displayGuiScreen(previous);
                return;
            }
        }
        if(rectPrev != null && rectPrev.contains(p)) {
            this.currentPageOffset -= 1;
            SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
            return;
        }
        if(rectNext != null && rectNext.contains(p)) {
            this.currentPageOffset += 1;
            SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
            return;
        }

        int index = currentPageOffset * 2;
        if(pages.size() > index) {
            IGuiRenderablePage page = pages.get(index);
            if(page != null) {
                if(page.propagateMouseClick(mouseX, mouseY)) return;
            }
        }
        index += 1;
        if(pages.size() > index) {
            IGuiRenderablePage page = pages.get(index);
            if(page != null) {
                page.propagateMouseClick(mouseX, mouseY);
            }
        }
    }

}
