/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.journal.page.RenderablePage;
import hellfirepvp.astralsorcery.client.screen.journal.progression.ScreenJournalProgressionRenderer;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.LinkedList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenJournalProgression
 * Created by HellFirePvP
 * Date: 03.08.2019 / 16:48
 */
public class ScreenJournalProgression extends ScreenJournal {

    private static ScreenJournalProgression currentInstance = null;
    private boolean expectReinit = false;
    private boolean rescaleAndRefresh = true;

    private static ScreenJournalProgressionRenderer progressionRenderer;

    private int bufX, bufY;
    private boolean dragging = false;

    private ScreenJournalProgression() {
        super(new TranslationTextComponent("gui.journal.progression"), 10);
    }

    public static ScreenJournalProgression getJournalInstance() {
        if(currentInstance != null) {
            return currentInstance;
        }
        return new ScreenJournalProgression();
    }

    public static ScreenJournal getOpenJournalInstance() {
        ScreenJournal gui = ScreenJournalPages.getClearOpenGuiInstance();
        if(gui == null) {
            gui = getJournalInstance();
        }
        return gui;
    }

    public void expectReInit() {
        this.expectReinit = true;
    }

    public void preventRefresh() {
        this.rescaleAndRefresh = false;
    }

    //TODO on disconnect
    public static void resetJournal() {
        currentInstance = null;
        ScreenJournalPages.getClearOpenGuiInstance();
    }

    @Override
    public void removed() {
        super.removed();
        rescaleAndRefresh = false;
    }

    @Override
    protected void init() {
        super.init();

        if(expectReinit) {
            expectReinit = false;
            return; //We ASSUME, that the state is valid.
        }

        if(currentInstance == null || progressionRenderer == null) {
            currentInstance = this;
            progressionRenderer = new ScreenJournalProgressionRenderer(currentInstance, guiHeight - 10, guiWidth - 10);
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
    public void render(int mouseX, int mouseY, float pTicks) {
        super.render(mouseX, mouseY, pTicks);

        GlStateManager.enableBlend();
        GlStateManager.pushMatrix();

        int cleanedX = mouseX - guiLeft;
        int cleanedY = mouseY - guiTop;

        if (Minecraft.getInstance().mouseHelper.isLeftDown() && progressionRenderer.realRenderBox.isInBox(cleanedX, cleanedY)) {
            if (dragging) {
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

        MainWindow window = Minecraft.getInstance().mainWindow;
        int factor = (int) window.getGuiScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((guiLeft + 27) * factor, (guiTop + 27) * factor, (guiWidth - 54) * factor, (guiHeight - 54) * factor);
        progressionRenderer.drawProgressionPart(this.blitOffset, mouseX, mouseY);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        drawDefault(TexturesAS.TEX_GUI_BOOK_FRAME_FULL, mouseX, mouseY);

        Rectangle guiStar = null;
        if (!ResearchHelper.getClientProgress().wasOnceAttuned()) {
            GlStateManager.disableDepthTest();
            //TODO render star
            //guiStar = IGuiRenderablePage.GUI_INTERFACE.drawInfoStar( guiLeft + guiWidth - 39, guiTop + 23, zLevel, 15, partialTicks);
            GlStateManager.enableDepthTest();
        }
        this.blitOffset += 150;
        drawMouseHighlight(this.blitOffset, mouseX, mouseY, guiStar);
        this.blitOffset -= 150;

        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
    }

    private void drawMouseHighlight(float zLevel, int mouseX, int mouseY, Rectangle starRect) {
        progressionRenderer.drawMouseHighlight(zLevel, mouseX, mouseY);

        if(starRect != null && starRect.contains(mouseX, mouseY)) {
            RenderingDrawUtils.renderBlueTooltip(mouseX, mouseY, new LinkedList<String>() {
                {
                    add(I18n.format("misc.journal.info.1"));
                    add(I18n.format("misc.journal.info.2",
                            Minecraft.getInstance().gameSettings.keyBindForward.getLocalizedName(),
                            Minecraft.getInstance().gameSettings.keyBindBack.getLocalizedName()));
                }
            }, font, false);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (Minecraft.getInstance().gameSettings.keyBindForward.isKeyDown()) {
            progressionRenderer.handleZoomIn();
        } else if (Minecraft.getInstance().gameSettings.keyBindBack.isKeyDown()) {
            progressionRenderer.handleZoomOut();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if(scroll < 0) {
            progressionRenderer.handleZoomOut();
            return true;
        }
        if(scroll > 0)  {
            progressionRenderer.handleZoomIn();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }

        if(mouseButton != 0) {
            return false;
        }
        if (handleBookmarkClick(mouseX, mouseY)) {
            return true;
        }
        return progressionRenderer.propagateClick(new Point((int) mouseX, (int) mouseY));
    }
}
