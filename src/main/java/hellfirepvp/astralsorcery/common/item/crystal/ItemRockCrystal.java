/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemRockCrystal
 * Created by HellFirePvP
 * Date: 21.07.2019 / 13:31
 */
public class ItemRockCrystal extends ItemCrystalBase {

    public ItemRockCrystal() {
        super(new Properties()
                .group(CommonProxy.ITEM_GROUP_AS));
    }

    @Override
    public ItemAttunedCrystalBase getTunedItemVariant() {
        return ItemsAS.ATTUNED_ROCK_CRYSTAL;
    }

    @Override
    public ItemCrystalBase getInertDuplicateItem() {
        return ItemsAS.ROCK_CRYSTAL;
    }
}
