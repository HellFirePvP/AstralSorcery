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
import java.util.*;

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
    static List<Tuple<ItemStack, Integer>> getInventoryMatchingItemStacks(PlayerEntity player, ItemStack referenceContainer) {
        Map<BlockState, Tuple<ItemStack, Integer>> storedStates = getInventoryMatching(player, referenceContainer);
        List<Tuple<ItemStack, Integer>> foundStacks = new ArrayList<>(storedStates.values());
        foundStacks.sort(Comparator.comparing(tpl -> tpl.getA().getItem().getRegistryName()));
        return foundStacks;
    }

    @Nonnull
    static Map<BlockState, Tuple<ItemStack, Integer>> getInventoryMatching(PlayerEntity player, ItemStack referenceContainer) {
        Map<BlockState, ItemStack> mappedStacks = ItemBlockStorage.getMappedStoredStates(referenceContainer);
        Map<BlockState, Tuple<ItemStack, Integer>> foundContents = new HashMap<>();
        for (BlockState state : mappedStacks.keySet()) {
            ItemStack stored = mappedStacks.get(state);

            int countDisplay = 0;
            Collection<ItemStack> stacks = ItemUtils.findItemsInPlayerInventory(player, stored, true);
            for (ItemStack found : stacks) {
                countDisplay += found.getCount();
            }
            foundContents.put(state, new Tuple<>(stored.copy(), countDisplay));
        }
        return foundContents;
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

    static Random getPreviewRandomFromWorld(World world) {
        long tempSeed = 0x6834F10A91B03F15L;
        tempSeed *= (world.getGameTime() / 40) << 8;
        return new Random(tempSeed);
    }
}
