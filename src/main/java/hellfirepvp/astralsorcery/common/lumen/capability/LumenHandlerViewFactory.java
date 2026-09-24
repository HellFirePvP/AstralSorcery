/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.capability;

import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import net.minecraft.core.Direction;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenHandlerViewFactory
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenHandlerViewFactory {

    protected final Set<Direction> accessibleSides = new HashSet<>();
    protected Consumer<Lumen> changeListener = lumen -> {};
    protected Function<Lumen, Integer> capacityGetter = lumen -> 1000;
    protected LumenHandlerView.InputFilter inputFilter = LumenHandlerView.InputFilter.NO_FILTER;
    protected LumenHandlerView.ExtractFilter extractFilter = LumenHandlerView.ExtractFilter.NO_FILTER;

    protected LumenHandlerViewFactory() {}

    public static LumenHandlerViewFactory builder() {
        return new LumenHandlerViewFactory();
    }

    public LumenHandlerViewFactory accessibleSides(Direction... sides) {
        Collections.addAll(this.accessibleSides, sides);
        return this;
    }

    public LumenHandlerViewFactory onChange(Consumer<Lumen> changeListener) {
        this.changeListener = this.changeListener.andThen(changeListener);
        return this;
    }

    public LumenHandlerViewFactory tankCapacity(Function<Lumen, Integer> capacityGetter) {
        this.capacityGetter = capacityGetter;
        return this;
    }

    public LumenHandlerViewFactory inputFilter(LumenHandlerView.InputFilter inputFilter) {
        this.inputFilter = inputFilter;
        return this;
    }

    public LumenHandlerViewFactory extractFilter(LumenHandlerView.ExtractFilter extractFilter) {
        this.extractFilter = extractFilter;
        return this;
    }

    public LumenHandlerView createTileView(TileEntitySynchronized.Data data, LumenStackList contents) {
        return new LumenHandlerView(contents,
                this.accessibleSides,
                this.changeListener.andThen(lumen -> data.markForUpdate()),
                this.capacityGetter,
                this.inputFilter,
                this.extractFilter);
    }

    public LumenHandlerView createView(LumenStackList contents) {
        return new LumenHandlerView(contents, this.accessibleSides, this.changeListener, this.capacityGetter, this.inputFilter, this.extractFilter);
    }
}
