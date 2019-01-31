/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.gui.base.GuiWHScreen;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.MoonPhaseRenderHelper;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IConstellationSpecialShowup;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.util.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiConstellationPaper
 * Created by HellFirePvP
 * Date: 01.08.2016 / 01:08
 */
public class GuiConstellationPaper extends GuiWHScreen {

    private static final BindableResource textureScroll = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiconpaper");

    private final IConstellation constellation;
    private List<MoonPhase> phases = new LinkedList<>();

    public GuiConstellationPaper(IConstellation c) {
        super(344, 275);
        this.constellation = c;
        testPhases();
    }

    private void testPhases() {
        Collections.addAll(phases, MoonPhase.values());
    }

    @Override
    public void onGuiClosed() {
        SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        drawScroll();

        drawHeader();

        drawConstellation();

        drawPhaseInformation();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void drawHeader() {
        String locName = I18n.format(constellation.getUnlocalizedName()).toUpperCase();
        TextureHelper.refreshTextureBindState();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        double length = fr.getStringWidth(locName) * 1.8;
        double offsetLeft = width / 2 - length / 2;
        int offsetTop = guiTop + 45;
        GlStateManager.pushMatrix();
        GlStateManager.translate(offsetLeft + 2, offsetTop, 0);
        GlStateManager.scale(1.8, 1.8, 1.8);
        fr.drawString(locName, 0, 0, 0xAA4D4D4D, false);
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1, 1);
        GL11.glColor4f(1, 1, 1, 1);
    }

    private void drawConstellation() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderConstellation.renderConstellationIntoGUI(
                new Color(0.4F, 0.4F, 0.4F, 0.8F), constellation,
                width / 2 - 145 / 2, guiTop + 84,
                zLevel,
                145, 145, 2F, new RenderConstellation.BrightnessFunction() {
                    @Override
                    public float getBrightness() {
                        //return 0.8F - (0.6F * (tierN / h));
                        return 0.5F;
                    }
                }, true, false);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void drawPhaseInformation() {
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();
        Blending.DEFAULT.applyStateManager();
        GL11.glColor4f(1, 1, 1, 1);
        if(constellation instanceof IConstellationSpecialShowup) {
            double scale = 1.8;
            TextureHelper.refreshTextureBindState();
            FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
            double length = fr.getStringWidth("?  ?  ?") * scale;
            double offsetLeft = width / 2 - length / 2;
            int offsetTop = guiTop + 239;
            GL11.glPushMatrix();
            GL11.glTranslated(offsetLeft, offsetTop, 0);
            GL11.glScaled(scale, scale, scale);
            fr.drawString("?  ?  ?", 0, 0, 0xAA4D4D4D, false);
            GL11.glPopMatrix();
            GlStateManager.color(1, 1, 1, 1);
            GL11.glColor4f(1, 1, 1, 1);
            TextureHelper.refreshTextureBindState();
        } else {
            if(this.phases.isEmpty()) {
                testPhases();
                if(this.phases.isEmpty()) {
                    return;
                }
            }

            int size = 16;
            int offsetX = (width / 2) - (phases.size() * (size + 2)) / 2;
            int offsetY = guiTop + 237;
            for (int i = 0; i < phases.size(); i++) {
                MoonPhase ph = phases.get(i);
                MoonPhaseRenderHelper.getMoonPhaseTexture(ph).bind();
                drawRect(offsetX + (i * (size + 2)), offsetY, size, size);
            }
        }
    }

    private void drawScroll() {
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();
        drawWHRect(textureScroll);
        GL11.glDisable(GL11.GL_BLEND);
    }

}
