package hellfirepvp.astralsorcery.client.gui.journal.page;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalPageEmpty
 * Created by HellFirePvP
 * Date: 21.10.2016 / 17:57
 */
public class JournalPageEmpty implements IJournalPage {

    @Override
    public IGuiRenderablePage buildRenderPage() {
        return new Render();
    }

    public static class Render implements IGuiRenderablePage {

        @Override
        public void render(float offsetX, float offsetY, float pTicks, float zLevel) {}

    }

}
