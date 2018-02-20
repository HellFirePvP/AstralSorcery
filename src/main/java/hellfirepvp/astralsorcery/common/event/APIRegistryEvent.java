/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectRegistry;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.ILocatable;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: APIRegistryEvent
 * Created by HellFirePvP
 * Date: 13.11.2016 / 15:09
 */
//Generally all misc. api hooks should use this to register their stuff.
public class APIRegistryEvent {

    public static class ConstellationEffectRegister extends Event {

        /**
         * This does NOT include config generation.
         */
        public void registerEffect(IMajorConstellation c, Function<ILocatable, ConstellationEffect> effectInstanceProvider) {
            ConstellationEffectRegistry.registerFromAPI(c, effectInstanceProvider);
        }

    }

    public static class ConstellationRegister extends Event {

        /**
         * Registers and adds the given constellation.
         */
        public void registerConstellation(IConstellation c) {
            ConstellationRegistry.registerConstellation(c);
        }

    }

}
