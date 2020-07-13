/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.screen.base.WidthHeightScreen;
import hellfirepvp.astralsorcery.client.screen.journal.bookmark.BookmarkProvider;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenJournal
 * Created by HellFirePvP
 * Date: 03.08.2019 / 16:27
 */
public class ScreenJournal extends WidthHeightScreen {

    public static final int NO_BOOKMARK = -1;
    protected static List<BookmarkProvider> bookmarks = Lists.newArrayList();

    protected final int bookmarkIndex;

    protected Map<Rectangle, BookmarkProvider> drawnBookmarks = Maps.newHashMap();

    protected ScreenJournal(ITextComponent titleIn, int bookmarkIndex) {
        this(titleIn, 270, 420, bookmarkIndex);
    }

    public ScreenJournal(ITextComponent titleIn, int guiHeight, int guiWidth, int bookmarkIndex) {
        super(titleIn, guiHeight, guiWidth);
        this.bookmarkIndex = bookmarkIndex;
    }

    public static boolean addBookmark(BookmarkProvider bookmarkProvider) {
        int index = bookmarkProvider.getIndex();
        if (MiscUtils.contains(bookmarks, bm -> bm.getIndex() == index)) {
            return false;
        }
        bookmarks.add(bookmarkProvider);
        return true;
    }

    protected void drawDefault(AbstractRenderableTexture texture, int mouseX, int mouseY) {
        this.changeZLevel(100);
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        drawWHRect(texture);
        RenderSystem.disableBlend();

        drawBookmarks(mouseX, mouseY);
        this.changeZLevel(-100);
    }

    private void drawBookmarks(int mouseX, int mouseY) {
        drawnBookmarks.clear();

        float bookmarkWidth  = 67;
        float bookmarkHeight = 15;
        float bookmarkGap    = 18;

        float offsetX = guiLeft + guiWidth - 17.25F;
        float offsetY = guiTop  + 20;

        bookmarks.sort(Comparator.comparing(BookmarkProvider::getIndex));

        for (BookmarkProvider bookmarkProvider : bookmarks) {
            if (bookmarkProvider.canSee()) {
                Rectangle r = drawBookmark(
                        offsetX, offsetY,
                        bookmarkWidth, bookmarkHeight,
                        bookmarkWidth + (bookmarkIndex == bookmarkProvider.getIndex() ? 0 : 5),
                        this.getGuiZLevel(),
                        bookmarkProvider.getUnlocalizedName(), 0xDDDDDDDD, mouseX, mouseY,
                        bookmarkProvider.getTextureBookmark(), bookmarkProvider.getTextureBookmarkStretched());
                drawnBookmarks.put(r, bookmarkProvider);
                offsetY += bookmarkGap;
            }
        }
    }

    private Rectangle drawBookmark(float offsetX, float offsetY, float width, float height, float mouseOverWidth,
                                   float zLevel, String title, int titleRGBColor, int mouseX, int mouseY,
                                   AbstractRenderableTexture texture, AbstractRenderableTexture textureStretched) {
        texture.bindTexture();

        Rectangle r = new Rectangle(MathHelper.floor(offsetX), MathHelper.floor(offsetY), MathHelper.floor(width), MathHelper.floor(height));
        if (r.contains(mouseX, mouseY)) {
            if (mouseOverWidth > width) {
                textureStretched.bindTexture();
            }
            width = mouseOverWidth;
            r = new Rectangle(MathHelper.floor(offsetX), MathHelper.floor(offsetY), MathHelper.floor(width), MathHelper.floor(height));
        }

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        float actualWidth = width;
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX, buf -> {
            RenderingGuiUtils.rect(buf, offsetX, offsetY, zLevel, actualWidth, height).draw();
        });
        RenderSystem.disableBlend();

        RenderSystem.pushMatrix();
        RenderSystem.translated(offsetX + 2, offsetY + 4, zLevel + 50);
        RenderSystem.scaled(0.7, 0.7, 0.7);
        RenderingDrawUtils.renderStringAtCurrentPos(null, I18n.format(title), titleRGBColor);
        RenderSystem.popMatrix();
        return r;
    }

    protected boolean handleBookmarkClick(double mouseX, double mouseY) {
        return handleJournalNavigationBookmarkClick(mouseX, mouseY);
    }

    private boolean handleJournalNavigationBookmarkClick(double mouseX, double mouseY) {
        for (Rectangle bookmarkRectangle : drawnBookmarks.keySet()) {
            BookmarkProvider provider = drawnBookmarks.get(bookmarkRectangle);
            if (bookmarkIndex != provider.getIndex() && bookmarkRectangle.contains(mouseX, mouseY)) {
                ScreenJournalProgression.resetJournal();
                Minecraft.getInstance().displayGuiScreen(provider.getGuiScreen());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
