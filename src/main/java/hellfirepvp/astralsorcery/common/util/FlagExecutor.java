/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FlagExecutor
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
// Small utility class to only run specific code once to prevent infinite recursion
public class FlagExecutor {

    private static final Set<Flag> running = new HashSet<>();

    public static void run(Flag flag, Runnable runnable) {
        if (running.add(flag)) {
            runnable.run();
            running.remove(flag);
        }
    }

    public static boolean isFlagSet(Flag flag) {
        return running.contains(flag);
    }

    public enum Flag {

        RENDER_ASTROLABE_ITEM,
        CHAIN_BLOCK_BREAK,
        TREE_GROWTH,
        RANGED_SWEEP_ATTACK,
        AOE_DAMAGE_BURST,

    }
}
