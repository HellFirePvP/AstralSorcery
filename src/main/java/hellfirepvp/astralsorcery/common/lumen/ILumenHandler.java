/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ILumenHandler
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface ILumenHandler {

    BlockCapability<ILumenHandler, @Nullable Direction> BLOCK =
            BlockCapability.createSided(AstralSorcery.key("lumen_handler"), ILumenHandler.class);
    EntityCapability<ILumenHandler, @Nullable Void> ENTITY =
            EntityCapability.createVoid(AstralSorcery.key("lumen_handler"), ILumenHandler.class);
    ItemCapability<ILumenHandler, @Nullable Void> ITEM =
            ItemCapability.createVoid(AstralSorcery.key("lumen_handler"), ILumenHandler.class);

    enum Action {

        SIMULATE,
        EXECUTE;

        public boolean isSimulate() {
            return this == SIMULATE;
        }
    }

    /**
     * Quick helper access to a lumen handler that voids any lumen inserted.
     *
     * @return the voiding lumen handler
     */
    static ILumenHandler voiding() {
        return VoidLumenHandler.getInstance();
    }

    /**
     * Returns the total amount of lumen currently contained in this handler.
     * Implementation Note: Just return a copy of it all here. Do not modify the contents of the handler through this.
     *
     * @return the total amount of lumen contained in this handler.
     */
    List<LumenStack> getContainedLumen();

    /**
     * Returns the total amount of lumen currently contained in this handler for a specific type.
     * Implementation Note: A copy should be returned here. Do not modify the contents of the handler through this.
     *
     * @param type the type of lumen to check for
     * @return an optional containing the lumen stack of the specified type, or empty if none is present
     */
    Optional<LumenStack> getContainedLumen(LumenLike type);

    /**
     * Returns the limit of how much lumen of a specific type this handler can hold.
     * Returning 0 here does not directly mean that the handler cannot hold any of this type,
     * but may act as a display hint for rendering purposes.
     *
     * @param type the type of lumen to check for
     * @return the maximum amount of lumen of the specified type that this handler can hold
     */
    int getCapacity(LumenLike type);

    /**
     * Checks whether this type of lumen can be drawn from this handler.
     *
     * @param lumen the type of lumen to check for
     * @return true if this handler can draw the specified type of lumen, false otherwise
     */
    default boolean contains(LumenLike lumen) {
        return this.getContainedLumen(lumen).map(LumenStack::getAmount).orElse(0) > 0;
    }

    /**
     * Fill lumen into this lumen handler.
     *
     * @param stack the lumen stack to fill into this handler
     * @param action the action to perform, either simulating the fill or executing it
     * @return the amount of lumen that was successfully filled into this handler, or simulated to be potentially filled
     */
    int fill(LumenStack stack, Action action);

    /**
     * Drains lumen from this handler.
     *
     * @param lumen the type of lumen to drain from this handler
     * @param amount the amount of lumen to drain
     * @param action the action to perform, either simulating the drain or executing it
     * @return a LumenStack representing the drained lumen, or an empty stack if none could be drained
     */
    LumenStack drain(LumenLike lumen, int amount, Action action);

    default LumenStack drain(LumenStack stack, Action action) {
        if (stack.isEmpty()) return LumenStack.EMPTY;
        return this.drain(stack.getLumen(), stack.getAmount(), action);
    }

    /**
     * Checks if there is any lumen in this lumen handler.
     *
     * @return true if there is at least one lumen stack with a positive amount in this handler, false otherwise
     */
    default boolean isEmpty() {
        return this.getContainedLumen().stream().mapToInt(LumenStack::getAmount).sum() > 0;
    }
}
