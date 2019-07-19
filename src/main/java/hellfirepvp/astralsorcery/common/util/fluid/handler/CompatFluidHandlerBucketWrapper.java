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
import hellfirepvp.astralsorcery.common.util.fluid.CompatFluidUtil;
import hellfirepvp.astralsorcery.common.util.fluid.handler.tank.CompatFluidTankProperties;
import hellfirepvp.astralsorcery.common.util.fluid.handler.tank.ICompatFluidTankProperties;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CompatFluidHandlerBucketWrapper
 * Created by HellFirePvP
 * Date: 19.07.2019 / 22:05
 */
public class CompatFluidHandlerBucketWrapper implements ICompatFluidHandlerItem, ICapabilityProvider {

    private final LazyOptional<ICompatFluidHandlerItem> holder = LazyOptional.of(() -> this);

    protected ItemStack container;

    public CompatFluidHandlerBucketWrapper(@Nonnull ItemStack container) {
        this.container = container;
    }

    @Nonnull
    @Override
    public ItemStack getContainer() {
        return this.container;
    }

    public boolean canFillFluidType(CompatFluidStack fluidStack) {
        Fluid fluid = fluidStack.getFluid();
        if (fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER ||
                fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA) {
            return true;
        }
        return false;
    }

    @Nullable
    public CompatFluidStack getFluid() {
        Item item = this.getContainer().getItem();
        if (item == Items.LAVA_BUCKET) {
            return new CompatFluidStack(Fluids.LAVA, CompatFluidStack.BUCKET_VOLUME);
        } else if (item == Items.WATER_BUCKET) {
            return new CompatFluidStack(Fluids.WATER, CompatFluidStack.BUCKET_VOLUME);
        }
        return null;
    }

    protected void setFluid(@Nullable CompatFluidStack fluidStack) {
        if (fluidStack == null) {
            container = new ItemStack(Items.BUCKET);
        } else {
            container = CompatFluidUtil.getFilledBucket(fluidStack);
        }
    }

    @Override
    public ICompatFluidTankProperties[] getTankProperties() {
        return new ICompatFluidTankProperties[] { new CompatFluidTankProperties(getFluid(), CompatFluidStack.BUCKET_VOLUME)};
    }

    @Override
    public int fill(CompatFluidStack resource, boolean doFill) {
        if (container.getCount() != 1 ||
                resource == null ||
                resource.getAmount() < CompatFluidStack.BUCKET_VOLUME ||
                getFluid() != null ||
                !canFillFluidType(resource)) {
            return 0;
        }

        if (doFill) {
            setFluid(resource);
        }

        return CompatFluidStack.BUCKET_VOLUME;
    }

    @Nullable
    @Override
    public CompatFluidStack drain(CompatFluidStack resource, boolean doDrain) {
        if (container.getCount() != 1 ||
                resource == null ||
                resource.getAmount() < CompatFluidStack.BUCKET_VOLUME) {
            return null;
        }

        CompatFluidStack fluidStack = getFluid();
        if (fluidStack != null && fluidStack.isFluidEqual(resource)) {
            if (doDrain) {
                setFluid(null);
            }
            return fluidStack;
        }

        return null;
    }

    @Nullable
    @Override
    public CompatFluidStack drain(int maxDrain, boolean doDrain) {
        if (container.getCount() != 1 ||
                maxDrain < CompatFluidStack.BUCKET_VOLUME) {
            return null;
        }

        CompatFluidStack fluidStack = getFluid();
        if (fluidStack != null) {
            if (doDrain) {
                setFluid(null);
            }
            return fluidStack;
        }

        return null;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return CapabilitiesAS.FLUID_HANDLER_ITEM_COMPAT.orEmpty(cap, holder);
    }
}
