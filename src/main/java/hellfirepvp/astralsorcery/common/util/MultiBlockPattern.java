package hellfirepvp.astralsorcery.common.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiBlockPattern
 * Created by HellFirePvP
 * Date: 15.05.2016 / 15:40
 */
public class MultiBlockPattern {

    private List<Part> multiblockParts = new LinkedList<>();

    public void appendPart(BlockPos offset, IBlockState expectedState) {
        multiblockParts.add(new Part(offset, expectedState));
    }

    public boolean isPresent(World world, BlockPos pos) {
        for (Part p : multiblockParts) {
            BlockPos at = pos.add(p.offset);
            IBlockState state = world.getBlockState(at);
            if(!state.getBlock().equals(p.expectedBlock) ||
                    !(state.getBlock().getMetaFromState(state) == p.expectedMeta)) {
                return false;
            }
        }
        return true;
    }

    public static class Part {

        private BlockPos offset;
        private Block expectedBlock;
        private int expectedMeta;

        public Part(BlockPos offset, IBlockState state) {
            this.offset = offset;
            this.expectedBlock = state.getBlock();
            this.expectedMeta = state.getBlock().getMetaFromState(state);
        }
    }

}
