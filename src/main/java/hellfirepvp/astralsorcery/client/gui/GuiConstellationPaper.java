package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.effect.text.OverlayText;
import hellfirepvp.astralsorcery.client.util.AssetLoader;
import hellfirepvp.astralsorcery.client.util.BindableResource;
import hellfirepvp.astralsorcery.client.util.MoonPhaseRenderHelper;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.common.constellation.CelestialHandler;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.Tier;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiConstellationPaper
 * Created by HellFirePvP
 * Date: 01.08.2016 / 01:08
 */
public class GuiConstellationPaper extends GuiScreen {

    private static final BindableResource textureScroll = AssetLoader.loadTexture(AssetLoader.TextureLocation.GUI, "guiConPaper");
    private static final OverlayText.OverlayFontRenderer ofr = new OverlayText.OverlayFontRenderer();

    public static final int guiHeight = 300;
    public static final int guiWidth = 250;
    private int guiLeft, guiTop;

    private final Constellation constellation;
    private List<CelestialHandler.MoonPhase> phases = new LinkedList<>();

    public GuiConstellationPaper(Constellation c) {
        this.constellation = c;
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
    public void initGui() {
        super.initGui();

        initComponents();
    }

    private void initComponents() {
        guiLeft = width / 2 - guiWidth / 2;
        guiTop = height / 2 - guiHeight / 2;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ofr.font_size_multiplicator = 0.2F;
        drawScroll();

        drawHeader();

        drawConstellation(partialTicks);

        drawPhaseInformation();
    }

    private void drawHeader() {
        String locName = I18n.translateToLocal(constellation.getName()).toUpperCase();
        int length = ofr.getStringWidth(locName);
        int offsetLeft = width / 2 - length / 2;
        int offsetTop = guiTop + 25;
        GL11.glColor4f(0.2F, 0.2F, 0.2F, 0.8F);
        ofr.drawString(locName, offsetLeft, offsetTop, null, 0F, 0);
        GL11.glColor4f(1, 1, 1, 1);
    }

    private void drawConstellation(float parTicks) {
        float h = ConstellationRegistry.getHighestTierNumber();
        float tierN = constellation.queryTier().tierNumber();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderConstellation.renderConstellationIntoGUI(
                new Color(0.3F, 0.3F, 0.3F, 0F), constellation,
                width / 2 - 110 / 2, guiTop + 84,
                zLevel,
                110, 110, 2F, new RenderConstellation.BrightnessFunction() {
                    @Override
                    public float getBrightness() {
                        return 0.8F - (0.6F * (tierN / h));
                    }
                }, true, false);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void drawPhaseInformation() {
        int size = 15;
        int offsetX = (width / 2 + 5) - (phases.size() * (size + 2)) / 2;
        int offsetY = guiTop + 206;
        for (int i = 0; i < phases.size(); i++) {
            CelestialHandler.MoonPhase ph = phases.get(i);
            MoonPhaseRenderHelper.getMoonPhaseTexture(ph).bind();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_DST_ALPHA);
            drawRect(offsetX + (i * (size + 2)), offsetY, size, size);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }


    private void drawScroll() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        textureScroll.bind();
        drawRect(guiLeft, guiTop, guiWidth, guiHeight);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void drawRect(int offsetX, int offsetY, int width, int height) {
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX, offsetY + height, zLevel).tex(0, 1).endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel).tex(1, 1).endVertex();
        vb.pos(offsetX + width, offsetY, zLevel).tex(1, 0).endVertex();
        vb.pos(offsetX, offsetY, zLevel).tex(0, 0).endVertex();
        tes.draw();
    }

}
