/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.inventory;

import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: InventoryViewFactory
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class InventoryViewFactory {

    protected final int viewSize;
    protected final Set<Direction> accessibleSides = new HashSet<>();
    protected Consumer<Integer> changeListener = slot -> {};
    protected BiFunction<Integer, ItemStack, Integer> stackSizeLimiter = (slot, stack) -> stack.isEmpty() ? 64 : stack.getMaxStackSize();

    protected InventoryViewFactory(int viewSize) {
        this.viewSize = viewSize;
    }

    public static InventoryViewFactory builder(int viewSize) {
        return new InventoryViewFactory(viewSize);
    }

    public InventoryViewFactory accessibleSides(Direction... sides) {
        Collections.addAll(this.accessibleSides, sides);
        return this;
    }

    public InventoryViewFactory onChange(Consumer<Integer> changeListener) {
        this.changeListener = this.changeListener.andThen(changeListener);
        return this;
    }

    public InventoryViewFactory stackSizeLimiter(BiFunction<Integer, ItemStack, Integer> stackSizeLimiter) {
        this.stackSizeLimiter = stackSizeLimiter;
        return this;
    }

    public InventoryView createTileView(TileEntitySynchronized.Data data, InventoryStackList contents) {
        return new InventoryView(this.viewSize,
                contents,
                this.accessibleSides,
                this.changeListener.andThen(slot -> data.markForUpdate()),
                this.stackSizeLimiter);
    }

    public InventoryView createView(InventoryStackList contents) {
        return new InventoryView(this.viewSize, contents, this.accessibleSides, this.changeListener, this.stackSizeLimiter);
    }
}
