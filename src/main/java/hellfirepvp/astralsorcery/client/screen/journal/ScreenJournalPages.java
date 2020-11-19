/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.base.NavigationArrowScreen;
import hellfirepvp.astralsorcery.client.screen.journal.page.RenderablePage;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.common.data.journal.JournalPage;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenJournalPages
 * Created by HellFirePvP
 * Date: 03.08.2019 / 16:54
 */
public class ScreenJournalPages extends ScreenJournal implements NavigationArrowScreen {

    private static ScreenJournalPages openGuiInstance;
    private static boolean saveSite = true;

    @Nullable
    private final ScreenJournalProgression origin;
    @Nullable
    private final Screen previous;
    private final ResearchNode researchNode;
    private final List<RenderablePage> pages;

    private boolean informPreviousClose = true;
    private int currentPageOffset = 0; //* 2 = left page.
    private Rectangle rectBack, rectNext, rectPrev;

    public ScreenJournalPages(@Nullable ScreenJournalProgression origin, ResearchNode node) {
        super(node.getName(), NO_BOOKMARK);
        this.researchNode = node;
        this.origin = origin;
        this.previous = null;
        List<JournalPage> pageList = node.getPages();
        this.pages = new ArrayList<>(pageList.size());
        for (int i = 0; i < pageList.size(); i++) {
            this.pages.add(pageList.get(i).buildRenderPage(node, i));
        }
    }

    //Use this to use this screen independently of the actual journal.
    public ScreenJournalPages(@Nullable Screen previous, ResearchNode detailedInformation, int exactPage) {
        super(detailedInformation.getName(), NO_BOOKMARK);
        this.researchNode = detailedInformation;
        this.origin = null;
        this.previous = previous;
        this.currentPageOffset = exactPage / 2;
        List<JournalPage> pageList = detailedInformation.getPages();
        this.pages = new ArrayList<>(pageList.size());
        for (int i = 0; i < pageList.size(); i++) {
            this.pages.add(pageList.get(i).buildRenderPage(detailedInformation, i));
        }
    }

    public static ScreenJournalPages getClearOpenGuiInstance() {
        ScreenJournalPages gui = openGuiInstance;
        openGuiInstance = null;
        return gui;
    }

    public int getCurrentPageOffset() {
        return currentPageOffset;
    }

    public ResearchNode getResearchNode() {
        return researchNode;
    }

    @Override
    public void init() {
        super.init();

        if (origin != null) {
            origin.preventRefresh();
            origin.width = width;
            origin.height = height;
            origin.init();
        }
    }

    @Override
    public void render(MatrixStack renderStack, int mouseX, int mouseY, float pTicks) {
        super.render(renderStack, mouseX, mouseY, pTicks);

        if (origin != null) {
            drawDefault(renderStack, TexturesAS.TEX_GUI_BOOK_BLANK, mouseX, mouseY);
        } else {
            RenderSystem.enableBlend();
            Blending.DEFAULT.apply();
            drawWHRect(renderStack, TexturesAS.TEX_GUI_BOOK_BLANK);
            RenderSystem.disableBlend();
        }

        this.setBlitOffset(100);
        int pageYOffset = 20;

        //Headline
        if (this.currentPageOffset == 0) {
            int width = font.getStringPropertyWidth(this.getTitle());

            renderStack.push();
            renderStack.translate(guiLeft + 117, guiTop + 22, this.getGuiZLevel());
            renderStack.scale(1.3F, 1.3F, 1F);
            renderStack.translate(-width / 2F, 0, 0);
            RenderingDrawUtils.renderStringAt(font, renderStack, this.getTitle(), 0x00DDDDDD);
            renderStack.pop();

            RenderSystem.enableBlend();
            Blending.DEFAULT.apply();
            TexturesAS.TEX_GUI_BOOK_UNDERLINE.bindTexture();
            RenderingGuiUtils.drawRect(renderStack, guiLeft + 30, guiTop + 35, this.getGuiZLevel(), 175, 6);
            RenderSystem.disableBlend();

            pageYOffset += 30;
        }

        int index = currentPageOffset * 2;
        if (pages.size() > index) {
            RenderablePage page = pages.get(index);
            page.render(renderStack, guiLeft + 30, guiTop + pageYOffset, this.getGuiZLevel(), pTicks, mouseX, mouseY);
        }
        index = index + 1;
        if (pages.size() > index) {
            RenderablePage page = pages.get(index);
            page.render(renderStack, guiLeft + 220, guiTop + 20, this.getGuiZLevel(), pTicks, mouseX, mouseY);
        }

        this.setBlitOffset(120);
        drawNavArrows(renderStack, pTicks, mouseX, mouseY);
        this.setBlitOffset(100);

        index = currentPageOffset * 2;
        if (pages.size() > index) {
            RenderablePage page = pages.get(index);
            page.postRender(renderStack, guiLeft + 30, guiTop + pageYOffset, this.getGuiZLevel(), pTicks, mouseX, mouseY);
        }
        index = index + 1;
        if (pages.size() > index) {
            RenderablePage page = pages.get(index);
            page.postRender(renderStack, guiLeft + 220, guiTop + 20, this.getGuiZLevel(), pTicks, mouseX, mouseY);
        }

        this.setBlitOffset(0);
    }

