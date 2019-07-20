/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.GameRulesAS;
import hellfirepvp.astralsorcery.common.util.block.BlockPredicate;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Tuple;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
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

    private static Map<DyeColor, Color> prettierColorMapping = new HashMap<>();

    @Nullable
    public static <T> T getTileAt(IWorldReader world, BlockPos pos, Class<T> tileClass, boolean forceChunkLoad) {
        if(world == null || pos == null) return null; //Duh.
        if(world instanceof World) {
            if (!world.isBlockLoaded(pos) && !forceChunkLoad) return null;
        }
        TileEntity te = world.getTileEntity(pos);
        if(te == null) return null;
        if(tileClass.isInstance(te)) return (T) te;
        return null;
    }

    public static boolean canEntityTickAt(World world, BlockPos pos) {
        if (!isChunkLoaded(world, pos)) {
            return false;
        }
        BlockPos test = new BlockPos(pos.getX(), 0, pos.getZ());
        return world.isAreaLoaded(test, 0);
    }

    @Nullable
    public static <T> T getRandomEntry(List<T> list, Random rand) {
        if(list == null || list.isEmpty()) return null;
        return list.get(rand.nextInt(list.size()));
    }

    @Nullable
    public static ModContainer getCurrentlyActiveMod() {
        return ModLoadingContext.get().getActiveContainer();
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
            if(max == null || max.compareTo(val) < 0) {
                max = val;
            }
        }
        return max;
    }

    public static boolean canSeeSky(World world, BlockPos at, boolean loadChunk, boolean defaultValue) {
        if (world.getGameRules().getBoolean(GameRulesAS.IGNORE_SKYLIGHT_CHECK_RULE)) {
            return true;
        }

        if (!isChunkLoaded(world, at) && !loadChunk) {
            return defaultValue;
        }
        return world.canBlockSeeSky(at);
    }

    public static <K, V, N> Map<K, N> remap(Map<K, V> map, Function<V, N> remapFct) {
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, (e) -> remapFct.apply(e.getValue())));
    }

    public static <T, K, V> List<T> flatten(Map<K, V> map, BiFunction<K, V, T> flatFunction) {
        return map.entrySet()
                .stream()
                .map((entry) -> flatFunction.apply(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public static <T> List<T> flatList(Collection<List<T>> listCollection) {
        return listCollection.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public static <K, V, L> Map<K, V> splitMap(Collection<L> col, Function<L, Tuple<K, V>> split) {
        Map<K, V> map = new HashMap<>();
        col.forEach(l -> {
            Tuple<K, V> result = split.apply(l);
            map.put(result.getA(), result.getB());
        });
        return map;
    }

    public static <T> void mergeList(Collection<T> src, List<T> dst) {
        for (T element : src) {
            if (!dst.contains(element)) {
                dst.add(element);
            }
        }
    }

    public static <T> void cutList(List<T> toRemove, List<T> from) {
        for (T element : toRemove) {
            if (from.contains(element)) {
                from.remove(element);
            }
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
            if(matchingFct.test(element)) {
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

    public static boolean canPlayerPlaceBlockPos(PlayerEntity player, Hand withHand, BlockState tryPlace, BlockPos pos, Direction againstSide) {
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
        return getMainOrOffHand(entity, search, null);
    }

    @Nullable
    public static Tuple<Hand, ItemStack> getMainOrOffHand(LivingEntity entity, Item search, @Nullable Predicate<ItemStack> acceptorFnc) {
        Hand hand = Hand.MAIN_HAND;
        ItemStack held = entity.getHeldItem(hand);
        if (held.isEmpty() || !search.getClass().isAssignableFrom(held.getItem().getClass()) || (acceptorFnc != null && !acceptorFnc.test(held))) {
            hand = Hand.OFF_HAND;
            held = entity.getHeldItem(hand);
        }
        if (held.isEmpty() || !search.getClass().isAssignableFrom(held.getItem().getClass()) || (acceptorFnc != null && !acceptorFnc.test(held))) {
            return null;
        }
        return new Tuple<>(hand, held);
    }

    @Nonnull
    public static Color flareColorFromDye(DyeColor color) {
        Color c = prettierColorMapping.get(color);
        if(c == null) c = Color.WHITE;
        return c;
    }

    @Nonnull
    public static TextFormatting textFormattingForDye(DyeColor color) {
        switch (color) {
            case WHITE:
                return TextFormatting.WHITE;
            case ORANGE:
                return TextFormatting.GOLD;
            case MAGENTA:
                return TextFormatting.DARK_PURPLE;
            case LIGHT_BLUE:
                return TextFormatting.DARK_AQUA;
            case YELLOW:
                return TextFormatting.YELLOW;
            case LIME:
                return TextFormatting.GREEN;
            case PINK:
                return TextFormatting.LIGHT_PURPLE;
            case GRAY:
                return TextFormatting.DARK_GRAY;
            case LIGHT_GRAY:
                return TextFormatting.GRAY;
            case CYAN:
                return TextFormatting.BLUE;
            case PURPLE:
                return TextFormatting.DARK_PURPLE;
            case BLUE:
                return TextFormatting.DARK_BLUE;
            case BROWN:
                return TextFormatting.GOLD;
            case GREEN:
                return TextFormatting.DARK_GREEN;
            case RED:
                return TextFormatting.DARK_RED;
            case BLACK:
                return TextFormatting.DARK_GRAY; //Black is unreadable. fck that.
            default:
                return TextFormatting.WHITE;
        }
    }

    public static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toTitleCase(str.charAt(0)) + str.substring(1);
    }

    public static void transferEntityTo(Entity entity, DimensionType target, BlockPos targetPos) {
        if (entity.getEntityWorld().isRemote) return; //No transfers on clientside.
        entity.setSneaking(false);
        Dimension dimFrom = entity.getEntityWorld().getDimension();
        if (dimFrom.getType() != target) {
            if (!ForgeHooks.onTravelToDimension(entity, target)) {
                return;
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
                entity.changeDimension(target);
            }
        }
        entity.setPositionAndUpdate(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
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
    public static RayTraceResult rayTraceLook(PlayerEntity player) {
        return rayTraceLook(player, player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue());
    }

    @Nullable
    public static RayTraceResult rayTraceLook(LivingEntity entity, double reachDst) {
        Vec3d pos = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        Vec3d lookVec = entity.getLookVec();
        Vec3d end = pos.add(lookVec.x * reachDst, lookVec.y * reachDst, lookVec.z * reachDst);
        RayTraceContext ctx = new RayTraceContext(pos, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, entity);
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

    public static boolean isChunkLoaded(World world, BlockPos pos) {
        return world.isBlockLoaded(pos);
    }

    public static boolean isChunkLoaded(World world, ChunkPos pos) {
        return world.isBlockLoaded(new BlockPos(pos.x * 16, 0, pos.z * 16));
    }

    public static boolean isPlayerFakeMP(ServerPlayerEntity player) {
        if(player instanceof FakePlayer) return true;

        boolean isModdedPlayer = false;
        for (Mods mod : Mods.values()) {
            if(!mod.isPresent()) {
                continue;
            }
            Class<?> specificPlayerClass = mod.getExtendedPlayerClass();
            if(specificPlayerClass != null) {
                if(player.getClass() != ServerPlayerEntity.class && player.getClass() == specificPlayerClass) {
                    isModdedPlayer = true;
                    break;
                }
            }
        }
        if(!isModdedPlayer && player.getClass() != ServerPlayerEntity.class) {
            return true;
        }

        if(player.connection == null) return true;
        try {
            player.getPlayerIP().length();
            player.connection.netManager.getRemoteAddress().toString();
        } catch (Exception exc) {
            return true;
        }
        return false;
    }

    @Nullable
    public static BlockPos searchAreaForFirst(World world, BlockPos center, int radius, @Nullable Vector3 offsetFrom, BlockPredicate acceptor) {
        for (int r = 0; r <= radius; r++) {
            List<BlockPos> posList = new LinkedList<>();
            for (int xx = -r; xx <= r; xx++) {
                for (int yy = -r; yy <= r; yy++) {
                    for (int zz = -r; zz <= r; zz++) {

                        BlockPos pos = center.add(xx, yy, zz);
                        if(isChunkLoaded(world, new ChunkPos(pos))) {
                            BlockState state = world.getBlockState(pos);
                            if(acceptor.test(world, pos, state)) {
                                posList.add(pos);
                            }
                        }
                    }
                }
            }
            if(!posList.isEmpty()) {
                Vector3 offset = new Vector3(center).add(0.5, 0.5, 0.5);
                if(offsetFrom != null) {
                    offset = offsetFrom;
                }
                BlockPos closest = null;
                double prevDst = 0;
                for (BlockPos pos : posList) {
                    if(closest == null || offset.distance(pos) < prevDst) {
                         closest = pos;
                         prevDst = offset.distance(pos);
                    }
                }
                return closest;
            }
            posList.clear();
        }
        return null;
    }

    public static List<BlockPos> searchAreaFor(World world, BlockPos center, BlockState blockToSearch, int radius) {
        List<BlockPos> found = new LinkedList<>();
        for (int xx = -radius; xx <= radius; xx++) {
            for (int yy = -radius; yy <= radius; yy++) {
                for (int zz = -radius; zz <= radius; zz++) {
                    BlockPos pos = center.add(xx, yy, zz);
                    if(isChunkLoaded(world, new ChunkPos(pos))) {
                        BlockState state = world.getBlockState(pos);
                        Block b = state.getBlock();
                        if(b.equals(blockToSearch)) {
                            found.add(pos);
                        }
                    }
                }
            }
        }
        return found;
    }

    static {
        prettierColorMapping.put(DyeColor.WHITE,      ColorsAS.DYE_WHITE);
        prettierColorMapping.put(DyeColor.ORANGE,     ColorsAS.DYE_ORANGE);
        prettierColorMapping.put(DyeColor.MAGENTA,    ColorsAS.DYE_MAGENTA);
        prettierColorMapping.put(DyeColor.LIGHT_BLUE, ColorsAS.DYE_LIGHT_BLUE);
        prettierColorMapping.put(DyeColor.YELLOW,     ColorsAS.DYE_YELLOW);
        prettierColorMapping.put(DyeColor.LIME,       ColorsAS.DYE_LIME);
        prettierColorMapping.put(DyeColor.PINK,       ColorsAS.DYE_PINK);
        prettierColorMapping.put(DyeColor.GRAY,       ColorsAS.DYE_GRAY);
        prettierColorMapping.put(DyeColor.LIGHT_GRAY, ColorsAS.DYE_LIGHT_GRAY);
        prettierColorMapping.put(DyeColor.CYAN,       ColorsAS.DYE_CYAN);
        prettierColorMapping.put(DyeColor.PURPLE,     ColorsAS.DYE_PURPLE);
        prettierColorMapping.put(DyeColor.BLUE,       ColorsAS.DYE_BLUE);
        prettierColorMapping.put(DyeColor.BROWN,      ColorsAS.DYE_BROWN);
        prettierColorMapping.put(DyeColor.GREEN,      ColorsAS.DYE_GREEN);
        prettierColorMapping.put(DyeColor.RED,        ColorsAS.DYE_RED);
        prettierColorMapping.put(DyeColor.BLACK,      ColorsAS.DYE_BLACK);
    }

}
