/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.gui.journal.page.IJournalPage;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.MoonPhaseRenderHelper;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IConstellationSpecialShowup;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalConstellationDetails
 * Created by HellFirePvP
 * Date: 16.08.2016 / 19:09
 */
public class GuiJournalConstellationDetails extends GuiScreenJournal {

    //private static OverlayText.OverlayFontRenderer fontRenderer = new OverlayText.OverlayFontRenderer();
    private static final BindableResource texArrow = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiJArrow");

    private IConstellation constellation;
    private GuiJournalConstellationCluster origin;
    private boolean detailed;

    private Rectangle rectBack;
    private List<MoonPhase> phases = new LinkedList<>();
    private List<String> locText = new LinkedList<>();

    public GuiJournalConstellationDetails(GuiJournalConstellationCluster origin, IConstellation c, boolean detailed) {
        super(-1);
        this.origin = origin;
        this.constellation = c;
        this.detailed = detailed;
        testPhases();
        buildLines();
    }

    private void buildLines() {
        String unloc = constellation.getUnlocalizedName() + ".effect";
        String text = I18n.format(unloc);
        if(unloc.equals(text)) return;
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        List<String> lines = new LinkedList<>();
        for (String segment : text.split("<NL>")) {
            StringBuilder cache = new StringBuilder();
            for(String element : segment.split(" ")) {
                String cacheStr = cache.toString();
                String built = cacheStr.isEmpty() ? element : cacheStr + " " + element;
                if(fr.getStringWidth(built) > IJournalPage.DEFAULT_WIDTH - 20) {
                    lines.add(cacheStr);
                    cache = new StringBuilder();
                    cache.append(element);
                } else {
                    if(cacheStr.isEmpty()) {
                        cache.append(element);
                    } else {
                        cache.append(' ').append(element);
                    }
                }
            }
            lines.add(cache.toString());
            lines.add("");
        }
        locText.addAll(lines);
    }

