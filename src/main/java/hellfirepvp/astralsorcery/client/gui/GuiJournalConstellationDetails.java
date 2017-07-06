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
import hellfirepvp.astralsorcery.common.constellation.*;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalConstellationDetails
 * Created by HellFirePvP
 * Date: 16.08.2016 / 19:09
 */
public class GuiJournalConstellationDetails extends GuiScreenJournal {

    //private static OverlayText.OverlayFontRenderer fontRenderer = new OverlayText.OverlayFontRenderer();
    private static final BindableResource texBlack = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "black");
    private static final BindableResource texBg = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiresbgcst");
    private static final BindableResource texArrow = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijarrow");

    private IConstellation constellation;
    private GuiJournalConstellationCluster origin;
    private boolean detailed;

    private Rectangle rectBack;
    private List<MoonPhase> phases = new LinkedList<>();
    private List<MoonPhase> activePhases = new LinkedList<>();
    private List<String> locText = new LinkedList<>();

    public GuiJournalConstellationDetails(GuiJournalConstellationCluster origin, IConstellation c) {
        super(-1);
        this.origin = origin;
        this.constellation = c;
        boolean has = false;
        for (String strConstellation : ResearchManager.clientProgress.getKnownConstellations()) {
            IConstellation ce = ConstellationRegistry.getConstellationByName(strConstellation);
            if(ce != null && ce.equals(c)) {
                has = true;
                break;
            }
        }
        this.detailed = has;
        testPhases();
        testActivePhases();
        buildLines();
    }

    private void buildLines() {
        String unloc = constellation.getUnlocalizedName() + ".effect";
        String text = I18n.format(unloc);
        if(unloc.equals(text)) return;

        List<String> lines = new LinkedList<>();
        for (String segment : text.split("<NL>")) {
            lines.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(segment, IJournalPage.DEFAULT_WIDTH));
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

    private void testActivePhases() {
        if(Minecraft.getMinecraft().world == null) return;
        WorldSkyHandler handler = ConstellationSkyHandler.getInstance().getWorldHandler(Minecraft.getMinecraft().world);
        if(handler == null) return;
        for (MoonPhase phase : this.phases) {
            List<IConstellation> active = handler.getConstellationsForMoonPhase(phase);
            if(active != null && !active.isEmpty()) {
                if(active.contains(constellation)) {
                    activePhases.add(phase);
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        drawCstBackground();
        drawDefault(textureResShellCst);

        zLevel += 150;
        drawArrows(partialTicks);
        drawConstellation(partialTicks);
        drawPhaseInformation();
        drawExtendedInformation();
        zLevel -= 150;

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.popMatrix();
    }

    private void drawCstBackground() {
        texBlack.bind();
        GlStateManager.color(1F, 1F, 1F, 1F);
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder bb = tes.getBuffer();
        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bb.pos(guiLeft + 15,  guiTop + 240, zLevel).tex(0, 1).endVertex();
        bb.pos(guiLeft + 200, guiTop + 240, zLevel).tex(1, 1).endVertex();
        bb.pos(guiLeft + 200, guiTop + 10,  zLevel).tex(1, 0).endVertex();
        bb.pos(guiLeft + 15,  guiTop + 10,  zLevel).tex(0, 0).endVertex();
        tes.draw();
        GlStateManager.color(0.8F, 0.8F, 1F, 0.7F);
        texBg.bind();
        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bb.pos(guiLeft + 35,       guiTop + guiHeight - 10, zLevel).tex(0.3, 0.9).endVertex();
        bb.pos(guiLeft + 35 + 170, guiTop + guiHeight - 10, zLevel).tex(0.7, 0.9).endVertex();
        bb.pos(guiLeft + 35 + 170, guiTop + 10,             zLevel).tex(0.7, 0.1).endVertex();
        bb.pos(guiLeft + 35,       guiTop + 10,             zLevel).tex(0.3, 0.1).endVertex();
        tes.draw();
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    private void drawExtendedInformation() {
        float br = 0.8666F;
        GlStateManager.color(br, br, br, 0.8F);
        String info = I18n.format(constellation.getUnlocalizedInfo()).toUpperCase();
        info = detailed ? info : "???";
        TextureHelper.refreshTextureBindState();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        double width = fr.getStringWidth(info);
        double chX = 305 - (width / 2);
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.translate(guiLeft + chX, guiTop + 44, 0);
        fr.drawString(info, 0, 0, 0xCCDDDDDD, true);
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.color(br, br, br, 0.8F);
        TextureHelper.refreshTextureBindState();

        if(detailed && !locText.isEmpty()) {
            int offsetX = 220, offsetY = 77;
            for (String s : locText) {
                GlStateManager.pushMatrix();
                GlStateManager.disableDepth();
                GlStateManager.translate(guiLeft + offsetX, guiTop + offsetY, 0);
                fr.drawString(s, 0, 0, 0xCCDDDDDD, true);
                GlStateManager.enableDepth();
                GlStateManager.popMatrix();
                GlStateManager.color(1F, 1F, 1F, 1F);
                GlStateManager.color(br, br, br, 0.8F);
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
            GlStateManager.disableDepth();
            double scale = 1.8;
            TextureHelper.refreshTextureBindState();
            FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
            double length = fr.getStringWidth("? ? ?") * scale;
            double offsetLeft = guiLeft + 296 - length / 2;
            int offsetTop = guiTop + 199;
            GlStateManager.pushMatrix();
            GlStateManager.translate(offsetLeft + 10, offsetTop, 0);
            GlStateManager.scale(scale, scale, scale);
            fr.drawStringWithShadow("? ? ?", 0, 0, 0xCCDDDDDD);
            GlStateManager.popMatrix();
            GlStateManager.color(1, 1, 1, 1);
            TextureHelper.refreshTextureBindState();
            GlStateManager.enableDepth();
        } else if(ResearchManager.clientProgress.hasConstellationDiscovered(constellation.getUnlocalizedName())) {
            GlStateManager.enableBlend();
            Blending.DEFAULT.applyStateManager();
            GlStateManager.color(0.7F, 0.7F, 0.7F, 0.6F);
            int size = 19;
            int offsetX = 95 + (width / 2) - (phases.size() * (size + 2)) / 2;
            int offsetY = 199 + guiTop;
            for (int i = 0; i < phases.size(); i++) {
                MoonPhase ph = phases.get(i);
                if(!this.activePhases.contains(ph)) {
                    MoonPhaseRenderHelper.getMoonPhaseTexture(ph).bind();
                    drawRect(offsetX + (i * (size + 2)), offsetY, size, size);
                }
            }
            Blending.PREALPHA.applyStateManager();
            GlStateManager.color(1F, 1F, 1F, 1F);
            for (int i = 0; i < phases.size(); i++) {
                MoonPhase ph = phases.get(i);
                if(this.activePhases.contains(ph)) {
                    MoonPhaseRenderHelper.getMoonPhaseTexture(ph).bind();
                    drawRect(offsetX + (i * (size + 2)), offsetY, size, size);
                }
            }
            Blending.DEFAULT.applyStateManager();
            TextureHelper.refreshTextureBindState();
        } else {
            GlStateManager.enableBlend();
            Blending.DEFAULT.applyStateManager();
            GlStateManager.color(0.8F, 0.8F, 0.8F, 1F);
            int size = 19;
            int offsetX = 95 + (width / 2) - (phases.size() * (size + 2)) / 2;
            int offsetY = 199 + guiTop;
            for (int i = 0; i < phases.size(); i++) {
                MoonPhase ph = phases.get(i);
                MoonPhaseRenderHelper.getMoonPhaseTexture(ph).bind();
                drawRect(offsetX + (i * (size + 2)), offsetY, size, size);
            }
            TextureHelper.refreshTextureBindState();
        }
    }

    private void drawConstellation(float partial) {
        float br = 0.866F;
        GlStateManager.color(br, br, br, 0.8F);
        String name = I18n.format(constellation.getUnlocalizedName()).toUpperCase();
        TextureHelper.refreshTextureBindState();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        double width = fr.getStringWidth(name);
        double offsetX = 305 - (width * 1.8 / 2);
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.translate(guiLeft + offsetX, guiTop + 26, 0);
        GlStateManager.scale(1.8, 1.8, 1.8);
        fr.drawString(name, 0, 0, 0xEEDDDDDD, true);
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.color(br, br, br, 0.8F);
        TextureHelper.refreshTextureBindState();
        String dstInfo = "astralsorcery.journal.constellation.dst.";
        if(constellation instanceof IMajorConstellation) {
            dstInfo += "major";
        } else if(constellation instanceof IWeakConstellation) {
            dstInfo += "weak";
        } else {
            dstInfo += "minor";
        }
        if (!detailed) {
            dstInfo = "???";
        }
        dstInfo = I18n.format(dstInfo);
        width = fr.getStringWidth(dstInfo);
        offsetX = 305 - (width / 2);
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.translate(guiLeft + offsetX, guiTop + 219, 0);
        fr.drawString(dstInfo, 0, 0, 0x99DDDDDD, true);
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
        GlStateManager.color(1F, 1F, 1F, 0.8F);
        TextureHelper.refreshTextureBindState();

        Random rand = new Random(0x4196A15C91A5E199L);

        GlStateManager.enableBlend();
        Blending.DEFAULT.apply();

        boolean known = ResearchManager.clientProgress.hasConstellationDiscovered(constellation.getUnlocalizedName());
        RenderConstellation.renderConstellationIntoGUI(known ? constellation.getConstellationColor() : constellation.getTierRenderColor(), constellation,
                guiLeft + 40, guiTop + 60, zLevel,
                150, 150, 2F,
                new RenderConstellation.BrightnessFunction() {
            @Override
            public float getBrightness() {
                return 0.3F + 0.7F * RenderConstellation.conCFlicker(ClientScheduler.getClientTick(), partial, 12 + rand.nextInt(10));
            }
        }, true, false);
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    private void drawArrows(float partialTicks) {
        Point mouse = getCurrentMousePoint();
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
        GlStateManager.color(1F, 1F, 1F, 0.8F);
        GlStateManager.translate(-(width / 2), -(height / 2), 0);
        texArrow.bind();
        drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
        GlStateManager.popMatrix();
    }

    @Override
    protected boolean handleRightClickClose(int mouseX, int mouseY) {
        Minecraft.getMinecraft().displayGuiScreen(origin);
        return true;
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
