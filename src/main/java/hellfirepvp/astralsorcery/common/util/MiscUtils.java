/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MiscUtils
 * Created by HellFirePvP
 * Date: 01.08.2016 / 13:38
 */
public class MiscUtils {

    private static Map<EnumDyeColor, Color> prettierColorMapping = new HashMap<>();

    @Nullable
    public static <T> T getTileAt(IBlockAccess world, BlockPos pos, Class<T> tileClass, boolean forceChunkLoad) {
        if(world == null || pos == null) return null; //Duh.
        if(world instanceof World) {
            if(!((World) world).isBlockLoaded(pos) && !forceChunkLoad) return null;
        }
        TileEntity te = world.getTileEntity(pos);
        if(te == null) return null;
        if(tileClass.isInstance(te)) return (T) te;
        return null;
    }

    @Nullable
    public static <T> T getRandomEntry(List<T> list, Random rand) {
        if(list == null || list.isEmpty()) return null;
        return list.get(rand.nextInt(list.size()));
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
            case SILVER:
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
        return String.valueOf(Character.toTitleCase(str.charAt(0))) + str.substring(1);
    }

    public static boolean breakBlockWithPlayer(BlockPos pos, EntityPlayerMP playerMP) {
        return playerMP.interactionManager.tryHarvestBlock(pos);
    }

    //Copied from ForgeHooks.onBlockBreak & PlayerInteractionManager.tryHarvestBlock
    //Duplicate break functionality without a active player.
    //Emulates a FakePlayer - attempts without a player as harvester in case a fakeplayer leads to issues.
    public static boolean breakBlockWithoutPlayer(WorldServer world, BlockPos pos) {
        try {
            FakePlayer fp = AstralSorcery.proxy.getASFakePlayerServer(world);
            IBlockState state = world.getBlockState(pos);
            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, fp);
            MinecraftForge.EVENT_BUS.post(event);
            int exp = event.getExpToDrop();
            if(event.isCanceled()) return false;

            IBlockState iblockstate = world.getBlockState(pos);
            TileEntity tileentity = world.getTileEntity(pos);
            Block block = iblockstate.getBlock();
            world.playEvent(null, 2001, pos, Block.getStateId(iblockstate));
            boolean flag = block.canHarvestBlock(world, pos, fp);
            boolean flag1 = block.removedByPlayer(iblockstate, world, pos, fp, flag);
            if (flag1) {
                block.onBlockDestroyedByPlayer(world, pos, iblockstate);
            }
            if (flag1 && flag) {
                block.harvestBlock(world, fp, pos, iblockstate, tileentity, null);
            }
            if (flag1 && exp > 0) {
                block.dropXpOnBlockBreak(world, pos, exp);
            }
            return flag1;
        } catch (Exception ignored) {} //Silently fail and propagate it as "can't break this block"
        return false;
    }

    public static void transferEntityTo(Entity entity, int targetDimId, BlockPos targetPos) {
        if(entity.getEntityWorld().isRemote) return; //No transfers on clientside.
        if(entity.getEntityWorld().provider.getDimension() != targetDimId) {
            if(entity instanceof EntityPlayerMP) {
                FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().transferPlayerToDimension((EntityPlayerMP) entity, targetDimId, new NoOpTeleporter(((EntityPlayerMP) entity).getServerWorld()));
            } else {
                entity.changeDimension(targetDimId);
            }
        }
        entity.setPositionAndUpdate(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
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
    public static RayTraceResult rayTraceLook(EntityLivingBase entity, double reachDst) {
        Vec3d pos = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        Vec3d lookVec = entity.getLookVec();
        Vec3d end = pos.addVector(lookVec.xCoord * reachDst, lookVec.yCoord * reachDst, lookVec.zCoord * reachDst);
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

    public static boolean isChunkLoaded(World world, ChunkPos pos) {
        return world.getChunkProvider().getLoadedChunk(pos.chunkXPos, pos.chunkZPos) != null;
    }

    public static boolean isPlayerFakeMP(EntityPlayerMP player) {
        if(player instanceof FakePlayer) return true;
        if(player.getClass() != EntityPlayerMP.class) return true;
        if(player.connection == null) return true;
        try {
            player.getPlayerIP().length();
            player.connection.netManager.getRemoteAddress().toString();
        } catch (Exception exc) {
            return true;
        }
        if(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList() == null) return true;
        return !FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerList().contains(player);
    }

    public static List<BlockPos> searchAreaFor(World world, BlockPos center, Block blockToSearch, int metaToSearch, int radius) {
        List<BlockPos> found = new LinkedList<>();
        for (int xx = -radius; xx <= radius; xx++) {
            for (int yy = -radius; yy <= radius; yy++) {
                for (int zz = -radius; zz <= radius; zz++) {
                    BlockPos pos = center.add(xx, yy, zz);
                    if(isChunkLoaded(world, new ChunkPos(pos))) {
                        IBlockState state = world.getBlockState(pos);
                        Block b = state.getBlock();
                        if(b.equals(blockToSearch) && b.getMetaFromState(state) == metaToSearch) {
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
        prettierColorMapping.put(EnumDyeColor.SILVER, new Color(0xBDBDBD));
        prettierColorMapping.put(EnumDyeColor.CYAN, new Color(0x5498B4));
        prettierColorMapping.put(EnumDyeColor.PURPLE, new Color(0xB721F7));
        prettierColorMapping.put(EnumDyeColor.BLUE, new Color(0x3C00FF));
        prettierColorMapping.put(EnumDyeColor.BROWN, new Color(0xB77109));
        prettierColorMapping.put(EnumDyeColor.GREEN, new Color(0x00AA00));
        prettierColorMapping.put(EnumDyeColor.RED, new Color(0xFF0000));
        prettierColorMapping.put(EnumDyeColor.BLACK, new Color(0x000000));
    }

}
