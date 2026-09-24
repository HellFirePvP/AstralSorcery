/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.page;

import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.tome.TomePage;
import hellfirepvp.astralsorcery.common.util.ComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderPageText
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderPageText extends RenderPage {

    private final Font font;
    private final List<FormattedCharSequence> localizedParagraphs;

    public RenderPageText(@Nullable ResearchNode node, int nodePage, String unlocalized) {
        super(node, nodePage);
        this.font = Minecraft.getInstance().font;
        this.localizedParagraphs = this.buildLines(unlocalized);
    }

    private List<FormattedCharSequence> buildLines(String unlocalized) {
        String text = Language.getInstance().getOrDefault(unlocalized);
        List<FormattedCharSequence> lines = new ArrayList<>();
        for (String paragraph : text.split("\\\\n")) {
            try {
                lines.addAll(this.font.split(ComponentUtil.parseFormats(paragraph), TomePage.DEFAULT_WIDTH));
                lines.add(FormattedCharSequence.EMPTY);
            } catch (Exception exc) {
                //parser failed or smth
                lines.clear();
                lines.add(Language.getInstance().getVisualOrder(Component.literal(exc.getMessage()).withStyle(ChatFormatting.RED)));
                return lines;
            }
        }
        return lines;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float pTicks, float mouseX, float mouseY) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0);
        for (FormattedCharSequence text : this.localizedParagraphs) {
            guiGraphics.drawString(this.font, text, 0, 0, ColorsAS.TOME_TEXT_COLOR.getColor(), true);
            guiGraphics.pose().translate(0, 10, 0);
        }
        guiGraphics.pose().popPose();
    }
}
