/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.capability;

import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lumen.ILumenHandler;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.LumenLike;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenHandlerItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenHandlerItem implements ILumenHandler {

    private final ItemStack backingStack;
    private final Consumer<Lumen> changeListener;
    private final Function<Lumen, Integer> capacityGetter;
    private LumenHandlerView.InputFilter inputFilter;
    private LumenHandlerView.ExtractFilter extractFilter;
    private final Function<ItemStack, StoredLumenComponent> componentGetter;
    private final BiConsumer<ItemStack, StoredLumenComponent> componentSetter;

    public LumenHandlerItem(ItemStack backingStack,
                            Consumer<Lumen> changeListener,
                            Function<Lumen, Integer> capacityGetter,
                            LumenHandlerView.InputFilter inputFilter,
                            LumenHandlerView.ExtractFilter extractFilter,
                            Function<ItemStack, StoredLumenComponent> componentGetter,
                            BiConsumer<ItemStack, StoredLumenComponent> componentSetter) {
        this.backingStack = backingStack;
        this.changeListener = changeListener;
        this.capacityGetter = capacityGetter;
        this.inputFilter = inputFilter;
        this.extractFilter = extractFilter;
        this.componentGetter = componentGetter;
        this.componentSetter = componentSetter;
    }

    public void bypassFilters(Consumer<LumenHandlerItem> run) {
        LumenHandlerView.InputFilter in = this.inputFilter;
        LumenHandlerView.ExtractFilter ex = this.extractFilter;
        this.inputFilter = LumenHandlerView.InputFilter.NO_FILTER;
        this.extractFilter = LumenHandlerView.ExtractFilter.NO_FILTER;
        try {
            run.accept(this);
        } finally {
            this.inputFilter = in;
            this.extractFilter = ex;
        }
    }

    protected StoredLumenComponent getComponent() {
        return this.componentGetter.apply(this.backingStack);
    }

    protected void setComponent(StoredLumenComponent component) {
        this.componentSetter.accept(this.backingStack, component);
    }

    @Override
    public List<LumenStack> getContainedLumen() {
        return this.getComponent().storedLumen()
                .stream()
                .map(StoredLumenComponent.StoredLumen::lumenStack)
                .filter(stack -> !stack.isEmpty())
                .toList();
    }

    @Override
    public Optional<LumenStack> getContainedLumen(LumenLike type) {
        return this.getComponent().getStoredLumen(type.asLumen()).map(StoredLumenComponent.StoredLumen::lumenStack);
    }

    @Override
    public int getCapacity(LumenLike type) {
        return this.getComponent().getStoredLumen(type.asLumen())
                .map(StoredLumenComponent.StoredLumen::maxAmount)
                .orElse(this.capacityGetter.apply(type.asLumen()));
    }

    @Override
    public int fill(LumenStack stack, Action action) {
        if (stack.isEmpty()) return 0;

        LumenStack fillStack = stack.copy();
        StoredLumenComponent.StoredLumen existingStored = this.getComponent().getStoredLumen(fillStack.getLumen()).orElse(null);
        LumenStack existing = existingStored == null ? LumenStack.EMPTY : existingStored.lumenStack();
        int capacity = this.getCapacity(fillStack.getLumen());

        if (!this.inputFilter.canInsert(fillStack, existing)) return 0;
        if (capacity <= 0) return 0;

        if (action.isSimulate()) {
            if (existing.isEmpty()) {
                return Math.min(capacity, fillStack.getAmount());
            }
            return Math.min(capacity - existing.getAmount(), fillStack.getAmount());
        }
        if (existingStored == null || existing.isEmpty()) {
            LumenStack newContent = fillStack.copyWithAmount(Math.min(capacity, fillStack.getAmount()));
            this.setComponent(this.getComponent().updateLumenStack(newContent.getLumen(), newContent.getAmount(), capacity));
            this.onContentChanged(newContent.getLumen());
            return newContent.getAmount();
        }

        int maxFillable = capacity - existing.getAmount();
        if (maxFillable <= 0) return 0;

        if (fillStack.getAmount() < maxFillable) {
            existing.grow(fillStack.getAmount());
            this.setComponent(this.getComponent().updateLumenStack(existing.getLumen(), existing.getAmount(), existingStored.maxAmount()));
            this.onContentChanged(existing.getLumen());
            return fillStack.getAmount();
        } else {
            existing.setAmount(capacity);
            this.setComponent(this.getComponent().updateLumenStack(existing.getLumen(), existing.getAmount(), existingStored.maxAmount()));
            this.onContentChanged(existing.getLumen());
            return maxFillable;
        }
    }

    @Override
    public LumenStack drain(LumenLike lumen, int amount, Action action) {
        if (amount <= 0 || !this.contains(lumen)) {
            return LumenStack.EMPTY;
        }
        StoredLumenComponent.StoredLumen existingStored = this.getComponent().getStoredLumen(lumen.asLumen()).orElse(null);
        LumenStack existing = existingStored == null ? LumenStack.EMPTY : existingStored.lumenStack();
        if (existingStored == null || existing.isEmpty()) {
            return LumenStack.EMPTY;
        }
        if (!this.extractFilter.canExtract(amount, existing)) {
            return LumenStack.EMPTY;
        }

        int canDrain = Math.min(existing.getAmount(), amount);
        LumenStack drainedStack = existing.copyWithAmount(canDrain);
        if (!action.isSimulate()) {
            existing.shrink(canDrain);
            this.setComponent(this.getComponent().updateLumenStack(lumen.asLumen(), existing.getAmount(), existingStored.maxAmount()));
            this.onContentChanged(existing.getLumen());
        }
        return drainedStack;
    }

    private void onContentChanged(Lumen type) {
        this.changeListener.accept(type);
    }
}
