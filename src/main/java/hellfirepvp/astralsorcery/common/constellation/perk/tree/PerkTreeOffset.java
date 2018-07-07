/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree;

import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreeOffset
 * Created by HellFirePvP
 * Date: 17.06.2018 / 09:32
 */
public class PerkTreeOffset extends PerkTreePoint {

    public PerkTreeOffset(AbstractPerk perk, int offsetX, int offsetY) {
        super(perk, offsetX, offsetY);
    }

    public PerkTreeOffset(AbstractPerk perk, Point offset) {
        super(perk, offset);
    }
}
