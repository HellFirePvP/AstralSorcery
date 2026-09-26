/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.screen.element.TomeNavArrowElement;
import hellfirepvp.astralsorcery.client.screen.element.SearchInputElement;
import hellfirepvp.astralsorcery.client.screen.tome.research.TomeResearchPartRenderer;
import hellfirepvp.astralsorcery.client.screen.tome.research.TomeResearchSearchRenderer;
import hellfirepvp.astralsorcery.client.screen.tome.research.TomeResearchViewRenderer;
import hellfirepvp.astralsorcery.client.sound.PlayableSoundInstance;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.data.ResearchNodeLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomeResearchScreen
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TomeResearchScreen extends TomeScreen {

    public static final BookmarkProvider BOOKMARK = new BookmarkProvider("research",
            10, () -> Minecraft.getInstance().setScreen(TomeResearchScreen.getOpenTome()), () -> true);

    static TomeScreen OPEN_INSTANCE = null;
    private boolean expectNewInit = false;
    private boolean doViewRefresh = true;

    private SearchInputElement searchInput = null;

    private TomeNavArrowElement searchArrowLeft;
    private TomeNavArrowElement searchArrowRight;

    private TomeResearchViewRenderer researchViewRenderer;
    private TomeResearchSearchRenderer researchSearchRenderer;

    private TomeResearchScreen() {
        super(BOOKMARK.getBookmarkIndex());
    }

    public static TomeScreen getOpenTome() {
        //Force player into welcome node if hasn't learned to click on others yet.
        PlayerProgress prog = ResearchManager.getClientProgress();
        if (!prog.hasLearnedToNavigateTome()) {
            ResearchNode startNode = ResearchNodeLoader.getInstance().getNode(AstralSorcery.key("welcome")).orElse(null);
            if (startNode != null) {
                return TomePagesScreen.fromProgressNode(new TomeResearchScreen(), startNode);
            }
        }

        //Return open pages instance if possible/known
        if (OPEN_INSTANCE != null) {
            return OPEN_INSTANCE;
        }
        return new TomeResearchScreen();
    }

    public static void resetOpenTome() {
        OPEN_INSTANCE = null;
    }

    public void expectNewInit() {
        this.expectNewInit = true;
    }

    public void preventViewRefresh() {
        this.doViewRefresh = false;
    }

    @Override
    public void removeWidget(GuiEventListener listener) {
        super.removeWidget(listener);
    }

    private Optional<SearchInputElement> getSearchInput() {
        return Optional.ofNullable(this.searchInput);
    }

    public ViewType getViewType() {
        if (this.getSearchInput().map(SearchInputElement::getText).map(String::length).orElse(0) >= 3) {
            return ViewType.SEARCH;
        }
        return ViewType.RESEARCH;
    }

    @Override
    public void removed() {
        super.removed();
        this.doViewRefresh = false;
    }

    @Override
    protected void init() {
        super.init();

        this.clearWidgets();
        this.initBookmarks();
        this.initSearchNavArrows();
        this.searchInput = this.addRenderableWidget(new SearchInputElement(this.screenLeft + 300, this.screenTop + 16, this::onSearchInput));

        if (this.expectNewInit) {
            this.expectNewInit = false;
            return; //Don't refresh any views and renders; existing state is (expectedly) still valid
        }

        if (OPEN_INSTANCE == null) OPEN_INSTANCE = this;
        if (this.researchViewRenderer == null) this.researchViewRenderer = new TomeResearchViewRenderer(this);
        if (this.researchSearchRenderer == null) this.researchSearchRenderer = new TomeResearchSearchRenderer(this);

        this.researchViewRenderer.setViewBox(this.getRectangle());
        this.researchSearchRenderer.setViewBox(this.getRectangle());

        if (this.doViewRefresh) {
            this.researchViewRenderer.refreshView();
            this.researchSearchRenderer.refreshView();
        } else {
            this.doViewRefresh = true;
        }
    }

    private void initSearchNavArrows() {
        this.searchArrowLeft = new TomeNavArrowElement(this.screenLeft + (this.screenWidth / 2) - 170, this.screenTop + 230,
                false, () -> this.researchSearchRenderer.decrementPage());
        this.searchArrowRight = new TomeNavArrowElement(this.screenLeft + (this.screenWidth / 2) + 170, this.screenTop + 230,
                true, () -> this.researchSearchRenderer.incrementPage());
        this.searchArrowLeft.visible = false;
        this.searchArrowRight.visible = false;
        this.addRenderableWidget(this.searchArrowLeft);
        this.addRenderableWidget(this.searchArrowRight);
    }

    private void onSearchInput() {
        if (this.getViewType() == ViewType.SEARCH && this.isDragging()) {
            this.stopDragging(-1, -1);
            this.researchViewRenderer.applyMouseMove();
        }
        this.getSearchInput().map(SearchInputElement::getText).ifPresent(text -> {
            this.researchSearchRenderer.updateSearchContents(text);
        });
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        boolean renderNavArrows = this.getViewType() == ViewType.SEARCH;
        this.researchSearchRenderer.setNavArrowVisibility(renderNavArrows, this.searchArrowLeft, this.searchArrowRight);

        TomeResearchPartRenderer partRenderer = this.getViewType().getRenderer(this);
        partRenderer.draw(guiGraphics, () -> {
            super.render(guiGraphics, mouseX, mouseY, partialTick);
        }, mouseX, mouseY, partialTick);
    }

    @Override
    protected void mouseDragTick(double mouseX, double mouseY, double mouseDiffX, double mouseDiffY, double mouseOffsetX, double mouseOffsetY) {
        super.mouseDragTick(mouseX, mouseY, mouseDiffX, mouseDiffY, mouseOffsetX, mouseOffsetY);

        if (this.getViewType() == ViewType.RESEARCH) {
            this.researchViewRenderer.moveMouse((float) mouseDiffX, (float) mouseDiffY);
        }
    }

    @Override
    protected void mouseDragStop(double mouseX, double mouseY, double mouseDiffX, double mouseDiffY) {
        super.mouseDragStop(mouseX, mouseY, mouseDiffX, mouseDiffY);

        if (this.getViewType() == ViewType.RESEARCH) {
            this.researchViewRenderer.applyMouseMove();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button != 0) {
            return false;
        }
        if (this.getViewType().getRenderer(this).mouseClick(mouseX, mouseY)) {
            this.stopDragging(mouseX, mouseY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return true;
        }
        //Only vertical scrolling (for now?)
        return this.getViewType().getRenderer(this).mouseScrolled(mouseX, mouseY, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.getSearchInput().map(input -> input.keyPressed(keyCode, scanCode, modifiers)).orElse(false)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.getSearchInput().map(input -> input.charTyped(codePoint, modifiers)).orElse(false)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    protected boolean shouldRightClickCloseScreen(double mouseX, double mouseY) {
        return true;
    }

    @Override
    protected boolean shouldInventoryKeyCloseScreen() {
        return this.getSearchInput().map(input -> !input.isHoveredOrFocused() && input.getText().isEmpty()).orElse(false);
    }

    public enum ViewType {

        RESEARCH,
        SEARCH;

        public TomeResearchPartRenderer getRenderer(TomeResearchScreen screen) {
            return switch (this) {
                case RESEARCH -> screen.researchViewRenderer;
                case SEARCH -> screen.researchSearchRenderer;
            };
        }
    }
}
