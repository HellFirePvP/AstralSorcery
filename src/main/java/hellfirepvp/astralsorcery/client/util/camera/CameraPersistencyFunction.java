/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.camera;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CameraPersistencyFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface CameraPersistencyFunction {

    boolean isExpired();

    void setExpired();

    class Simple implements CameraPersistencyFunction {

        private boolean expired = false;

        @Override
        public boolean isExpired() {
            return this.expired;
        }

        @Override
        public void setExpired() {
            this.expired = true;
        }
    }

}