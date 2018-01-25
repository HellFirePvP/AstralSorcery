/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.useables;

import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemFracturedEssence
 * Created by HellFirePvP
 * Date: 25.01.2018 / 20:27
 */
public class ItemFracturedEssence extends Item implements ItemDynamicColor {

    public ItemFracturedEssence() {
        setMaxDamage(0);
        setMaxStackSize(16);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public int getColorForItemStack(ItemStack stack, int tintIndex) {
        int c = 0x00FFFFFF;
        return c | 0xFF000000;
    }

}
