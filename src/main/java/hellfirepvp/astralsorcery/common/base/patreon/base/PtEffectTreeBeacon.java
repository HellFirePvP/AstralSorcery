/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.base;

import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PtEffectTreeBeacon
 * Created by HellFirePvP
 * Date: 20.06.2018 / 19:59
 */
public class PtEffectTreeBeacon extends PatreonEffectHelper.PatreonEffect {

    private int overlay = 0xFFFFFFFF, tree = 0xFFFFFFFF, drain = 0xFFFFFFFF;

    public PtEffectTreeBeacon(UUID uniqueId, PatreonEffectHelper.FlareColor chosenColor) {
        super(uniqueId, chosenColor);
    }

    public PtEffectTreeBeacon setOverlayColor(int overlay) {
        this.overlay = overlay;
        return this;
    }

    public PtEffectTreeBeacon setDrainColor(int drain) {
        this.drain = drain;
        return this;
    }

    public PtEffectTreeBeacon setTreeColor(int tree) {
        this.tree = tree;
        return this;
    }

    public int getColorTranslucentOverlay() {
        return this.overlay;
    }

    public int getColorTreeEffects() {
        return this.tree;
    }

    public int getColorTreeDrainEffects() {
        return this.drain;
    }

}
