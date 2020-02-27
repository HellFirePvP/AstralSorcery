/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base;

import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Tuple;
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
 * Date: 23.02.2020 / 17:36
 */
public interface ItemBlockStorage {

    Random random = new Random();

    static boolean storeBlockState(ItemStack stack, World world, BlockPos pos) {
        if (world.getTileEntity(pos) != null) {
            return false;
        }
        BlockState state = world.getBlockState(pos);
        if (state.isAir(world, pos) ||
                state.getBlockHardness(world, pos) == -1 ||
                ItemUtils.createBlockStack(state).isEmpty()) {
            return false;
        }
        CompoundNBT persistent = NBTHelper.getPersistentData(stack);
        ListNBT stored = persistent.getList("storedStates", Constants.NBT.TAG_COMPOUND);
        stored.add(NBTHelper.getBlockStateNBTTag(state));
        persistent.put("storedStates", stored);
        return true;
    }

    static void clearContainerFor(PlayerEntity player) {
        Tuple<Hand, ItemStack> held = MiscUtils.getMainOrOffHand(player, stack -> stack.getItem() instanceof ItemBlockStorage);
        if (held != null) {
            NBTHelper.getPersistentData(held.getB()).remove("storedStates");
        }
    }

    @Nonnull
    static Map<BlockState, ItemStack> getMappedStoredStates(ItemStack referenceContainer) {
        List<BlockState> blockStates = getStoredStates(referenceContainer);
        Map<BlockState, ItemStack> map = new LinkedHashMap<>();
        for (BlockState state : blockStates) {
            ItemStack stack = ItemUtils.createBlockStack(state);
            if (!stack.isEmpty()) {
                map.put(state, stack);
            }
        }
        return map;
    }

    @Nonnull
    static NonNullList<BlockState> getStoredStates(ItemStack referenceContainer) {
        NonNullList<BlockState> states = NonNullList.create();
        if (!referenceContainer.isEmpty() && referenceContainer.getItem() instanceof ItemBlockStorage) {
            CompoundNBT persistent = NBTHelper.getPersistentData(referenceContainer);
            ListNBT stored = persistent.getList("storedStates", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < stored.size(); i++) {
                BlockState state = NBTHelper.getBlockStateFromTag(stored.getCompound(i));
                if (state != null) {
                    states.add(state);
                }
            }
        }
        return states;
    }

}
