package hellfirepvp.astralsorcery.client.gui.journal;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.gui.GuiJournalConstellations;
import hellfirepvp.astralsorcery.client.gui.GuiJournalPerkMap;
import hellfirepvp.astralsorcery.client.gui.GuiJournalProgression;
import hellfirepvp.astralsorcery.client.gui.journal.page.IGuiRenderablePage;
import hellfirepvp.astralsorcery.client.gui.journal.page.IJournalPage;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
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

    private static GuiJournalPages openGuiInstance;
    private static boolean saveSite = true;
    //private static OverlayText.OverlayFontRenderer titleFontRenderer = new OverlayText.OverlayFontRenderer();

    private static final BindableResource texArrow = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiJArrow");
    private static final BindableResource texUnderline = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "underline");

    private GuiJournalProgression origin;
    private List<IGuiRenderablePage> pages;
    private String unlocTitle;

    private int currentPageOffset = 0; //* 2 = left page.
    private Rectangle rectBack, rectNext, rectPrev;

    GuiJournalPages(GuiJournalProgression origin, ResearchNode node) {
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

    public static GuiJournalPages getClearOpenGuiInstance() {
        GuiJournalPages gui = openGuiInstance;
        openGuiInstance = null;
        return gui;
    }

    @Override
    public void onGuiClosed() {
        if(saveSite) {
            openGuiInstance = this;
            GuiJournalProgression.getJournalInstance().rescaleAndRefresh = false;
        } else {
            saveSite = true;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        drawDefault(textureResBlank);
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
            double width = fontRendererObj.getStringWidth(name);
            GL11.glTranslated(guiLeft + 117, guiTop + 22, 0);
            GL11.glScaled(1.3, 1.3, 1.3);
            GL11.glTranslated(-(width / 2), 0, 0);
            fontRendererObj.drawString(name, 0, 0, 0x00DDDDDD);//Color.LIGHT_GRAY.getRGB());
            //fontRendererObj.drawString(name, guiLeft + offsetX, guiTop + 15, zLevel, null, 0.7F, 0);
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
            page.render(guiLeft + 30, guiTop + pageOffsetY, partialTicks, zLevel, mouseX, mouseY);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
            TextureHelper.refreshTextureBindState();
        }
        index = index + 1;
        if(pages.size() > index) {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            IGuiRenderablePage page = pages.get(index);
            page.render(guiLeft + 220, guiTop + 20, partialTicks, zLevel, mouseX, mouseY);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
            TextureHelper.refreshTextureBindState();
        }
        drawBackArrow(partialTicks);
        drawNavArrows(partialTicks);
        TextureHelper.refreshTextureBindState();
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
            float t = ClientScheduler.getClientTick() + partialTicks;
            float sin = MathHelper.sin(t / 4F) / 32F + 1F;
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
                float t = ClientScheduler.getClientTick() + partialTicks;
                float sin = MathHelper.sin(t / 4F) / 32F + 1F;
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
                float t = ClientScheduler.getClientTick() + partialTicks;
                float sin = MathHelper.sin(t / 4F) / 32F + 1F;
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
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);
        if(rectResearchBookmark != null && rectResearchBookmark.contains(p)) {
            saveSite = false;
            Minecraft.getMinecraft().displayGuiScreen(origin);
            return;
        }
        if(rectConstellationBookmark != null && rectConstellationBookmark.contains(p)) {
            saveSite = false;
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalConstellations.getConstellationScreen());
            return;
        }
        if(rectPerkMapBookmark != null && rectPerkMapBookmark.contains(p)) {
            saveSite = false;
            Minecraft.getMinecraft().displayGuiScreen(new GuiJournalPerkMap());
            return;
        }
        if(rectBack != null && rectBack.contains(p)) {
            origin.expectReinit = true;
            saveSite = false;
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
