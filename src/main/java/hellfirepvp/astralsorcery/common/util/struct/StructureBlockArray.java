package hellfirepvp.astralsorcery.common.util.struct;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiBlockArray
 * Created by HellFirePvP
 * Date: 30.07.2016 / 16:07
 */
public class StructureBlockArray extends BlockArray {

    private Map<BlockPos, TileEntityCallback> tileCallbacks = new HashMap<>();

    public void addTileCallback(BlockPos pos, TileEntityCallback callback) {
        tileCallbacks.put(pos, callback);
    }

    public void placeInWorld(World world, BlockPos center, PastPlaceProcessor processor) {
        Map<BlockPos, IBlockState> result = new HashMap<>();
        for (Map.Entry<BlockPos, BlockInformation> entry : pattern.entrySet()) {
            BlockInformation info = entry.getValue();
            BlockPos at = center.add(entry.getKey());
            IBlockState state = info.type.getStateFromMeta(info.meta);
            world.setBlockState(at, state, 3);
            result.put(at, state);

            TileEntity placed = world.getTileEntity(at);
            if(tileCallbacks.containsKey(entry.getKey())) {
                TileEntityCallback callback = tileCallbacks.get(entry.getKey());
                if(callback.isApplicable(placed)) {
                    callback.onPlace(world, at, placed);
                }
            }
        }
        if(processor != null) {
            for (Map.Entry<BlockPos, IBlockState> entry : result.entrySet())
                processor.process(world, entry.getKey(), entry.getValue());
        }
    }

    public static interface TileEntityCallback {

        public boolean isApplicable(TileEntity te);

        public void onPlace(World world, BlockPos at, TileEntity te);

    }

    public static interface PastPlaceProcessor {

        public void process(World world, BlockPos pos, IBlockState currentState);

    }

}
