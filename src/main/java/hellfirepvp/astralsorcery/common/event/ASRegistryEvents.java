/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import net.minecraftforge.eventbus.api.Event;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ASRegistryEvents
 * Created by HellFirePvP
 * Date: 01.06.2019 / 17:37
 */
public class ASRegistryEvents {

    public static class ConstellationRegister extends Event {

        /**
         * Registers and adds the given constellation.
         */
        public void registerConstellation(IConstellation c) {
            ConstellationRegistry.registerConstellation(c);
        }

    }

}
