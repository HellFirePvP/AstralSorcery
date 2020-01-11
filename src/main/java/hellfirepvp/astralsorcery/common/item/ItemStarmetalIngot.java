/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.item.Item;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemStarmetalIngot
 * Created by HellFirePvP
 * Date: 21.07.2019 / 12:24
 */
public class ItemStarmetalIngot extends Item {

    public ItemStarmetalIngot() {
        super(new Properties()
                .group(RegistryItems.ITEM_GROUP_AS));
    }
}
