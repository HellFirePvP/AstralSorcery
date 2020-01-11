/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.tile;

import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidTankAccess
 * Created by HellFirePvP
 * Date: 19.09.2019 / 16:57
 */
public class FluidTankAccess {

    private Set<AccessibleTank> tanks = new HashSet<>();

    public void putTank(int tankId, IFluidTank tank, Direction... sides) {
        this.tanks.add(new AccessibleTank(tankId, tank, sides));
    }

    public void putTank(int tankId, IFluidTank tank, Predicate<Direction> accessibleSides) {
        this.tanks.add(new AccessibleTank(tankId, tank, accessibleSides));
    }

    private boolean hasTanksForSide(Direction dir) {
        return dir == null || MiscUtils.contains(this.tanks, tank -> tank.accessibleSides.test(dir));
    }

    public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
        return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY == capability && hasTanksForSide(facing);
    }

    public LazyOptional<IFluidHandler> getCapability(@Nullable Direction facing) {
        Set<AccessibleTank> available = facing == null ? this.tanks :
                this.tanks.stream().filter(t -> t.isAccessible(facing)).collect(Collectors.toSet());
        return available.isEmpty() ? LazyOptional.empty() : LazyOptional.of(() -> new SidedAccess(available));
    }

    private static class SidedAccess implements IFluidHandler {

        private final Set<AccessibleTank> tanks;

        private SidedAccess(Set<AccessibleTank> accessibleTanks) {
            this.tanks = accessibleTanks;
        }

        private Optional<AccessibleTank> getTank(int id) {
            return this.tanks.stream().filter(tank -> tank.getId() == id).findFirst();
        }

        @Override
        public int getTanks() {
            return this.tanks.size();
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            return this.getTank(tank).map(t -> t.getTank().getFluid()).orElse(FluidStack.EMPTY);
        }

        @Override
        public int getTankCapacity(int tank) {
            return this.getTank(tank).map(t -> t.getTank().getCapacity()).orElse(0);
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            return this.getTank(tank).map(t -> t.getTank().isFluidValid(stack)).orElse(false);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            for (AccessibleTank tank : this.tanks) {
                int filled = tank.getTank().fill(resource, action);
                if (filled > 0) {
                    return filled;
                }
            }
            return 0;
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            for (AccessibleTank tank : this.tanks) {
                FluidStack drained = tank.getTank().drain(resource, action);
                if (!drained.isEmpty()) {
                    return drained;
                }
            }
            return FluidStack.EMPTY;
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            for (AccessibleTank tank : this.tanks) {
                FluidStack drained = tank.getTank().drain(maxDrain, action);
                if (!drained.isEmpty()) {
                    return drained;
                }
            }
            return FluidStack.EMPTY;
        }
    }

    private static class AccessibleTank {

        private final int id;
        private final IFluidTank tank;
        private final Predicate<Direction> accessibleSides;

        private AccessibleTank(int id, IFluidTank tank, Direction... sides) {
            this(id, tank, (side) -> Arrays.asList(sides).contains(side));
        }

        private AccessibleTank(int id, IFluidTank tank, Predicate<Direction> accessibleSides) {
            this.id = id;
            this.tank = tank;
            this.accessibleSides = accessibleSides;
        }

        private IFluidTank getTank() {
            return tank;
        }

        private int getId() {
            return id;
        }

        private boolean isAccessible(Direction side) {
            return accessibleSides.test(side);
        }
    }
}
