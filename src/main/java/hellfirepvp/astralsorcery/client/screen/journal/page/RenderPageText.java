/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.page;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.common.data.journal.JournalPage;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderPageText
 * Created by HellFirePvP
 * Date: 10.10.2019 / 17:31
 */
public class RenderPageText extends RenderablePage {

    private final FontRenderer fontRenderer;
    private List<String> localizedText;

    public RenderPageText(String unlocalized) {
        this(RenderablePage.getFontRenderer(), unlocalized);
    }

    public RenderPageText(FontRenderer fontRenderer, String unlocalized) {
        super(null, -1);
        this.fontRenderer = fontRenderer;
        this.localizedText = buildLines(unlocalized);
    }

    private List<String> buildLines(String unlocText) {
        String text = I18n.format(unlocText);
        List<String> lines = new LinkedList<>();
        for (String segment : text.split("<NL>")) {
            lines.addAll(fontRenderer.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
            lines.add("");
        }
        return lines;
    }

    @Override
    public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
        int iX = (int) offsetX;
        int iY = (int) offsetY;

        GlStateManager.color4f(0.9F, 0.9F, 0.9F, 1F);
        GlStateManager.disableDepthTest();

        for (int i = 0; i < this.localizedText.size(); i++) {
            String line = this.localizedText.get(i);
            RenderingDrawUtils.renderStringAtPos(iX, iY + (i * 10), this.fontRenderer, line, 0x00DDDDDD, false);
        }

        GlStateManager.enableDepthTest();
        GlStateManager.color4f(1F, 1F, 1F, 1F);
    }
}
