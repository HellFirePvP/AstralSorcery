/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree;

import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.GemSlotPerk;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreeGem
 * Created by HellFirePvP
 * Date: 17.11.2018 / 18:47
 */
public class PerkTreeGem<T extends AbstractPerk & GemSlotPerk> extends PerkTreePoint {

    public PerkTreeGem(T perk, Point offset) {
        super(perk, offset);
    }

    @Nullable
    @Override
    public Rectangle renderAtCurrentPos(AllocationStatus status, long spriteOffsetTick, float pTicks) {
        //TODO test item rendering
        return super.renderAtCurrentPos(status, spriteOffsetTick, pTicks);
    }
}
