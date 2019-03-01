/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
        if(Item.getItemFromBlock(stateToStore.getBlock()) == Items.AIR) return; //Can't charge the player anyway.
        if(stateToStore.getBlockHardness(w, pos) == -1) return;
        NBTTagCompound stateTag = NBTHelper.getBlockStateNBTTag(stateToStore);

        NBTTagCompound cmp = NBTHelper.getPersistentData(storeIn);
        NBTTagList list = cmp.getTagList("storedStates", Constants.NBT.TAG_COMPOUND);
        list.appendTag(stateTag);
        cmp.setTag("storedStates", list);
    }

    @Nonnull
    public static Map<IBlockState, ItemStack> getMappedStoredStates(ItemStack referenceContainer) {
        List<IBlockState> blockStates = getStoredStates(referenceContainer);
        Map<IBlockState, ItemStack> map = new LinkedHashMap<>();
        for (IBlockState state : blockStates) {
            ItemStack stack = ItemUtils.createBlockStack(state);
            if(!stack.isEmpty()) {
                map.put(state, stack);
            }
        }
        return map;
    }

    @Nonnull
    private static NonNullList<IBlockState> getStoredStates(ItemStack referenceContainer) {
        NonNullList<IBlockState> states = NonNullList.create();
        if(!referenceContainer.isEmpty() && referenceContainer.getItem() instanceof ItemBlockStorage) {
            NBTTagCompound persistent = NBTHelper.getPersistentData(referenceContainer);
            NBTTagList stored = persistent.getTagList("storedStates", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < stored.tagCount(); i++) {
                IBlockState state = NBTHelper.getBlockStateFromTag(stored.getCompoundTagAt(i));
                if(state != null) {
                    states.add(state);
                }
            }
        }
        return states;
    }

    public static void tryClearContainerFor(EntityPlayer player) {
        ItemStack used = player.getHeldItem(EnumHand.MAIN_HAND);
        if(!used.isEmpty() && used.getItem() instanceof ItemBlockStorage) {
            NBTHelper.getPersistentData(used).removeTag("storedStates");
        }
    }

    protected static Random getPreviewRandomFromWorld(World world) {
        long tempSeed = 0x6834F10A91B03F15L;
        tempSeed *= (world.getTotalWorldTime() / 40) << 8;
        return new Random(tempSeed);
    }
}
