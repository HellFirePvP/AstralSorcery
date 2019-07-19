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
 * Class: ICompatFluidTankProperties
 * Created by HellFirePvP
 * Date: 19.07.2019 / 13:37
 */
public interface ICompatFluidTankProperties {

    @Nullable
    CompatFluidStack getContents();

    int getCapacity();

    boolean canFill();

    boolean canDrain();

    boolean canFillFluidType(CompatFluidStack fluidStack);

    boolean canDrainFluidType(CompatFluidStack fluidStack);

}
