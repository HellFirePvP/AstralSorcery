package hellfirepvp.astralsorcery.common.util.struct;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockArray
 * Created by HellFirePvP
 * Date: 30.07.2016 / 16:23
 */
public class BlockArray {

    protected static final Random STATIC_RAND = new Random();

    protected Map<BlockPos, TileEntityCallback> tileCallbacks = new HashMap<>();
    protected Map<BlockPos, BlockInformation> pattern = new HashMap<>();
    private Vec3i min = new Vec3i(0, 0, 0), max = new Vec3i(0, 0, 0);

    public void addBlock(int x, int y, int z, @Nonnull IBlockState state) {
        addBlock(new BlockPos(x, y, z), state);
    }

    public void addBlock(BlockPos offset, @Nonnull IBlockState state) {
        Block b = state.getBlock();
        pattern.put(offset, new BlockInformation(b, state));
        updateSize(offset);
    }
    public void addTileCallback(BlockPos pos, TileEntityCallback callback) {
        tileCallbacks.put(pos, callback);
    }

    public Vec3i getMax() {
        return max;
    }

    public Vec3i getMin() {
        return min;
    }

    private void updateSize(BlockPos addedPos) {
        if(addedPos.getX() < min.getX()) {
            min = new Vec3i(addedPos.getX(), min.getY(), min.getZ());
        }
        if(addedPos.getX() > max.getX()) {
            max = new Vec3i(addedPos.getX(), max.getY(), max.getZ());
        }
        if(addedPos.getY() < min.getY()) {
            min = new Vec3i(min.getX(), addedPos.getY(), min.getZ());
        }
        if(addedPos.getY() > max.getY()) {
            max = new Vec3i(max.getX(), addedPos.getY(), max.getZ());
        }
        if(addedPos.getZ() < min.getZ()) {
            min = new Vec3i(min.getX(), min.getY(), addedPos.getZ());
        }
        if(addedPos.getZ() > max.getZ()) {
            max = new Vec3i(max.getX(), max.getY(), addedPos.getZ());
        }
    }

    public Map<BlockPos, BlockInformation> getPattern() {
        return pattern;
    }

    public Map<BlockPos, TileEntityCallback> getTileCallbacks() {
        return tileCallbacks;
    }

    public void addBlockCube(@Nonnull IBlockState state, int ox, int oy, int oz, int tx, int ty, int tz) {
        int lx, ly, lz;
        int hx, hy, hz;
        if(ox < tx) {
            lx = ox;
            hx = tx;
        } else {
            lx = tx;
            hx = ox;
        }
        if(oy < ty) {
            ly = oy;
            hy = ty;
        } else {
            ly = ty;
            hy = oy;
        }
        if(oz < tz) {
            lz = oz;
            hz = tz;
        } else {
            lz = tz;
            hz = oz;
        }

        for (int xx = lx; xx <= hx; xx++) {
            for (int zz = lz; zz <= hz; zz++) {
                for (int yy = ly; yy <= hy; yy++) {
                    addBlock(new BlockPos(xx, yy, zz), state);
                }
            }
        }
    }

    public static interface TileEntityCallback {

        public boolean isApplicable(TileEntity te);

        public void onPlace(IBlockAccess access, BlockPos at, TileEntity te);

    }

    public static class BlockInformation {

        public final Block type;
        public final IBlockState state;
        public final int metadata;

        protected BlockInformation(Block type, IBlockState state) {
            this.type = type;
            this.state = state;
            this.metadata = type.getMetaFromState(state);
        }

    }

}
