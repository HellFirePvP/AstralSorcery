/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.capability;

import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lumen.ILumenHandler;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.LumenLike;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import net.minecraft.core.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenHandlerView
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenHandlerView implements ILumenHandler {

    private final LumenStackList contents;
    private final Set<Direction> accessibleSides;
    private final Consumer<Lumen> changeListener;
    private final Function<Lumen, Integer> capacityGetter;
    private InputFilter inputFilter;
    private ExtractFilter extractFilter;

    public LumenHandlerView(LumenStackList contents,
                            Set<Direction> accessibleSides,
                            Consumer<Lumen> changeListener,
                            Function<Lumen, Integer> capacityGetter,
                            InputFilter inputFilter,
                            ExtractFilter extractFilter) {
        this.contents = contents;
        this.accessibleSides = accessibleSides;
        this.changeListener = changeListener;
        this.capacityGetter = capacityGetter;
        this.inputFilter = inputFilter;
        this.extractFilter = extractFilter;
    }

    public void bypassFilters(Consumer<LumenHandlerView> run) {
        InputFilter in = this.inputFilter;
        ExtractFilter ex = this.extractFilter;
        this.inputFilter = InputFilter.NO_FILTER;
        this.extractFilter = ExtractFilter.NO_FILTER;
        try {
            run.accept(this);
        } finally {
            this.inputFilter = in;
            this.extractFilter = ex;
        }
    }

    @Override
    public List<LumenStack> getContainedLumen() {
        return this.contents.getLumenStacks();
    }

    @Override
    public Optional<LumenStack> getContainedLumen(LumenLike type) {
        return this.contents.getLumenStack(type.asLumen());
    }

    @Override
    public int getCapacity(LumenLike type) {
        return this.capacityGetter.apply(type.asLumen());
    }

    @Override
    public int fill(LumenStack stack, Action action) {
        if (stack.isEmpty()) {
            return 0;
        }

        LumenStack fillStack = stack.copy();
        LumenStack existing = this.contents.getLumenStack(fillStack.getLumen()).orElse(LumenStack.EMPTY);
        int capacity = this.getCapacity(fillStack.getLumen());

        if (!this.inputFilter.canInsert(fillStack, existing)) {
            return 0;
        }

        if (action.isSimulate()) {
            if (existing.isEmpty()) {
                return Math.min(capacity, fillStack.getAmount());
            }
            return Math.min(capacity - existing.getAmount(), fillStack.getAmount());
        }

        if (existing.isEmpty()) {
            LumenStack newContent = fillStack.copyWithAmount(Math.min(capacity, fillStack.getAmount()));
            this.contents.getModifiableLumenStacks().add(newContent);
            this.onContentChanged(newContent.getLumen());
            return newContent.getAmount();
        }

        int maxFillable = capacity - existing.getAmount();
        if (maxFillable <= 0) {
            return 0;
        }

        if (fillStack.getAmount() < maxFillable) {
            existing.grow(fillStack.getAmount());
            this.contents.setLumenStack(existing);
            this.onContentChanged(existing);
            return fillStack.getAmount();
        } else {
            existing.setAmount(capacity);
            this.contents.setLumenStack(existing);
            this.onContentChanged(existing);
            return maxFillable;
        }
    }

    @Override
    public LumenStack drain(LumenLike lumen, int amount, Action action) {
        if (amount <= 0 || !this.contains(lumen)) {
            return LumenStack.EMPTY;
        }
        LumenStack existing = this.contents.getLumenStack(lumen.asLumen()).orElse(LumenStack.EMPTY);
        if (existing.isEmpty()) {
            return LumenStack.EMPTY;
        }
        if (!this.extractFilter.canExtract(amount, existing)) {
            return LumenStack.EMPTY;
        }
        int canDrain = Math.min(existing.getAmount(), amount);
        LumenStack drainedStack = existing.copyWithAmount(canDrain);
        if (!action.isSimulate()) {
            existing.shrink(canDrain);
            this.contents.setLumenStack(existing);
            this.onContentChanged(existing);
        }
        return drainedStack;
    }

    public void clear() {
        this.contents.clear();
        this.onContentChanged(LumenAS.NONE.get());
    }

    private void onContentChanged(LumenStack lumenStack) {
        this.onContentChanged(lumenStack.getLumen());
    }

    private void onContentChanged(Lumen lumen) {
        this.changeListener.accept(lumen);
    }

    protected boolean hasHandlerForSide(@Nullable Direction facing) {
        return facing == null || this.accessibleSides.contains(facing);
    }

    @Nullable
    public LumenHandlerView getLumenAccess(@Nullable Direction direction) {
        if (!this.hasHandlerForSide(direction)) {
            return null;
        }
        return this;
    }

    @FunctionalInterface
    public interface InputFilter {

        InputFilter NO_FILTER = (toAdd, existing) -> true;
        InputFilter NO_INSERT = (toAdd, existing) -> false;

        boolean canInsert(LumenStack toAdd, @Nonnull LumenStack existing);

        default InputFilter and(InputFilter other) {
            return (toAdd, existing) ->
                    other.canInsert(toAdd, existing) && this.canInsert(toAdd, existing);
        }
    }

    @FunctionalInterface
    public interface ExtractFilter {

        ExtractFilter NO_FILTER = (amount, existing) -> true;
        ExtractFilter NO_EXTRACT = (amount, existing) -> false;

        boolean canExtract(int amount, @Nonnull LumenStack existing);

        default ExtractFilter and(ExtractFilter other) {
            return (amount, existing) ->
                    other.canExtract(amount, existing) && this.canExtract(amount, existing);
        }
    }
}
