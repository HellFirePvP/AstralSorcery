/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import org.apache.logging.log4j.util.TriConsumer;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MiscUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class MiscUtil {

    public static boolean getTileExists(BlockGetter level, BlockPos pos, Class<?> tileClass, boolean forceLoad) {
        return getTileAt(level, pos, tileClass, forceLoad).isPresent();
    }

    public static <T> Optional<T> getTileAt(BlockGetter level, BlockPos pos, Class<T> tileClass, boolean forceLoad) {
        if (level == null || pos == null) return Optional.empty(); //Duh.
        if (!forceLoad &&
                level instanceof LevelAccessor levelAccessor &&
                !ChunkUtil.isChunkLoaded(levelAccessor, pos)) {
            return Optional.empty();
        }
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile == null) return Optional.empty();
        if (tileClass.isInstance(tile)) return Optional.of(cast(tile));
        return Optional.empty();
    }

    public static Optional<BlockPos> iterateDown(BlockGetter blockGetter, BlockPos pos, Predicate<BlockPos> filter) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        mutable.set(pos);

        while (mutable.getY() >= blockGetter.getMinBuildHeight()) {
            if (filter.test(mutable)) {
                return Optional.of(mutable.immutable());
            }
            mutable.move(0, -1, 0);
        }
        return Optional.empty();
    }

    public static Optional<BlockPos> iterateTopDown(BlockGetter blockGetter, BlockPos column, Predicate<BlockPos> filter) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        pos.set(column);
        pos.setY(blockGetter.getMaxBuildHeight());

        while (pos.getY() >= blockGetter.getMinBuildHeight()) {
            if (filter.test(pos)) {
                return Optional.of(pos.immutable());
            }
            pos.move(0, -1, 0);
        }
        return Optional.empty();
    }

    // Catchall to fix generic type erasure issues
    public static <X, Y> X cast(Y value) {
        return (X) value;
    }

    public static long getBlockPosSeed(BlockPos pos) {
        long hash = pos.getX() * 3129871L ^ pos.getY() * 116129781L ^ pos.getZ() * 4392871L;
        hash = hash * hash * 42317861L + hash * 11L;
        return hash;
    }

    public static int roundChanced(float value, RandomSource rand) {
        int rounded = Mth.floor(value);
        if (rand.nextFloat() < (value - rounded)) {
            rounded += 1;
        }
        return rounded;
    }

    public static int roundChanced(double value, RandomSource rand) {
        int rounded = Mth.floor(value);
        if (rand.nextFloat() < (value - rounded)) {
            rounded += 1;
        }
        return rounded;
    }

    @Nonnull
    public static <T> T getEnumEntry(Class<T> enumClazz, int index) {
        if (!enumClazz.isEnum()) {
            throw new IllegalArgumentException("Called getEnumEntry on class " + enumClazz.getName() + " which isn't an enum.");
        }
        T[] values = enumClazz.getEnumConstants();
        if (values.length == 0) {
            throw new IllegalArgumentException(enumClazz.getName() + " has no enum constants.");
        }
        return values[Mth.clamp(index, 0, values.length - 1)];
    }

    public static <T> void copy(NonNullList<T> target, NonNullList<T> from) {
        int copySize = Math.min(target.size(), from.size());
        for (int i = 0; i < copySize; i++) {
            target.set(i, from.get(i));
        }
    }

    public static <T> void shuffle(List<T> list) {
        shuffle(list, RandomSource.create());
    }

    public static <T> void shuffle(List<T> list, RandomSource rand) {
        int length = list.size();
        for (int i = length - 1; i > 0; i--) {
            int newIndex = rand.nextInt(i);

            T value = list.get(i);
            list.set(i, list.get(newIndex));
            list.set(newIndex, value);
        }
    }

    public static <R, L extends List<R>> Collector<R, ?, L> collectShuffledList(Supplier<L> collection) {
        return Collectors.collectingAndThen(
                Collectors.toCollection(collection),
                list -> {
                    shuffle(list);
                    return list;
                });
    }

    public static <T> Optional<T> getRandomEntry(RandomSource rand, T... entries) {
        if (entries == null || entries.length == 0) {
            return Optional.empty();
        }
        int index = rand.nextInt(entries.length);
        return Optional.ofNullable(entries[index]);
    }

    public static <T> Optional<T> getRandomEntry(Collection<T> collection, RandomSource rand) {
        if (collection == null || collection.isEmpty()) {
            return Optional.empty();
        }
        int index = rand.nextInt(collection.size());
        return Optional.ofNullable(Iterables.get(collection, index));
    }

    public static <T> Optional<T> getWeightedRandomEntry(Collection<T> list, RandomSource rand, Function<T, Integer> getWeightFunction) {
        if (list.isEmpty()) return Optional.empty();

        List<WeightedEntry.Wrapper<T>> entries = list.stream()
                .map(e -> WeightedEntry.wrap(e, getWeightFunction.apply(e)))
                .toList();
        return WeightedRandom.getRandomItem(rand, entries).map(WeightedEntry.Wrapper::data);
    }

    public static boolean canSeeSky(Level level, BlockPos pos, boolean loadChunk, boolean defaultValue) {
        return canSeeSky(level, pos, loadChunk, false, defaultValue);
    }

    public static boolean canSeeSky(Level level, BlockPos pos, boolean loadChunk, boolean allowInNoSkyWorlds, boolean defaultValue) {
        //TODO sky check gamerule

        if (!level.dimensionType().hasSkyLight()) {
            return allowInNoSkyWorlds;
        }
        if (!loadChunk) {
            return ChunkUtil.executeWithChunk(level, pos, () -> {
                return level.canSeeSky(pos);
            }, defaultValue);
        }
        return level.canSeeSky(pos);
    }

    public static Optional<Tuple<InteractionHand, ItemStack>> getMainOrOffHand(LivingEntity entity, Predicate<ItemStack> acceptorFnc) {
        InteractionHand hand = InteractionHand.MAIN_HAND;
        ItemStack held = entity.getItemInHand(hand);
        if (held.isEmpty() || !acceptorFnc.test(held)) {
            hand = InteractionHand.OFF_HAND;
            held = entity.getItemInHand(hand);
        }
        if (held.isEmpty() || !acceptorFnc.test(held)) {
            return Optional.empty();
        }
        return Optional.of(new Tuple<>(hand, held));
    }

    public static MutableComponent getBlockStateDisplayName(Level level, Player player, BlockPos pos) {
        return getBlockStateDisplayName(level, player, pos, BlockHitResult.miss(Vec3.atCenterOf(pos), Direction.DOWN, pos));
    }

    public static MutableComponent getBlockStateDisplayName(Level level, Player player, BlockPos pos, BlockHitResult hitResult) {
        BlockState state = level.getBlockState(pos);
        try {
            ItemStack picked = state.getCloneItemStack(hitResult, level, pos, player);
            Block pickedBlock = Block.byItem(picked.getItem());
            if (pickedBlock != Blocks.AIR && state.is(pickedBlock)) { //Bi-directional resolvable
                return Component.empty().append(picked.getHoverName());
            }
        } catch (Exception ignored) {
            // Catchall in case pick-block with a invalid hit result goes wrong
        }
        return state.getBlock().getName();
    }

    public static boolean isPlayerFake(ServerPlayer player) {
        if (player instanceof FakePlayer) {
            return true;
        }
        if (player.connection == null) {
            return true;
        }
        try {
            player.connection.getConnection().getRemoteAddress().toString();
        } catch (Exception exc) {
            return true;
        }
        return false;
    }

    public static ServerPlayer getAstralFakePlayer(ServerLevel sLevel) {
        GameProfile fakePlayerProfile = new GameProfile(UUID.fromString("9af7f161-0186-41d4-a590-976a9996fce5"), "AS-FakePlayer");
        return FakePlayerFactory.get(sLevel, fakePlayerProfile);
    }

    public static <T> T safeGetConfig(ModConfigSpec.ConfigValue<T> cfg, T defaultValue) {
        try {
            return cfg.get();
        } catch (Exception exc) {
            return defaultValue;
        }
    }

    public static <T> Optional<T> firstNonNull(Supplier<T>... suppliers) {
        for (Supplier<T> supplier : suppliers) {
            T value = supplier.get();
            if (value != null) return Optional.of(value);
        }
        return Optional.empty();
    }

    public static void schedule(MinecraftServer srv, Runnable runnable) {
        if (srv.isStopped()) {
            runnable.run();
        } else {
            srv.tell(new TickTask(0, runnable));
        }
    }

    public static <T, V> Function<T, V> nullFunction(Runnable run) {
        return nullFunction((v) -> run.run());
    }

    public static <T, V> Function<T, V> nullFunction(Consumer<T> run) {
        return (t) -> {
            run.accept(t);
            return null;
        };
    }

    public static <T> Supplier<T> nullSupplier(Runnable run) {
        return () -> {
            run.run();
            return null;
        };
    }

    public static <T> Runnable apply(Consumer<T> func, Supplier<T> supply) {
        return () -> func.accept(supply.get());
    }

    public static <T, U> Consumer<T> apply(BiConsumer<T, U> func, Supplier<U> supply) {
        return (t) -> func.accept(t, supply.get());
    }

    public static <T, U, V> BiConsumer<T, U> apply(TriConsumer<T, U, V> func, Supplier<V> supply) {
        return (t, u) -> func.accept(t, u, supply.get());
    }

    public static <T, R> Supplier<R> apply(Function<T, R> func, Supplier<T> supply) {
        return () -> func.apply(supply.get());
    }

    public static <T, P, R> Function<P, R> apply(BiFunction<T, P, R> func, Supplier<T> supply) {
        return p -> func.apply(supply.get(), p);
    }
}
