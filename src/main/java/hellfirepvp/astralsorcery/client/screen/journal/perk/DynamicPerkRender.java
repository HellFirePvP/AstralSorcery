/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.perk;

import hellfirepvp.astralsorcery.common.perk.AllocationStatus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DynamicPerkRender
 * Created by HellFirePvP
 * Date: 08.08.2019 / 17:42
 */
public interface DynamicPerkRender {

    public void renderAt(AllocationStatus status, long spriteOffsetTick, float pTicks,
                         float x, float y, float zLevel, float scale);

}
