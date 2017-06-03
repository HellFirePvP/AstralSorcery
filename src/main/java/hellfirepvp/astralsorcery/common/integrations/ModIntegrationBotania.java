package hellfirepvp.astralsorcery.common.integrations;

import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import vazkii.botania.api.item.IBlockProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationBotania
 * Created by HellFirePvP
 * Date: 22.04.2017 / 16:47
 */
public class ModIntegrationBotania {

    private ModIntegrationBotania() {}

    //null if no provider can provide that, an item (size 1) if it could be requested successfully.
    @Nullable
    public static ItemStack requestFromInventory(EntityPlayer requestingPlayer, ItemStack requestingStack, Block block, int meta, boolean doit) {
        IItemHandler inv = requestingPlayer.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if(inv == null) {
            return null;
        }
        List<ItemStack> providers = new LinkedList<>();
        for (int i = inv.getSlots() - 1; i >= 0; i--) {
            ItemStack invStack = inv.getStackInSlot(i);
            if (invStack != null && invStack.getItem() != null) {
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
        return null;
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
            if (invStack != null && invStack.getItem() != null) {
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
            amtFound += stack.stackSize;
        }
        return amtFound;
    }

}
