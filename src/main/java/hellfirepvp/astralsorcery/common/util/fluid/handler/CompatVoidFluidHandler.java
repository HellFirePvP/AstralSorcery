/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.fluid.handler;

import hellfirepvp.astralsorcery.common.util.fluid.CompatFluidStack;
import hellfirepvp.astralsorcery.common.util.fluid.handler.tank.CompatFluidTankInfo;
import hellfirepvp.astralsorcery.common.util.fluid.handler.tank.ICompatFluidTank;
import hellfirepvp.astralsorcery.common.util.fluid.handler.tank.ICompatFluidTankProperties;

import javax.annotation.Nullable;

import static hellfirepvp.astralsorcery.common.util.fluid.handler.CompatEmptyFluidHandler.EMPTY_TANK_INFO;
import static hellfirepvp.astralsorcery.common.util.fluid.handler.CompatEmptyFluidHandler.EMPTY_TANK_PROPERTIES_ARRAY;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CompatVoidFluidHandler
 * Created by HellFirePvP
 * Date: 19.07.2019 / 15:42
 */
public class CompatVoidFluidHandler implements ICompatFluidHandler, ICompatFluidTank {

    public static final CompatVoidFluidHandler INSTANCE = new CompatVoidFluidHandler();

    public CompatVoidFluidHandler() {}

    @Override
    public ICompatFluidTankProperties[] getTankProperties() {
        return EMPTY_TANK_PROPERTIES_ARRAY;
    }

    @Nullable
    @Override
    public CompatFluidStack getFluid() {
        return null;
    }

    @Override
    public int getFluidAmount() {
        return 0;
    }

    @Override
    public int getCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public CompatFluidTankInfo getInfo() {
        return EMPTY_TANK_INFO;
    }

    @Override
    public int fill(CompatFluidStack resource, boolean doFill) {
        return resource.getAmount();
    }

    @Nullable
    @Override
    public CompatFluidStack drain(CompatFluidStack resource, boolean doDrain) {
        return null;
    }

    @Nullable
    @Override
    public CompatFluidStack drain(int maxDrain, boolean doDrain) {
        return null;
    }
}