    private void testPhases() {
        if(constellation instanceof IWeakConstellation) {
            Collections.addAll(phases, MoonPhase.values());
        } else if(constellation instanceof IMinorConstellation) {
            //Why this way? To maintain phase-order.
            for (MoonPhase ph : MoonPhase.values()) {
                if(((IMinorConstellation) constellation).getShowupMoonPhases().contains(ph)) {
                    phases.add(ph);
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        drawDefault(textureResBlank);

        zLevel += 150;
        drawBackArrow(partialTicks);
        drawConstellation();
        drawPhaseInformation();
        if(detailed) {
            drawExtendedInformation();
        }
        zLevel -= 150;

        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void drawExtendedInformation() {
        float br = 0.8666F;
        GL11.glColor4f(br, br, br, 0.8F);
        String info = I18n.format(constellation.getUnlocalizedInfo()).toUpperCase();
        TextureHelper.refreshTextureBindState();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        double width = fr.getStringWidth(info);
        double chX = 305 - (width * 1.8 / 2);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glTranslated(guiLeft + chX, guiTop + 26, 0);
        GL11.glScaled(1.8, 1.8, 1.8);
        fr.drawString(info, 0, 0, 0xCCDDDDDD, true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GL11.glColor4f(br, br, br, 0.8F);
        TextureHelper.refreshTextureBindState();

        if(!locText.isEmpty()) {
            int offsetX = 220, offsetY = 70;
            for (String s : locText) {
                GL11.glPushMatrix();
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glTranslated(guiLeft + offsetX, guiTop + offsetY, 0);
                fr.drawString(s, 0, 0, 0xCCDDDDDD, true);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glPopMatrix();
                GlStateManager.color(1F, 1F, 1F, 1F);
                GL11.glColor4f(br, br, br, 0.8F);
                TextureHelper.refreshTextureBindState();
                offsetY += 13;
            }
        }

        /*texArrow.bind();
        fontRenderer.font_size_multiplicator = 0.06F;
        String pref = I18n.translateToLocal("constraint.description");
        fontRenderer.drawString(pref, guiLeft + 228, guiTop + 60, zLevel, null, 0.7F, 0);

        texArrow.bind();
        fontRenderer.font_size_multiplicator = 0.05F;
        SizeConstraint sc = constellation.getSizeConstraint();
        String trSize = I18n.translateToLocal(sc.getUnlocalizedName());
        fontRenderer.drawString("- " + trSize, guiLeft + 228, guiTop + 85, zLevel, null, 0.7F, 0);

        List<RitualConstraint> constrList = constellation.getConstraints();
        for (int i = 0; i < constrList.size(); i++) {
            RitualConstraint cstr = constrList.get(i);
            String str = I18n.translateToLocal(cstr.getUnlocalizedName());
            texArrow.bind();
            fontRenderer.font_size_multiplicator = 0.05F;
            fontRenderer.drawString("- " + str, guiLeft + 228, guiTop + 107 + (i * 22), zLevel, null, 0.7F, 0);
        }*/
    }

    private void drawPhaseInformation() {
        if(constellation instanceof IConstellationSpecialShowup) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            double scale = 1.8;
            TextureHelper.refreshTextureBindState();
            FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
            double length = fr.getStringWidth("? ? ?") * scale;
            double offsetLeft = guiLeft + 296 - length / 2;
            int offsetTop = guiTop + 199;
            GL11.glPushMatrix();
            GL11.glTranslated(offsetLeft + 10, offsetTop, 0);
            GL11.glScaled(scale, scale, scale);
            fr.drawStringWithShadow("? ? ?", 0, 0, 0xCCDDDDDD);
            GL11.glPopMatrix();
            GlStateManager.color(1, 1, 1, 1);
            GL11.glColor4f(1, 1, 1, 1);
            TextureHelper.refreshTextureBindState();
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        } else {
            GL11.glEnable(GL11.GL_BLEND);
            Blending.DEFAULT.apply();
            GL11.glColor4f(1F, 1F, 1F, 1F);
            int size = 19;
            int offsetX = 95 + (width / 2) - (phases.size() * (size + 2)) / 2;
            int offsetY = 199 + guiTop;
            for (int i = 0; i < phases.size(); i++) {
                MoonPhase ph = phases.get(i);
                MoonPhaseRenderHelper.getMoonPhaseTexture(ph).bind();
                drawRect(offsetX + (i * (size + 2)), offsetY, size, size);
            }
        }
    }

    private void drawConstellation() {
        float br = 0.866F;
        GL11.glColor4f(br, br, br, 0.8F);
        String name = I18n.format(constellation.getUnlocalizedName()).toUpperCase();
        TextureHelper.refreshTextureBindState();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        double width = fr.getStringWidth(name);
        double offsetX = 110 - (width * 1.8 / 2);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glTranslated(guiLeft + offsetX, guiTop + 26, 0);
        GL11.glScaled(1.8, 1.8, 1.8);
        fr.drawString(name, 0, 0, 0xCCDDDDDD, true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GL11.glColor4f(br, br, br, 0.8F);
        TextureHelper.refreshTextureBindState();
        String dstInfo = "astralsorcery.journal.constellation.dst.";
        if(constellation instanceof IMajorConstellation) {
            dstInfo += "major";
        } else if(constellation instanceof IWeakConstellation) {
            dstInfo += "weak";
        } else {
            dstInfo += "minor";
        }
        dstInfo = I18n.format(dstInfo);
        width = fr.getStringWidth(dstInfo);
        offsetX = 110 - (width * 1.25 / 2);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glTranslated(guiLeft + offsetX, guiTop + 46, 0);
        GL11.glScaled(1.25, 1.25, 1.25);
        fr.drawString(dstInfo, 0, 0, 0xCCDDDDDD, true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GL11.glColor4f(br, br, br, 0.8F);
        TextureHelper.refreshTextureBindState();

        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();
        RenderConstellation.renderConstellationIntoGUI(new Color(0x00DDDDDD), constellation, guiLeft + 25, guiTop + 60, zLevel, 170, 170, 2F, new RenderConstellation.BrightnessFunction() {
            @Override
            public float getBrightness() {
                return 0.5F;
            }
        }, true, false);
    }

    private void drawBackArrow(float partialTicks) {
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
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if(mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);
        if(rectResearchBookmark != null && rectResearchBookmark.contains(p)) {
            GuiJournalProgression.resetJournal();
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalProgression.getJournalInstance());
            return;
        }
        if(rectConstellationBookmark != null && rectConstellationBookmark.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalConstellationCluster.getConstellationScreen());
            return;
        }
        if(rectPerkMapBookmark != null && rectPerkMapBookmark.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiJournalPerkMap());
            return;
        }
        if(rectBack != null && rectBack.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(origin);
        }
    }

}
