/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.tick;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TickableListener
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface TickableListener extends Tickable {

    default void onPlayerTick(PlayerTickEvent.Post event) {
        this.tick();
    }

    default void onLevelTick(LevelTickEvent.Post event) {
        this.tick();
    }

    default void onClientTick(ClientTickEvent.Post event) {
        this.tick();
    }

    default void onServerTick(ServerTickEvent.Post event) {
        this.tick();
    }
}
