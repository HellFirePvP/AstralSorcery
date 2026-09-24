/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.research;

import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.element.TomeNavArrowElement;
import hellfirepvp.astralsorcery.client.screen.element.TomeSearchEntryElement;
import hellfirepvp.astralsorcery.client.screen.tome.TomeResearchScreen;
import hellfirepvp.astralsorcery.client.sound.PlayableSoundInstance;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.data.ResearchNodeLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomeResearchSearchRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TomeResearchSearchRenderer extends TomeResearchPartRenderer {

    private final List<ResearchNode> matchingNodes = new ArrayList<>();
    private final TreeMap<Integer, List<ResearchNode>> matchingNodesByPage = new TreeMap<>();
    private final List<TomeSearchEntryElement> searchElements = new ArrayList<>();

    //Defines how many search results are on the left/right page
    private static final int searchEntriesLeft = 15;
    private static final int searchEntriesRight = 14;
    private static final int searchEntryDrawWidth = 170; //How long search result strings may be at most

    private int currentPage = 0; //* 2 = left page.

    public TomeResearchSearchRenderer(TomeResearchScreen screen) {
        super(screen);
    }

    @Override
    public void refreshView() {}

    @Override
    public void draw(GuiGraphics guiGraphics, Runnable renderWidgets, float mouseX, float mouseY, float pTicks) {
        this.getParentScreen().renderTransparentBackground(guiGraphics);
        RenderingDrawUtil.drawTexturedRect(guiGraphics.pose(), TexturesAS.SCREEN_TOME_FRAME_FULL, this.getParentScreen().getScreenRectangle());

        renderWidgets.run();
    }

    public void updateSearchContents(String newText) {
        this.matchingNodes.clear();
        this.matchingNodesByPage.clear();

        List<ResearchNode> allNodes = ResearchNodeLoader.getInstance().getNodes();
        String matchText = newText.toLowerCase(Locale.ROOT);

        if (newText.length() >= 3) {
            for (ResearchNode node : allNodes) {
                if (!this.matchingNodes.contains(node) && node.getName().getString().toLowerCase(Locale.ROOT).contains(matchText)) {
                    this.matchingNodes.add(node);
                }
            }
        }
        this.matchingNodes.sort(Comparator.comparing(node -> node.getName().getString()));

        Font fr = Minecraft.getInstance().font;
        int addedPages = 0;
        int pageIndex = 0;
        while (addedPages < this.matchingNodes.size()) {
            List<ResearchNode> page = this.matchingNodesByPage.computeIfAbsent(pageIndex, index -> new ArrayList<>());
            int remainingLines = (pageIndex % 2 == 0 ? searchEntriesLeft : searchEntriesRight) - page.size();

            ResearchNode toAddNode = this.matchingNodes.get(addedPages);
            int lines = fr.split(toAddNode.getName(), searchEntryDrawWidth).size();

            if (remainingLines < lines) {
                pageIndex++; //Add this node to the next page.
                continue;
            }

            page.add(toAddNode);
            addedPages++;
        }

        //Shift the pages further down in case the result gets narrower
        while (this.currentPage > 0 && this.currentPage > this.matchingNodesByPage.size() / 2) {
            this.currentPage--;
        }
        this.updatePageContents();
    }

    public void updatePageContents() {
        this.searchElements.forEach(element -> this.getParentScreen().removeWidget(element));
        this.searchElements.clear();

        Font font = Minecraft.getInstance().font;
        List<ResearchNode> entries = this.matchingNodesByPage.getOrDefault(this.currentPage, Collections.emptyList());
        int offsetX = this.getParentScreen().getScreenLeft() + 33;
        int offsetY = this.getParentScreen().getScreenTop() + 24;
        int maxWidth = 4 + entries.stream()
                .map(ResearchNode::getName)
                .mapToInt(font::width)
                .max().orElse(0);
        for (ResearchNode entry : entries) {
            TomeSearchEntryElement entryElement = new TomeSearchEntryElement(offsetX, offsetY, this.getParentScreen(), entry, maxWidth);
            this.searchElements.add(entryElement);
            this.getParentScreen().addRenderableWidget(entryElement);
            offsetY += 13;
        }

        offsetX = this.getParentScreen().getScreenLeft() + 223;
        offsetY = this.getParentScreen().getScreenTop() + 37;
        entries = this.matchingNodesByPage.getOrDefault(this.currentPage + 1, Collections.emptyList());
        maxWidth = 4 + entries.stream()
                .map(ResearchNode::getName)
                .mapToInt(font::width)
                .max().orElse(0);
        for (ResearchNode entry : entries) {
            TomeSearchEntryElement entryElement = new TomeSearchEntryElement(offsetX, offsetY, this.getParentScreen(), entry, maxWidth);
            this.searchElements.add(entryElement);
            this.getParentScreen().addRenderableWidget(entryElement);
            offsetY += 13;
        }
    }

    public void incrementPage() {
        if (this.hasNextPage()) {
            this.currentPage++;
            PlayableSoundInstance.of(SoundsAS.SCREEN_TOME_PAGE).forUI().play();
            this.updatePageContents();
        }
    }

    public void decrementPage() {
        if (this.currentPage > 0) {
            this.currentPage--;
            PlayableSoundInstance.of(SoundsAS.SCREEN_TOME_PAGE).forUI().play();
            this.updatePageContents();
        }
    }

    public boolean hasNextPage() {
        return this.matchingNodesByPage.size() / 2 > this.currentPage + 1;
    }

    @Override
    public boolean mouseClick(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
        return false;
    }

    public void setNavArrowVisibility(boolean isSearchView, TomeNavArrowElement arrowLeft, TomeNavArrowElement arrowRight) {
        if (!isSearchView) {
            arrowLeft.active = false;
            arrowLeft.visible = false;
            arrowRight.active = false;
            arrowRight.visible = false;
            return;
        }

        if (this.currentPage > 0) {
            arrowLeft.active = true;
            arrowLeft.visible = true;
        } else {
            arrowLeft.active = false;
            arrowLeft.visible = false;
        }
        if (this.hasNextPage()) {
            arrowRight.active = true;
            arrowRight.visible = true;
        } else {
            arrowRight.active = false;
            arrowRight.visible = false;
        }
    }
}
