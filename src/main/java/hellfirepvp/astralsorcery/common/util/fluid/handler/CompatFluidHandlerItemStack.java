/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.fluid.handler;

import hellfirepvp.astralsorcery.common.lib.CapabilitiesAS;
import hellfirepvp.astralsorcery.common.util.fluid.CompatFluidStack;
import hellfirepvp.astralsorcery.common.util.fluid.handler.tank.CompatFluidTankProperties;
import hellfirepvp.astralsorcery.common.util.fluid.handler.tank.ICompatFluidTankProperties;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CompatFluidHandlerItemStack
 * Created by HellFirePvP
 * Date: 19.07.2019 / 18:07
 */
public class CompatFluidHandlerItemStack implements ICompatFluidHandlerItem, ICapabilityProvider {

    public static final String FLUID_NBT_KEY = "Fluid";

    private final LazyOptional<ICompatFluidHandlerItem> holder = LazyOptional.of(() -> this);

    protected ItemStack container;
    protected int capacity;

    public CompatFluidHandlerItemStack(ItemStack container, int capacity) {
        this.container = container;
        this.capacity = capacity;
    }

    @Nonnull
    @Override
    public ItemStack getContainer() {
        return this.container;
    }

    @Nullable
    public CompatFluidStack getFluid() {
        CompoundNBT nbt = this.getContainer().getTag();
        if (nbt == null || !nbt.contains(FLUID_NBT_KEY)) {
            return null;
        }
        return CompatFluidStack.deserialize(nbt.getCompound(FLUID_NBT_KEY));
    }

    protected void setFluid(CompatFluidStack fluid) {
        if (!container.hasTag()) {
            container.setTag(new CompoundNBT());
        }

        container.getTag().put(FLUID_NBT_KEY, fluid.serialize());
    }

    @Override
    public ICompatFluidTankProperties[] getTankProperties() {
        return new ICompatFluidTankProperties[] { new CompatFluidTankProperties(getFluid(), this.capacity)};
    }

    @Override
    public int fill(CompatFluidStack resource, boolean doFill) {
        if (container.getCount() != 1 ||
                resource == null ||
                resource.getAmount() <= 0 ||
                !canFillFluidType(resource)) {
            return 0;
        }

        CompatFluidStack contained = getFluid();
        if (contained == null) {
            int fillAmount = Math.min(capacity, resource.getAmount());

            if (doFill) {
                CompatFluidStack filled = resource.copy();
                filled.setAmount(filled.getAmount() + fillAmount);
                setFluid(filled);
            }

            return fillAmount;
        } else {
            if (contained.isFluidEqual(resource)) {
                int fillAmount = Math.min(capacity - contained.getAmount(), resource.getAmount());

                if (doFill && fillAmount > 0) {
                    contained.setAmount(contained.getAmount() + fillAmount);
                    setFluid(contained);
                }

                return fillAmount;
            }

            return 0;
        }
    }

    @Nullable
    @Override
    public CompatFluidStack drain(CompatFluidStack resource, boolean doDrain) {
        if (container.getCount() != 1 ||
                resource == null ||
                resource.getAmount() <= 0 ||
                !resource.isFluidEqual(getFluid())) {
            return null;
        }
        return drain(resource.getAmount(), doDrain);
    }

    @Nullable
    @Override
    public CompatFluidStack drain(int maxDrain, boolean doDrain) {
        if (container.getCount() != 1 || maxDrain <= 0) {
            return null;
        }

        CompatFluidStack contained = getFluid();
        if (contained == null || contained.getAmount() <= 0 || !canDrainFluidType(contained)) {
            return null;
        }

        final int drainAmount = Math.min(contained.getAmount(), maxDrain);

        CompatFluidStack drained = contained.copy();
        drained.setAmount(drainAmount);

        if (doDrain) {
            contained.setAmount(contained.getAmount() - drainAmount);
            if (contained.getAmount() == 0) {
                setContainerToEmpty();
            } else {
                setFluid(contained);
            }
        }

        return drained;
    }

    public boolean canFillFluidType(CompatFluidStack fluid) {
        return true;
    }

    public boolean canDrainFluidType(CompatFluidStack fluid) {
        return true;
    }

    protected void setContainerToEmpty() {
        container.getTag().remove(FLUID_NBT_KEY);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return CapabilitiesAS.FLUID_HANDLER_ITEM_COMPAT.orEmpty(cap, holder);
    }
}
