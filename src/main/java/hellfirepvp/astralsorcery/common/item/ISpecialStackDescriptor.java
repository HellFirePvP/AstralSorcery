package hellfirepvp.astralsorcery.common.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ISpecialStackDescriptor
 * Created by HellFirePvP
 * Date: 23.04.2017 / 21:42
 */
//Intended to be implemented by blocks to give a more appropiate itemstack as descriptor
public interface ISpecialStackDescriptor {

    @Nonnull
    public ItemStack getDecriptor(IBlockState state);

}