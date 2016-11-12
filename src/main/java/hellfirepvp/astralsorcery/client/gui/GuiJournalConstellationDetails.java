package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.text.OverlayText;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.client.util.MoonPhaseRenderHelper;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.common.constellation.CelestialHandler;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.Tier;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.EnumSet;
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

    private static OverlayText.OverlayFontRenderer fontRenderer = new OverlayText.OverlayFontRenderer();
    private static final BindableResource texArrow = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiJArrow");

    private Constellation constellation;
    private GuiJournalConstellationCluster origin;
    private boolean detailed;

    private Rectangle rectBack;
    private List<CelestialHandler.MoonPhase> phases = new LinkedList<>();

    public GuiJournalConstellationDetails(GuiJournalConstellationCluster origin, Constellation c, boolean detailed) {
        super(-1);
        this.origin = origin;
        this.constellation = c;
        this.detailed = detailed;
        testPhases();
    }

    private void testPhases() {
        Tier t = constellation.queryTier();
        for (CelestialHandler.MoonPhase ph : CelestialHandler.MoonPhase.values()) {
            if(t.areAppearanceConditionsMet(ph, EnumSet.noneOf(CelestialHandler.CelestialEvent.class)))
                phases.add(ph);
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
        fontRenderer.zLevel = zLevel;
        fontRenderer.font_size_multiplicator = 0.08F;
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
        fontRenderer.font_size_multiplicator = 0.08F;
        float br = 0.8666F;
        GL11.glColor4f(br, br, br, 0.8F);
        String info = I18n.translateToLocal(constellation.getInfoString()).toUpperCase();
        double w = fontRenderer.getStringWidth(info);
        double chX = 305 - (w / 2);
        fontRenderer.drawString(info, guiLeft + chX, guiTop + 18, zLevel, null, 0.7F, 0);

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
        GL11.glColor4f(1F, 1F, 1F, 0.6F);
        GL11.glPushMatrix();
        fontRenderer.zLevel = zLevel;
        fontRenderer.font_size_multiplicator = 0.05F;
        String trCh = detailed ? I18n.translateToLocal(constellation.queryTier().chanceAsRarityUnlocName()).toUpperCase() : "???";
        String chance = I18n.translateToLocal("tier.chance").toUpperCase();

        fontRenderer.drawString(chance, guiLeft + 228, guiTop + 154, zLevel, null, 0.7F, 0);
        TextureHelper.refreshTextureBindState();

        fontRenderer.font_size_multiplicator = 0.045F;
        fontRenderer.drawString(trCh, guiLeft + 230, guiTop + 175, zLevel, null, 0.7F, 0);
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        int size = 19;
        int offsetX = 105 + (width / 2) - (phases.size() * (size + 2)) / 2;
        int offsetY = 199 + guiTop;
        for (int i = 0; i < phases.size(); i++) {
            CelestialHandler.MoonPhase ph = phases.get(i);
            MoonPhaseRenderHelper.getMoonPhaseTexture(ph).bind();
            drawRect(offsetX + (i * (size + 2)), offsetY, size, size);
        }
    }

    private void drawConstellation() {
        float br = 0.866F;
        GL11.glColor4f(br, br, br, 0.8F);
        String name = I18n.translateToLocal(constellation.getName()).toUpperCase();
        double width = fontRenderer.getStringWidth(name);
        double offsetX = 110 - (width / 2);
        fontRenderer.drawString(name, guiLeft + offsetX, guiTop + 15, zLevel, null, 0.7F, 0);
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();
        RenderConstellation.renderConstellationIntoGUI(new Color(0xDDDDDD), constellation, guiLeft + 25, guiTop + 60, zLevel, 170, 170, 3F, new RenderConstellation.BrightnessFunction() {
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

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);
        if(rectResearchBookmark != null && rectResearchBookmark.contains(p)) {
            GuiJournalProgression.resetJournal();
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalProgression.getJournalInstance());
            return;
        }
        if(rectConstellationBookmark != null && rectConstellationBookmark.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalConstellations.getConstellationScreen());
            return;
        }
        if(rectBack != null && rectBack.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(origin);
        }
    }

}
