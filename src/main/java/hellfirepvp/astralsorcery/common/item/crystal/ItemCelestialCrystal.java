/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.item.ItemStack;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCelestialCrystal
 * Created by HellFirePvP
 * Date: 21.07.2019 / 13:33
 */
public class ItemCelestialCrystal extends ItemCrystalBase {

    public ItemCelestialCrystal() {
        super(new Properties()
                .group(CommonProxy.ITEM_GROUP_AS)
                .rarity(CommonProxy.RARITY_CELESTIAL));
    }

    protected Color getItemEntityColor(ItemStack stack) {
        return ColorsAS.CELESTIAL_CRYSTAL;
    }

    @Override
    public int getGeneratedPropertyTiers() {
        return 8;
    }

    @Override
    public int getMaxPropertyTiers() {
        return 10;
    }

    @Override
    public ItemAttunedCrystalBase getTunedItemVariant() {
        return ItemsAS.ATTUNED_CELESTIAL_CRYSTAL;
    }

    @Override
    public ItemCrystalBase getInertDuplicateItem() {
        return ItemsAS.CELESTIAL_CRYSTAL;
    }
}
