/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.tank;

import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FluidTankViewFactory
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FluidTankViewFactory {

    protected final int tankCount;
    protected final Set<Direction> accessibleSides = new HashSet<>();
    protected Consumer<Integer> changeListener = slot -> {};
    protected Function<Integer, Integer> tankCapacityGetter = tank -> FluidType.BUCKET_VOLUME;
    protected FluidTankView.InputFilter inputFilter = FluidTankView.InputFilter.NO_FILTER;
    protected FluidTankView.ExtractFilter extractFilter = FluidTankView.ExtractFilter.NO_FILTER;

    protected FluidTankViewFactory(int tankCount) {
        this.tankCount = tankCount;
    }

    public static FluidTankViewFactory builder(int tankCount) {
        return new FluidTankViewFactory(tankCount);
    }

    public FluidTankViewFactory accessibleSides(Direction... sides) {
        Collections.addAll(this.accessibleSides, sides);
        return this;
    }

    public FluidTankViewFactory onChange(Consumer<Integer> changeListener) {
        this.changeListener = this.changeListener.andThen(changeListener);
        return this;
    }

    public FluidTankViewFactory tankCapacity(Function<Integer, Integer> tankCapacityGetter) {
        this.tankCapacityGetter = tankCapacityGetter;
        return this;
    }

    public FluidTankViewFactory inputFilter(FluidTankView.InputFilter inputFilter) {
        this.inputFilter = inputFilter;
        return this;
    }

    public FluidTankViewFactory extractFilter(FluidTankView.ExtractFilter extractFilter) {
        this.extractFilter = extractFilter;
        return this;
    }

    public FluidTankView createTileView(TileEntitySynchronized.Data data, FluidContainerList contents) {
        return new FluidTankView(this.tankCount,
                contents,
                this.accessibleSides,
                this.changeListener.andThen(tank -> data.markForUpdate()),
                this.tankCapacityGetter,
                this.inputFilter,
                this.extractFilter);
    }

    public FluidTankView createView(FluidContainerList contents) {
        return new FluidTankView(this.tankCount,
                contents,
                this.accessibleSides,
                this.changeListener,
                this.tankCapacityGetter,
                this.inputFilter,
                this.extractFilter);
    }
}
