/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.tank;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FluidTankView
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FluidTankView implements IFluidHandler {

    private final int tanks;
    private final FluidContainerList contents;
    private final Set<Direction> applicableSides;
    private final Consumer<Integer> changeListener;
    private final Function<Integer, Integer> tankCapacityGetter;
    private InputFilter inputFilter;
    private ExtractFilter extractFilter;

    public FluidTankView(int tanks,
                         FluidContainerList contents,
                         Set<Direction> applicableSides,
                         Consumer<Integer> changeListener,
                         Function<Integer, Integer> tankCapacityGetter,
                         InputFilter inputFilter,
                         ExtractFilter extractFilter) {
        this.tanks = tanks;
        this.contents = contents;
        this.applicableSides = applicableSides;
        this.changeListener = changeListener;
        this.tankCapacityGetter = tankCapacityGetter;
        this.inputFilter = inputFilter;
        this.extractFilter = extractFilter;
    }

    private FluidContainer getTank(int tank) {
        this.validateTankAccess(tank);
        return this.contents.getTank(tank);
    }

    protected void validateTankAccess(int tank) {
        if (tank >= this.getTanks()) {
            throw new IndexOutOfBoundsException("Tank " + tank + " not in valid range - [0, " + this.getTanks() + ")");
        }
    }

    public void withoutFilters(Consumer<FluidTankView> run) {
        this.getWithoutFilters(tank -> {
            run.accept(tank);
            return null;
        });
    }

    public <T> T getWithoutFilters(Function<FluidTankView, T> fn) {
        InputFilter in = this.inputFilter;
        ExtractFilter ex = this.extractFilter;
        this.inputFilter = InputFilter.NO_FILTER;
        this.extractFilter = ExtractFilter.NO_FILTER;
        try {
            return fn.apply(this);
        } finally {
            this.inputFilter = in;
            this.extractFilter = ex;
        }
    }

    @Override
    public int getTanks() {
        return this.tanks;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        this.validateTankAccess(tank);
        return this.getTank(tank).getContent();
    }

    @Override
    public int getTankCapacity(int tank) {
        this.validateTankAccess(tank);
        return this.tankCapacityGetter.apply(tank);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        this.validateTankAccess(tank);
        return this.inputFilter.canInsert(tank, stack, this.getFluidInTank(tank));
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) {
            return 0;
        }

        FluidStack fillStack = resource.copy();
        int filled = 0;
        for (int tank = 0; tank < this.getTanks(); tank++) {
            if (!fillStack.isEmpty() && this.isFluidValid(tank, fillStack)) {
                int tankFilled = this.fillTank(tank, fillStack, action);
                fillStack.shrink(tankFilled);
                filled += tankFilled;
            }
        }
        return filled;
    }

    private int fillTank(int tankIndex, FluidStack fillStack, FluidAction action) {
        FluidContainer tank = this.getTank(tankIndex);
        FluidStack tankContent = tank.getContent();
        int capacity = this.getTankCapacity(tankIndex);

        if (action.simulate()) {
            if (tankContent.isEmpty()) {
                return Math.min(capacity, fillStack.getAmount());
            }
            if (!FluidStack.isSameFluidSameComponents(tankContent, fillStack)) {
                return 0;
            }
            return Math.min(capacity - tankContent.getAmount(), fillStack.getAmount());
        }

        if (tankContent.isEmpty()) {
            FluidStack newContent = fillStack.copyWithAmount(Math.min(capacity, fillStack.getAmount()));
            tank.setContent(newContent);
            this.onContentChanged(tankIndex);
            return newContent.getAmount();
        }
        if (!FluidStack.isSameFluidSameComponents(tankContent, fillStack)) {
            return 0;
        }

        int maxFillable = capacity - tankContent.getAmount();
        if (maxFillable <= 0) {
            return 0;
        }

        if (fillStack.getAmount() < maxFillable) {
            tank.getModifiableContent().grow(fillStack.getAmount());
            maxFillable = fillStack.getAmount();
        } else {
            tank.getModifiableContent().setAmount(capacity);
        }
        this.onContentChanged(tankIndex);
        return maxFillable;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) {
            return FluidStack.EMPTY;
        }
        return this.drainInternal(resource.getAmount(), this.getDrainableTanks(resource), action);
    }

    private List<Integer> getDrainableTanks(FluidStack resource) {
        return IntStream.range(0, this.getTanks())
                .filter(tank -> !this.getTank(tank).getContent().isEmpty())
                .filter(tank -> FluidStack.isSameFluidSameComponents(this.getTank(tank).getContent(), resource))
                .boxed()
                .toList();
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        List<Integer> allTanks = IntStream.range(0, this.getTanks()).boxed().toList();
        return this.drainInternal(maxDrain, allTanks, action);
    }

    private FluidStack drainInternal(int maxDrain, List<Integer> eligibleTanks, FluidAction action) {
        FluidStack drained = FluidStack.EMPTY;
        int toDrain = maxDrain;
        for (int tankId : eligibleTanks) {
            if (toDrain > 0) {
                FluidStack tankDrain = this.drainTank(tankId, drained, toDrain, action);
                toDrain -= tankDrain.getAmount();
                if (drained.isEmpty()) {
                    drained = tankDrain;
                } else {
                    drained.grow(tankDrain.getAmount());
                }
            }
        }
        return drained;
    }

    //fluidstack that Was drained
    private FluidStack drainTank(int tankIndex, FluidStack drainedType, int maxDrain, FluidAction action) {
        FluidContainer tank = this.getTank(tankIndex);
        FluidStack tankContent = tank.getContent();

        if (tankContent.isEmpty() || !this.extractFilter.canExtract(tankIndex, maxDrain, tankContent)) {
            return FluidStack.EMPTY;
        }
        if (!drainedType.isEmpty() && !FluidStack.isSameFluidSameComponents(tankContent, drainedType)) {
            return FluidStack.EMPTY;
        }

        int canDrain = Math.min(maxDrain, tankContent.getAmount());
        if (action.execute()) {
            tank.getModifiableContent().shrink(canDrain);
            this.onContentChanged(tankIndex);
        }
        return tankContent.copyWithAmount(canDrain);
    }

    public void clearTanks() {
        IntStream.range(0, this.getTanks()).forEach(this::clearTank);
    }

    public void clearTank(int tank) {
        this.getTank(tank).clear();
        this.onContentChanged(tank);
    }

    private void onContentChanged(int tank) {
        this.changeListener.accept(tank);
    }

    protected boolean hasHandlerForSide(@Nullable Direction facing) {
        return facing == null || this.applicableSides.contains(facing);
    }

    @Nullable
    public FluidTankView getFluidAccess(@Nullable Direction direction) {
        if (!this.hasHandlerForSide(direction)) {
            return null;
        }
        return this;
    }

    @FunctionalInterface
    public interface InputFilter {

        InputFilter NO_FILTER = (tank, toAdd, existing) -> true;

        boolean canInsert(int tank, FluidStack toAdd, @Nonnull FluidStack existing);

        default InputFilter and(InputFilter other) {
            return (tank, toAdd, existing) ->
                    other.canInsert(tank, toAdd, existing) && this.canInsert(tank, toAdd, existing);
        }
    }

    @FunctionalInterface
    public interface ExtractFilter {

        ExtractFilter NO_FILTER = (tank, amount, existing) -> true;

        boolean canExtract(int tank, int amount, @Nonnull FluidStack existing);

        default ExtractFilter and(ExtractFilter other) {
            return (tank, amount, existing) ->
                    other.canExtract(tank, amount, existing) && this.canExtract(tank, amount, existing);
        }
    }
}
