/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ServerLifecycleListener
 * Created by HellFirePvP
 * Date: 30.06.2019 / 09:17
 */
public interface ServerLifecycleListener {

    public void onServerStart();

    public void onServerStop();

    public static ServerLifecycleListener stop(Runnable onStop) {
        return wrap(null, onStop);
    }

    public static ServerLifecycleListener start(Runnable onStart) {
        return wrap(onStart, null);
    }

    public static ServerLifecycleListener wrap(@Nullable Runnable onStart, @Nullable Runnable onStop) {
        return new ServerLifecycleListener() {
            @Override
            public void onServerStart() {
                if (onStart != null) {
                    onStart.run();
                }
            }

            @Override
            public void onServerStop() {
                if (onStop != null) {
                    onStop.run();
                }
            }
        };
    }

}
