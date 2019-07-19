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
import hellfirepvp.astralsorcery.common.util.fluid.handler.tank.CompatFluidTankProperties;
import hellfirepvp.astralsorcery.common.util.fluid.handler.tank.ICompatFluidTank;
import hellfirepvp.astralsorcery.common.util.fluid.handler.tank.ICompatFluidTankProperties;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CompatEmptyFluidHandler
 * Created by HellFirePvP
 * Date: 19.07.2019 / 15:38
 */
public class CompatEmptyFluidHandler implements ICompatFluidHandler, ICompatFluidTank {

    public static final CompatEmptyFluidHandler INSTANCE = new CompatEmptyFluidHandler();
    public static final CompatFluidTankInfo EMPTY_TANK_INFO = new CompatFluidTankInfo(null, 0);
    public static final ICompatFluidTankProperties EMPTY_TANK_PROPERTIES = new CompatFluidTankProperties(null, 0, false, false);
    public static final ICompatFluidTankProperties[] EMPTY_TANK_PROPERTIES_ARRAY = new ICompatFluidTankProperties[] { EMPTY_TANK_PROPERTIES };

    protected CompatEmptyFluidHandler() {}

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
        return 0;
    }

    @Override
    public CompatFluidTankInfo getInfo() {
        return EMPTY_TANK_INFO;
    }

    @Override
    public int fill(CompatFluidStack resource, boolean doFill) {
        return 0;
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
