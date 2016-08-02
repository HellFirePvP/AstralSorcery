package hellfirepvp.astralsorcery.common.util.struct;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

    protected Map<BlockPos, BlockInformation> pattern = new HashMap<>();

    public void addBlock(int x, int y, int z, IBlockState state) {
        addBlock(new BlockPos(x, y, z), state);
    }

    public void addBlock(BlockPos offset, IBlockState state) {
        Block b = state.getBlock();
        pattern.put(offset, new BlockInformation(b, b.getMetaFromState(state)));
    }

    public void addBlock(BlockPos offset, Block b, int meta) {
        pattern.put(offset, new BlockInformation(b, meta));
    }

    public void buildQuad(IBlockState state, int ox, int oy, int oz, int tx, int ty, int tz) {
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
        /*do {
            do {
                do {
                    addBlock(new BlockPos(lx, ly, lz), state);
                    ly++;
                } while (ly < hy);
                lz++;
            } while (lz < hz);
            lx++;
        } while (lx < hx);*/

    }

    protected static class BlockInformation {

        protected Block type;
        protected int meta;

        protected BlockInformation(Block type, int meta) {
            this.meta = meta;
            this.type = type;
        }

    }

}
