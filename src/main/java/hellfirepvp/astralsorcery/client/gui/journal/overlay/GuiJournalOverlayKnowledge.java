/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal.overlay;

import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournalOverlay;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalOverlayKnowledge
 * Created by HellFirePvP
 * Date: 26.09.2018 / 12:51
 */
public class GuiJournalOverlayKnowledge extends GuiScreenJournalOverlay {

    public static final BindableResource textureKnowledgeOverlay = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guicontippaper_blank");
    private static final int DEFAULT_WIDTH = 175;

    private final KnowledgeFragment knowledgeFragment;
    private final List<String> lines = new LinkedList<>();

    public GuiJournalOverlayKnowledge(GuiScreenJournal origin, KnowledgeFragment display) {
        super(origin);
        this.knowledgeFragment = display;
    }

    @Override
    public void initGui() {
        super.initGui();

        String text = this.knowledgeFragment.getLocalizedPage();
        for (String segment : text.split("<NL>")) {
            lines.addAll(fontRenderer.listFormattedStringToWidth(segment, DEFAULT_WIDTH));
            lines.add("");
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();

        int width = 275;
        int height = 344;

        textureKnowledgeOverlay.bindTexture();
        drawTexturedRect(guiLeft + guiWidth / 2 - width / 2, guiTop + guiHeight / 2 - height / 2, width, height, textureKnowledgeOverlay);

        drawHeader();
        drawPageText();

        GlStateManager.enableDepth();
        TextureHelper.refreshTextureBindState();
    }

    private void drawPageText() {
        GlStateManager.color(1, 1, 1, 1);
        int offsetY = guiTop + 35;
        int offsetX = guiLeft + guiWidth / 2 - DEFAULT_WIDTH / 2;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            fontRenderer.drawString(line, offsetX, offsetY + (i * 10), 0xEE333333, false);
        }
        GlStateManager.color(1, 1, 1, 1);
    }

    private void drawHeader() {
        String locTitle = this.knowledgeFragment.getLocalizedIndexName();
        TextureHelper.refreshTextureBindState();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        double length = fr.getStringWidth(locTitle) * 1.8;
        double offsetLeft = width / 2 - length / 2;
        int offsetTop = guiTop + 10;
        GlStateManager.pushMatrix();
        GlStateManager.translate(offsetLeft + 2, offsetTop, 0);
        GlStateManager.scale(1.8, 1.8, 1.8);
        fr.drawString(locTitle, 0, 0, 0xEE333333, false);
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1, 1);
        GL11.glColor4f(1, 1, 1, 1);
    }

    public KnowledgeFragment getKnowledgeFragment() {
        return knowledgeFragment;
    }

}
