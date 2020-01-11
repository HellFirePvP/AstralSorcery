/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.bookmark;

import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BookmarkProvider
 * Created by HellFirePvP
 * Date: 03.08.2019 / 16:29
 */
@OnlyIn(Dist.CLIENT)
public class BookmarkProvider {

    private final Supplier<Screen> provider;
    private final int index;
    private final String unlocName;
    private Supplier<Boolean> canSeeTest;

    public BookmarkProvider(String unlocName, int bookmarkIndex,
                            Supplier<Screen> guiProvider,
                            Supplier<Boolean> canSeeTest) {
        this.unlocName = unlocName;
        this.index = bookmarkIndex;
        this.provider = guiProvider;
        this.canSeeTest = canSeeTest;
    }

    public Screen getGuiScreen() {
        return provider.get();
    }

    public boolean canSee() {
        return canSeeTest.get();
    }

    public int getIndex() {
        return index;
    }

    public String getUnlocalizedName() {
        return unlocName;
    }

    public AbstractRenderableTexture getTextureBookmark() {
        return TexturesAS.TEX_GUI_BOOKMARK;
    }

    public AbstractRenderableTexture getTextureBookmarkStretched() {
        return TexturesAS.TEX_GUI_BOOKMARK_STRETCHED;
    }

}
