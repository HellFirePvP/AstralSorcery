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
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.BlockSnapshot;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BlockUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class BlockUtil {

    public static List<ItemStack> getDrops(ServerLevel sLevel, BlockPos pos, int fortuneLevel) {
        return getDrops(sLevel, pos, fortuneLevel, new ItemStack(Items.STICK));
    }

    public static List<ItemStack> getDrops(ServerLevel sLevel, BlockPos pos, int fortuneLevel, ItemStack stack) {
        return getDrops(sLevel, sLevel.getBlockState(pos), pos, fortuneLevel, stack);
    }

    public static List<ItemStack> getDrops(ServerLevel sLevel, BlockState state, BlockPos pos, int fortuneLevel) {
        return getDrops(sLevel, state, pos, fortuneLevel, new ItemStack(Items.STICK));
    }

    public static List<ItemStack> getDrops(ServerLevel sLevel, BlockState state, BlockPos pos, int fortuneLevel, ItemStack stack) {
        stack = stack.copy();
        EnchantmentHelper.updateEnchantments(stack, mutable -> {
            mutable.set(sLevel.registryAccess().holderOrThrow(Enchantments.FORTUNE), fortuneLevel);
        });
        try {
            return Block.getDrops(state, sLevel, pos, sLevel.getBlockEntity(pos), null, stack);
        } catch (Exception exc) {
            return List.of();
        }
    }

    public static boolean isReplaceable(Level level, BlockPos pos) {
        return isReplaceable(level, pos, level.getBlockState(pos));
    }

    public static boolean isReplaceable(Level level, BlockPos pos, BlockState state) {
        if (level.isEmptyBlock(pos)) return true;

        BlockPlaceContext ctx = TestBlockUseContext.getHandContext(level, null, InteractionHand.MAIN_HAND, pos, Direction.UP);
        return state.canBeReplaced(ctx);
    }

    public static boolean isLiquidBlock(BlockState state) {
        return state.getBlock() instanceof LiquidBlock;
    }

    public static <T> LevelChanges<T> captureLevelChanges(Level level, Supplier<ChangeResult<T>> fn) {
        boolean wasCapturingBlockStates = level.captureBlockSnapshots;
        List<BlockSnapshot> previousCapturedStates = new ArrayList<>(level.capturedBlockSnapshots);

        level.capturedBlockSnapshots.clear();
        level.captureBlockSnapshots = true;

        ChangeResult<T> result = fn.get();
        List<BlockSnapshot> capturedSnapshots = new ArrayList<>(level.capturedBlockSnapshots.size());
        ResourceKey<Level> dimKey = level.dimension();
        level.capturedBlockSnapshots.stream()
                .map(BlockSnapshot::getPos)
                .collect(Collectors.toSet())
                .forEach(pos -> capturedSnapshots.add(BlockSnapshot.create(dimKey, level, pos)));

        if (result.revert) {
            restoreWorldState(level, wasCapturingBlockStates, previousCapturedStates);
        } else {
            applyWorldState(level, wasCapturingBlockStates, previousCapturedStates);
        }
        return new LevelChanges<>(capturedSnapshots, result.value);
    }

    private static void applyWorldState(Level level, boolean wasCapturingBlockStates, List<BlockSnapshot> previousCapturedStates) {
        level.capturedBlockSnapshots.forEach(snapshot -> {
            int flags = snapshot.getFlags();
            BlockState previous = snapshot.getState();
            BlockState newState = level.getBlockState(snapshot.getPos());

            newState.onPlace(level, snapshot.getPos(), previous, false);
            level.markAndNotifyBlock(snapshot.getPos(), level.getChunkAt(snapshot.getPos()), previous, newState, flags, Block.UPDATE_LIMIT);
        });

        level.captureBlockSnapshots = wasCapturingBlockStates;
        level.capturedBlockSnapshots.clear();
        level.capturedBlockSnapshots.addAll(previousCapturedStates);
    }

    private static void restoreWorldState(Level level, boolean wasCapturingBlockStates, List<BlockSnapshot> previousCapturedStates) {
        level.captureBlockSnapshots = false;

        level.restoringBlockSnapshots = true;
        level.capturedBlockSnapshots.forEach(snapshot -> snapshot.restore(Block.UPDATE_ALL));
        level.capturedBlockSnapshots.clear();
        level.restoringBlockSnapshots = false;

        level.captureBlockSnapshots = wasCapturingBlockStates;
        level.capturedBlockSnapshots.addAll(previousCapturedStates);
    }

    public static class ChangeResult<T> {

        @Nullable
        private final T value;
        private final boolean revert;

        private ChangeResult(@Nullable T value, boolean revert) {
            this.value = value;
            this.revert = revert;
        }

        public static ChangeResult<Void> apply() {
            return new ChangeResult<>(null, false);
        }

        public static <T> ChangeResult<T> apply(T value) {
            return new ChangeResult<>(value, false);
        }

        public static ChangeResult<Void> revert() {
            return new ChangeResult<>(null, true);
        }

        public static <T> ChangeResult<T> revert(T value) {
            return new ChangeResult<>(value, true);
        }
    }

    public record LevelChanges<T>(List<BlockSnapshot> capturedChanges, T value) {}

    public static class TestBlockUseContext extends BlockPlaceContext {

        private final Entity entity;

        private TestBlockUseContext(Level worldIn, @Nullable Entity usingEntity, InteractionHand hand, ItemStack stack, BlockPos at, Direction side) {
            super(worldIn, null, hand, stack, new BlockHitResult(Vec3.atCenterOf(at), side, at, false));
            this.entity = usingEntity;
        }

        public static BlockPlaceContext getHandContext(Level worldIn, @Nullable Entity usingEntity, InteractionHand usedHand, BlockPos at, Direction side) {
            return getHandContextWithItem(worldIn, usingEntity, usedHand, ItemStack.EMPTY, at, side);
        }

        public static BlockPlaceContext getHandContextWithItem(Level worldIn, @Nullable Entity usingEntity, InteractionHand usedHand, ItemStack stack, BlockPos at, Direction side) {
            return new TestBlockUseContext(worldIn, usingEntity, usedHand, stack, at, side);
        }

        @Override
        public Direction getHorizontalDirection() {
            return this.entity == null ? Direction.NORTH : Direction.fromYRot(this.entity.getYRot());
        }

        @Override
        public Direction getNearestLookingDirection() {
            return Direction.orderedByNearest(this.entity)[0];
        }

        @Override
        public Direction[] getNearestLookingDirections() {
            Direction[] adirection = Direction.orderedByNearest(this.entity);
            if (this.replaceClicked) {
                return adirection;
            } else {
                Direction direction = this.getClickedFace();

                int i;
                i = 0;
                while (i < adirection.length && adirection[i] != direction.getOpposite()) {
                    ++i;
                }

                if (i > 0) {
                    System.arraycopy(adirection, 0, adirection, 1, i);
                    adirection[0] = direction.getOpposite();
                }
                return adirection;
            }
        }

        @Override
        public boolean isSecondaryUseActive() {
            return false;
        }

        @Override
        public float getRotation() {
            return 0F;
        }
    }
}
