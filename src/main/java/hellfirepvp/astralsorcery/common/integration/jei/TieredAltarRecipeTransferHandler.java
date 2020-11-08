/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.jei;

import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.container.ContainerAltarBase;
import hellfirepvp.astralsorcery.common.util.MapStream;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.config.ServerInfo;
import mezz.jei.network.Network;
import mezz.jei.network.packets.PacketRecipeTransfer;
import mezz.jei.transfer.RecipeTransferUtil;
import mezz.jei.util.Translator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MappedRecipeTransferHandler
 * Created by HellFirePvP
 * Date: 05.09.2020 / 15:35
 */
public class TieredAltarRecipeTransferHandler<C extends ContainerAltarBase> implements IRecipeTransferHandler<C> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Class<C> containerClass;
    private final IStackHelper stackHelper;
    private final IRecipeTransferHandlerHelper handlerHelper;
    private final int maxListSize;

    public TieredAltarRecipeTransferHandler(Class<C> containerClass,
                                            IStackHelper stackHelper,
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
    public IRecipeTransferError transferRecipe(C container, IRecipeLayout recipeLayout, PlayerEntity player, boolean maxTransfer, boolean doTransfer) {
        if (!ServerInfo.isJeiOnServer()) {
            String tooltipMessage = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.no.server");
            return handlerHelper.createUserErrorWithTooltip(tooltipMessage);
        }

        if (!containerClass.isAssignableFrom(container.getClass())) {
            return handlerHelper.createInternalError();
        }

        IRecipeCategory<?> category = recipeLayout.getRecipeCategory();
        if (!(category instanceof CategoryAltar)) {
            return handlerHelper.createInternalError();
        }
        AltarType recipeTier = ((CategoryAltar) category).getAltarType();
        AltarType altarTier = container.getTileEntity().getAltarType();

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
        //Remove relay inputs from the input grid.
        Map<Integer, IGuiIngredient<ItemStack>> itemStacks = new HashMap<>();
        for (Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> entry : itemStackGroup.getGuiIngredients().entrySet()) {
            if (entry.getKey() < 25) {
                itemStacks.put(entry.getKey(), entry.getValue());
            }
        }

        for (IGuiIngredient<ItemStack> ingredient : itemStacks.values()) {
            if (ingredient.isInput() && !ingredient.getAllIngredients().isEmpty()) {
                inputCount++;
            }
        }

        if (inputCount > craftingSlots.size()) {
            LOGGER.error("Recipe Transfer does not work for container {}", container.getClass());
            return handlerHelper.createInternalError();
        }

        Map<Integer, ItemStack> availableItemStacks = new HashMap<>();
        int filledCraftSlotCount = 0;
        int emptySlotCount = 0;

        for (Slot slot : craftingSlots.values()) {
            final ItemStack stack = slot.getStack();
            if (!stack.isEmpty()) {
                if (!slot.canTakeStack(player)) {
                    LOGGER.error("Recipe Transfer does not work for container {}. Player can't move item out of Crafting Slot number {}", container.getClass(), slot.slotNumber);
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

        RecipeTransferUtil.MatchingItemsResult matchingItemsResult = RecipeTransferUtil.getMatchingItems(stackHelper, availableItemStacks, itemStacks);

        if (matchingItemsResult.missingItems.size() > 0) {
            String message = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.missing");
            return handlerHelper.createUserErrorForSlots(message, matchingItemsResult.missingItems);
        }


        List<Integer> craftingSlotIndexes = new ArrayList<>(craftingSlots.keySet());
        Collections.sort(craftingSlotIndexes);

        List<Integer> inventorySlotIndexes = new ArrayList<>(inventorySlots.keySet());
        Collections.sort(inventorySlotIndexes);

        Map<Integer, Integer> matchIndices = MapStream.of(matchingItemsResult.matchingItems)
                .mapKey(container::translateIndex)
                .toMap();

        // check that the slots exist and can be altered
        for (Map.Entry<Integer, Integer> entry : matchIndices.entrySet()) {
            int craftNumber = entry.getKey();
            int slotNumber = craftingSlotIndexes.get(craftNumber);
            if (slotNumber < 0 || slotNumber >= container.inventorySlots.size()) {
                LOGGER.error("Recipes Transfer references slot {} outside of the inventory's size {}", slotNumber, container.inventorySlots.size());
                return handlerHelper.createInternalError();
            }
        }

        if (doTransfer) {
            PacketRecipeTransfer packet = new PacketRecipeTransfer(matchIndices, craftingSlotIndexes, inventorySlotIndexes, maxTransfer, true);
            Network.sendPacketToServer(packet);
        }

        return null;
    }
}
