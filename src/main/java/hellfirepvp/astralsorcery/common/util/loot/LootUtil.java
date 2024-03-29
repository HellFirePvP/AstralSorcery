/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.loot;

import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameterSet;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LootUtil
 * Created by HellFirePvP
 * Date: 08.05.2020 / 16:59
 */
public class LootUtil {

    private LootUtil() {}

    public static boolean doesContextFulfillSet(LootContext ctx, LootParameterSet set) {
        for (LootParameter<?> required : set.getRequiredParameters()) {
            if (!ctx.has(required)) {
                return false;
            }
        }
        return true;
    }

}
