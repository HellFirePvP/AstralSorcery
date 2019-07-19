/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.fluid.handler;

import hellfirepvp.astralsorcery.common.util.fluid.CompatFluidStack;
import hellfirepvp.astralsorcery.common.util.fluid.handler.tank.ICompatFluidTankProperties;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ICompatFluidHandler
 * Created by HellFirePvP
 * Date: 19.07.2019 / 13:07
 */
public interface ICompatFluidHandler {

    ICompatFluidTankProperties[] getTankProperties();

    int fill(CompatFluidStack resource, boolean doFill);

    @Nullable
    CompatFluidStack drain(CompatFluidStack resource, boolean doDrain);

    @Nullable
    CompatFluidStack drain(int maxDrain, boolean doDrain);
}
