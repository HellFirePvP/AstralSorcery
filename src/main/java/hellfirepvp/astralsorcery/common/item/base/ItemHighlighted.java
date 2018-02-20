/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base;

import net.minecraft.item.ItemStack;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemHighlighted
 * Created by HellFirePvP
 * Date: 01.08.2016 / 19:42
 */
public interface ItemHighlighted {

    default public Color getHightlightColor(ItemStack stack) {
        return Color.WHITE;
    }

}
