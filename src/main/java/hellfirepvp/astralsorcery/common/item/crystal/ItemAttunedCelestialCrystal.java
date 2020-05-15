/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.item.base.render.ItemGatedVisibility;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemAttunedCelestialCrystal
 * Created by HellFirePvP
 * Date: 21.07.2019 / 13:49
 */
public class ItemAttunedCelestialCrystal extends ItemAttunedCrystalBase implements ItemGatedVisibility {

    public ItemAttunedCelestialCrystal() {
        super(new Properties()
                .rarity(CommonProxy.RARITY_CELESTIAL)
                .group(CommonProxy.ITEM_GROUP_AS_CRYSTALS));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            for (IWeakConstellation cst : ConstellationRegistry.getWeakConstellations()) {
                ItemStack stack = new ItemStack(this);
                setAttunedConstellation(stack, cst);
                items.add(stack);
            }
        }
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
    @OnlyIn(Dist.CLIENT)
    public boolean isSupposedToSeeInRender(ItemStack stack) {
        return getClientProgress().getTierReached().isThisLaterOrEqual(ProgressionTier.CONSTELLATION_CRAFT);
    }

    protected Color getItemEntityColor(ItemStack stack) {
        return ColorsAS.CELESTIAL_CRYSTAL;
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
