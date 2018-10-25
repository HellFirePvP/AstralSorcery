/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.data.KnowledgeFragmentData;
import hellfirepvp.astralsorcery.client.data.PersistentDataManager;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournalOverlay;
import hellfirepvp.astralsorcery.client.gui.journal.overlay.GuiJournalOverlayKnowledge;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.GuiTextEntry;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragmentManager;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.util.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.io.IOException;
import java.util.Random;

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

    private static final int entriesLeft = 16;
    private static final int entriesRight = 14;
    private static final Random rand = new Random();

    private Rectangle rectNext, rectPrev;
    private KnowledgeFragment lastRenderHover = null;

    private final List<KnowledgeFragment> allFragments;
    private List<KnowledgeFragment> searchResult;

    private int doublePageID = 0;
    private int doublePageCount = 0;

    private GuiTextEntry searchTextEntry = new GuiTextEntry();

    public GuiJournalKnowledgeIndex() {
        super(3);
        this.closeWithInventoryKey = false;

        KnowledgeFragmentData dat = PersistentDataManager.INSTANCE.getData(PersistentDataManager.PersistentKey.KNOWLEDGE_FRAGMENTS);
        List<KnowledgeFragment> known = Lists.newArrayList(dat.getAllFragments());
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
        int results = this.searchResult.size();
        this.doublePageCount = (results / (entriesLeft + entriesRight));
        if (results > 0 && results % (entriesLeft + entriesRight) == 0) {
            this.doublePageCount--;
        }
        while (this.doublePageID > 0 && this.doublePageID >= this.doublePageCount) {
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
        int offsetY = guiTop + 20;

        int start = this.doublePageID * (entriesLeft + entriesRight);
        int index = start;

        boolean drewHover = false; //To avoid double-handling range checks
        double effectPart = (Math.sin(Math.toRadians(((ClientScheduler.getClientTick()) * 5D) % 360D)) + 1D) / 2D;
        int br = Math.round((0.45F + 0.1F * ((float) effectPart)) * 255F);
        int c = Math.round((0.7F + 0.2F * ((float) effectPart)) * 255F);
        Color boxColor = new Color(c, c, c, br);

        for (;index < Math.min(this.searchResult.size(), start + entriesLeft); index++) {
            int offset = index - start;
            int y = offsetY + (offset * 13);
            KnowledgeFragment frag = this.searchResult.get(index);
            String line = frag.getLocalizedIndexName();
            int length = fontRenderer.drawString(line, offsetX, y, 0x00D0D0D0, false);

            Rectangle rctString = new Rectangle(offsetX - 2, y - 2, length + 4, 7 + 4);
            if (!drewHover && rctString.contains(mouseX, mouseY)) {
                drawRect(rctString.x, rctString.y,
                        rctString.x + rctString.width, rctString.y + rctString.height,
                        boxColor.getRGB());
                lastRenderHover = frag;
                drewHover = true;
            }
        }

        offsetX = guiLeft + 225;
        offsetY = guiTop + 46;
        start += entriesLeft;
        index = start;

        for (;index < Math.min(this.searchResult.size(), start + entriesRight); index++) {
            int offset = index - start;
            int y = offsetY + (offset * 13);
            KnowledgeFragment frag = this.searchResult.get(index);
            String line = frag.getLocalizedIndexName();
            int length = fontRenderer.drawString(line, offsetX, y, 0x00D0D0D0, false);

            Rectangle rctString = new Rectangle(offsetX - 2, y - 2, length + 4, 7 + 4);
            if (!drewHover && rctString.contains(mouseX, mouseY)) {
                drawRect(rctString.x, rctString.y,
                        rctString.x + rctString.width, rctString.y + rctString.height,
                        boxColor.getRGB());
                lastRenderHover = frag;
                drewHover = true;
            }
        }

        GlStateManager.enableDepth();
        GlStateManager.color(1F, 1F, 1F, 1F);

        TextureHelper.refreshTextureBindState();
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
            if(doublePageID <= doublePageCount - 1) {
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
        if(doublePageID + 1 <= doublePageCount) {
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
