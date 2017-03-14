package hellfirepvp.astralsorcery.common.util.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PrecisionSingleFluidCapabilityTank
 * Created by HellFirePvP
 * Date: 10.03.2017 / 16:36
 */
public class PrecisionSingleFluidCapabilityTank  implements IFluidTank, IFluidTankProperties, IFluidHandler {

    private double amount = 0;
    private int maxCapacity;
    private Fluid fluid = null;

    private boolean allowInput = true, allowOutput = true;

    private List<EnumFacing> accessibleSides = new ArrayList<>();
    private boolean acceptNullCapabilityAccess;

    private PrecisionSingleFluidCapabilityTank() {}

    public PrecisionSingleFluidCapabilityTank(int maxCapacity) {
        this(maxCapacity, EnumFacing.VALUES);
    }

    public PrecisionSingleFluidCapabilityTank(int maxCapacity, boolean acceptNull) {
        this(maxCapacity, acceptNull, EnumFacing.VALUES);
    }

    public PrecisionSingleFluidCapabilityTank(int capacity, EnumFacing... accessibleFrom) {
        this(capacity, false, accessibleFrom);
    }

    public PrecisionSingleFluidCapabilityTank(int capacity, boolean acceptNull, EnumFacing... accessibleFrom) {
        this.maxCapacity = Math.max(0, capacity);
        this.accessibleSides = Arrays.asList(accessibleFrom);
        this.acceptNullCapabilityAccess = acceptNull;
    }

    public void setAllowInput(boolean allowInput) {
        this.allowInput = allowInput;
    }

    public void setAllowOutput(boolean allowOutput) {
        this.allowOutput = allowOutput;
    }

    //returns min(toAdd, what can be added at most)
    public double getMaxAddable(double toAdd) {
        return Math.min(toAdd, maxCapacity - toAdd);
    }

    public int getMaxDrainable(double toDrain) {
        return (int) Math.floor(Math.min(toDrain, amount));
    }

    //leftover amount that could not be added
    public double addAmount(double amount) {
        if (this.fluid == null) return amount;
        double addable = getMaxAddable(amount);
        this.amount += addable;
        return amount - addable;
    }

    //returns amount drained
    public int drain(double amount) {
        if (this.fluid == null) return 0;
        int drainable = getMaxDrainable(amount);
        this.amount -= drainable;
        if (this.amount <= 0) {
            setFluid(null);
        }
        return drainable;
    }

    @Nullable
    @Override
    public FluidStack getFluid() {
        if (fluid == null) return null;
        return new FluidStack(fluid, getFluidAmount());
    }

    @Nullable
    public Fluid getTankFluid() {
        return fluid;
    }

    public void setFluid(@Nullable Fluid fluid) {
        if (fluid != this.fluid) {
            this.amount = 0;
        }
        this.fluid = fluid;
    }

    @Override
    public int getFluidAmount() {
        return MathHelper.floor(amount);
    }

    @Nullable
    @Override
    public FluidStack getContents() {
        return getFluid();
    }

    @Override
    public int getCapacity() {
        return this.maxCapacity;
    }

    @Override
    public boolean canFill() {
        return this.allowInput && this.amount < this.maxCapacity;
    }

    @Override
    public boolean canDrain() {
        return this.allowOutput && this.amount > 0 && this.fluid != null;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluidStack) {
        return canFill() && (this.fluid == null || fluidStack.getFluid().equals(this.fluid));
    }

    @Override
    public boolean canDrainFluidType(FluidStack fluidStack) {
        return canDrain() && (this.fluid != null && fluidStack.getFluid().equals(this.fluid));
    }

    public float getPercentageFilled() {
        return (((float) amount) / ((float) maxCapacity));
    }

    @Override
    public FluidTankInfo getInfo() {
        return new FluidTankInfo(this);
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[] { this };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (!canFillFluidType(resource)) return 0;
        int maxAdded = resource.amount;
        int addable = MathHelper.floor(getMaxAddable(maxAdded));
        if(doFill) {
            addable = MathHelper.floor(maxAdded - addAmount(addable));
        }
        return addable;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (!canDrainFluidType(resource)) return null;
        return drain(resource.amount, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (!canDrain()) return null;
        int maxDrainable = getMaxDrainable(maxDrain);
        if (doDrain) {
            maxDrainable = drain(maxDrainable);
        }
        return new FluidStack(this.fluid, maxDrainable);
    }

    public NBTTagCompound writeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setDouble("amt", this.amount);
        tag.setInteger("capacity", this.maxCapacity);
        tag.setBoolean("aIn", this.allowInput);
        tag.setBoolean("aOut", this.allowOutput);
        if(this.fluid != null) {
            tag.setString("fluid", this.fluid.getName());
        }
        tag.setBoolean("allowNull", this.acceptNullCapabilityAccess);
        int[] sides = new int[accessibleSides.size()];
        for (int i = 0; i < accessibleSides.size(); i++) {
            EnumFacing side = accessibleSides.get(i);
            sides[i] = side.ordinal();
        }
        tag.setIntArray("sides", sides);
        return tag;
    }

    public void readNBT(NBTTagCompound tag) {
        this.amount = tag.getDouble("amt");
        this.maxCapacity = tag.getInteger("capacity");
        this.allowInput = tag.getBoolean("aIn");
        this.allowOutput = tag.getBoolean("aOut");
        if(tag.hasKey("fluid")) {
            this.fluid = FluidRegistry.getFluid(tag.getString("fluid"));
        } else {
            this.fluid = null;
        }
        this.acceptNullCapabilityAccess = tag.getBoolean("allowNull");
        int[] sides = tag.getIntArray("sides");
        for (int i : sides) {
            this.accessibleSides.add(EnumFacing.values()[i]);
        }
    }

    public static PrecisionSingleFluidCapabilityTank deserialize(NBTTagCompound tag) {
        PrecisionSingleFluidCapabilityTank tank = new PrecisionSingleFluidCapabilityTank();
        tank.readNBT(tag);
        return tank;
    }

    public boolean hasCapability(EnumFacing facing) {
        return (facing == null && acceptNullCapabilityAccess) || accessibleSides.contains(facing);
    }

    public IFluidHandler getCapability(EnumFacing facing) {
        if(hasCapability(facing)) {
            return this;
        }
        return null;
    }


}
