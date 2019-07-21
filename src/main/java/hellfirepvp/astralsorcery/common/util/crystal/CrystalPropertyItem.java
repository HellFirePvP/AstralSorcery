/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.crystal;

import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalPropertyItem
 * Created by HellFirePvP
 * Date: 21.07.2019 / 13:31
 */
public interface CrystalPropertyItem {

    int getMaxPropertySize(ItemStack stack);

    @Nullable
    CrystalProperties getProperties(ItemStack stack);

}
