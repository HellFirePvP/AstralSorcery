/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.object;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InitRunnable
 * Created by HellFirePvP
 * Date: 06.03.2020 / 20:50
 */
public class InitRunnable implements Runnable {

    private final Runnable actualRun;
    private boolean didRun = false;

    private InitRunnable(Runnable actualRun) {
        this.actualRun = actualRun;
    }

    public static InitRunnable doOnce(Runnable run) {
        return new InitRunnable(run);
    }

    public static void doOnceNow(Runnable run) {
        doOnce(run).run();
    }

    @Override
    public void run() {
        if (!didRun) {
            this.actualRun.run();
            didRun = true;
        }
    }
}
