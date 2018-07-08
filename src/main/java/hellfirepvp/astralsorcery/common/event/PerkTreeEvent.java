/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreeEvent
 * Created by HellFirePvP
 * Date: 08.07.2018 / 12:19
 */
public class PerkTreeEvent {

    //Register perk-attribute types during this event.
    //AttributeTypeRegistry.registerType
    //Called AFTER perks have been registered (or at least after the PerkRegister event has been fired)
    public static class PerkAttributeTypeRegister extends Event {}

    //Register your perks during this event.
    //PerkTree.INSTANCE.register
    public static class PerkRegister extends Event {}

}
