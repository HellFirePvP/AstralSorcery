/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ItemDynamicColor
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
//Mirror for ItemColor
public interface ItemDynamicColor extends ItemLike {

    int getColor(ItemStack stack, long tick, int tintIndex);

}
