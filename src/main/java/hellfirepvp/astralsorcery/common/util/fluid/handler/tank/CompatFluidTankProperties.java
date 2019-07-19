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
 * Class: CompatFluidTankProperties
 * Created by HellFirePvP
 * Date: 19.07.2019 / 15:40
 */
public class CompatFluidTankProperties implements ICompatFluidTankProperties {

    private final CompatFluidStack contents;
    private final int capacity;
    private final boolean canFill;
    private final boolean canDrain;

    public CompatFluidTankProperties(@Nullable CompatFluidStack contents, int capacity) {
        this(contents, capacity, true, true);
    }

    public CompatFluidTankProperties(@Nullable CompatFluidStack contents, int capacity, boolean canFill, boolean canDrain) {
        this.contents = contents;
        this.capacity = capacity;
        this.canFill = canFill;
        this.canDrain = canDrain;
    }

    @Nullable
    @Override
    public CompatFluidStack getContents() {
        return this.contents == null ? null : this.contents.copy();
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public boolean canFill() {
        return this.canFill;
    }

    @Override
    public boolean canDrain() {
        return this.canDrain;
    }

    @Override
    public boolean canFillFluidType(CompatFluidStack fluidStack) {
        return this.canFill();
    }

    @Override
    public boolean canDrainFluidType(CompatFluidStack fluidStack) {
        return this.canDrain();
    }
}
