/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.struct;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatternBlockArray
 * Created by HellFirePvP
 * Date: 30.07.2016 / 16:24
 */
public class PatternBlockArray extends BlockArray {

    private Map<BlockPos, TileEntityMatcher> matcherMap = new HashMap<>();

    public void addMatcher(BlockPos offset, TileEntityMatcher matcher) {
        matcherMap.put(offset, matcher);
    }

    public boolean matches(World world, BlockPos center) {
        for (Map.Entry<BlockPos, BlockInformation> entry : pattern.entrySet()) {
            BlockInformation info = entry.getValue();
            BlockPos at = center.add(entry.getKey());
            IBlockState state = world.getBlockState(at);
            if(!info.matcher.isStateValid(world, at, state)) {
                return false;
            }
            if(matcherMap.containsKey(entry.getKey())) {
                TileEntity te = world.getTileEntity(at);
                TileEntityMatcher matcher = matcherMap.get(entry.getKey());
                if(!matcher.isApplicable(te) || !matcher.matches(world, at, te)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Optional<Boolean> matchSingleBlock(World world, BlockPos center, BlockPos offset) {
        if(!pattern.containsKey(offset)) return Optional.empty();
        BlockInformation info = pattern.get(offset);
        BlockPos at = center.add(offset);
        IBlockState state = world.getBlockState(at);
        if(!info.matcher.isStateValid(world, at, state)) {
            return Optional.of(false);
        }
        if(matcherMap.containsKey(offset)) {
            TileEntity te = world.getTileEntity(at);
            TileEntityMatcher matcher = matcherMap.get(offset);
            if(!matcher.isApplicable(te) || !matcher.matches(world, at, te)) {
                return Optional.of(false);
            }
        }
        return Optional.of(true);
    }

    public static interface TileEntityMatcher {

        public boolean isApplicable(TileEntity te);

        public boolean matches(World world, BlockPos at, TileEntity te);

    }

}
