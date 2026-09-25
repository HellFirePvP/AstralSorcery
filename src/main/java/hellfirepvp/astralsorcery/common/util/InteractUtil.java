/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: InteractUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class InteractUtil {

    @Nullable
    public static ItemInteractionResult tryTransferFluidIntoBlock(ItemStack heldStack, Player player, Level level, BlockPos pos, Consumer<ItemStack> stackUpdateFn) {
        IFluidHandler target = MiscUtil.getTileAt(level, pos, BlockEntity.class, true)
                .map(blockEntity -> level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null))
                .orElse(null);
        if (heldStack.isEmpty() || target == null) {
            return null;
        }

        FluidActionResult far = FluidUtil.tryEmptyContainer(heldStack, target, FluidType.BUCKET_VOLUME, player, true);
        if (!far.isSuccess()) return null;
        if (!player.isCreative()) stackUpdateFn.accept(far.getResult());
        return ItemInteractionResult.SUCCESS;
    }

    @Nullable
    public static ItemInteractionResult tryTransferFluidFromBlock(ItemStack heldStack, Player player, Level level, BlockPos pos, Consumer<ItemStack> stackUpdateFn) {
        IFluidHandler target = MiscUtil.getTileAt(level, pos, BlockEntity.class, true)
                .map(blockEntity -> level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null))
                .orElse(null);
        if (heldStack.isEmpty() || target == null) {
            return null;
        }

        FluidActionResult far = FluidUtil.tryFillContainer(heldStack, target, FluidType.BUCKET_VOLUME, player, true);
        if (!far.isSuccess()) return null;
        if (!player.isCreative()) stackUpdateFn.accept(far.getResult());
        return ItemInteractionResult.SUCCESS;
    }

    @Nullable
    public static ItemInteractionResult tryPlaceItemIntoBlock(ItemStack heldStack, Player player, Level level, BlockPos pos) {
        IItemHandler target = MiscUtil.getTileAt(level, pos, BlockEntity.class, true)
                .map(blockEntity -> level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null))
                .orElse(null);
        if (target == null) {
            return null;
        }

        boolean playPickupSound = false;
        ItemInteractionResult result = null;
        //Extract 1 item that can be found
        for (int slot = 0; slot < target.getSlots(); slot++) {
            ItemStack invSlot = target.extractItem(slot, 64, false);
            if (!invSlot.isEmpty()) {
                if (!player.getInventory().add(invSlot)) {
                    ItemUtil.dropItem(level, player.position(), invSlot);
                }
                playPickupSound = true;
                result = ItemInteractionResult.SUCCESS;
                break;
            }
        }

        if (!heldStack.isEmpty()) {
            for (int slot = 0; slot < target.getSlots(); slot++) {
                ItemStack remaining = target.insertItem(slot, heldStack, false);
                int inserted = heldStack.getCount() - remaining.getCount();
                if (inserted > 0) {
                    playPickupSound = true;
                    if (!player.isCreative()) {
                        heldStack.shrink(inserted);
                    }
                    result = ItemInteractionResult.SUCCESS;
                    break;
                }
            }
        }

        if (playPickupSound) {
            level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS,
                    0.2F, ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
        return result;
    }

    public static void handleContainerReplacement(Player player, ItemStack newStack, InteractionHand hand) {
        if (!player.isCreative()) {
            ItemStack contained = player.getItemInHand(hand);
            contained.shrink(1);
            giveItemToPlayer(player, newStack, hand);
        }
    }

    public static void giveItemToPlayer(Player player, ItemStack stack) {
        giveItemToPlayer(player, stack, -1);
    }

    public static void giveItemToPlayer(Player player, ItemStack stack, InteractionHand hand) {
        int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : Inventory.SLOT_OFFHAND;
        giveItemToPlayer(player, stack, slot);
    }

    public static void giveItemToPlayer(Player player, ItemStack stack, int slot) {
        ItemHandlerHelper.giveItemToPlayer(player, stack, slot);
    }
}
