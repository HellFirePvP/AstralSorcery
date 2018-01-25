/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal.page;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IJournalPage
 * Created by HellFirePvP
 * Date: 29.08.2016 / 18:00
 */
public interface IJournalPage {

    public static final int DEFAULT_WIDTH = 175;
    public static final int DEFAULT_HEIGHT = 220;

    //Called if the collection of pages that contains this page is opened.
    public IGuiRenderablePage buildRenderPage();

}
