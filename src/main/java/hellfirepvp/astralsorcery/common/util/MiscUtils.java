/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import com.google.common.collect.Iterables;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Tuple;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import org.apache.logging.log4j.util.TriConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MiscUtils
 * Created by HellFirePvP
 * Date: 01.08.2016 / 13:38
 */
public class MiscUtils {

    @Nullable
    public static <T> T getTileAt(IBlockReader world, BlockPos pos, Class<T> tileClass, boolean forceChunkLoad) {
        if (world == null || pos == null) return null; //Duh.
        if (world instanceof IWorld) {
            if (!((IWorld) world).getChunkProvider().isChunkLoaded(new ChunkPos(pos)) && !forceChunkLoad) {
                return null;
            }
        }
        TileEntity te = world.getTileEntity(pos);
        if (te == null) return null;
        if (tileClass.isInstance(te)) return (T) te;
        return null;
    }

    public static boolean canEntityTickAt(IWorld world, BlockPos pos) {
        if (!world.getChunkProvider().isChunkLoaded(new ChunkPos(pos))) {
            return false;
        }
        BlockPos test = new BlockPos(pos.getX(), 0, pos.getZ());
        boolean isForced = false;
        if (world instanceof ServerWorld) {
            isForced = ((ServerWorld) world).getForcedChunks().contains(new ChunkPos(test).asLong());
        }
        int range = isForced ? 0 : 32;
        return world.isAreaLoaded(test, range);
    }

