/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.progression;

import hellfirepvp.astralsorcery.client.screen.helper.SizeHandler;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GalaxySizeHandler
 * Created by HellFirePvP
 * Date: 03.08.2019 / 17:03
 */
public class GalaxySizeHandler extends SizeHandler {

    public GalaxySizeHandler(int height, int width) {
        super(height, width);
    }

    @Nullable
    @Override
    public int[] buildRequiredRectangle() {
        int leftMost = 0;
        int rightMost = 0;
        int upperMost = 0;
        int lowerMost = 0;

        PlayerProgress progress = ResearchHelper.getClientProgress();
        for (ResearchProgression resProgress : progress.getResearchProgression()) {
            JournalCluster cluster = JournalProgressionClusterMapping.getClusterMapping(resProgress);

            int x = cluster.x;
            int y = cluster.y;
            if(x < leftMost) leftMost = x;
            if(x > rightMost) rightMost = x;
            if(y > lowerMost) lowerMost = y;
            if(y < upperMost) upperMost = y;
            x = cluster.maxX;
            y = cluster.maxY;
            if(x < leftMost) leftMost = x;
            if(x > rightMost) rightMost = x;
            if(y > lowerMost) lowerMost = y;
            if(y < upperMost) upperMost = y;
        }
        return new int[] { leftMost, rightMost, upperMost, lowerMost };
    }
}