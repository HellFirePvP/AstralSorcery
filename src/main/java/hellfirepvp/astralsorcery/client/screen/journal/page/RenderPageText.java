/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.page;

import com.mojang.blaze3d.matrix.MatrixStack;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.common.data.journal.JournalPage;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.*;

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
    private final List<IReorderingProcessor> localizedText;

    public RenderPageText(String unlocalized) {
        this(RenderablePage.getFontRenderer(), unlocalized);
    }

    public RenderPageText(FontRenderer fontRenderer, String unlocalized) {
        super(null, -1);
        this.fontRenderer = fontRenderer;
        this.localizedText = buildLines(unlocalized);
    }

    private List<IReorderingProcessor> buildLines(String unlocText) {
        String text = LanguageMap.getInstance().func_230503_a_(unlocText);
        List<IReorderingProcessor> lines = new LinkedList<>();
        for (String segment : text.split("<NL>")) {
            lines.addAll(fontRenderer.trimStringToWidth(new StringTextComponent(segment), JournalPage.DEFAULT_WIDTH));
            lines.add(IReorderingProcessor.field_242232_a);
        }
        return lines;
    }

    @Override
    public void render(MatrixStack renderStack, float x, float y, float z, float pTicks, float mouseX, float mouseY) {
        renderStack.push();
        renderStack.translate(x, y, z);
        for (IReorderingProcessor text : this.localizedText) {
            RenderingDrawUtils.renderStringAt(text, renderStack, this.fontRenderer, 0x00CCCCCC, false);
            renderStack.translate(0, 10, 0);
        }
        renderStack.pop();
    }
}
