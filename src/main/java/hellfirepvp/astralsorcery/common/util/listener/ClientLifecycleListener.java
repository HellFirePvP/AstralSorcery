/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.listener;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ClientLifecycleListener
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface ClientLifecycleListener {

    void onClientConnect();

    void onClientDisconnect();

    static ClientLifecycleListener connect(Runnable connect) {
        return wrap(connect, null);
    }

    static ClientLifecycleListener disconnect(Runnable disconnect) {
        return wrap(null, disconnect);
    }

    static ClientLifecycleListener wrap(@Nullable Runnable connect, @Nullable Runnable disconnect) {
        return new ClientLifecycleListener() {
            @Override
            public void onClientConnect() {
                if (connect != null) connect.run();
            }

            @Override
            public void onClientDisconnect() {
                if (disconnect != null) disconnect.run();
            }
        };
    }
}
