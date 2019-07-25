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
import hellfirepvp.observerlib.common.block.PatternBlockArray;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.TickPriority;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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

    public void setAir(IWorld world, BlockPos pos) {
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), Constants.BlockFlags.DEFAULT);
    }

    public void setBlockState(IWorld world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state, Constants.BlockFlags.DEFAULT);
    }

    public void setBlockCube(IWorld world, BlockPos offset, Function<BlockPos, BlockState> stateSupplier, int ox, int oy, int oz, int tx, int ty, int tz) {
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
                    BlockPos at = offset.add(xx, yy, zz);
                    world.setBlockState(at, stateSupplier.apply(at), Constants.BlockFlags.DEFAULT);
                }
            }
        }
    }

    public Map<BlockPos, BlockState> placeInWorld(IWorld world, BlockPos center, Predicate<BlockPos> posFilter) {
        Map<BlockPos, BlockState> result = new HashMap<>();
        for (Map.Entry<BlockPos, MatchableState> entry : this.getContents().entrySet()) {
            MatchableState match = entry.getValue();
            BlockPos at = center.add(entry.getKey());
            if (!posFilter.test(at)) {
                continue;
            }

            BlockState state = match.getDescriptiveState(0);
            if (!world.setBlockState(at, state, Constants.BlockFlags.DEFAULT)) {
                continue;
            }
            result.put(at, state);

            Fluid f;
            if (MiscUtils.isFluidBlock(state) && (f = MiscUtils.tryGetFuild(state)) != null) {
                world.getPendingFluidTicks().scheduleTick(at, f, f.getTickRate(world), TickPriority.HIGH);
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

    public Map<BlockPos, BlockState> placeInWorld(IWorld world, BlockPos center, Predicate<BlockPos> posFilter, PastPlaceProcessor processor) {
        Map<BlockPos, BlockState> result = this.placeInWorld(world, center, posFilter);
        if(processor != null) {
            for (Map.Entry<BlockPos, BlockState> entry : result.entrySet()) {
                if (posFilter.test(entry.getKey())) {
                    processor.process(world, entry.getKey(), entry.getValue());
                }
            }
        }
        return result;
    }

    public static interface PastPlaceProcessor {

        void process(IWorld world, BlockPos pos, BlockState currentState);

    }

    public static interface TileEntityCallback {

        boolean isApplicable(TileEntity te);

        void onPlace(IWorldReader access, BlockPos at, TileEntity te);

    }
    
}
