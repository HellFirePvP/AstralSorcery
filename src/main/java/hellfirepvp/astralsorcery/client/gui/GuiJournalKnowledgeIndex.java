/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import com.google.common.collect.BoundType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.data.KnowledgeFragmentData;
import hellfirepvp.astralsorcery.client.data.PersistentDataManager;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.gui.journal.overlay.GuiJournalOverlayKnowledge;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.GuiTextEntry;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragmentManager;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.io.IOException;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalKnowledgeIndex
 * Created by HellFirePvP
 * Date: 04.10.2018 / 21:23
 */
public class GuiJournalKnowledgeIndex extends GuiScreenJournal {

    private static final AbstractRenderableTexture texArrow   = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijarrow");
    private static final AbstractRenderableTexture textureSearchTextBG = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijtextarea");

    public static final int DEFAULT_WIDTH = 170;

    private static final int entriesLeft = 15;
    private static final int entriesRight = 14;
    private static final Random rand = new Random();
    
    private static Rectangle rectSearchTextEntry = new Rectangle(300, 20, 88, 15);

    private Rectangle rectNext, rectPrev;
    private KnowledgeFragment lastRenderHover = null;

    private final List<KnowledgeFragment> allFragments;
    private List<KnowledgeFragment> searchResult;

    private int doublePageID = 0;
    private Map<Integer, List<KnowledgeFragment>> indexedPages = Maps.newHashMap();

    private GuiTextEntry searchTextEntry = new GuiTextEntry();

    public GuiJournalKnowledgeIndex() {
        super(3);
        this.closeWithInventoryKey = false;

        KnowledgeFragmentData dat = PersistentDataManager.INSTANCE.getData(PersistentDataManager.PersistentKey.KNOWLEDGE_FRAGMENTS);
        List<KnowledgeFragment> known = Lists.newArrayList(dat.getAllFragments());
        known.removeIf(f -> !f.isFullyPresent());
        known.sort(Comparator.comparing(KnowledgeFragment::getLocalizedIndexName));
        this.allFragments = known;
        this.searchResult = Lists.newArrayList(this.allFragments);
        this.searchResult.sort(Comparator.comparing(KnowledgeFragment::getLocalizedIndexName));

        this.searchTextEntry.setChangeCallback(this::updateSearchResult);

        this.updatePages();
    }

    private void updateSearchResult() {
        this.searchResult = Lists.newArrayList(this.allFragments);

        String res = this.searchTextEntry.getText().toLowerCase();
        this.searchResult.removeIf(k -> !k.getLocalizedIndexName().toLowerCase().contains(res));
        this.searchResult.sort(Comparator.comparing(KnowledgeFragment::getLocalizedIndexName));

        this.updatePages();
    }

