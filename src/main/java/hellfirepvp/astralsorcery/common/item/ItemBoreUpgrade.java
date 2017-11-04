/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.item.base.IItemVariants;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileBore;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBoreUpgrade
 * Created by HellFirePvP
 * Date: 03.11.2017 / 14:19
 */
public class ItemBoreUpgrade extends Item implements IItemVariants {

    public ItemBoreUpgrade() {
        setMaxStackSize(1);
        setHasSubtypes(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(isInCreativeTab(tab)) {
            for (TileBore.BoreType bt : TileBore.BoreType.values()) {
                items.add(new ItemStack(this, 1, bt.ordinal()));
            }
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        Item i = stack.getItem();
        if(i instanceof ItemBoreUpgrade) {
            TileBore.BoreType type = TileBore.BoreType.values()[MathHelper.clamp(stack.getItemDamage(), 0, TileBore.BoreType.values().length - 1)];
            return super.getUnlocalizedName(stack) + "." + type.getName();
        }
        return super.getUnlocalizedName(stack);
    }

    @Nonnull
    public static TileBore.BoreType getBoreType(ItemStack stack) {
        return TileBore.BoreType.values()[MathHelper.clamp(stack.getItemDamage(), 0, TileBore.BoreType.values().length - 1)];
    }

    @Override
    public String[] getVariants() {
        String[] out = new String[TileBore.BoreType.values().length];
        TileBore.BoreType[] values = TileBore.BoreType.values();
        for (int i = 0; i < values.length; i++) {
            out[i] = values[i].name();
        }
        return out;
    }

    @Override
    public int[] getVariantMetadatas() {
        int[] out = new int[TileBore.BoreType.values().length];
        TileBore.BoreType[] values = TileBore.BoreType.values();
        for (int i = 0; i < values.length; i++) {
            out[i] = values[i].ordinal();
        }
        return out;
    }
}
