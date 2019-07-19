/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.fluid.handler.tank;

import hellfirepvp.astralsorcery.common.util.fluid.CompatFluidStack;
import hellfirepvp.astralsorcery.common.util.fluid.handler.ICompatFluidHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CompatFluidTank
 * Created by HellFirePvP
 * Date: 19.07.2019 / 14:10
 */
public class CompatFluidTank implements ICompatFluidHandler, ICompatFluidTank {

    protected CompatFluidStack fluid;
    protected int capacity;
    protected TileEntity tile;
    protected boolean canFill = true;
    protected boolean canDrain = true;
    protected ICompatFluidTankProperties[] tankProperties;

    public CompatFluidTank(int capacity) {
        this(null, capacity);
    }

    public CompatFluidTank(@Nullable CompatFluidStack fluidStack, int capacity) {
        this.fluid = fluidStack;
        this.capacity = capacity;
    }

    public CompatFluidTank(Fluid fluid, int amount, int capacity) {
        this(new CompatFluidStack(fluid, amount), capacity);
    }

    public CompatFluidTank readFromNBT(CompoundNBT nbt) {
        if (!nbt.contains("Empty")) {
            CompatFluidStack fluid = CompatFluidStack.deserialize(nbt);
            setFluid(fluid);
        } else {
            setFluid(null);
        }
        return this;
    }

    public CompoundNBT writeToNBT(CompoundNBT nbt) {
        if (fluid != null) {
            fluid.writeToNBT(nbt);
        } else {
            nbt.putString("Empty", "");
        }
        return nbt;
    }

    @Nullable
    @Override
    public CompatFluidStack getFluid() {
        return this.fluid;
    }

    public void setFluid(@Nullable CompatFluidStack fluid) {
        this.fluid = fluid;
    }

    @Override
    public int getFluidAmount() {
        if (this.getFluid() == null) {
            return 0;
        }
        return this.getFluid().getAmount();
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setTileEntity(TileEntity tile) {
        this.tile = tile;
    }

    @Override
    public CompatFluidTankInfo getInfo() {
        return new CompatFluidTankInfo(this);
    }

    @Override
    public ICompatFluidTankProperties[] getTankProperties() {
        return new ICompatFluidTankProperties[0];
    }

    @Override
    public int fill(CompatFluidStack resource, boolean doFill) {
        if (!canFillFluidType(resource)) {
            return 0;
        }

        return fillInternal(resource, doFill);
    }

    @Nullable
    @Override
    public CompatFluidStack drain(CompatFluidStack resource, boolean doDrain) {
        if (!canDrainFluidType(getFluid())) {
            return null;
        }
        return drainInternal(resource, doDrain);
    }

    @Nullable
    @Override
    public CompatFluidStack drain(int maxDrain, boolean doDrain) {
        if (!canDrainFluidType(fluid)) {
            return null;
        }
        return drainInternal(maxDrain, doDrain);
    }

    @Nullable
    public CompatFluidStack drainInternal(CompatFluidStack resource, boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(getFluid())) {
            return null;
        }
        return drainInternal(resource.getAmount(), doDrain);
    }

    @Nullable
    public CompatFluidStack drainInternal(int maxDrain, boolean doDrain) {
        if (getFluid() == null || maxDrain <= 0) {
            return null;
        }

        int drained = maxDrain;
        if (getFluidAmount() < drained) {
            drained = getFluidAmount();
        }

        CompatFluidStack stack = new CompatFluidStack(getFluid(), drained);
        if (doDrain) {
            getFluid().setAmount(getFluidAmount() - drained);
            if (getFluidAmount() <= 0) {
                fluid = null;
            }

            onContentsChanged();
        }
        return stack;
    }

    public int fillInternal(CompatFluidStack resource, boolean doFill) {
        if (resource == null || resource.getAmount() <= 0) {
            return 0;
        }

        if (!doFill) {
            if (fluid == null) {
                return Math.min(capacity, resource.getAmount());
            }

            if (!fluid.isFluidEqual(resource)) {
                return 0;
            }

            return Math.min(capacity - fluid.getAmount(), resource.getAmount());
        }

        if (fluid == null) {
            fluid = new CompatFluidStack(resource, Math.min(capacity, resource.getAmount()));

            onContentsChanged();

            return fluid.getAmount();
        }

        if (!fluid.isFluidEqual(resource)) {
            return 0;
        }
        int filled = capacity - fluid.getAmount();

        if (resource.getAmount() < filled) {
            fluid.setAmount(fluid.getAmount() + resource.getAmount());
            filled = resource.getAmount();
        } else {
            fluid.setAmount(capacity);
        }

        onContentsChanged();

        return filled;
    }

    public boolean canFill() {
        return canFill;
    }

    public boolean canDrain() {
        return canDrain;
    }

    public void setCanFill(boolean canFill) {
        this.canFill = canFill;
    }

    public void setCanDrain(boolean canDrain) {
        this.canDrain = canDrain;
    }

    public boolean canFillFluidType(CompatFluidStack fluid) {
        return canFill();
    }

    public boolean canDrainFluidType(@Nullable CompatFluidStack fluid) {
        return fluid != null && canDrain();
    }

    protected void onContentsChanged() {}
}
