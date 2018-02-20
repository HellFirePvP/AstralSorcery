/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.gui.journal.GuiJournalPages;
import hellfirepvp.astralsorcery.client.gui.journal.GuiProgressionRenderer;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.gui.journal.page.IGuiRenderablePage;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.LinkedList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournal
 * Created by HellFirePvP
 * Date: 11.08.2016 / 18:38
 */
public class GuiJournalProgression extends GuiScreenJournal {

    private static GuiJournalProgression currentInstance = null;
    public boolean expectReinit = false;
    public boolean rescaleAndRefresh = true;

    private static GuiProgressionRenderer progressionRenderer;

    private int bufX, bufY;
    private boolean dragging = false;

    private GuiJournalProgression() {
        super(0);
    }

    public static GuiJournalProgression getJournalInstance() {
        if(currentInstance != null) {
            return currentInstance;
        }
        return new GuiJournalProgression();
    }

    public static GuiScreenJournal getOpenJournalInstance() {
        GuiScreenJournal gui = GuiJournalPages.getClearOpenGuiInstance();
        if(gui == null) gui = getJournalInstance();
        return gui;
    }

    public static void resetJournal() {
        currentInstance = null;
        GuiJournalPages.getClearOpenGuiInstance();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        rescaleAndRefresh = false;
    }

    @Override
    public void initGui() {
        super.initGui();

        if(expectReinit) {
            expectReinit = false;
            return; //We ASSUME, that the state is valid.
        }

        if(currentInstance == null || progressionRenderer == null) {
            currentInstance = this;
            progressionRenderer = new GuiProgressionRenderer(currentInstance, guiHeight - 10, guiWidth - 10);
            progressionRenderer.centerMouse();
        }

        progressionRenderer.updateOffset(guiLeft + 10, guiTop + 10);
        progressionRenderer.setBox(10, 10, guiWidth - 10, guiHeight - 10);
        //progressionRenderer.resetOverlayText();

        if(rescaleAndRefresh) {
            progressionRenderer.resetZoom();
            progressionRenderer.unfocus();
            progressionRenderer.refreshSize();
            progressionRenderer.updateMouseState();
        } else {
            rescaleAndRefresh = true;
        }

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        int cleanedX = mouseX - guiLeft;
        int cleanedY = mouseY - guiTop;


        if(Mouse.isButtonDown(0) && progressionRenderer.realRenderBox.isInBox(cleanedX, cleanedY)) {
            if(dragging) {
                progressionRenderer.moveMouse(-(cleanedX - bufX), -(cleanedY - bufY));
            } else {
                bufX = cleanedX;
                bufY = cleanedY;
                dragging = true;
            }
        } else {
            progressionRenderer.applyMovedMouseOffset();
            dragging = false;
        }

        //Zooming stuff.
        int dWheelChange = Mouse.getDWheel();
        if(dWheelChange < 0) {
            progressionRenderer.handleZoomOut();
        }
        if(dWheelChange > 0)  {
            progressionRenderer.handleZoomIn();
        }

        ScaledResolution res = new ScaledResolution(mc);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((guiLeft + 27) * res.getScaleFactor(), (guiTop + 27) * res.getScaleFactor(), (guiWidth - 54) * res.getScaleFactor(), (guiHeight - 54) * res.getScaleFactor());
        progressionRenderer.drawProgressionPart(zLevel);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        drawDefault(textureResShell);

        Rectangle guiStar = null;
        if(!ResearchManager.clientProgress.wasOnceAttuned()) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            guiStar = IGuiRenderablePage.GUI_INTERFACE.drawInfoStar( guiLeft + guiWidth - 39, guiTop + 23, zLevel, 15, partialTicks);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }
        zLevel += 150;
        drawMouseHighlight(zLevel, getCurrentMousePoint(), guiStar);
        zLevel -= 150;

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void drawMouseHighlight(float zLevel, Point mousePoint, Rectangle starRect) {
        progressionRenderer.drawMouseHighlight(zLevel, mousePoint);

        if(starRect != null && starRect.contains(mousePoint)) {
            RenderingUtils.renderBlueTooltip(mousePoint.x, mousePoint.y, new LinkedList<String>() {
                {
                    add(I18n.format("misc.journal.info.1"));
                    add(I18n.format("misc.journal.info.2",
                            Minecraft.getMinecraft().gameSettings.keyBindForward.getDisplayName(),
                            Minecraft.getMinecraft().gameSettings.keyBindBack.getDisplayName()));
                }
            }, IGuiRenderablePage.GUI_INTERFACE.getStandardFontRenderer());
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if(Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
            progressionRenderer.handleZoomIn();
        } else if(Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
            progressionRenderer.handleZoomOut();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if(mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);
        if(rectConstellationBookmark != null && rectConstellationBookmark.contains(p)) {
            resetJournal();
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalConstellationCluster.getConstellationScreen());
            return;
        }
        if(rectPerkMapBookmark != null && rectPerkMapBookmark.contains(p)) {
            resetJournal();
            Minecraft.getMinecraft().displayGuiScreen(new GuiJournalPerkMap());
            return;
        }
        progressionRenderer.propagateClick(p);
    }

}
