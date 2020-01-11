/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal;

import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalAttributeItem
 * Created by HellFirePvP
 * Date: 20.08.2019 / 19:27
 */
public interface CrystalAttributeItem {

    @Nullable
    CrystalAttributes getAttributes(ItemStack stack);

    void setAttributes(ItemStack stack, @Nullable CrystalAttributes attributes);

}
