/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.progression;

import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalCluster
 * Created by HellFirePvP
 * Date: 03.08.2019 / 17:52
 */
public class JournalCluster extends Rectangle {

    public final AbstractRenderableTexture cloudTexture, clusterBackgroundTexture;
    public int maxX, maxY;

    /**
     * Keep in mind: negative coords are left upper side of the GUI
     * <p>
     * uppermost = most negative Y, lowermost = most positive Y
     * leftmost =  most negative X, rightmost = most positive X
     * <p>
     * A wrong definition doesn't affect size calculation, but rendering.
     */
    public JournalCluster(AbstractRenderableTexture cloudTexture, AbstractRenderableTexture clusterBackgroundTexture,
                          int leftMost, int upperMost, int rightMost, int lowerMost) {
        super(leftMost, upperMost, rightMost - leftMost, lowerMost - upperMost);
        this.cloudTexture = cloudTexture;
        this.clusterBackgroundTexture = clusterBackgroundTexture;
        this.maxX = rightMost;
        this.maxY = lowerMost;
    }

}
