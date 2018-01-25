/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

    private static final int CONSTELLATIONS_PER_PAGE = 4;
    private static final BindableResource texBlack = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "black");
    private static final BindableResource texArrow = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijarrow");
    private static final BindableResource texBg = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiresbgcst");

    private static final int width = 80, height = 110;
    private static final Map<Integer, Point> offsetMap = new HashMap<>(); //we put 6 on "1" page/screen

    private List<IConstellation> constellations;
    private String unlocTitle;
    private int pageId = 0;

    private Map<Rectangle, IConstellation> rectCRenderMap = new HashMap<>();

    private Rectangle rectBack, rectPrev, rectNext;

    public GuiJournalConstellationCluster(int bookmark, int pageId, String unlocTitle, List<IConstellation> constellations) {
        this(bookmark, unlocTitle, constellations);
        this.pageId = pageId;
    }

    public GuiJournalConstellationCluster(int bookmark, String unlocTitle, List<IConstellation> constellations) {
        super(bookmark);
        this.unlocTitle = unlocTitle;
        this.constellations = constellations;
    }

    public static GuiScreenJournal getConstellationScreen() {
        PlayerProgress client = ResearchManager.clientProgress;
        List<IConstellation> constellations = ConstellationRegistry.resolve(client.getSeenConstellations());
        return new GuiJournalConstellationCluster(1, "no.title", constellations);

        /*if(tiersFound.isEmpty()) {
            return new GuiJournalConstellationCluster(1, false, "gui.journal.c.unmapped", unmapped);
        } else if(tiersFound.size() == 1) {
            return new GuiJournalConstellationCluster(1, true, tiersFound.get(0).getUnlocalizedName(), tierMapped.get(tiersFound.get(0)));
        } else {
            return new GuiJournalConstellations(unmapped, tiersFound);
        }*/
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        drawCstBackground();
        drawDefault(textureResShell);

        zLevel += 250;
        drawNavArrows(partialTicks);
        rectCRenderMap.clear();
        drawConstellations(partialTicks);
        zLevel -= 250;

        GlStateManager.popMatrix();
    }

    private void drawCstBackground() {
        texBlack.bind();
        GlStateManager.color(1F, 1F, 1F, 1F);
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder bb = tes.getBuffer();
        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bb.pos(guiLeft + 15,            guiTop + guiHeight - 10, zLevel).tex(0, 1).endVertex();
        bb.pos(guiLeft + guiWidth - 15, guiTop + guiHeight - 10, zLevel).tex(1, 1).endVertex();
        bb.pos(guiLeft + guiWidth - 15, guiTop + 10,             zLevel).tex(1, 0).endVertex();
        bb.pos(guiLeft + 15,            guiTop + 10,             zLevel).tex(0, 0).endVertex();
        tes.draw();
        GlStateManager.color(0.8F, 0.8F, 1F, 0.7F);
        texBg.bind();
        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bb.pos(guiLeft + 15,            guiTop + guiHeight - 10, zLevel).tex(0.1, 0.9).endVertex();
        bb.pos(guiLeft + guiWidth - 15, guiTop + guiHeight - 10, zLevel).tex(0.9, 0.9).endVertex();
        bb.pos(guiLeft + guiWidth - 15, guiTop + 10,             zLevel).tex(0.9, 0.1).endVertex();
        bb.pos(guiLeft + 15,            guiTop + 10,             zLevel).tex(0.1, 0.1).endVertex();
        tes.draw();
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    private void drawConstellations(float partial) {
        Point mouse = getCurrentMousePoint();
        List<IConstellation> cs = constellations.subList(pageId * CONSTELLATIONS_PER_PAGE, Math.min((pageId + 1) * CONSTELLATIONS_PER_PAGE, constellations.size()));
        for (int i = 0; i < cs.size(); i++) {
            IConstellation c = cs.get(i);
            Point p = offsetMap.get(i);
            rectCRenderMap.put(drawConstellationRect(c, guiLeft + p.x, guiTop + p.y, zLevel, partial, mouse, null), c);
        }
        TextureHelper.refreshTextureBindState();
    }

    private void drawNavArrows(float partialTicks) {
        Point mouse = getCurrentMousePoint();
        int cIndex = pageId * CONSTELLATIONS_PER_PAGE;
        rectBack = null;
        rectNext = null;
        rectPrev = null;
        GlStateManager.color(1F, 1F, 1F, 1F);
        if(cIndex > 0) {
            int width = 30;
            int height = 15;
            rectPrev = new Rectangle(guiLeft + 15, guiTop + 127, width, height);
            GlStateManager.pushMatrix();
            GlStateManager.translate(rectPrev.getX() + (width / 2), rectPrev.getY() + (height / 2), 0);
            float uFrom = 0F, vFrom = 0.5F;
            if(rectPrev.contains(mouse)) {
                uFrom = 0.5F;
                GlStateManager.scale(1.1, 1.1, 1.1);
            } else {
                double t = ClientScheduler.getClientTick() + partialTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
                GlStateManager.scale(sin, sin, sin);
            }
            GlStateManager.translate(-(width / 2), -(height / 2), 0);
            texArrow.bind();
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
            GlStateManager.popMatrix();
            TextureHelper.refreshTextureBindState();
        }
        int nextIndex = cIndex + CONSTELLATIONS_PER_PAGE;
        if(constellations.size() >= (nextIndex + 1)) {
            int width = 30;
            int height = 15;
            rectNext = new Rectangle(guiLeft + 367, guiTop + 125, width, height);
            GlStateManager.pushMatrix();
            GlStateManager.translate(rectNext.getX() + (width / 2), rectNext.getY() + (height / 2), 0);
            float uFrom = 0F, vFrom = 0F;
            if(rectNext.contains(mouse)) {
                uFrom = 0.5F;
                GlStateManager.scale(1.1, 1.1, 1.1);
            } else {
                double t = ClientScheduler.getClientTick() + partialTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
                GlStateManager.scale(sin, sin, sin);
            }
            GlStateManager.translate(-(width / 2), -(height / 2), 0);
            texArrow.bind();
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
            GlStateManager.popMatrix();
            TextureHelper.refreshTextureBindState();
        }

        if(bookmarkIndex != 1) {
            int width = 30;
            int height = 15;
            rectBack = new Rectangle(guiLeft + 197, guiTop + 230, width, height);
            GlStateManager.pushMatrix();
            GlStateManager.translate(rectBack.getX() + (width / 2), rectBack.getY() + (height / 2), 0);
            float uFrom = 0F, vFrom = 0.5F;
            if(rectBack.contains(mouse)) {
                uFrom = 0.5F;
                GlStateManager.scale(1.1, 1.1, 1.1);
            } else {
                double t = ClientScheduler.getClientTick() + partialTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
                GlStateManager.scale(sin, sin, sin);
            }
            GlStateManager.translate(-(width / 2), -(height / 2), 0);
            texArrow.bind();
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
            GlStateManager.popMatrix();
            TextureHelper.refreshTextureBindState();
        }
    }

    protected static Rectangle drawConstellationRect(IConstellation display, double offsetX, double offsetY, float zLevel, float partial, Point mouse, @Nullable String specTitle) {
        Rectangle rect = new Rectangle(MathHelper.floor(offsetX), MathHelper.floor(offsetY), width, height);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.translate(offsetX + (width / 2), offsetY + (width / 2), zLevel);

        if(rect.contains(mouse)) {
            GlStateManager.scale(1.1, 1.1, 1.1);
        }

        Random rand = new Random(0x4196A15C91A5E199L);

        GlStateManager.translate(-(width / 2), -(width / 2), zLevel);

        float r = 0xDD / 255F;
        float g = 0xDD / 255F;
        float b = 0xDD / 255F;

        GlStateManager.color(1F, 1F, 1F, 1F);

        RenderConstellation.renderConstellationIntoGUI(display,
                0, 0, 0,
                95, 95, 2F, new RenderConstellation.BrightnessFunction() {
                    @Override
                    public float getBrightness() {
                        return 0.3F + 0.7F * RenderConstellation.conCFlicker(ClientScheduler.getClientTick(), partial, 12 + rand.nextInt(10));
                    }
                }, true, false);

        GlStateManager.color(r, g, b, 1F);

        TextureHelper.refreshTextureBindState();
        String trName = specTitle == null ? I18n.format(display.getUnlocalizedName()).toUpperCase() : I18n.format(specTitle).toUpperCase();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        float fullLength = (width / 2) - (((float) fr.getStringWidth(trName)) / 2F);
        fr.drawString(trName, fullLength, 90F, 0xBBDDDDDD, true);

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.popMatrix();
        return rect;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

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
            Minecraft.getMinecraft().displayGuiScreen(getConstellationScreen());
            return;
        }
        for (Rectangle r : rectCRenderMap.keySet()) {
            if(r.contains(p)) {
                IConstellation c = rectCRenderMap.get(r);
                Minecraft.getMinecraft().displayGuiScreen(new GuiJournalConstellationDetails(this, c));
            }
        }
        if(rectBack != null && rectBack.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(getConstellationScreen());
            return;
        }
        if(rectPrev != null && rectPrev.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiJournalConstellationCluster(bookmarkIndex, pageId - 1, unlocTitle, constellations));
            return;
        }
        if(rectNext != null && rectNext.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiJournalConstellationCluster(bookmarkIndex, pageId + 1, unlocTitle, constellations));
        }
    }

    static {
        offsetMap.put(0, new Point(45, 55));
        offsetMap.put(1, new Point(125, 105));
        offsetMap.put(2, new Point(200, 45));
        offsetMap.put(3, new Point(280, 110));
    }

}
