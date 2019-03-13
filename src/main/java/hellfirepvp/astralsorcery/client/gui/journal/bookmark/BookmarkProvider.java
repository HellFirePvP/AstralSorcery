/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal.bookmark;

import hellfirepvp.astralsorcery.client.util.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.common.util.Provider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BookmarkProvider
 * Created by HellFirePvP
 * Date: 26.11.2018 / 01:07
 */
@SideOnly(Side.CLIENT)
public class BookmarkProvider {

    private final Provider<GuiScreen> provider;
    private final int index;
    private final String unlocName;
    private Provider<Boolean> canSeeTest;

    public BookmarkProvider(String unlocName, int bookmarkIndex,
                            Provider<GuiScreen> guiProvider,
                            Provider<Boolean> canSeeTest) {
        this.unlocName = unlocName;
        this.index = bookmarkIndex;
        this.provider = guiProvider;
        this.canSeeTest = canSeeTest;
    }

    public GuiScreen getGuiScreen() {
        return provider.provide();
    }

    public boolean canSee() {
        return canSeeTest.provide();
    }

    public int getIndex() {
        return index;
    }

    public String getUnlocalizedName() {
        return unlocName;
    }

    public AbstractRenderableTexture getTextureBookmark() {
        return AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijbookmark");
    }

    public AbstractRenderableTexture getTextureBookmarkStretched() {
        return AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijbookmarkstretched");
    }

}
