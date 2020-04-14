/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.journal;

import hellfirepvp.astralsorcery.client.screen.journal.page.RenderPageText;
import hellfirepvp.astralsorcery.client.screen.journal.page.RenderablePage;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalPageText
 * Created by HellFirePvP
 * Date: 11.10.2019 / 22:25
 */
public class JournalPageText implements JournalPage {

    private final String unlocalizedKey;

    public JournalPageText(String unlocalizedKey) {
        this.unlocalizedKey = unlocalizedKey;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderablePage buildRenderPage(ResearchNode node, int nodePage) {
        return new RenderPageText(this.unlocalizedKey);
    }
}
