/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemVariants
 * Created by HellFirePvP
 * Date: 07.05.2016 / 14:47
 */
public class ItemVariants extends Item implements IItemVariants {

    protected String[] variants;

    public ItemVariants(String[] variants) {
        setMaxStackSize(64);
        setHasSubtypes(true);
        setMaxDamage(0);
        this.variants = variants;
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list) {
        for (int a = 0; a < getVariants().length; a++) {
            list.add(new ItemStack(this, 1, a));
        }
    }

    public String getUnlocalizedName(ItemStack par1ItemStack) {
        if (getVariants()[par1ItemStack.getItemDamage()].equals("*")) {
            return super.getUnlocalizedName();
        }
        return super.getUnlocalizedName() + "." + getVariants()[par1ItemStack.getItemDamage()];
    }

    @Override
    public String[] getVariants() {
        return variants;
    }

    @Override
    public int[] getVariantMetadatas() {
        return null;
    }
}
