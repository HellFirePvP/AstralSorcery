/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.perk;

import hellfirepvp.astralsorcery.client.screen.helper.SizeHandler;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreeSizeHandler
 * Created by HellFirePvP
 * Date: 08.08.2019 / 18:37
 */
public class PerkTreeSizeHandler extends SizeHandler {

    public PerkTreeSizeHandler(int height, int width) {
        super(height, width);
        setWidthHeightNodes(10);
        setSpaceBetweenNodes(10);
    }

    @Nullable
    @Override
    public int[] buildRequiredRectangle() {
        int leftMost = 0;
        int rightMost = 0;
        int upperMost = 0;
        int lowerMost = 0;

        for (PerkTreePoint<?> point : PerkTree.PERK_TREE.getPerkPoints()) {
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

    // --------------------------------------
    //  Temporary fix to scaling position issues :P
    // --------------------------------------

    public double clampX(double centerX) {
        if((centerX + widthToBorder) > getTotalWidth()) {
            centerX = getTotalWidth() - widthToBorder;
        }
        if((centerX - widthToBorder) < 0) {
            centerX = Math.min(widthToBorder, getMidX());
        }
        return centerX;
    }

    public double clampY(double centerY) {
        if((centerY + heightToBorder) > getTotalHeight()) {
            centerY = getTotalHeight() - heightToBorder;
        }
        if((centerY - heightToBorder) < 0) {
            centerY = Math.min(heightToBorder, getMidY());
        }
        return centerY;
    }

}