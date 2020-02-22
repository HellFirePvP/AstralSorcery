/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.item.base.render.ItemGatedVisibility;
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
public class ItemCelestialCrystal extends ItemCrystalBase implements ItemGatedVisibility {

    public ItemCelestialCrystal() {
        super(new Properties()
                .group(CommonProxy.ITEM_GROUP_AS)
                .rarity(CommonProxy.RARITY_CELESTIAL));
    }

    @Override
    public boolean isSupposedToSeeInRender(ItemStack stack) {
        return getClientProgress().getTierReached().isThisLaterOrEqual(ProgressionTier.CONSTELLATION_CRAFT);
    }

    protected Color getItemEntityColor(ItemStack stack) {
        return ColorsAS.CELESTIAL_CRYSTAL;
    }

    @Override
    public int getGeneratedPropertyTiers() {
        return 12;
    }

    @Override
    public int getMaxPropertyTiers() {
        return 18;
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
