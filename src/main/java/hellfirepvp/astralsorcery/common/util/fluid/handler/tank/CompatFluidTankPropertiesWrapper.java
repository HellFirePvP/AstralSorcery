/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.fluid.handler.tank;

import hellfirepvp.astralsorcery.common.util.fluid.CompatFluidStack;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CompatFluidTankPropertiesWrapper
 * Created by HellFirePvP
 * Date: 19.07.2019 / 14:22
 */
public class CompatFluidTankPropertiesWrapper implements ICompatFluidTankProperties {

    protected final CompatFluidTank tank;

    public CompatFluidTankPropertiesWrapper(CompatFluidTank tank) {
        this.tank = tank;
    }

    @Nullable
    @Override
    public CompatFluidStack getContents() {
        CompatFluidStack contents = tank.getFluid();
        return contents == null ? null : contents.copy();
    }

    @Override
    public int getCapacity() {
        return tank.getCapacity();
    }

    @Override
    public boolean canFill() {
        return tank.canFill();
    }

    @Override
    public boolean canDrain() {
        return tank.canDrain();
    }

    @Override
    public boolean canFillFluidType(CompatFluidStack fluidStack) {
        return tank.canFillFluidType(fluidStack);
    }

    @Override
    public boolean canDrainFluidType(CompatFluidStack fluidStack) {
        return tank.canDrainFluidType(fluidStack);
    }
}
