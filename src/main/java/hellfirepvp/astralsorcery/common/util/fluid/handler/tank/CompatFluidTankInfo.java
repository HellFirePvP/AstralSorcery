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
 * Class: CompatFluidTankInfo
 * Created by HellFirePvP
 * Date: 19.07.2019 / 13:39
 */
public class CompatFluidTankInfo {

    public final CompatFluidStack fluid;
    public final int capacity;

    public CompatFluidTankInfo(@Nullable CompatFluidStack fluid, int capacity) {
        this.fluid = fluid;
        this.capacity = capacity;
    }

    public CompatFluidTankInfo(ICompatFluidTank tank) {
        this.fluid = tank.getFluid();
        this.capacity = tank.getCapacity();
    }
}
