/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.item.base.render.ItemGatedVisibility;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.crystal.CrystalProperties;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.Random;

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
                .group(RegistryItems.ITEM_GROUP_AS)
                .rarity(RegistryItems.RARITY_CELESTIAL));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            ItemStack stack = new ItemStack(this);
            this.applyProperties(stack, CrystalProperties.getMaxCelestialProperties());

            items.add(stack);
        }
    }

    @Override
    public int getMaxPropertySize(ItemStack stack) {
        return CrystalProperties.MAX_SIZE_CELESTIAL;
    }

    @Override
    public CrystalProperties getMaxProperties(ItemStack stack) {
        return CrystalProperties.getMaxCelestialProperties();
    }

    @Override
    public Item getAttunedVariant() {
        return null;
    }

    @Override
    public CrystalProperties generateRandom(Random rand) {
        return CrystalProperties.createRandomCelestial();
    }

    @Override
    public boolean isSupposedToSeeInRender(ItemStack stack) {
        return getClientProgress().getTierReached().isThisLaterOrEqual(ProgressionTier.CONSTELLATION_CRAFT);
    }
}
