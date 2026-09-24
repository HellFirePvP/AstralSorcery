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
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenHandlerItemFactory
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenHandlerItemFactory {

    protected Consumer<Lumen> changeListener = lumen -> {};
    protected Function<Lumen, Integer> capacityGetter = lumen -> 1000;
    protected LumenHandlerView.InputFilter inputFilter = LumenHandlerView.InputFilter.NO_FILTER;
    protected LumenHandlerView.ExtractFilter extractFilter = LumenHandlerView.ExtractFilter.NO_FILTER;
    private Function<ItemStack, StoredLumenComponent> componentGetter = stack -> stack.getOrDefault(DataComponentsAS.STORED_LUMEN, StoredLumenComponent.EMPTY);
    private BiConsumer<ItemStack, StoredLumenComponent> componentSetter = (stack, cmp) -> stack.set(DataComponentsAS.STORED_LUMEN, cmp);

    protected LumenHandlerItemFactory() {}

    public static LumenHandlerItemFactory builder() {
        return new LumenHandlerItemFactory();
    }

    public LumenHandlerItemFactory onChange(Consumer<Lumen> changeListener) {
        this.changeListener = this.changeListener.andThen(changeListener);
        return this;
    }

    public LumenHandlerItemFactory tankCapacity(Function<Lumen, Integer> capacityGetter) {
        this.capacityGetter = capacityGetter;
        return this;
    }

    public LumenHandlerItemFactory inputFilter(LumenHandlerView.InputFilter inputFilter) {
        this.inputFilter = inputFilter;
        return this;
    }

    public LumenHandlerItemFactory extractFilter(LumenHandlerView.ExtractFilter extractFilter) {
        this.extractFilter = extractFilter;
        return this;
    }

    public LumenHandlerItemFactory componentAccess(Function<ItemStack, StoredLumenComponent> componentGetter,
                                                   BiConsumer<ItemStack, StoredLumenComponent> componentSetter) {
        this.componentGetter = componentGetter;
        this.componentSetter = componentSetter;
        return this;
    }

    public LumenHandlerItem createHandler(ItemStack stack) {
        return new LumenHandlerItem(
                stack,
                this.changeListener,
                this.capacityGetter,
                this.inputFilter,
                this.extractFilter,
                this.componentGetter,
                this.componentSetter);
    }
}
