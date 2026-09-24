/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BookmarkProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class BookmarkProvider implements Comparable<BookmarkProvider> {

    private final MutableComponent name;
    private final int bookmarkIndex;
    private final Runnable onBookmarkSelected;
    private final Supplier<Boolean> canSeeTest;

    public BookmarkProvider(String name, int bookmarkIndex, Runnable onBookmarkSelected, Supplier<Boolean> canSeeTest) {
        this(Component.translatable(Util.makeDescriptionId("tome.bookmark", AstralSorcery.key(name))), bookmarkIndex, onBookmarkSelected, canSeeTest);
    }

    public BookmarkProvider(MutableComponent name, int bookmarkIndex, Runnable onBookmarkSelected, Supplier<Boolean> canSeeTest) {
        this.name = name;
        this.bookmarkIndex = bookmarkIndex;
        this.onBookmarkSelected = onBookmarkSelected;
        this.canSeeTest = canSeeTest;
    }

    public MutableComponent getName() {
        return this.name;
    }

    public int getBookmarkIndex() {
        return this.bookmarkIndex;
    }

    public boolean canSee() {
        return this.canSeeTest.get();
    }

    public void getOnBookmarkSelected() {
        this.onBookmarkSelected.run();
    }

    public AbstractRenderTexture getBookmarkTexture() {
        return TexturesAS.SCREEN_TOME_BOOKMARK;
    }

    @Override
    public int compareTo(@NotNull BookmarkProvider o) {
        return this.bookmarkIndex - o.bookmarkIndex;
    }
}
