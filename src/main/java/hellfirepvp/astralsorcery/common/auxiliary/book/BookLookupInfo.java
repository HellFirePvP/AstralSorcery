/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary.book;

import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalPages;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BookLookupInfo
 * Created by HellFirePvP
 * Date: 10.10.2019 / 17:48
 */
public class BookLookupInfo {

    private final ResearchNode node;
    private final int pageIndex;
    private final ResearchProgression neededKnowledge;

    public BookLookupInfo(ResearchNode node, int pageIndex, ResearchProgression neededKnowledge) {
        this.node = node;
        this.pageIndex = pageIndex;
        this.neededKnowledge = neededKnowledge;
    }

    public ResearchNode getResearchNode() {
        return node;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public boolean canSee(PlayerProgress progress) {
        return this.getResearchNode().canSee(progress) && progress.getResearchProgression().contains(this.neededKnowledge);
    }

    @OnlyIn(Dist.CLIENT)
    public void openGui() {
        Minecraft.getInstance().displayGuiScreen(new ScreenJournalPages(Minecraft.getInstance().currentScreen, this.getResearchNode(), this.getPageIndex()));
    }
}
