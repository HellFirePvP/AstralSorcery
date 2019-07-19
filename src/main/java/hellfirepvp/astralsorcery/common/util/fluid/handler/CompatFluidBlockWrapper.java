/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.fluid.handler;

import hellfirepvp.astralsorcery.common.util.fluid.CompatFluidStack;
import hellfirepvp.astralsorcery.common.util.fluid.CompatFluidUtil;
import hellfirepvp.astralsorcery.common.util.fluid.handler.tank.CompatFluidTankProperties;
import hellfirepvp.astralsorcery.common.util.fluid.handler.tank.ICompatFluidTankProperties;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CompatFluidBlockWrapper
 * Created by HellFirePvP
 * Date: 19.07.2019 / 15:51
 */
public class CompatFluidBlockWrapper implements ICompatFluidHandler {

    protected final FlowingFluidBlock fluidBlock;
    protected final World world;
    protected final BlockPos blockPos;

    public CompatFluidBlockWrapper(FlowingFluidBlock fluidBlock, World world, BlockPos blockPos) {
        this.fluidBlock = fluidBlock;
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public ICompatFluidTankProperties[] getTankProperties() {
        BlockState state = world.getBlockState(blockPos);
        if (state.getBlock() instanceof FlowingFluidBlock) {
            int level = state.get(FlowingFluidBlock.LEVEL);
            float perc = ((float) level) / 8F;
            int amountFilled = Math.round(CompatFluidStack.BUCKET_VOLUME * perc);
            CompatFluidStack fluid = amountFilled > 0 ? new CompatFluidStack(fluidBlock.getFluidState(state).getFluid(), amountFilled) : null;
            return new ICompatFluidTankProperties[] { new CompatFluidTankProperties(fluid, CompatFluidStack.BUCKET_VOLUME, false, true) };
        }
        return CompatEmptyFluidHandler.EMPTY_TANK_PROPERTIES_ARRAY;
    }

    @Override
    public int fill(CompatFluidStack resource, boolean doFill) {
        if (resource == null) {
            return 0;
        }
        BlockState toPlace = resource.getFluid().getDefaultState().getBlockState();
        if (doFill && toPlace != null && toPlace.getBlock() != Blocks.AIR) {
            CompatFluidUtil.destroyBlockOnFluidPlacement(world, blockPos);
            world.setBlockState(blockPos, toPlace, Constants.BlockFlags.DEFAULT_AND_RERENDER);
        }
        return CompatFluidStack.BUCKET_VOLUME;
    }

    @Nullable
    @Override
    public CompatFluidStack drain(CompatFluidStack resource, boolean doDrain) {
        if (resource == null) {
            return null;
        }
        BlockState state = world.getBlockState(blockPos);
        if (!(state.getBlock() instanceof FlowingFluidBlock)) {
            return null;
        }
        Fluid f = state.getFluidState().getFluid();

        CompatFluidStack simulatedDrain = new CompatFluidStack(f, CompatFluidStack.BUCKET_VOLUME);
        if (resource.containsFluid(simulatedDrain)) {
            if (doDrain) {
                f = fluidBlock.pickupFluid(world, blockPos, state);
                if (f != Fluids.EMPTY) {
                    return new CompatFluidStack(f, CompatFluidStack.BUCKET_VOLUME);
                }
            } else {
                return simulatedDrain;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public CompatFluidStack drain(int maxDrain, boolean doDrain) {
        if (maxDrain <= 0) {
            return null;
        }
        BlockState state = world.getBlockState(blockPos);
        if (!(state.getBlock() instanceof FlowingFluidBlock)) {
            return null;
        }
        Fluid f = state.getFluidState().getFluid();

        CompatFluidStack simulatedDrain = new CompatFluidStack(f, CompatFluidStack.BUCKET_VOLUME);
        if (simulatedDrain.getAmount() <= maxDrain) {
            if (doDrain) {
                f = fluidBlock.pickupFluid(world, blockPos, state);
                if (f != Fluids.EMPTY) {
                    return new CompatFluidStack(f, CompatFluidStack.BUCKET_VOLUME);
                }
            } else {
                return simulatedDrain;
            }
        }
        return null;
    }
}
