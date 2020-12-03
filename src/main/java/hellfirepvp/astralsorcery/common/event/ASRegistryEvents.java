/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import hellfirepvp.astralsorcery.common.perk.source.ModifierSourceProvider;
import net.minecraftforge.eventbus.api.Event;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ASRegistryEvents
 * Created by HellFirePvP
 * Date: 01.06.2019 / 17:37
 */
public class ASRegistryEvents {

    /**
     * Use this event to register a new modifier source provider to the automatic gathering.
     */
    public static class ModifierSourceRegister extends Event {

        private final Consumer<ModifierSourceProvider<?>> registrar;

        public ModifierSourceRegister(Consumer<ModifierSourceProvider<?>> registrar) {
            this.registrar = registrar;
        }

        public void registerSource(ModifierSourceProvider<?> sourceProvider) {
            this.registrar.accept(sourceProvider);
        }
    }

}
