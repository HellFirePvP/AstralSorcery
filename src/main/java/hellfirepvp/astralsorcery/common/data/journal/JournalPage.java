/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.journal;

import hellfirepvp.astralsorcery.client.screen.journal.page.RenderablePage;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalPage
 * Created by HellFirePvP
 * Date: 29.08.2016 / 18:00
 */
public interface JournalPage {

    public static final int DEFAULT_WIDTH = 175;
    public static final int DEFAULT_HEIGHT = 220;

    @OnlyIn(Dist.CLIENT)
    public RenderablePage buildRenderPage(ResearchNode node, int page);

}
