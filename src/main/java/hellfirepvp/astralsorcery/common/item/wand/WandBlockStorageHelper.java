/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import hellfirepvp.astralsorcery.common.component.BlockStateStorageComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: WandBlockStorageHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class WandBlockStorageHelper {

    private static final long PREVIEW_SEED_BASE = 0x6834F10A91B03F15L;
    public static final int MAX_RAYTRACE_DISTANCE = 60;

    private WandBlockStorageHelper() {}

    public static BlockStateStorageComponent getStorage(ItemStack stack) {
        return stack.getOrDefault(DataComponentsAS.BLOCK_STATE_STORAGE, BlockStateStorageComponent.EMPTY);
    }

    public static boolean tryStoreBlock(ItemStack stack, Level level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) != null) return true;

        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.getDestroySpeed(level, pos) == -1) return true;

        ItemStack blockStack = new ItemStack(state.getBlock());
        if (blockStack.isEmpty()) return true;

        BlockStateStorageComponent current = getStorage(stack);
        stack.set(DataComponentsAS.BLOCK_STATE_STORAGE, current.withAddedState(state));
        player.displayClientMessage(Component.translatable("message.astralsorcery.wand.stored",
                state.getBlock().getName()), true);
        return true;
    }

    public static void clearStorage(ItemStack stack) {
        BlockStateStorageComponent current = getStorage(stack);
        stack.set(DataComponentsAS.BLOCK_STATE_STORAGE, current.withClearedStates());
    }

    public static boolean consumeBlock(Player player, ItemStack match) {
        IItemHandler inv = player.getCapability(Capabilities.ItemHandler.ENTITY);
        if (inv == null) return false;

        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack inSlot = inv.getStackInSlot(i);
            if (ItemStack.isSameItem(inSlot, match)) {
                ItemStack extracted = inv.extractItem(i, 1, false);
                if (!extracted.isEmpty()) return true;
            }
        }
        return false;
    }

    /**
     * Gets a map of stored BlockState -> (ItemStack representation, available count in inventory).
     * This maps each unique stored state to how many matching items the player has.
     */
    public static Map<BlockState, InventoryEntry> getInventoryMatching(Player player, ItemStack wandStack) {
        BlockStateStorageComponent storage = getStorage(wandStack);
        Map<BlockState, InventoryEntry> result = new LinkedHashMap<>();

        for (BlockState state : storage.storedStates()) {
            if (result.containsKey(state)) continue;

            ItemStack blockItem = new ItemStack(state.getBlock());
            if (blockItem.isEmpty()) continue;

            int count = player.isCreative() ? Integer.MAX_VALUE : countMatchingItems(player, blockItem);
            result.put(state, new InventoryEntry(blockItem, count));
        }
        return result;
    }

    /**
     * Computes the total number of available items across all stored states.
     */
    public static int countTotalAvailable(Player player, ItemStack wandStack) {
        if (player.isCreative()) return Integer.MAX_VALUE;

        int total = 0;
        for (InventoryEntry entry : getInventoryMatching(player, wandStack).values()) {
            total += entry.count();
        }
        return total;
    }

    /**
     * Builds a position -> state map for preview rendering.
     * Uses a time-seeded random for consistent preview across frames.
     */
    public static Map<BlockPos, BlockState> buildPreviewMap(Player player, ItemStack wandStack,
                                                             List<BlockPos> positions, Level level) {
        return buildPlaceableMap(player, wandStack, positions, getPreviewRandom(level));
    }

    /**
     * Builds a position -> state map for actual placement.
     * Uses a fresh random so each placement produces a different shuffle.
     */
    public static Map<BlockPos, BlockState> buildPlaceableMap(Player player, ItemStack wandStack,
                                                               List<BlockPos> positions) {
        return buildPlaceableMap(player, wandStack, positions, new Random());
    }

    private static Map<BlockPos, BlockState> buildPlaceableMap(Player player, ItemStack wandStack,
                                                                List<BlockPos> positions, Random rand) {
        Map<BlockState, InventoryEntry> inventoryMap = getInventoryMatching(player, wandStack);
        if (inventoryMap.isEmpty()) return Map.of();

        Map<BlockState, Integer> remainingAmounts = new HashMap<>();
        for (var entry : inventoryMap.entrySet()) {
            remainingAmounts.put(entry.getKey(), entry.getValue().count());
        }
        List<BlockState> availableStates = new ArrayList<>(remainingAmounts.keySet());

        Map<BlockPos, BlockState> placeables = new LinkedHashMap<>();
        for (BlockPos pos : positions) {
            Collections.shuffle(availableStates, rand);
            BlockState toPlace = availableStates.isEmpty() ? null : availableStates.get(0);
            if (toPlace == null) continue;

            if (!player.isCreative()) {
                int count = remainingAmounts.get(toPlace);
                count--;
                if (count <= 0) {
                    remainingAmounts.remove(toPlace);
                    availableStates.remove(toPlace);
                } else {
                    remainingAmounts.put(toPlace, count);
                }
            }

            placeables.put(pos, toPlace);
        }
        return placeables;
    }

    /**
     * Returns a deterministic Random seeded by world game time, so previews are
     * consistent across frames within a 2-second window.
     */
    public static Random getPreviewRandom(Level level) {
        long seed = PREVIEW_SEED_BASE * ((level.getGameTime() / 40) << 8);
        return new Random(seed);
    }

    private static int countMatchingItems(Player player, ItemStack match) {
        IItemHandler inv = player.getCapability(Capabilities.ItemHandler.ENTITY);
        if (inv == null) return 0;

        int count = 0;
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack inSlot = inv.getStackInSlot(i);
            if (ItemStack.isSameItem(inSlot, match)) {
                count += inSlot.getCount();
            }
        }
        return count;
    }

    public record InventoryEntry(ItemStack itemStack, int count) {}
}
