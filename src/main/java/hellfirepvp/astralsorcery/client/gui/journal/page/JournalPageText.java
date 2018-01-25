/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal.page;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalPageText
 * Created by HellFirePvP
 * Date: 29.08.2016 / 21:34
 */
public class JournalPageText implements IJournalPage {

    private final FontRendererGetter fontRendererGetter;
    private final String text;

    public JournalPageText(String unlocText) {
        this(unlocText, new DefaultFontRendererGetter());
    }

    public JournalPageText(String unlocText, FontRendererGetter renderGetter) {
        this.text = unlocText;
        this.fontRendererGetter = renderGetter;
    }

    @Override
    public IGuiRenderablePage buildRenderPage() {
        return new Render(fontRendererGetter.getFontRenderer(), text);
    }

    public static class Render implements IGuiRenderablePage {

        private FontRenderer fontRenderer;
        private List<String> lines;

        public Render(FontRenderer fontRenderer, String unlocText) {
            this.fontRenderer = fontRenderer;
            this.lines = buildLines(unlocText);
        }

        private List<String> buildLines(String unlocText) {
            String text = I18n.format(unlocText);
            List<String> lines = new LinkedList<>();
            for (String segment : text.split("<NL>")) {
                lines.addAll(fontRenderer.listFormattedStringToWidth(segment, DEFAULT_WIDTH));
                lines.add("");
            }
            return lines;
        }

        @Override
        public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
            offsetY += 5;
            GL11.glColor4f(0.86F, 0.86F, 0.86F, 1F);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                fontRenderer.drawString(line, offsetX, offsetY + (i * 10), 0x00DDDDDD, false);
            }
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

    }

    public static interface FontRendererGetter {

        public FontRenderer getFontRenderer();

    }

    public static class DefaultFontRendererGetter implements FontRendererGetter {

        @Override
        public FontRenderer getFontRenderer() {
            return Minecraft.getMinecraft().fontRenderer;
        }
    }

}
