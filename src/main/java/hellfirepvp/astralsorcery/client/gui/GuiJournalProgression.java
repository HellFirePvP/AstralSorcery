package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.gui.journal.GuiProgressionRenderer;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.util.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.AssetLoader;
import hellfirepvp.astralsorcery.client.util.BindableResource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournal
 * Created by HellFirePvP
 * Date: 11.08.2016 / 18:38
 */
public class GuiJournalProgression extends GuiScreenJournal {

    public static GuiJournalProgression currentInstance = null;
    public boolean expectReinit = false;

    private static GuiProgressionRenderer progressionRenderer;

    private int bufX, bufY;
    private boolean dragging = false;

    public GuiJournalProgression() {
        super(0);
    }

    @Override
    public void initGui() {
        super.initGui();

        if(expectReinit) {
            expectReinit = false;
            return; //We ASSUME, that the state is valid.
        }

        if(currentInstance == null) {
            currentInstance = this;
            progressionRenderer = new GuiProgressionRenderer(currentInstance, guiHeight - 10, guiWidth - 10);
            progressionRenderer.centerMouse();
        }
        progressionRenderer.updateOffset(guiLeft + 10, guiTop + 10);
        progressionRenderer.setBox(10, 10, guiWidth - 10, guiHeight - 10);
        progressionRenderer.resetZoom();
        progressionRenderer.unfocus();
        progressionRenderer.refreshSize();
        progressionRenderer.updateMouseState();
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
        GL11.glScissor((guiLeft + 20) * res.getScaleFactor(), (guiTop + 20) * res.getScaleFactor(), (guiWidth - 40) * res.getScaleFactor(), (guiHeight - 40) * res.getScaleFactor());
        progressionRenderer.drawProgressionPart(zLevel);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        drawDefault(textureResShell);

        zLevel += 150;
        drawMouseHighlight(zLevel, getCurrentMousePoint());
        zLevel -= 150;

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void drawMouseHighlight(float zLevel, Point mousePoint) {
        progressionRenderer.drawMouseHighlight(zLevel, mousePoint);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);
        if(rectConstellationBookmark != null && rectConstellationBookmark.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalConstellations.getConstellationScreen());
            return;
        }
        progressionRenderer.propagateClick(p);
    }

}
