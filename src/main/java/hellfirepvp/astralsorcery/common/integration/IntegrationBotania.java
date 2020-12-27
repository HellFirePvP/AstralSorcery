/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration;

import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import vazkii.botania.api.item.IBlockProvider;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IntegrationBotania
 * Created by Penrif
 * Date: 12.26.2020 / 16:45
 */
public class IntegrationBotania {
    
    public static Collection<ItemStack> botaniaFindItemsInPlayerInventory(PlayerEntity player, ItemStack match) {
        List<ItemStack> stacksOut = new LinkedList<>();

        // Botania can only supply blocks, so let's filter that out first.
        if(!(match.getItem() instanceof BlockItem)) {
            return stacksOut;
        }

        Block matchBlock = ((BlockItem) match.getItem()).getBlock();

        IItemHandler handler = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(ItemUtils.EMPTY_INVENTORY);
        for (int j = 0; j < handler.getSlots(); j++) {
            ItemStack s = handler.getStackInSlot(j);
            Item sItem = s.getItem();
            if (sItem instanceof IBlockProvider) {
                IBlockProvider provider = (IBlockProvider) sItem;
                int blockCount = provider.getBlockCount(player, s, s, matchBlock);
                if (blockCount == -1) {
                    // Used by rods to indicate infinite.  That doesn't suit our needs, so let's just report a lot.
                    blockCount = 9001;
                }
                if (blockCount > 0) {
                    stacksOut.add(ItemUtils.copyStackWithSize(s, blockCount));
                }
            }
        }
        return stacksOut;
    }
    
    public static boolean consumeFromPlayerInventory(PlayerEntity player, ItemStack requestingItemStack, ItemStack toConsume, boolean simulate) {

        // Botania can only supply blocks, so let's filter that out first.
        if (!(toConsume.getItem() instanceof BlockItem)) {
            return false;
        }

        Block consumeBlock = ((BlockItem) toConsume.getItem()).getBlock();
        IItemHandler handler = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(ItemUtils.EMPTY_INVENTORY);
        for (int j = 0; j < handler.getSlots(); j++) {
            ItemStack s = handler.getStackInSlot(j);
            Item sItem = s.getItem();
            if (sItem instanceof IBlockProvider) {
                IBlockProvider provider = (IBlockProvider) sItem;
                int blockCount = provider.getBlockCount(player, s, s, consumeBlock);
                if (blockCount == -1 || blockCount > toConsume.getCount()) {
                    // -1 is used by rods to indicate infinite.  Not technically true, but until Botania sorts out how to report how many invocations the player's
                    // mana can handle, we'll have to take it at its word.
                    // It also doesn't take mana availability into account until actually invoked to fully commit, so best we can do is eat up
                    // as much as we have, and if it wasn't enough, say sorry and fail the entire operation.
                    if (!simulate) {
                        for (int i = 0; i < toConsume.getCount(); i++) {
                            if (!provider.provideBlock(player, s, s, consumeBlock, true)) {
                                return false;
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

}
