/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal;

import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePoint;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreeSizeHandler
 * Created by HellFirePvP
 * Date: 01.07.2018 / 01:29
 */
public class PerkTreeSizeHandler extends SizeHandler {

    public PerkTreeSizeHandler(int height, int width) {
        super(height, width);
    }

    @Nullable
    @Override
    public int[] buildRequiredRectangle() {
        int leftMost = 0;
        int rightMost = 0;
        int upperMost = 0;
        int lowerMost = 0;

        for (PerkTreePoint point : PerkTree.INSTANCE.getPerkPoints()) {
            Point offset = point.getOffset();
            int x = offset.x;
            int y = offset.y;
            if(x < leftMost) leftMost = x;
            if(x > rightMost) rightMost = x;
            if(y > lowerMost) lowerMost = y;
            if(y < upperMost) upperMost = y;
        }
        return new int[] { leftMost, rightMost, upperMost, lowerMost };
    }

}
