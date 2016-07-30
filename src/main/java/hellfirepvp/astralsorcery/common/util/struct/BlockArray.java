package hellfirepvp.astralsorcery.common.util.struct;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockArray
 * Created by HellFirePvP
 * Date: 30.07.2016 / 16:23
 */
public class BlockArray {

    protected Map<BlockPos, BlockInformation> pattern = new HashMap<>();

    public void addBlock(BlockPos offset, IBlockState state) {
        Block b = state.getBlock();
        pattern.put(offset, new BlockInformation(b, b.getMetaFromState(state)));
    }
    public void addBlock(BlockPos offset, Block b, int meta) {
        pattern.put(offset, new BlockInformation(b, meta));
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
