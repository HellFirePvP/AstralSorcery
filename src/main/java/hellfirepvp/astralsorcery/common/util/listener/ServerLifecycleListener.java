/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.listener;

import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ServerLifecycleListener
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface ServerLifecycleListener {

    void onServerStart(MinecraftServer server);

    void onServerStop(MinecraftServer server);

    static ServerLifecycleListener start(Runnable onStart) {
        return wrap(server -> onStart.run(), null);
    }

    static ServerLifecycleListener start(Consumer<MinecraftServer> onStart) {
        return wrap(onStart, null);
    }

    static ServerLifecycleListener stop(Runnable onStop) {
        return wrap(null, server -> onStop.run());
    }

    static ServerLifecycleListener stop(Consumer<MinecraftServer> onStop) {
        return wrap(null, onStop);
    }

    static ServerLifecycleListener wrap(@Nullable Consumer<MinecraftServer> onStart, @Nullable Consumer<MinecraftServer> onStop) {
        return new ServerLifecycleListener() {
            @Override
            public void onServerStart(MinecraftServer server) {
                if (onStart != null) onStart.accept(server);
            }

            @Override
            public void onServerStop(MinecraftServer server) {
                if (onStop != null) onStop.accept(server);
            }
        };
    }
}
