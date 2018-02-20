/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations;

import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import vazkii.botania.api.item.IBlockProvider;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationBotania
 * Created by HellFirePvP
 * Date: 21.04.2017 / 00:38
 */
public class ModIntegrationBotania {

    private ModIntegrationBotania() {}

    //Empty if no provider can provide that, an item (size 1) if it could be requested successfully.
    @Nonnull
    public static ItemStack requestFromInventory(EntityPlayer requestingPlayer, ItemStack requestingStack, Block block, int meta, boolean doit) {
        IItemHandler inv = requestingPlayer.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if(inv == null) {
            return ItemStack.EMPTY;
        }
        List<ItemStack> providers = new LinkedList<>();
        for (int i = inv.getSlots() - 1; i >= 0; i--) {
            ItemStack invStack = inv.getStackInSlot(i);
            if (!invStack.isEmpty()) {
                Item item = invStack.getItem();
                if ((item instanceof IBlockProvider)) {
                    providers.add(invStack);
                }
            }
        }
        for (ItemStack provStack : providers) {
            IBlockProvider prov = (IBlockProvider) provStack.getItem();
            if (prov.provideBlock(requestingPlayer, requestingStack, provStack, block, meta, doit)) {
                return new ItemStack(block, 1, meta);
            }
        }
        return ItemStack.EMPTY;
    }

    //-1 = infinite
    public static int getItemCount(EntityPlayer requestingPlayer, ItemStack requestingStack, IBlockState stateSearch) {
        Block block = stateSearch.getBlock();
        int meta;
        try {
            meta = block.damageDropped(stateSearch);
        } catch (Exception e) {
            meta = 0;
        }
        ItemStack blockStackStored = ItemUtils.createBlockStack(stateSearch);
        IItemHandler inv = requestingPlayer.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if(inv == null) {
            return 0;
        }
        int amtFound = 0;
        for (int i = inv.getSlots() - 1; i >= 0; i--) {
            ItemStack invStack = inv.getStackInSlot(i);
            if (!invStack.isEmpty()) {
                Item item = invStack.getItem();
                if ((item instanceof IBlockProvider)) {
                    int res = ((IBlockProvider) item).getBlockCount(requestingPlayer, requestingStack, invStack, block, meta);
                    if(res == -1) {
                        return -1;
                    } else {
                        amtFound += res;
                    }
                }
            }
        }

        Collection<ItemStack> stacks = ItemUtils.scanInventoryForMatching(new InvWrapper(requestingPlayer.inventory), blockStackStored, false);
        for (ItemStack stack : stacks) {
            amtFound += stack.getCount();
        }
        return amtFound;
    }

}
