/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MiscUtils
 * Created by HellFirePvP
 * Date: 01.08.2016 / 13:38
 */
public class MiscUtils {

    @Nullable
    public static <T> T getTileAt(IBlockAccess world, BlockPos pos, Class<T> tileClass, boolean forceChunkLoad) {
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

}
