/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base;

import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AlignmentChargeRevealer
 * Created by HellFirePvP
 * Date: 02.03.2020 / 22:05
 */
public interface AlignmentChargeRevealer {

    default public boolean shouldReveal(ItemStack stack) {
        return true;
    }
}
