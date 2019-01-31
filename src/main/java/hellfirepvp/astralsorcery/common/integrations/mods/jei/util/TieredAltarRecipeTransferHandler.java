/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.jei.util;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.container.ContainerAltarBase;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import mezz.jei.JustEnoughItems;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.network.packets.PacketRecipeTransfer;
import mezz.jei.startup.StackHelper;
import mezz.jei.util.Log;
import mezz.jei.util.Translator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TieredAltarRecipeTransferHandler
 * Created by HellFirePvP
 * Date: 15.12.2018 / 00:06
 */
public class TieredAltarRecipeTransferHandler<C extends ContainerAltarBase> implements IRecipeTransferHandler<C> {

    private static final int FILL_CONTAINER_SIZE = 25;
    private static final Tuple<Integer, Integer>[] mirrorMapping = new Tuple[] {
            new Tuple<>(2, 4),
            new Tuple<>(3, 7),
            new Tuple<>(6, 8)
    };

    private final Class<C> containerClass;
    private final StackHelper stackHelper;
    private final IRecipeTransferHandlerHelper handlerHelper;
    private final int maxListSize;

    public TieredAltarRecipeTransferHandler(Class<C> containerClass,
                                            StackHelper stackHelper,
                                            IRecipeTransferHandlerHelper handlerHelper,
                                            int maxListSize) {
        this.containerClass = containerClass;
        this.stackHelper = stackHelper;
        this.handlerHelper = handlerHelper;
        this.maxListSize = maxListSize;
    }

    @Override
    public Class<C> getContainerClass() {
        return containerClass;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(C container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
        if (!JEISessionHandler.getInstance().isJeiOnServer()) {
            String tooltipMessage = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.no.server");
            return handlerHelper.createUserErrorWithTooltip(tooltipMessage);
        }

        if (!containerClass.isAssignableFrom(container.getClass())) {
            return handlerHelper.createInternalError();
        }

        Map<Integer, Slot> inventorySlots = new HashMap<>();
        for (Slot slot : container.inventorySlots.subList(0, 36)) {
            inventorySlots.put(slot.slotNumber, slot);
        }

        Map<Integer, Slot> craftingSlots = new HashMap<>();
        for (Slot slot : container.inventorySlots.subList(36, 36 + maxListSize)) {
            craftingSlots.put(slot.slotNumber, slot);
        }

        int inputCount = 0;
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        Map<Integer, ? extends IGuiIngredient<ItemStack>> itemGroup = Maps.newHashMap(itemStackGroup.getGuiIngredients());
        Iterator<Integer> iterator = itemGroup.keySet().iterator();
        while (iterator.hasNext()) {
            Integer slotId = iterator.next();
            if (slotId > craftingSlots.size()) {
                iterator.remove();
                continue;
            }

            IGuiIngredient<ItemStack> ingredient = itemGroup.get(slotId);
            if (ingredient.isInput() && !ingredient.getAllIngredients().isEmpty()) {
                inputCount++;
            }
        }

        if (inputCount > craftingSlots.size()) {
            Log.get().error("Recipe Transfer helper {} does not work for container {}", containerClass, container.getClass());
            return handlerHelper.createInternalError();
        }

        Map<Integer, ItemStack> availableItemStacks = new HashMap<>();
        int filledCraftSlotCount = 0;
        int emptySlotCount = 0;

        for (Slot slot : craftingSlots.values()) {
            final ItemStack stack = slot.getStack();
            if (!stack.isEmpty()) {
                if (!slot.canTakeStack(player)) {
                    Log.get().error("Recipe Transfer helper {} does not work for container {}. Player can't move item out of Crafting Slot number {}", containerClass, container.getClass(), slot.slotNumber);
                    return handlerHelper.createInternalError();
                }
                filledCraftSlotCount++;
                availableItemStacks.put(slot.slotNumber, stack.copy());
            }
        }

        for (Slot slot : inventorySlots.values()) {
            final ItemStack stack = slot.getStack();
            if (!stack.isEmpty()) {
                availableItemStacks.put(slot.slotNumber, stack.copy());
            } else {
                emptySlotCount++;
            }
        }

        // check if we have enough inventory space to shuffle items around to their final locations
        if (filledCraftSlotCount - inputCount > emptySlotCount) {
            String message = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.inventory.full");
            return handlerHelper.createUserErrorWithTooltip(message);
        }

        StackHelper.MatchingItemsResult matchingItemsResult = stackHelper.getMatchingItems(availableItemStacks, itemGroup);

        if (matchingItemsResult.missingItems.size() > 0) {
            String message = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.missing");
            return handlerHelper.createUserErrorForSlots(message, matchingItemsResult.missingItems);
        }


        List<Integer> craftingSlotIndexes = new ArrayList<>(craftingSlots.keySet());
        Collections.sort(craftingSlotIndexes);

        List<Integer> inventorySlotIndexes = new ArrayList<>(inventorySlots.keySet());
        Collections.sort(inventorySlotIndexes);

        // check that the slots exist and can be altered
        for (Map.Entry<Integer, Integer> entry : matchingItemsResult.matchingItems.entrySet()) {
            int craftNumber = entry.getKey();
            int slotNumber = craftingSlotIndexes.get(craftNumber);
            if (slotNumber < 0 || slotNumber >= container.inventorySlots.size()) {
                Log.get().error("Recipes Transfer Helper {} references slot {} outside of the inventory's size {}", containerClass, slotNumber, container.inventorySlots.size());
                return handlerHelper.createInternalError();
            }
        }

        mirrorGrid(craftingSlotIndexes);
        Map<Integer, Integer> slotMap = Maps.newHashMap(matchingItemsResult.matchingItems);
        mirrorSlotGrid(slotMap);

        if (doTransfer) {
            PacketRecipeTransfer packet = new PacketRecipeTransfer(slotMap, craftingSlotIndexes, inventorySlotIndexes, maxTransfer, true);
            JustEnoughItems.getProxy().sendPacketToServer(packet);
        }

        return null;
    }

    private void mirrorSlotGrid(Map<Integer, Integer> slotMap) {
        for (Tuple<Integer, Integer> mirrorPair : mirrorMapping) {
            if (slotMap.containsKey(mirrorPair.key)) {
                Integer keyCache = slotMap.get(mirrorPair.key);

                if (slotMap.containsKey(mirrorPair.value)) {
                    slotMap.put(mirrorPair.key, slotMap.remove(mirrorPair.value));
                } else {
                    slotMap.remove(mirrorPair.key);
                }
                slotMap.put(mirrorPair.value, keyCache);
            } else {
                if (slotMap.containsKey(mirrorPair.value)) {
                    slotMap.put(mirrorPair.key, slotMap.remove(mirrorPair.value));
                }
            }
        }
    }

    private void mirrorGrid(List<Integer> slotIndexes) {
        for (Tuple<Integer, Integer> mirrorPair : mirrorMapping) {
            Integer slotFrom = slotIndexes.get(mirrorPair.key);
            slotIndexes.set(mirrorPair.key, slotIndexes.get(mirrorPair.value));
            slotIndexes.set(mirrorPair.value, slotFrom);
        }
    }

}
