/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.perk;

import hellfirepvp.astralsorcery.client.screen.helper.SizeHandler;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
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

    public PerkTreeSizeHandler() {
        setWidthHeightNodes(10F);
        setSpaceBetweenNodes(10F);
    }

    @Nullable
    @Override
    public float[] buildRequiredRectangle() {
        float leftMost = 0;
        float rightMost = 0;
        float upperMost = 0;
        float lowerMost = 0;

        for (PerkTreePoint<?> point : PerkTree.PERK_TREE.getPerkPoints()) {
            Point.Float offset = point.getOffset();
            float x = offset.x;
            float y = offset.y;
            if (x < leftMost) leftMost = x;
            if (x > rightMost) rightMost = x;
            if (y > lowerMost) lowerMost = y;
            if (y < upperMost) upperMost = y;
        }
        return new float[] { leftMost, rightMost, upperMost, lowerMost };
    }
}
