/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.journal;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    @OnlyIn(Dist.CLIENT)
    public IGuiRenderablePage buildRenderPage();

}
