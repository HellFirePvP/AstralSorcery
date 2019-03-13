/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.perk;

import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePoint;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DynamicPerkRender
 * Created by HellFirePvP
 * Date: 24.11.2018 / 12:40
 */
public interface DynamicPerkRender {

    public void renderAt(PerkTreePoint.AllocationStatus status, long spriteOffsetTick, float pTicks,
                              double x, double y, double scale);

}