    private void drawNavArrows(MatrixStack renderStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();

        this.rectNext = null;
        this.rectPrev = null;
        this.rectBack = this.drawArrow(renderStack, guiLeft + 197, guiTop + 230, this.getGuiZLevel(), Type.LEFT, mouseX, mouseY, partialTicks);

        int cIndex = currentPageOffset * 2;
        if (cIndex > 0) {
            this.rectPrev = this.drawArrow(renderStack, guiLeft + 25, guiTop + 220, this.getGuiZLevel(), Type.LEFT, mouseX, mouseY, partialTicks);
        }
        int nextIndex = cIndex + 2;
        if (pages.size() >= (nextIndex + 1)) {
            this.rectNext = this.drawArrow(renderStack, guiLeft + 367, guiTop + 220, this.getGuiZLevel(), Type.RIGHT, mouseX, mouseY, partialTicks);
        }

        RenderSystem.disableBlend();
    }

    @Override
    protected boolean shouldRightClickCloseScreen(double mouseX, double mouseY) {
        if (origin != null) {
            origin.expectReInit();
            saveSite = false;
        } else {
            informPreviousClose = false;
        }
        return true;
    }

    @Override
    public void closeScreen() {
        if (origin != null) {
            if (saveSite) {
                openGuiInstance = this;
                ScreenJournalProgression.getJournalInstance().preventRefresh();
                Minecraft.getInstance().displayGuiScreen(null);
            } else {
                saveSite = true;
                openGuiInstance = null;
                Minecraft.getInstance().displayGuiScreen(origin);
            }
        } else {
            if (previous != null && informPreviousClose) {
                previous.closeScreen();
            }
            Minecraft.getInstance().displayGuiScreen(previous);
        }
    }

    @Override
    protected void mouseDragTick(double mouseX, double mouseY, double mouseDiffX, double mouseDiffY, double mouseOffsetX, double mouseOffsetY) {
        int index = currentPageOffset * 2;
        if (pages.size() > index) {
            RenderablePage page = pages.get(index);
            if (page != null) {
                if (page.propagateMouseDrag(mouseOffsetX, mouseOffsetY)) {
                    return;
                }
            }
        }
        index += 1;
        if (pages.size() > index) {
            RenderablePage page = pages.get(index);
            if (page != null) {
                page.propagateMouseDrag(mouseOffsetX, mouseOffsetY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }

        if (mouseButton == 1) {
            return true;
        }

        if (mouseButton != 0) {
            return false;
        }

        if (origin != null) {
            if (handleBookmarkClick(mouseX, mouseY)) {
                saveSite = false;
                return true;
            }
        }
        if (rectBack != null && rectBack.contains(mouseX, mouseY)) {
            if (origin != null) {
                origin.expectReInit();
                saveSite = false;
                this.closeScreen();
                return true;
            } else {
                informPreviousClose = false;
                this.closeScreen();
                return true;
            }
        }
        if (rectPrev != null && rectPrev.contains(mouseX, mouseY)) {
            this.currentPageOffset -= 1;
            SoundHelper.playSoundClient(SoundsAS.GUI_JOURNAL_PAGE, 1F, 1F);
            return true;
        }
        if (rectNext != null && rectNext.contains(mouseX, mouseY)) {
            this.currentPageOffset += 1;
            SoundHelper.playSoundClient(SoundsAS.GUI_JOURNAL_PAGE, 1F, 1F);
            return true;
        }

        int index = currentPageOffset * 2;
        if (pages.size() > index) {
            RenderablePage page = pages.get(index);
            if (page != null) {
                if (page.propagateMouseClick(mouseX, mouseY)) {
                    return true;
                }
            }
        }
        index += 1;
        if (pages.size() > index) {
            RenderablePage page = pages.get(index);
            if (page != null) {
                if (page.propagateMouseClick(mouseX, mouseY)) {
                    return true;
                }
            }
        }
        return false;
    }
}
