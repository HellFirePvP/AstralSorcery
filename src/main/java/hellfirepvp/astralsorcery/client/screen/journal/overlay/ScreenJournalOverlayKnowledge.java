/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.overlay;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournal;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenJournalOverlayKnowledge
 * Created by HellFirePvP
 * Date: 04.08.2019 / 09:19
 */
public class ScreenJournalOverlayKnowledge extends ScreenJournalOverlay {

    private static final int HEADER_WIDTH = 190;
    private static final int DEFAULT_WIDTH = 175;

    private final KnowledgeFragment fragment;
    private final List<String> lines = new LinkedList<>();

    public ScreenJournalOverlayKnowledge(KnowledgeFragment fragment, ScreenJournal origin) {
        super(new TranslationTextComponent(fragment.getUnlocalizedName()), origin);
        this.fragment = fragment;
    }

    @Override
    protected void init() {
        super.init();

        String text = this.fragment.getLocalizedPage().getFormattedText();
        for (String segment : text.split("<NL>")) {
            lines.addAll(font.listFormattedStringToWidth(segment, DEFAULT_WIDTH));
            lines.add("");
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        GlStateManager.disableDepthTest();
        Blending.DEFAULT.applyStateManager();

        int width = 275;
        int height = 344;

        TexturesAS.TEX_GUI_KNOWLEDGE_FRAGMENT_BLANK.bindTexture();
        RenderingGuiUtils.drawTexturedRect(guiLeft + guiWidth / 2 - width / 2, guiTop + guiHeight / 2 - height / 2, this.blitOffset, width, height, TexturesAS.TEX_GUI_KNOWLEDGE_FRAGMENT_BLANK);

        drawHeader();
        drawPageText();

        GlStateManager.enableDepthTest();
    }

    private void drawPageText() {
        int offsetY = guiTop + 40;
        int offsetX = guiLeft + guiWidth / 2 - DEFAULT_WIDTH / 2;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            RenderingDrawUtils.renderStringAtPos(offsetX, offsetY + (i * 10), font, line, 0xEE333333, false);
        }
    }

    private void drawHeader() {
        String locTitle = this.fragment.getLocalizedIndexName();
        List<String> split = font.listFormattedStringToWidth(locTitle, MathHelper.floor(HEADER_WIDTH / 1.4));
        int step = 14;

        int offsetTop = guiTop + 15 - (split.size() * step) / 2;

        GlStateManager.pushMatrix();
        GlStateManager.translated(0, offsetTop, 0);
        for (int i = 0; i < split.size(); i++) {
            String s = split.get(i);

            double offsetLeft = width / 2 - (font.getStringWidth(s) * 1.4) / 2;
            GlStateManager.pushMatrix();
            GlStateManager.translated(offsetLeft, i * step, 0);
            GlStateManager.scaled(1.4, 1.4, 1.4);
            RenderingDrawUtils.renderStringAtCurrentPos(font, s, 0xEE333333);
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }
}
