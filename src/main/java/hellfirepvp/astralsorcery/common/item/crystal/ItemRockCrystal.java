/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

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
 * Class: ItemRockCrystal
 * Created by HellFirePvP
 * Date: 21.07.2019 / 13:31
 */
public class ItemRockCrystal extends ItemCrystalBase {

    public ItemRockCrystal() {
        super(new Properties()
                .group(RegistryItems.ITEM_GROUP_AS));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            ItemStack stack = new ItemStack(this);
            this.applyProperties(stack, CrystalProperties.getMaxRockProperties());

            items.add(stack);
        }
    }

    @Override
    public int getMaxPropertySize(ItemStack stack) {
        return CrystalProperties.MAX_SIZE_ROCK;
    }

    @Override
    public Item getAttunedVariant() {
        return null;
    }

    @Override
    public CrystalProperties generateRandom(Random rand) {
        return CrystalProperties.createRandomRock();
    }
}
