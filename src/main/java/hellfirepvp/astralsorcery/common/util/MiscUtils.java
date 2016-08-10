package hellfirepvp.astralsorcery.common.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MiscUtils
 * Created by HellFirePvP
 * Date: 01.08.2016 / 13:38
 */
public class MiscUtils {

    @Nullable
    public static <T extends TileEntity> T getTileAt(IBlockAccess world, BlockPos pos, Class<T> tileClass) {
        TileEntity te = world.getTileEntity(pos);
        if(te == null) return null;
        if(tileClass.isInstance(te)) return (T) te;
        return null;
    }

    @Nullable
    public static RayTraceResult rayTraceLook(EntityLivingBase entity, double reachDst) {
        Vec3d pos = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        Vec3d lookVec = entity.getLookVec();
        Vec3d end = pos.addVector(lookVec.xCoord * reachDst, lookVec.yCoord * reachDst, lookVec.zCoord * reachDst);
        return entity.worldObj.rayTraceBlocks(pos, end);
    }

    public static boolean isChunkLoaded(World world, ChunkPos pos) {
        return world.getChunkProvider().getLoadedChunk(pos.chunkXPos, pos.chunkZPos) != null;
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
