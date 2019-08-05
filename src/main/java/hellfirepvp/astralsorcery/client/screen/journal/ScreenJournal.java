/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.screen.base.WidthHeightScreen;
import hellfirepvp.astralsorcery.client.screen.journal.bookmark.BookmarkProvider;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
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
        this.blitOffset += 100;

        drawWHRect(texture);
        drawBookmarks(mouseX, mouseY);

        this.blitOffset -= 100;
    }

    //TODO knowledge fragments
    //private void resolveFragments() {
    //    KnowledgeFragmentData data = PersistentDataManager.INSTANCE.getData(PersistentDataManager.PersistentKey.KNOWLEDGE_FRAGMENTS);
    //    this.fragmentList = data.getFragmentsFor(this);
    //}

    private void drawBookmarks(int mouseX, int mouseY) {
        //if (fragmentList == null) { TODO knowledge fragments
        //    resolveFragments();
        //}
        drawnBookmarks.clear();

        GlStateManager.pushMatrix();

        double bookmarkWidth =  67;
        double bookmarkHeight = 15;
        double bookmarkGap = 18;

        double knBookmarkWidth =  83;

        double offsetX = guiLeft + guiWidth - 17.25;
        double offsetY = guiTop  + 20;

        bookmarks.sort(Comparator.comparing(BookmarkProvider::getIndex));

        for (BookmarkProvider bookmarkProvider : bookmarks) {
            if (bookmarkProvider.canSee()) {
                Rectangle r = drawBookmark(
                        offsetX, offsetY,
                        bookmarkWidth, bookmarkHeight,
                        bookmarkWidth + (bookmarkIndex == bookmarkProvider.getIndex() ? 0 : 5),
                        this.blitOffset,
                        bookmarkProvider.getUnlocalizedName(), 0xDDDDDDDD, mouseX, mouseY,
                        bookmarkProvider.getTextureBookmark(), bookmarkProvider.getTextureBookmarkStretched());
                drawnBookmarks.put(r, bookmarkProvider);
                offsetY += bookmarkGap;
            }
        }

        offsetY += bookmarkGap / 2;

        /*TODO knowledge fragments
        this.pageFragments.clear();
        for (KnowledgeFragment frag : this.fragmentList) {
            if (frag.isFullyPresent()) {
                offsetY += bookmarkGap;
                Rectangle rctFragment = drawBookmark(
                        offsetX, offsetY,
                        knBookmarkWidth, bookmarkHeight,

                        TODO check this, it doesn't seem right == 0
                        knBookmarkWidth + (bookmarkIndex == 0 ? 0 : 5),

                        zLevel,
                        frag.getUnlocalizedBookmark(), 0xDDDDDDDD,
                        mousePoint, textureKnBookmark, textureKnBookmarkStr);
                this.pageFragments.put(rctFragment, frag);
            }
        }
        */

        GlStateManager.popMatrix();
    }

    private Rectangle drawBookmark(double offsetX, double offsetY, double width, double height, double mouseOverWidth,
                                   float zLevel, String title, int titleRGBColor, int mouseX, int mouseY,
                                   AbstractRenderableTexture texture, AbstractRenderableTexture textureStretched) {
        GlStateManager.pushMatrix();
        GlStateManager.color4f(1F, 1F, 1F, 1F);
        texture.bindTexture();

        Rectangle r = new Rectangle(MathHelper.floor(offsetX), MathHelper.floor(offsetY), MathHelper.floor(width), MathHelper.floor(height));
        if (r.contains(mouseX, mouseY)) {
            if(mouseOverWidth > width) {
                textureStretched.bindTexture();
            }
            width = mouseOverWidth;
            r = new Rectangle(MathHelper.floor(offsetX), MathHelper.floor(offsetY), MathHelper.floor(width), MathHelper.floor(height));
        }

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, zLevel).tex(0, 1).endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel).tex(1, 1).endVertex();
        vb.pos(offsetX + width, offsetY,          zLevel).tex(1, 0).endVertex();
        vb.pos(offsetX,         offsetY,          zLevel).tex(0, 0).endVertex();
        tes.draw();

        GlStateManager.pushMatrix();
        GlStateManager.translated(offsetX + 2, offsetY + 4, zLevel + 50);
        GlStateManager.scaled(0.7, 0.7, 0.7);
        RenderingDrawUtils.renderStringAtCurrentPos(null, I18n.format(title), titleRGBColor);
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();

        return r;
    }

    protected boolean handleBookmarkClick(double mouseX, double mouseY) {
        return handleJournalNavigationBookmarkClick(mouseX, mouseY);// || handleFragmentClick(mouseX, mouseY); TODO knowledge fragments
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

    //TODO knowledge fragments
    //private boolean handleFragmentClick(Point mouse) {
    //    for (Rectangle r : this.pageFragments.keySet()) {
    //        if (r.contains(mouse)) {
    //            if (this instanceof GuiJournalProgression) {
    //                ((GuiJournalProgression) this).expectReinit = true;
    //            }
    //            KnowledgeFragment frag = this.pageFragments.get(r);
    //            Minecraft.getMinecraft().displayGuiScreen(new GuiJournalOverlayKnowledge(this, frag));
    //            SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
    //            return true;
    //        }
    //    }
    //    return false;
    //}

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
