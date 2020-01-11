/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationBaseItem
 * Created by HellFirePvP
 * Date: 28.09.2019 / 07:11
 */
public interface ConstellationBaseItem {

    @Nullable
    IConstellation getConstellation(ItemStack stack);

    boolean setConstellation(ItemStack stack, @Nullable IConstellation cst);
}
