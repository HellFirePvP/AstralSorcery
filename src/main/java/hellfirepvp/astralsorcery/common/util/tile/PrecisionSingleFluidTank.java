/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.tile;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PrecisionSingleFluidTank
 * Created by HellFirePvP
 * Date: 30.06.2019 / 22:30
 */
public class PrecisionSingleFluidTank implements IFluidTank {

    private double amount = 0;
    private int maxCapacity;
    private Fluid fluid = Fluids.EMPTY;
    private Runnable onUpdate = null;

    private boolean allowInput = true, allowOutput = true;

    private PrecisionSingleFluidTank() {}

    public PrecisionSingleFluidTank(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public void setOnUpdate(Runnable onUpdate) {
        this.onUpdate = onUpdate;
    }

    public void setAllowInput(boolean allowInput) {
        this.allowInput = allowInput;
    }

    public void setAllowOutput(boolean allowOutput) {
        this.allowOutput = allowOutput;
    }

    //returns min(toAdd, what can be added at most)
    public double getMaxAddable(double toAdd) {
        return Math.min(toAdd, maxCapacity - amount);
    }

    public int getMaxDrainable(double toDrain) {
        return (int) Math.floor(Math.min(toDrain, amount));
    }

    //leftover amount that could not be added
    public double addAmount(double amount) {
        if (this.fluid == Fluids.EMPTY) {
            return amount;
        }
        double addable = getMaxAddable(amount);
        this.amount += addable;
        if (Math.abs(addable) > 0 && this.onUpdate != null) {
            this.onUpdate.run();
        }
        return amount - addable;
    }

    //returns amount drained
    @Nonnull
    public FluidStack drain(double amount) {
        if (this.fluid == Fluids.EMPTY) {
            return FluidStack.EMPTY;
        }
        int drainable = getMaxDrainable(amount);
        this.amount -= drainable;
        Fluid drainedFluid = this.fluid;
        if (this.amount <= 0) {
            setFluid(Fluids.EMPTY);
        }
        if (Math.abs(drainable) > 0 && this.onUpdate != null) {
            this.onUpdate.run();
        }
        return new FluidStack(drainedFluid, drainable);
    }

    @Nonnull
    @Override
    public FluidStack getFluid() {
        if (fluid == Fluids.EMPTY) {
            return FluidStack.EMPTY;
        }
        return new FluidStack(fluid, getFluidAmount());
    }

    public void setFluid(@Nonnull Fluid fluid) {
        boolean update = false;
        if (fluid != this.fluid) {
            this.amount = 0;
            update = true;
        }
        this.fluid = fluid;
        if (update && this.onUpdate != null) {
            this.onUpdate.run();
        }
    }

    @Override
    public int getFluidAmount() {
        return MathHelper.floor(amount);
    }

    @Override
    public int getCapacity() {
        return this.maxCapacity;
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return true;
    }

    public boolean canFill() {
        return this.allowInput && this.amount < this.maxCapacity;
    }

    public boolean canDrain() {
        return this.allowOutput && this.amount > 0 && this.fluid != Fluids.EMPTY;
    }

    public boolean canFillFluidType(FluidStack fluidStack) {
        return canFill() && (this.fluid == Fluids.EMPTY || fluidStack.getFluid().equals(this.fluid));
    }

    public boolean canDrainFluidType(FluidStack fluidStack) {
        return canDrain() && (this.fluid != Fluids.EMPTY && fluidStack.getFluid().equals(this.fluid));
    }

    public float getPercentageFilled() {
        return (((float) amount) / ((float) maxCapacity));
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (!canFillFluidType(resource)) {
            return 0;
        }
        int maxAdded = resource.getAmount();
        int addable = MathHelper.floor(getMaxAddable(maxAdded));
        if (action.execute()) {
            if (addable > 0 && this.fluid == Fluids.EMPTY) {
                setFluid(resource.getFluid());
            }
            addable -= addAmount(addable);
        }
        return addable;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        if (!canDrain()) {
            return FluidStack.EMPTY;
        }
        int maxDrainable = getMaxDrainable(maxDrain);
        if (action.execute()) {
            return drain(maxDrainable);
        }
        return new FluidStack(this.fluid, maxDrainable);
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (!canDrainFluidType(resource)) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    public CompoundNBT writeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putDouble("amt", this.amount);
        tag.putInt("capacity", this.maxCapacity);
        tag.putBoolean("aIn", this.allowInput);
        tag.putBoolean("aOut", this.allowOutput);
        tag.putString("fluid", this.fluid.getRegistryName().toString());
        return tag;
    }

    public void readNBT(CompoundNBT tag) {
        this.amount = tag.getDouble("amt");
        this.maxCapacity = tag.getInt("capacity");
        this.allowInput = tag.getBoolean("aIn");
        this.allowOutput = tag.getBoolean("aOut");
        this.fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(tag.getString("fluid")));
    }

    public static PrecisionSingleFluidTank deserialize(CompoundNBT tag) {
        PrecisionSingleFluidTank tank = new PrecisionSingleFluidTank();
        tank.readNBT(tag);
        return tank;
    }

}
