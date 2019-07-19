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
 * Class: ICompatFluidTank
 * Created by HellFirePvP
 * Date: 19.07.2019 / 13:38
 */
public interface ICompatFluidTank {

    @Nullable
    CompatFluidStack getFluid();

    int getFluidAmount();

    int getCapacity();

    CompatFluidTankInfo getInfo();

    int fill(CompatFluidStack resource, boolean doFill);

    @Nullable
    CompatFluidStack drain(int maxDrain, boolean doDrain);

}
