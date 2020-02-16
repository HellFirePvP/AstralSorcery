/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.telescope;

import hellfirepvp.astralsorcery.client.screen.base.ConstellationDiscoveryScreen;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FullScreenDrawArea
 * Created by HellFirePvP
 * Date: 15.02.2020 / 21:14
 */
public class FullScreenDrawArea extends ConstellationDiscoveryScreen.DrawArea {

    public static final FullScreenDrawArea INSTANCE = new FullScreenDrawArea();

    private FullScreenDrawArea() {
        super(new Rectangle());
    }

    @Override
    public boolean contains(double mouseX, double mouseY) {
        return true; //Yes.
    }
}
