/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.observerlib.api.block.MatchableState;
import hellfirepvp.observerlib.api.tile.MatchableTile;
import hellfirepvp.observerlib.common.block.PatternBlockArray;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureBlockArray
 * Created by HellFirePvP
 * Date: 22.07.2019 / 08:24
 */
public class StructureBlockArray extends PatternBlockArray {

    protected Map<BlockPos, TileEntityCallback> tileCallbacks = new HashMap<>();

    public StructureBlockArray(ResourceLocation registryName) {
        super(registryName);
    }

    public Map<BlockPos, BlockState> placeInWorld(World world, BlockPos center) {
        Map<BlockPos, BlockState> result = new HashMap<>();
        for (Map.Entry<BlockPos, MatchableState> entry : this.getContents().entrySet()) {
            MatchableState match = entry.getValue();
            BlockPos at = center.add(entry.getKey());
            BlockState state = match.getDescriptiveState(0);
            if (!world.setBlockState(at, state, Constants.BlockFlags.DEFAULT)) {
                continue;
            }
            result.put(at, state);

            if (MiscUtils.isFluidBlock(state)) {
                world.neighborChanged(at, state.getBlock(), at);
            }

            TileEntity placed = world.getTileEntity(at);
            if (tileCallbacks.containsKey(entry.getKey())) {
                TileEntityCallback callback = tileCallbacks.get(entry.getKey());
                if (callback.isApplicable(placed)) {
                    callback.onPlace(world, at, placed);
                }
            }
        }
        return result;
    }

    public Map<BlockPos, BlockState> placeInWorld(World world, BlockPos center, PastPlaceProcessor processor) {
        Map<BlockPos, BlockState> result = this.placeInWorld(world, center);
        if(processor != null) {
            for (Map.Entry<BlockPos, BlockState> entry : result.entrySet())
                processor.process(world, entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static interface PastPlaceProcessor {

        void process(World world, BlockPos pos, BlockState currentState);

    }

    public static interface TileEntityCallback {

        boolean isApplicable(TileEntity te);

        void onPlace(IWorldReader access, BlockPos at, TileEntity te);

    }
    
}
