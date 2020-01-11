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
 * Class: ConstellationItem
 * Created by HellFirePvP
 * Date: 16.08.2019 / 06:31
 */
public interface ConstellationItem {

    @Nullable
    IWeakConstellation getAttunedConstellation(ItemStack stack);

    boolean setAttunedConstellation(ItemStack stack, @Nullable IWeakConstellation cst);

    @Nullable
    IMinorConstellation getTraitConstellation(ItemStack stack);

    boolean setTraitConstellation(ItemStack stack, @Nullable IMinorConstellation cst);

}
