/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.CommonProxy;
import net.minecraft.item.Item;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemParchment
 * Created by HellFirePvP
 * Date: 21.07.2019 / 12:26
 */
public class ItemParchment extends Item {

    public ItemParchment() {
        super(new Properties()
                .group(CommonProxy.ITEM_GROUP_AS));
    }
}
