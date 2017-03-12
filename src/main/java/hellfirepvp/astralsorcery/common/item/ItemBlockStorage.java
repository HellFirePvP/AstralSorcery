/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlockStorage
 * Created by HellFirePvP
 * Date: 03.03.2017 / 17:08
 */
public abstract class ItemBlockStorage extends Item {

    public static void tryStoreBlock(ItemStack storeIn, World w, BlockPos pos) {
        if(w.getTileEntity(pos) != null) return;
        IBlockState stateToStore = w.getBlockState(pos);
        if(Item.getItemFromBlock(stateToStore.getBlock()) == null) return; //Can't charge the player anyway.
        NBTTagCompound cmp = NBTHelper.getPersistentData(storeIn);
        cmp.setString("storedBlock", stateToStore.getBlock().getRegistryName().toString());
        cmp.setInteger("storedMeta", stateToStore.getBlock().damageDropped(stateToStore));
    }

    @Nullable
    public static ItemStack getStoredStateAsStack(ItemStack stack) {
        IBlockState stored = getStoredState(stack);
        if(stored == null) return null; //Guarantees also that the block has an itemblock.
        return new ItemStack(stored.getBlock(), 1, stored.getBlock().damageDropped(stored));
    }

    @Nullable
    public static IBlockState getStoredState(ItemStack stack) {
        NBTTagCompound persistentTag = NBTHelper.getPersistentData(stack);
        ResourceLocation blockRes;
        if(persistentTag.hasKey("storedBlock")) {
            blockRes = new ResourceLocation(persistentTag.getString("storedBlock"));
        } else {
            blockRes = new ResourceLocation("air");
        }
        Block b = ForgeRegistries.BLOCKS.getValue(blockRes);
        if(b == null) return null;
        if(Item.getItemFromBlock(b) == null) return null; //Can't charge the user properly anyway...

        boolean hasMeta = persistentTag.hasKey("storedMeta");
        int meta = persistentTag.getInteger("storedMeta");
        if(hasMeta) {
            return b.getStateFromMeta(meta);
        } else {
            return b.getDefaultState();
        }
    }
}
