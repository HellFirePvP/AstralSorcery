/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen;

import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VoidLumenHandler
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class VoidLumenHandler implements ILumenHandler {

    private static final VoidLumenHandler INSTANCE = new VoidLumenHandler();

    private VoidLumenHandler() {}

    public static VoidLumenHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public List<LumenStack> getContainedLumen() {
        return List.of();
    }

    @Override
    public Optional<LumenStack> getContainedLumen(LumenLike type) {
        return Optional.empty();
    }

    @Override
    public int getCapacity(LumenLike type) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int fill(LumenStack stack, Action action) {
        return stack.getAmount();
    }

    @Override
    public LumenStack drain(LumenLike lumen, int amount, Action action) {
        return LumenStack.EMPTY;
    }
}
