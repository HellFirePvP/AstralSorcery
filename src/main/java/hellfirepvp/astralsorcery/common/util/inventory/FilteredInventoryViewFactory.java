/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.inventory;

import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FilteredInventoryViewFactory
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FilteredInventoryViewFactory extends InventoryViewFactory {

    protected FilteredInventoryView.InputFilter inputFilter = FilteredInventoryView.InputFilter.NO_FILTER;
    protected FilteredInventoryView.ExtractFilter extractFilter = FilteredInventoryView.ExtractFilter.NO_FILTER;

    protected FilteredInventoryViewFactory(int viewSize) {
        super(viewSize);
    }

    public static FilteredInventoryViewFactory filteredBuilder(int viewSize) {
        return new FilteredInventoryViewFactory(viewSize);
    }

    public FilteredInventoryViewFactory accessibleSides(Direction... sides) {
        return MiscUtil.cast(super.accessibleSides(sides));
    }

    @Override
    public FilteredInventoryViewFactory stackSizeLimiter(BiFunction<Integer, ItemStack, Integer> stackSizeLimiter) {
        return MiscUtil.cast(super.stackSizeLimiter(stackSizeLimiter));
    }

    @Override
    public FilteredInventoryViewFactory onChange(Consumer<Integer> changeListener) {
        return MiscUtil.cast(super.onChange(changeListener));
    }

    public FilteredInventoryViewFactory inputFilter(FilteredInventoryView.InputFilter inputFilter) {
        this.inputFilter = this.inputFilter.and(inputFilter);
        return this;
    }

    public FilteredInventoryViewFactory extractFilter(FilteredInventoryView.ExtractFilter extractFilter) {
        this.extractFilter = this.extractFilter.and(extractFilter);
        return this;
    }

    public FilteredInventoryView createTileView(TileEntitySynchronized.Data data, InventoryStackList contents) {
        return new FilteredInventoryView(this.viewSize,
                contents,
                this.accessibleSides,
                this.changeListener.andThen(slot -> data.markForUpdate()),
                this.stackSizeLimiter,
                this.inputFilter,
                this.extractFilter);
    }

    public FilteredInventoryView createView(InventoryStackList contents) {
        return new FilteredInventoryView(this.viewSize,
                contents,
                this.accessibleSides,
                this.changeListener,
                this.stackSizeLimiter,
                this.inputFilter,
                this.extractFilter);
    }
}
