/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.telescope;

import hellfirepvp.astralsorcery.client.screen.ScreenTelescope;
import hellfirepvp.astralsorcery.client.screen.base.ConstellationDiscoveryScreen;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TelescopeRotationDrawArea
 * Created by HellFirePvP
 * Date: 15.02.2020 / 09:44
 */
public class TelescopeRotationDrawArea extends ConstellationDiscoveryScreen.DrawArea {

    private ScreenTelescope screenTelescope;
    private TileTelescope.TelescopeRotation rotation;

    public TelescopeRotationDrawArea(ScreenTelescope screenTelescope, TileTelescope.TelescopeRotation rotation, Rectangle area) {
        super(area);
        this.screenTelescope = screenTelescope;
        this.rotation = rotation;
    }

    @Override
    public boolean isVisible() {
        return this.rotation == screenTelescope.getRotation();
    }
}
