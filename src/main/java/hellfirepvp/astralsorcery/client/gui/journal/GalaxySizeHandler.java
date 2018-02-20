/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal;

import hellfirepvp.astralsorcery.client.util.mappings.ClientJournalMapping;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GalaxySizeHandler
 * Created by HellFirePvP
 * Date: 13.08.2016 / 10:52
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

        PlayerProgress progress = ResearchManager.clientProgress;
        for (ResearchProgression resProgress : progress.getResearchProgression()) {
            ClientJournalMapping.JournalCluster cluster = ClientJournalMapping.getClusterMapping(resProgress);
            if(cluster == null)
                throw new IllegalStateException("Could not get Cluster mapping for " + resProgress.name() + " - This is an Implementation error. Please report this!");

            //Because i don't care.
            Point b = cluster.boundary1;
            int x = b.x;
            int y = b.y;
            if(x < leftMost) leftMost = x;
            if(x > rightMost) rightMost = x;
            if(y > lowerMost) lowerMost = y;
            if(y < upperMost) upperMost = y;
            b = cluster.boundary2;
            x = b.x;
            y = b.y;
            if(x < leftMost) leftMost = x;
            if(x > rightMost) rightMost = x;
            if(y > lowerMost) lowerMost = y;
            if(y < upperMost) upperMost = y;
        }
        return new int[] { leftMost, rightMost, upperMost, lowerMost };
    }

}