    private void updatePages() {
        this.indexedPages.clear();

        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        int index = 0;
        NavigableMap<Integer, KnowledgeFragment> results = Maps.newTreeMap();
        for (KnowledgeFragment frag : this.searchResult) {
            int lines = fr.listFormattedStringToWidth(frag.getLocalizedIndexName(), DEFAULT_WIDTH).size();
            for (int i = 0; i < lines; i++) {
                results.put(index, frag);
                index++;
            }
        }

        int pageIndex = 0;
        List<KnowledgeFragment> currentDoublePage = new LinkedList<>();

        do {
            int startIndex = pageIndex * (entriesLeft + entriesRight);
            int endIndex = Math.min(startIndex + (entriesLeft + entriesRight), results.size());
            Map<Integer, KnowledgeFragment> pageMap = Maps.subMap(results,
                    Range.range(startIndex, BoundType.CLOSED, endIndex, BoundType.OPEN));
            for (KnowledgeFragment frag : pageMap.values()) {
                if (!currentDoublePage.contains(frag)) {
                    currentDoublePage.add(frag);
                }
            }
            this.indexedPages.put(pageIndex, currentDoublePage);
            pageIndex++;
            currentDoublePage = new LinkedList<>();
        } while (results.containsKey(pageIndex * (entriesLeft + entriesRight)));

        while (this.doublePageID > 0 && this.doublePageID >= this.indexedPages.size()) {
            this.doublePageID--;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Point mouse = new Point(mouseX, mouseY);
        lastRenderHover = null;

        drawDefault(textureResBlank, mouse);

        drawFragmentIndices(mouseX, mouseY, partialTicks);
        drawSearchBox();

        zLevel += 150;
        drawNavArrows(mouseX, mouseY, partialTicks);
        zLevel -= 150;
    }

    private void drawFragmentIndices(int mouseX, int mouseY, float pTicks) {
        GL11.glColor4f(0.86F, 0.86F, 0.86F, 1F);
        GlStateManager.color(0.86F, 0.86F, 0.86F, 1F);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();
        Blending.DEFAULT.applyStateManager();
        TextureHelper.setActiveTextureToAtlasSprite();

        int offsetX = guiLeft + 35;
        int offsetY = guiTop + 26;

        List<KnowledgeFragment> pageFragments = this.indexedPages.get(this.doublePageID);
        if (pageFragments != null) {

            int start = 0;
            int idx = 0;
            int drawIndex = 0;

            boolean drewHover = false; //To avoid double-handling range checks
            double effectPart = (Math.sin(Math.toRadians(((ClientScheduler.getClientTick()) * 5D) % 360D)) + 1D) / 2D;
            int br = Math.round((0.45F + 0.1F * ((float) effectPart)) * 255F);
            int c = Math.round((0.7F + 0.2F * ((float) effectPart)) * 255F);
            Color boxColor = new Color(c, c, c, br);

            for (;idx < Math.min(pageFragments.size(), start + entriesLeft) && drawIndex < entriesLeft; idx++) {
                int step = 12;

                int y = offsetY + (drawIndex * step);
                int iOffsetY = y;

                KnowledgeFragment frag = pageFragments.get(idx);
                List<String> lines = fontRenderer.listFormattedStringToWidth(frag.getLocalizedIndexName(), DEFAULT_WIDTH);
                drawIndex += lines.size();
                int maxLength = 0;

                for (String line : lines) {
                    int length = fontRenderer.drawString(line, offsetX, iOffsetY, 0x00D0D0D0, false) - offsetX;
                    if (length > maxLength) {
                        maxLength = length;
                    }
                    iOffsetY += step;
                }

                Rectangle rctString = new Rectangle(offsetX - 2, y - 2, maxLength + 4, iOffsetY - y);
                if (!drewHover && rctString.contains(mouseX, mouseY)) {
                    drawRect(rctString.x, rctString.y,
                            rctString.x + rctString.width, rctString.y + rctString.height,
                            boxColor.getRGB());
                    lastRenderHover = frag;
                    drewHover = true;
                }
            }

            offsetX = guiLeft + 225;
            offsetY = guiTop + 39;
            start += idx;
            drawIndex = 0;

            for (;idx < Math.min(pageFragments.size(), start + entriesRight); idx++) {
                int step = 12;

                int y = offsetY + (drawIndex * step);
                int iOffsetY = y;

                KnowledgeFragment frag = pageFragments.get(idx);
                List<String> lines = fontRenderer.listFormattedStringToWidth(frag.getLocalizedIndexName(), DEFAULT_WIDTH);
                drawIndex += lines.size();
                int maxLength = 0;

                for (String line : lines) {
                    int length = fontRenderer.drawString(line, offsetX, iOffsetY, 0x00D0D0D0, false) - offsetX;
                    if (length > maxLength) {
                        maxLength = length;
                    }
                    iOffsetY += step;
                }

                Rectangle rctString = new Rectangle(offsetX - 2, y - 2, maxLength + 4, iOffsetY - y);
                if (!drewHover && rctString.contains(mouseX, mouseY)) {
                    drawRect(rctString.x, rctString.y,
                            rctString.x + rctString.width, rctString.y + rctString.height,
                            boxColor.getRGB());
                    lastRenderHover = frag;
                    drewHover = true;
                }
            }
        }


        GlStateManager.enableDepth();
        GlStateManager.color(1F, 1F, 1F, 1F);

        TextureHelper.refreshTextureBindState();
    }

    @Override
    protected boolean handleRightClickClose(int mouseX, int mouseY) {
        if (rectSearchTextEntry.contains(mouseX - guiLeft, mouseY - guiTop)) {
            searchTextEntry.setText("");
            return true;
        }
        return false;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if(mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);

        if (handleBookmarkClick(p)) {
            return;
        }

        if(rectPrev != null && rectPrev.contains(p)) {
            if(doublePageID >= 1) {
                this.doublePageID--;
            }
            SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
            return;
        }
        if(rectNext != null && rectNext.contains(p)) {
            if(doublePageID <= this.indexedPages.size() - 1) {
                this.doublePageID++;
            }
            SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
            return;
        }

        if (lastRenderHover != null) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiJournalOverlayKnowledge(this, lastRenderHover));
            SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
            return;
        }
    }

    private void drawSearchBox() {
        GlStateManager.color(1F, 1F, 1F, 1F);

        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft + 300, guiTop + 20, 0);
        textureSearchTextBG.bindTexture();
        drawTexturedRectAtCurrentPos(88.5, 15);

        String text = this.searchTextEntry.getText();

        int length = fontRenderer.getStringWidth(text);
        boolean addDots = length > 75;
        while (length > 75) {
            text = text.substring(1, text.length());
            length = fontRenderer.getStringWidth("..." + text);
        }
        if (addDots) {
            text = "..." + text;
        }

        if ((ClientScheduler.getClientTick() % 20) > 10) {
            text += "_";
        }

        GlStateManager.translate(4, 4, 0);
        fontRenderer.drawString(text, 0, 0, new Color(0xCCCCCC).getRGB(), false);

        GlStateManager.color(1F, 1F, 1F, 1F);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        TextureHelper.refreshTextureBindState();
        GlStateManager.popMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
    }

    private void drawNavArrows(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableDepth();
        Point mouse = new Point(mouseX, mouseY);
        rectNext = null;
        rectPrev = null;
        if(doublePageID - 1 >= 0) {
            int width = 30;
            int height = 15;
            rectPrev = new Rectangle(guiLeft + 25, guiTop + 220, width, height);
            GlStateManager.pushMatrix();
            GlStateManager.translate(rectPrev.getX() + (width / 2), rectPrev.getY() + (height / 2), 0);
            float uFrom = 0F, vFrom = 0.5F;
            if(rectPrev.contains(mouse)) {
                uFrom = 0.5F;
                GlStateManager.scale(1.1, 1.1, 1.1);
            } else {
                double t = ClientScheduler.getClientTick() + partialTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
                GlStateManager.scale(sin, sin, sin);
            }
            GlStateManager.color(1F, 1F, 1F, 0.8F);
            GlStateManager.translate(-(width / 2), -(height / 2), 0);
            texArrow.bindTexture();
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
            GlStateManager.popMatrix();
        }
        if(doublePageID + 1 < this.indexedPages.size()) {
            int width = 30;
            int height = 15;
            rectNext = new Rectangle(guiLeft + 367, guiTop + 220, width, height);
            GlStateManager.pushMatrix();
            GlStateManager.translate(rectNext.getX() + (width / 2), rectNext.getY() + (height / 2), 0);
            float uFrom = 0F, vFrom = 0F;
            if(rectNext.contains(mouse)) {
                uFrom = 0.5F;
                GlStateManager.scale(1.1, 1.1, 1.1);
            } else {
                double t = ClientScheduler.getClientTick() + partialTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
                GlStateManager.scale(sin, sin, sin);
            }
            GlStateManager.color(1F, 1F, 1F, 0.8F);
            GlStateManager.translate(-(width / 2), -(height / 2), 0);
            texArrow.bindTexture();
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
            GlStateManager.popMatrix();
        }
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableDepth();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (keyCode != Keyboard.KEY_ESCAPE) {
            searchTextEntry.textboxKeyTyped(typedChar, keyCode);
        }
    }

}
