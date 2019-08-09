/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.journal.page.RenderablePage;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.common.data.journal.JournalPage;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenJournalPages
 * Created by HellFirePvP
 * Date: 03.08.2019 / 16:54
 */
public class ScreenJournalPages extends ScreenJournal {

    private static ScreenJournalPages openGuiInstance;
    private static boolean saveSite = true;

    @Nullable
    private final ScreenJournalProgression origin;
    @Nullable
    private final Screen previous;
    private final ResearchNode researchNode;
    private List<RenderablePage> pages;

    private boolean informPreviousClose = true;
    private int currentPageOffset = 0; //* 2 = left page.
    private Rectangle rectBack, rectNext, rectPrev;

    public ScreenJournalPages(@Nullable ScreenJournalProgression origin, ResearchNode node) {
        super(new TranslationTextComponent(node.getUnLocalizedName()), NO_BOOKMARK);
        this.researchNode = node;
        this.origin = origin;
        this.previous = null;
        this.pages = new ArrayList<>(node.getPages().size());
        pages.addAll(node.getPages().stream().map(JournalPage::buildRenderPage).collect(Collectors.toList()));
    }

    //Use this to use this screen independently of the actual journal.
    public ScreenJournalPages(@Nullable Screen previous, ResearchNode detailedInformation, int exactPage) {
        super(new TranslationTextComponent(detailedInformation.getUnLocalizedName()), NO_BOOKMARK);
        this.researchNode = detailedInformation;
        this.origin = null;
        this.previous = previous;
        this.pages = new ArrayList<>(detailedInformation.getPages().size());
        pages.addAll(detailedInformation.getPages().stream().map(JournalPage::buildRenderPage).collect(Collectors.toList()));
        this.currentPageOffset = exactPage / 2;
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

        if(origin != null) {
            origin.preventRefresh();
            origin.setSize(width, height);
            origin.init();
        }
    }