    @Nullable
    public static <T> T getRandomEntry(Collection<T> collection, Random rand) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        int index = rand.nextInt(collection.size());
        return Iterables.get(collection, index);
    }

    @Nullable
    public static <T> T getRandomEntry(T[] array, Random rand) {
        if (array == null || array.length <= 0) {
            return null;
        }
        return array[rand.nextInt(array.length)];
    }

    @Nullable
    public static ModContainer getCurrentlyActiveMod() {
        return ModLoadingContext.get().getActiveContainer();
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
        return values[MathHelper.clamp(index, 0, values.length - 1)];
    }

    @Nullable
    public static <T> T getWeightedRandomEntry(Collection<T> list, Random rand, Function<T, Integer> getWeightFunction) {
        if (list.isEmpty()) {
            return null;
        }
        List<WRItemObject<T>> weightedItems = new ArrayList<>(list.size());
        for (T e : list) {
            weightedItems.add(new WRItemObject<>(getWeightFunction.apply(e), e));
        }
        WRItemObject<T> item = WeightedRandom.getRandomItem(rand, weightedItems);
        return item != null ? item.getValue() : null;
    }

    public static <T, V extends Comparable<V>> V getMaxEntry(Collection<T> elements, Function<T, V> valueFunction) {
        V max = null;
        for (T element : elements) {
            V val = valueFunction.apply(element);
            if (max == null || max.compareTo(val) < 0) {
                max = val;
            }
        }
        return max;
    }

    public static boolean canSeeSky(World world, BlockPos at, boolean loadChunk, boolean defaultValue) {
        //if (world.getGameRules().getBoolean(GameRulesAS.IGNORE_SKYLIGHT_CHECK_RULE)) {
        //    return true;
        //}

        if (!world.getChunkProvider().isChunkLoaded(new ChunkPos(at)) && !loadChunk) {
            return defaultValue;
        }
        return world.canBlockSeeSky(at);
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

    public static <T> Supplier<T> nullSupplier(Runnable run) {
        return () -> {
            run.run();
            return null;
        };
    }

    public static <T, V> List<V> transformList(List<T> list, Function<T, V> map) {
        return list.stream().map(map).collect(Collectors.toList());
    }

    public static <T, V> Set<V> transformSet(Set<T> list, Function<T, V> map) {
        return list.stream().map(map).collect(Collectors.toSet());
    }

    public static <T, V> Collection<V> transformCollection(Collection<T> list, Function<T, V> map) {
        return list.stream().map(map).collect(Collectors.toList());
    }

    public static <K, V, N> Map<K, N> remap(Map<K, V> map, Function<V, N> remapFct) {
        return MapStream.of(map).mapValue(remapFct).toMap();
    }

    public static <T> void mergeList(Collection<T> src, List<T> dst) {
        for (T element : src) {
            if (!dst.contains(element)) {
                dst.add(element);
            }
        }
    }

    public static <T> void cutList(Collection<? extends T> toRemove, List<T> from) {
        for (T element : toRemove) {
            from.remove(element);
        }
    }

    public static <T> List<T> copyList(List<T> list) {
        List<T> l = new ArrayList<>(list.size());
        Collections.copy(l, list);
        return l;
    }

    public static <T> Set<T> copySet(Set<T> set) {
        Set<T> s = new HashSet<>(set.size());
        s.addAll(set);
        return s;
    }

    @Nullable
    public static <T> T iterativeSearch(Collection<T> collection, Predicate<T> matchingFct) {
        for (T element : collection) {
            if (matchingFct.test(element)) {
                return element;
            }
        }
        return null;
    }

    public static <T> boolean contains(Collection<T> collection, Predicate<T>  matchingFct) {
        return iterativeSearch(collection, matchingFct) != null;
    }

    public static <T> boolean matchesAny(T element, Collection<Predicate<T>> tests) {
        for (Predicate<T> test : tests) {
            if (test.test(element)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFluidBlock(BlockState state) {
        return state.getBlock() instanceof FlowingFluidBlock;
    }

    @Nullable
    public static Fluid tryGetFuild(BlockState state) {
        if (!isFluidBlock(state)) {
            return null;
        }
        if (state.getBlock() instanceof FlowingFluidBlock) {
            IFluidState fluidState = state.getBlock().getFluidState(state);
            if (!fluidState.isEmpty()) {
                return fluidState.getFluid();
            }
        }
        return null;
    }

    public static boolean canPlayerAttackServer(@Nullable LivingEntity source, @Nonnull LivingEntity target) {
        if (!target.isAlive()) {
            return false;
        }
        if (target instanceof PlayerEntity) {
            PlayerEntity plTarget = (PlayerEntity) target;
            if (target.getEntityWorld() instanceof ServerWorld &&
                    target.getEntityWorld().getServer() != null &&
                    target.getEntityWorld().getServer().isPVPEnabled()) {
                return false;
            }
            if (plTarget.isSpectator() || plTarget.isCreative()) {
                return false;
            }
            if (source instanceof PlayerEntity &&
                    !((PlayerEntity) source).canAttackPlayer(plTarget)) {
                return false;
            }
        }
        return true;
    }

    public static boolean canPlayerBreakBlockPos(PlayerEntity player, BlockPos tryBreak) {
        BlockEvent.BreakEvent ev = new BlockEvent.BreakEvent(player.getEntityWorld(), tryBreak, player.getEntityWorld().getBlockState(tryBreak), player);
        MinecraftForge.EVENT_BUS.post(ev);
        return !ev.isCanceled();
    }

    public static boolean canPlayerPlaceBlockPos(PlayerEntity player, BlockState tryPlace, BlockPos pos, Direction againstSide) {
        BlockSnapshot snapshot = new BlockSnapshot(player.getEntityWorld(), pos, tryPlace);
        return !ForgeEventFactory.onBlockPlace(player, snapshot, againstSide);
    }

    public static boolean isConnectionEstablished(ServerPlayerEntity player) {
        return player.connection != null && player.connection.netManager != null && player.connection.netManager.isChannelOpen();
    }

    public static long getRandomWorldSeed(IWorld world) {
        return new Random(world.getSeed()).nextLong();
    }

    @Nullable
    public static Tuple<Hand, ItemStack> getMainOrOffHand(LivingEntity entity, Item search) {
        return getMainOrOffHand(entity, stack -> !stack.isEmpty() && stack.getItem().equals(search));
    }

    @Nullable
    public static Tuple<Hand, ItemStack> getMainOrOffHand(LivingEntity entity, Predicate<ItemStack> acceptorFnc) {
        Hand hand = Hand.MAIN_HAND;
        ItemStack held = entity.getHeldItem(hand);
        if (held.isEmpty() || !acceptorFnc.test(held)) {
            hand = Hand.OFF_HAND;
            held = entity.getHeldItem(hand);
        }
        if (held.isEmpty() || !acceptorFnc.test(held)) {
            return null;
        }
        return new Tuple<>(hand, held);
    }

    public static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toTitleCase(str.charAt(0)) + str.substring(1);
    }

    @Nullable
    public static <T extends Entity> T transferEntityTo(T entity, DimensionType target, BlockPos targetPos) {
        if (entity.getEntityWorld().isRemote) {
            return null; //No transfers on clientside.
        }
        entity.setSneaking(false);
        Dimension dimFrom = entity.getEntityWorld().getDimension();
        if (dimFrom.getType() != target) {
            if (!ForgeHooks.onTravelToDimension(entity, target)) {
                return null;
            }

            if (entity instanceof ServerPlayerEntity) {
                MinecraftServer srv = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
                ServerWorld targetWorld = srv.getWorld(target);
                ((ServerPlayerEntity) entity).teleport(targetWorld,
                        targetPos.getX() + 0.5,
                        targetPos.getY() + 0.1,
                        targetPos.getZ() + 0.5,
                        entity.rotationYaw,
                        entity.rotationPitch);
            } else {
                entity = (T) entity.changeDimension(target);
                if (entity == null) {
                    return null;
                }
            }
        }
        entity.setPositionAndUpdate(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
        return entity;
    }

    @Nullable
    public static BlockPos itDownTopBlock(World world, BlockPos at) {
        IChunk chunk = world.getChunk(at);
        BlockPos downPos = null;

        for (BlockPos blockpos = new BlockPos(at.getX(), chunk.getTopFilledSegment() + 16, at.getZ()); blockpos.getY() >= 0; blockpos = downPos) {
            downPos = blockpos.down();
            BlockState test = world.getBlockState(downPos);
            IFluidState state = test.getFluidState();
            if (!world.isAirBlock(downPos) && !test.isIn(BlockTags.LEAVES) && !test.isFoliage(world, downPos)) {
                break;
            }
        }

        return downPos;
    }

    public static List<Vector3> getCirclePositions(Vector3 centerOffset, Vector3 axis, double radius, int amountOfPointsOnCircle) {
        List<Vector3> out = new LinkedList<>();
        Vector3 circleVec = axis.clone().perpendicular().normalize().multiply(radius);
        double degPerPoint = 360D / ((double) amountOfPointsOnCircle);
        for (int i = 0; i < amountOfPointsOnCircle; i++) {
            double deg = i * degPerPoint;
            out.add(circleVec.clone().rotate(Math.toRadians(deg), axis.clone()).add(centerOffset));
        }
        return out;
    }

    @Nullable
    public static BlockRayTraceResult rayTraceLookBlock(PlayerEntity player) {
        return rayTraceLookBlock(player, player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue());
    }

    @Nonnull
    public static RayTraceResult rayTraceLook(PlayerEntity player) {
        return rayTraceLook(player, player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue());
    }

    @Nullable
    public static BlockRayTraceResult rayTraceLookBlock(PlayerEntity player, RayTraceContext.BlockMode blockMode, RayTraceContext.FluidMode fluidMode) {
        return rayTraceLookBlock(player, blockMode, fluidMode, player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue());
    }

    @Nonnull
    public static RayTraceResult rayTraceLook(PlayerEntity player, RayTraceContext.BlockMode blockMode, RayTraceContext.FluidMode fluidMode) {
        return rayTraceLook(player, blockMode, fluidMode, player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue());
    }

    @Nullable
    public static BlockRayTraceResult rayTraceLookBlock(PlayerEntity player, double reachDst) {
        return rayTraceLookBlock(player, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, reachDst);
    }

    @Nonnull
    public static RayTraceResult rayTraceLook(PlayerEntity player, double reachDst) {
        return rayTraceLook(player, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, reachDst);
    }

    @Nullable
    public static BlockRayTraceResult rayTraceLookBlock(Entity entity, RayTraceContext.BlockMode blockMode, RayTraceContext.FluidMode fluidMode, double reachDst) {
        RayTraceResult rtr = rayTraceLook(entity, blockMode, fluidMode, reachDst);
        if (rtr.getType() == RayTraceResult.Type.BLOCK && rtr instanceof BlockRayTraceResult) {
            return (BlockRayTraceResult) rtr;
        }
        return null;
    }

    @Nonnull
    public static RayTraceResult rayTraceLook(Entity entity, RayTraceContext.BlockMode blockMode, RayTraceContext.FluidMode fluidMode, double reachDst) {
        Vec3d pos = new Vec3d(entity.getPosX(), entity.getPosY() + entity.getEyeHeight(), entity.getPosZ());
        Vec3d lookVec = entity.getLookVec();
        Vec3d end = pos.add(lookVec.x * reachDst, lookVec.y * reachDst, lookVec.z * reachDst);
        RayTraceContext ctx = new RayTraceContext(pos, end, blockMode, fluidMode, entity);
        return entity.world.rayTraceBlocks(ctx);
    }

    public static Color calcRandomConstellationColor(float perc) {
        return new Color(Color.HSBtoRGB((230F + (50F * perc)) / 360F, 0.8F, 0.8F - (0.3F * perc)));
    }

    public static void applyRandomOffset(Vector3 target, Random rand) {
        applyRandomOffset(target, rand, 1F);
    }

    public static void applyRandomOffset(Vector3 target, Random rand, float multiplier) {
        target.addX(rand.nextFloat() * multiplier * (rand.nextBoolean() ? 1 : -1));
        target.addY(rand.nextFloat() * multiplier * (rand.nextBoolean() ? 1 : -1));
        target.addZ(rand.nextFloat() * multiplier * (rand.nextBoolean() ? 1 : -1));
    }

    public static void executeWithChunk(IWorldReader world, ChunkPos pos, Runnable run) {
        executeWithChunk(world, pos.asBlockPos(), nullSupplier(run));
    }

    public static void executeWithChunk(IWorldReader world, BlockPos pos, Runnable run) {
        executeWithChunk(world, pos, nullSupplier(run));
    }

    public static <T> T executeWithChunk(IWorldReader world, BlockPos pos, Supplier<T> run) {
        return executeWithChunk(world, pos, run, (T) null);
    }

    public static <T> T executeWithChunk(IWorldReader world, BlockPos pos, Supplier<T> run, T defaultValue) {
        if (world instanceof ServerWorld && LogCategory.UNINTENDED_CHUNK_LOADING.isEnabled()) {
            ServerChunkProvider provider = ((ServerWorld) world).getChunkProvider();
            int prev = provider.getLoadedChunkCount();
            try {
                if (provider.isChunkLoaded(new ChunkPos(pos))) {
                    return run.get();
                }
            } finally {
                int current = ((ServerWorld) world).getChunkProvider().getLoadedChunkCount();
                if (current > prev) { //We... don't really care about unloading tbh.
                    AstralSorcery.log.warn("Astral Sorcery loaded a chunk when it intended not to!");
                    AstralSorcery.log.warn("Previous chunk count: " + prev);
                    AstralSorcery.log.warn("Current chunk count: " + current);
                    AstralSorcery.log.warn("Loaded " + (current - prev) + " chunks!");
                    AstralSorcery.log.warn("Stacktrace:", new Exception());
                }
            }
        } else if (world instanceof IWorld) {
            AbstractChunkProvider provider = ((IWorld) world).getChunkProvider();
            if (provider.isChunkLoaded(new ChunkPos(pos))) {
                return run.get();
            }
        } else {
            if (world.isBlockLoaded(pos)) {
                return run.get();
            }
        }
        return defaultValue;
    }

    public static <T> void executeWithChunk(IWorldReader world, BlockPos pos, T obj, Consumer<T> run) {
        executeWithChunk(world, pos, nullSupplier(apply(run, () -> obj)));
    }

    public static <T, U> void executeWithChunk(IWorldReader world, BlockPos pos, T obj, U obj1, BiConsumer<T, U> run) {
        executeWithChunk(world, pos, obj, apply(run, () -> obj1));
    }

    public static <T, R> R executeWithChunk(IWorldReader world, BlockPos pos, T obj, Function<T, R> run) {
        return executeWithChunk(world, pos, apply(run, () -> obj));
    }

    public static <T, R> R executeWithChunk(IWorldReader world, BlockPos pos, T obj, Function<T, R> run, R _default) {
        return executeWithChunk(world, pos, apply(run, () -> obj), _default);
    }

    public static <T> T eitherOf(Random r, T... selection) {
        if (selection.length == 0) {
            return null;
        }
        return selection[r.nextInt(selection.length)];
    }

    public static <T> T eitherOf(Random r, Supplier<T>... selection) {
        if (selection.length == 0) {
            return null;
        }
        return selection[r.nextInt(selection.length)].get();
    }

    public static boolean isPlayerFakeMP(ServerPlayerEntity player) {
        if (player instanceof FakePlayer) {
            return true;
        }

        boolean isModdedPlayer = false;
        for (Mods mod : Mods.values()) {
            if (!mod.isPresent()) {
                continue;
            }
            Class<?> specificPlayerClass = mod.getExtendedPlayerClass();
            if (specificPlayerClass != null) {
                if (player.getClass() != ServerPlayerEntity.class && player.getClass() == specificPlayerClass) {
                    isModdedPlayer = true;
                    break;
                }
            }
        }
        if (!isModdedPlayer && player.getClass() != ServerPlayerEntity.class) {
            return true;
        }

        if (player.connection == null) {
            return true;
        }
        try {
            player.getPlayerIP().length();
            player.connection.netManager.getRemoteAddress().toString();
        } catch (Exception exc) {
            return true;
        }
        return false;
    }

}
