/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.item.base.render.ItemGatedVisibility;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.crystal.CrystalProperties;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemAttunedRockCrystal
 * Created by HellFirePvP
 * Date: 21.07.2019 / 13:38
 */
public class ItemAttunedRockCrystal extends ItemAttunedCrystalBase implements ItemGatedVisibility {

    public ItemAttunedRockCrystal() {
        super(new Properties()
                .group(RegistryItems.ITEM_GROUP_AS_CRYSTALS));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            for (IWeakConstellation cst : ConstellationRegistry.getWeakConstellations()) {
                ItemStack stack = new ItemStack(this);
                ItemAttunedCrystalBase.applyMainConstellation(stack, cst);
                this.applyProperties(stack, CrystalProperties.getMaxRockProperties());

                items.add(stack);
            }
        }
    }

    @Override
    public int getMaxPropertySize(ItemStack stack) {
        return CrystalProperties.MAX_SIZE_ROCK;
    }

    @Override
    public CrystalProperties getMaxProperties(ItemStack stack) {
        return CrystalProperties.getMaxRockProperties();
    }

    @Override
    public Item getAttunedVariant() {
        return this;
    }

    @Override
    public CrystalProperties generateRandom(Random rand) {
        return CrystalProperties.createRandomRock();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isSupposedToSeeInRender(ItemStack stack) {
        return getClientProgress().getTierReached().isThisLaterOrEqual(ProgressionTier.ATTUNEMENT);
    }
}
