/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SidedHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SidedHelper {

    public static LogicalSide getSide(Level level) {
        return level.isClientSide() ? LogicalSide.CLIENT : LogicalSide.SERVER;
    }

    public static LogicalSide getSide(Entity entity) {
        return entity.getCommandSenderWorld().isClientSide() ? LogicalSide.CLIENT : LogicalSide.SERVER;
    }

}
