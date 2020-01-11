/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.progression;

import hellfirepvp.astralsorcery.client.screen.helper.SizeHandler;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ProgressionSizeHandler
 * Created by HellFirePvP
 * Date: 03.08.2019 / 18:06
 */
public class ProgressionSizeHandler extends SizeHandler {

    private ResearchProgression part;

    public ProgressionSizeHandler(ResearchProgression part, int height, int width) {
        super(height, width);
        this.part = part;
    }

    @Override
    @Nullable
    public int[] buildRequiredRectangle() {
        int leftMost = 0;
        int rightMost = 0;
        int upperMost = 0;
        int lowerMost = 0;

        for (ResearchNode node : part.getResearchNodes()) {
            int x = node.renderPosX;
            int y = node.renderPosZ;

            if(x < leftMost) leftMost = x;
            if(x > rightMost) rightMost = x;
            if(y > lowerMost) lowerMost = y;
            if(y < upperMost) upperMost = y;
        }
        return new int[] { leftMost, rightMost, upperMost, lowerMost };
    }
}
