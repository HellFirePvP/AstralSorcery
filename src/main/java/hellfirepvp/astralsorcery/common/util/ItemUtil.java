/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ItemUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ItemUtil {

    private static final RandomSource rand = RandomSource.create();

    public static ItemStack createBlockStack(BlockState state) {
        return new ItemStack(state.getBlock());
    }

    public static BlockState createBlockState(Level level, ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem bi)) return Blocks.AIR.defaultBlockState();
        BlockUtil.LevelChanges<BlockState> changes = BlockUtil.captureLevelChanges(level, () -> {
            DirectionalPlaceContext ctx = new DirectionalPlaceContext(level, BlockPos.ZERO, Direction.DOWN, stack, Direction.UP);
            bi.place(ctx);
            return BlockUtil.ChangeResult.revert(level.getBlockState(BlockPos.ZERO));
        });
        return changes.value();
    }

    public static NonNullList<ItemStack> listContents(IItemHandler handler) {
        NonNullList<ItemStack> stacks = NonNullList.withSize(handler.getSlots(), ItemStack.EMPTY);
        for (int i = 0; i < handler.getSlots(); i++) {
            stacks.set(i, handler.getStackInSlot(i));
        }
        return stacks;
    }

    public static Map<Integer, ItemStack> findItemsInInventory(Player player, Predicate<ItemStack> match) {
        IItemHandler inv = player.getCapability(Capabilities.ItemHandler.ENTITY);
        if (inv == null) return Map.of();
        return findItemsInInventory(inv, match);
    }

    public static Map<Integer, ItemStack> findItemsInInventory(IItemHandler inv, ItemStack match, boolean strict) {
        Predicate<ItemStack> matchPredicate = strict
                ? stack -> ItemStack.isSameItem(stack, match)
                : stack -> ItemStack.isSameItemSameComponents(stack, match);
        return findItemsInInventory(inv, matchPredicate);
    }

    public static Map<Integer, ItemStack> findItemsInInventory(IItemHandler inv, Predicate<ItemStack> match) {
        return IntStream.range(0, inv.getSlots())
                .mapToObj(slot -> new Tuple<>(slot, inv.getStackInSlot(slot)))
                .filter(stack -> match.test(stack.getB()))
                .collect(Collectors.toMap(Tuple::getA, Tuple::getB));
    }

    public static Optional<ItemEntity> dropItemNaturally(Level level, BlockPos pos, ItemStack stack) {
        return dropItemNaturally(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
    }

    public static Optional<ItemEntity> dropItemNaturally(Level level, Vec3 pos, ItemStack stack) {
        return dropItem(level, pos.x, pos.y, pos.z, stack);
    }

    public static Optional<ItemEntity> dropItemNaturally(Level level, double x, double y, double z, ItemStack stack) {
        if (stack.isEmpty()) return Optional.empty();

        double yOffset = EntityType.ITEM.getHeight() / 2.0;
        x += Mth.nextDouble(rand, -0.25, 0.25);
        y += Mth.nextDouble(rand, -0.25, 0.25) - yOffset;
        z += Mth.nextDouble(rand, -0.25, 0.25);

        ItemEntity entity = new ItemEntity(level, x, y, z, stack);
        entity.setDefaultPickUpDelay();
        level.addFreshEntity(entity);
        return Optional.of(entity);
    }


    public static Optional<ItemEntity> dropItem(Level level, BlockPos pos, ItemStack stack) {
        return dropItem(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
    }

    public static Optional<ItemEntity> dropItem(Level level, Vec3 pos, ItemStack stack) {
        return dropItem(level, pos.x, pos.y, pos.z, stack);
    }

    public static Optional<ItemEntity> dropItem(Level level, double x, double y, double z, ItemStack stack) {
        if (stack.isEmpty()) return Optional.empty();

        double yOffset = EntityType.ITEM.getHeight() / 2.0;
        x += Mth.nextDouble(rand, -0.25, 0.25);
        y += Mth.nextDouble(rand, -0.25, 0.25) - yOffset;
        z += Mth.nextDouble(rand, -0.25, 0.25);

        ItemEntity entity = new ItemEntity(level, x, y, z, stack, 0, 0, 0);
        entity.setDefaultPickUpDelay();
        level.addFreshEntity(entity);
        return Optional.of(entity);
    }

    public static Optional<ItemStack> swapItem(RegistryAccess registries, ItemStack existing, Item newItem) {
        ResourceLocation newStackId = registries.registryOrThrow(Registries.ITEM).getKey(newItem);
        RegistryOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);
        return ItemStack.CODEC.encodeStart(ops, existing).map(itemTag -> {
            if (itemTag instanceof CompoundTag tag) {
                tag.putString("id", newStackId.toString());
                return ItemStack.CODEC.parse(ops, tag).result().orElse(ItemStack.EMPTY);
            }
            return ItemStack.EMPTY;
        }).result();
    }

    public static List<List<ItemEntity>> collectMergeableItems(List<ItemEntity> entities) {
        List<ItemEntity> nonEmpty = entities.stream()
                .filter(e -> !e.getItem().isEmpty())
                .toList();

        List<List<ItemEntity>> groups = new ArrayList<>();
        for (ItemEntity entity : nonEmpty) {
            boolean added = false;
            for (List<ItemEntity> group : groups) {
                if (ItemEntity.areMergable(group.getFirst().getItem(), entity.getItem())) {
                    group.add(entity);
                    added = true;
                    break;
                }
            }
            if (!added) {
                List<ItemEntity> newGroup = new ArrayList<>();
                newGroup.add(entity);
                groups.add(newGroup);
            }
        }

        return groups;
    }
}