    @Override
    public void onClose() {
        super.onClose();

        if (origin != null) {
            if (saveSite) {
                openGuiInstance = this;
                ScreenJournalProgression.getJournalInstance().preventRefresh();
            } else {
                saveSite = true;
                openGuiInstance = null;
            }
        }
        if (previous != null) {
            if (informPreviousClose) {
                previous.onClose();
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float pTicks) {
        super.render(mouseX, mouseY, pTicks);

        GlStateManager.pushMatrix();

        if (origin != null) {
            drawDefault(TexturesAS.TEX_GUI_BOOK_BLANK, mouseX, mouseY);
        } else {
            drawWHRect(TexturesAS.TEX_GUI_BOOK_BLANK);
        }

        this.blitOffset += 100;
        int pageYOffset = 20;

        //Draw headline
        if (this.currentPageOffset == 0) {
            GlStateManager.pushMatrix();
            GlStateManager.disableDepthTest();

            String headline = this.getTitle().getFormattedText();
            double width = font.getStringWidth(headline);
            GlStateManager.translated(guiLeft + 117, guiTop + 22, 0);
            GlStateManager.scaled(1.3, 1.3, 1.3);
            GlStateManager.translated(-(width / 2), 0, 0);
            RenderingDrawUtils.renderStringAtCurrentPos(font, headline, 0x00DDDDDD);

            GlStateManager.enableDepthTest();
            GlStateManager.popMatrix();

            TexturesAS.TEX_GUI_BOOK_UNDERLINE.bindTexture();
            GlStateManager.pushMatrix();
            GlStateManager.translated(guiLeft + 30, guiTop + 35, this.blitOffset);
            drawTexturedRectAtCurrentPos(175, 6);
            GlStateManager.popMatrix();

            pageYOffset += 30;
        }

        int index = currentPageOffset * 2;
        if(pages.size() > index) {
            GlStateManager.pushMatrix();
            RenderablePage page = pages.get(index);
            page.render    (guiLeft + 30, guiTop + pageYOffset, pTicks, this.blitOffset, mouseX, mouseY);
            GlStateManager.popMatrix();
        }
        index = index + 1;
        if(pages.size() > index) {
            GlStateManager.pushMatrix();
            RenderablePage page = pages.get(index);
            page.render    (guiLeft + 220, guiTop + 20, pTicks, this.blitOffset, mouseX, mouseY);
            GlStateManager.popMatrix();
        }

        drawBackArrow(pTicks, mouseX, mouseY);
        drawNavArrows(pTicks, mouseX, mouseY);

        index = currentPageOffset * 2;
        if(pages.size() > index) {
            GlStateManager.pushMatrix();
            RenderablePage page = pages.get(index);
            page.postRender(guiLeft + 30, guiTop + pageYOffset, pTicks, this.blitOffset, mouseX, mouseY);
            GlStateManager.popMatrix();
        }
        index = index + 1;
        if(pages.size() > index) {
            GlStateManager.pushMatrix();
            RenderablePage page = pages.get(index);
            page.postRender(guiLeft + 220, guiTop + 20, pTicks, this.blitOffset, mouseX, mouseY);
            GlStateManager.popMatrix();
        }
        this.blitOffset -= 100;

        GlStateManager.color4f(1F, 1F, 1F, 1F);
        GlStateManager.popMatrix();
    }

    private void drawBackArrow(float partialTicks, int mouseX, int mouseY) {
        int width = 30;
        int height = 15;
        rectBack = new Rectangle(guiLeft + 197, guiTop + 230, width, height);

        GlStateManager.disableDepthTest();
        GlStateManager.pushMatrix();
        GlStateManager.translated(rectBack.getX() + (width / 2), rectBack.getY() + (height / 2), 0);

        float uFrom = 0F, vFrom = 0.5F;
        if(rectBack.contains(mouseX, mouseY)) {
            uFrom = 0.5F;
            GlStateManager.scaled(1.1, 1.1, 1.1);
        } else {
            double t = ClientScheduler.getClientTick() + partialTicks;
            float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
            GlStateManager.scaled(sin, sin, sin);
        }
        GlStateManager.color4f(1F, 1F, 1F, 0.8F);
        GlStateManager.translated(-(width / 2), -(height / 2), 0);
        TexturesAS.TEX_GUI_BOOK_ARROWS.bindTexture();
        drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
        GlStateManager.color4f(1F, 1F, 1F, 1F);

        GlStateManager.popMatrix();
        GlStateManager.enableDepthTest();
    }

    private void drawNavArrows(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.disableDepthTest();
        GlStateManager.color4f(1F, 1F, 1F, 0.8F);
        int cIndex = currentPageOffset * 2;
        rectNext = null;
        rectPrev = null;

        if (cIndex > 0) {
            int width = 30;
            int height = 15;
            rectPrev = new Rectangle(guiLeft + 25, guiTop + 220, width, height);
            GlStateManager.pushMatrix();
            GlStateManager.translated(rectPrev.getX() + (width / 2), rectPrev.getY() + (height / 2), 0);
            float uFrom = 0F, vFrom = 0.5F;
            if (rectPrev.contains(mouseX, mouseY)) {
                uFrom = 0.5F;
                GlStateManager.scaled(1.1, 1.1, 1.1);
            } else {
                double t = ClientScheduler.getClientTick() + partialTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
                GlStateManager.scaled(sin, sin, sin);
            }
            GlStateManager.color4f(1F, 1F, 1F, 0.8F);
            GlStateManager.translated(-(width / 2), -(height / 2), 0);
            TexturesAS.TEX_GUI_BOOK_ARROWS.bindTexture();
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
            GlStateManager.popMatrix();
        }
        int nextIndex = cIndex + 2;
        if (pages.size() >= (nextIndex + 1)) {
            int width = 30;
            int height = 15;
            rectNext = new Rectangle(guiLeft + 367, guiTop + 220, width, height);
            GlStateManager.pushMatrix();
            GlStateManager.translated(rectNext.getX() + (width / 2), rectNext.getY() + (height / 2), 0);
            float uFrom = 0F, vFrom = 0F;
            if (rectNext.contains(mouseX, mouseY)) {
                uFrom = 0.5F;
                GlStateManager.scaled(1.1, 1.1, 1.1);
            } else {
                double t = ClientScheduler.getClientTick() + partialTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
                GlStateManager.scaled(sin, sin, sin);
            }
            GlStateManager.translated(-(width / 2), -(height / 2), 0);
            TexturesAS.TEX_GUI_BOOK_ARROWS.bindTexture();
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
            GlStateManager.popMatrix();
        }
        GlStateManager.color4f(1F, 1F, 1F, 1F);
        GlStateManager.enableDepthTest();
    }

    @Override
    protected boolean handleRightClickClose(double mouseX, double mouseY) {
        if (origin != null) {
            origin.expectReInit();
            saveSite = false;
            Minecraft.getInstance().displayGuiScreen(origin);
        } else {
            informPreviousClose = false;
            Minecraft.getInstance().displayGuiScreen(previous);
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
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
                Minecraft.getInstance().displayGuiScreen(origin);
                return true;
            } else {
                informPreviousClose = false;
                Minecraft.getInstance().displayGuiScreen(previous);
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
                if(page.propagateMouseClick(mouseX, mouseY)) {
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
