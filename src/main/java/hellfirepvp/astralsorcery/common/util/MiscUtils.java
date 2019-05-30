/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.util.block.BlockPredicate;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Tuple;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
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

    public static final String GAMERULE_SKIP_SKYLIGHT_CHECK = "astralSorceryIgnoreSkyCheck";
    private static Map<EnumDyeColor, Color> prettierColorMapping = new HashMap<>();

    @Nullable
    public static <T> T getTileAt(IWorldReader world, BlockPos pos, Class<T> tileClass, boolean forceChunkLoad) {
        if(world == null || pos == null) return null; //Duh.
        if(world instanceof World) {
            if(!world.isBlockLoaded(pos) && !forceChunkLoad) return null;
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
        ChunkPos chTest = new ChunkPos(test);
        boolean isForced = world.func_212416_f(chTest.x, chTest.z);
        int range = isForced ? 0 : 32;
        return world.isAreaLoaded(test.add(-range, 0, -range), test.add(range, 0, range), true);
    }

    @Nullable
    public static <T> T getRandomEntry(List<T> list, Random rand) {
        if(list == null || list.isEmpty()) return null;
        return list.get(rand.nextInt(list.size()));
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
        if (world.getGameRules().getBoolean(GAMERULE_SKIP_SKYLIGHT_CHECK)) {
            return true;
        }

        if (!isChunkLoaded(world, at) && !loadChunk) {
            return defaultValue;
        }
        return world.canSeeSky(at);
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

    public static boolean canPlayerAttackServer(@Nullable EntityLivingBase source, @Nonnull EntityLivingBase target) {
        if (!target.isAlive()) {
            return false;
        }
        if (target instanceof EntityPlayer) {
            EntityPlayer plTarget = (EntityPlayer) target;
            if (target.getEntityWorld() instanceof WorldServer &&
                    target.getEntityWorld().getServer() != null &&
                    target.getEntityWorld().getServer().isPVPEnabled()) {
                return false;
            }
            if (plTarget.isSpectator() || plTarget.isCreative()) {
                return false;
            }
            if (source instanceof EntityPlayer &&
                    !((EntityPlayer) source).canAttackPlayer(plTarget)) {
                return false;
            }
        }
        return true;
    }

    /*
    TODO 1.14
    public static boolean isFluidBlock(IBlockState state) {
        return state.getBlock() instanceof BlockLiquid || state.getBlock() instanceof BlockFluidBase;
    }

    @Nullable
    public static Fluid tryGetFuild(IBlockState state) {
        if (!isFluidBlock(state)) {
            return null;
        }
        if (state.getBlock() instanceof BlockLiquid) {
            Material mat = state.getMaterial();
            if (mat == Material.WATER) {
                return FluidRegistry.WATER;
            } else if (mat == Material.LAVA) {
                return FluidRegistry.LAVA;
            }
        } else if (state.getBlock() instanceof BlockFluidBase) {
            return ((BlockFluidBase) state.getBlock()).getFluid();
        }
        return null;
    }*/

    public static boolean canPlayerBreakBlockPos(EntityPlayer player, BlockPos tryBreak) {
        BlockEvent.BreakEvent ev = new BlockEvent.BreakEvent(player.getEntityWorld(), tryBreak, player.getEntityWorld().getBlockState(tryBreak), player);
        MinecraftForge.EVENT_BUS.post(ev);
        return !ev.isCanceled();
    }

    public static boolean canPlayerPlaceBlockPos(EntityPlayer player, EnumHand withHand, IBlockState tryPlace, BlockPos pos, EnumFacing againstSide) {
        BlockSnapshot snapshot = new BlockSnapshot(player.getEntityWorld(), pos, tryPlace);
        BlockEvent.PlaceEvent ev = ForgeEventFactory.onPlayerBlockPlace(player, snapshot, againstSide, withHand);
        return !ev.isCanceled();
    }

    public static boolean isConnectionEstablished(EntityPlayerMP player) {
        return player.connection != null && player.connection.netManager != null && player.connection.netManager.isChannelOpen();
    }

    @Nullable
    public static Tuple<EnumHand, ItemStack> getMainOrOffHand(EntityLivingBase entity, Item search) {
        return getMainOrOffHand(entity, search, null);
    }

    @Nullable
    public static Tuple<EnumHand, ItemStack> getMainOrOffHand(EntityLivingBase entity, Item search, @Nullable Predicate<ItemStack> acceptorFnc) {
        EnumHand hand = EnumHand.MAIN_HAND;
        ItemStack held = entity.getHeldItem(hand);
        if (held.isEmpty() || !search.getClass().isAssignableFrom(held.getItem().getClass()) || (acceptorFnc != null && !acceptorFnc.test(held))) {
            hand = EnumHand.OFF_HAND;
            held = entity.getHeldItem(hand);
        }
        if (held.isEmpty() || !search.getClass().isAssignableFrom(held.getItem().getClass()) || (acceptorFnc != null && !acceptorFnc.test(held))) {
            return null;
        }
        return new Tuple<>(hand, held);
    }

    @Nonnull
    public static Color flareColorFromDye(EnumDyeColor color) {
        Color c = prettierColorMapping.get(color);
        if(c == null) c = Color.WHITE;
        return c;
    }

    @Nonnull
    public static TextFormatting textFormattingForDye(EnumDyeColor color) {
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
        if(entity.getEntityWorld().isRemote) return; //No transfers on clientside.
        entity.setSneaking(false);
        Dimension dimFrom = entity.getEntityWorld().getDimension();
        if(dimFrom.getType() != target) {
            if(!ForgeHooks.onTravelToDimension(entity, target)) {
                return;
            }

            NoOpTeleporter teleporter = new NoOpTeleporter(targetPos);
            if(entity instanceof EntityPlayerMP) {
                MinecraftServer srv = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
                srv.getPlayerList().changePlayerDimension((EntityPlayerMP) entity, target);
            } else {
                entity.changeDimension(target, teleporter);
            }
        }
        entity.setPositionAndUpdate(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
    }

    @Nullable
    public static BlockPos itDownTopBlock(World world, BlockPos at) {
        Chunk chunk = world.getChunk(at);
        BlockPos downPos = null;

        for (BlockPos blockpos = new BlockPos(at.getX(), chunk.getTopFilledSegment() + 16, at.getZ()); blockpos.getY() >= 0; blockpos = downPos) {
            downPos = blockpos.down();
            IBlockState test = world.getBlockState(downPos);
            IFluidState state = test.getFluidState();
            state.isTagged(FluidTags.LAVA);
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
    public static RayTraceResult rayTraceLook(EntityPlayer player) {
        return rayTraceLook(player, player.getAttribute(EntityPlayer.REACH_DISTANCE).getValue());
    }

    @Nullable
    public static RayTraceResult rayTraceLook(EntityLivingBase entity, double reachDst) {
        Vec3d pos = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        Vec3d lookVec = entity.getLookVec();
        Vec3d end = pos.add(lookVec.x * reachDst, lookVec.y * reachDst, lookVec.z * reachDst);
        return entity.world.rayTraceBlocks(pos, end);
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

    public static boolean isPlayerFakeMP(EntityPlayerMP player) {
        if(player instanceof FakePlayer) return true;

        boolean isModdedPlayer = false;
        for (Mods mod : Mods.values()) {
            if(!mod.isPresent()) {
                continue;
            }
            Class<?> specificPlayerClass = mod.getExtendedPlayerClass();
            if(specificPlayerClass != null) {
                if(player.getClass() != EntityPlayerMP.class && player.getClass() == specificPlayerClass) {
                    isModdedPlayer = true;
                    break;
                }
            }
        }
        if(!isModdedPlayer && player.getClass() != EntityPlayerMP.class) {
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
                            IBlockState state = world.getBlockState(pos);
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

    public static List<BlockPos> searchAreaFor(World world, BlockPos center, IBlockState blockToSearch, int radius) {
        List<BlockPos> found = new LinkedList<>();
        for (int xx = -radius; xx <= radius; xx++) {
            for (int yy = -radius; yy <= radius; yy++) {
                for (int zz = -radius; zz <= radius; zz++) {
                    BlockPos pos = center.add(xx, yy, zz);
                    if(isChunkLoaded(world, new ChunkPos(pos))) {
                        IBlockState state = world.getBlockState(pos);
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
        prettierColorMapping.put(EnumDyeColor.WHITE, new Color(0xFFFFFF));
        prettierColorMapping.put(EnumDyeColor.ORANGE, new Color(0xFF8C1D));
        prettierColorMapping.put(EnumDyeColor.MAGENTA, new Color(0xEF0EFF));
        prettierColorMapping.put(EnumDyeColor.LIGHT_BLUE, new Color(0x06E5FF));
        prettierColorMapping.put(EnumDyeColor.YELLOW, new Color(0xFFEB00));
        prettierColorMapping.put(EnumDyeColor.LIME, new Color(0x93FF10));
        prettierColorMapping.put(EnumDyeColor.PINK, new Color(0xFF18D9));
        prettierColorMapping.put(EnumDyeColor.GRAY, new Color(0x5E5E5E));
        prettierColorMapping.put(EnumDyeColor.LIGHT_GRAY, new Color(0xBDBDBD));
        prettierColorMapping.put(EnumDyeColor.CYAN, new Color(0x5498B4));
        prettierColorMapping.put(EnumDyeColor.PURPLE, new Color(0xB721F7));
        prettierColorMapping.put(EnumDyeColor.BLUE, new Color(0x3C00FF));
        prettierColorMapping.put(EnumDyeColor.BROWN, new Color(0xB77109));
        prettierColorMapping.put(EnumDyeColor.GREEN, new Color(0x00AA00));
        prettierColorMapping.put(EnumDyeColor.RED, new Color(0xFF0000));
        prettierColorMapping.put(EnumDyeColor.BLACK, new Color(0x000000));
    }

}
