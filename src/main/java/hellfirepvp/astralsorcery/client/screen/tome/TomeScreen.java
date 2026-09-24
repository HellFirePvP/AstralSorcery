/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome;

import hellfirepvp.astralsorcery.client.screen.base.FixedSizeScreen;
import hellfirepvp.astralsorcery.client.screen.element.TomeBookmarkElement;
import hellfirepvp.astralsorcery.client.sound.PlayableSoundInstance;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomeScreen
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class TomeScreen extends FixedSizeScreen {

    public static final int NO_BOOKMARK = -1;
    private static final SortedSet<BookmarkProvider> BOOKMARKS = new TreeSet<>();

    private final int thisBookmarkIndex;

    protected TomeScreen(int thisBookmarkIndex) {
        super(GameNarrator.NO_TITLE, 270, 420);
        this.thisBookmarkIndex = thisBookmarkIndex;
    }

    @Override
    public <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget) {
        return super.addRenderableWidget(widget);
    }

    protected void initBookmarks() {
        int bookmarkGap = 18;

        int offsetX = this.screenLeft + this.screenWidth - 16;// - 17.25F;
        int offsetY = this.screenTop  + 20;

        for (BookmarkProvider provider : BOOKMARKS) {
            if (!provider.canSee()) continue;
            boolean isSelected = this.thisBookmarkIndex != NO_BOOKMARK && this.thisBookmarkIndex == provider.getBookmarkIndex();
            this.addRenderableWidget(new TomeBookmarkElement(offsetX, offsetY, isSelected, provider));
            offsetY += bookmarkGap;
        }
    }

    public static void addBookmark(BookmarkProvider bookmarkProvider) {
        BOOKMARKS.add(bookmarkProvider);
    }

    public int getBookmarkIndex() {
        return thisBookmarkIndex;
    }

    public void doBookmarkClick(BookmarkProvider provider) {
        if (!provider.canSee()) return;
        PlayableSoundInstance.of(SoundsAS.SCREEN_TOME_PAGE).forUI().play();
        provider.getOnBookmarkSelected();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        PlayableSoundInstance.of(SoundsAS.SCREEN_TOME_CLOSE).forUI().play();
    }
}
